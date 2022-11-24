package com.lamesa.lofiradiopro.ui;

import static com.lamesa.lofiradiopro.app.LofiApp.mixpanel;
import static com.lamesa.lofiradiopro.utils.metodos.AboutLofer;
import static com.lamesa.lofiradiopro.utils.metodos.CompartirApp;
import static com.lamesa.lofiradiopro.utils.metodos.EnviarFavorito;
import static com.lamesa.lofiradiopro.utils.metodos.EnviarReporte;
import static com.lamesa.lofiradiopro.utils.metodos.EnviarSugerencia;
import static com.lamesa.lofiradiopro.utils.metodos.LogCatActivo;
import static com.lamesa.lofiradiopro.utils.metodos.initFirebase;
import static com.lamesa.lofiradiopro.utils.metodos.setLogCat;
import static com.lamesa.lofiradiopro.utils.optimizacion.optimizacionCancion;
import static com.lamesa.lofiradiopro.utils.shared.LoginDeUsuario;
import static com.lamesa.lofiradiopro.utils.shared.MIXPANEL_TOKEN;
import static com.lamesa.lofiradiopro.utils.shared.mAuth;
import static com.lamesa.lofiradiopro.utils.shared.sp_autoapagado;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_autoapagado;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_cancion_sonando_nombre;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_linkyoutube_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_numcancion_sonando;
import static com.lamesa.lofiradiopro.utils.shared.user;
import static com.lamesa.lofiradiopro.radio.player.RadioService.ApagarAutoApagado;
import static com.lamesa.lofiradiopro.radio.player.RadioService.EncenderAutoApagado;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.amplitude.api.Amplitude;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.eightbitlab.bottomnavigationbar.BottomBarItem;
import com.eightbitlab.bottomnavigationbar.BottomNavigationBar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.adapter.CollectionAdapter;
import com.lamesa.lofiradiopro.adapter.CollectionAdapterTemporal;
import com.lamesa.lofiradiopro.adapter.TagAdapter;
import com.lamesa.lofiradiopro.domain.model.coleccion;
import com.lamesa.lofiradiopro.domain.model.tag;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.radio.player.PlaybackStatus;
import com.lamesa.lofiradiopro.radio.player.RadioManager;
import com.lamesa.lofiradiopro.radio.player.RadioService;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


/**
 * Created by Luis Mesa on 03/06/2019.
 */

public class MainActivity extends AppCompatActivity {

	private static final String TAG = "TAG";
	public static TextView tvCancion;
	public static LinearLayout linOptimizacion;
	public static TextView tvLista;
	public static LinearLayout lin_opciones;
	public static ImageView ivSleep;
	public static TextView tvMinutosfaltantes;
	ImageView imgProfile;
	RadioManager radioManager;
	String streamURL;
	private final Context mContext = MainActivity.this;
	private ArrayList<coleccion> coleccionList;
	private RecyclerView recyclerViewCollection;
	private CollectionAdapter collectionAdapter;
	private ArrayList<tag> tagList;
	private RecyclerView recyclerViewTag;
	private TagAdapter tagbAdapter;
	private final Integer[] image1 = {R.drawable.gradienttag1, R.drawable.gradienttag2, R.drawable.gradienttag3, R.drawable.gradienttag4, R.drawable.gradienttag5};
	private final String[] title1 = {"Jackets", "Sunglasses", "Kurtas", "Footwear", "Backpacks"};
	private final String[] description1 = {"Fast Food Eat", "Chicken Restaurant", "US Pizza", "Dominoz", "Dominoz"};
	private ArrayList<coleccion> offerList;
	private RecyclerView recyclerViewOffer;
	private CollectionAdapterTemporal offerAdapter;
	private final Integer[] image2 = {R.drawable.prueba, R.drawable.prueba, R.drawable.prueba, R.drawable.prueba, R.drawable.prueba};
	private String youtubeLink;
	private TableLayout mainLayout;
	private String mLinkCancion;
	private ImageView ivGif;
	private ArrayList<String> lstFavoritos;
	private ImageView ivDetener;
	private ImageView ivLike;
	private ImageView ivDislike;
	private Dialog slideDialog;
	private LinearLayout lin_ReiniciarRadio;
	private TextView tvNombreCuenta;
	private FirebaseAnalytics mFirebaseAnalytics;
	private ImageView ivFavoritos;
	private ImageView ivMenu;
	private TinyDB tinydbMainActivity;
	private ArrayList<String> temp_lstNombreListas;
	private ArrayList<String> temp_lstGifs;
	private ArrayList<String> temp_lstCanciones;
	private ArrayList<String> temp_lstCancionesHistorial;
	private RecyclerView recyclerViewParati;
	private List<coleccion> coleccionListParati;
	private CollectionAdapter collectionAdapterParati;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFirebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
		Amplitude.getInstance().initialize(this, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking(getApplication());
		// funcion de la radio PARA QUE FUNCIONE
		radioManager = RadioManager.with(this);
		tinydbMainActivity = new TinyDB(MainActivity.this);
		// definir de nuevo las temporales en MainActivity, traer las estaticas desde splash crea un error nulo
		temp_lstNombreListas = tinydbMainActivity.getListString("lstNombreListas");
		temp_lstGifs = tinydbMainActivity.getListString("lstGifs");
		temp_lstCancionesHistorial = tinydbMainActivity.getListString("lstCancionesHistorial");


		vistas();
		ComprobarLogin();
		ListasRecycler();
		BottomNavigation();
		initFirebase(MainActivity.this);

	}

