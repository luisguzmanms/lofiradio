package com.lamesa.lofiradiopro.utils;


import static com.lamesa.lofiradiopro.utils.shared.LoginDeUsuario;
import static com.lamesa.lofiradiopro.utils.shared.MIXPANEL_TOKEN;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_cancion_sonando_nombre;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_linkyoutube_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_lista_sonando;
import static com.lamesa.lofiradiopro.utils.shared.sp_get_numcancion_sonando;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.amplitude.api.Amplitude;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.ixuea.android.downloader.DownloadService;
import com.ixuea.android.downloader.callback.DownloadListener;
import com.ixuea.android.downloader.domain.DownloadInfo;
import com.ixuea.android.downloader.exception.DownloadException;
import com.kongzue.dialog.interfaces.OnDialogButtonClickListener;
import com.kongzue.dialog.util.BaseDialog;
import com.kongzue.dialog.v3.FullScreenDialog;
import com.lamesa.lofiradiopro.BuildConfig;
import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.data.TinyDB;
import com.lamesa.lofiradiopro.ui.Login;
import com.lamesa.lofiradiopro.radio.player.RadioManager;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by Luis Mesa on 23/06/2019.
 */
public class metodos extends AppCompatActivity {


	public static RadioManager radioManager;
	public static Intent intentAiguilleur;
	public static boolean LogCatActivo = true;
	public static CountDownTimer countDownTimer;


