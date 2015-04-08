package com.gmail.spaskhristov.ac;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class MenuActivity extends Activity implements
		NumberPicker.OnValueChangeListener, RadioGroup.OnCheckedChangeListener {

	private String URL;// = "http://10.0.2.2:82/ac/index.php";
	private String URLdata;
	private String msg = "AC Menu project: ";
	private NumberPicker tempPicker;
	private TextView tempHome;
	private RadioGroup radioMode;
	private RadioGroup radioFanSpeed;
	private RadioButton radioModeAuto;
	private RadioButton radioModeCool;
	private RadioButton radioModeDry;
	private RadioButton radioModeHeat;
	private RadioButton radioFan1;
	private RadioButton radioFan2;
	private RadioButton radioFan3;
	private RadioButton radioFan4;
	private String optionAC;
	private int valTemp;
	private String valMode;
	private String valFan;
	private SharedPreferences prefs;

	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
		this.URL = this.prefs.getString("url", null);
		this.URLdata = this.URL + "data.txt";
		if (this.URL == null) {
			Intent intent = new Intent(this, SetUrlActivity.class);
			startActivity(intent);
		}

		//

		this.tempPicker = (NumberPicker) findViewById(R.id.tempPicker);
		this.tempPicker.setOnValueChangedListener(this);

		this.radioFanSpeed = (RadioGroup) findViewById(R.id.radioFanSpeed);
		this.radioFanSpeed.setOnCheckedChangeListener(this);

		this.radioMode = (RadioGroup) findViewById(R.id.radioMode);
		this.radioMode.setOnCheckedChangeListener(this);

		this.radioModeAuto = (RadioButton) findViewById(R.id.radioModeAuto);
		this.radioModeCool = (RadioButton) findViewById(R.id.radioModeCool);
		this.radioModeDry = (RadioButton) findViewById(R.id.radioModeDry);
		this.radioModeHeat = (RadioButton) findViewById(R.id.radioModeHeat);

		this.radioFan1 = (RadioButton) findViewById(R.id.radioFan1);
		this.radioFan2 = (RadioButton) findViewById(R.id.radioFan2);
		this.radioFan3 = (RadioButton) findViewById(R.id.radioFan3);
		this.radioFan4 = (RadioButton) findViewById(R.id.radioFan4);

		this.tempHome = (TextView) findViewById(R.id.txtTempHome);

		new HttpAsyncTask().execute(URLdata);

		this.handler = new Handler();

		doTheAutoRefresh();

	}

	private void doTheAutoRefresh() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				getTempHome(""); // this is where you put your refresh code
				doTheAutoRefresh();
			}
		};
		handler.postDelayed(runnable, 3000);
	}

	@Override
	protected void onResume() {
		super.onResume();

		this.URL = this.prefs.getString("url", null);
		if (this.URL == null) {
			Intent intent = new Intent(this, SetUrlActivity.class);
			startActivity(intent);
		}

		Log.d(msg, "The onResume() event");
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacksAndMessages(null);
		Log.d(msg, "The onPause() event");
	}

	@Override
	protected void onStop() {
		super.onStop();
		handler.removeCallbacksAndMessages(null);
		Log.d(msg, "The onStop() event");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacksAndMessages(null);
		Log.d(msg, "The onDestroy() event");
	}

	@Override
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
		makeMsg();
	}

	public void sendMessage(String message) {
		ConnectionServer connectionServer = new ConnectionServer();
		try {
			String response = String.valueOf(connectionServer.sendMessage(URL,
					message));
			this.tempHome.setText(response);
			if (!response.equals("no connection")) {
				doTheAutoRefresh();
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void getTempHome(String urlStr) {
		ConnectionServer connectionServer = new ConnectionServer();
		try {
			String response = String.valueOf(connectionServer.sendMessage(URL,
					""));
			this.tempHome.setText(response);
			if (response.equals("no connection")) {
				handler.removeCallbacksAndMessages(null);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private void parseData() {
		this.valTemp = Integer.parseInt(this.optionAC.substring(0, 2));
		this.valMode = this.optionAC.substring(2, 3);
		this.valFan = this.optionAC.substring(3);
	}

	private void setOptionAC() {
		setTempPicker();
		setMode();
		setFanSpeed();
	}

	private void setTempPicker() {
		this.tempPicker.setMinValue(16);
		this.tempPicker.setMaxValue(32);
		this.tempPicker.setValue(this.valTemp);
	}

	private void setMode() {
		if (this.valMode.equals("a")) {
			this.radioModeAuto.setChecked(true);
		} else if (this.valMode.equals("c")) {
			this.radioModeCool.setChecked(true);
		} else if (this.valMode.equals("d")) {
			this.radioModeDry.setChecked(true);
		} else {
			this.radioModeHeat.setChecked(true);
		}
	}

	private void setFanSpeed() {
		if (this.valFan.equals("1")) {
			this.radioFan1.setChecked(true);
		} else if (this.valFan.equals("2")) {
			this.radioFan2.setChecked(true);
		} else if (this.valFan.equals("3")) {
			this.radioFan3.setChecked(true);
		} else {
			this.radioFan4.setChecked(true);
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		makeMsg();
	}

	private void makeMsg() {
		String currTemp = String.valueOf(this.tempPicker.getValue());
		int currModeId = radioMode.getCheckedRadioButtonId();
		int currFanSpeedId = radioFanSpeed.getCheckedRadioButtonId();
		String currMode = "";
		String currFanSpeed = "";
		switch (currModeId) {
		case R.id.radioModeAuto:
			currMode = "a";
			break;
		case R.id.radioModeCool:
			currMode = "c";
			break;
		case R.id.radioModeDry:
			currMode = "d";
			break;
		case R.id.radioModeHeat:
			currMode = "h";
			break;
		}
		switch (currFanSpeedId) {
		case R.id.radioFan1:
			currFanSpeed = "1";
			break;
		case R.id.radioFan2:
			currFanSpeed = "2";
			break;
		case R.id.radioFan3:
			currFanSpeed = "3";
			break;
		case R.id.radioFan4:
			currFanSpeed = "4";
			break;
		}
		String msg = currTemp + currMode + currFanSpeed;
		sendMessage(msg);

	}

	public String getOptionAC() {
		InputStream inputStream = null;
		String result = "";
		try {
			// create HttpClient
			HttpClient httpclient = new DefaultHttpClient();
			// make GET request to the given URL
			HttpResponse httpResponse = httpclient.execute(new HttpGet(
					this.URLdata));
			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();
			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return result;
	}

	// convert inputstream to String
	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null) {
			result += line;
		}
		inputStream.close();
		return result;

	}

	private class HttpAsyncTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return getOptionAC();
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			optionAC = result;
			parseData();
			setOptionAC();
		}
	}
}
