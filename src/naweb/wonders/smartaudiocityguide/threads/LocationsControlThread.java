package naweb.wonders.smartaudiocityguide.threads;

import java.util.Hashtable;
import java.util.List;

import naweb.wonders.smartaudiocityguide.models.Location;
import naweb.wonders.smartaudiocityguide.models.WebServer;

import com.google.android.gms.location.LocationClient;

public class LocationsControlThread extends Thread {
	private LocationClient locationClient;

	public LocationsControlThread(LocationClient locationClient) {
		this.locationClient = locationClient;
	}

	public void run() {
		Hashtable<Integer, LocationThread> hashtable = new Hashtable<Integer, LocationThread>();

		while (true) {
			List<Location> locations = getLocationsAround();

			for (Location location : locations) {
				LocationThread thread = hashtable.get(location.getId());

				if (thread == null) {
					thread = new LocationThread(locationClient, location);

					hashtable.put(location.getId(), thread);

					thread.start();

					continue;
				}

				if (thread.isAlive() == false) {
					thread.start();
				}
			}

			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public List<Location> getLocationsAround() {
		if (locationClient.isConnected() == false)
			return null;

		Location location = new Location(locationClient.getLastLocation()
				.getLatitude(), locationClient.getLastLocation().getLongitude());

		WebServer webServer = new WebServer();

		return webServer.requestLocationsAround(location);
	}
}
