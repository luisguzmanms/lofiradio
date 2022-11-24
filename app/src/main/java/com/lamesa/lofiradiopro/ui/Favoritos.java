package com.lamesa.lofiradiopro.ui;

import static com.lamesa.lofiradiopro.utils.metodos.InstagramCreador;
import static com.lamesa.lofiradiopro.utils.metodos.LogCatActivo;
import static com.lamesa.lofiradiopro.utils.metodos.setLogCat;
import static com.lamesa.lofiradiopro.utils.shared.LoginDeUsuario;
import static com.lamesa.lofiradiopro.utils.shared.user;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amplitude.api.Amplitude;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.adapter.DataAdapterFavorito;
import com.lamesa.lofiradiopro.domain.model.favoritomodel;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.radio.player.PlaybackStatus;
import com.lamesa.lofiradiopro.radio.player.RadioManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by Luis Mesa on 05/07/2019.
 */

public class Favoritos extends AppCompatActivity {
	private static final String TAG = "TAG";
	public static DataAdapterFavorito myAdapterFavoritos;
	private List<favoritomodel> lstFavoritos;
	private ArrayList<String> lstFavoritosCanciones;
	private FirebaseAnalytics mFirebaseAnalytics;
	private RadioManager radioManager;
	private RecyclerView recyclerViewFavoritos;
	private TextView tvInstagramCreador;
	private TinyDB tinydbFavoritos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favoritos);
		lstFavoritos = new ArrayList<>();
		lstFavoritosCanciones = new ArrayList<>();
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(Favoritos.this);
		Amplitude.getInstance().initialize(this, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking(getApplication());
		// funcion de la radio PARA QUE FUNCIONE
		radioManager = RadioManager.with(this);
		tinydbFavoritos = new TinyDB(this);
		vistas();
		ListasRecycler();
	}

	private void ListasRecycler() {

		recyclerViewFavoritos = findViewById(R.id.recyclerViewFavoritos);
		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Favoritos.this);
		recyclerViewFavoritos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		recyclerViewFavoritos.setItemAnimator(new DefaultItemAnimator());

		if (LoginDeUsuario() == true) {

			Query database = FirebaseDatabase.getInstance().getReference().child("lofiradio").child("usuario").child(user.getUid()).child("favoritos").child("canciones");

			myAdapterFavoritos = new DataAdapterFavorito(Favoritos.this, lstFavoritos);
			recyclerViewFavoritos.setLayoutManager(new GridLayoutManager(this, 1));
			recyclerViewFavoritos.setAdapter(myAdapterFavoritos);

			database.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
					lstFavoritos.removeAll(lstFavoritos);
					lstFavoritosCanciones.removeAll(lstFavoritosCanciones);
					for (DataSnapshot snapshot :
							dataSnapshot.getChildren()) {
						favoritomodel book = snapshot.getValue(favoritomodel.class);
						lstFavoritos.add(book);
						lstFavoritosCanciones.add(book.getLinkYT());
					}

					// orden al reves
					Collections.reverse(lstFavoritosCanciones);
					tinydbFavoritos.putListString("favoritos", lstFavoritosCanciones);
					for (String ccc : tinydbFavoritos.getListString("favoritos")) {
						// System.out.println("ccc : : :" + ccc);
					}
					myAdapterFavoritos.notifyDataSetChanged();
				}

				@Override
				public void onCancelled(@NonNull DatabaseError databaseError) {

				}
			});
		}
	}

	private void vistas() {
		tvInstagramCreador = findViewById(R.id.tv_instagramcreador);
		tvInstagramCreador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InstagramCreador(Favoritos.this);
			}
		});
	}

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
		radioManager.unbind();
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
				setLogCat("favoritos.class", "Cargando...", LogCatActivo);
				break;
			case PlaybackStatus.ERROR:
				break;
			case PlaybackStatus.IDLE:
				break;
			case PlaybackStatus.STOPPED:
				break;

		}


	}


}








