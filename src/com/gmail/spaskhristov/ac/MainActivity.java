package com.gmail.spaskhristov.ac;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

	private String URL;// = "http://10.0.2.2:82/ac/data.txt";
	private String msg = "AC Main project: ";
	private Button btnOnOff;
	private Button btnSetUrl;
	private SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.URL = this.prefs.getString("url", null);
		if (this.URL == null) {
			Intent intent = new Intent(this, SetUrlActivity.class);
			startActivity(intent);
		}

		this.btnOnOff = (Button) findViewById(R.id.btnOnOff);
		this.btnOnOff.setOnClickListener(this);

		this.btnSetUrl = (Button) findViewById(R.id.btnSetUrl);
		this.btnSetUrl.setOnClickListener(this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(msg, "The onResume() event");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(msg, "The onPause() event");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.d(msg, "The onStop() event");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(msg, "The onDestroy() event");
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btnOnOff:
			intent = new Intent(this, MenuActivity.class);
			startActivity(intent);
			break;
		case R.id.btnSetUrl:
			intent = new Intent(this, SetUrlActivity.class);
			startActivity(intent);
			break;
		}
	}
}
