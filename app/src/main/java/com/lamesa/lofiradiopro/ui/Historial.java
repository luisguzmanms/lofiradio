package com.lamesa.lofiradiopro.ui;

import static com.lamesa.lofiradiopro.utils.metodos.InstagramCreador;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplitude.api.Amplitude;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.adapter.DataAdapterCancionHistorial;
import com.lamesa.lofiradiopro.domain.model.favoritomodel;
import com.lamesa.lofiradiopro.domain.model.historialcancion;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.radio.player.PlaybackStatus;
import com.lamesa.lofiradiopro.radio.player.RadioManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by World Of UI/UX on 17/4/19.
 */

public class Historial extends AppCompatActivity {
	private static final String TAG = "TAG";


	private List<favoritomodel> lstFavoritos;
	private ArrayList<String> lstFavoritosCanciones;
	private FirebaseAnalytics mFirebaseAnalytics;
	private RadioManager radioManager;
	private RecyclerView recyclerViewHistorial;
	private TextView tvInstagramCreador;
	private TinyDB tinydbHistorial;
	private DataAdapterCancionHistorial mDataAdapterCancionHistorial;
	private List<historialcancion> mListaHistorialcanciones;
	private ImageView tvBorrarHistorial;
	private ArrayList<String> temp_lstCancionesHistorial;
	private ArrayList<String> temp_lstCancionesHoraHistorial;
	private ArrayList<String> temp_lstCancionesLinkHistorial;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_historial);
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(Historial.this);
		Amplitude.getInstance().initialize(this, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking(getApplication());
		// funcion de la radio PARA QUE FUNCIONE
		radioManager = RadioManager.with(this);
		tinydbHistorial = new TinyDB(this);
		vistas();
		ListasRecycler();


		if (temp_lstCancionesHistorial.size() == 0 || temp_lstCancionesHistorial == null) {
			Toast.makeText(this, "El historial esta vacio", Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	private void ListasRecycler() {


		recyclerViewHistorial = findViewById(R.id.recyclerViewHistorial);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Historial.this);
		recyclerViewHistorial.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		recyclerViewHistorial.setItemAnimator(new DefaultItemAnimator());

		mListaHistorialcanciones = new ArrayList<>();

		temp_lstCancionesHistorial = tinydbHistorial.getListString("lstCancionesHistorial");
		temp_lstCancionesHoraHistorial = tinydbHistorial.getListString("lstCancionesHoraHistorial");
		temp_lstCancionesLinkHistorial = tinydbHistorial.getListString("lstCancionesLinkHistorial");


		for (int i = 0; i < temp_lstCancionesHistorial.size(); i++) {

			historialcancion mhistorialcancion = new historialcancion(temp_lstCancionesHistorial.get(i), temp_lstCancionesHoraHistorial.get(i), temp_lstCancionesLinkHistorial.get(i));
			mListaHistorialcanciones.add(mhistorialcancion);
		}

		Collections.reverse(mListaHistorialcanciones);

		mDataAdapterCancionHistorial = new DataAdapterCancionHistorial(Historial.this, mListaHistorialcanciones);
		recyclerViewHistorial.setAdapter(mDataAdapterCancionHistorial);
	}


	private void vistas() {
		tvInstagramCreador = findViewById(R.id.tv_instagramcreador);
		tvInstagramCreador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InstagramCreador(Historial.this);
			}
		});
		tvBorrarHistorial = findViewById(R.id.iv_borrarhistorial);
		tvBorrarHistorial.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create Alert using Builder
				CFAlertDialog.Builder builder = new CFAlertDialog.Builder(Historial.this)
						.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)
						.setTitle(getString(R.string.eliminarhistorial))

						.addButton(getString(R.string.si), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
							temp_lstCancionesHistorial.removeAll(temp_lstCancionesHistorial);
							temp_lstCancionesHoraHistorial.removeAll(temp_lstCancionesHoraHistorial);
							temp_lstCancionesLinkHistorial.removeAll(temp_lstCancionesLinkHistorial);

							tinydbHistorial.putListString("lstCancionesHistorial", temp_lstCancionesHistorial);
							tinydbHistorial.putListString("lstCancionesHoraHistorial", temp_lstCancionesHoraHistorial);
							tinydbHistorial.putListString("lstCancionesLinkHistorial", temp_lstCancionesLinkHistorial);

							mDataAdapterCancionHistorial.notifyDataSetChanged();

							onBackPressed();

							dialog.dismiss();

						})
						.addButton(getString(R.string.cancelar), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
							dialog.dismiss();
						});

				// Show the alert
				builder.show();
			}
		});
	}


	//region RADIO (IMPORTANTE PARA EL FUNCIONAMIENTO, DEBE IR EN LA ACTIVIDAD EN DONDE RE REPRODUSCA UNA CANCION)


	@Override
	public void onStart() {

		super.onStart();

		EventBus.getDefault().register(this);
	}

	@Override
	public void onStop() {

		EventBus.getDefault().unregister(this);


		super.onStop();


	}

	@Override
	protected void onDestroy() {

//        radioManager.unbind();


		super.onDestroy();


	}

	@Override
	protected void onResume() {
		super.onResume();

		radioManager.bind();
	}

	@Override
	public void onBackPressed() {

		finish();
	}


	//endregion

	@Subscribe
	public void onEvent(String status) {

		switch (status) {

			case PlaybackStatus.LOADING:

				// loading
				//     Toast.makeText(mContext, "Cargando...", Toast.LENGTH_SHORT).show();
				//   System.out.println("CARGANDOOOOOOOOOOOOOOOOOOOOOOO");

				break;

			case PlaybackStatus.ERROR:

				//   Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();

				break;

			case PlaybackStatus.IDLE:

				//   Toast.makeText(this, "IDLEEEEEEEEEEEEEEE", Toast.LENGTH_SHORT).show();

				break;

			case PlaybackStatus.STOPPED:

				//

				break;

		}


	}


}