	public static void InstagramCreador(Context mContext) {

		String scheme = "http://instagram.com/u/luisguzmanms";
		String path = "https://instagram.com/luisguzmanms";
		String nomPackageInfo = "com.instagram.android";
		try {
			mContext.getPackageManager().getPackageInfo(nomPackageInfo, 0);
			intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(scheme));
		} catch (Exception e) {
			intentAiguilleur = new Intent(Intent.ACTION_VIEW, Uri.parse(path));
		}
		mContext.startActivity(intentAiguilleur);

// Use this link to open directly a picture


	}

	public static void EnviarSugerencia(Context mContext) {


		// Set an EditText view to get user input
		final EditText input_sugerencia = new EditText(mContext);

		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.enviarsugerencia))
				.setMessage(mContext.getString(R.string.mens_enviarsugerencia))
				.setView(input_sugerencia)

				// Specifying a listener allows you to take an action before dismissing the dialog.
				// The dialog is automatically dismissed when a dialog button is clicked.
				.setPositiveButton(mContext.getString(R.string.enviarsugerencia), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						String sugerencia = input_sugerencia.getText().toString();

						if (!sugerencia.isEmpty()) {


							String NombreUsuario = "vacio";
							String CorreoUsuario = "vacio";
							String UIDUsuario = "vacio";


							String Hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
							String Sugerencia = sugerencia;


							FirebaseAuth mAuthUSER = FirebaseAuth.getInstance();
							FirebaseUser u = mAuthUSER.getCurrentUser();

							if (LoginDeUsuario() == true) {
								NombreUsuario = u.getDisplayName();
								CorreoUsuario = u.getEmail();
								UIDUsuario = u.getUid();
							}


							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
							String strDate = "" + mdformat.format(calendar.getTime()) + "--" + Hora;


							DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
							DatabaseReference clientesRef = ref.child("lofiradio").child("ayuda").child("sugerencia").child(strDate);


							((DatabaseReference) clientesRef).child("NombreUsuario").setValue(NombreUsuario);
							((DatabaseReference) clientesRef).child("CorreoUsuario").setValue(CorreoUsuario);
							((DatabaseReference) clientesRef).child("UIDUsuario").setValue(UIDUsuario);
							((DatabaseReference) clientesRef).child("Sugerencia").setValue(Sugerencia);
							((DatabaseReference) clientesRef).child("Hora").setValue(Hora);


							//  Toast.makeText(MainActivity.this, tinydb.getListString("YT " + sp_get_lista_sonando(MainActivity.this)).get(sp_get_numcancion_sonando(MainActivity.this)), Toast.LENGTH_SHORT).show();

							Toast.makeText(mContext, mContext.getString(R.string.sugerenciaenviada), Toast.LENGTH_SHORT).show();

						} else {
							Toast.makeText(mContext, "Error.", Toast.LENGTH_SHORT).show();
						}

					}
				})

				// A null listener allows the button to dismiss the dialog and take no further action.
				.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {


					}
				})
				.show();

	}

	public static void EnviarReporte(Context mContext) {


		// Set an EditText view to get user input
		final EditText input_reporte = new EditText(mContext);

		new AlertDialog.Builder(mContext)
				.setTitle(mContext.getString(R.string.enviarreporte))
				.setMessage(mContext.getString(R.string.mens_enviarreporte))
				.setView(input_reporte)

				// Specifying a listener allows you to take an action before dismissing the dialog.
				// The dialog is automatically dismissed when a dialog button is clicked.
				.setPositiveButton(mContext.getString(R.string.enviarreporte), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

						String reporte = input_reporte.getText().toString();

						if (!reporte.isEmpty()) {

							String NombreUsuario = "vacio";
							String CorreoUsuario = "vacio";
							String UIDUsuario = "vacio";


							String Hora = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
							String Reporte = reporte;

							FirebaseAuth mAuthUSER = FirebaseAuth.getInstance();
							FirebaseUser u = mAuthUSER.getCurrentUser();

							if (LoginDeUsuario() == true) {
								NombreUsuario = u.getDisplayName();
								CorreoUsuario = u.getEmail();
								UIDUsuario = u.getUid();
							}


							Calendar calendar = Calendar.getInstance();
							SimpleDateFormat mdformat = new SimpleDateFormat("dd-MM-yyyy");
							String strDate = "" + mdformat.format(calendar.getTime()) + "--" + Hora;


							DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
							DatabaseReference clientesRef = ref.child("lofiradio").child("ayuda").child("reportebug").child(strDate);


							((DatabaseReference) clientesRef).child("NombreUsuario").setValue(NombreUsuario);
							((DatabaseReference) clientesRef).child("CorreoUsuario").setValue(CorreoUsuario);
							((DatabaseReference) clientesRef).child("UIDUsuario").setValue(UIDUsuario);
							((DatabaseReference) clientesRef).child("Reporte").setValue(reporte);
							((DatabaseReference) clientesRef).child("Hora").setValue(Hora);


							//  Toast.makeText(MainActivity.this, tinydb.getListString("YT " + sp_get_lista_sonando(MainActivity.this)).get(sp_get_numcancion_sonando(MainActivity.this)), Toast.LENGTH_SHORT).show();

							Toast.makeText(mContext, mContext.getString(R.string.reporteenviado), Toast.LENGTH_SHORT).show();

						} else {
							Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
						}

					}
				})

				// A null listener allows the button to dismiss the dialog and take no further action.
				.setNegativeButton(mContext.getString(R.string.cancelar), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {


					}
				})
				.show();

	}

	public static void setLogCat(String TAG, String msg, boolean activo) {

		if (activo) {
			//  System.out.println("setLogCat: " + TAG + " | " + msg);
			Log.i("setLogCat", "setLogCat: " + TAG + " | " + msg);

		}
	}

	public static void EnviarFavorito(Context mContext, TinyDB tinyDB) {

		FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(mContext);

		FirebaseAuth mAuthUSERr;

		mAuthUSERr = FirebaseAuth.getInstance();
		FirebaseUser user = mAuthUSERr.getCurrentUser();

		if (user != null) {


			Random r = new Random();
			int number1 = r.nextInt(9999) + 1;

			Random r2 = new Random();
			int number2 = r2.nextInt(43) + 1;

			int IdFavorito = number1 - number2;

			// aumentar el numero de favoritos en estadisticas


			DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
			DatabaseReference clientesRef = ref.child("lofiradio").child("usuario").child(user.getUid()).child("favoritos");

			String CancionSonandoYT = "vacio";

			if (sp_get_lista_sonando(mContext).equals("favoritos")) {
				Toast.makeText(mContext, mContext.getString(R.string.cancionenfavoritos), Toast.LENGTH_SHORT).show();
			} else {

				if (tinyDB.getListString("YT " + sp_get_lista_sonando(mContext)) != null && tinyDB.getListString("YT " + sp_get_lista_sonando(mContext)).size() != 0) {
					CancionSonandoYT = tinyDB.getListString("YT " + sp_get_lista_sonando(mContext)).get(sp_get_numcancion_sonando(mContext));
				}

			}


			String NombreCancionSonando = sp_get_cancion_sonando_nombre(mContext);


			if (!NombreCancionSonando.equals("...")) {
				((DatabaseReference) clientesRef).child("canciones").child(String.valueOf(IdFavorito)).child("LinkYT").setValue(sp_get_linkyoutube_sonando(mContext));
				((DatabaseReference) clientesRef).child("canciones").child(String.valueOf(IdFavorito)).child("NombreCancionSonando").setValue(NombreCancionSonando);
				((DatabaseReference) clientesRef).child("canciones").child(String.valueOf(IdFavorito)).child("IdFavorito").setValue(String.valueOf(IdFavorito));

				//  Toast.makeText(MainActivity.this, tinydb.getListString("YT " + sp_get_lista_sonando(MainActivity.this)).get(sp_get_numcancion_sonando(MainActivity.this)), Toast.LENGTH_SHORT).show();

				Toast.makeText(mContext, mContext.getString(R.string.cancionagregadafavoritos), Toast.LENGTH_SHORT).show();


				// AMPLITUDE

				JSONObject eventProperties = null;
				try {
					eventProperties = new JSONObject().put("CANCION-FAVORITA_NombreCancion", sp_get_cancion_sonando_nombre(mContext)).put("CANCION-FAVORITA_LinkYT", sp_get_linkyoutube_sonando(mContext));
				} catch (JSONException e) {
					e.printStackTrace();
				}

				Amplitude.getInstance().logEvent("CANCION FAVORITA");
				Amplitude.getInstance().setUserProperties(eventProperties);


				// firebase analitycs

				Bundle params = new Bundle();
				params.putString("NombreCancion", sp_get_cancion_sonando_nombre(mContext));
				params.putString("LinkCancion", CancionSonandoYT);

				//  System.out.println("CancionSonandoYT : : " + CancionSonandoYT);
				//    System.out.println("sp_get_linkextra_cancion_sonando(MainActivity.this) : : " + sp_get_linkextra_cancion_sonando(MainActivity.this));


				mFirebaseAnalytics.logEvent("CancionFavorita", params);


				//region MIX PANEL
				MixpanelAPI mixpanel =
						MixpanelAPI.getInstance(mContext, MIXPANEL_TOKEN);

				try {
					JSONObject props = new JSONObject();
					props.put("Cancion", sp_get_cancion_sonando_nombre(mContext));
					props.put("LinkYT", sp_get_linkyoutube_sonando(mContext));
					props.put("Lista", sp_get_lista_sonando(mContext));
					mixpanel.track("CancionFavorita", props);
				} catch (JSONException e) {
					e.printStackTrace();
				}

				//Identify the user profile that is going to be updated
				mixpanel.alias(user.getUid(), null);

				//Add the color green to the list property "Favorite Colors"
				//A new list property is created if it doesn't already exist
				mixpanel.getPeople().append("CancionesFavoritas", sp_get_cancion_sonando_nombre(mContext));

				//endregion


			} else {
				Toast.makeText(mContext, mContext.getString(R.string.nocancionsonando), Toast.LENGTH_SHORT).show();
			}


			// si funciona marcar con con color rojo,  llamar metodo para que se recargue

			//   comprobarfavorito(); no funciona solo lee el ultimo child

			//  Toast.makeText(detalle.this, getString(R.string.toast_agregadofavorito), Toast.LENGTH_SHORT).show();


		} else {

			Intent intent = new Intent(mContext, Login.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
			mContext.startActivity(intent);
		}

	}

	public static void initFirebase(final Context mContext) {


		final TinyDB tinydbinitFirebase = new TinyDB(mContext);


		try {


			DatabaseReference mref = FirebaseDatabase.getInstance().getReference("lofiradio");
			mref.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {

					try {


						//region ACTUALIZAR APP

						if (dataSnapshot.child("tools").child("actualizacion").exists()) {


							int versionNueva = Integer.parseInt(dataSnapshot.child("tools").child("actualizacion").child("version").getValue().toString());
							Boolean estado = dataSnapshot.child("tools").child("actualizacion").child("estado").getValue(Boolean.class);
							String urlDescarga = dataSnapshot.child("tools").child("actualizacion").child("urlDescarga").getValue(String.class);
							Boolean cancelable = dataSnapshot.child("tools").child("actualizacion").child("cancelable").getValue(Boolean.class);


							int versionActual = BuildConfig.VERSION_CODE;

							if (estado == true && versionNueva > versionActual) {

								try {


									if (mContext != null) {

										//需要先创建Builder
										androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
										builder.setTitle(mContext.getResources().getString(R.string.actualizacion_disponible));
										builder.setCancelable(cancelable);   //每次都需要指定的设置
										builder.setMessage(mContext.getResources().getString(R.string.msg_actualizacion));
										builder.setPositiveButton(mContext.getResources().getString(R.string.actualizar), new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialogInterface, int i) {
												ActualizarApp(mContext, urlDescarga);
											}
										});
										//builder.setNegativeButton("NO", null);

										builder.show();

									}
								} catch (Resources.NotFoundException e) {
									e.printStackTrace();
								}


							} else {
                                    /*
                                    File archivoUpdate = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/update.apk");
                                    if (archivoUpdate.exists()){
                                        archivoUpdate.delete();
                                    }

                                     */

							}


						}

						//endregion

						// Toast.makeText(mContext, "Cambio", Toast.LENGTH_SHORT).show();
					} catch (NumberFormatException e) {
						e.printStackTrace();
					}

				}

				@Override
				public void onCancelled(DatabaseError databaseError) {
					// Toast.makeText(mContext, "ha cambiado", Toast.LENGTH_LONG).show();
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void ActualizarApp(Context mContext, String urlDescarga) {

		com.ixuea.android.downloader.callback.DownloadManager downloadManager = DownloadService.getDownloadManager(mContext.getApplicationContext());

		//call back after permission granted
		PermissionListener permissionlistener = new PermissionListener() {
			private ProgressDialog progressDialog;

			@Override
			public void onPermissionGranted() {

				File toInstall = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)));

				if (!toInstall.exists()) {
					toInstall.mkdirs();
				}


				//create download info set download uri and save path.
				final DownloadInfo downloadInfo = new DownloadInfo.Builder().setUrl(urlDescarga)
						.setPath(toInstall + "/update.apk")
						.build();

//set download callback.
				downloadInfo.setDownloadListener(new DownloadListener() {

					@Override
					public void onStart() {
						progressDialog = new ProgressDialog(mContext);
						progressDialog.setTitle(mContext.getResources().getString(R.string.decargando_actualizacion));
						progressDialog.setMessage(mContext.getResources().getString(R.string.espera_descarga));
						progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						progressDialog.setCancelable(false);
						progressDialog.show();
					}

					@Override
					public void onWaited() {
                        /*
                        tv_download_info.setText("Waiting");
                        bt_download_button.setText("Pause");
                         */
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

					}

					@Override
					public void onPaused() {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}
					}

					@Override
					public void onDownloading(long progress, long size) {
						try {

							long porcentaje = ((progress * 100) / size);


							if (progressDialog.isShowing()) {
								progressDialog.setMessage(String.format("Loading: %d", porcentaje) + " - %100");
							}

						} catch (Exception e) {
							e.printStackTrace();
						}

					}

					@Override
					public void onRemoved() {
						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}


					}

					@Override
					public void onDownloadSuccess() {

						if (progressDialog.isShowing()) {
							progressDialog.dismiss();
						}

						File toInstall = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/update.apk");


						if (toInstall.exists()) {

							Intent install;

							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
								Uri contentUri = FileProvider.getUriForFile(
										mContext,
										BuildConfig.APPLICATION_ID + ".provider",
										new File(String.valueOf(toInstall))
								);
								install = new Intent(Intent.ACTION_VIEW);
								install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
								install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
								install.setData(contentUri);
								mContext.startActivity(install);
								// finish()
							} else {
								install = new Intent(Intent.ACTION_VIEW);
								install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
								install.setDataAndType(
										Uri.parse(String.valueOf(toInstall)),
										"\"application/vnd.android.package-archive\"");
								mContext.startActivity(install);

								// finish()
							}


						}
					}

					@Override
					public void onDownloadFailed(DownloadException e) {
						e.printStackTrace();
						Toast.makeText(mContext, "Download fail:" + e.getMessage(), Toast.LENGTH_LONG).show();
					}
				});

