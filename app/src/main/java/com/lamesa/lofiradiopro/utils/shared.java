package com.lamesa.lofiradiopro.utils;


import static com.lamesa.lofiradiopro.ui.MainActivity.tvCancion;
import static com.lamesa.lofiradiopro.ui.MainActivity.tvLista;
import static com.lamesa.lofiradiopro.utils.metodos.setLogCat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.radio.player.RadioManager;
import com.lamesa.lofiradiopro.radio.player.RadioService;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.naveed.ytextractor.ExtractorException;
import com.naveed.ytextractor.YoutubeStreamExtractor;
import com.naveed.ytextractor.model.YTMedia;
import com.naveed.ytextractor.model.YoutubeMeta;
import com.naveed.ytextractor.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by Luis Mesa on 23/06/2019.
 */
public class shared extends AppCompatActivity {

	public static final String MIXPANEL_TOKEN = "c2cf4aa94fc3499f96f0f2e21a40a9f5";
	public static RadioManager radioManager;
	public static FirebaseAuth mAuth;
	public static FirebaseUser user;
	public static TinyDB tinydbShared;

	public static void sp_autoapagado(boolean Activado, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_AUTOAPAGADO", MODE_PRIVATE).edit();
		editor.putBoolean("activado", Activado);
		editor.apply();
	}

	public static boolean sp_get_autoapagado(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_AUTOAPAGADO", Context.MODE_PRIVATE);
		boolean mActivado = prfs.getBoolean("activado", false);
		return mActivado;
	}

	public static void sp_linkextra_sonando(String Cancion, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_CANCION_SONAR", MODE_PRIVATE).edit();
		editor.putString("cancion", Cancion);
		editor.apply();
	}

	public static String sp_get_linkextra_cancion_sonando(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_CANCION_SONAR", Context.MODE_PRIVATE);
		String mCancionSonando = prfs.getString("cancion", "");
		return mCancionSonando;
	}

	public static void sp_cancion_sonando_nombre(String CancionNombre, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_CANCION_SONAR_NOMBRE", MODE_PRIVATE).edit();
		editor.putString("cancionnombre", CancionNombre);
		editor.apply();
	}


	public static String sp_get_cancion_sonando_nombre(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_CANCION_SONAR_NOMBRE", MODE_PRIVATE);
		String mCancionSonandoNombre = prfs.getString("cancionnombre", "...");
		return mCancionSonandoNombre;
	}


	public static void sp_running_cancion(Boolean Reproduciendo, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_RUNNING_CANCION", MODE_PRIVATE).edit();
		editor.putBoolean("reproduciendo", Reproduciendo);
		editor.apply();
	}


	public static boolean sp_get_running_cancion(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_RUNNING_CANCION", MODE_PRIVATE);
		boolean mCancionSonandoNombre = prfs.getBoolean("reproduciendo", false);
		return mCancionSonandoNombre;
	}


	public static void sp_lista_sonando(String NombreLista, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_LISTA_SONANDO", MODE_PRIVATE).edit();
		editor.putString("nombrelista", NombreLista);
		editor.apply();
	}

	public static String sp_get_lista_sonando(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_LISTA_SONANDO", MODE_PRIVATE);
		String mListaSonando = prfs.getString("nombrelista", "...");
		return mListaSonando;
	}

	public static void sp_linkyoutube_sonando(String LinkYoutube, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_LINKYOUTUBE_SONANDO", MODE_PRIVATE).edit();
		editor.putString("linkyoutube", LinkYoutube);
		editor.apply();
	}

	public static String sp_get_linkyoutube_sonando(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_LINKYOUTUBE_SONANDO", MODE_PRIVATE);
		String mListaSonando = prfs.getString("linkyoutube", "vacio");
		return mListaSonando;
	}

	public static void sp_numcancion_sonando(int NumCancionSonando, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_NUMCANCION_SONANDO", MODE_PRIVATE).edit();
		editor.putInt("numcancionsonando", NumCancionSonando);
		editor.apply();
	}

	public static int sp_get_numcancion_sonando(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_NUMCANCION_SONANDO", MODE_PRIVATE);
		int mListaSonando = prfs.getInt("numcancionsonando", 0);
		return mListaSonando;
	}

