package naweb.wonders.smartaudiocityguide;

import java.io.File;

import naweb.wonders.smartaudiocityguide.services.BackgroundService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends FragmentActivity implements OnInitListener,
		OnPreparedListener {

	private GoogleMap googleMap = null;
	private Marker userMarker = null;

	private TextToSpeech textToSpeech;

	private BackgroundServiceReceiver backgroundServiceReceiver = null;

	private MediaRecorder mediaRecorder;
	private String recordedAudioPath;
	private Boolean isRecording = false;

	private Intent serviceIntent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		createFolder();

		textToSpeech = new TextToSpeech(this, this);

		serviceIntent = new Intent(this, BackgroundService.class);
		startService(serviceIntent);

		setContentView(R.layout.activity_main);
	}

	private void createFolder() {
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/SACG/Sounds";

		File directory = new File(path);
		Boolean i = directory.mkdirs();

		i.getClass();

		recordedAudioPath = path + "/newAudioRecord.3gp";
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

		try {
			MapsInitializer.initialize(getApplicationContext());
		} catch (GooglePlayServicesNotAvailableException e) {
			return;
		}

		if (userMarker == null) {
			userMarker = googleMap
					.addMarker(new MarkerOptions()
							.position(new LatLng(0, 0))
							.icon(BitmapDescriptorFactory
									.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
		}
	}

	@Override
	public void onInit(int arg0) {
		textToSpeech.speak("Smart Audio City Guide", TextToSpeech.QUEUE_ADD,
				null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		stopService(serviceIntent);
	}

	public void changeUserPosition(LatLng latLng) {
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

		userMarker.setPosition(latLng);
	}

	public void routeButton_OnClick(View view) {
		if (!isRecording) {
			startAudioRecording();

			isRecording = true;
		} else {
			stopAudioRecording();
			playAudioRecord();

			isRecording = false;
		}
	}

	public void worldButton_OnClick(View view) {
		textToSpeech.speak("Você escolheu mundo", TextToSpeech.QUEUE_ADD, null);
	}

	public void soundButton_OnClick(View view) {
		textToSpeech.speak("Você escolheu som", TextToSpeech.QUEUE_ADD, null);
	}

	private void startAudioRecording() {
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mediaRecorder.setOutputFile(recordedAudioPath);
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			mediaRecorder.prepare();
		} catch (Exception e) {
			return;
		}

		mediaRecorder.start();
	}

	private void stopAudioRecording() {
		mediaRecorder.stop();
		mediaRecorder.release();
	}

	private void playAudioRecord() {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnPreparedListener(this);

		try {
			mediaPlayer.setDataSource(recordedAudioPath);
			mediaPlayer.prepare();
		} catch (Exception e) {
			return;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mediaPlayer.start();
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
