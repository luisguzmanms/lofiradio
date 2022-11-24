package com.lamesa.lofiradiopro.radio.player;

import static com.lamesa.lofiradiopro.utils.shared.sp_get_cancion_sonando_nombre;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_lista_sonando;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.ui.MainActivity;


public class MediaNotificationManager {

	public static final int NOTIFICATION_ID = 555;
	private final String PRIMARY_CHANNEL = "PRIMARY_CHANNEL_ID";
	private final String PRIMARY_CHANNEL_NAME = "LOFI RADIO";
	Context mContext;
	private final RadioService service;
	private final String strAppName;
	private String strCancionNombre;
	private final Resources resources;
	private final NotificationManagerCompat notificationManager;

	public MediaNotificationManager(RadioService service) {

		this.service = service;
		this.resources = service.getResources();

		strAppName = resources.getString(R.string.app_name);


		notificationManager = NotificationManagerCompat.from(service);
	}

	public void startNotify(String playbackStatus) {

		Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.drawable.icon_app);

		int icon = R.drawable.ic_pause_white;
		Intent playbackAction = new Intent(service, RadioService.class);
		playbackAction.setAction(RadioService.ACTION_PAUSE);
		PendingIntent action = PendingIntent.getService(service, 1, playbackAction, 0);

		if (playbackStatus.equals(PlaybackStatus.PAUSED)) {

			icon = R.drawable.ic_play_white;
			playbackAction.setAction(RadioService.ACTION_PLAY);
			action = PendingIntent.getService(service, 2, playbackAction, 0);

		}

		Intent stopIntent = new Intent(service, RadioService.class);
		stopIntent.setAction(RadioService.ACTION_STOP);
		PendingIntent stopAction = PendingIntent.getService(service, 3, stopIntent, 0);


		Intent intentFavorito = new Intent(service, RadioService.class);
		intentFavorito.setAction("favorito");
		PendingIntent actionFavorito = PendingIntent.getService(service, 3, intentFavorito, 0);


		Intent intent = new Intent(service, MainActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pendingIntent = PendingIntent.getActivity(service, 0, intent, 0);


		notificationManager.cancel(NOTIFICATION_ID);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			NotificationManager manager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
			NotificationChannel channel = new NotificationChannel(PRIMARY_CHANNEL, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW);
			channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
			manager.createNotificationChannel(channel);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(service, PRIMARY_CHANNEL)
				.setAutoCancel(false)
				.setContentTitle(sp_get_cancion_sonando_nombre(service))
				.setContentText(strAppName + " - " + sp_get_lista_sonando(service))
				.setLargeIcon(largeIcon)
				.setContentIntent(pendingIntent)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setSmallIcon(R.drawable.ic_wave)
				.addAction(icon, "pause", action)
				.addAction(R.drawable.ic_favorite_black_24dp, "favorito", actionFavorito)
				.addAction(R.drawable.ic_stop_white, "stop", stopAction)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setWhen(System.currentTimeMillis())
				.setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
						.setMediaSession(service.getMediaSession().getSessionToken())
						.setShowActionsInCompactView(0, 1, 2)
						.setShowCancelButton(true)
						.setCancelButtonIntent(stopAction));

		service.startForeground(NOTIFICATION_ID, builder.build());
	}

	public void cancelNotify() {

		service.stopForeground(true);
	}

}
