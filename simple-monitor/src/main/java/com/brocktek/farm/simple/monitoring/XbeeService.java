package com.brocktek.farm.simple.monitoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.prefs.Preferences;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;

import com.google.inject.Singleton;

@Singleton
public class XbeeService implements MonitoringService, SerialPortEventListener {

	private List<MonitoringServiceListener> listeners = new ArrayList<MonitoringServiceListener>();
	private List<FrameListener> frameListeners = new ArrayList<FrameListener>();
	private Preferences prefs = Preferences.systemNodeForPackage(XbeeService.class);
	private byte[] oldData = new byte[0];

	private SerialPort port;

	public XbeeService() {
		String portName = prefs.get("port", "");
		setPortName(portName);
	}

	@Override
	public String getPortName() {
		if (port != null)
			return port.getPortName();
		else
			return null;
	}

	@Override
	public void setPortName(String portName) {
		for (String availablePortName : SerialPortList.getPortNames()) {
			if (availablePortName.equals(portName)) {
				if (port != null) {
					try {
						// TODO
						port.removeEventListener();
						port.closePort();
					} catch (SerialPortException e) {
						e.printStackTrace();
					}
				}
				port = new SerialPort(portName);
			}
		}

		if (port != null) {
			try {
				port.openPort();
				port.addEventListener(this);
				prefs.put("port", portName);
			} catch (SerialPortException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public void addMonitoringServiceListener(MonitoringServiceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeMonitoringServiceListener(MonitoringServiceListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void serialEvent(SerialPortEvent evt) {
		if (!evt.isRXCHAR())
			return;

		/* Read Data */
		byte[] newData;
		try {
			newData = port.readBytes(evt.getEventValue());
		} catch (SerialPortException e) {
			e.printStackTrace();
			return;
		}

		/* Concatenate w/ Old Data */
		byte[] data = new byte[oldData.length + newData.length];
		for (int i = 0; i < oldData.length; i++)
			data[i] = oldData[i];
		for (int i = 0; i < newData.length; i++)
			data[i + oldData.length] = newData[i];

		/* Split Frames */
		int startByte = 0;
		boolean foundStart = false;
		List<XbeeFrame> frames = new Vector<XbeeFrame>(2);
		for (int i = 0; i < data.length; i++) {
			if (data[i] == XbeeFrame.DELIMITER) {
				if (foundStart) {
					frames.add(XbeeFrameFactory.build(unescape(Arrays.copyOfRange(data, startByte, i)), System.currentTimeMillis()));
					startByte = i;
				} else {
					startByte = i;
					foundStart = true;
				}
			}
		}

		/* Check for Complete Frame */
		XbeeFrame unknownFrame = XbeeFrameFactory.build(Arrays.copyOfRange(data, startByte, data.length), System.currentTimeMillis());
		if (unknownFrame.verify())
			frames.add(XbeeFrameFactory.build(unescape(Arrays.copyOfRange(data, startByte, data.length)), System.currentTimeMillis()));
		else
			oldData = Arrays.copyOfRange(data, startByte, data.length);

		/* Alert Coordinator of New Frames */
		for (XbeeFrame frame : frames) {
			System.out.println(frame);
			for (FrameListener frameListener : frameListeners) {
				frameListener.frameReceived(frame);
			}
			if (frame instanceof XbeeDataSampleRxIndicatorFrame) {
				XbeeDataSampleRxIndicatorFrame sampleFrame = (XbeeDataSampleRxIndicatorFrame) frame;
				System.out.println(sampleFrame.getWetBulbTemp() + " " + sampleFrame.getDryBulbTemp());
				for (MonitoringServiceListener listener : listeners) {
					listener.barnTempUpdated(sampleFrame.getAddress(), sampleFrame.getWetBulbTemp(), sampleFrame.getDryBulbTemp());
				}
			}
		}
	}

	private byte[] unescape(byte[] frame) {
		List<Byte> unescapedByteList = new ArrayList<Byte>(frame.length);
		unescapedByteList.add(frame[0]);
		boolean escapeByte = false;
		for (int i = 1; i < frame.length; i++) {
			if (frame[i] != XbeeFrame.ESCAPE) {
				if (!escapeByte)
					unescapedByteList.add(frame[i]);
				else {
					unescapedByteList.add((byte) (frame[i] ^ 0x20));
					escapeByte = false;
				}
			} else {
				escapeByte = true;
			}
		}

		byte[] unescapedBytes = new byte[unescapedByteList.size()];
		for (int i = 0; i < unescapedByteList.size(); i++) {
			unescapedBytes[i] = unescapedByteList.get(i);
		}
		return unescapedBytes;
	}

	@Override
	public void addFrameListener(FrameListener listener) {
		this.frameListeners.add(listener);		
	}
}
