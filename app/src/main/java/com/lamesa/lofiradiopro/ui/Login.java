package com.lamesa.lofiradiopro.ui;

import static com.lamesa.lofiradiopro.utils.metodos.InstagramCreador;
import static com.lamesa.lofiradiopro.utils.shared.MIXPANEL_TOKEN;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.amplitude.api.Amplitude;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lamesa.lofiradiopro.BuildConfig;
import com.lamesa.lofiradiopro.R;
import com.mixpanel.android.mpmetrics.MixpanelAPI;

import java.util.Locale;

/**
 * Created by Luis Mesa on 05/07/2019.
 */

public class Login extends AppCompatActivity {

	// firebase auth para el inicio de sesion
	private static final String TAG = "_MAIN_";
	private static final int RC_SIGN_IN = 9001;
	private FirebaseAuth.AuthStateListener mAuthListener;
	private FirebaseAuth mAuth;
	private GoogleApiClient mGoogleApiClient;
	private Button btnGoogleLogin;
	private DatabaseReference clientesRef;
	private DatabaseReference ref;
	private CheckBox checkPoliticas;
	private TextView tvCreador;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		Amplitude.getInstance().initialize(this, "d261f53264579f9554bd244eef7cc2e1").enableForegroundTracking(getApplication());


		// iniciar los procesos de identificar si ya esta logeado y el proceso de google para el servicio de logeo
		mAuth = FirebaseAuth.getInstance();
		initFirebaseAuth();
		initGoogleLogin();


