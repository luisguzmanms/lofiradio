package com.lamesa.lofiradiopro.radio.player;

import static com.lamesa.lofiradiopro.ui.MainActivity.ivSleep;
import static com.lamesa.lofiradiopro.ui.MainActivity.lin_opciones;
import static com.lamesa.lofiradiopro.ui.MainActivity.tvMinutosfaltantes;
import static com.lamesa.lofiradiopro.utils.metodos.EnviarFavorito;
import static com.lamesa.lofiradiopro.utils.shared.ReproducirCancion;
import static com.lamesa.lofiradiopro.utils.shared.sp_autoapagado;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_cancion_sonando_nombre;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_linkyoutube_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_numcancion_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_numcancion_sonando;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.amplitude.api.Amplitude;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.data.TinyDB;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Random;


public class RadioService extends Service implements Player.EventListener, AudioManager.OnAudioFocusChangeListener {

	public static final String ACTION_PLAY = "com.mcakir.radio.player.ACTION_PLAY";
	public static final String ACTION_PAUSE = "com.mcakir.radio.player.ACTION_PAUSE";
	public static final String ACTION_STOP = "com.mcakir.radio.player.ACTION_STOP";
	public static CountDownTimer countDownTimer;

	private final IBinder iBinder = new LocalBinder();
	private final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
	private Handler handler;
	private SimpleExoPlayer exoPlayer;
	private MediaSessionCompat mediaSession;
	private MediaControllerCompat.TransportControls transportControls;

	private boolean onGoingCall = false;
	private TelephonyManager telephonyManager;

	private WifiManager.WifiLock wifiLock;

	private AudioManager audioManager;

	private MediaNotificationManager notificationManager;

	private String status;

	private String strAppName;
	private String strLiveBroadcast;
	private String streamUrl;

	private final PhoneStateListener phoneStateListener = new PhoneStateListener() {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {

			if (state == TelephonyManager.CALL_STATE_OFFHOOK
					|| state == TelephonyManager.CALL_STATE_RINGING) {

				if (!isPlaying()) return;

				onGoingCall = true;
				stop();

			} else if (state == TelephonyManager.CALL_STATE_IDLE) {

				if (!onGoingCall) return;

				onGoingCall = false;
				resume();
			}
		}
	};
	private final MediaSessionCompat.Callback mediasSessionCallback = new MediaSessionCompat.Callback() {
		@Override
		public void onPause() {
			super.onPause();

			pause();
		}


		@Override
		public void onStop() {
			super.onStop();

			stop();

			notificationManager.cancelNotify();
		}

		@Override
		public void onPlay() {
			super.onPlay();

			resume();
		}
	};

	private TinyDB tinydbRadioService;


	public static void EncenderAutoApagado(int minutos, Context mContext) {


		int minutes = minutos;
		int milliseconds = minutes * 60 * 1000;


		countDownTimer = new CountDownTimer(milliseconds, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {


				NumberFormat f = new DecimalFormat("00");
				long hour = (millisUntilFinished / 3600000) % 24;
				long min = (millisUntilFinished / 60000) % 60;
				long sec = (millisUntilFinished / 1000) % 60;
				tvMinutosfaltantes.setVisibility(View.VISIBLE);
				tvMinutosfaltantes.setText(f.format(hour) + ":" + f.format(min) + ":" + f.format(sec));


                /*

                tvMinutosfaltantes.setText(String.format("%02d:%02d:%02d",
                        TimeUnit.MILLISECONDS.toHours(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));

                 */


			}

			@Override
			public void onFinish() {
				Toast.makeText(mContext, mContext.getString(R.string.radioapagado), Toast.LENGTH_SHORT).show();
				Intent stopIntent = new Intent(mContext, RadioService.class);
				stopIntent.setAction(RadioService.ACTION_STOP);
				PendingIntent stopAction = PendingIntent.getService(mContext, 3, stopIntent, 0);
				mContext.startService(stopIntent);

				// slide-up animation
				Animation slideUp = AnimationUtils.loadAnimation(mContext, R.anim.anim_slide_bottom_out_wallpaper);
				lin_opciones.setVisibility(View.VISIBLE);
				lin_opciones.startAnimation(slideUp);
				ivSleep.setImageResource(R.drawable.ic_moon_off);
				sp_autoapagado(false, mContext);

			}

		}.start();


		countDownTimer.start();
		ivSleep.setImageResource(R.drawable.ic_moon_on);
		sp_autoapagado(true, mContext);


	}


	public static void ApagarAutoApagado(Context mContext) {


		if (countDownTimer != null) {
			countDownTimer.cancel();
			tvMinutosfaltantes.setText("00:00:00");
			ivSleep.setImageResource(R.drawable.ic_moon_off);
			Toast.makeText(mContext, mContext.getString(R.string.apagado), Toast.LENGTH_SHORT).show();
			sp_autoapagado(false, mContext);

		}


	}


