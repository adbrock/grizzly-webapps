package com.brocktek.farm.monitoring;

import java.io.File;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import jersey.repackaged.com.google.common.util.concurrent.ThreadFactoryBuilder;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import com.brocktek.farm.model.Barn;
import com.brocktek.xbee.XbeeService;
import com.brocktek.zigbee.DataSampleFrame;
import com.brocktek.zigbee.ZigbeeFrame;
import com.brocktek.zigbee.ZigbeeFrameListener;
import com.brocktek.zigbee.ZigbeeService;

public class MonitoringService implements ZigbeeFrameListener {
	private static final Logger LOGGER = Logger.getLogger(MonitoringService.class.getName());
	private static final ThreadFactory TF = new ThreadFactoryBuilder().setNameFormat("MonitoringWorker(%d)").build();
	private final Prevayler<ConcurrentHashMap<Long, Barn>> prevayler;
	public final ZigbeeService zigbeeService;
	private final ScheduledExecutorService executorService;

	public MonitoringService() throws Exception {
		prevayler = PrevaylerFactory.createPrevayler(new ConcurrentHashMap<Long, Barn>(), "data");
		zigbeeService = new XbeeService();
		executorService = Executors.newScheduledThreadPool(1, TF);
	}

	public void start(String portName) throws Exception {
		long initMillis = System.currentTimeMillis();
		LOGGER.info("Initializing Zigbee Service.");
		zigbeeService.start(portName);
		zigbeeService.addFrameListener(this);

		Runnable pollBarns = new Runnable() {
			@Override
			public void run() {
				for (Entry<Long, Barn> entry : prevayler.prevalentSystem().entrySet()) {
					if (entry.getValue().isOnline()) {
						if (Instant.now().minusSeconds(135).compareTo(entry.getValue().getTimestamp()) > 0) {
							prevayler.execute(new UpdateBarnTransaction(entry.getValue().getAddress64(), Instant.now(), Barn.OFFLINE, 0.0, 0.0));
						}
					}
				}
			}
		};

		Runnable takeSnapshot = new Runnable() {
			@Override
			public void run() {
				try {
					File snapshot = prevayler.takeSnapshot();
					LOGGER.info(String.format("Snapshot completed. Total disk size: %,dB", snapshot.length()));
				} catch (Exception e) {
					LOGGER.warning(e.getMessage());
				}
			}
		};

		executorService.scheduleAtFixedRate(pollBarns, 15, 5, TimeUnit.SECONDS);
		executorService.scheduleAtFixedRate(takeSnapshot, 0, 24, TimeUnit.HOURS);

		if (zigbeeService.isOnline())
			LOGGER.info(String.format("Monitoring Service Initialized in %.2f seconds.", (double) (System.currentTimeMillis() - initMillis) / 1000));
		else {
			LOGGER.warning("Unable to start Zigbee Service.");
		}
	}

	public void shutdown() {
		executorService.shutdown();
		zigbeeService.removeFrameListener(this);
		zigbeeService.shutdown();
		LOGGER.info("Monitoring Service Shutdown");
	}

	public void addBarn(Barn barn) {
		prevayler.execute(new AddBarnTransaction(barn));
	}

	public void removeBarn(Barn barn) {
		prevayler.execute(new RemoveBarnTransaction(barn));
	}

	public Barn getBarn(long address64) {
		return prevayler.prevalentSystem().get(address64);
	}

	public void clearBarnHistory() {
		prevayler.execute(new ClearBarnHistoryTransaction());
	}

	public List<Barn> getBarnsAsList() {
		List<Barn> barnList = new ArrayList<Barn>(prevayler.prevalentSystem().size());
		for (Entry<Long, Barn> entry : prevayler.prevalentSystem().entrySet()) {
			barnList.add(entry.getValue());
		}
		Collections.sort(barnList);
		return barnList;
	}

	@Override
	public void frameReceived(ZigbeeFrame frame) {
		if (frame instanceof DataSampleFrame) {
			DataSampleFrame dataFrame = (DataSampleFrame) frame;
			Barn barn = prevayler.prevalentSystem().get(dataFrame.getAddress64());
			if (barn != null) {
				Instant timestamp = dataFrame.getTimestamp();
				double wetBulbTemp = (104.7 * (((double) dataFrame.getAnalog0() / 1023) * 5) - 3.6978);
				double dryBulbTemp = (104.7 * (((double) dataFrame.getAnalog1() / 1023) * 5) - 3.6978);
				prevayler.execute(new UpdateBarnTransaction(barn.getAddress64(), timestamp, Barn.ONLINE, wetBulbTemp, dryBulbTemp));
			}
		}
	}
}
