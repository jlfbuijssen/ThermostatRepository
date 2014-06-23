package com.example.thermostatapp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.InvalidInputValueException;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	double currentTemperature, manualTemperature, setTemperature;
	String sCurrentTemp, sManualTemp, sSetTemp;
	String time;
	boolean manualSetting;
	Handler mainHandler;
	int dateUpdateDelay;

	// Navigation Buttons
	ImageButton weekProgramNav, preferencesNav;
	// Image buttons to adjust temperature (manually)
	ImageButton buttonTempUp, buttonTempDown;

	// day/night temp icons
	ImageView dayTempSettingView, nightTempSettingView;
	// manual + vacation setting "icons"
	TextView manualSettingView, vacationSettingView;

	// Textview to show the temperature
	TextView temperatureView, tempUnitView;
	// Textviews to show date/time
	TextView timeView, dateView, dayView;
	// Textview to show the time of the next temperature setting switch (e.g.
	// night temp to day temp)
	TextView nextSwitchTimeView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initialize();
	}

	public void initialize() {
		// TODO Get the temperature from the server and store in public variable
		
		
		
		new Thread(new Runnable() {

			public void run() {
				try {
					currentTemperature = 10 * Double.parseDouble(HeatingSystem
							.get("currentTemperature"));
				} catch (ConnectException | IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.w("[GET]", e);
				}
			}
			
		}).start();

		currentTemperature = 210;
		setTemperature = 220;
		manualTemperature = 0;

		sCurrentTemp = String.format("%.1f", (currentTemperature * 0.1));
		sManualTemp = String.format("%.1f", manualTemperature * 0.1);
		sSetTemp = String.format("%.1f", setTemperature * 0.1);

		// TODO check all variables in this method.
		manualSetting = false;

		mainHandler = new Handler();
		mainHandler.postDelayed(mainRunnable, 100);

		// UI update delay
		dateUpdateDelay = 1600;
		
		

		/**
		 * Init views:
		 */

		// Init textviews:

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
		if (manualTemperature == currentTemperature) {
			manualTemperature += 0.1;
		} else if (manualTemperature != currentTemperature) {
			manualTemperature = currentTemperature + 0.1;
		}

		if (manualTemperature == setTemperature) {
			manualSetting = false;
		} else if (manualTemperature != setTemperature) {
			manualSetting = true;
		}

	}

	public void decreaseTemperature(View view) {
		if (manualTemperature == currentTemperature) {
			manualTemperature -= 0.1;
		} else if (manualTemperature != currentTemperature) {
			manualTemperature = currentTemperature - 0.1;
		}

		if (manualTemperature == setTemperature) {
			manualSetting = false;
		} else if (manualTemperature != setTemperature) {
			manualSetting = true;
		}
	}


	
	public String getTime() throws InterruptedException, ExecutionException{
		return requestData("Time");
		
	}
	public String getDay() throws InterruptedException, ExecutionException{
		return requestData("Day");
	}
	
	/**
	 * Get and put methods:
	 */
	
	public String getData(String attributeName) {
		String data = "";
		try {
			data = HeatingSystem.get(attributeName);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("[GET-REQUEST]", "A connection exeption occured");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("[GET-REQUEST]", "Found an illigal argument for a get request");
		}
		return data;
	}
	
	public String requestData(String request) throws InterruptedException, ExecutionException {
		String data = "";
		
		data = new DownloadWebpageTask().execute(request).get();
		
		return data;
	}
	
	public void putData(String attributeName, String value){
		
		try {
			HeatingSystem.put(attributeName, value);
		} catch (IllegalArgumentException | InvalidInputValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void putRequest(String requestedAttribute, String value){
		
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
	 * <code>dateUpdateDelay</code> miliseconds
	 */
	private Runnable mainRunnable = new Runnable() {
		@Override
		public void run() {
			String t = "";
			String d = "";
			
			//Debug code:
			//Log.i("[TIME-TEST]", "t should be empty: " + t);
			
			
			
			
			try {
				t = getTime();
				d = getDay();
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//Debug code:
			//Log.i("[TIME-TEST]", "t should give a time string: " + t);
			
			if (manualSetting) {
				temperatureView.setText(String.format("%.1f",
						manualTemperature * 0.1));
			} else {
				temperatureView.setText(String.format("%.1f",
						currentTemperature * 0.1));
			}
			/**
			 * Update Text views
			 */
			
			timeView.setText(t);
			dayView.setText(d);
			
			// foobar();
			/* makes the runnable re-run: */
			mainHandler.postDelayed(this, dateUpdateDelay);
		}
	};

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {

			return getData(urls[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Original:
			// textView.setText(result);

		}
	}

	/** Code copyrighted by Android */
	/**
	 * Uses AsyncTask to create a task away from the main UI thread. This task
	 * takes a URL string and uses it to create an HttpUrlConnection. Once the
	 * connection has been established, the AsyncTask downloads the contents of
	 * the webpage as an InputStream. Finally, the InputStream is converted into
	 * a string, which is displayed in the UI by the AsyncTask's onPostExecute
	 * method.
	 */
	
	private class UploadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			putData(params[0], params[1]);
			
			return "" ;

		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Original:
			// textView.setText(result);

		}
	} 

	
}