	private void ListasRecycler() {

		//region RECYCLER PARA TI
		recyclerViewParati = findViewById(R.id.RecyclerViewParaTi);

		RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
		recyclerViewParati.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewParati.setItemAnimator(new DefaultItemAnimator());

		coleccionListParati = new ArrayList<>();

		ArrayList<String> ListaParati = new ArrayList();
		ListaParati.add("Random");
		ListaParati.add("Sleepy");
		ListaParati.add("Study-Work");
		ListaParati.add("Sad");


		for (String Lista : ListaParati) {
			Random GifRandom = new Random();
			int numGifRandom = GifRandom.nextInt(temp_lstGifs.size());
			coleccion Collecion = new coleccion(Lista, "...", temp_lstGifs.get(numGifRandom));
			coleccionListParati.add(Collecion);
			System.out.println("ListaParati  : : : " + Lista);
		}
		collectionAdapterParati = new CollectionAdapter(mContext, coleccionListParati);
		recyclerViewParati.setAdapter(collectionAdapterParati);
		//endregion


		//region RECYCLER RADIOS
		recyclerViewCollection = findViewById(R.id.RecyclerViewCollection);

		RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(mContext);
		recyclerViewCollection.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewCollection.setItemAnimator(new DefaultItemAnimator());
		coleccionList = new ArrayList<>();

		// orden aleatorio
		Collections.shuffle(temp_lstNombreListas);
		for (int i = 0; i < temp_lstNombreListas.size(); i++) {
			Random GifRandom = new Random();
			int numGifRandom = GifRandom.nextInt(temp_lstGifs.size());
			coleccion coleccion = new coleccion(temp_lstNombreListas.get(i), "descripcion", temp_lstGifs.get(numGifRandom));
			coleccionList.add(coleccion);
		}
		collectionAdapter = new CollectionAdapter(mContext, coleccionList);
		recyclerViewCollection.setAdapter(collectionAdapter);
		//endregion

		recyclerViewOffer = findViewById(R.id.RecyclerViewOffers);
		RecyclerView.LayoutManager layoutManager3 = new LinearLayoutManager(mContext);
		recyclerViewOffer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
		recyclerViewOffer.setItemAnimator(new DefaultItemAnimator());
		offerList = new ArrayList<>();
		String[] pronto = {getString(R.string.offer1), getString(R.string.offer2), getString(R.string.offer3)};

		for (int i = 0; i < pronto.length; i++) {
			coleccion coleccion = new coleccion(pronto[i], "...", "...");
			offerList.add(coleccion);
		}
		offerAdapter = new CollectionAdapterTemporal(mContext, offerList);
		recyclerViewOffer.setAdapter(offerAdapter);
	}
	private void DialogoSesion() {

		String correo = "";
		String nombre = "";
		if (LoginDeUsuario() == true) {
			correo = user.getEmail();
			nombre = user.getDisplayName();
		}

		// Create Alert using Builder
		CFAlertDialog.Builder builder = new CFAlertDialog.Builder(this)
				.setDialogStyle(CFAlertDialog.CFAlertStyle.ALERT)

				.setTitle(R.string.cerrarsesion)
				.setMessage("Name: " + nombre + "\n" +
						"Email: " + correo)
				.addButton(getString(R.string.iniciarotracuenta), -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
					mAuth.signOut();
					startActivity(new Intent(MainActivity.this, Login.class));
					dialog.dismiss();
				})
				.addButton(getString(R.string.cancelar), -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
					dialog.dismiss();
				});

		// Show the alert
		builder.show();

	}
	private void DialogoMenu() {

		// Create Alert using Builder
		CFAlertDialog.Builder builder2 = new CFAlertDialog.Builder(this)
				.setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
				.addButton(getString(R.string.enviarsugerencia), -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
					EnviarSugerencia(MainActivity.this);
					dialog.dismiss();
				})
				.addButton(getString(R.string.reportarbug), -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
					EnviarReporte(MainActivity.this);
					dialog.dismiss();
				})
				.addButton(getString(R.string.compartirapp), -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
					CompartirApp(MainActivity.this);
					dialog.dismiss();
				})
				.addButton(getString(R.string.sobrelofiradio), -1, -1, CFAlertDialog.CFAlertActionStyle.DEFAULT, CFAlertDialog.CFAlertActionAlignment.JUSTIFIED, (dialog, which) -> {
					AboutLofer(MainActivity.this);
					dialog.dismiss();
				});

		// Show the alert
		builder2.show();
	}

	//region RADIO (IMPORTANTE PARA EL FUNCIONAMIENTO, DEBE IR EN LA ACTIVIDAD EN DONDE RE REPRODUSCA UNA CANCION)
	private void vistas() {

		tvCancion = findViewById(R.id.tvcancion);
		tvCancion.setText(sp_get_cancion_sonando_nombre(MainActivity.this));
		linOptimizacion = findViewById(R.id.lin_optimizacion);
		lin_opciones = findViewById(R.id.lin_opciones);
		tvMinutosfaltantes = findViewById(R.id.tv_minutosfaltantes);
		ivSleep = findViewById(R.id.ivsleep);
		ivSleep.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogoTemporizador(MainActivity.this);
			}
		});

		if (sp_get_autoapagado(MainActivity.this) == true) {
			ivSleep.setImageResource(R.drawable.ic_moon_on);
			tvMinutosfaltantes.setVisibility(View.VISIBLE);
		} else {
			ivSleep.setImageResource(R.drawable.ic_moon_off);
			tvMinutosfaltantes.setVisibility(View.GONE);
		}

		ivMenu = findViewById(R.id.ivmenu);
		ivMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogoMenu();
			}
		});

		ivFavoritos = findViewById(R.id.ivfavoritos);
		ivFavoritos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LoginDeUsuario() == true) {
					startActivity(new Intent(MainActivity.this, Favoritos.class));
				} else {
					startActivity(new Intent(MainActivity.this, Login.class));
				}

			}
		});

		lin_ReiniciarRadio = findViewById(R.id.lin_reiniciarRadio);
		lin_ReiniciarRadio.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent stopIntent = new Intent(MainActivity.this, RadioService.class);
				stopIntent.setAction(RadioService.ACTION_STOP);
				PendingIntent stopAction = PendingIntent.getService(MainActivity.this, 3, stopIntent, 0);
				startService(stopIntent);
				Toast.makeText(mContext, getString(R.string.radioreiniciada), Toast.LENGTH_SHORT).show();
				doRestart(MainActivity.this);

			}
		});
		ivDetener = findViewById(R.id.iv_detener);
		ivDetener.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent stopIntent = new Intent(MainActivity.this, RadioService.class);
				stopIntent.setAction(RadioService.ACTION_STOP);
				PendingIntent stopAction = PendingIntent.getService(MainActivity.this, 3, stopIntent, 0);
				startService(stopIntent);

				Toast.makeText(mContext, getString(R.string.detenido), Toast.LENGTH_SHORT).show();


			}
		});
		ivLike = findViewById(R.id.iv_like);
		// AGREGAR CANCION A FAVORITOS DEPENDIENDO DE EL NUMERO DE LA CANCION QUE ESTA SONANDO | agregar a amplitude
		ivLike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				EnviarFavorito(MainActivity.this, tinydbMainActivity);

				//Toast.makeText(MainActivity.this, String.valueOf(sp_get_numcancion_sonando(MainActivity.this)), Toast.LENGTH_SHORT).show();
			}
		});
		// AGREGAR CANCION QUE ESTA SONANDO SEGUN EL NUMERO A AMPLITUDE
		ivDislike = findViewById(R.id.iv_dislike);
		ivDislike.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (radioManager != null) {
					radioManager.playOrPause("http://116.203.132.26/gf/o12/QCJGVdCGPqfJnm8Lt1yYnA,1606758884/yt:YpcU9DmzYjk-1/there%20is%20hope%20for%20me%20and%20you.mp3");
				}

				Intent serviceIntent = new Intent(mContext, RadioService.class);
				mContext.startService(serviceIntent);
				//EnviarDislike();
				Toast.makeText(mContext, getString(R.string.nogustocancion), Toast.LENGTH_SHORT).show();
				//Toast.makeText(MainActivity.this, tinydb.getListString("YT " + sp_get_lista_sonando(MainActivity.this)).get(sp_get_numcancion_sonando(MainActivity.this)), Toast.LENGTH_SHORT).show();
			}
		});

		ivGif = findViewById(R.id.iv_gif);
		ivGif.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


			}
		});

		imgProfile = findViewById(R.id.imgprofile);
		tvNombreCuenta = findViewById(R.id.tv_nombrecuenta);


		tvLista = findViewById(R.id.tvaddress);
		Shader myShader = new LinearGradient(
				100, 0, 20, 100,
				Color.parseColor("#FF5733"), Color.parseColor("#7B68EE"),
				Shader.TileMode.CLAMP);
		tvLista.getPaint().setShader(myShader);
		if (sp_get_lista_sonando(MainActivity.this).equals("...")) {
			tvLista.setVisibility(View.VISIBLE);
		} else {
			tvLista.setText(getString(R.string.escuchandodesde) + sp_get_lista_sonando(MainActivity.this));
		}


		Random randomGif = new Random();
		int numRandomGif = randomGif.nextInt(temp_lstGifs.size());


		// gif de encabezado
		Glide.with(this).asGif().load(temp_lstGifs.get(numRandomGif)).diskCacheStrategy(DiskCacheStrategy.RESOURCE).listener(new RequestListener<GifDrawable>() {
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
		}).into(ivGif);


		/// pruebas reproduciion y conversion de cancion

		tvCancion.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				Toast.makeText(MainActivity.this, tvCancion.getText(), Toast.LENGTH_SHORT).show();

			}
		});

		tvLista.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				Toast.makeText(MainActivity.this, sp_get_lista_sonando(MainActivity.this), Toast.LENGTH_SHORT).show();

			}
		});


	}

	private void ComprobarLogin() {

		if (LoginDeUsuario() == true) {

			JSONObject eventProperties2 = null;
			try {
				eventProperties2 = new JSONObject().put("USUARIO_Nombre", user.getDisplayName()).put("USUARIO_Correo", user.getEmail()).put("USUARIO_Imagen", user.getPhotoUrl()).put("USUARIO_UID", user.getUid());
			} catch (JSONException e) {
				e.printStackTrace();
			}

			Amplitude.getInstance().logEvent("USUARIO");
			Amplitude.getInstance().setUserProperties(eventProperties2);


			tvNombreCuenta.setText(user.getDisplayName());
			tvNombreCuenta.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogoSesion();
				}
			});


			Glide.with(mContext)
					.load(user.getPhotoUrl())
					.apply(RequestOptions.circleCropTransform())
					.into(imgProfile);


			imgProfile.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogoSesion();
				}
			});

		} else {

			Glide.with(mContext)
					.load(R.drawable.icon_app)
					.apply(RequestOptions.circleCropTransform())
					.into(imgProfile);

			imgProfile.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, Login.class));
				}
			});
			tvNombreCuenta.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(MainActivity.this, Login.class));
				}
			});


		}

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
		// radioManager.unbind();
		sp_autoapagado(false, mContext);
		if (mixpanel != null) {
			mixpanel.flush();
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		radioManager.bind();
	}

	//endregion

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
	private void OptimizacionCanciones() {
		Random optRandom = new Random();
		int numRandom = optRandom.nextInt(30);
		if (numRandom == 5) {
			if (temp_lstNombreListas != null) {
				for (String NombreLista : temp_lstNombreListas) {
					if (tinydbMainActivity.getListString(NombreLista) != null) {
						temp_lstCanciones = tinydbMainActivity.getListString(NombreLista);
						for (String Canciones : temp_lstCanciones) {
							optimizacionCancion(Canciones, MainActivity.this);
							//   System.out.println("PARA OPTIMIZAR: : : : " + Canciones);
							setLogCat("MainActivity.class", "Para optimizar  : : : " + Canciones, LogCatActivo);
						}
					}
				}
			}
		}

		private void EnviarDislike () {

			if (!sp_get_lista_sonando(MainActivity.this).equals("...")) {
				String CancionSonandoYT = "Random";
				if (!sp_get_lista_sonando(MainActivity.this).equals("favoritos")) {
					CancionSonandoYT = tinydbMainActivity.getListString("YT " + sp_get_lista_sonando(MainActivity.this)).get(sp_get_numcancion_sonando(MainActivity.this));
				} else {
					CancionSonandoYT = tinydbMainActivity.getListString("favoritos").get(sp_get_numcancion_sonando(MainActivity.this));
				}
				String NombreCancionSonando = sp_get_cancion_sonando_nombre(MainActivity.this);
				// AMPLITUDE
				JSONObject eventProperties = null;
				try {
					eventProperties = new JSONObject().put("CANCION-DISLIKE_NombreCancion", sp_get_cancion_sonando_nombre(MainActivity.this)).put("CANCION-DISLIKE_LinkYT", sp_get_linkyoutube_sonando(mContext));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Amplitude.getInstance().logEvent("CANCION DISLIKE");
				Amplitude.getInstance().setUserProperties(eventProperties);
				// firebase analitycs
				Bundle params = new Bundle();
				params.putString("NombreCancion", sp_get_cancion_sonando_nombre(MainActivity.this));
				params.putString("LinkCancion", CancionSonandoYT);
				mFirebaseAnalytics.logEvent("CancionDislike", params);

				//region MIX PANEL
				MixpanelAPI mixpanel =
						MixpanelAPI.getInstance(mContext, MIXPANEL_TOKEN);

				try {
					JSONObject props = new JSONObject();
					props.put("Cancion", sp_get_cancion_sonando_nombre(mContext));
					props.put("LinkYT", sp_get_linkyoutube_sonando(mContext));
					props.put("Lista", sp_get_lista_sonando(mContext));
					mixpanel.track("CancionDislike", props);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//endregion
			}
		}

		private void BottomNavigation () {

			BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_bar);
			BottomBarItem item_inicio = new BottomBarItem(R.drawable.ic_home_icon_silhouette, R.string.inicio);
			BottomBarItem item_historial = new BottomBarItem(R.drawable.ic_history_clock_button, R.string.historial);
			BottomBarItem item_favoritos = new BottomBarItem(R.drawable.ic_favorite_heart_button, R.string.favoritos);
			bottomNavigationBar.addTab(item_inicio);
			bottomNavigationBar.addTab(item_historial);
			bottomNavigationBar.addTab(item_favoritos);

			bottomNavigationBar.setOnSelectListener(new BottomNavigationBar.OnSelectListener() {
				@Override
				public void onSelect(int position) {
					//  Toast.makeText(mContext, String.valueOf(position), Toast.LENGTH_SHORT).show();
					switch (position) {
						case 0:
							startActivity(new Intent(MainActivity.this, MainActivity.class));
							finish();
							break;
						case 1:
							startActivity(new Intent(MainActivity.this, Historial.class));
							Toast.makeText(mContext, getString(R.string.historial), Toast.LENGTH_SHORT).show();
							break;
						case 2:
							Toast.makeText(mContext, getString(R.string.favoritos), Toast.LENGTH_SHORT).show();
							if (LoginDeUsuario() == true) {
								startActivity(new Intent(MainActivity.this, Favoritos.class));
							} else {
								startActivity(new Intent(MainActivity.this, Login.class));
							}
							break;
					}

				}
			});
			bottomNavigationBar.setOnReselectListener(new BottomNavigationBar.OnReselectListener() {
				@Override
				public void onReselect(int position) {
					switch (position) {
						case 0:
							startActivity(new Intent(MainActivity.this, MainActivity.class));
							finish();
							break;
						case 1:
							startActivity(new Intent(MainActivity.this, Historial.class));
							Toast.makeText(mContext, "Historial", Toast.LENGTH_SHORT).show();
							break;
						case 2:
							Toast.makeText(mContext, "Favoritos", Toast.LENGTH_SHORT).show();
							if (LoginDeUsuario() == true) {
								startActivity(new Intent(MainActivity.this, Favoritos.class));
							} else {
								startActivity(new Intent(MainActivity.this, Login.class));
							}
							break;
					}
				}
			});
		}
	}

	public void DialogoTemporizador(Context mContext) {

		//region DIALOGO
		final AlertDialog.Builder d = new AlertDialog.Builder(mContext);
		LayoutInflater inflater = getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.number_picker_dialog, null);
		d.setTitle(R.string.autoapagado);
		d.setMessage(R.string.minutosparaapagar);
		d.setView(dialogView);
		final NumberPicker numberPicker = (NumberPicker) dialogView.findViewById(R.id.dialog_number_picker);
		numberPicker.setMaxValue(60);
		numberPicker.setMinValue(1);
		numberPicker.setValue(15);
		numberPicker.setWrapSelectorWheel(false);
		numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker numberPicker, int i, int i1) {
				Log.d("Dialogo alerta", "onValueChange: ");
			}
		});
		d.setPositiveButton(getString(R.string.encendercontador), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				Log.d("Dialogo", "onClick: " + numberPicker.getValue());
				Toast.makeText(mContext, "onClick: " + numberPicker.getValue(), Toast.LENGTH_SHORT).show();
				String minutos = String.valueOf(numberPicker.getValue());

				if (minutos.isEmpty() || minutos.equals("0")) {
					Toast.makeText(mContext, mContext.getString(R.string.ingresarnumero), Toast.LENGTH_SHORT).show();
				} else {
					ApagarAutoApagado(mContext);
					EncenderAutoApagado(Integer.parseInt(minutos), mContext);
					Toast.makeText(mContext, mContext.getString(R.string.sedetendraen) + minutos + mContext.getString(R.string.minutos), Toast.LENGTH_SHORT).show();
				}
			}
		});
		d.setNegativeButton(getString(R.string.apagarcontador), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				ApagarAutoApagado(mContext);
			}
		});
		AlertDialog alertDialog = d.create();
		alertDialog.show();
	}


		public static void doRestart(Context c) {
			try {
				//check if the context is given
				if (c != null) {
					//fetch the packagemanager so we can get the default launch activity
					// (you can replace this intent with any other activity if you want
					PackageManager pm = c.getPackageManager();
					//check if we got the PackageManager
					if (pm != null) {
						//create the intent with the default start activity for your application
						Intent mStartActivity = pm.getLaunchIntentForPackage(
								c.getPackageName()
						);
						if (mStartActivity != null) {
							mStartActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							//create a pending intent so the application is restarted after System.exit(0) was called.
							// We use an AlarmManager to call this intent in 100ms
							int mPendingIntentId = 223344;
							PendingIntent mPendingIntent = PendingIntent
									.getActivity(c, mPendingIntentId, mStartActivity,
											PendingIntent.FLAG_CANCEL_CURRENT);
							AlarmManager mgr = (AlarmManager) c.getSystemService(Context.ALARM_SERVICE);
							mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
							//kill the application
							System.exit(0);
						} else {
							Log.e(TAG, "Was not able to restart application, mStartActivity null");
						}
					} else {
						Log.e(TAG, "Was not able to restart application, PM null");
					}
				} else {
					Log.e(TAG, "Was not able to restart application, Context null");
				}
			} catch (Exception ex) {
				Log.e(TAG, "Was not able to restart application");
			}

		}

}










