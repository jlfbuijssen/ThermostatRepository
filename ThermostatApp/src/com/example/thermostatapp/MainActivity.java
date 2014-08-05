package com.example.thermostatapp;

import java.util.concurrent.ExecutionException;

import org.thermostatapp.util.WeekProgram;

import com.example.thermostatapp.R.color;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	double currentTemperature, manualTemperature, setTemperature;
	String sCurrentTemp, sManualTemp, sSetTemp;
	String time, day, state;
	boolean manualSetting;
	Handler mainHandler, dataHandler;
	long updateDelay;
	WeekProgram wpg;

	// Navigation Buttons
	ImageButton weekProgramNav, preferencesNav;
	// Image buttons to adjust temperature (manually)
	ImageButton buttonTempUp, buttonTempDown;

	// day/night temp icons
	ImageView dayTempSettingView, nightTempSettingView;
	// manual + vacation setting "icons"
	TextView manualSettingView, vacationSettingView;

	// TextView to show the temperature
	TextView temperatureView, tempUnitView;
	// TextViews to show date/time
	TextView timeView, dateView, dayView;
	// TextView to show the time of the next temperature setting switch (e.g.
	// night temp to day temp)
	TextView nextSwitchTimeView;
	
	Communication channel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initialize();
	}

	public void initialize() {
		// TODO Get the temperature from the server and store in public variable
		
		channel = new Communication();
		
		currentTemperature = 210;
		setTemperature = 220;
		manualTemperature = 0;

		sCurrentTemp = String.format("%.1f", (currentTemperature * 0.1));
		sManualTemp = String.format("%.1f", manualTemperature * 0.1);
		sSetTemp = String.format("%.1f", setTemperature * 0.1);

		day = "Monday";
		time = "00:00";
		state = "on";
		
		// TODO check all variables in this method.
		manualSetting = false;

		mainHandler = new Handler();
		mainHandler.postDelayed(mainRunnable, 0);
		dataHandler = new Handler();
		

		// UI update delay in milliseconds
		updateDelay = 100;
		Thread mainRunnableThread = new Thread(dataRunnable);
		mainRunnableThread.start();
		
		/**
		 * Init views:
		 */

		// Init TextViews:

		// Setting icons
		dayTempSettingView = (ImageView) findViewById(R.id.imageView_sun);
		nightTempSettingView = (ImageView) findViewById(R.id.imageView_moon);

		manualSettingView = (TextView) findViewById(R.id.textView_manualToggle);
		vacationSettingView = (TextView) findViewById(R.id.textView_vacationToggle);

		// Temperature Text views:
		temperatureView = (TextView) findViewById(R.id.temp_textview);
		tempUnitView = (TextView) findViewById(R.id.unit_temp_textview);

		// Date/time text views:
		timeView = (TextView) findViewById(R.id.time_textview);
		dateView = (TextView) findViewById(R.id.date_textview);
		dateView.setText("");
		dayView = (TextView) findViewById(R.id.day_textview);

		// Next switch text view:
		nextSwitchTimeView = (TextView) findViewById(R.id.next_switch_time_textview);

		// Init buttons:
		buttonTempUp = (ImageButton) findViewById(R.id.temp_up_button);
		buttonTempDown = (ImageButton) findViewById(R.id.temp_down_button);

		weekProgramNav = (ImageButton) findViewById(R.id.imageButton_weekProgram);
		preferencesNav = (ImageButton) findViewById(R.id.imageButton_preferences);
		
		
	}

	public void increaseTemperature(View view) {
		/*
		if (manualTemperature == currentTemperature) {\
			manualTemperature += 0.1;
		} else if (manualTemperature != currentTemperature) {
			manualTemperature = currentTemperature + 0.1;
		}

		if (manualTemperature == setTemperature) {
			manualSetting = false;
		} else if (manualTemperature != setTemperature) {
			manualSetting = true;
		}
		*/
		
		try {
			currentTemperature = Double.parseDouble(channel.getCurrentTemperature());
		} catch (NumberFormatException | InterruptedException
				| ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Extra hassle because of double type
		currentTemperature *= 10;
		currentTemperature ++;
		currentTemperature /= 10;
		
		String t = String.format("%.1f", currentTemperature);
		channel.setCurrentTemperature(t);
		mainHandler.removeCallbacks(mainRunnable);
		dataHandler.removeCallbacks(dataRunnable);
		mainHandler.postDelayed(mainRunnable, 0);
		dataHandler.postDelayed(dataRunnable, 0);
	}

	public void decreaseTemperature(View view) {
		/*if (manualTemperature == currentTemperature) {
			manualTemperature -= 0.1;
		} else if (manualTemperature != currentTemperature) {
			manualTemperature = currentTemperature - 0.1;
		}

		if (manualTemperature == setTemperature) {
			manualSetting = false;
		} else if (manualTemperature != setTemperature) {
			manualSetting = true;
		}*/
		
		try {
			currentTemperature = Double.parseDouble(channel.getCurrentTemperature());
		} catch (NumberFormatException | InterruptedException
				| ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Extra hassle because of double type
		currentTemperature *= 10;
		currentTemperature --;
		currentTemperature /= 10;
		
		String t = String.format("%.1f", currentTemperature);
		Log.i("Temperature", t);
		channel.setCurrentTemperature(t);
		mainHandler.removeCallbacks(mainRunnable);
		dataHandler.removeCallbacks(dataRunnable);
		mainHandler.postDelayed(mainRunnable, 0);
		dataHandler.postDelayed(dataRunnable, 0);
	}


	
	
	/**
	 * Invoking of activities
	 */

	public void invokeWeekProgramActivity(View view) {
		// create an intent
		Intent intent = new Intent(this, WeekProgramActivity.class);
		// Invoke the activity
		startActivity(intent);
	}

	public void invokePreferencesActivity(View view) {
		// create an intent
		Intent intent = new Intent(this, PreferencesActivity.class);
		// Invoke the activity
		startActivity(intent);
	}

	/**
	 * This part of the code comprises of runnables.
	 */

	/**
	 * This runnable is used to update the date and day, it will run every
	 * <code>updateDelay</code> miliseconds
	 */
	private Runnable mainRunnable = new Runnable() {
		@Override
		public void run() {
					
			//Debug code:
			//Log.i("[TIME-TEST]", "t should be empty: " + t);
			
			
			//Debug code:
			//Log.i("[TIME-TEST]", "t should give a time string: " + t);
			
			
			
			/**
			 * Update Text views
			 */
			
			timeView.setText(time);
			dayView.setText(day);
			temperatureView.setText(sCurrentTemp);
			//Log.d("s == ", s);
			//Log.d("b == ", Boolean.toString(!s.equals("on")));
			if(state.equals("on")){
				vacationSettingView.setTextColor(getResources().getColor(R.color.light_gray));
			} else {
				vacationSettingView.setTextColor(getResources().getColor(R.color.white));
			}
			
			// foobar();
			/* makes the runnable re-run: */
			mainHandler.postDelayed(this, updateDelay);
		}
	};
	
	private Runnable dataRunnable = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] s = updateData();
			time = s[0];
			day = s[1];
			sCurrentTemp = s[2];
			state = s[3];
			
			
			
			dataHandler.postDelayed(this, updateDelay);
		}
		
	};

	String[] updateData(){
		String[] s = new String[4];
		try {
			s[0] = channel.getTime();
			s[1] = channel.getDay();
			s[2] = channel.getCurrentTemperature();
			s[3] = channel.getWeekProgramState();
			//Log.d("s == ", s);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return s;
	}
	
	
}