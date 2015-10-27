package com.shh.soccerbeacon;

import java.util.ArrayList;

import com.shh.soccerbeacon.adapter.BeaconListAdapter;
import com.shh.soccerbeacon.dto.BeaconListItem;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MyBeaconsActivity extends ActionBarActivity 
{
	ListView lvBeaconList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mybeacons);
		
		lvBeaconList = (ListView) findViewById(R.id.lvBeaconList);
		
		BeaconListItem a = new BeaconListItem("AZXS1A");
		BeaconListItem b = new BeaconListItem("ZCXVAZ");
		
		ArrayList<BeaconListItem> beaconList = new ArrayList<BeaconListItem>();
		beaconList.add(a);
		beaconList.add(b);
		
		BeaconListAdapter beaconListAdapter = new BeaconListAdapter(getApplicationContext(), beaconList);
		lvBeaconList.setAdapter(beaconListAdapter);
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
}
