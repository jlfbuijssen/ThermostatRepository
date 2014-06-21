package com.example.thermostatapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
	
	double currentTemperature, manualTemperature, setTemperature;
	boolean manualSetting;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		/* Hello guys, Max was here
		 * And So was Jeffrey
		 */
		initialize();
	}
	
	public void initialize(){
		//TODO Get the temperature from the server and store in public variable
		currentTemperature = 21.0;
		setTemperature = 22.0;
		manualTemperature = 0;
		//TODO check all variables in this method.
		manualSetting = false;
	}
	public void increaseTemperature(){
		if (manualTemperature == currentTemperature) {
			manualTemperature += 0.1;
		} else if (manualTemperature != currentTemperature) {
			manualTemperature = currentTemperature + 0.1;
		}
		
		if (manualTemperature == setTemperature){
			manualSetting = false;
		} else if (manualTemperature != setTemperature){
			manualSetting = true;
		}
		
	}
	public void decreaseTemperature(){
		if (manualTemperature == currentTemperature) {
			manualTemperature -= 0.1;
		} else if (manualTemperature != currentTemperature) {
			manualTemperature = currentTemperature - 0.1;
		}
		
		if (manualTemperature == setTemperature){
			manualSetting = false;
		} else if (manualTemperature != setTemperature){
			manualSetting = true;
		}
	}
	public void invokeWeekProgramActivity(){
		//create an intent
		Intent intent = new Intent(this, WeekProgramActivity.class);
		//Invoke the activity
		startActivity(intent);
	}
	public void invokePreferencesActivity(){
		// create an intent 
		Intent intent = new Intent(this, PreferencesActivity.class);
		//Invoke the activity
		startActivity(intent);
	}
}
