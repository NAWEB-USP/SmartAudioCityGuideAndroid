package naweb.wonders.smartaudiocityguide;

import naweb.wonders.smartaudiocityguide.services.BackgroundService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnInitListener {

	private GoogleMap googleMap = null;
	private Marker userMarker = null;

	private TextToSpeech textToSpeech;

	private BackgroundServiceReceiver backgroundServiceReceiver = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		textToSpeech = new TextToSpeech(this, this);

		Intent intent = new Intent(this, BackgroundService.class);
		startService(intent);

		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();

		IntentFilter intentFilter = new IntentFilter(
				BackgroundServiceReceiver.PROCESS_RESPONSE);
		intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
		backgroundServiceReceiver = new BackgroundServiceReceiver();
		registerReceiver(backgroundServiceReceiver, intentFilter);

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
	}

	public void changeUserPosition(LatLng latLng) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		userMarker.setPosition(latLng);
	}

	@Override
	public void onInit(int arg0) {
		textToSpeech.speak("Smart Audio City Guide", TextToSpeech.QUEUE_ADD,
				null);
	}

	public void routeButton_OnClick(View view) {
		textToSpeech.speak("Voc� escolheu rota", TextToSpeech.QUEUE_ADD, null);
	}

	public void worldButton_OnClick(View view) {
		textToSpeech.speak("Voc� escolheu mundo", TextToSpeech.QUEUE_ADD, null);
	}

	public void soundButton_OnClick(View view) {
		textToSpeech.speak("Voc� escolheu som", TextToSpeech.QUEUE_ADD, null);
	}

	public class BackgroundServiceReceiver extends BroadcastReceiver {
		public static final String PROCESS_RESPONSE = "BackgroundServiceReceiver.PROCESS_RESPONSE";

		@Override
		public void onReceive(Context context, Intent intent) {
			LatLng latLng = intent.getParcelableExtra("LatLng");

			changeUserPosition(latLng);
		}
	}
}