	public static void sp_dia_ingreso(int NumDiaingreso, Context mContext) {
		SharedPreferences.Editor editor = mContext.getSharedPreferences("SP_DIA_INGRESO", MODE_PRIVATE).edit();
		editor.putInt("numdiaingreso", NumDiaingreso);
		editor.apply();
	}

	public static int sp_get_dia_ingreso(Context mContext) {
		SharedPreferences prfs = mContext.getSharedPreferences("SP_DIA_INGRESO", MODE_PRIVATE);
		int mDiaIngreso = prfs.getInt("numdiaingreso", 0);
		return mDiaIngreso;
	}

	public static boolean LoginDeUsuario() {
		// NECESARIO PARA COMPROBAR EL INICIO DE SESION DE UN USUARIO
		mAuth = FirebaseAuth.getInstance();
		user = mAuth.getCurrentUser();
		return user != null;
	}

	//region METODO PARA EXTRAR Y REPRODUCIR CANCION
	public static void ReproducirCancion(String nombreLista, String youtubeLink, final Context mContext, Boolean GuardarHistorial) {
		MixpanelAPI mixpanel =
				MixpanelAPI.getInstance(mContext, MIXPANEL_TOKEN);
		tinydbShared = new TinyDB(mContext);
		if (youtubeLink != null
				&& (youtubeLink.contains("://youtu.be/") || youtubeLink.contains("youtube.com/watch?v="))) {
			youtubeLink = youtubeLink;
			// We have a valid link
			tvCancion.setText(mContext.getString(R.string.cargandocancion));
			System.out.println(mContext.getString(R.string.cargandocancion));
			String finalYoutubeLink = youtubeLink;
			new YoutubeStreamExtractor(new YoutubeStreamExtractor.ExtractorListner() {
				@Override
				public void onExtractionDone(List<YTMedia> adativeStream, final List<YTMedia> muxedStream, YoutubeMeta meta) {
					for (YTMedia c : muxedStream) {
						setLogCat("muxedStream", "url : " + c.getUrl() + " format: " + c.getItag(), true);
					}
					for (YTMedia ytFile : adativeStream) {
						//  setLogCat("adativeStream", "url : "+ytFile.getUrl()+" format: "+ytFile.getItag(), true);
						//   if (ytFile.getItag() == 249||ytFile.getItag() == 250 ||ytFile.getItag() == 251 || ytFile.getItag() == 139 || ytFile.getItag() == 140 ||ytFile.getItag() == 141){
						if (ytFile.getItag() == 139 || ytFile.getItag() == 249 || ytFile.getItag() == 250) {
							setLogCat("adativeStream", "url : " + ytFile.getUrl() + " getItag: " + ytFile.getItag(), true);
							setLogCat("YTextrar", "Link extract : " + ytFile.getUrl(), true);
							String song = ytFile.getUrl();
							// guarda la song en un shared
							sp_linkextra_sonando(song, mContext);
							//guardar nombre de cancion en shared
							String TituloCancion = meta.getTitle().replace("letra", "").replace("Letra", "").replace("sub", "").replace("lyrics", "");
							sp_cancion_sonando_nombre(TituloCancion, mContext);
							// SE ENVIA EL TITULO AL TEXTVIEW PRINCIPAL DEL MAIN
							tvCancion.setText(TituloCancion);
							tvLista.setText(mContext.getString(R.string.escuchandodesde) + sp_get_lista_sonando(mContext));
							sp_cancion_sonando_nombre(TituloCancion, mContext);
							sp_linkyoutube_sonando(finalYoutubeLink, mContext);
							//region GUARDAR HISTORIAL
							if (GuardarHistorial) {
								String LinkYoutubeCancion = finalYoutubeLink;
								//region GUARDAR CANCION EN EL EN HISTORIAL
								ArrayList<String> temp_lstCancionesHistorial = tinydbShared.getListString("lstCancionesHistorial");
								temp_lstCancionesHistorial.add(TituloCancion);
								tinydbShared.putListString("lstCancionesHistorial", temp_lstCancionesHistorial);
								for (String x : tinydbShared.getListString("lstCancionesHistorial")) {
									System.out.println("Historial " + x);
								}
								//endregion
								//region GUARDAR HORA DE CANCION EN EL EN HISTORIAL
								Calendar calendar = Calendar.getInstance();
								System.out.println(calendar.getTime());
								SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss "); //SimpleDateFormat mdformat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
								String Hora = mdformat.format(calendar.getTime());
								ArrayList<String> temp_lstCancionesHoraHistorial = tinydbShared.getListString("lstCancionesHoraHistorial");
								temp_lstCancionesHoraHistorial.add(Hora);
								tinydbShared.putListString("lstCancionesHoraHistorial", temp_lstCancionesHoraHistorial);
								for (String x : tinydbShared.getListString("lstCancionesHoraHistorial")) {
									System.out.println("Hora Canción " + x);
								}
								//endregion

								//region GUARDAR LINK DE CANCION EN EL EN HISTORIAL
								ArrayList<String> temp_lstCancionesLinkHistorial = tinydbShared.getListString("lstCancionesLinkHistorial");
								temp_lstCancionesLinkHistorial.add(LinkYoutubeCancion);
								tinydbShared.putListString("lstCancionesLinkHistorial", temp_lstCancionesLinkHistorial);
								for (String x : tinydbShared.getListString("lstCancionesLinkHistorial")) {
									System.out.println("Link Canción " + x);
								}
								//endregion
							}

							//endregion

							if (radioManager != null) {
								radioManager.playOrPause(song);
							}

							Intent serviceIntent = new Intent(mContext, RadioService.class);
							mContext.startService(serviceIntent);
							try {
								JSONObject props = new JSONObject();
								props.put("Cancion", sp_get_cancion_sonando_nombre(mContext));
								props.put("LinkYT", sp_get_linkyoutube_sonando(mContext));
								props.put("Lista", sp_get_lista_sonando(mContext));
								mixpanel.track("CancionReproducida", props);
							} catch (JSONException e) {
								e.printStackTrace();
							}

							mixpanel.getPeople().identify(mixpanel.getDistinctId());
							mixpanel.getPeople().increment("CancionesReproducidas", 1);
							//endregion
							break;

						}

					}
					//Toast.makeText(getApplicationContext(), meta.getTitle(), Toast.LENGTH_LONG).show();
					// Toast.makeText(mContext, meta.getAuthor(), Toast.LENGTH_LONG).show();


					if (adativeStream.isEmpty()) {
						LogUtils.log("null ha");
						setLogCat("adativeStream", "adativeStream is empty", true);

						return;
					}
					if (muxedStream.isEmpty()) {
						LogUtils.log("null ha");
						setLogCat("adativeStream", "muxedStream is empty", true);

						return;
					}
					String url = muxedStream.get(0).getUrl();
					// PlayVideo(url);
				}


				@Override
				public void onExtractionGoesWrong(final ExtractorException e) {

					setLogCat("onExtractionGoesWrong", "Error: " + e.getMessage(), true);
					// Toast.makeText(mContext, "Ocurrió un error, por favor revise su conexion a internet o intente mas tarde", Toast.LENGTH_LONG).show();
					ArrayList<String> temp_lstCanciones = tinydbShared.getListString(sp_get_lista_sonando(mContext));
					Random RandomCancion = new Random();
					if (temp_lstCanciones != null) {
						int numCancion = RandomCancion.nextInt(temp_lstCanciones.size());
						ReproducirCancion(sp_get_lista_sonando(mContext), temp_lstCanciones.get(numCancion), mContext, true);
					}

					//region MIX PANEL
					MixpanelAPI mixpanel =
							MixpanelAPI.getInstance(mContext, MIXPANEL_TOKEN);

					try {
						JSONObject props = new JSONObject();
						props.put("Cancion", sp_get_cancion_sonando_nombre(mContext));
						props.put("LinkYT", sp_get_linkyoutube_sonando(mContext));
						props.put("Lista", sp_get_lista_sonando(mContext));
						props.put("Error", e.getMessage());
						mixpanel.track("ErrorReproduccion", props);
					} catch (JSONException e2) {
						e2.printStackTrace();
					}
					//endregion
				}
			}).useDefaultLogin().Extract(youtubeLink);
		}
	}
	//endregion
}
