package com.shh.soccerbeacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shh.soccerbeacon.adapter.BeaconLocationsListAdapter;
import com.shh.soccerbeacon.adapter.CalibrateListAdapter;
import com.shh.soccerbeacon.dto.BeaconLocationItem;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class CalibrateListActivity extends ActionBarActivity 
{
	Context mContext;
	ListView lvBeaconLocations;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	CalibrateListAdapter calibrateListAdapter;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calibratelist);
		
		mContext = this;
		
		lvBeaconLocations = (ListView) findViewById(R.id.lvBeaconLocations);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		String beaconLocationsJSON = sharedPref.getString("BeaconLocations", "[]");
			
		Gson gson = new Gson();
		Type collectionType = new TypeToken<Collection<BeaconLocationItem>>(){}.getType();
		beaconLocationsList = (ArrayList<BeaconLocationItem>) gson.fromJson(beaconLocationsJSON, collectionType);
		
		calibrateListAdapter = new CalibrateListAdapter(getApplicationContext(), beaconLocationsList);
		lvBeaconLocations.setAdapter(calibrateListAdapter);
		
		lvBeaconLocations.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int index,
					long arg3) {
								
				TextView tvBeaconName = (TextView) v.findViewById(R.id.tvBeaconName);
				TextView tvBeaconMajor = (TextView) v.findViewById(R.id.tvBeaconMajor);
				TextView tvBeaconMinor = (TextView) v.findViewById(R.id.tvBeaconMinor);
				
				String name = tvBeaconName.getText().toString();
				int major = Integer.parseInt(tvBeaconMajor.getText().toString());
				int minor = Integer.parseInt(tvBeaconMinor.getText().toString());
				
				Intent intent = new Intent(mContext, CalibrationActivity.class);
				intent.putExtra("beaconName", name);
				intent.putExtra("beaconMajor", major);
				intent.putExtra("beaconMinor", minor);
								
				startActivityForResult(intent, 1);		
			}			
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	    if (requestCode == 1) 
	    {
	        if (resultCode == RESULT_OK) 
	        {
	        	double a = data.getDoubleExtra("a", 0);
	        	double b = data.getDoubleExtra("b", 0);
	        	
	        	boolean useManual = data.getBooleanExtra("useManual", true);
	        	int major = data.getIntExtra("beaconMajor", -1);
	        	int minor = data.getIntExtra("beaconMinor", -1);
	        	
	        	//Log.i("BEACON", "useManual: " + useManual + ", major: " + major);
	        		        	
	        	for (int i = 0; i < beaconLocationsList.size(); i++)
	        	{        		
	        		BeaconLocationItem beacon = beaconLocationsList.get(i);

	        		if (beacon.getMajor() == major && beacon.getMinor() == minor)
	        		{
	        			if (useManual)
	        			{	        			
		        			beacon.setManual(true);
		        			beacon.setManualA(a);
		        			beacon.setManualB(b);
		        			beacon.setShiftC(0);
	        			}
	        			else
	        			{
	        				beacon.setManual(false);
	        				beacon.setShiftC(0);
	        			}
	        			
	        			break;
	        		}
	        	}
	        	
	        	calibrateListAdapter.notifyDataSetChanged();
	        	
	    		String beaconLocationsJSON = new Gson().toJson(beaconLocationsList);
	    		
	    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
	    		SharedPreferences.Editor editor = sharedPref.edit();
	    		editor.putString("BeaconLocations", beaconLocationsJSON);
	    		editor.apply();
	        }
	    }
	}
}