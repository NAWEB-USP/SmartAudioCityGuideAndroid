package naweb.wonders.smartaudiocityguide.services;

import naweb.wonders.smartaudiocityguide.models.Location;
import naweb.wonders.smartaudiocityguide.models.WebServer;

import com.google.android.gms.location.LocationClient;

public class LocationService {
	private LocationClient locationClient;

	public LocationService(LocationClient locationClient) {
		this.locationClient = locationClient;
	}

	public void getLocationsAround() {
		final Location location = new Location(locationClient.getLastLocation()
				.getLatitude(), locationClient.getLastLocation().getLongitude());

		final WebServer webServer = new WebServer();

		(new Thread() {
			public void run() {
				webServer.requestLocationsAround(location);
			}
		}).start();
	}
}
