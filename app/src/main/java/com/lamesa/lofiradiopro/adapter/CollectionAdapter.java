package com.lamesa.lofiradiopro.adapter;

import static com.lamesa.lofiradiopro.ui.MainActivity.tvLista;
import static com.lamesa.lofiradiopro.utils.metodos.LogCatActivo;
import static com.lamesa.lofiradiopro.utils.metodos.setLogCat;
import static com.lamesa.lofiradiopro.utils.shared.ReproducirCancion;
import static com.lamesa.lofiradiopro.utils.shared.radioManager;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_cancion_sonando_nombre;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_numcancion_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_numcancion_sonando;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.domain.model.coleccion;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.radio.player.RadioManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by Luis Mesa on 22/08/2019.
 */
public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.MyViewHolder> {

	private final Context mContext;
	private final List<coleccion> albumList;
	private final ArrayList<String> lstCancionesHistorial = new ArrayList<>();
	private final ArrayList<String> temp_lstCancionesHistorial2 = new ArrayList<>();
	int[] myImageList = {R.drawable.gradient1, R.drawable.gradient2, R.drawable.gradient3,
			R.drawable.gradient4, R.drawable.gradient5};
	// Allows to remember the last item shown on screen
	private int lastPosition = -1;
	private TinyDB tinydbColletctionAdapter;

	public CollectionAdapter(Context mContext, List<coleccion> albumList) {
		this.mContext = mContext;
		this.albumList = albumList;
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View itemView = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.raw_collection, parent, false);

		radioManager = RadioManager.with(mContext);

		return new MyViewHolder(itemView);
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {
		final coleccion coleccion = albumList.get(position);
		setAnimation(holder.cardview, position);


		holder.tvTitle.setText(coleccion.getTitle());
		holder.tvDescription.setText(coleccion.getDescription());

		// loading album cover using Glide library
		Glide.with(mContext).asGif().load(coleccion.getImage()).diskCacheStrategy(DiskCacheStrategy.DATA).listener(new RequestListener<GifDrawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
				return false;
			}

			@Override
			public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
				resource.setLoopCount(300);
				resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
					@Override
					public void onAnimationEnd(Drawable drawable) {
						//do whatever after specified number of loops complete
					}
				});
				return false;
			}
		}).into(holder.imgThumbnail);

		holder.viewGradient.setBackgroundResource(myImageList[position % myImageList.length]);
		holder.layout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {


			}
		});

		holder.imgThumbnail.setOnClickListener(new View.OnClickListener() {


			@Override
			public void onClick(View view) {


				setLogCat("ADAPTADOR RADIO", "Click radio", LogCatActivo);


				if (radioManager.isPlaying() && sp_get_lista_sonando(mContext).equals(coleccion.getTitle())) {
					Toast.makeText(mContext, mContext.getString(R.string.radioisplaying), Toast.LENGTH_SHORT).show();
					// tinydbColletctionAdapter.putListString("lstCancionesHistorial", lstCancionesHistorial);
				} else {


					JSONObject eventProperties = null;
					try {
						eventProperties = new JSONObject().put("CLICK-RADIO_Radio", coleccion.getTitle());
					} catch (JSONException e) {
						e.printStackTrace();
					}

					Amplitude.getInstance().logEvent("CLICK RADIO");
					Amplitude.getInstance().setUserProperties(eventProperties);


					Toast.makeText(mContext, coleccion.getTitle(), Toast.LENGTH_LONG).show();
					ArrayList<String> temp_lstCanciones = tinydbColletctionAdapter.getListString(coleccion.getTitle());
					Random RandomCancion = new Random();
					int numCancion = RandomCancion.nextInt(temp_lstCanciones.size());

					ReproducirCancion(coleccion.getTitle(), temp_lstCanciones.get(numCancion), mContext, true);
					sp_lista_sonando(coleccion.getTitle(), mContext);
					sp_numcancion_sonando(numCancion, mContext);


					//region ENVIAR DATOS A AMPLITUDE

					String LinkYoutubeCancion = tinydbColletctionAdapter.getListString("YT " + sp_get_lista_sonando(mContext)).get(sp_get_numcancion_sonando(mContext));


					JSONObject eventProperties2 = null;
					try {
						eventProperties2 = new JSONObject().put("CANCION-REPRODUCIDA_NombreCancion", sp_get_cancion_sonando_nombre(mContext)).put("CANCION-REPRODUCIDA_LinkYT", LinkYoutubeCancion);
					} catch (JSONException e) {
						e.printStackTrace();
					}

					Amplitude.getInstance().logEvent("CANCION REPRODUCIDA");
					Amplitude.getInstance().setUserProperties(eventProperties2);


					//endregion


				}


				tvLista.setVisibility(View.VISIBLE);

			}
		});

	}

	private void setAnimation(View viewToAnimate, int position) {
		// If the bound view wasn't previously displayed on screen, it's animated
		if (position > lastPosition) {
			Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.anim_float_window_enter);
			viewToAnimate.startAnimation(animation);
			lastPosition = position;
		}
	}

	@Override
	public int getItemCount() {
		return albumList.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView tvTitle, tvDescription;
		public ImageView imgThumbnail;
		public View viewGradient;
		public CardView cardview;
		RelativeLayout layout;

		public MyViewHolder(View view) {
			super(view);
			cardview = (CardView) view.findViewById(R.id.cardview);
			tvTitle = (TextView) view.findViewById(R.id.tvtitle);
			tvDescription = (TextView) view.findViewById(R.id.tvdesc);
			imgThumbnail = (ImageView) view.findViewById(R.id.imgitem);
			viewGradient = (View) view.findViewById(R.id.viewgradient);
			layout = (RelativeLayout) view.findViewById(R.id.rlmain);
			Amplitude.getInstance().initialize(mContext, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking((Application) mContext.getApplicationContext());
			tinydbColletctionAdapter = new TinyDB(mContext);
		}
	}
}