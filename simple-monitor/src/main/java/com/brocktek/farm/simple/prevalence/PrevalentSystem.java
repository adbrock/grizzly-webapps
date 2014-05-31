package com.brocktek.farm.simple.prevalence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import com.brocktek.farm.simple.model.Barn;
import com.brocktek.farm.simple.monitoring.MonitoringService;
import com.brocktek.farm.simple.monitoring.MonitoringServiceListener;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class PrevalentSystem implements PrevalenceService, MonitoringServiceListener {

	private Prevayler<Map<Long, Barn>> prevayler;
	private HeartbeatTimerTask heartbeatTimer;

	@Inject
	public PrevalentSystem(MonitoringService monitoringService) throws Exception {
		prevayler = PrevaylerFactory.createPrevayler(new ConcurrentHashMap<Long, Barn>(), "Barns");
		Timer timer = new Timer();
		timer.schedule(heartbeatTimer = new HeartbeatTimerTask(), 0, 1000);
		monitoringService.addMonitoringServiceListener(this);
	}

	public List<Barn> getBarnsAsList() {
		List<Barn> barnList = new ArrayList<Barn>(prevayler.prevalentSystem().size());
		for (Entry<Long, Barn> entry : prevayler.prevalentSystem().entrySet()) {
			barnList.add(entry.getValue());
		}
		Collections.sort(barnList);
		return barnList;
	}

	public void addBarn(Barn barn) {
		prevayler.execute(new AddBarnTransaction(barn));
		heartbeatTimer.getTimestampMap().put(barn.getAddress(), 0L);
	}

	public void removeBarn(Barn barn) {
		prevayler.execute(new RemoveBarnTransaction(barn));
		heartbeatTimer.getTimestampMap().remove(barn.getAddress());
	}

	public void updateBarnTemp(long address, double wetBulbTemp, double dryBulbTemp) {
		if (prevayler.prevalentSystem().containsKey(address)) {
			prevayler.execute(new UpdateBarnTempTransaction(address, wetBulbTemp, dryBulbTemp));
			heartbeatTimer.getTimestampMap().put(address, System.currentTimeMillis());
		}
	}

	public void updateBarnStatus(long address, boolean status) {
		prevayler.execute(new SetBarnStatusTransaction(address, status));
	}

	@Override
	public void barnTempUpdated(long address, double wetBulbTemp, double dryBulbTemp) {
		updateBarnTemp(address, wetBulbTemp, dryBulbTemp);
	}

	private class HeartbeatTimerTask extends TimerTask {
		private Map<Long, Long> timestampMap = new ConcurrentHashMap<Long, Long>();

		public Map<Long, Long> getTimestampMap() {
			return this.timestampMap;
		}

		@Override
		public void run() {
			long currentTime = System.currentTimeMillis();
			for (Barn barn : getBarnsAsList()) {
				if (!timestampMap.containsKey(barn.getAddress()))
					updateBarnStatus(barn.getAddress(), false);
				else if (currentTime > (timestampMap.get(barn.getAddress()) + 30000))
					updateBarnStatus(barn.getAddress(), false);
			}
		}
	}
}
