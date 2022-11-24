package com.lamesa.lofiradiopro.ui;


import static com.lamesa.lofiradiopro.utils.metodos.InstagramCreador;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.amplitude.api.Amplitude;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.domain.model.coleccion;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Luis Mesa on 09/09/2019.
 */
public class Splash extends AppCompatActivity {

	public static Context mContext;
	public static List<coleccion> lstColeccion;
	private static String youtubeLink;
	private static String mLinkCancion;
	private final ArrayList<String> lstNombreListas = new ArrayList<>();
	private final ArrayList<String> lstGifs = new ArrayList<>();
	private TinyDB tinydb;
	private ArrayList<String> temp_lstNombreListas;
	private ArrayList<String> temp_lstGifs;
	private ArrayList<String> temp_lstCanciones;
	private ArrayList<String> lstCanciones = new ArrayList<>();
	private ArrayList<String> lstCancionesMP3;
	private boolean Termino;
	private Handler handler;
	private TimerTask Comprobar;
	private TextView tvCreador;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		Amplitude.getInstance().initialize(this, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking(getApplication());
		tinydb = new TinyDB(this);
		AppVersion();

		TraerNombreListas();
		ComprobarListas();


		tvCreador = (TextView) findViewById(R.id.tv_creador);
		tvCreador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InstagramCreador(Splash.this);
			}
		});


	}


	private void AppVersion() {
		PackageInfo pinfo = null;
		try {
			pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
			int versionNumber = pinfo.versionCode;
			String versionName = pinfo.versionName;

			TextView tvAppVersion = findViewById(R.id.tv_appversion);
			tvAppVersion.setText("Version: " + versionName);

		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

	}

	private void ComprobarListas() {


		final Handler handler = new Handler();
		Timer timer = new Timer();

		Comprobar = new TimerTask() {
			@Override
			public void run() {
				handler.post(new Runnable() {
					public void run() {
						try {


							ArrayList<String> temp_lstGifsPrueba = tinydb.getListString("lstGifs");
							ArrayList<String> temp_lstNombreListasPrueba = tinydb.getListString("lstNombreListas");
							ArrayList<String> temp_lstCancionesPrueba = tinydb.getListString("Random");
							ArrayList<String> temp_lstCancionesYT = tinydb.getListString("YT Random");

							if (temp_lstCancionesPrueba != null && temp_lstGifsPrueba != null && temp_lstNombreListasPrueba != null && temp_lstCancionesYT != null) {
								if (temp_lstCancionesPrueba.size() != 0 && temp_lstGifsPrueba.size() != 0 && temp_lstNombreListasPrueba.size() != 0 && temp_lstCancionesYT.size() != 0) {
									Comprobar.cancel();
									startActivity(new Intent(Splash.this, MainActivity.class));
									finish();
								} else {
									System.out.println("LISTAS VACIAS ");

								}
							} else {
								System.out.println("LISTAS NULAS ");

							}
							//you may call the cancel() method but if it is not handled in doInBackground() method
						} catch (Exception e) {
							Log.e("error", e.getMessage());
						}
					}
				});
			}
		};
		timer.schedule(Comprobar, 0, 7000);
	}

	private void TraerNombreListas() {
		//  if (u != null) {
		TraerGifs();
		Query database = FirebaseDatabase.getInstance().getReference().child("lofiradio").child("musica");
		database.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				lstNombreListas.removeAll(lstNombreListas);
				for (DataSnapshot snapshot :
						dataSnapshot.getChildren()) {
					String NombreListas = String.valueOf(snapshot.getKey());
					lstNombreListas.add(NombreListas);
					System.out.println("NombreListas :: :: :: " + NombreListas);
				}
				tinydb.putListString("lstNombreListas", lstNombreListas);
				temp_lstNombreListas = tinydb.getListString("lstNombreListas");
				for (String NombreLista : lstNombreListas) {
					TraerCancionesDeLista(NombreLista);
				}
			}
			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
	}


	private void TraerGifs() {

		Query database2 = FirebaseDatabase.getInstance().getReference().child("lofiradio").child("gif");
		database2.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				lstGifs.removeAll(lstGifs);
				for (DataSnapshot snapshot :
						dataSnapshot.getChildren()) {
					String Gifs = String.valueOf(snapshot.getValue());
					lstGifs.add(Gifs);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
		//endregion
	}


	private void TraerCancionesDeLista(final String NombreLista) {
		lstCanciones = new ArrayList<>();
		lstCancionesMP3 = new ArrayList<>();
		Query database = FirebaseDatabase.getInstance().getReference().child("lofiradio").child("musica").child(NombreLista);
		database.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
				lstCanciones.removeAll(lstCanciones);
				for (DataSnapshot snapshot :
						dataSnapshot.getChildren()) {
					String Canciones = String.valueOf(snapshot.getValue());
					lstCanciones.add(Canciones);
				}
				tinydb.putListString(NombreLista, lstCanciones);
				tinydb.putListString("YT " + NombreLista, lstCanciones);
				temp_lstCanciones = tinydb.getListString(NombreLista);
				for (String temp_Canciones : temp_lstCanciones) {
					//  System.out.println("CANCIONES EN TEMPRALES DE " + NombreLista + " ::::: " + temp_Canciones);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError databaseError) {
			}
		});
	}

	//endregion


}
