package com.example.barcodescan;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class BarcodeScanActivity extends Activity {
	
	public final static String POST_RESULT = "com.example.myfirstapp.POSTRESULT";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_barcode_scan);
		
		AppPreferences prefs = new AppPreferences(this);
		String key = prefs.getAPIKey();
		
		if (!key.equals("")) {
			EditText username = (EditText)findViewById(R.id.username);
			username.setText(prefs.getEmail());
			
			EditText pass = (EditText)findViewById(R.id.password);
			pass.setText("aaaaaaaaaaaaa");
		}
	}
	
	public void authenticate(View view) {
		Intent intent = new Intent(this, BookListActivity.class);
		
		EditText username = (EditText)findViewById(R.id.username);
		String userMessage = username.getText().toString();
		
		EditText pass = (EditText)findViewById(R.id.password);
		String passMessage = pass.getText().toString();
		
		AppPreferences prefs = new AppPreferences(this);
		
		String key = prefs.getAPIKey();
		
		String id = "";
		String email = "";
		String apiKey = "";
		
		if (key.equals("")) {
			JSONObject userObject = null;
			try {
				JSONObject result = WebService.sendLogin(this,userMessage, passMessage);
				userObject = (JSONObject)result.get("user");
				
				id = userObject.getString("id");
				email = userObject.getString("email");
				apiKey = userObject.getString("api_key");			
				
			} catch (Exception e) {
				(Toast.makeText(this, "Error Logging In", Toast.LENGTH_SHORT)).show();
				return;
			}
		} else {
			id = prefs.getId();
			email = prefs.getEmail();
			apiKey = key;
		}
		
		User user = new User(email,id,apiKey);	
		prefs.logIn(user);
		
		Bundle bundle = new Bundle();
		bundle.putSerializable("user", user);
		
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
