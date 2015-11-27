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
	String currentPosColor = "#FFFF00";
	
	float beaconRadius = 15;
	
	int bitmapWidth = 0;
	int bitmapHeight = 0;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private float scaleFactor;
	
	Paint fieldPaint;
	Paint beaconPaint;
	Paint textPaint;
	Paint beaconRangePaint;
	Paint currentPosPaint;
	
    public FieldView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        
        fieldPaint = new Paint();
		fieldPaint.setColor(Color.parseColor(fieldColor));
        
		beaconPaint = new Paint();
		beaconPaint.setColor(Color.parseColor(beaconColor));
		
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK); 
		textPaint.setTextSize(20); 
		
		beaconRangePaint = new Paint();
		beaconRangePaint.setColor(Color.parseColor(beaconRangeColor));
		beaconRangePaint.setStyle(Paint.Style.STROKE);
		beaconRangePaint.setStrokeWidth(4f);
		
		currentPosPaint = new Paint();
		currentPosPaint.setColor(Color.parseColor(currentPosColor));
    }   
    
    @Override
	protected void onDraw(Canvas canvas) {
    	super.onDraw(canvas);
    	
    	bitmapWidth = canvas.getWidth();
    	bitmapHeight = canvas.getHeight();
    	
    	// always redraw the whole field
		if (fieldWidth != -1 && fieldHeight != -1)
		{
			float widthRatio = ((float)bitmapWidth-margin*2) / fieldWidth;
    		float heightRatio = ((float)bitmapHeight-margin*2) / fieldHeight;
    		        
    		// scale to width
    		if (widthRatio < heightRatio)
	        {
	        	scaleFactor = widthRatio;
	        }	   
	        else
	        {	        	
	        	scaleFactor = heightRatio;
	        }	        
    			    	                       
	        canvas.drawRect(calculateAdjustedX(0), calculateAdjustedY(0), calculateAdjustedX(fieldWidth), calculateAdjustedY(fieldHeight), fieldPaint);
       
			Log.i("canvas", "bitmapWidth: " + bitmapWidth + ", bitmapHeight: " + bitmapHeight);
			Log.i("canvas", "fieldWidth: " + fieldWidth + ", fieldHeight: " + fieldHeight);
			Log.i("canvas", "widthRatio: " + widthRatio + ", heightRatio: " + heightRatio);

		   // sort by distance
		   Collections.sort(beaconLocationsList);
	       
	       if (beaconLocationsList != null)
	       {
	    	   //Log.i("BEACON", "-------------------------");
	    	   
	    	   // first loop to draw range circles
	    	   for (int i = 0 ; i < beaconLocationsList.size(); i++)
	    	   {	    		   
	    		   BeaconLocationItem beacon = beaconLocationsList.get(i);
	    		   canvas.drawCircle(calculateAdjustedX(beacon.getX()), calculateAdjustedY(beacon.getY()), beaconRadius, beaconPaint);  		   
	    		   canvas.drawText(beacon.getBeaconName(), calculateAdjustedX(beacon.getX()), calculateAdjustedY(beacon.getY()) + beaconRadius, textPaint); 
	    		   
	    		   float distance = beacon.getDistance();
	    		   //Log.i("BEACON", "distance: " + distance);
	    		   
	    		   if (distance != -1)
	    		   {	    			  
	    			   canvas.drawCircle(calculateAdjustedX(beacon.getX()), calculateAdjustedY(beacon.getY()), calculateAdjustedDistance(distance), beaconRangePaint);
	    		   }
	    	   }
	    	   
	    	   // current "closest" position discovered so far...
	    	   float pointX = -1;
	    	   float pointY = -1;
	    	   
	    	   // second loop to calculate positions
	    	   for (int i = 0 ; i < beaconLocationsList.size(); i++)
	    	   {		   
	    		   BeaconLocationItem firstBeacon = beaconLocationsList.get(i);
	    		   
	    		   if (firstBeacon.getDistance() == -1)
	    			   continue;
	    		   
	    		   // case #1: only 1 circle's distance is available
	    		   // cannot infer any position from it...
	    		   if (i == (beaconLocationsList.size() - 1))
	    		   {
	    			   Log.i("BEACON", "Only one circle available: " + firstBeacon.getBeaconName() + ", " + firstBeacon.getDistance());
	    			   break;
	    		   }
	    		   // case #2: first two small circles are available
	    		   else
	    		   {
	    			   BeaconLocationItem secondBeacon = beaconLocationsList.get(i+1);
	    			   
	    			   Log.i("BEACON", "Two circles available: " + firstBeacon.getBeaconName() + ", " + firstBeacon.getDistance() + ", " + secondBeacon.getBeaconName() + ", " + secondBeacon.getDistance());
	    			   	    	
	    			   double x1 = firstBeacon.getX();
	    			   double y1 = firstBeacon.getY();
	    			   double r1 = firstBeacon.getDistance();
	    			   
	    			   double x2 = secondBeacon.getX();
	    			   double y2 = secondBeacon.getY();	    			   
	    			   double r2 = secondBeacon.getDistance();
	    			   
	    			   double xDistance = Math.abs(x1 - x2);
	    			   double yDistance = Math.abs(y1 - y2);
	    			   	    			   
	    			   double centerDistance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);
	    			   
	    			   // case #2a: two circles intersect
	    			   if (Math.abs(r2-r1) <= centerDistance && centerDistance <= (r2+r1))
	    			   {	 	    				   
	    				   double Zy = (r1*r1-r2*r2)-(x1*x1-x2*x2)-(y1*y1-y2*y2)+2*x1*(x1-x2);
	    				   double Ay = (((y1-y2)*(y1-y2))/((x1-x2)*(x1-x2))) + 1;
	    				   double By = ((Zy *(y1-y2))/((x1-x2)*(x1-x2))) - 2*y1;
	    				   double Cy = ((Zy*Zy)/(4*(x1-x2)*(x1-x2))) + y1*y1 - r1*r1;
	    				   
	    				   float py1_neg = (float) ((-By-Math.sqrt(By*By-4*Ay*Cy))/(2*Ay));
	    				   float py1_pos = (float) ((-By+Math.sqrt(By*By-4*Ay*Cy))/(2*Ay));    
	    				   
	    				   float px1_neg = (float)(((r1*r1-r2*r2)-(x1*x1-x2*x2)-(y1*y1-y2*y2)+2*py1_neg*(y1-y2))/(-2*(x1-x2)));
	    				   float px1_pos = (float)(((r1*r1-r2*r2)-(x1*x1-x2*x2)-(y1*y1-y2*y2)+2*py1_pos*(y1-y2))/(-2*(x1-x2)));
	    				   
    					   canvas.drawCircle(calculateAdjustedX(px1_neg), calculateAdjustedY(py1_neg), beaconRadius, currentPosPaint);	    					   
    					   canvas.drawCircle(calculateAdjustedX(px1_pos), calculateAdjustedY(py1_pos), beaconRadius, currentPosPaint);	
	    				   
	    				   // case #1: two circles intersect at one point
	    				   /*
	    				   if ((r2 + r1) == centerDistance)
	    				   {
	    					   Log.i("BEACON", "Two circles intersect at ONE point");
	    					   break;
	    				   }
	    				   // case #2: two circles intersect at two points
	    				   else
	    				   {
	    					   Log.i("BEACON", "Two circles intersect at TWO point");
	    					   
	    					   // need to take a look at third point...   
	    	    			   break;
	    				   }*/
	    			   }
	    			   // case #2b: two circles do not intersect
	    			   else
	    			   {
	    				   // one circle is contained within another
	    				   if ((centerDistance + r1 < r2) || (centerDistance + r2 < r1))
	    				   {
	    					   Log.i("BEACON", "One circle is contained within another");
	    					   
	    					   // line that goes throught two center points: y = mx + c
	    					   double m = (y2-y1)/(x2-x1);  
	    					   double c = y1 - m*x1;
	    					   
	    					   //Log.i("BEACON", "m: " + m);
	    					   //Log.i("BEACON", "c: " + c);
	    					   
	    					   // intersection point between line and circle 1;  
	    					   double p1 = x1;
	    					   double q1 = y1;
	    					
	    					   // coefficients for quadratic formula equation
	    					   double A1 = m*m+1;
	    					   double B1 = 2*(m*c-m*q1-p1);
	    					   double C1 = q1*q1-r1*r1+p1*p1-2*c*q1+c*c;
	    					   
	    					   float px1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
	    					   float py1_neg = (float) ((m * (-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);

	    					   float px1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	    					   
	    					   float py1_pos = (float) ((m * (-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
	    					   
	    					   // intersection point between line and circle 2;  
	    					   double p2 = x2;
	    					   double q2 = y2;
	    	    			   
	    					   // coefficients for quadratic formula equation
	    					   double A2 = m*m+1;
	    					   double B2 = 2*(m*c-m*q2-p2);
	    					   double C2 = q2*q2-r2*r2+p2*p2-2*c*q2+c*c;
	    					   
	    					   float px2_neg = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    					   float px2_pos = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    					   
	    					   float py2_neg = (float) ((m * (-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
	    					   float py2_pos = (float) ((m * (-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
	    					   
	    					   // pick pair of points that are closer together
	    					   double d_xneg = Math.abs(px1_neg - px2_neg);
	    					   double d_yneg = Math.abs(py1_neg - py2_neg);
	    					   double d_neg = Math.sqrt(d_xneg * d_xneg + d_yneg * d_yneg);
	    					   
	    					   double d_xpos = Math.abs(px1_pos - px2_pos);
	    					   double d_ypos = Math.abs(py1_pos - py2_pos);
	    					   double d_pos = Math.sqrt(d_xpos * d_xpos + d_ypos * d_ypos);
	    					   
	    					   float intersect_x1, intersect_y1, intersect_x2, intersect_y2;
	    					   
	    					   if (d_neg < d_pos)
	    					   {
	    						   intersect_x1 = px1_neg;
	    						   intersect_y1 = py1_neg;
	    						   
	    						   intersect_x2 = px2_neg;
	    						   intersect_y2 = py2_neg;
	    					   }
	    					   else
	    					   {
	    						   intersect_x1 = px1_pos;
	    						   intersect_y1 = py1_pos;
	    						   
	    						   intersect_x2 = px2_pos;
	    						   intersect_y2 = py2_pos;
	    					   }
	    					   
	    					   canvas.drawCircle(calculateAdjustedX(intersect_x1), calculateAdjustedY(intersect_y1), beaconRadius, currentPosPaint);	    					   
	    					   canvas.drawCircle(calculateAdjustedX(intersect_x2), calculateAdjustedY(intersect_y2), beaconRadius, currentPosPaint);	    					   
	    	    			   		    					   
	    					   break;	    					   
	    				   }
	    				   // two circles lie outside of each other
	    				   else
	    				   {
	    					   Log.i("BEACON", "Two circles LIE OUTSIDE OF EACH OTHER");
	    					   
	    					   // line that goes throught two center points: y = mx + c
	    					   double m = (y2-y1)/(x2-x1);  
	    					   double c = y1 - m*x1;
	    					   
	    					   //Log.i("BEACON", "m: " + m);
	    					   //Log.i("BEACON", "c: " + c);
	    					   
	    					   // intersection point between line and circle 1;  
	    					   double p1 = x1;
	    					   double q1 = y1;
	    					
	    					   // coefficients for quadratic formula equation
	    					   double A1 = m*m+1;
	    					   double B1 = 2*(m*c-m*q1-p1);
	    					   double C1 = q1*q1-r1*r1+p1*p1-2*c*q1+c*c;
	    					   
	    					   float px1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
	    					   float py1_neg = (float) ((m * (-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);

	    					   float px1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	    					   
	    					   float py1_pos = (float) ((m * (-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
	    					   
	    					   // pick the point that is closer to the other circle
	    					   double d1_xneg = Math.abs(px1_neg - x2);
	    					   double d1_yneg = Math.abs(py1_neg - y2);
	    					   double d1_neg = Math.sqrt(d1_xneg * d1_xneg + d1_yneg * d1_yneg); 

	    					   double d1_xpos = Math.abs(px1_pos - x2);
	    					   double d1_ypos = Math.abs(py1_pos - y2);
	    					   double d1_pos = Math.sqrt(d1_xpos * d1_xpos + d1_ypos * d1_ypos); 
		    					   
	    					   float intersect_x1, intersect_y1;
	    					   
	    					   if (d1_neg < d1_pos)
	    					   {
	    						   intersect_x1 = px1_neg;
	    						   intersect_y1 = py1_neg;
	    					   }
	    					   else
	    					   {
	    						   intersect_x1 = px1_pos;
	    						   intersect_y1 = py1_pos;
	    					   }
	    					   	    	    			   
	    	    			   // intersection point between line and circle 2;  
	    					   double p2 = x2;
	    					   double q2 = y2;
	    	    			   
	    					   // coefficients for quadratic formula equation
	    					   double A2 = m*m+1;
	    					   double B2 = 2*(m*c-m*q2-p2);
	    					   double C2 = q2*q2-r2*r2+p2*p2-2*c*q2+c*c;
	    					   
	    					   float px2_neg = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    					   float px2_pos = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    					   
	    					   float py2_neg = (float) ((m * (-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
	    					   float py2_pos = (float) ((m * (-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
	    					   
	    					   // pick the point that is closer to the other circle
	    					   double d2_xneg = Math.abs(px2_neg - x1);
	    					   double d2_yneg = Math.abs(py2_neg - y1);
	    					   double d2_neg = Math.sqrt(d2_xneg * d2_xneg + d2_yneg * d2_yneg); 

	    					   double d2_xpos = Math.abs(px2_pos - x1);
	    					   double d2_ypos = Math.abs(py2_pos - y1);
	    					   double d2_pos = Math.sqrt(d2_xpos * d2_xpos + d2_ypos * d2_ypos); 
	    					  
	    					   float intersect_x2, intersect_y2;
	    					   
	    					   if (d2_neg < d2_pos)
	    					   {
	    						   intersect_x2 = px2_neg;
	    						   intersect_y2 = py2_neg;
	    					   }
	    					   else
	    					   {
	    						   intersect_x2 = px2_pos;
	    						   intersect_y2 = py2_pos;
	    					   }
	    					   
	    					   canvas.drawCircle(calculateAdjustedX(intersect_x1), calculateAdjustedY(intersect_y1), beaconRadius, currentPosPaint);	    					   
	    					   canvas.drawCircle(calculateAdjustedX(intersect_x2), calculateAdjustedY(intersect_y2), beaconRadius, currentPosPaint);	    					   
	    	    			   	    					   
	    					   break;
	    				   }
	    			   }
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
	
	public float calculateAdjustedX(float x)
	{
		float scaledX = x * scaleFactor;
		
		return ((bitmapWidth-2*margin)-fieldWidth*scaleFactor)/2 + scaledX + margin;
	}
	
	public float calculateAdjustedY(float y)
	{
		float scaledY = y * scaleFactor;
		
		return ((bitmapHeight-2*margin)-fieldHeight*scaleFactor)/2 + scaledY + margin;
	}
	
	public float calculateAdjustedDistance(float distance)
	{
		return distance * scaleFactor;
	}
}