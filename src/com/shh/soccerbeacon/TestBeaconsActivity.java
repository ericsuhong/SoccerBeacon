package com.shh.soccerbeacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shh.soccerbeacon.dto.BeaconListItem;
import com.shh.soccerbeacon.dto.BeaconLocationItem;
import com.shh.soccerbeacon.view.FieldView;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;

public class TestBeaconsActivity extends ActionBarActivity implements BeaconConsumer
{	
	FieldView fvFieldView;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private BeaconManager beaconManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int fieldWidth = sharedPref.getInt("FieldWidth", -1);
		int fieldHeight = sharedPref.getInt("FieldHeight", -1);
				
		fvFieldView = (FieldView) findViewById(R.id.fvFieldView);
		fvFieldView.setMargin(30);	
		fvFieldView.setFieldWidth(fieldWidth);
		fvFieldView.setFieldHeight(fieldHeight);
		
		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
		
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
		
		fvFieldView.setBeaconLocationsList(beaconLocationsList);	
		
		// start listening to the beacons...
		beaconManager = BeaconManager.getInstanceForApplication(this);
		
		// add iBeacon Layout
		beaconManager.getBeaconParsers().add(new BeaconParser().
	               setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
		
		// scan for new updates every 500 milliseconds
		// default is 1100 milliseconds...
		beaconManager.setForegroundScanPeriod(500);
		beaconManager.setForegroundBetweenScanPeriod(0);
		
		beaconManager.setBackgroundScanPeriod(500);
		beaconManager.setBackgroundBetweenScanPeriod(0);
		
		beaconManager.bind(this);	
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.testbeacons_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_start)
		{
			
		}
		else if (id == R.id.option_show_circles) 
		{
			if (item.isChecked())
			{	
				item.setChecked(false);
			}
			else
			{
				item.setChecked(true);
			}
			
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBeaconServiceConnect() {
		beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override 
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) 
            {     
        		Log.i("BEACON", "Beacon STARTED with size: " + beacons.size());
        		        		
            	if (beacons.size() > 0) 
                {
            		//Log.i("BEACON", "Beacon SIZE: " + beacons.size());            		
            		
                	for (Beacon current_beacon : beacons)
                	{
                		//Log.i("BEACON", "Beacon ID1: " + current_beacon.getId1() + " ID2: " + current_beacon.getId2() + " ID3: " + current_beacon.getBluetoothName() + " RSSI: " + current_beacon.getRssi());
                		int major = current_beacon.getId2().toInt();
                		int minor = current_beacon.getId3().toInt();
                		int RSSI = current_beacon.getRssi();
                		
                		// search through beacon locations to see if this beacon is registered in the field
                		for (int i = 0; i < beaconLocationsList.size(); i++)
                		{
                			BeaconLocationItem beaconLocation = beaconLocationsList.get(i);
                			if (beaconLocation.getMajor() == major && beaconLocation.getMinor() == minor)
                			{         				
                				beaconLocation.setPrevRSSI(beaconLocation.getRSSI());
                				beaconLocation.setPrevDetectedTime(beaconLocation.getLastDetectedTime());
                				
                				long timestamp = System.currentTimeMillis();
                				beaconLocation.setRSSI(RSSI);                				
                				beaconLocation.setLastDetectedTime(timestamp);
                			}
                		}       		
                	}       
                }
            	
            	runOnUiThread(new Runnable() {
          		     @Override
          		     public void run() {
          		    	fvFieldView.invalidate();
          		     }
            	});
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("com.shh.soccerbeaconRegion", null, null, null));
        } catch (RemoteException e)
        {
    		Log.e("BEACON", "REMOTE ERROR" + e.getMessage());
        }		
	}
}