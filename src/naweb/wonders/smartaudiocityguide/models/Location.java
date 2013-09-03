package naweb.wonders.smartaudiocityguide.models;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location {
	private int id;
	private double latitude;
	private double longitude;

	public Location(int id, double latitude, double longitude) {
		this.id = id;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Location(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public JSONObject toJSON() {
		JSONObject jsonObject = new JSONObject();

		try {
			jsonObject.put("id", id);
			jsonObject.put("latitude", latitude);
			jsonObject.put("longitude", longitude);

			return jsonObject;
		} catch (JSONException e) {
			e.printStackTrace();

			return null;
		}
	}

	public static List<Location> jsonToLocationsList(String json) {
		List<Location> locations = new ArrayList<Location>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);

				Location location = new Location(jsonObject.getInt("id"),
						jsonObject.getDouble("latitude"),
						jsonObject.getDouble("longitude"));

				locations.add(location);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return locations;
	}
}
