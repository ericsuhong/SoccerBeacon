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
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class TestBeaconsActivity extends ActionBarActivity
{	
	FieldView fvFieldView;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;

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
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
		
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
		
		fvFieldView.setBeaconLocationsList(beaconLocationsList);
		
		fvFieldView.invalidate();
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
}