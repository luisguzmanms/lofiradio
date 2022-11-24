package com.lamesa.lofiradiopro.radio.player;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

class NotificacionIntent extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (savedInstanceState == null) {
			Bundle extras = getIntent().getExtras();
			if (extras == null) {
				Toast.makeText(this, "NOOOO", Toast.LENGTH_SHORT).show();
			} else if (extras.getBoolean("NotiClick")) {
				Toast.makeText(this, "LOLOOOO", Toast.LENGTH_SHORT).show();
			}

		} else {
			Toast.makeText(this, "LOLOOadasdasdsadOO", Toast.LENGTH_SHORT).show();
		}

	}


}