	@Nullable
	@Override
	public IBinder onBind(Intent intent) {

		return iBinder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		strAppName = getResources().getString(R.string.app_name);
		strLiveBroadcast = "Lo-fi RADIO pro";
		tinydbRadioService = new TinyDB(this);

		onGoingCall = false;

		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		notificationManager = new MediaNotificationManager(this);

		wifiLock = ((WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE))
				.createWifiLock(WifiManager.WIFI_MODE_FULL, "mcScPAmpLock");

		mediaSession = new MediaSessionCompat(this, getClass().getSimpleName());
		transportControls = mediaSession.getController().getTransportControls();
		mediaSession.setActive(true);
		mediaSession.setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
		mediaSession.setMetadata(new MediaMetadataCompat.Builder()
				.putString(MediaMetadataCompat.METADATA_KEY_ARTIST, "...")
				.putString(MediaMetadataCompat.METADATA_KEY_ALBUM, strAppName)
				.putString(MediaMetadataCompat.METADATA_KEY_TITLE, sp_get_cancion_sonando_nombre(this)) // strLiveBroadcast
				.build());
		mediaSession.setCallback(mediasSessionCallback);

		telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

		handler = new Handler();
		DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
		AdaptiveTrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
		DefaultTrackSelector trackSelector = new DefaultTrackSelector(trackSelectionFactory);
		exoPlayer = ExoPlayerFactory.newSimpleInstance(getApplicationContext(), trackSelector);
		exoPlayer.addListener(this);


		registerReceiver(becomingNoisyReceiver, new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY));

		status = PlaybackStatus.IDLE;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		String action = intent.getAction();

		if (TextUtils.isEmpty(action))
			return START_NOT_STICKY;

		int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {

			stop();


			return START_NOT_STICKY;
		}

		if (action.equalsIgnoreCase(ACTION_PLAY)) {

			transportControls.play();

		} else if (action.equalsIgnoreCase(ACTION_PAUSE)) {

			transportControls.pause();

		} else if (action.equalsIgnoreCase(ACTION_STOP)) {

			transportControls.stop();

		} else if (action.equalsIgnoreCase("favorito")) {

			//  Toast.makeText(this, "aaaaaaaaaaaaaaaaa", Toast.LENGTH_SHORT).show();
			EnviarFavorito(this, tinydbRadioService);

		}

