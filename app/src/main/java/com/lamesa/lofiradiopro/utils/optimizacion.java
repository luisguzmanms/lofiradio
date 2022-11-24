package com.lamesa.lofiradiopro.utils;


import static com.lamesa.lofiradiopro.ui.MainActivity.linOptimizacion;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.lamesa.lofiradiopro.radio.player.PlaybackStatus;
import com.lamesa.lofiradiopro.radio.player.RadioManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractorOptimizacion;
import at.huber.youtubeExtractor.YtFile;

/**
 * Created by Luis Mesa on 23/06/2019.
 */
public class optimizacion extends AppCompatActivity {

	public static RadioManager radioManager;
	private static String youtubeLink;
	private static String mLinkCancion;

	//region METODO PARA OPTIMIZAR UN LINK

	public static void optimizacionCancion(String mLinkCancion, Context mContext) {

		//region COMPRUEBA
		if (mLinkCancion != null
				&& (mLinkCancion.contains("://youtu.be/") || mLinkCancion.contains("youtube.com/watch?v="))) {
			youtubeLink = mLinkCancion;
			// We have a valid link
			getYoutubeDownloadUrl(youtubeLink, mContext);
			// Toast.makeText(mContext, "El link es correcto", Toast.LENGTH_SHORT).show();
			System.out.println("El link es correcto");
		} else {
			// Toast.makeText(mContext, "Link Incorrecto", Toast.LENGTH_LONG).show();
			System.out.println("Link Incorrecto");
			// finish();
		}
		//endregion
	}

	//region COMPRRUEBA link, lo EXTRAE para la optimizacion
	public static void getYoutubeDownloadUrl(String youtubeLink, final Context mContext) {

		linOptimizacion.setVisibility(View.VISIBLE);
		new YouTubeExtractorOptimizacion(mContext) {
			@Override
			public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
				if (ytFiles == null) {
					return;
				}
				// Iterate over itags
				for (int i = 0, itag; i < ytFiles.size(); i++) {
					itag = ytFiles.keyAt(i);
					// ytFile represents one file with its url and meta data
					YtFile ytFile = ytFiles.get(itag);
					// Just add videos in a decent format => height -1 = audio
					if (ytFile.getFormat().getHeight() == -1) {
						System.out.println("OPTIMI... :: " + ytFile.getUrl());
						linOptimizacion.setVisibility(View.GONE);
						break;
					}
				}
			}
		}.extract(youtubeLink, true, false);
	}
	//endregion

	//region RADIO (IMPORTANTE PARA EL FUNCIONAMIENTO)
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

	@Subscribe
	public void onEvent(String status) {
		switch (status) {
			case PlaybackStatus.LOADING:
				break;
			case PlaybackStatus.ERROR:
				break;
			case PlaybackStatus.IDLE:
				break;
			case PlaybackStatus.STOPPED:
				break;
		}
	}
	//endregion


}
