package naweb.wonders.smartaudiocityguide.services;

import naweb.wonders.smartaudiocityguide.MainActivity.BackgroundServiceReceiver;
import naweb.wonders.smartaudiocityguide.threads.LocationsControlThread;
import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

public class BackgroundService extends IntentService implements
		LocationListener, ConnectionCallbacks, OnConnectionFailedListener,
		OnInitListener {
	private LocationClient locationClient;

	private static final LocationRequest LOCATION_REQUEST = LocationRequest
			.create().setInterval(2000) // 2 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	public BackgroundService(String name) {
		super(name);
	}

	public BackgroundService() {
		super(".services.BackgroundService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		locationClient = new LocationClient(this, this, this);
		locationClient.connect();
	}

	@Override
	public void onInit(int arg0) {
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		locationClient.requestLocationUpdates(LOCATION_REQUEST, this);

		LocationsControlThread locationsControlThread = new LocationsControlThread(
				locationClient);
		locationsControlThread.start();
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());

		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(BackgroundServiceReceiver.PROCESS_RESPONSE);
		broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
		broadcastIntent.putExtra("LatLng", latLng);
		sendBroadcast(broadcastIntent);
	}
}
