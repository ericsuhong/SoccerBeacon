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
import org.apache.commons.math3.stat.regression.OLSMultipleLinearRegression;

import com.shh.soccerbeacon.adapter.BeaconListAdapter;
import com.shh.soccerbeacon.dto.BeaconListItem;

import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class CalibrationActivity extends ActionBarActivity implements BeaconConsumer, OnClickListener
{
	Context mContext;
	
	private BeaconManager beaconManager;
	
	BeaconListAdapter beaconListAdapter;
	ArrayList<BeaconListItem> beaconList;
	
	ArrayList<Button> buttonArray;
	ArrayList<TextView> tvArray;

	Button btn1m, btn2m, btn3m, btn4m, btn5m, btn6m, btn7m, btn8m, btn9m, btn10m;
	TextView tv1m, tv2m, tv3m, tv4m, tv5m, tv6m, tv7m, tv8m, tv9m, tv10m;
	
	TextView tvCurrentRSSI;
	TextView tvCurrentAvgView;
	
	OLSMultipleLinearRegression ols = new OLSMultipleLinearRegression();
	
	int currentRSSI;
	int sumRSSI = 0;
	int count = 0;
	double avgRSSI;
	
	boolean calibrating = false;
	
	String beaconName;
	int beaconMajor;
	int beaconMinor;
	
	int currentX = -1;
	
	double x[] = new double[10];
	double y[] = new double[10];
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calibration);
		
		mContext = this;
		
		for (int i = 0; i < y.length; i++)
		{
			x[i] = Math.log(i+1);
			y[i] = 0;
		}
		
		y[0] = 56.2903;
		y[1] = 64.2258;
		y[2] = 70.4193;
		y[3] = 75.1562;
		y[4] = 77.8387;
		y[5] = 78.8060;
		y[6] = 80.6770;
		y[7] = 84.5480;
		y[8] = 85.5480;
		y[9] = 87.5800;
		
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
                			avgRSSI = ((double)sumRSSI/count);
                			
                		    y[currentX] = -1 * avgRSSI;	
                		}
                	}           
                	
            		runOnUiThread(new Runnable() {
            		     @Override
            		     public void run() {
            		    	 tvCurrentRSSI.setText("" + currentRSSI);
            		    	 tvCurrentAvgView.setText("" + avgRSSI);
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
					
					currentX = i;
					
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.calibrate_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_submit) 
		{			
			int numCalibrated = 0;
			
			ArrayList<Double> data = new ArrayList<Double>();
			
			for (int i = 0; i < y.length; i++)
			{
				if (y[i] != 0)
				{
					data.add(y[i]);
					data.add(x[i]);
					
					numCalibrated++;
				}				
			}
			
			double[] converted_data = new double[data.size()];
			for (int i = 0; i < data.size(); i++)
			{
				converted_data[i] = data.get(i);
			}			
			
			if (numCalibrated < 5)
			{				
				AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
	            builder1.setMessage("At least 5 locations must be calibrated...");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("OK",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();				
			}
			else
			{
				try 
				{
					Log.e("BEACON", "converted_data length: " + converted_data.length);
					ols.newSampleData(converted_data, converted_data.length/2, 1);
				}
				catch(IllegalArgumentException e)
				{
					Log.e("BEACON", "Can't sample data: " + e.toString());
					e.printStackTrace();
				}
				
				double[] coe = null;
				
				try 
				{
					coe = ols.estimateRegressionParameters();
				}
				catch(Exception e)
				{
					Log.e("BEACON", "Can't estimate parameters: " + e.toString());
					e.printStackTrace();    
				}
				
				final double a = coe[0];
				final double b = coe[1];
				
				AlertDialog.Builder builder1 = new AlertDialog.Builder(mContext);
	            builder1.setMessage("Calculated values:\nA: " + String.format("%.3f", a) + ", B: " + String.format("%.3f", b) + "\n\nDo you want to submit?");
	            builder1.setCancelable(true);
	            builder1.setPositiveButton("SUBMIT",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                	Intent returnIntent = new Intent();
						returnIntent.putExtra("a", a);
						returnIntent.putExtra("b", b);
						returnIntent.putExtra("beaconMajor", beaconMajor);
						returnIntent.putExtra("beaconMinor", beaconMinor);
						setResult(Activity.RESULT_OK, returnIntent);
						finish();
	                }
	            });
	            
	            builder1.setNegativeButton("CANCEL",
	                    new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    dialog.cancel();
	                }
	            });

	            AlertDialog alert11 = builder1.create();
	            alert11.show();
			}

			    
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
