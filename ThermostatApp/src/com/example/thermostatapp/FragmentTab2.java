package com.example.thermostatapp;

import java.io.IOException;
import java.util.ArrayList;

import org.thermostatapp.util.CorruptWeekProgramException;
import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.Switch;
import org.thermostatapp.util.WeekProgram;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FragmentTab2 extends Fragment {
	WeekProgram wkpg = new WeekProgram();
	ArrayList<Switch> switches = new ArrayList<Switch>();
	String day = "";
	
	  public View onCreateView(LayoutInflater inflater, ViewGroup container, 
	                           Bundle savedInstanceState){
		View view = inflater.inflate(R.layout.tab, container, false);
		
        new Thread(new Runnable() {
            public void run() {
              try {
				try {
					String day = HeatingSystem.get("day");
					switches = HeatingSystem.getSwitchesDay(wkpg, day, "day");
					
					
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

	    			final LinearLayout lm = (LinearLayout) view.findViewById(R.id.linearMain);
	    			
	    			int num = 1;
					for (int i = 0; i < switches.size(); i++) {
						
						if (switches.get(i).getType().equals("night")) {
							
							LinearLayout ll = new LinearLayout(view.getContext());
				            ll.setOrientation(LinearLayout.HORIZONTAL);
				            
				            String status = "OFF";
				            
				            if (switches.get(i).getState() == true) {
				            	status = "ON";
				            }
							
							Button changeSwitchButton = new Button(view.getContext());
							changeSwitchButton.setId(i + 100);
							changeSwitchButton.setText("Set Time");
							changeSwitchButton.setBackgroundColor(Color.rgb(70, 80, 90));
							
							TextView textview = new TextView(view.getContext());
							textview.setLayoutParams(params1);
							textview.setId(i + 50);
							textview.setText("Switch " + num + ": " + switches.get(i).getTime() + " - Status: " + status);
							
							ll.addView(textview);

							ll.addView(changeSwitchButton,params);
							
//							changeSwitchButton = ((Button) view.findViewById(i + 100));
//							changeSwitchButton.setOnClickListener(new View.OnClickListener() {
//						        public void onClick(View view) {
//						            Toast.makeText(view.getContext(),
//						                    "Button clicked index = " + id_, Toast.LENGTH_SHORT)
//						                    .show();
							
							lm.addView(ll);
							num++;
						}
					}
			

//            TextView textview = (TextView) view.findViewById(R.id.tabtextview);  
//            textview.setLayoutParams(params1);
//      		textview.setText(output);
//	
		
	    }
	}