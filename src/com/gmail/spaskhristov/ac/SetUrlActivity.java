package com.gmail.spaskhristov.ac;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetUrlActivity extends Activity implements OnClickListener {

	private String msg = "AC set URL: ";
	private Button btnSetURL;
	private TextView txtURL;
	private EditText editTxtSetUrl;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_seturl);

		this.prefs = PreferenceManager.getDefaultSharedPreferences(this);

		this.txtURL = (TextView) findViewById(R.id.txtURL);
		this.editTxtSetUrl = (EditText) findViewById(R.id.editTxtSetUrl);

		this.btnSetURL = (Button) findViewById(R.id.btnSetURL);
		this.btnSetURL.setOnClickListener(this);
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
		switch (v.getId()) {
		case R.id.btnSetURL:
			this.editor = prefs.edit();
			String currUrl = String.valueOf(this.txtURL.getText());
			String newUrl = String.valueOf(this.editTxtSetUrl.getText());
			if (this.editTxtSetUrl.getText().toString().trim().length() > 0) {
				editor.putString("url", newUrl);
				editor.commit();
				this.txtURL.setText(newUrl);
			} else {
				editor.putString("url", currUrl);
				editor.commit();
			}
			finish();
			break;
		}
	}
}
