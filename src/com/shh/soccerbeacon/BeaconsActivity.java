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
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class BeaconsActivity extends ActionBarActivity implements BeaconConsumer
{
	private ListView lvBeaconList;
	private ProgressBar progressBar;
	private BeaconManager beaconManager;
	
	BeaconListAdapter beaconListAdapter;
	ArrayList<BeaconListItem> beaconList;
		
	boolean clickable;
	
	Intent intent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacons);
			
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		lvBeaconList = (ListView) findViewById(R.id.lvBeaconList);
		
		intent = getIntent();
		clickable = intent.getBooleanExtra("clickable", false);
		
		final int xPos = intent.getIntExtra("xPos", -1);
		final int yPos = intent.getIntExtra("yPos", -1);
				
		lvBeaconList.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int arg2, long arg3) {				
				if (clickable)
				{
					TextView tvBeaconName = (TextView) v.findViewById(R.id.tvBeaconName);
					TextView tvBeaconMajor = (TextView) v.findViewById(R.id.tvBeaconMajor);
					TextView tvBeaconMinor = (TextView) v.findViewById(R.id.tvBeaconMinor);
					
					String name = tvBeaconName.getText().toString();
					int major = Integer.parseInt(tvBeaconMajor.getText().toString());
					int minor = Integer.parseInt(tvBeaconMinor.getText().toString());
					
					Intent returnIntent = new Intent();
					returnIntent.putExtra("xPos", xPos);
					returnIntent.putExtra("yPos", yPos);
					returnIntent.putExtra("beaconName", name);
					returnIntent.putExtra("beaconMajor", major);
					returnIntent.putExtra("beaconMinor", minor);
					setResult(Activity.RESULT_OK, returnIntent);
					finish();
				}
			}});
		
		beaconList = new ArrayList<BeaconListItem>();

		beaconListAdapter = new BeaconListAdapter(getApplicationContext(), beaconList);
		lvBeaconList.setAdapter(beaconListAdapter);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		
		beaconList = new ArrayList<BeaconListItem>();
		progressBar.setVisibility(View.VISIBLE);
	    lvBeaconList.setVisibility(View.GONE);
	    beaconListAdapter.updateAdapter(beaconList);
		
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(!mBluetoothAdapter.isEnabled())
        {
          Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          startActivityForResult(enableBtIntent, 1);
        }
		
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
	protected void onStop()
	{
		super.onStop();
		
		beaconManager.unbind(this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.beacons, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		//int id = item.getItemId();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBeaconServiceConnect() {
		beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override 
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) 
            {              
        		Log.i("BEACON", "Beacon STARTED with size: " + beacons.size());
        		
        		beaconList = new ArrayList<BeaconListItem>();
        		
            	if (beacons.size() > 0) 
                {
            		//Log.i("BEACON", "Beacon SIZE: " + beacons.size());            		
            		
                	for (Beacon current_beacon : beacons)
                	{
                		//Log.i("BEACON", "Beacon ID1: " + current_beacon.getId1() + " ID2: " + current_beacon.getId2() + " ID3: " + current_beacon.getBluetoothName() + " RSSI: " + current_beacon.getRssi());
                   		String beaconName = current_beacon.getBluetoothName();
                		int major = current_beacon.getId2().toInt();
                		int minor = current_beacon.getId3().toInt();
                		int rssi = current_beacon.getRssi();
                		
                		String majorminor = major + "-" + minor;
                		
                		// do not show "already-added" beacons
                		if (intent.getExtras().get(majorminor) == null)
                		{
                			BeaconListItem beacon_item = new BeaconListItem(beaconName, major, minor, rssi);
                			beaconList.add(beacon_item);
                		}
                	}           
                	
                	// sort by RSSI
                	Collections.sort(beaconList);
                	
            		runOnUiThread(new Runnable() {
            		     @Override
            		     public void run() {
            		    	 progressBar.setVisibility(View.GONE);
            		    	 lvBeaconList.setVisibility(View.VISIBLE);
            		    	 beaconListAdapter.updateAdapter(beaconList);
            		     }});
                }
            	else
            	{
            		runOnUiThread(new Runnable() {
           		     @Override
           		     public void run() {
           		    	 progressBar.setVisibility(View.VISIBLE);
           		    	 lvBeaconList.setVisibility(View.GONE);
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
