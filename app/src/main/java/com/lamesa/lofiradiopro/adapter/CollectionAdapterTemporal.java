package com.lamesa.lofiradiopro.adapter;

import static com.lamesa.lofiradiopro.utils.shared.radioManager;

import android.app.Application;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.domain.model.coleccion;
import com.lamesa.lofiradiopro.radio.player.RadioManager;

import java.util.List;


/**
 * Created by Luis Mesa on 22/08/2019.
 */
public class CollectionAdapterTemporal extends RecyclerView.Adapter<CollectionAdapterTemporal.MyViewHolder> {

	private final Context mContext;
	private final List<coleccion> albumList;
	int[] myImageList = {R.drawable.gradient1, R.drawable.gradient2, R.drawable.gradient3,
			R.drawable.gradient4, R.drawable.gradient5};

	public CollectionAdapterTemporal(Context mContext, List<coleccion> albumList) {
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

		holder.tvTitle.setText(coleccion.getTitle());
		holder.tvDescription.setText(coleccion.getDescription());

		// loading album cover using Glide library
		Glide.with(mContext).asGif().load(coleccion.getImage()).listener(new RequestListener<GifDrawable>() {
			@Override
			public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
				return false;
			}

			@Override
			public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
				resource.setLoopCount(50);
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
				Toast.makeText(mContext, coleccion.getTitle(), Toast.LENGTH_SHORT).show();
			}
		});

	}

	@Override
	public int getItemCount() {
		return albumList.size();
	}

	public class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView tvTitle, tvDescription;
		public ImageView imgThumbnail;
		public View viewGradient;
		RelativeLayout layout;

		public MyViewHolder(View view) {
			super(view);
			tvTitle = (TextView) view.findViewById(R.id.tvtitle);
			tvDescription = (TextView) view.findViewById(R.id.tvdesc);
			imgThumbnail = (ImageView) view.findViewById(R.id.imgitem);
			viewGradient = (View) view.findViewById(R.id.viewgradient);
			layout = (RelativeLayout) view.findViewById(R.id.rlmain);
			Amplitude.getInstance().initialize(mContext, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking((Application) mContext.getApplicationContext());

		}
	}
}