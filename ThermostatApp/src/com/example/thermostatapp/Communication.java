package com.example.thermostatapp;

import java.net.ConnectException;
import java.util.concurrent.ExecutionException;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.InvalidInputValueException;
import org.thermostatapp.util.WeekProgram;

import android.os.AsyncTask;
import android.util.Log;

public class Communication {

	Communication() {

	}

	/**
	 * Get communicators:
	 */

	public String getTime() throws InterruptedException, ExecutionException {
		return requestData("Time");
	}

	public String getDay() throws InterruptedException, ExecutionException {
		return requestData("Day");
	}

	public String getCurrentTemperature() throws InterruptedException,
			ExecutionException {
		return requestData("currentTemperature");
	}

	public String getDayTemperature() throws InterruptedException,
			ExecutionException {
		return requestData("dayTemperature");
	}

	public String getNightTemperature() throws InterruptedException,
			ExecutionException {
		return requestData("nightTemperature");
	}

	public String getWeekProgramState() throws InterruptedException,
			ExecutionException {
		Log.w("Week ProgramState = ", requestData("weekProgramState"));
		return requestData("weekProgramState");
	}

	public WeekProgram getWeekProgram() throws InterruptedException,
			ExecutionException {

		return new DownloadWeekProgram().execute().get();
	}

	public void setTime(String time) {
		try {
			putRequest("time", time);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDay(String day) {
		try {
			putRequest("day", day);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setCurrentTemperature(String temperature) {
		try {
			putRequest("currentTemperature", temperature);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setDayTemperature(String temperature) {
		try {
			putRequest("dayTemperature", temperature);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setNightTemperature(String temperature) {
		try {
			putRequest("nightTemperature", temperature);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setWeekProgramState(String state) {
		try {
			putRequest("weekProgramState", state);
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setWeekProgram(WeekProgram wpg) {
		pushWeekProgram(wpg);
	}

	/**
	 * Get and put methods:
	 */

	private String getData(String attributeName) {
		String data = "";
		try {
			data = HeatingSystem.get(attributeName);
			Log.d("getting data for " + attributeName, "Data == " + data);
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("[GET-REQUEST]", "A connection exeption occured");
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("[GET-REQUEST]",
					"Found an illigal argument for a get request");
		}
		return data;
	}

	private String requestData(String request) throws InterruptedException,
			ExecutionException {
		String data = "";

		data = new DownloadWebpageTask().execute(request).get();

		return data;
	}

	private void putData(String attributeName, String value) {

		try {
			HeatingSystem.put(attributeName, value);
		} catch (IllegalArgumentException | InvalidInputValueException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void putRequest(String requestedAttribute, String value)
			throws InterruptedException, ExecutionException {
		new UploadWebpageTask().execute(requestedAttribute, value).get();
	}

	private WeekProgram requestWeekProgram() {
		WeekProgram wpg = null;
		try {
			wpg = HeatingSystem.getWeekProgram();
		} catch (ConnectException | CorruptWeekProgramException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return wpg;
	}

	private void pushWeekProgram(WeekProgram wpg) {
		try {
			new UploadWeekProgram().execute(wpg).get();
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void putWeekProgram(WeekProgram wpg) {
		HeatingSystem.setWeekProgram(wpg);
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

	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			return getData(params[0]);
		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Original:
			// textView.setText(result);

		}
	}

	private class UploadWebpageTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {

			putData(params[0], params[1]);

			return "";

		}

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			// Original:
			// textView.setText(result);

		}
	}

	private class DownloadWeekProgram extends
			AsyncTask<Void, Void, WeekProgram> {

		@Override
		protected WeekProgram doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return requestWeekProgram();
		}
	}

	private class UploadWeekProgram extends
			AsyncTask<WeekProgram, Void, Boolean> {

		@Override
		protected Boolean doInBackground(WeekProgram... params) {
			putWeekProgram(params[0]);
			return null;
			// TODO Auto-generated method stub

		}

	}

}
