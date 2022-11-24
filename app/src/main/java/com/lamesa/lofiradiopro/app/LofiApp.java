package com.lamesa.lofiradiopro.app;

import static com.lamesa.lofiradiopro.utils.shared.MIXPANEL_TOKEN;
import static com.lamesa.lofiradiopro.utils.shared.sp_dia_ingreso;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_dia_ingreso;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Calendar;

public class LofiApp extends Application {

	public static MixpanelAPI mixpanel;
	private FirebaseAuth mAuthh;

	@Override
	public void onCreate() {
		super.onCreate();

		mAuthh = FirebaseAuth.getInstance();
		FirebaseUser u = mAuthh.getCurrentUser();
		mixpanel =
				MixpanelAPI.getInstance(this, MIXPANEL_TOKEN);
		Calendar c = Calendar.getInstance();
		int DiaActual = c.get(Calendar.DAY_OF_WEEK);

		if (DiaActual != sp_get_dia_ingreso(LofiApp.this)) {
			// This method must be called on a background thread.
			class TestAsync extends AsyncTask<Void, Integer, Void> {
				final String TAG = getClass().getSimpleName();
				protected void onPreExecute() {
					super.onPreExecute();
					Log.d(TAG + " PreExceute", "On pre Exceute......");
				}
				protected Void doInBackground(Void... arg0) {
					Log.d(TAG + " DoINBackGround", "On doInBackground...");

					Glide.get(LofiApp.this).clearDiskCache();

					return null;
				}
				protected void onProgressUpdate(Integer... a) {
					super.onProgressUpdate(a);
					Log.d(TAG + " onProgressUpdate", "You are in progress update ... " + a[0]);
				}
				protected void onPostExecute(Void result) {
					super.onPostExecute(result);
					Log.d(TAG + " onPostExecute", "" + result);
				}
			}

			new TestAsync().execute();
			sp_dia_ingreso(DiaActual, LofiApp.this);
			System.out.println("Cache limpiada!!!!");

		}
	}


}



