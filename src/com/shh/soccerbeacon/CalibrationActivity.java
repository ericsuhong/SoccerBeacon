package com.shh.soccerbeacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import com.shh.soccerbeacon.adapter.BeaconListAdapter;
import com.shh.soccerbeacon.dto.BeaconListItem;

import android.support.v7.app.ActionBarActivity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CalibrationActivity extends ActionBarActivity implements BeaconConsumer, OnClickListener
{
	private BeaconManager beaconManager;
	
	BeaconListAdapter beaconListAdapter;
	ArrayList<BeaconListItem> beaconList;
	
	ArrayList<Button> buttonArray;
	ArrayList<TextView> tvArray;

	Button btn1m, btn2m, btn3m, btn4m, btn5m, btn6m, btn7m, btn8m, btn9m, btn10m;
	TextView tv1m, tv2m, tv3m, tv4m, tv5m, tv6m, tv7m, tv8m, tv9m, tv10m;
	
	TextView tvCurrentRSSI;
	TextView tvCurrentAvgView;
	
	int currentRSSI;
	int sumRSSI = 0;
	int count = 0;
	
	boolean calibrating = false;
	
	String beaconName;
	int beaconMajor;
	int beaconMinor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calibration);

		Intent intent = getIntent();
		beaconName = intent.getStringExtra("beaconName");
		beaconMajor = intent.getIntExtra("beaconMajor", -1);
		beaconMinor = intent.getIntExtra("beaconMinor", -1);
		
		TextView tvBeaconName = (TextView) findViewById(R.id.tvBeaconName);
		tvBeaconName.setText(beaconName);
		
		TextView tvMajorMinor = (TextView) findViewById(R.id.tvMajorMinor);
		tvMajorMinor.setText(beaconMajor + ", " + beaconMinor);
		
		tvCurrentRSSI = (TextView) findViewById(R.id.tvCurrentRSSI);
		
		btn1m = (Button) findViewById(R.id.btn1m);
		btn2m = (Button) findViewById(R.id.btn2m);
		btn3m = (Button) findViewById(R.id.btn3m);
		btn4m = (Button) findViewById(R.id.btn4m);
		btn5m = (Button) findViewById(R.id.btn5m);
		btn6m = (Button) findViewById(R.id.btn6m);
		btn7m = (Button) findViewById(R.id.btn7m);
		btn8m = (Button) findViewById(R.id.btn8m);
		btn9m = (Button) findViewById(R.id.btn9m);
		btn10m = (Button) findViewById(R.id.btn10m);
		
		tv1m = (TextView) findViewById(R.id.tv1mAvg);
		tv2m = (TextView) findViewById(R.id.tv2mAvg);
		tv3m = (TextView) findViewById(R.id.tv3mAvg);
		tv4m = (TextView) findViewById(R.id.tv4mAvg);
		tv5m = (TextView) findViewById(R.id.tv5mAvg);
		tv6m = (TextView) findViewById(R.id.tv6mAvg);
		tv7m = (TextView) findViewById(R.id.tv7mAvg);
		tv8m = (TextView) findViewById(R.id.tv8mAvg);
		tv9m = (TextView) findViewById(R.id.tv9mAvg);
		tv10m = (TextView) findViewById(R.id.tv10mAvg);
		
		buttonArray = new ArrayList<Button>();
		buttonArray.add(btn1m);
		buttonArray.add(btn2m);
		buttonArray.add(btn3m);
		buttonArray.add(btn4m);
		buttonArray.add(btn5m);
		buttonArray.add(btn6m);
		buttonArray.add(btn7m);
		buttonArray.add(btn8m);
		buttonArray.add(btn9m);
		buttonArray.add(btn10m);
		
		tvArray = new ArrayList<TextView>();
		tvArray.add(tv1m);
		tvArray.add(tv2m);
		tvArray.add(tv3m);
		tvArray.add(tv4m);
		tvArray.add(tv5m);
		tvArray.add(tv6m);
		tvArray.add(tv7m);
		tvArray.add(tv8m);
		tvArray.add(tv9m);
		tvArray.add(tv10m);		
		
		for (int i = 0; i < buttonArray.size(); i++)
		{
			buttonArray.get(i).setOnClickListener(this);
		}		
		
		beaconManager = BeaconManager.getInstanceForApplication(this);
		
		// add iBeacon Layout
		beaconManager.getBeaconParsers().add(new BeaconParser().
	               setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
		
		// scan for new updates every 1500 milliseconds to allow selecting beacons with more ease....
		beaconManager.setForegroundScanPeriod(1500);
		beaconManager.setForegroundBetweenScanPeriod(0);
		
		beaconManager.setBackgroundScanPeriod(1500);
		beaconManager.setBackgroundBetweenScanPeriod(0);
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
				
		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		
		if(!mBluetoothAdapter.isEnabled())
        {
          Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
          startActivityForResult(enableBtIntent, 1);
        }
		
		if (calibrating)
			beaconManager.bind(this);
	}
	
	@Override
	protected void onStop()
	{
		super.onStop();
		
		if (calibrating)
			beaconManager.unbind(this);
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
                	for (Beacon current_beacon : beacons)
                	{
                		//Log.i("BEACON", "Beacon ID1: " + current_beacon.getId1() + " ID2: " + current_beacon.getId2() + " ID3: " + current_beacon.getBluetoothName() + " RSSI: " + current_beacon.getRssi());
                		int major = current_beacon.getId2().toInt();
                		int minor = current_beacon.getId3().toInt();
                		int rssi = current_beacon.getRssi();
                		
                		if (beaconMajor == major && beaconMinor == minor)
                		{
                			currentRSSI = rssi;
                			sumRSSI+=rssi;
                			count++;
                		}
                	}           
                	
            		runOnUiThread(new Runnable() {
            		     @Override
            		     public void run() {
            		    	 tvCurrentRSSI.setText("" + currentRSSI);
            		    	 tvCurrentAvgView.setText("" + ((double)sumRSSI/count));
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

	@Override
	public void onClick(View v) 
	{
		int id = v.getId();
	
		if (!calibrating)
		{
			for (int i = 0; i < buttonArray.size(); i++)
			{
				Button probeButton = buttonArray.get(i);

				if (probeButton.getId() != id)
				{
					probeButton.setEnabled(false);
				}
				else
				{					
					tvCurrentAvgView = tvArray.get(i);
					sumRSSI = 0;
					count = 0;
					
					beaconManager.bind(this);
				}
			}
			
			calibrating = true;
		}
		else
		{
			for (int i = 0; i < buttonArray.size(); i++)
			{
				Button probeButton = buttonArray.get(i);
				probeButton.setEnabled(true);
			}
			calibrating = false;
			
			beaconManager.unbind(this);
		}
	}
}
