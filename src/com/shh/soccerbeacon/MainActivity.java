package com.shh.soccerbeacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shh.soccerbeacon.dto.BeaconLocationItem;
import com.shh.soccerbeacon.view.FieldView;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity 
{
	private Context mContext;
	private Button btnSetBeaconLocations;
	private Button btnSetFieldDimensions;
	private Button btnCalibrateBeacons;

	private Button btnStart;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private FieldView fvFieldView;
	private TextView tvFieldWidth;
	private TextView tvFieldHeight;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		setContentView(R.layout.activity_main);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		// set default setting values
		int displayMargin = sharedPref.getInt("DisplayMargin", 80);
		int scanInterval = sharedPref.getInt("ScanInterval", 350);
		int runningSumCount = sharedPref.getInt("RunningSumCount", 7);
		float outlierTrimDistance = sharedPref.getFloat("OutlierTrimDistance", 2.0f);
		float outlierTrimFactor = sharedPref.getFloat("OutlierTrimFactor", 0.3f);
		boolean useClosestBeacon = sharedPref.getBoolean("UseClosestBeacon", true);
		float fieldWidth = sharedPref.getFloat("FieldWidth", -1f);
		float fieldHeight = sharedPref.getFloat("FieldHeight", -1f);
		String beaconLocations = sharedPref.getString("BeaconLocations", "[]");
		
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("DisplayMargin", displayMargin);
		editor.putInt("ScanInterval", scanInterval);
		editor.putInt("RunningSumCount", runningSumCount);
		editor.putFloat("OutlierTrimDistance", outlierTrimDistance);
		editor.putFloat("OutlierTrimFactor", outlierTrimFactor);
		editor.putBoolean("useClosestBeacon", useClosestBeacon);
		editor.putFloat("FieldWidth", fieldWidth);
		editor.putFloat("FieldHeight", fieldHeight);
		editor.putString("BeaconLocations", beaconLocations);
		editor.apply();
		
		tvFieldWidth = (TextView) findViewById(R.id.tvFieldWidth);
		tvFieldHeight = (TextView) findViewById(R.id.tvFieldHeight);
		
		fvFieldView = (FieldView) findViewById(R.id.fvFieldView);
		fvFieldView.setMargin(30);	
		fvFieldView.setFieldWidth(fieldWidth);
		fvFieldView.setFieldHeight(fieldHeight);
				
		btnSetFieldDimensions = (Button) findViewById(R.id.btnSetFieldDimensions);
		
		btnSetFieldDimensions.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, FieldDimensionsActivity.class);
				startActivity(intent);
			}
		});
		
		btnSetBeaconLocations = (Button) findViewById(R.id.btnSetBeaconLocations);
		btnSetBeaconLocations.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, BeaconLocationsActivity.class);
				startActivity(intent);
			}
		});
		
		btnCalibrateBeacons = (Button) findViewById(R.id.btnCalibrateBeacons);
		btnCalibrateBeacons.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, CalibrateListActivity.class);
				startActivity(intent);
			}
		});
						
		btnStart = (Button) findViewById(R.id.btnStart);
		
		btnStart.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, StartBeaconsActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
				
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		float fieldWidth = sharedPref.getFloat("FieldWidth", -1);
		float fieldHeight = sharedPref.getFloat("FieldHeight", -1);
				
		fvFieldView.setFieldWidth(fieldWidth);
		fvFieldView.setFieldHeight(fieldHeight);
		
		if (fieldWidth == -1 && fieldHeight == -1)
		{
			btnSetBeaconLocations.setEnabled(false);
			btnStart.setEnabled(false);
			
			tvFieldWidth.setText("Field Dimensions are not set");			
		}
		else
		{
			tvFieldWidth.setText(fieldWidth + "m x ");
			tvFieldHeight.setText(fieldHeight + "m");
			btnSetBeaconLocations.setEnabled(true);
		}
		
		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
					
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
				
		// need at least one beacon locations
		if (beaconLocationsList.size() < 1)
		{
			btnCalibrateBeacons.setEnabled(false);
			btnStart.setEnabled(false);
		}
		else
		{
			btnCalibrateBeacons.setEnabled(true);
			btnStart.setEnabled(true);
		}
		
		fvFieldView.setBeaconLocationsList(beaconLocationsList);
		
		fvFieldView.invalidate();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_beacons) 
		{
			Intent intent = new Intent(this, BeaconsActivity.class);
			intent.putExtra("clickable", false); // "standalone" beacon ranging mode. Cannot click on beacon list
			startActivity(intent);
			    
			return true;
		}
		else if (id == R.id.action_settings) 
		{
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
			    
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
