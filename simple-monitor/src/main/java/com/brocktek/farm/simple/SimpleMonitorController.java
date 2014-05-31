package com.brocktek.farm.simple;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import jssc.SerialPortList;

import com.brocktek.farm.simple.model.Barn;
import com.brocktek.farm.simple.monitoring.FrameListener;
import com.brocktek.farm.simple.monitoring.MonitoringService;
import com.brocktek.farm.simple.monitoring.MonitoringServiceListener;
import com.brocktek.farm.simple.monitoring.XbeeFrame;
import com.brocktek.farm.simple.prevalence.PrevalentSystem;
import com.brocktek.farm.simple.util.IpUtil;
import com.google.inject.Inject;

public class SimpleMonitorController implements MonitoringServiceListener, FrameListener {

	@Inject MonitoringService monitoringService;
	private PrevalentSystem prevalentSystem = PrevalentSystemInjector.getPrevalentSystem();
	private List<Barn> currentBarnList;
	private ObservableList<String> availableBarnList;

	@FXML TableView<Barn> tblBarns;
	@FXML TableColumn<Barn, String> clmId;
	@FXML TableColumn<Barn, String> clmStatus;
	@FXML TableColumn<Barn, String> clmWetBulb;
	@FXML TableColumn<Barn, String> clmDryBulb;

	@FXML ComboBox<String> cboPorts;
	@FXML ListView<String> lstAvailableBarns;
	@FXML TextField txtName;
	@FXML TextArea txtFrames;
	@FXML Label lblAddress;

	@FXML
	protected void initialize() throws MalformedURLException {
		currentBarnList = new ArrayList<Barn>();
		availableBarnList = FXCollections.observableArrayList();
		lstAvailableBarns.setItems(availableBarnList);
		lstAvailableBarns.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		for (Barn barn : prevalentSystem.getBarnsAsList()) {
			currentBarnList.add(barn);
		}
		monitoringService.addMonitoringServiceListener(this);
		monitoringService.addFrameListener(this);

		tblBarns.setItems(FXCollections.observableArrayList(currentBarnList));
		clmId.setCellValueFactory(new PropertyValueFactory<Barn, String>("id"));
		clmStatus.setCellValueFactory(new PropertyValueFactory<Barn, String>("online"));
		clmWetBulb.setCellValueFactory(new PropertyValueFactory<Barn, String>("wetBulbTemp"));
		clmDryBulb.setCellValueFactory(new PropertyValueFactory<Barn, String>("dryBulbTemp"));

		lblAddress.setText(IpUtil.getExternalIp());

		cboPorts.setItems(FXCollections.observableArrayList(SerialPortList.getPortNames()));
		cboPorts.getSelectionModel().select(monitoringService.getPortName());
		cboPorts.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldVal, String newVal) {
				monitoringService.setPortName(newVal);
			}
		});
	}

	@FXML
	protected void handleAddBarnAction() {
		Barn barn = new Barn();
		barn.setId(txtName.getText());
		barn.setAddress(Long.parseLong(lstAvailableBarns.getSelectionModel().getSelectedItem(), 16));
		currentBarnList.add(barn);
		availableBarnList.remove(lstAvailableBarns.getSelectionModel().getSelectedItem());
		prevalentSystem.addBarn(barn);
		txtName.clear();
	}

	@FXML
	protected void handleRemoveBarnAction() {
		if (tblBarns.getSelectionModel().getSelectedItem() != null) {
			Barn barn = tblBarns.getSelectionModel().getSelectedItem();
			currentBarnList.remove(barn);
			prevalentSystem.removeBarn(barn);
			currentBarnList.clear();
			for (Barn barns : prevalentSystem.getBarnsAsList()) {
				currentBarnList.add(barns);
			}
			tblBarns.getItems().clear();
			tblBarns.setItems(FXCollections.observableArrayList(currentBarnList));
		}
	}

	@Override
	public void barnTempUpdated(long address, double wetBulbTemp, double dryBulbTemp) {		
		List<Long> currentAddressList = new ArrayList<Long>();
		for (Barn barn : currentBarnList) {
			currentAddressList.add(barn.getAddress());
		}

		if (!currentAddressList.contains(address)) {
			if (!availableBarnList.contains(String.format("%16s", Long.toHexString(address)).replace(" ", "0").toUpperCase())) {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						availableBarnList.add(String.format("%16s", Long.toHexString(address)).replace(" ", "0").toUpperCase());
					}
				});
			}
		} else {
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					currentBarnList.clear();
					for (Barn barn : prevalentSystem.getBarnsAsList()) {
						currentBarnList.add(barn);
					}
					tblBarns.getItems().clear();
					tblBarns.setItems(FXCollections.observableArrayList(currentBarnList));
				}
			});
		}
	}

	@Override
	public void frameReceived(XbeeFrame frame) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtFrames.appendText(frame.toString() + "\n");
			}
		});		
	}
}