		tvCreador = (TextView) findViewById(R.id.tvCreador);
		tvCreador.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InstagramCreador(Login.this);
			}
		});
		btnGoogleLogin = (Button) findViewById(R.id.btn_googlelogin);
		checkPoliticas = (CheckBox) findViewById(R.id.check_politicas);
		checkPoliticas.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				String idioma = Locale.getDefault().getLanguage(); // es

				if (idioma == "es") {
					new AlertDialog.Builder(Login.this)
							.setTitle("Políticas de Privacidad")
							.setMessage("El presente Política de Privacidad establece los términos en que Lo-fi RADIO pro usa y protege la información que es proporcionada por sus usuarios al momento de utilizar su sitio web. Esta compañía está comprometida con la seguridad de los datos de sus usuarios. Cuando le pedimos llenar los campos de información personal con la cual usted pueda ser identificado, lo hacemos asegurando que sólo se empleará de acuerdo con los términos de este documento. Sin embargo esta Política de Privacidad puede cambiar con el tiempo o ser actualizada por lo que le recomendamos y enfatizamos revisar continuamente esta página para asegurarse que está de acuerdo con dichos cambios.\n" +
									"\n" +
									"Información que es recogida\n" +
									"\n" +
									"Nuestro sitio web podrá recoger información personal por ejemplo: Nombre,  información de contacto como  su dirección de correo electrónica e información demográfica. Así mismo cuando sea necesario podrá ser requerida información específica para procesar algún pedido o realizar una entrega o facturación.\n" +
									"\n" +
									"Uso de la información recogida\n" +
									"\n" +
									"Nuestro sitio web emplea la información con el fin de proporcionar el mejor servicio posible, particularmente para mantener un registro de usuarios, de pedidos en caso que aplique, y mejorar nuestros productos y servicios.  Es posible que sean enviados correos electrónicos periódicamente a través de nuestro sitio con ofertas especiales, nuevos productos y otra información publicitaria que consideremos relevante para usted o que pueda brindarle algún beneficio, estos correos electrónicos serán enviados a la dirección que usted proporcione y podrán ser cancelados en cualquier momento.\n" +
									"\n" +
									"Lo-fi RADIO pro está altamente comprometido para cumplir con el compromiso de mantener su información segura. Usamos los sistemas más avanzados y los actualizamos constantemente para asegurarnos que no exista ningún acceso no autorizado.\n" +
									"\n" +
									"Cookies\n" +
									"\n" +
									"Una cookie se refiere a un fichero que es enviado con la finalidad de solicitar permiso para almacenarse en su ordenador, al aceptar dicho fichero se crea y la cookie sirve entonces para tener información respecto al tráfico web, y también facilita las futuras visitas a una web recurrente. Otra función que tienen las cookies es que con ellas las web pueden reconocerte individualmente y por tanto brindarte el mejor servicio personalizado de su web.\n" +
									"\n" +
									"Nuestro sitio web emplea las cookies para poder identificar las páginas que son visitadas y su frecuencia. Esta información es empleada únicamente para análisis estadístico y después la información se elimina de forma permanente. Usted puede eliminar las cookies en cualquier momento desde su ordenador. Sin embargo las cookies ayudan a proporcionar un mejor servicio de los sitios web, estás no dan acceso a información de su ordenador ni de usted, a menos de que usted así lo quiera y la proporcione directamente, visitas a una web . Usted puede aceptar o negar el uso de cookies, sin embargo la mayoría de navegadores aceptan cookies automáticamente pues sirve para tener un mejor servicio web. También usted puede cambiar la configuración de su ordenador para declinar las cookies. Si se declinan es posible que no pueda utilizar algunos de nuestros servicios.\n" +
									"\n" +
									"Enlaces a Terceros\n" +
									"\n" +
									"Este sitio web pudiera contener en laces a otros sitios que pudieran ser de su interés. Una vez que usted de clic en estos enlaces y abandone nuestra página, ya no tenemos control sobre al sitio al que es redirigido y por lo tanto no somos responsables de los términos o privacidad ni de la protección de sus datos en esos otros sitios terceros. Dichos sitios están sujetos a sus propias políticas de privacidad por lo cual es recomendable que los consulte para confirmar que usted está de acuerdo con estas.\n" +
									"\n" +
									"Control de su información personal\n" +
									"\n" +
									"En cualquier momento usted puede restringir la recopilación o el uso de la información personal que es proporcionada a nuestro sitio web.  Cada vez que se le solicite rellenar un formulario, como el de alta de usuario, puede marcar o desmarcar la opción de recibir información por correo electrónico.  En caso de que haya marcado la opción de recibir nuestro boletín o publicidad usted puede cancelarla en cualquier momento.\n" +
									"\n" +
									"Esta compañía no venderá, cederá ni distribuirá la información personal que es recopilada sin su consentimiento, salvo que sea requerido por un juez con un orden judicial.\n" +
									"\n" +
									"Lo-fi RADIO pro Se reserva el derecho de cambiar los términos de la presente Política de Privacidad en cualquier momento.")

							// Specifying a listener allows you to take an action before dismissing the dialog.
							// The dialog is automatically dismissed when a dialog button is clicked.
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									checkPoliticas.setChecked(true);
								}
							})

							// A null listener allows the button to dismiss the dialog and take no further action.
							.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									checkPoliticas.setChecked(false);
								}
							})
							.setIcon(R.drawable.ic_close_black_24dp)
							.show();

				} else {
					new AlertDialog.Builder(Login.this)
							.setTitle("Privacy Policy")
							.setMessage("This Privacy Policy establishes the terms in which Lo-fi RADIO pro uses and protects the information that is provided by its users when using its website. This company is committed to the security of its users' data. When we ask you to fill in the personal information fields with which you can be identified, we do so by ensuring that it will only be used in accordance with the terms of this document. However, this Privacy Policy may change over time or be updated so we recommend and emphasize continually reviewing this page to ensure you agree with those changes.\n" +
									"\n" +
									"Information that is collected\n" +
									"\n" +
									"Our website may collect personal information such as: Name, contact information such as your email address and demographic information. Likewise, when necessary, specific information may be required to process an order or make a delivery or billing.\n" +
									"\n" +
									"Use of the information collected\n" +
									"\n" +
									"Our website uses the information in order to provide the best possible service, particularly to maintain a user registry, of orders if applicable, and to improve our products and services. E-mails may be sent periodically through our site with special offers, new products and other advertising information that we consider relevant to you or that may provide some benefit, these emails will be sent to the address you provide and may be canceled anytime.\n" +
									"\n" +
									"Lo-fi RADIO pro is highly committed to fulfill the commitment to keep your information secure. We use the most advanced systems and constantly update them to ensure that there is no unauthorized access.\n" +
									"\n" +
									"cookies\n" +
									"\n" +
									"A cookie refers to a file that is sent for the purpose of requesting permission to be stored on your computer, accepting that file is created and the cookie is then used to have information regarding web traffic, and also facilitates future visits to a web recurrent. Another function that cookies have is that with them the websites can recognize you individually and therefore provide you with the best personalized service on your website.\n" +
									"\n" +
									"Our website uses cookies to identify the pages that are visited and their frequency. This information is used only for statistical analysis and then the information is permanently deleted. You can delete cookies at any time from your computer. However, cookies help to provide a better service for websites, they do not give access to information from your computer or from you, unless you want it and provide it directly, visits to a website. You can accept or deny the use of cookies, however most browsers accept cookies automatically as it serves to have a better web service. You can also change your computer settings to decline cookies. If they decline, you may not be able to use some of our services.\n" +
									"\n" +
									"Third Party Links\n" +
									"\n" +
									"This website may contain links to other sites that may be of interest to you. Once you click on these links and leave our page, we no longer have control over the site to which you are redirected and therefore we are not responsible for the terms or privacy or the protection of your data on those other third-party sites. These sites are subject to their own privacy policies, so it is recommended that you consult them to confirm that you agree with them.\n" +
									"\n" +
									"Control of your personal information\n" +
									"\n" +
									"At any time you may restrict the collection or use of personal information that is provided to our website. Each time you are asked to fill out a form, such as the user registration, you can check or uncheck the option to receive information by email. In case you have marked the option to receive our newsletter or advertising you can cancel it at any time.\n" +
									"\n" +
									"This company will not sell, assign or distribute personal information that is collected without your consent, unless required by a judge with a court order.\n" +
									"\n" +
									"Lo-fi RADIO pro reserves the right to change the terms of this Privacy Policy at any time.")

							// Specifying a listener allows you to take an action before dismissing the dialog.
							// The dialog is automatically dismissed when a dialog button is clicked.
							.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									checkPoliticas.setChecked(true);
								}
							})

							// A null listener allows the button to dismiss the dialog and take no further action.
							.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									checkPoliticas.setChecked(false);
								}
							})
							.setIcon(R.drawable.ic_close_black_24dp)
							.show();
				}
			}
		});

		btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (checkPoliticas.isChecked()) {
					mGoogleApiClient.clearDefaultAccountAndReconnect();
					signInGoogle();
				} else {
					Toast.makeText(Login.this, getString(R.string.leerpoliticas), Toast.LENGTH_SHORT).show();
				}


			}
		});


	}


	@Override
	protected void onStart() {
		super.onStart();
		mAuth.addAuthStateListener(mAuthListener);
		FirebaseUser u = mAuth.getCurrentUser();


	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mAuthListener != null) {
			mAuth.removeAuthStateListener(mAuthListener);
		}
	}


	public void revoke(View view) {

		mAuth.signOut();

		// Revoke Access Google...
	}


	// iniciar firebase auth para identificar si el usuario esta logeado
	private void initFirebaseAuth() {

		//Install Firebase
		//https://firebase.google.com/docs/android/setup

		mAuth = FirebaseAuth.getInstance();
		mAuthListener = new FirebaseAuth.AuthStateListener() {


			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user != null) {
					Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());

					startActivity(new Intent(Login.this, MainActivity.class));
					finish();


					Toast.makeText(Login.this, getString(R.string.loginexitoso), Toast.LENGTH_SHORT).show();

				} else {
					Log.d(TAG, "onAuthStateChanged:signed_out");

// Success

					//   Toast.makeText(login.this, getString(R.string.toast_debeiniciarsesion), Toast.LENGTH_SHORT).show();


				}
			}
		};

	}


	private void registrardatos() {


		//login aunth
		// get el usuario
		mAuth = FirebaseAuth.getInstance();
		FirebaseUser u = mAuth.getCurrentUser();

		if (u != null) {


			// enviar y registrar datos en la base de datos


			ref = FirebaseDatabase.getInstance().getReference();
			clientesRef = ref.child("lofiradio").child("usuario").child(u.getUid()).child("info");


			((DatabaseReference) clientesRef).child("nombregoogle").setValue(u.getDisplayName());
			((DatabaseReference) clientesRef).child("idioma").setValue(Locale.getDefault().getDisplayLanguage());
			((DatabaseReference) clientesRef).child("correo").setValue(u.getEmail());
			((DatabaseReference) clientesRef).child("imagengoogle").setValue(u.getPhotoUrl().toString());
			((DatabaseReference) clientesRef).child("uid").setValue(u.getUid());


			//region MIX PANEL


			MixpanelAPI mixpanel =
					MixpanelAPI.getInstance(Login.this, MIXPANEL_TOKEN);

// identify must be called before
// user profile properties can be set


			//decirle que todos los eventos seran registrados con el id del correo
			mixpanel.identify(u.getEmail());
			//para enviar la info a el identificador
			mixpanel.getPeople().identify(u.getEmail());

			// Sets user 13793's "Plan" attribute to "Premium"
			mixpanel.getPeople().set("$name", u.getDisplayName());
			mixpanel.getPeople().set("$email", u.getEmail());
			mixpanel.getPeople().set("Imagen", u.getPhotoUrl());
			mixpanel.getPeople().set("Idioma", Locale.getDefault().getDisplayLanguage());
			mixpanel.getPeople().set("Uid", u.getUid());
			mixpanel.getPeople().set("PrimeraVersion", BuildConfig.VERSION_NAME);

			//endregion


		}


	}


	// iniciar el proceso de google, para identificar si hay internet o si funciona
	private void initGoogleLogin() {

		//https://developers.google.com/identity/sign-in/android/sign-in

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(getString(R.string.default_web_client_id))
				.requestEmail()
				.build();

		// Build a GoogleApiClient with access to the Google Sign-In API and the
		// options specified by gso.
		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
					@Override
					public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

						//   Toast.makeText(login.this, getString(R.string.toast_errorconexion), Toast.LENGTH_SHORT).show();

					}
				})
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();

	}


	private void signInGoogle() {
		Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
		startActivityForResult(signInIntent, RC_SIGN_IN);
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
	}


	private void handleSignInResult(GoogleSignInResult result) {
		Log.d(TAG, "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			GoogleSignInAccount acct = result.getSignInAccount();
			firebaseAuthWithGoogle(acct);
			updateUI();
			//   Toast.makeText(this, "aaaaaaaaaa", Toast.LENGTH_SHORT).show();

		} else {
			// Signed out, show unauthenticated UI.
		}
	}


	private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
		//https://firebase.google.com/docs/auth/android/google-signin?utm_source=studio
		Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

		AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						if (task.isSuccessful()) {
							// Sign in success, update UI with the signed-in user's information
							Log.d(TAG, "signInWithCredential:success");
							FirebaseUser user = mAuth.getCurrentUser();
							updateUI();


							registrardatos();
							//  Toast.makeText(login.this, "Login Exitoso", Toast.LENGTH_SHORT).show();


						} else {
							// If sign in fails, display a message to the user.
							Log.w(TAG, "signInWithCredential:failure", task.getException());

							//    Toast.makeText(login.this, getString(R.string.toast_errorautenticacion), Toast.LENGTH_LONG).show();

						}
					}
				});
	}


	private void updateUI() {


		FirebaseUser u = mAuth.getCurrentUser();


	}


}
