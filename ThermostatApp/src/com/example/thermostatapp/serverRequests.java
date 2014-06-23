
package com.example.thermostatapp;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Locale;







import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.InvalidInputValueException;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;

import android.util.Xml;

public class serverRequests {

	private static final String targetURL = "http://localhost:8080/";
	private final static int TIME_OUT = 10000; // in milliseconds.
	
	private static boolean inTemperatureBoundaries(String temperature)
			throws InvalidInputValueException {
		try {
			double temp = Double.parseDouble(temperature);
			if (temp >= 5 && temp <= 30)
				return true;
			throw new InvalidInputValueException(
					"Invalid Value for temperature: " + temperature
							+ ", must be between 5.00 & 30.0 degrees.");
		} catch (NumberFormatException e) {
			throw new InvalidInputValueException(
					"Invalid Value for temperature syntax: " + temperature);
		}
	}
	
	private static HttpURLConnection getHttpConnection(String link, String type)
			throws IOException, MalformedURLException, UnknownHostException,
			FileNotFoundException {
		
		URL url = new URL(link);
		
		HttpURLConnection connect = (HttpURLConnection) url.openConnection();
		
		connect.setReadTimeout(TIME_OUT);
		connect.setConnectTimeout(TIME_OUT);
		connect.setRequestProperty("Content-Type","application/xml");
		connect.setRequestMethod(type);
		
		if (type.equalsIgnoreCase("GET")) {
			connect.setDoInput(true);
			connect.setDoOutput(false);
			
		} else if (type.equalsIgnoreCase("PUT")) {
			connect.setDoInput(true); 
			connect.setDoOutput(true);
		}
		
		connect.connect();
		
		if (connect.getResponseCode() != 200 && connect.getResponseCode() != 201) {
			throw new RuntimeException("HTTP GET Request Failed with Error code : "
					+ connect.getResponseCode() + " ERROR: " +  connect.getResponseMessage());
		}
		
		return connect;
	}
	
	public static void put(String attribute_name, String value)
			throws IllegalArgumentException, InvalidInputValueException {
		// Perform Dimension checks. En check if we get a http response status
		// code of "OK".
		// String[] xml_attribute_names

		String link = "";
		boolean match = false;
		String[] valid_names = { "day", "time", "currentTemperature",
				"dayTemperature", "nightTemperature", "weekProgramState" };
		for (int i = 0; i < valid_names.length; i++) {
			if (attribute_name.equalsIgnoreCase(valid_names[i])) {
				match = true;
				link = HeatingSystem.BASE_ADDRESS + "/" + valid_names[i];
				break;
			}
		}

		if (!match) {
			throw new IllegalArgumentException("Invalid attribute name: "
					+ attribute_name);
		}

		String tag_name = "";
		if (attribute_name.equals("day")) {
			tag_name = "current_day";
			String[] valid_days = { "Monday", "Tuesday", "Wednesday",
					"Thursday", "Friday", "Saturday", "Sunday" };
			for (int i = 0; i < valid_days.length; i++) {
				if (value.equalsIgnoreCase(valid_days[i])) {
					// Do not make it case-sensitive but adjust to the name with
					// a Capital letter to be sure it has the right format.
					value = valid_days[i];
					break;
				}
				// Invalid day value.
				if (i == valid_days.length - 1)
					throw new InvalidInputValueException("Not a correct day: "
							+ value);
			}
		} else if (attribute_name.equals("time")) {
			tag_name = "time";

			if (!Switch.isValidTimeSyntax(value)) {
				throw new InvalidInputValueException("Invalid Time Value: "
						+ value);
			}
		} else if (attribute_name.equals("currentTemperature")) {
			tag_name = "current_temperature";
			inTemperatureBoundaries(value);
		} else if (attribute_name.equals("dayTemperature")) {
			tag_name = "day_temperature";
			inTemperatureBoundaries(value);
		} else if (attribute_name.equals("nightTemperature")) {
			tag_name = "night_temperature";
			inTemperatureBoundaries(value);
		} else if (attribute_name.equals("weekProgramState")) {
			tag_name = "week_program_state";
			value = value.toLowerCase(Locale.US); // Must be spelled in lower
													// case, assure this.
			if (!value.equals("on") && !value.equals("off")) {
				throw new InvalidInputValueException(
						"Value for weekProgramState should be \"on\" or \"off\"");
			}
		}

		// If the script gets here the attribute names & value is corresponding
		// and correct. E.g. like temperature between the boundaries
		// of 5 till 30 degrees.

		// Output string.
		String output = "<" + tag_name + ">" + value + "</" + tag_name + ">";

		DataOutputStream out = null;
		try {
			HttpURLConnection connect = getHttpConnection(link, "PUT");

			out = new DataOutputStream(connect.getOutputStream());
			out.writeBytes(output);
			out.flush();

			String response = connect.getResponseMessage();
			int responseCode = connect.getResponseCode();
			System.out.println("Http Response: " + response);
			System.out.println("Http Response Code: " + responseCode);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close(); // Close stream.
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static String createThermostat(String thermostatId) {
		try {
			
			HttpURLConnection httpConnection = getHttpConnection(targetURL + thermostatId, "PUT");

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
				(httpConnection.getInputStream())));

			String output;
			System.out.println("Output from Server:  \n");

			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output);
			}