//submit download info to download manager.
				downloadManager.download(downloadInfo);

			}


			@Override
			public void onPermissionDenied(List<String> deniedPermissions) {

				Toast.makeText(mContext, "¡Error!", Toast.LENGTH_SHORT).show();

			}


		};


		TedPermission.with(mContext)
				.setPermissionListener(permissionlistener)
				.setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
				.setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
				.check();


	}

	public static void AboutLofer(Context mContext) {
		OnDialogButtonClickListener nextStepListener = new OnDialogButtonClickListener() {
			@Override
			public boolean onClick(BaseDialog baseDialog, View v) {
				//  Toast.makeText(mContext, "baseDialog "+ v.getId(), Toast.LENGTH_SHORT).show();
				return false;
			}
		};


		FullScreenDialog
				.show(((AppCompatActivity) mContext), R.layout.layout_about_lofer, new FullScreenDialog.OnBindView() {
					@Override
					public void onBind(FullScreenDialog dialog, View rootView) {

						ImageView ivInstagram = rootView.findViewById(R.id.iv_instagram);
						ivInstagram.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rebrand.ly/instacreador"));
									mContext.startActivity(browserIntent);
								} catch (Exception e) {
									//e.toString();
								}
							}
						});

						ImageView ivDonar = rootView.findViewById(R.id.iv_donar);
						ivDonar.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://rebrand.ly/donarlofiradioapp"));
									mContext.startActivity(browserIntent);
								} catch (Exception e) {
									//e.toString();
								}
							}
						});

					}
				})
				.setOkButton("OK", nextStepListener)
				// .setCancelButton("取消")
				.setTitle("About Lofer - Lofi RADIO")
		;
	}

	public static void CompartirApp(Context mContext) {
		try {
			Intent shareIntent = new Intent(Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Lofer - Lofi Radio");
			String shareMessage = mContext.getString(R.string.sharemsg);
			shareMessage = shareMessage + mContext.getString(R.string.paramasinfo) + " https://rebrand.ly/lofi-radio" + "\n\n" + mContext.getString(R.string.verenaptoide) + "https://rebrand.ly/lofiradioapp" + "\n\n";
			shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
			mContext.startActivity(Intent.createChooser(shareIntent, "Share with"));
		} catch (Exception e) {
			//e.toString();
		}
	}

}