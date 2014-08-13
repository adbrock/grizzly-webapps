package com.brocktek.farm.prevalence;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import com.brocktek.farm.model.Barn;
import com.brocktek.problem.ProblemService;
import com.brocktek.zigbee.DataSampleFrame;
import com.brocktek.zigbee.ZigbeeFrame;
import com.brocktek.zigbee.ZigbeeFrameListener;
import com.brocktek.zigbee.ZigbeeService;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PrevaylerService implements ZigbeeFrameListener {
	private Prevayler<ConcurrentHashMap<Long, Barn>> prevayler;

	@Inject ZigbeeService zigbeeService;
	@Inject ScheduledExecutorService scheduleService;
	@Inject ProblemService problemService;

	public void start() throws Exception {
		prevayler = PrevaylerFactory.createPrevayler(new ConcurrentHashMap<Long, Barn>(), "DATA/Barns");
		zigbeeService.addFrameListener(this);

		for (Entry<Long, Barn> entry : prevayler.prevalentSystem().entrySet()) {
			if (entry.getValue().getTimestamp() != null)
				prevayler.execute(new SetBarnStatusOfflineTransaction(entry.getValue(), entry.getValue().getTimestamp()));
			prevayler.execute(new SetBarnStatusOfflineTransaction(entry.getValue(), Instant.now()));
		}

		Runnable pollBarns = new Runnable() {
			@Override
			public void run() {
				for (Entry<Long, Barn> entry : prevayler.prevalentSystem().entrySet()) {
					if (entry.getValue().isOnline()) {
						if (Instant.now().minusSeconds(45).compareTo(entry.getValue().getTimestamp()) > 0) {
							prevayler.execute(new SetBarnStatusOfflineTransaction(entry.getValue(), Instant.now()));
						}
					}
				}
			}
		};

		Runnable takeSnapshot = new Runnable() {
			@Override
			public void run() {
				try {
					prevayler.takeSnapshot();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		scheduleService.scheduleAtFixedRate(pollBarns, 15, 5, TimeUnit.SECONDS);
		scheduleService.scheduleAtFixedRate(takeSnapshot, 0, 24, TimeUnit.HOURS);
	}

	public void shutdown() {
		zigbeeService.removeFrameListener(this);
	}

	public void addBarn(Barn barn) {
		prevayler.execute(new AddBarnTransaction(barn));
		prevayler.execute(new SetBarnStatusOfflineTransaction(barn, Instant.now()));
	}

	public void removeBarn(Barn barn) {
		prevayler.execute(new RemoveBarnTransaction(barn));
	}

	public Barn getBarn(long address64) {
		return prevayler.prevalentSystem().get(address64);
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
		List<Barn> barns = getBarnsAsList();
		if (frame instanceof DataSampleFrame) {
			DataSampleFrame dataFrame = (DataSampleFrame) frame;
			Barn barn = prevayler.prevalentSystem().get(dataFrame.getAddress64());
			if (barn != null) {
				Instant timestamp = dataFrame.getTimestamp();
				double wetBulbTemp = (103 * ((double) dataFrame.getAnalog1() / 256) - .85);
				double dryBulbTemp = (103 * ((double) dataFrame.getAnalog2() / 256) - .85);
				if (!barn.isOnline())
					prevayler.execute(new SetBarnStatusOfflineTransaction(barn, timestamp));
				prevayler.execute(new UpdateBarnTempTransaction(barn, timestamp, wetBulbTemp, dryBulbTemp));
			}
		}
	}
}
