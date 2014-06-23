package com.example.thermostatapp;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;
import org.thermostatapp.util.Switch;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ActionBar.LayoutParams;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentTab1 extends Fragment {
	
	WeekProgram wkpg = new WeekProgram();
	ArrayList<Switch> switches = new ArrayList<Switch>();
	String day = "";
	String output = "";
	int inte = new Integer(0); // cant you just assign this integer to 0? like: int inte = 0;
	
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	                           Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.tab, container, false);
		
        new Thread(new Runnable() {
            public void run() {
              try {
				try {
					wkpg = HeatingSystem.getWeekProgram();
					String day = HeatingSystem.get("day");
					switches = HeatingSystem.getSwitchesDay(wkpg, day, "day");
					
					output = "";
					
					for (int i = 0; i < switches.size(); i++) {
						output = output + (Integer.toString(i) + ": ");
						output = output + (switches.get(i).getTime() + " status: " + Boolean.toString(switches.get(i).state) +"\n");
						System.out.println(output);
					}
				
			    	        
			    	        	
					inte = switches.size();
					
					
				} catch (CorruptWeekProgramException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            }
            
          }).start();  

		return view;
	  }
	  
	  public void onViewCreated(View view, Bundle savedInstanceState){
	        super.onViewCreated(view, savedInstanceState);
	        // initialise your views
	        
	        RelativeLayout.LayoutParams params1 = new 
	    	        RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT); 
	    	        params1.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
	    			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
	    	                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

            TextView textview = (TextView) view.findViewById(R.id.tabtextview);  
            textview.setLayoutParams(params1);
      		textview.setText(output);
	
		
	    }
    }
