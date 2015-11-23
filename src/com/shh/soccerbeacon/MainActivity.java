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

public class MainActivity extends ActionBarActivity 
{
	private Context mContext;
	private Button btnSetBeaconLocations;
	private Button btnSetFieldDimensions;

	private Button btnStart;
	private Button btnTest;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private FieldView fvFieldView;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		setContentView(R.layout.activity_main);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int fieldWidth = sharedPref.getInt("FieldWidth", -1);
		int fieldHeight = sharedPref.getInt("FieldHeight", -1);
				
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
		
		btnStart = (Button) findViewById(R.id.btnStart);
				
		btnTest = (Button) findViewById(R.id.btnTest);
		
		btnTest.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, TestBeaconsActivity.class);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
				
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int fieldWidth = sharedPref.getInt("FieldWidth", -1);
		int fieldHeight = sharedPref.getInt("FieldHeight", -1);
				
		fvFieldView.setFieldWidth(fieldWidth);
		fvFieldView.setFieldHeight(fieldHeight);
		
		if (fieldWidth == -1 && fieldHeight == -1)
		{
			btnSetBeaconLocations.setEnabled(false);
			btnStart.setEnabled(false);
			btnTest.setEnabled(false);
		}
		else
		{
			btnSetBeaconLocations.setEnabled(true);
		}
		
		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
			
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
				
		// need at least two beacon locations
		if (beaconLocationsList.size() < 2)
		{
			btnStart.setEnabled(false);
			btnTest.setEnabled(false);
		}
		else
		{
			btnStart.setEnabled(true);
			btnTest.setEnabled(true);
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
		else if (id == R.id.action_calibration) 
		{
			Intent intent = new Intent(this, CalibrationActivity.class);
			startActivity(intent);
			    
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
