package com.shh.soccerbeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import com.shh.soccerbeacon.adapter.BeaconListAdapter;
import com.shh.soccerbeacon.dto.BeaconListItem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class NearbyBeaconsActivity extends ActionBarActivity implements BeaconConsumer
{
	private ListView lvBeaconList;
	private BeaconManager beaconManager;
	
	private static String UUID = "f7826da6-4fa2-4e98-8024-bc5b71e0893e";
	
	BeaconListAdapter beaconListAdapter;
	ArrayList<BeaconListItem> beaconList;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nearbybeacons);
		
		lvBeaconList = (ListView) findViewById(R.id.lvBeaconList);
		
		beaconList = new ArrayList<BeaconListItem>();

		beaconListAdapter = new BeaconListAdapter(getApplicationContext(), beaconList);
		lvBeaconList.setAdapter(beaconListAdapter);
		
		beaconManager = BeaconManager.getInstanceForApplication(this);
		
		// add iBeacon Layout
		beaconManager.getBeaconParsers().add(new BeaconParser().
	               setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));

		
		// scan for new updates every 500 milliseconds
		// default is 1100 milliseconds...
		beaconManager.setForegroundScanPeriod(2000);
		beaconManager.setForegroundBetweenScanPeriod(0);
		
		beaconManager.setBackgroundScanPeriod(2000);
		beaconManager.setBackgroundBetweenScanPeriod(0);
		
		beaconManager.bind(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.mybeacons, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBeaconServiceConnect() {
		beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override 
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
              
        		Log.i("BEACON", "Beacon STARTED with size: " + beacons.size());
        		
            	if (beacons.size() > 0) 
                {
            		Log.i("BEACON", "Beacon SIZE: " + beacons.size());
            		
            		beaconList = new ArrayList<BeaconListItem>();
            		
                	for (Beacon current_beacon : beacons)
                	{
                		Log.i("BEACON", "Beacon ID1: " + current_beacon.getId1() + " ID2: " + current_beacon.getId2() + " ID3: " + current_beacon.getBluetoothName() + " RSSI: " + current_beacon.getRssi());
                		                		
                		BeaconListItem beacon_item = new BeaconListItem(current_beacon.getBluetoothName(), current_beacon.getRssi());
                		
                		beaconList.add(beacon_item);
                	}           
                	
                	// sort by RSSI
                	Collections.sort(beaconList);
                	
            		runOnUiThread(new Runnable() {
            		     @Override
            		     public void run() {	
            		    	 beaconListAdapter.updateAdapter(beaconList);
            		     }});
                }
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