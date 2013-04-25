package com.example.barcodescan;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AppPreferences {
	 private static final String APP_SHARED_PREFS = "com.example.barcodescan.barcodeScan_preferences";
	 
	 private SharedPreferences preferences;
	 
	 public AppPreferences(Context context) {
		 preferences = context.getSharedPreferences(APP_SHARED_PREFS, Activity.MODE_PRIVATE);
	 }
	
	 public void logIn(User user) {
		 
		 Editor preferencesEditor = preferences.edit();
			
	   preferencesEditor.putString("id", user.getId());
	   preferencesEditor.putString("email", user.getEmail());
	   preferencesEditor.putString("apikey", user.getApiKey());
	   preferencesEditor.commit();
	 }
	 
	 public void logOut() {
		 Editor preferencesEditor = preferences.edit();		
	   preferencesEditor.clear();
		 preferencesEditor.commit();
	 }
	 
	 public String getAPIKey() {
		 return preferences.getString("apikey", ""); 
	 }
	 
	 public String getEmail() {
		 return preferences.getString("email", "");
	 }
	 public String getId() {
		 return preferences.getString("id", "");
	 }
}
