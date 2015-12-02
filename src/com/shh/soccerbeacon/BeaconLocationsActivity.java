package com.shh.soccerbeacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import org.json.JSONArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shh.soccerbeacon.adapter.BeaconLocationsListAdapter;
import com.shh.soccerbeacon.dto.BeaconLocationItem;

import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
	
	private int runningSumCount = 10;
		
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
		
		runningSumCount = sharedPref.getInt("RunningSumCount", -1);

		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
			
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
		
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
				
				float xPos_int = Float.parseFloat(xPos);
				float yPos_int = Float.parseFloat(yPos);
				
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
				float fieldWidth = sharedPref.getFloat("FieldWidth", -1f);
				float fieldHeight = sharedPref.getFloat("FieldHeight", -1f);
				
				if (xPos_int > fieldWidth)
				{
					etXpos.setError("X coordinate must be <= " + String.format("%.3f", fieldWidth));
					return;
				}
				
				if (yPos_int > fieldHeight)
				{
					etYpos.setError("Y coordinate must be <= " + String.format("%.3f", fieldWidth));
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
	        	float xPos = data.getFloatExtra("xPos", -1);
	        	float yPos = data.getFloatExtra("yPos", -1);
	        	String beaconName = data.getStringExtra("beaconName");
	        	int beaconMajor = data.getIntExtra("beaconMajor", -1);
	        	int beaconMinor = data.getIntExtra("beaconMinor", -1);
	        	
	        	BeaconLocationItem item = new BeaconLocationItem(xPos, yPos, beaconName, beaconMajor, beaconMinor, runningSumCount);
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