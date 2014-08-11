package com.brocktek.farm;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import jssc.SerialPortList;

import com.brocktek.farm.model.Barn;
import com.brocktek.farm.prevalence.PrevaylerService;
import com.brocktek.problem.Problem;
import com.brocktek.problem.ProblemService;
import com.brocktek.zigbee.NodeIdentificationFrame;
import com.brocktek.zigbee.ZigbeeFrame;
import com.brocktek.zigbee.ZigbeeFrameListener;
import com.brocktek.zigbee.ZigbeeService;
import com.google.inject.Inject;

public class BrocktekMonitoringController implements ZigbeeFrameListener {
	private ObservableList<String> availablePorts;
	private ObservableList<Barn> availableBarns;
	private ObservableList<Barn> activeBarns;

	@Inject PrevaylerService prevaylerService;
	@Inject ZigbeeService zigbeeService;
	@Inject ProblemService problemService;

	@FXML Button btnAdd;
	@FXML ChoiceBox<String> cboPort;
	@FXML TextField txtId;
	@FXML ListView<Barn> lstActive;
	@FXML ListView<Barn> lstAvailable;
	@FXML ListView<Problem> lstProblems;

	@FXML
	public void initialize() {
		availablePorts = FXCollections.observableArrayList();
		availableBarns = FXCollections.observableArrayList();
		activeBarns = prevaylerService.getBarnsAsObservableList();

		lstProblems.setItems(problemService.getProblems());
		lstActive.setItems(activeBarns);
		lstAvailable.setItems(availableBarns);

		btnAdd.setDisable(true);
		txtId.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				if (newValue.equals(""))
					btnAdd.setDisable(true);
				else
					btnAdd.setDisable(false);
			}
		});

		cboPort.setItems(availablePorts);
		if (zigbeeService.isOnline())
			cboPort.setValue(BrocktekMonitoring.prefs.get("port", "COM1"));
		cboPort.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				zigbeeService.shutdown();
				zigbeeService.start(newValue);
				BrocktekMonitoring.prefs.put("port", newValue);
			}
		});
		zigbeeService.addFrameListener(this);
		handleRefreshAction();
	}

	@FXML
	protected void handleRefreshAction() {
		String[] availablePorts = SerialPortList.getPortNames();

		for (String availablePort : availablePorts) {
			if (!this.availablePorts.contains(availablePort))
				this.availablePorts.add(availablePort);
		}

		boolean notFound = true;
		for (String foundPort : this.availablePorts) {
			for (String availablePort : availablePorts) {
				if (foundPort.equals(availablePort))
					notFound = false;
			}
			if (!foundPort.equals(cboPort.getValue()))
				if (notFound)
					this.availablePorts.remove(foundPort);
		}

		zigbeeService.getNetworkNodes();
	}

	@FXML
	protected void handleAddAction() {
		Barn selectedBarn = lstAvailable.selectionModelProperty().getValue().getSelectedItem();
		if (selectedBarn != null) {
			selectedBarn.setId(txtId.getText());
			prevaylerService.addBarn(selectedBarn);
			availableBarns.remove(selectedBarn);
			txtId.setText("");
		}
	}

	@FXML
	protected void handleRemoveAction() {
		Barn selectedBarn = lstActive.selectionModelProperty().getValue().getSelectedItem();
		if (selectedBarn != null) {
			prevaylerService.removeBarn(selectedBarn);
		}
	}

	@Override
	public void frameReceived(ZigbeeFrame frame) {
		if (frame instanceof NodeIdentificationFrame) {
			long address64 = ((NodeIdentificationFrame) frame).getAddress64();
			boolean notFound = true;
			for (Barn barn : availableBarns) {
				if (Long.parseLong(barn.getAddress64(), 16) == address64) {
					notFound = false;
					break;
				}
			}
			for (Barn barn : activeBarns) {
				if (Long.parseLong(barn.getAddress64(), 16) == address64) {
					notFound = false;
					break;
				}
			}
			if (notFound) {
				Barn barn = new Barn();
				barn.setAddress64(String.format("%016d", address64));
				availableBarns.add(barn);
			}
		}
	}
}
