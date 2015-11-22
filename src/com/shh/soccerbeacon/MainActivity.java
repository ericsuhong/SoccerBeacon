package com.shh.soccerbeacon;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity 
{
	private Context mContext;
	private Button btnSetBeaconLocations;
	private Button btnSetFieldDimensions;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mContext = this;
		setContentView(R.layout.activity_main);
		
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
