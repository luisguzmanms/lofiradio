package com.lamesa.lofiradiopro.adapter;

import static com.lamesa.lofiradiopro.utils.metodos.LogCatActivo;
import static com.lamesa.lofiradiopro.utils.metodos.setLogCat;
import static com.lamesa.lofiradiopro.utils.shared.ReproducirCancion;
import static com.lamesa.lofiradiopro.utils.shared.radioManager;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_cancion_sonando_nombre;
import static com.lamesa.lofiradiopro.utils.shared.sp_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_numcancion_sonando;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.amplitude.api.Amplitude;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.domain.model.historialcancion;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.radio.player.RadioService;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luis Mesa on 22/08/2019.
 */

public class DataAdapterCancionHistorial extends RecyclerView.Adapter<DataAdapterCancionHistorial.MyViewHolder> {

	private final Context mContext;
	private final List<historialcancion> mHistorial;
	private ArrayList examplelistFull;
	//   private InterstitialAd mInterstitialAd;
	private FirebaseAuth mAuth;
	private FirebaseAnalytics mFirebaseAnalytics;
	private TinyDB tinydbDataAdapterCancionHistorial;
	private int lastPosition = -1;

	public DataAdapterCancionHistorial(Context mContext, List<historialcancion> mhistorial) {

		this.mContext = mContext;
		this.mHistorial = mhistorial;

	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

		mAuth = FirebaseAuth.getInstance();

		mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
		//     MobileAds.initialize(mContext, "ca-app-pub-4887224789758978~2509724130");
		Amplitude.getInstance().initialize(mContext, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking((Application) mContext.getApplicationContext());
		tinydbDataAdapterCancionHistorial = new TinyDB(mContext);

		View view;
		LayoutInflater mInflater = LayoutInflater.from(mContext);
		view = mInflater.inflate(R.layout.raw_historialcancion, parent, false);
		return new MyViewHolder(view);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {

		setAnimation(holder.cardView, position);

		FirebaseUser u = mAuth.getCurrentUser();

		if (radioManager.isPlaying()) {
			if (sp_get_cancion_sonando_nombre(mContext).equals(mHistorial.get(position).getTitle())) {
				holder.ivStop.setVisibility(View.VISIBLE);
			} else {
				holder.ivStop.setVisibility(View.GONE);
			}
		}

		holder.tvNombreCancion.setText(mHistorial.get(position).getTitle());
		holder.tvHora.setText(mHistorial.get(position).getFecha());

		holder.cardView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//region ENVIAR DATOS A AMPLITUDE

				JSONObject eventProperties = null;
				try {
					eventProperties = new JSONObject().put("CANCION-REPRODUCIDA_NombreCancion", mHistorial.get(position).getTitle()).put("CANCION-REPRODUCIDA_LinkYT", mHistorial.get(position).getLinkYT());
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Amplitude.getInstance().logEvent("CANCION REPRODUCIDA");
				Amplitude.getInstance().setUserProperties(eventProperties);

				//endregion

				//region FIREBASE ANALITYCS

				Bundle params = new Bundle();
				params.putString("NombreCancion", mHistorial.get(position).getTitle());
				params.putString("LinkYT", mHistorial.get(position).getLinkYT());
				mFirebaseAnalytics.logEvent("CancionReproducida", params);

				//endregion

				Toast.makeText(mContext, mHistorial.get(position).getTitle(), Toast.LENGTH_LONG).show();
				ReproducirCancion("historial", mHistorial.get(position).getLinkYT(), mContext, false);
				sp_lista_sonando("historial", mContext);
				sp_numcancion_sonando(position, mContext);

				setLogCat("ADAPTER HISTORIAL", "mHistorial.get(position).getLinkYT() " + mHistorial.get(position).getLinkYT(), LogCatActivo);

				holder.ivStop.setVisibility(View.VISIBLE);

			}
		});

		holder.ivStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent stopIntent = new Intent(mContext, RadioService.class);
				stopIntent.setAction(RadioService.ACTION_STOP);
				PendingIntent stopAction = PendingIntent.getService(mContext, 3, stopIntent, 0);
				mContext.startService(stopIntent);
				holder.ivStop.setVisibility(View.GONE);
			}
		});

	}

	private void EliminarFavorito(int IdFavorito) {

		//login aunth
		// get el usuario
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser u = mAuth.getCurrentUser();

		if (u != null) {

			DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
			DatabaseReference clientesRef = ref.child("lofiradio").child("usuario").child(u.getUid()).child("favoritos").child("canciones").child(String.valueOf(IdFavorito));

			((DatabaseReference) clientesRef).child("IdFavorito").removeValue();
			((DatabaseReference) clientesRef).child("LinkYT").removeValue();
			((DatabaseReference) clientesRef).child("NombreCancionSonando").removeValue();

			Toast.makeText(mContext, mContext.getString(R.string.cancionelimfavoritos), Toast.LENGTH_SHORT).show();

		}
	}

	private void setAnimation(View viewToAnimate, int position) {
		// If the bound view wasn't previously displayed on screen, it's animated
		if (position > lastPosition) {
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.bottom_in);
			viewToAnimate.startAnimation(animation);
			lastPosition = position;
		}
	}

	@Override
	public int getItemCount() {
		return mHistorial.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {

		private final CardView cardView;
		private final ImageView ivStop;
		private final TextView tvNombreCancion;
		public TextView tvHora;

		public MyViewHolder(View itemView) {
			super(itemView);

			tvHora = itemView.findViewById(R.id.tv_hora);
			cardView = (CardView) itemView.findViewById(R.id.cardviewFavorito);
			tvNombreCancion = (TextView) itemView.findViewById(R.id.tv_nombrecancion);
			ivStop = (ImageView) itemView.findViewById(R.id.iv_stop);

		}
	}

}