			httpConnection.disconnect();
			return output;

		  } catch (MalformedURLException e) {

			e.printStackTrace();
			return "err";
			

		  } catch (IOException e) {

			e.printStackTrace();
			return "err";

		  }
	}
	
	public static String viewThermostat(String thermostatId) {
		try {
			
			HttpURLConnection httpConnection = getHttpConnection(targetURL + thermostatId, "GET");

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
				(httpConnection.getInputStream())));

			String output;
			System.out.println("Output from Server:  \n");

			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output);
			}

			httpConnection.disconnect();
			return output;

		  } catch (MalformedURLException e) {

			e.printStackTrace();
			return "err";
			

		  } catch (IOException e) {

			e.printStackTrace();
			return "err";

		  }
	}
	
	public static String viewWeekProgram(String thermostatId) {
		try {
			
			HttpURLConnection httpConnection = getHttpConnection(targetURL + thermostatId + "/weekProgram", "GET");

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
				(httpConnection.getInputStream())));

			String output;
			System.out.println("Output from Server:  \n");

			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output);
			}

			httpConnection.disconnect();
			return output;

		  } catch (MalformedURLException e) {

			e.printStackTrace();
			return "err";
			

		  } catch (IOException e) {

			e.printStackTrace();
			return "err";

		  }
	}
	
	public static String viewDay(String thermostatId) {
		try {
			
			HttpURLConnection httpConnection = getHttpConnection(targetURL + thermostatId + "/day", "GET");

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
				(httpConnection.getInputStream())));

			String output;
			String completeMessage = "";
			System.out.println("Output from Server:  \n");

			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output);
				completeMessage.concat(output);
			}

			httpConnection.disconnect();
			return completeMessage;

		  } catch (MalformedURLException e) {

			e.printStackTrace();
			return "err";
			

		  } catch (IOException e) {

			e.printStackTrace();
			return "err";

		  }
	}
	
	public static String manageThermostatStatus(String thermostatId, String attribute_name, String value)
			throws IOException, MalformedURLException, UnknownHostException,
			FileNotFoundException, InvalidInputValueException {
		
		String link = "";
		
		boolean match = false;
		String[] valid_names = { "day", "time", "currentTemperature",
				"dayTemperature", "nightTemperature", "weekProgramState" };
		for (int i = 0; i < valid_names.length; i++) {
			if (attribute_name.equalsIgnoreCase(valid_names[i])) {
				match = true;
				link = targetURL + thermostatId + "/" + valid_names[i];
				break;
			}
		}

		if (!match) {
			throw new IllegalArgumentException("Invalid attribute name: "
					+ attribute_name);
		}

		String tag_name = "";
		if (attribute_name.equals("day")) {
			tag_name = "current_day";
			String[] valid_days = { "Monday", "Tuesday", "Wednesday",
					"Thursday", "Friday", "Saturday", "Sunday" };
			for (int i = 0; i < valid_days.length; i++) {
				if (value.equalsIgnoreCase(valid_days[i])) {
					// Do not make it case-sensitive but adjust to the name with
					// a Capital letter to be sure it has the right format.
					value = valid_days[i];
					break;
				}
				// Invalid day value.
				if (i == valid_days.length - 1)
					throw new InvalidInputValueException("Not a correct day: "
							+ value);
			}
		} else if (attribute_name.equals("time")) {
			tag_name = "time";

			if (!Switch.isValidTimeSyntax(value)) {
				throw new InvalidInputValueException("Invalid Time Value: "
						+ value);
			}
		} else if (attribute_name.equals("currentTemperature")) {
			tag_name = "current_temperature";
			inTemperatureBoundaries(value);
		} else if (attribute_name.equals("dayTemperature")) {
			tag_name = "day_temperature";
			inTemperatureBoundaries(value);
		} else if (attribute_name.equals("nightTemperature")) {
			tag_name = "night_temperature";
			inTemperatureBoundaries(value);
		} else if (attribute_name.equals("weekProgramState")) {
			tag_name = "week_program_state";
			value = value.toLowerCase(Locale.US); // Must be spelled in lower
													// case, assure this.
			if (!value.equals("on") && !value.equals("off")) {
				throw new InvalidInputValueException(
						"Value for weekProgramState should be \"on\" or \"off\"");
			}
		}
		
		try {
			
			HttpURLConnection httpConnection = getHttpConnection(targetURL + thermostatId + "/", "PUT");

			BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
				(httpConnection.getInputStream())));

			String output;
			System.out.println("Output from Server:  \n");

			while ((output = responseBuffer.readLine()) != null) {
				System.out.println(output);
			}

			httpConnection.disconnect();
			return output;

		  } catch (MalformedURLException e) {

			e.printStackTrace();
			return "err";
			

		  } catch (IOException e) {

			e.printStackTrace();
			return "err";

		  }
	}
	
	
	
	
	
	public static void main(String[] args) throws MalformedURLException, UnknownHostException, FileNotFoundException, IOException, InvalidInputValueException {
		
		//HeatingSystem.BASE_ADDRESS = targetURL + "/test";
		
		//manageThermostat("test","PUT");
		//manageThermostatStatus("test", "day", "Sunday");
		createThermostat("test");
		
		//manageThermostatStatus("test", "weekProgramState", "on");
		
		//viewThermostat("test");

		//HeatingSystem.put("day", "Monday");
		
		//System.out.println(viewDay("test"));
	}
}