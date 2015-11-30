package com.shh.soccerbeacon.dto;

import java.util.ArrayList;

public class BeaconLocationItem implements Comparable<BeaconLocationItem>
{
	private int xPos;
	private int yPos;
	private String beaconName;
	private int major;
	private int minor;
	
	private int RSSI = 0;
	
	ArrayList<Integer> RSSIArray;
	ArrayList<Long> timestampArray;
				
	private float distance = -1;
	
	// calibration data
	private boolean calibrated = false;
	private double calibrationA;
	private double calibrationB;

	public BeaconLocationItem(int xPos, int yPos, String beaconName, int major, int minor)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.beaconName = beaconName;
		this.major = major;
		this.minor = minor;
		
		RSSIArray = new ArrayList<Integer>();
		timestampArray = new ArrayList<Long>();
	}

	public String getBeaconName() {
		return beaconName;
	}

	public void setBeaconId(String beaconName) {
		this.beaconName = beaconName;
	}
	
	public int getRSSI() {
		return RSSI;
	}
	
	public void setRSSI(int RSSI) 
	{
		this.RSSI = RSSI;
		
		long currentTime = System.currentTimeMillis();
		
		if (timestampArray.size() > 0)
		{		
			// if running sum was idle for more than 2.5 seconds, restart running sum
			if (timestampArray.get(timestampArray.size()-1) < (currentTime - 2500))
			{
				RSSIArray.clear();
				timestampArray.clear();
			}
		}
		
		RSSIArray.add(RSSI);	
		timestampArray.add(currentTime);		
	}
		
	public float getAverageRSSI() 
	{
		if (RSSIArray == null)
			RSSIArray = new ArrayList<Integer>();
		
		if (timestampArray == null)
			timestampArray = new ArrayList<Long>();
		
		if (RSSIArray.size() == 0)
			return 0;
		
		int runningSum = 0;
		
		for (int i = 0; i < RSSIArray.size(); i++)
		{
			runningSum += RSSIArray.get(i);
		}

		return (float)runningSum/RSSIArray.size();
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}
	
	public int getX() {
		return xPos;
	}

	public void setX(int xPos) {
		this.xPos = xPos;
	}

	public int getY() {
		return yPos;
	}

	public void setY(int yPos) {
		this.yPos = yPos;
	}
	
	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
		
	public boolean isCalibrated()
	{
		return calibrated;
	}
	
	public void setCalibrated(boolean calibrated)
	{
		this.calibrated = calibrated;
	}
	
	public double getCalibrationA()
	{
		return calibrationA;
	}
	
	public void setCalibrationA(double a)
	{
		this.calibrationA = a;
	}
	
	public double getCalibrationB()
	{
		return calibrationB;
	}
	
	public void setCalibrationB(double b)
	{
		this.calibrationB = b;
	}
	
	public float distanceFunction(float RSSI)
	{						
		float distance = (float) Math.exp((-RSSI - calibrationA)/calibrationB);
		
		if (distance < 0)
		{
			return 0.5f;
		}
		
		//Log.i("BEACON", "DISTANCE: " + distance);
		
		return distance;
	}

	public float calculateDistance()
	{
	   if (getAverageRSSI() == 0)
	   {
		   return -1;
	   }
	   else
	   {
		   return distanceFunction(getAverageRSSI());
	   }		  
	}

	// sort by distance, then by position
	@Override
	public int compareTo(BeaconLocationItem target)
	{
		if (this.distance < target.distance)
		{
			return -1;
		}
		else if (this.distance > target.distance)
		{
			return 1;
		}
		else
		{		
			if (this.xPos > target.xPos)
			{
				return 1;
			}
			else if (this.xPos == target.xPos)
			{
				if (this.yPos > target.yPos)
					return 1;
				else if (this.yPos == target.yPos)
					return 0;
				else
					return -1;
			}
			else
			{
				return -1;
			}
		}
	}
}