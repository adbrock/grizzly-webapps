package com.brocktek.farm.prevalence;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.brocktek.farm.model.Barn;

public class MockPrevaylerService {

	public static List<Barn> getMockBarnList() {
		Random random = new Random();
		List<Barn> mockBarnList = new ArrayList<Barn>();

		for (int i = 1; i <= 100; i++) {
			Barn barn = new Barn();
			barn.setId(Integer.toString(i));
			barn.setAddress64(String.format("%16s", Long.toHexString(random.nextLong()).replace(" ", "0").toUpperCase()));

			if (random.nextBoolean()) {
				double wetBulbStartTemp = 60 + (random.nextDouble() * (120 - 60));
				double dryBulbStartTemp = 60 + (random.nextDouble() * (140 - 80));
				for (int j = 100; j >= 0; j--) {
					Instant timestamp = Instant.now().minus(j * 105, ChronoUnit.MINUTES);
					double wetBulbTemp = wetBulbStartTemp += (random.nextDouble() * 2);
					double dryBulbTemp = dryBulbStartTemp += (random.nextDouble() * 2);
					barn.updateTemperature(timestamp, wetBulbTemp, dryBulbTemp);
				}
			} else {
				barn.updateStatus(Instant.now(), false);
			}

			mockBarnList.add(barn);
		}

		return mockBarnList;
	}
}