		return START_NOT_STICKY;


	}

	@Override
	public boolean onUnbind(Intent intent) {

		if (status.equals(PlaybackStatus.IDLE))
			stopSelf();

		return super.onUnbind(intent);
	}

	@Override
	public void onRebind(final Intent intent) {

	}

	@Override
	public void onDestroy() {

		pause();

		exoPlayer.release();
		exoPlayer.removeListener(this);

		if (telephonyManager != null)
			telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);

		notificationManager.cancelNotify();

		mediaSession.release();

		unregisterReceiver(becomingNoisyReceiver);

		super.onDestroy();
	}

	@Override
	public void onAudioFocusChange(int focusChange) {

		switch (focusChange) {
			case AudioManager.AUDIOFOCUS_GAIN:

				exoPlayer.setVolume(0.8f);

				resume();

				break;

			case AudioManager.AUDIOFOCUS_LOSS:

				stop();

				break;

			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:

				if (isPlaying()) pause();

				break;

			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

				if (isPlaying())
					exoPlayer.setVolume(0.1f);

				break;
		}

	}

	@Override
	public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

		switch (playbackState) {
			case Player.STATE_BUFFERING:
				status = PlaybackStatus.LOADING;
				Toast.makeText(this, getString(R.string.cargandocancion), Toast.LENGTH_SHORT).show();
				//   Notification.show(this, sp_get_cancion_sonando_nombre(this), sp_get_lista_sonando(this), R.mipmap.ic_launcher).showNotification();

				break;
			case Player.STATE_ENDED:
				status = PlaybackStatus.STOPPED;

				try {

					String ListaSonando = sp_get_lista_sonando(this);
					int NumCancionSonando = sp_get_numcancion_sonando(this);


					//    Toast.makeText(this, "STOPPED", Toast.LENGTH_SHORT).show();
					ArrayList<String> temp_lstCanciones = tinydbRadioService.getListString(ListaSonando);


					Random RandomCancion = new Random();
					int numCancion = RandomCancion.nextInt(temp_lstCanciones.size());

					if (numCancion != NumCancionSonando) {
						ReproducirCancion(ListaSonando, temp_lstCanciones.get(numCancion), this, true);
						sp_lista_sonando(ListaSonando, this);
						sp_numcancion_sonando(numCancion, this);


						String listasonando = "Random";
						if (sp_get_lista_sonando(this).equals("favoritos")) {
							listasonando = "favoritos";
						} else if (sp_get_lista_sonando(this).equals("historial")) {
							Intent stopIntent = new Intent(this, RadioService.class);
							stopIntent.setAction(RadioService.ACTION_STOP);
							PendingIntent stopAction = PendingIntent.getService(this, 3, stopIntent, 0);
							startService(stopIntent);
						} else {
							listasonando = "YT " + sp_get_lista_sonando(this);
						}


						JSONObject eventProperties2 = null;
						try {
							eventProperties2 = new JSONObject().put("CANCION-REPRODUCIDA_NombreCancion", sp_get_cancion_sonando_nombre(this)).put("CANCION-REPRODUCIDA_LinkYT", sp_get_linkyoutube_sonando(this));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Amplitude.getInstance().logEvent("CANCION REPRODUCIDA");
						Amplitude.getInstance().setUserProperties(eventProperties2);


					} else {
						ReproducirCancion(ListaSonando, temp_lstCanciones.get(0), this, true);
						sp_lista_sonando(ListaSonando, this);
						sp_numcancion_sonando(numCancion, this);


						String listasonando = "Random";
						if (sp_get_lista_sonando(this).equals("favoritos")) {
							listasonando = "favoritos";
						} else {
							listasonando = "YT " + sp_get_lista_sonando(this);
						}

						JSONObject eventProperties2 = null;
						try {
							eventProperties2 = new JSONObject().put("CANCION-REPRODUCIDA_NombreCancion", sp_get_cancion_sonando_nombre(this)).put("CANCION-REPRODUCIDA_LinkYT", tinydbRadioService.getListString(listasonando).get(sp_get_numcancion_sonando(this)));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						Amplitude.getInstance().logEvent("CANCION REPRODUCIDA");
						Amplitude.getInstance().setUserProperties(eventProperties2);


					}

				} catch (Exception e) {
					e.printStackTrace();
				}


				break;
			case Player.STATE_IDLE:
				status = PlaybackStatus.IDLE;
				//  Toast.makeText(this, "IDLE", Toast.LENGTH_SHORT).show();
				System.out.println("IDLE");
				break;
			case Player.STATE_READY:


				status = playWhenReady ? PlaybackStatus.PLAYING : PlaybackStatus.PAUSED;
				// Toast.makeText(this, "STATE_READY", Toast.LENGTH_SHORT).show();

				// AL EMPEZAR LA RADIO ACTIVAR LAS OPCIONES DE DISLIKE Y LIKE Y DETENER
				// slide-up animation
				Animation slideUp = AnimationUtils.loadAnimation(this, R.anim.anim_slide_bottom_in_wallpaper);
				lin_opciones.setVisibility(View.VISIBLE);
				lin_opciones.startAnimation(slideUp);


				break;
			default:
				status = PlaybackStatus.IDLE;
				break;
		}

		if (!status.equals(PlaybackStatus.IDLE))
			notificationManager.startNotify(status);

		EventBus.getDefault().post(status);
	}

	@Override
	public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

	}

	@Override
	public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

	}

	@Override
	public void onLoadingChanged(boolean isLoading) {

	}

	@Override
	public void onPlayerError(ExoPlaybackException error) {

		EventBus.getDefault().post(PlaybackStatus.ERROR);
	}

	@Override
	public void onRepeatModeChanged(int repeatMode) {

	}

	@Override
	public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

	}

	@Override
	public void onPositionDiscontinuity(int reason) {

	}

	@Override
	public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

	}

	@Override
	public void onSeekProcessed() {

	}

	public void play(String streamUrl) {

		this.streamUrl = streamUrl;

		if (wifiLock != null && !wifiLock.isHeld()) {

			wifiLock.acquire();

		}

//        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(getUserAgent());

		DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, getUserAgent(), BANDWIDTH_METER);

		ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(dataSourceFactory)
				.setExtractorsFactory(new DefaultExtractorsFactory())
				.createMediaSource(Uri.parse(streamUrl));

		exoPlayer.prepare(mediaSource);
		exoPlayer.setPlayWhenReady(true);
	}

	public void resume() {

		if (streamUrl != null)
			play(streamUrl);
	}

	public void pause() {

		exoPlayer.setPlayWhenReady(false);
		audioManager.abandonAudioFocus(this);
		wifiLockRelease();
	}

	public void stop() {

		exoPlayer.stop();

		audioManager.abandonAudioFocus(this);
		wifiLockRelease();
	}

	public void playOrPause(String url) {

		if (streamUrl != null && streamUrl.equals(url)) {

			if (!isPlaying()) {

				play(streamUrl);

			} else {

				pause();
			}

		} else {

			if (isPlaying()) {

				pause();

			}

			play(url);
		}
	}

	public String getStatus() {

		return status;
	}

	public MediaSessionCompat getMediaSession() {

		return mediaSession;
	}

	public boolean isPlaying() {

		return this.status.equals(PlaybackStatus.PLAYING);
	}

	private void wifiLockRelease() {

		if (wifiLock != null && wifiLock.isHeld()) {

			wifiLock.release();
		}
	}

	private String getUserAgent() {

		return Util.getUserAgent(this, getClass().getSimpleName());
	}

	public class LocalBinder extends Binder {
		public RadioService getService() {
			return RadioService.this;
		}
	}
}
