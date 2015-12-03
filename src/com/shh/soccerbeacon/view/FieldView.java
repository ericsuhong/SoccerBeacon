package com.shh.soccerbeacon.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private boolean useClosestBeacon = true;
	private float outlierDistance = 3;
	private float outlierTrimFactor = 0.5f;
	
	private float fieldWidth = -1;
	private float fieldHeight = -1;
	
	String fieldColor = "#35AE2F";
	String beaconColor = "#EEEEEE";
	String beaconRangeColor = "#000000";
	String highlightColor = "#00FFFF";
	String oldPosColor = "#FF0000";
	String currentPosColor = "#FFFF00";

	float beaconRadius = 12;
	float currentPosRadius = 7;
	float fontSize = 30;
	
	int bitmapWidth = 0;
	int bitmapHeight = 0;
	
	boolean showRange = true;
	boolean showBeaconName = true;
	boolean showBeaconInfo = true;
	
	ArrayList<BeaconLocationItem> beaconLocationsList;
	
	private float scaleFactor;
	
	Paint fieldPaint;
	Paint beaconPaint;
	Paint textPaint;
	Paint beaconRangePaint;
	Paint oldPosPaint;
	Paint currentPosPaint;
	Paint highlightPaint;
	
	ArrayList<Float> xList = new ArrayList<Float>();
	ArrayList<Float> yList = new ArrayList<Float>();
		
	boolean isPlaying = false;
			
    public FieldView(Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        
        fieldPaint = new Paint();
		fieldPaint.setColor(Color.parseColor(fieldColor));
        
		beaconPaint = new Paint();
		beaconPaint.setColor(Color.parseColor(beaconColor));
		
		textPaint = new Paint();
		textPaint.setColor(Color.BLACK); 
		textPaint.setTextSize(fontSize); 
		
		beaconRangePaint = new Paint();
		beaconRangePaint.setColor(Color.parseColor(beaconRangeColor));
		beaconRangePaint.setStyle(Paint.Style.STROKE);
		beaconRangePaint.setStrokeWidth(4f);
				
		oldPosPaint = new Paint();
		oldPosPaint.setColor(Color.parseColor(oldPosColor));
		
		currentPosPaint = new Paint();
		currentPosPaint.setColor(Color.parseColor(currentPosColor));
		
		highlightPaint = new Paint();
		highlightPaint.setColor(Color.parseColor(highlightColor));
		highlightPaint.setStyle(Paint.Style.STROKE);
		highlightPaint.setStrokeWidth(4f);
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
	    	   
	    	   // first loop to draw beacons
	    	   for (int i = 0 ; i < beaconLocationsList.size(); i++)
	    	   {	    		   
	    		   BeaconLocationItem beacon = beaconLocationsList.get(i);
	    		   canvas.drawCircle(calculateAdjustedX(beacon.getX()), calculateAdjustedY(beacon.getY()), beaconRadius, beaconPaint);  		   
	

	    		   if (showBeaconInfo)
	    		   {
		    		   canvas.drawText(beacon.getBeaconName(), calculateAdjustedX(beacon.getX()), calculateAdjustedY(beacon.getY()) + fontSize, textPaint);
	    		   }
	    		   
	    		   if (showBeaconInfo)
	    		   {
	    			   if (beacon.getRSSI() != 0)
	    				   canvas.drawText("" + beacon.getRSSI() + ", " + String.format("%.3f", beacon.getDistance()) + "m", calculateAdjustedX(beacon.getX()), calculateAdjustedY(beacon.getY()) + 2*fontSize, textPaint); 
	    		   }	    			   
	    	   }
	    	   	    	   
	    	   // current "closest" position discovered so far...
	    	   float currentX = -1;
	    	   float currentY = -1;
	    	   
	    	   int i;
	    	   
	    	   // second loop to calculate positions
	    	   for (i = 0 ; i < beaconLocationsList.size(); i++)
	    	   {		   
	    		   BeaconLocationItem firstBeacon = beaconLocationsList.get(i);
	    		   
	    		   if (firstBeacon.getDistance() == -1)
	    		   {
	    			   continue;
	    		   }
	    		   else
	    		   {		    		   
		    		   if (showRange)
		    		   {		    		   
			    		   float distance = firstBeacon.getDistance();
			    		   //Log.i("BEACON", "distance: " + distance);
			    		   
		    			   canvas.drawCircle(calculateAdjustedX(firstBeacon.getX()), calculateAdjustedY(firstBeacon.getY()), calculateAdjustedDistance(distance), highlightPaint);    		   
		    		   }
	    		   }	    			   
	    		   
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
	    			   i++;
	    			   
	    			   BeaconLocationItem secondBeacon = beaconLocationsList.get(i);
	    			   
	    			   if (showRange)
		    		   {		    		   
			    		   float distance = secondBeacon.getDistance();
			    		   //Log.i("BEACON", "distance: " + distance);
			    		   
		    			   canvas.drawCircle(calculateAdjustedX(secondBeacon.getX()), calculateAdjustedY(secondBeacon.getY()), calculateAdjustedDistance(distance), highlightPaint);    		   
		    		   }
	    			   
	    			   //Log.i("BEACON", "Two circles available: " + firstBeacon.getBeaconName() + ", " + firstBeacon.getDistance() + ", " + secondBeacon.getBeaconName() + ", " + secondBeacon.getDistance());
	    			   	    	
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
	    				   float py1_neg, py1_pos, px1_neg, px1_pos;
	    				   
	    				   if (x1 == x2)
	    				   {
	    					  py1_pos = py1_neg = (float) (((r1*r1-r2*r2)-(y1*y1-y2*y2))/ ((-2)*(y1-y2)));
	    					  
	    					  double A = 1;
	    					  double B = -2 * x1;
	    					  double C = x1*x1-r1*r1+(py1_pos-y1)*(py1_pos-y1);
	    					  
	    					  px1_pos = (float) ((-B+Math.sqrt(B*B-4*A*C))/(2*A));
	    					  px1_neg = (float) ((-B-Math.sqrt(B*B-4*A*C))/(2*A));
	    				   }
	    				   else
	    				   {	    				   
		    				   double Zy = (r1*r1-r2*r2)-(x1*x1-x2*x2)-(y1*y1-y2*y2)+2*x1*(x1-x2);
		    				   double Ay = (((y1-y2)*(y1-y2))/((x1-x2)*(x1-x2))) + 1;
		    				   double By = ((Zy *(y1-y2))/((x1-x2)*(x1-x2))) - 2*y1;
		    				   double Cy = ((Zy*Zy)/(4*(x1-x2)*(x1-x2))) + y1*y1 - r1*r1;
		    				   
		    				   py1_neg = (float) ((-By-Math.sqrt(By*By-4*Ay*Cy))/(2*Ay));
		    				   py1_pos = (float) ((-By+Math.sqrt(By*By-4*Ay*Cy))/(2*Ay));    
		    				   
		    				   px1_neg = (float)(((r1*r1-r2*r2)-(x1*x1-x2*x2)-(y1*y1-y2*y2)+2*py1_neg*(y1-y2))/(-2*(x1-x2)));
		    				   px1_pos = (float)(((r1*r1-r2*r2)-(x1*x1-x2*x2)-(y1*y1-y2*y2)+2*py1_pos*(y1-y2))/(-2*(x1-x2)));
	    				   }
	    				   
	    				   //Log.i("BEACON", "py1_neg, py1_pos: " + py1_neg + ", " + py1_pos);
    					   //Log.i("BEACON", "px1_neg, px1_pos: " + px1_neg + ", " + px1_pos);
	    				   
	    				   /*
	    				   if (showRange && showInfo)
    					   {
	    					   canvas.drawCircle(calculateAdjustedX(px1_neg), calculateAdjustedY(py1_neg), debugPosRadius, highlightPaint);	    					   
	    					   canvas.drawCircle(calculateAdjustedX(px1_pos), calculateAdjustedY(py1_pos), debugPosRadius, highlightPaint);	
    					   }*/
	    				   
    					   // case #1: two circles intersect at one point
	    				   if (px1_neg == px1_pos && py1_neg == py1_pos)
	    				   {
	    					   Log.i("BEACON", "Two circles intersect at ONE point");
	    					   
	    					   currentX = px1_pos;
	    					   currentY = py1_pos;
	    					   
	    					   break;
	    				   }
	    				   // case #2: two circles intersect at two points
	    				   else
	    				   {
	    					   Log.i("BEACON", "Two circles intersect at TWO point");
	    					   
	    					   currentX = (px1_pos + px1_neg)/2;
    						   currentY = (py1_pos + py1_neg)/2;	
    						   
    						   break;
	    					   
    						   /*
	    					   if (i == (beaconLocationsList.size() - 1))
	    					   {
	    						   Log.i("BEACON", "ONLY two circles are available...");

	    						   // only two circles are available...
	    						   // take an average pos between two intersection points
	    						   
	    						   pointX = (px1_pos + px1_neg)/2;
	    						   pointY = (py1_pos + py1_neg)/2;	    						   
	    					   }
	    					   else
	    					   {
	    						   Log.i("BEACON", "Third circle is available...");
	    						   
		    					   // need to take a look at third point...
	    						   i++;
	    						   
	    						   BeaconLocationItem thirdBeacon = beaconLocationsList.get(i);
	    						   
	    						   // choose an intersection point that is closer to the third circle
	    						   float pos_distance, neg_distance;
	    						   float pos_x1, pos_y1, pos_x2, pos_y2;
	    						   float neg_x1, neg_y1, neg_x2, neg_y2;
	    						   float pos_x, pos_y, neg_x, neg_y;
	    						   
	    						   // draw a line between each intersect point to the center of the third circle
	    						   // line that goes throught two center points: y = mx + c
		    					   double m_pos = (py1_pos-thirdBeacon.getY())/(px1_pos-thirdBeacon.getX());  
		    					   double c_pos = thirdBeacon.getY() - m_pos*thirdBeacon.getX();
		    					   
		    					   if ((px1_pos-thirdBeacon.getX()) != 0)
		    					   {
		    						   //Log.i("BEACON", "SLOPE IS NOT INFINITE!!!");
		    						   
			    					   // two intersection points between a pos line and a third circle
			    					   double p = thirdBeacon.getX();
			    					   double q = thirdBeacon.getY();
			    					   double r = thirdBeacon.getDistance();
			    					
			    					   // coefficients for quadratic formula equation
			    					   double A1 = m_pos*m_pos+1;
			    					   double B1 = 2*(m_pos*c_pos-m_pos*q-p);
			    					   double C1 = q*q-r*r+p*p-2*c_pos*q+c_pos*c_pos;
			    					   
			    					   pos_x1 = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
			    					   pos_y1 = (float) (m_pos * pos_x1 + c_pos);
	
			    					   pos_x2 = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	    					   
			    					   pos_y2 = (float) (m_pos * pos_x2 + c_pos);
			    					   
			    					   //Log.i("BEACON", "pos_x1, pos_y1: " + pos_x1 + ", " + pos_y1);
			    					   //Log.i("BEACON", "pos_x2, pos_y2: " + pos_x2 + ", " + pos_y2);
		    					   }
		    					   else
		    					   {
		    						   //Log.i("BEACON", "SLOPE IS INFINITE!!!");
		    						   
		    						   double x3 = thirdBeacon.getX();
		    						   double y3 = thirdBeacon.getY();
		    						   double r3 = thirdBeacon.getDistance();
		    						   
		    						   double A1 = 1;
			    					   double B1 = -2 * y3;
			    					   double C1 = y3*y3 - r3*r3;
		    						   
			    					   pos_x1 = (float) x3;
			    					   pos_y1 = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    								   
			    					   pos_x2 = (float) x1;
			    					   pos_y2 = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    					   }		    					   
		    					   
		    					   // find a closer distance point
		    					   float pos_distance1 = (float) Math.sqrt((Math.abs(pos_x1-px1_pos)*Math.abs(pos_x1-px1_pos)+(Math.abs(pos_y1-py1_pos)*Math.abs(pos_y1-py1_pos))));
		    					   float pos_distance2 = (float) Math.sqrt((Math.abs(pos_x2-px1_pos)*Math.abs(pos_x2-px1_pos)+(Math.abs(pos_y2-py1_pos)*Math.abs(pos_y2-py1_pos))));
		    				    					   
		    					   if (pos_distance1 < pos_distance2)
		    					   {
		    						   pos_distance = pos_distance1;
		    						   pos_x = pos_x1;
		    						   pos_y = pos_y1;
		    					   }
		    					   else
		    					   {
		    						   pos_distance = pos_distance2;
		    						   pos_x = pos_x2;
		    						   pos_y = pos_y2;
		    					   }
		    					   
		    					   double m_neg = (py1_neg-thirdBeacon.getY())/(px1_neg-thirdBeacon.getX()); 
		    					   double c_neg = thirdBeacon.getY() - m_neg*thirdBeacon.getX();

		    					   if ((px1_neg-thirdBeacon.getX()) != 0)
		    					   {		    
		    						   Log.i("BEACON", "SLOPE IS NOT INFINITE!!!");
		    						   
		    						   // two intersection points between a pos line and a third circle
			    					   double p = thirdBeacon.getX();
			    					   double q = thirdBeacon.getY();
			    					   double r = thirdBeacon.getDistance();
		    						   
			    					   // coefficients for quadratic formula equation
			    					   double A2 = m_neg*m_neg+1;
			    					   double B2 = 2*(m_neg*c_neg-m_neg*q-p);
			    					   double C2 = q*q-r*r+p*p-2*c_neg*q+c_neg*c_neg;
			    					   
			    					   neg_x1 = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
			    					   neg_y1 = (float) (m_neg * neg_x1 + c_neg);
	
			    					   neg_x2 = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));	    					   
			    					   neg_y2 = (float) (m_neg * neg_x2 + c_neg);
		    					   }
		    					   else
		    					   {
		    						   Log.i("BEACON", "SLOPE IS INFINITE!!!");
		    						   
		    						   double x3 = thirdBeacon.getX();
		    						   double y3 = thirdBeacon.getY();
		    						   double r3 = thirdBeacon.getDistance();
		    						   
		    						   double A1 = 1;
			    					   double B1 = -2 * y3;
			    					   double C1 = y3*y3 - r3*r3;
		    						   
			    					   neg_x1 = (float) x3;
			    					   neg_y1 = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    								   
			    					   neg_x2 = (float) x1;
			    					   neg_y2 = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    					   }
		    					   
		    					   // find a closer distance point
		    					   float neg_distance1 = (float) Math.sqrt((Math.abs(neg_x1-px1_neg)*Math.abs(neg_x1-px1_neg)+(Math.abs(neg_y1-py1_neg)*Math.abs(neg_y1-py1_neg))));
		    					   float neg_distance2 = (float) Math.sqrt((Math.abs(neg_x2-px1_neg)*Math.abs(neg_x2-px1_neg)+(Math.abs(neg_y2-py1_neg)*Math.abs(neg_y2-py1_neg))));
		    				    					   
		    					   if (neg_distance1 < neg_distance2)
		    					   {
		    						   neg_distance = neg_distance1;
		    						   neg_x = neg_x1;
		    						   neg_y = neg_y1;
		    					   }
		    					   else
		    					   {
		    						   neg_distance = neg_distance2;
		    						   neg_x = neg_x2;
		    						   neg_y = neg_y2;
		    					   }
		    					   
		    					   if (pos_distance < neg_distance)
		    					   {
		    						   pointX = (px1_pos + pos_x)/2;
		    						   pointY = (py1_pos + pos_y)/2;
		    					   }
		    					   else
		    					   {
		    						   pointX = (px1_neg + neg_x)/2;
		    						   pointY = (py1_neg + neg_y)/2;
		    					   }		    					   
   		    					   
								   if (showDebug)
								   {
									   canvas.drawCircle(calculateAdjustedX(pos_x), calculateAdjustedY(pos_y), debugPosRadius, debugPosPaint);	    					   
									   canvas.drawCircle(calculateAdjustedX(neg_x), calculateAdjustedY(neg_y), debugPosRadius, debugPosPaint);
								  
									   canvas.drawLine(calculateAdjustedX((float)thirdBeacon.getX()), calculateAdjustedY((float)thirdBeacon.getY()), calculateAdjustedX((float)px1_pos), calculateAdjustedY((float)(px1_pos*m_pos+c_pos)), debugPosPaint);
			    					   canvas.drawLine(calculateAdjustedX((float)thirdBeacon.getX()), calculateAdjustedY((float)thirdBeacon.getY()), calculateAdjustedX((float)px1_neg), calculateAdjustedY((float)(px1_neg*m_neg+c_neg)), debugPosPaint);
		    					   }
	    					   }			   
	    					   
	    	    			   break;*/
	    				   }
	    			   }
	    			   // case #2b: two circles do not intersect
	    			   else
	    			   {
	    				   // one circle is contained within another
	    				   if ((centerDistance + r1 < r2) || (centerDistance + r2 < r1))
	    				   {
	    					   Log.i("BEACON", "One circle is contained within another");
	    					   
	    					   // line that goes through two center points: y = mx + c
	    					   double m = (y2-y1)/(x2-x1);  
	    					   double c = y1 - m*x1;
	    					   
	    					   float px1_neg, py1_neg, px1_pos, py1_pos;
	    					   float px2_neg, py2_neg, px2_pos, py2_pos;
	    					   	    					   
	    					   if ((x2-x1) != 0)
	    					   {	
	    						   //Log.i("BEACON", "SLOPE IS NOT INFINITE!!");
	    						   
		    					   // intersection point between line and circle 1;  
		    					   double p1 = x1;
		    					   double q1 = y1;
		    					
		    					   // coefficients for quadratic formula equation
		    					   double A1 = m*m+1;
		    					   double B1 = 2*(m*c-m*q1-p1);
		    					   double C1 = q1*q1-r1*r1+p1*p1-2*c*q1+c*c;
		    					   
		    					   px1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    					   py1_neg = (float) ((m * (-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
	
		    					   px1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	    					   
		    					   py1_pos = (float) ((m * (-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
		    					   
		    					   // intersection point between line and circle 2;  
		    					   double p2 = x2;
		    					   double q2 = y2;
		    	    			   
		    					   // coefficients for quadratic formula equation
		    					   double A2 = m*m+1;
		    					   double B2 = 2*(m*c-m*q2-p2);
		    					   double C2 = q2*q2-r2*r2+p2*p2-2*c*q2+c*c;
		    					   
		    					   px2_neg = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
		    					   px2_pos = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
		    					   
		    					   py2_neg = (float) ((m * (-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
		    					   py2_pos = (float) ((m * (-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
	    					   }
	    					   else
	    					   {
	    						   //Log.i("BEACON", "SLOPE IS INFINITE!!");
	    						   
	    						   double A1 = 1;
		    					   double B1 = -2 * y1;
		    					   double C1 = y1*y1 - r1*r1;
	    						   
	    						   px1_neg = (float) x1;
	    						   py1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
	    								   
	    						   px1_pos = (float) x1;
	    						   py1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
	    						   
	    						   double A2 = 1;
		    					   double B2 = -2 * y2;
		    					   double C2 = y2*y2 - r2*r2;
	    						   
		    					   px2_neg = (float) x2;
	    						   py2_neg = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    								   
	    						   px2_pos = (float) x2;
	    						   py2_pos = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    					   }
	    					   
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
	    					   
	    					   currentX = (intersect_x1 + intersect_x2)/2;
    						   currentY = (intersect_y1 + intersect_y2)/2;	    					   
	    					       					   
	    					   break;	    					   
	    				   }
	    				   // two circles lie outside of each other
	    				   else
	    				   {	    					   
	    					   // line that goes throught two center points: y = mx + c
	    					   double m = (y2-y1)/(x2-x1);  
	    					   double c = y1 - m*x1;
	    					   
	    					   float px1_neg, py1_neg, px1_pos, py1_pos;
	    					   float px2_neg, py2_neg, px2_pos, py2_pos;
	    					   
	    					   if ((x2-x1) != 0)
	    					   {	    					   
		    					   // intersection point between line and circle 1;  
		    					   double p1 = x1;
		    					   double q1 = y1;
		    					
		    					   // coefficients for quadratic formula equation
		    					   double A1 = m*m+1;
		    					   double B1 = 2*(m*c-m*q1-p1);
		    					   double C1 = q1*q1-r1*r1+p1*p1-2*c*q1+c*c;
		    					   
		    					   px1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    					   py1_neg = (float) ((m * (-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
	
		    					   px1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	    					   
		    					   py1_pos = (float) ((m * (-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
		    					   
	   	    	    			   // intersection point between line and circle 2;  
								   double p2 = x2;
								   double q2 = y2;
								   
								   // coefficients for quadratic formula equation
								   double A2 = m*m+1;
								   double B2 = 2*(m*c-m*q2-p2);
								   double C2 = q2*q2-r2*r2+p2*p2-2*c*q2+c*c;
								   
								   px2_neg = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
								   px2_pos = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
								   
								   py2_neg = (float) ((m * (-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);
								   py2_pos = (float) ((m * (-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2)) + c);			   
	    					   }
	    					   else
	    					   {
	    						   px1_neg = px1_pos = (float) x1;
	    						   px2_neg = px2_pos = (float) x1;
	    						   
	    						   double A1 = 1;
		    					   double B1 = -2 * y1;
		    					   double C1 = y1*y1-r1*r1;
		    					   
		    					   py1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
		    					   py1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	
		    					   
		    					   double A2 = 1;
		    					   double B2 = -2 * y2;
		    					   double C2 = y2*y2-r2*r2;
		    					   
		    					   py2_pos = (float) ((-B2+Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
		    					   py2_neg = (float) ((-B2-Math.sqrt(B2*B2-4*A2*C2))/(2*A2));
	    					   }
	    					   
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
	    					   
	    					   currentX = (intersect_x1 + intersect_x2)/2;
    						   currentY = (intersect_y1 + intersect_y2)/2;
	    					   
	    					   break;
	    				   }
	    			   }
	    		   }
	    	   }
	    	   
	    	   if (currentX == -1 && currentY == -1)
	    	   {	    		   
	    		   return;
		       }
	    	   
	    	   i++;
	    	   
	    	   BeaconLocationItem thirdBeacon = null;
	    	   
	    	   if (!useClosestBeacon)
	    	   {
	    		   // use closest distance third beacon
		    	   if (i < beaconLocationsList.size())
		    	   {
		    		   thirdBeacon = beaconLocationsList.get(i);
		    	   }
	    	   }
	    	   else
	    	   {
	    		   ArrayList<BeaconLocationItem> closeBeaconList = (ArrayList<BeaconLocationItem>) beaconLocationsList.clone();
	    		   closeBeaconList.remove(0);
	    		   closeBeaconList.remove(0);	    		   
	    		   
	    		   if (closeBeaconList.size() > 0)
	    		   {	    			   
	    			   final float pointX_fixed = currentX;
		    		   final float pointY_fixed = currentY;
		    		   	
		    		   // sort by distance from this point
		    		   Collections.sort(closeBeaconList, new Comparator<BeaconLocationItem>()
		    			{
		    	            public int compare(BeaconLocationItem p1, BeaconLocationItem p2) 
		    	            {
			    			   	double d1 = Math.sqrt(Math.abs(pointX_fixed - p1.getX())*Math.abs(pointX_fixed - p1.getX()) + Math.abs(pointY_fixed - p1.getY())*Math.abs(pointY_fixed - p1.getY()));
			    			   	double d2 = Math.sqrt(Math.abs(pointX_fixed - p2.getX())*Math.abs(pointX_fixed - p2.getX()) + Math.abs(pointY_fixed - p2.getY())*Math.abs(pointY_fixed - p2.getY()));
			    			   	
			    			   	if (d1 < d2)
			    			   		return -1;
			    			   	else if (d1 == d2)
			    			   		return 0;
			    			   	else
			    			   		return 1;			   	
		    	            }
		    	        });  		   
		    		   
		    		   thirdBeacon = closeBeaconList.get(0);
	    		   }	    		   
	    	   }
	    	   
	    	   if (thirdBeacon != null)
	    	   {
	    		   float distance = thirdBeacon.getDistance();	

	    		   if (showRange)
	    		   {		    		   
	    			   canvas.drawCircle(calculateAdjustedX(thirdBeacon.getX()), calculateAdjustedY(thirdBeacon.getY()), calculateAdjustedDistance(distance), beaconRangePaint);    		   
	    		   }
	    		   
	    		   float x1 = currentX;
	    		   float y1 = currentY;
	    		   float x2 = thirdBeacon.getX();
	    		   float y2 = thirdBeacon.getY();
	    		   float r = distance;
		    		   
	    		   // line that goes throught two center points: y = mx + c
				   double m = (y2-y1)/(x2-x1);  
				   double c = y1 - m*x1;
				   
				   float px1_neg, py1_neg, px1_pos, py1_pos;
				   
				   if ((x2-x1) != 0)
				   {	    					   
					   // intersection point between line and circle 1;  
					   double p = x2;
					   double q = y2;
					
					   // coefficients for quadratic formula equation
					   double A1 = m*m+1;
					   double B1 = 2*(m*c-m*q-p);
					   double C1 = q*q-r*r+p*p-2*c*q+c*c;
					   
					   px1_neg = (float) ((-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1));
					   py1_neg = (float) ((m * (-B1-Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);

					   px1_pos = (float) ((-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1));	    					   
					   py1_pos = (float) ((m * (-B1+Math.sqrt(B1*B1-4*A1*C1))/(2*A1)) + c);
					   
					   // pick the point that is closer to the current point
					   double d1_xneg = Math.abs(px1_neg - x1);
					   double d1_yneg = Math.abs(py1_neg - y1);
					   double d1_neg = Math.sqrt(d1_xneg * d1_xneg + d1_yneg * d1_yneg); 

					   double d1_xpos = Math.abs(px1_pos - x1);
					   double d1_ypos = Math.abs(py1_pos - y1);
					   double d1_pos = Math.sqrt(d1_xpos * d1_xpos + d1_ypos * d1_ypos); 
					   				   
					   if (d1_neg < d1_pos)
					   {
						   currentX = (px1_neg + currentX)/2;
						   currentY = (py1_neg + currentY)/2;
					   }
					   else
					   {
						   currentX = (px1_pos + currentX)/2;
						   currentY = (py1_pos + currentY)/2;
					   }
				   }
	    	   }    	   
	    	   	    	   
	    	   // trim outliers...
	    	   // compare the distance between current location and previous location
	    	   
	    	   if (xList.size() > 0)
	    	   {
	    		   double prevX = xList.get(xList.size()-1); 
	    		   double prevY = yList.get(yList.size()-1);
	    		   
	    		   float distancePrevCurrent = (float) Math.sqrt(Math.abs(prevX - currentX)*Math.abs(prevX - currentX) + Math.abs(prevY - currentY)*Math.abs(prevY - currentY));
	    		   
	    		   // trim the outlier if it is above or equal to the threshold
	    		   if (distancePrevCurrent >= this.outlierDistance)
	    		   {
	    			   //Log.i("TRIM", "TRIMMING!!!");
	    			   //Log.i("TRIM", "PrevX, PrevY: " + prevX + ", " + prevY);
	    			   //Log.i("TRIM", "CurrX, CurrY: " + currentX + ", " + currentY);

	    			   currentX = (float) ((currentX - prevX)*this.outlierTrimFactor + prevX);
	    			   currentY = (float) ((currentY - prevY)*this.outlierTrimFactor + prevY);
	    			   
	    			   //Log.i("TRIM", "NewX, NewY: " + currentX + ", " + currentY);	    			   
	    		   }
	    	   }    	   	 
	    	  	
	    	   // if playing, add points to the list and draw all circles gathered so far...
	    	   if (isPlaying)
	    	   {
		    	   if (currentX != -1 && currentY != -1)
		    	   {	    		
		    		   xList.add(currentX);
			    	   yList.add(currentY);
		    	   }	
	    		   
		    	   for (int j = 0; j < xList.size(); j++)
		    	   {
		    		   if (j == xList.size()-1)
		    			   canvas.drawCircle(calculateAdjustedX(xList.get(j)), calculateAdjustedY(yList.get(j)), currentPosRadius, currentPosPaint);
		    		   else
		    			   canvas.drawCircle(calculateAdjustedX(xList.get(j)), calculateAdjustedY(yList.get(j)), currentPosRadius, oldPosPaint);
		    	   }
	    	   }
	    	   else // otherwise, simply draw current circle
	    	   {
    			   canvas.drawCircle(calculateAdjustedX(currentX), calculateAdjustedY(currentY), currentPosRadius, currentPosPaint);
	    	   }
	    	   
	    	   // first point is now available...
	    	   //canvas.drawCircle(calculateAdjustedX(pointX), calculateAdjustedY(pointY), currentPosRadius, currentPosPaint);   
	       }
		}
    }
            
    public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}
	
	public void setOutlierDistance(float outlierDistance) {
		this.outlierDistance = outlierDistance;
	}
	
	public void setOutlierTrimFactor(float outlierTrimFactor) {
		this.outlierTrimFactor = outlierTrimFactor;
	}
	
	public void setUseClosestBeacon(boolean useClosestBeacon) {
		this.useClosestBeacon = useClosestBeacon;
	}

	public float getFieldWidth() {
		return fieldWidth;
	}

	public void setFieldWidth(float fieldWidth) {
		this.fieldWidth = fieldWidth;
	}

	public float getFieldHeight() {
		return fieldHeight;
	}

	public void setFieldHeight(float fieldHeight) {
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
	
	public void setShowRange(boolean showRange)
	{
		this.showRange = showRange;		
	}
	
	public void setShowBeaconInfo(boolean showBeaconInfo)
	{
		this.showBeaconInfo = showBeaconInfo;		
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

	public void stop() 
	{
		this.isPlaying = false;
		this.xList.clear();
		this.yList.clear();
	}

	public void start() 
	{
		this.isPlaying = true;
	}	
}