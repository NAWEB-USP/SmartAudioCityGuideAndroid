package naweb.wonders.smartaudiocityguide.threads;

import naweb.wonders.smartaudiocityguide.models.Location;
import naweb.wonders.smartaudiocityguide.models.WebServer;

import com.google.android.gms.location.LocationClient;

public class LocationThread extends Thread {
	private LocationClient locationClient;
	private Location location;

	private WebServer webServer;

	public LocationThread(LocationClient locationClient, Location location) {
		this.locationClient = locationClient;
		this.location = location;

		this.webServer = new WebServer();
	}

	public void run() {
		while (true) {
			while (locationClient.isConnected() == false) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			double distance = location.distanceToInMeters(locationClient
					.getLastLocation().getLatitude(), locationClient
					.getLastLocation().getLongitude());
		}
	}
}
