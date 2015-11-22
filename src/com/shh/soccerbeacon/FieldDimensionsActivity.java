package com.shh.soccerbeacon;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shh.soccerbeacon.dto.BeaconLocationItem;
import com.shh.soccerbeacon.dto.FieldData;

import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class FieldDimensionsActivity extends ActionBarActivity
{	
	Context mContext;
	
	EditText etFieldWidth;
	EditText etFieldHeight;
	Button btnSetDimensions;
	
	int fieldWidth;
	int fieldHeight;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fielddimensions);
		
		mContext = this;		
		
		etFieldWidth = (EditText) findViewById(R.id.etFieldWidth);
		etFieldHeight = (EditText) findViewById(R.id.etFieldHeight);
		btnSetDimensions = (Button) findViewById(R.id.btnSetDimensions);
		
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
		int fieldWidth = sharedPref.getInt("FieldWidth", -1);
		int fieldHeight = sharedPref.getInt("FieldHeight", -1);
		
		if (fieldWidth != -1)
			etFieldWidth.setText(fieldWidth + "");
		
		if (fieldHeight != -1)
			etFieldHeight.setText(fieldHeight + "");
		
		btnSetDimensions.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v) {
				String fieldWidth = etFieldWidth.getText().toString();
				String fieldHeight = etFieldHeight.getText().toString();
				
				if (fieldWidth == null ||fieldWidth.trim().equals(""))
				{
					etFieldWidth.setError("Field Width is empty");
					return;
				}
				
				if (fieldHeight == null || fieldHeight.trim().equals(""))
				{
					etFieldHeight.setError("Field Height is empty");
					return;
				}
				
				int fieldWidth_int = Integer.parseInt(fieldWidth);
				int fieldHeight_int = Integer.parseInt(fieldHeight);
				
				if (fieldWidth_int <= 0)
				{
					etFieldWidth.setError("Field Width must be greater than 0");
					return;
				}
				
				if (fieldHeight_int <= 0)
				{
					etFieldHeight.setError("Field Height must be greater than 0");
					return;
				}
				
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString("BeaconLocations", "[]"); // clear beacon locations
				editor.putInt("FieldWidth", fieldWidth_int);
				editor.putInt("FieldHeight", fieldHeight_int);
				editor.commit();
								
				finish();
			}			
		});	
	}
}