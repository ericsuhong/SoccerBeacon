package com.shh.soccerbeacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shh.soccerbeacon.adapter.BeaconLocationsListAdapter;
import com.shh.soccerbeacon.dto.BeaconLocationItem;
import com.shh.soccerbeacon.dto.FieldData;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class BeaconLocationsActivity extends ActionBarActivity
{	
	Context mContext;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private ListView lvBeaconLocations;
	BeaconLocationsListAdapter beaconLocationsListAdapter;
	EditText etXpos, etYpos;
	Button btnAddBeacon;	
		
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beaconlocations);
		
		mContext = this;
		
		lvBeaconLocations = (ListView) findViewById(R.id.lvBeaconLocations);
		btnAddBeacon = (Button) findViewById(R.id.btnAddBeacon);
		etXpos = (EditText) findViewById(R.id.etXpos);
		etYpos = (EditText) findViewById(R.id.etYpos);		
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
			
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
		
		/*
		BeaconLocationItem item = new BeaconLocationItem(10,20, "HELLO", 3901, 19349, -30);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);
		beaconLocationsList.add(item);*/
		
		beaconLocationsListAdapter = new BeaconLocationsListAdapter(getApplicationContext(), beaconLocationsList);
		lvBeaconLocations.setAdapter(beaconLocationsListAdapter);
		
		btnAddBeacon.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{
				String xPos = etXpos.getText().toString();
				String yPos = etYpos.getText().toString();
				
				if (xPos == null || xPos.trim().equals(""))
				{
					etXpos.setError("X coordinate is empty");
					return;
				}
				
				if (yPos == null || yPos.trim().equals(""))
				{
					etYpos.setError("Y coordinate is empty");
					return;
				}
				
				int xPos_int = Integer.parseInt(xPos);
				int yPos_int = Integer.parseInt(yPos);
				
				if (xPos_int < 0)
				{
					etXpos.setError("Coordinate must be positive");
					return;
				}
				
				if (yPos_int < 0)
				{
					etYpos.setError("Coordinate must be positive");
					return;
				}
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
				int fieldWidth = sharedPref.getInt("FieldWidth", -1);
				int fieldHeight = sharedPref.getInt("FieldHeight", -1);
				
				if (xPos_int > fieldWidth)
				{
					etXpos.setError("X coordinate must be <= " + fieldWidth);
					return;
				}
				
				if (yPos_int > fieldHeight)
				{
					etYpos.setError("Y coordinate must be <= " + fieldHeight);
					return;
				}
				
				for (int i = 0; i < beaconLocationsList.size(); i++)
				{
					if (beaconLocationsList.get(i).getX() == xPos_int && beaconLocationsList.get(i).getY() == yPos_int)
					{
						etXpos.setError("Duplicate coordinates");
						return;
					}
				}
				
				Intent intent = new Intent(mContext, BeaconsActivity.class);
				intent.putExtra("clickable", true);
				intent.putExtra("xPos", xPos_int);
				intent.putExtra("yPos", yPos_int);
				
				// in order to not show already added beacons...
				for (int i = 0; i < beaconLocationsList.size(); i++)
				{
					String majorminor = beaconLocationsList.get(i).getMajor() + "-" + beaconLocationsList.get(i).getMinor();
					intent.putExtra(majorminor, "true");
				}
				
				startActivityForResult(intent, 1);				    
			}
		});
	}
		
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == 1) {
	        if (resultCode == RESULT_OK) 
	        {
	        	int xPos = data.getIntExtra("xPos", -1);
	        	int yPos = data.getIntExtra("yPos", -1);
	        	String beaconName = data.getStringExtra("beaconName");
	        	int beaconMajor = data.getIntExtra("beaconMajor", -1);
	        	int beaconMinor = data.getIntExtra("beaconMinor", -1);
	        	
	        	BeaconLocationItem item = new BeaconLocationItem(xPos, yPos, beaconName, beaconMajor, beaconMinor);
	        	beaconLocationsList.add(item);
	        	
	        	Collections.sort(beaconLocationsList);

	        	beaconLocationsListAdapter.notifyDataSetChanged();
	        	
	    		String beaconLocationsJSON = new Gson().toJson(beaconLocationsList);
	    		
	    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	    		SharedPreferences.Editor editor = sharedPref.edit();
	    		editor.putString("BeaconLocations", beaconLocationsJSON);
	    		editor.commit();
	        }
	        
	        etXpos.setText("");
	        etYpos.setText("");
	        
	        etXpos.setError(null);
	        etYpos.setError(null);
	        
	        etXpos.clearFocus();
	        etYpos.clearFocus();
	    }
	}
}