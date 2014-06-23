package com.example.thermostatapp;

import org.thermostatapp.util.HeatingSystem;
import org.thermostatapp.util.WeekProgram;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class WeekProgramActivity extends Activity {

Button getdata, putdata;
	
	ActionBar.Tab tab1, tab2, tab3;
	Fragment fragmentTab1 = new FragmentTab1();
	Fragment fragmentTab2 = new FragmentTab2();
	Fragment fragmentTab3 = new FragmentTab3();
	
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_program);
        
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        
        tab1 = actionBar.newTab().setText("Day");
        tab2 = actionBar.newTab().setText("Night");
        tab3 = actionBar.newTab().setText("Overview");
        
        tab1.setTabListener(new MyTabListener(fragmentTab1));
        tab2.setTabListener(new MyTabListener(fragmentTab2));
        tab3.setTabListener(new MyTabListener(fragmentTab3));
        
        actionBar.addTab(tab1);
        actionBar.addTab(tab2);
        actionBar.addTab(tab3);
        
		getdata = (Button)findViewById(R.id.getdata);
		putdata = (Button)findViewById(R.id.putdata);		
		
		getdata.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							WeekProgram wpg = HeatingSystem.getWeekProgram();
							System.out.println("BASE ADDRESS "+HeatingSystem.BASE_ADDRESS+"\n"+
									"Weekprogram :" + HeatingSystem.getWeekProgram()
									/*"Day "+HeatingSystem.get("day")+
									"time "+HeatingSystem.get("time")+
									"currentTemperature "+HeatingSystem.get("currentTemperature")+
									"dayTemperature "+HeatingSystem.get("dayTemperature")+
									"nightTemperature "+HeatingSystem.get("nightTemperature")+
									"weekProgramState "+HeatingSystem.get("weekProgramState")*/
									);			
							/* To work with program use 
							 * WeekProgram wpg = HeatingSystem.getWeekProgram();
							 */
						} catch (Exception e) {
							System.err.println("Error from getdata "+e);
						}
					}
				}).start();	
			}
		});
		
    }
}
