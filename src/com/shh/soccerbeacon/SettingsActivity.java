package com.shh.soccerbeacon;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SettingsActivity extends ActionBarActivity
{	
	EditText etDisplayMargin;
	EditText etScanInterval;
	EditText etRunningSumCount;
	EditText etOutlierDistance;
	EditText etOutlierTrimFactor;
	
	Button btnSave;
	
	Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		mContext = this;
		
		etDisplayMargin = (EditText) findViewById(R.id.etDisplayMargin);
		etScanInterval = (EditText) findViewById(R.id.etScanInterval);
		etRunningSumCount = (EditText) findViewById(R.id.etRunningSumCount);
		etOutlierDistance = (EditText) findViewById(R.id.etOutlierDistance);
		etOutlierTrimFactor = (EditText) findViewById(R.id.etOutlierTrimFactor);
		
		btnSave = (Button) findViewById(R.id.btnSave);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		int displayMargin = sharedPref.getInt("DisplayMargin", 10);
		int scanInterval = sharedPref.getInt("ScanInterval", 500);
		int runningSumCount = sharedPref.getInt("RunningSumCount", 10);
		float outlierDistance = sharedPref.getFloat("OutlierDistance", 5f);
		float outlierTrimFactor = sharedPref.getFloat("OutlierTrimFactor", 0.5f);		
		
		etDisplayMargin.setText("" + displayMargin);
		etScanInterval.setText("" + scanInterval);
		etRunningSumCount.setText("" + runningSumCount);
		etOutlierDistance.setText("" + outlierDistance);
		etOutlierTrimFactor.setText("" + outlierTrimFactor);
		
		btnSave.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) 
			{
				int displayMargin, scanInterval, runningSumCount;
				float outlierDistance, outlierTrimFactor;
				
				try {				
					displayMargin = Integer.parseInt(etDisplayMargin.getText().toString());
				}
				catch (NumberFormatException e)	{
					etDisplayMargin.setError("Please enter a valid number");
					return;
				}
				
				try {				
					scanInterval = Integer.parseInt(etScanInterval.getText().toString());
				}
				catch (NumberFormatException e)	{
					etScanInterval.setError("Please enter a valid number");
					return;
				}
				
				try {				
					runningSumCount = Integer.parseInt(etRunningSumCount.getText().toString());
				}
				catch (NumberFormatException e)	{
					etRunningSumCount.setError("Please enter a valid number");
					return;
				}
				
				try {				
					outlierDistance = Float.parseFloat(etOutlierDistance.getText().toString());
				}
				catch (NumberFormatException e)	{
					etOutlierDistance.setError("Please enter a valid number");
					return;
				}
				
				try {				
					outlierTrimFactor = Float.parseFloat(etOutlierTrimFactor.getText().toString());
				}
				catch (NumberFormatException e)	{
					etOutlierTrimFactor.setError("Please enter a valid number");
					return;
				}
				
				if (displayMargin < 0)
				{
					etDisplayMargin.setError("Display Margin must be >= 0px");
					return;
				}
				
				if (scanInterval < 100)
				{
					etScanInterval.setError("Scan Interval must be >= 100ms");
					return;
				}
				
				if (runningSumCount < 1)
				{
					etRunningSumCount.setError("Running Sum Count Limit must be >= 1");
					return;
				}

				if (outlierDistance < 1)
				{
					etOutlierDistance.setError("Outlier Distance must be >= 1");
					return;
				}
				
				if (outlierTrimFactor < 0 || outlierTrimFactor > 1)
				{
					etOutlierTrimFactor.setError("Outlier Trim Factor must be >= 0 and <= 1.0");
					return;
				}
				
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
				Editor editor = sharedPref.edit();
				
				editor.putInt("DisplayMargin", displayMargin);
				editor.putInt("ScanInterval", scanInterval);
				editor.putInt("RunningSumCount", runningSumCount);
				editor.putFloat("OutlierDistance", outlierDistance);
				editor.putFloat("OutlierTrimFactor", outlierTrimFactor);
				
				editor.apply();		
				
				Toast toast = Toast.makeText(mContext, "Settings saved", Toast.LENGTH_SHORT);
				toast.show();
				
				finish();
			}			
		});
	}
}
