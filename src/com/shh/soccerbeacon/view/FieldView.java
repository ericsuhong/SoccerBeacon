package com.shh.soccerbeacon.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.shh.soccerbeacon.R;
import com.shh.soccerbeacon.dto.BeaconLocationItem;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FieldView extends View 
{
	private int margin = 0;
	
	private int fieldWidth = -1;
	private int fieldHeight = -1;
	
	String fieldColor = "#35AE2F";
	String beaconColor = "#FF0000";
	String beaconRangeColor = "#000000";
	
	float beaconRadius = 15;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private float scaleFactor;
	
    public FieldView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
    }   
    
    @Override
	protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	int bitmapWidth = canvas.getWidth();
    	int bitmapHeight = canvas.getHeight();;
    	
    	// always redraw the whole field
		if (fieldWidth != -1 && fieldHeight != -1)
		{
			float widthRatio = ((float)bitmapWidth-margin*2) / fieldWidth;
    		float heightRatio = ((float)bitmapHeight-margin*2) / fieldHeight;
    		        
    		float adjustedFieldWidth = 0;
    		float adjustedFieldHeight = 0;
    		        
    		// scale to width
    		if (widthRatio < heightRatio)
	        {
    			adjustedFieldWidth = fieldWidth * widthRatio;
    			adjustedFieldHeight = fieldHeight * widthRatio;
	        	
	        	scaleFactor = widthRatio;
	        }	   
	        else
	        {
	        	adjustedFieldWidth = fieldWidth * heightRatio;
	        	adjustedFieldHeight = fieldHeight * heightRatio;
	        	
	        	scaleFactor = heightRatio;
	        }	        
    			    	
			Paint fieldPaint = new Paint();
			fieldPaint.setColor(Color.parseColor(fieldColor));
	                       
	       canvas.drawRect(((bitmapWidth-2*margin)-adjustedFieldWidth)/2+margin, ((bitmapHeight-2*margin)-adjustedFieldHeight)/2+margin, adjustedFieldWidth + ((bitmapWidth-2*margin)-adjustedFieldWidth)/2+margin, adjustedFieldHeight+((bitmapHeight-2*margin)-adjustedFieldHeight)/2+margin, fieldPaint);
       
			Log.i("canvas", "bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);
			Log.i("canvas", "fieldWidth: " + fieldWidth + ", fieldHeight: " + fieldHeight);
			Log.i("canvas", "widthRatio: " + widthRatio + ", heightRatio: " + heightRatio);
			Log.i("canvas", "adjustedFieldWidth: " + adjustedFieldWidth + ", adjustedFieldHeight: " + adjustedFieldHeight);
	       
			
		   // sort by distance
		   Collections.sort(beaconLocationsList);
	       
	       if (beaconLocationsList != null)
	       {
	    	   Log.i("BEACON", "-------------------------");
	    	   for (int i = 0 ; i < beaconLocationsList.size(); i++)
	    	   {
	    		   Paint beaconPaint = new Paint();
	    		   beaconPaint.setColor(Color.parseColor(beaconColor));
	    		   
	    		   BeaconLocationItem beacon = beaconLocationsList.get(i);
	    		   float x = beacon.getX() * scaleFactor;
	    		   float y = beacon.getY() * scaleFactor;
	    		   
	    		   canvas.drawCircle(((bitmapWidth-2*margin)-adjustedFieldWidth)/2 + x + margin, ((bitmapHeight-2*margin)-adjustedFieldHeight)/2+ y + margin, beaconRadius, beaconPaint);
	    	 
	    		   float distance = beacon.getDistance();
	    		   Log.i("BEACON", "distance: " + distance);
	    		   
	    		   if (distance != -1)
	    		   {
	    			   Paint beaconRangePaint = new Paint();
	    			   beaconRangePaint.setColor(Color.parseColor(beaconRangeColor));
	    			   beaconRangePaint.setStyle(Paint.Style.STROKE);
	    			   beaconRangePaint.setStrokeWidth(4f);
	    			  
	    			   canvas.drawCircle(((bitmapWidth-2*margin)-adjustedFieldWidth)/2 + x + margin, ((bitmapHeight-2*margin)-adjustedFieldHeight)/2+ y + margin, distance, beaconRangePaint);
	    		   }
	    	   }	       
	       }
		}
    }
            
    public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public int getFieldWidth() {
		return fieldWidth;
	}

	public void setFieldWidth(int fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	public int getFieldHeight() {
		return fieldHeight;
	}

	public void setFieldHeight(int fieldHeight) {
		this.fieldHeight = fieldHeight;
	}

	public ArrayList<BeaconLocationItem> getBeaconLocationsList() {
		return beaconLocationsList;
	}

	public void setBeaconLocationsList(
			ArrayList<BeaconLocationItem> beaconLocationsList) {
		this.beaconLocationsList = beaconLocationsList;
	}

	public float getBeaconRadius() {
		return beaconRadius;
	}

	public void setBeaconRadius(float beaconRadius) {
		this.beaconRadius = beaconRadius;
	}
}