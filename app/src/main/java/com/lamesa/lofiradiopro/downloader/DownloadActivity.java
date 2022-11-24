package com.lamesa.lofiradiopro.downloader;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lamesa.lofiradiopro.R;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class DownloadActivity extends Activity {

	private static final int ITAG_FOR_AUDIO = 140;

	private static String youtubeLink;

	private LinearLayout mainLayout;
	private ProgressBar mainProgressBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_sample_download);
		mainLayout = (LinearLayout) findViewById(R.id.main_layout);
		mainProgressBar = (ProgressBar) findViewById(R.id.prgrBar);


		String mLinkCancion = getIntent().getExtras().getString("mLinkCancion");

		if (mLinkCancion != null
				&& (mLinkCancion.contains("://youtu.be/") || mLinkCancion.contains("youtube.com/watch?v="))) {
			youtubeLink = mLinkCancion;
			// We have a valid link
			getYoutubeDownloadUrl(youtubeLink);
			Toast.makeText(this, "LINK LINK LINK", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, "TESTSTSTSTSTSTSTS", Toast.LENGTH_LONG).show();
			System.out.println("adsssssssssssssssssss");
			finish();
		}
	}


	private void getYoutubeDownloadUrl(String youtubeLink) {
		new YouTubeExtractor(this) {

			@Override
			public void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta vMeta) {
				mainProgressBar.setVisibility(View.GONE);

				if (ytFiles == null) {
					// Something went wrong we got no urls. Always check this.
					finish();
					return;
				}
				// Iterate over itags
				for (int i = 0, itag; i < ytFiles.size(); i++) {
					itag = ytFiles.keyAt(i);
					// ytFile represents one file with its url and meta data
					YtFile ytFile = ytFiles.get(itag);

					// Just add videos in a decent format => height -1 = audio
					if (ytFile.getFormat().getHeight() == -1 || ytFile.getFormat().getHeight() >= 360) {
						addButtonToMainLayout(vMeta.getTitle(), ytFile);
						System.out.println("YOUUUUUUUUUUUUUUUU :: " + ytFile.getUrl());
						Toast.makeText(DownloadActivity.this, ytFile.getUrl(), Toast.LENGTH_SHORT).show();

					}
				}
			}
		}.extract(youtubeLink, true, false);
	}

	private void addButtonToMainLayout(final String videoTitle, final YtFile ytfile) {
		// Display some buttons and let the user choose the format
		String btnText = (ytfile.getFormat().getHeight() == -1) ? "Audio " +
				ytfile.getFormat().getAudioBitrate() + " kbit/s" :
				ytfile.getFormat().getHeight() + "p";
		btnText += (ytfile.getFormat().isDashContainer()) ? " dash" : "";
		Button btn = new Button(this);
		btn.setText(btnText);
		btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String filename;
				if (videoTitle.length() > 55) {
					filename = videoTitle.substring(0, 55) + "." + ytfile.getFormat().getExt();
				} else {
					filename = videoTitle + "." + ytfile.getFormat().getExt();
				}
				filename = filename.replaceAll("[\\\\><\"|*?%:#/]", "");
				downloadFromUrl(ytfile.getUrl(), videoTitle, filename);
				System.out.println("asdasdasd " + ytfile.getUrl());
				Toast.makeText(DownloadActivity.this, ytfile.getUrl(), Toast.LENGTH_SHORT).show();
				finish();
			}
		});
		mainLayout.addView(btn);
	}


	private void downloadFromUrl(String youtubeDlUrl, String downloadTitle, String fileName) {
		Uri uri = Uri.parse(youtubeDlUrl);
		Toast.makeText(this, youtubeDlUrl, Toast.LENGTH_SHORT).show();
		System.out.println("YOUUUUUUUUUUUUUUUU :: " + youtubeDlUrl);
		DownloadManager.Request request = new DownloadManager.Request(uri);
		request.setTitle(downloadTitle);

		request.allowScanningByMediaScanner();
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

		DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

}
