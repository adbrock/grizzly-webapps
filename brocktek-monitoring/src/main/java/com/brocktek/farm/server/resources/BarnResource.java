package com.brocktek.farm.server.resources;

import java.lang.reflect.Type;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.brocktek.farm.BrocktekMonitoring;
import com.brocktek.farm.model.Barn;
import com.brocktek.farm.model.BarnUpdate;
import com.brocktek.farm.prevalence.MockPrevaylerService;
import com.brocktek.farm.prevalence.PrevaylerService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Path("/barns")
public class BarnResource {
	private static final Gson basicGson;
	private static final Gson detailGson;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(TableData.class, new TableDataSerializer());
		builder.registerTypeAdapter(Barn.class, new BasicBarnSerializer());
		builder.setPrettyPrinting();
		basicGson = builder.create();

		builder = new GsonBuilder();
		builder.registerTypeAdapter(ChartData.class, new ChartDataSerializer());
		builder.setPrettyPrinting();
		detailGson = builder.create();
	}

	private static final List<Barn> mockBarnList = MockPrevaylerService.getMockBarnList();
	private static final Map<Long, Barn> mockBarnMap;
	static {
		mockBarnMap = new ConcurrentHashMap<Long, Barn>();
		for (Barn barn : mockBarnList) {
			mockBarnMap.put(barn.getAddress64(), barn);
		}
	}

	private static PrevaylerService prevaylerService = BrocktekMonitoring.prevaylerService;

	@GET
	@Path("/")
	@Produces("application/json")
	public Response getBarns() {
		return Response.ok(basicGson.toJson(new TableData(prevaylerService.getBarnsAsList())), "application/json").build();
	}

	@GET
	@Path("/history")
	@Produces("application/json")
	public Response getBarnData(@QueryParam("address") String address64, @QueryParam("days") int length) {
		Instant startInstant = Instant.now().minus(length, ChronoUnit.DAYS);
		Barn barn = prevaylerService.getBarn(Long.parseLong(address64, 16));
		SortedSet<BarnUpdate> barnUpdateSet = barn.getUpdateSet().tailSet(new BarnUpdate(startInstant, 0.0, 0.0));
		SortedSet<BarnUpdate> chartDataSet = new TreeSet<BarnUpdate>();
		for (BarnUpdate update : barnUpdateSet) {
			chartDataSet.add(update);
		}
		ChartData chartData = new ChartData(chartDataSet);
		if (chartData.getUpdateSet().first().getTimestamp().compareTo(startInstant) > 0)
			chartData.getUpdateSet().add(new BarnUpdate(startInstant, 0.0, 0.0));
		chartData.getUpdateSet().add(new BarnUpdate(Instant.now(), barn.getWetBulbTemp(), barn.getDryBulbTemp()));
		return Response.ok(detailGson.toJson(chartData), "application/json").build();
	}

	private static class TableData {
		private List<Barn> barnList;

		public TableData(List<Barn> barnList) {
			this.barnList = barnList;
		}

		public List<Barn> getBarnList() {
			return barnList;
		}
	}

	private static class ChartData {
		private SortedSet<BarnUpdate> updateSet;

		public ChartData(SortedSet<BarnUpdate> updateList) {
			this.updateSet = updateList;
		}

		public SortedSet<BarnUpdate> getUpdateSet() {
			return this.updateSet;
		}

	}

	private static class TableDataSerializer implements JsonSerializer<TableData> {

		@Override
		public JsonElement serialize(TableData src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			JsonArray barnArray = new JsonArray();
			for (Barn barn : src.getBarnList()) {
				barnArray.add(context.serialize(barn));
			}
			jsonObject.add("data", barnArray);
			return jsonObject;
		}
	}

	private static class BasicBarnSerializer implements JsonSerializer<Barn> {

		@Override
		public JsonElement serialize(Barn src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();
			jsonObject.addProperty("address", String.format("%16s", Long.toHexString(src.getAddress64()).toUpperCase()).replace(" ", "0"));
			jsonObject.addProperty("id", src.getId());
			jsonObject.addProperty("status", src.isOnline() ? "Online" : "Offline");
			jsonObject.addProperty("alert", Instant.now().minus(4, ChronoUnit.HOURS).compareTo(src.getTimestamp()) > 0 ? true : false);
			jsonObject.addProperty("wetBulbTemp", String.format("%1$,.1f&degF", src.getWetBulbTemp()));
			jsonObject.addProperty("dryBulbTemp", String.format("%1$,.1f&degF", src.getDryBulbTemp()));
			return jsonObject;
		}
	}

	private static class ChartDataSerializer implements JsonSerializer<ChartData> {

		@Override
		public JsonElement serialize(ChartData src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();

			JsonArray wetBulbSeriesArray = new JsonArray();
			JsonArray dryBulbSeriesArray = new JsonArray();

			for (BarnUpdate update : src.getUpdateSet()) {
				JsonArray wetBulbDataPoint = new JsonArray();
				JsonArray dryBulbDataPoint = new JsonArray();
				wetBulbDataPoint.add(new JsonPrimitive(update.getTimestamp().toEpochMilli()));
				dryBulbDataPoint.add(new JsonPrimitive(update.getTimestamp().toEpochMilli()));
				wetBulbDataPoint.add(new JsonPrimitive(update.getWetBulbTemp()));
				dryBulbDataPoint.add(new JsonPrimitive(update.getDryBulbTemp()));
				wetBulbSeriesArray.add(wetBulbDataPoint);
				dryBulbSeriesArray.add(dryBulbDataPoint);
			}
			jsonObject.add("wetBulbSeries", wetBulbSeriesArray);
			jsonObject.add("dryBulbSeries", dryBulbSeriesArray);

			return jsonObject;
		}
	}
}
