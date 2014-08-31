package com.brocktek.farm.server.resources;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import com.brocktek.farm.BrocktekMonitoring;
import com.brocktek.farm.model.Barn;
import com.brocktek.farm.model.BarnUpdate;
import com.brocktek.farm.monitoring.MonitoringService;
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
	private static final Logger LOGGER = Logger.getLogger(BarnResource.class.getName());
	private static final Gson basicGson;
	private static final Gson detailGson;
	static {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(TableData.class, new TableDataSerializer());
		builder.registerTypeAdapter(Barn.class, new BasicBarnSerializer());
		basicGson = builder.create();

		builder = new GsonBuilder();
		builder.registerTypeAdapter(ChartData.class, new ChartDataSerializer());
		detailGson = builder.create();
	}

	private static MonitoringService monitoringrService = BrocktekMonitoring.monitoringService;

	@GET
	@Produces("application/json")
	public Response getBarns() {
		try {
			return Response.ok(basicGson.toJson(new TableData(monitoringrService.getBarnsAsList())), "application/json").build();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
	}

	@GET
	@Path("/history")
	@Produces("application/json")
	public Response getBarnData(@QueryParam("address") String address64) {
		try {
			Barn barn = monitoringrService.getBarn(Long.parseLong(address64, 16));
			ChartData chartData = new ChartData(barn.getUpdateSet());
			chartData.getUpdateSet().add(new BarnUpdate(Instant.now(), barn.getWetBulbTemp(), barn.getDryBulbTemp()));
			return Response.ok(detailGson.toJson(chartData), "application/json").build();
		} catch (Exception e) {
			LOGGER.severe(e.getMessage());
			throw new WebApplicationException(e.getMessage());
		}
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

		public ChartData(SortedSet<BarnUpdate> updateSet) {
			this.updateSet = new TreeSet<BarnUpdate>();
			for (BarnUpdate update : updateSet)
				this.updateSet.add(update);
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
			jsonObject.addProperty("wetBulbTemp", String.format("%1$,.1f&degF", src.getWetBulbTemp()));
			jsonObject.addProperty("dryBulbTemp", String.format("%1$,.1f&degF", src.getDryBulbTemp()));
			return jsonObject;
		}
	}

	private static class ChartDataSerializer implements JsonSerializer<ChartData> {

		@Override
		public JsonElement serialize(ChartData src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject jsonObject = new JsonObject();

			JsonArray timestampData = new JsonArray();
			JsonArray wetBulbData = new JsonArray();
			JsonArray dryBulbData = new JsonArray();
			int offset = TimeZone.getDefault().getOffset(Instant.now().toEpochMilli());
			for (BarnUpdate update : src.getUpdateSet()) {
				timestampData.add(new JsonPrimitive(update.getTimestamp().toEpochMilli() + offset));
				wetBulbData.add(new JsonPrimitive((new BigDecimal(update.getWetBulbTemp())).setScale(2, BigDecimal.ROUND_HALF_UP)));
				dryBulbData.add(new JsonPrimitive((new BigDecimal(update.getDryBulbTemp())).setScale(2, BigDecimal.ROUND_HALF_UP)));
			}
			jsonObject.add("timestampData", timestampData);
			jsonObject.add("wetBulbData", wetBulbData);
			jsonObject.add("dryBulbData", dryBulbData);

			return jsonObject;
		}
	}
}
