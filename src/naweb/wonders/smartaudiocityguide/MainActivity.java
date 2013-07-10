package naweb.wonders.smartaudiocityguide;

import android.location.Location;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements LocationListener,
		ConnectionCallbacks, OnConnectionFailedListener, OnInitListener {

	private GoogleMap googleMap = null;
	private LocationClient locationClient;
	private Marker userMarker = null;

	private TextToSpeech textToSpeech;

	private Boolean ready = false;

	private static final LocationRequest LOCATION_REQUEST = LocationRequest
			.create().setInterval(2000) // 2 seconds
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		textToSpeech = new TextToSpeech(this, this);

		locationClient = new LocationClient(this, this, this);

		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (googleMap == null) {
			googleMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
		}

		if (userMarker == null) {
			userMarker = googleMap
					.addMarker(new MarkerOptions()
							.position(new LatLng(0, 0))
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}

		locationClient.connect();
	}

	@Override
	public void onLocationChanged(Location location) {
		if (ready == false) {
			ready = true;
			textToSpeech.speak("pronto", TextToSpeech.QUEUE_ADD, null);
		}

		LatLng latLng = new LatLng(location.getLatitude(),
				location.getLongitude());

		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		userMarker.setPosition(latLng);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle arg0) {
		locationClient.requestLocationUpdates(LOCATION_REQUEST, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onInit(int arg0) {
		textToSpeech.speak("Smart Audio City Guide", TextToSpeech.QUEUE_ADD,
				null);
	}
}
