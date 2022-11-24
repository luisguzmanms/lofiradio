package com.lamesa.lofiradiopro.radio;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.lamesa.lofiradiopro.R;
import com.lamesa.lofiradiopro.radio.player.PlaybackStatus;
import com.lamesa.lofiradiopro.radio.player.RadioManager;
import com.lamesa.lofiradiopro.utils.Shoutcast;
import com.lamesa.lofiradiopro.utils.ShoutcastHelper;
import com.lamesa.lofiradiopro.utils.ShoutcastListAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class MainActivity extends AppCompatActivity {

	@BindView(R.id.toolbar)
	Toolbar toolbar;

	@BindView(R.id.playTrigger)
	ImageButton trigger;

	@BindView(R.id.listview)
	ListView listView;

	@BindView(R.id.name)
	TextView textView;

	@BindView(R.id.sub_player)
	View subPlayer;

	RadioManager radioManager;

	String streamURL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_radio);

		ButterKnife.bind(this);

		setSupportActionBar(toolbar);

		radioManager = RadioManager.with(this);

		listView.setAdapter(new ShoutcastListAdapter(this, ShoutcastHelper.retrieveShoutcasts(this)));
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

	@Subscribe
	public void onEvent(String status) {

		switch (status) {

			case PlaybackStatus.LOADING:

				// loading
				Toast.makeText(this, "Cargando", Toast.LENGTH_SHORT).show();
				break;

			case PlaybackStatus.ERROR:

				Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();

				break;

		}

		trigger.setImageResource(status.equals(PlaybackStatus.PLAYING)
				? R.drawable.ic_pause_black
				: R.drawable.ic_play_arrow_black);

	}

	@OnClick(R.id.playTrigger)
	public void onClicked() {

		if (TextUtils.isEmpty(streamURL)) return;

		radioManager.playOrPause(streamURL);
	}

	@OnItemClick(R.id.listview)
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		Shoutcast shoutcast = (Shoutcast) parent.getItemAtPosition(position);
		if (shoutcast == null) {

			return;

		}

		textView.setText(shoutcast.getName());

		subPlayer.setVisibility(View.VISIBLE);

		streamURL = shoutcast.getUrl();

	}
}