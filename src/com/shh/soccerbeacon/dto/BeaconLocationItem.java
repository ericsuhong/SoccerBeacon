package com.shh.soccerbeacon.dto;

import java.util.ArrayList;

import android.util.Log;

public class BeaconLocationItem implements Comparable<BeaconLocationItem>
{
	private int xPos;
	private int yPos;
	private String beaconName;
	private int major;
	private int minor;
	
	private int prevRSSI = 0;
	private long prevDetectedTime = -1; // in milliseconds
	
	private int RSSI = 0;
	private long lastDetectedTime = -1; // in milliseconds
	
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
	
	public void setRSSI(int RSSI) {
		this.RSSI = RSSI;
	}
	
	public int getPrevRSSI() {
		return prevRSSI;
	}
	
	public void setPrevRSSI(int RSSI) {
		this.prevRSSI = RSSI;
	}
	
	public long getPrevDetectedTime() {
		return prevDetectedTime;
	}

	public void setPrevDetectedTime(long prevDetectedTime) {
		this.prevDetectedTime = prevDetectedTime;
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
	
	public long getLastDetectedTime() {
		return lastDetectedTime;
	}

	public void setLastDetectedTime(long lastDetectedTime) {
		this.lastDetectedTime = lastDetectedTime;
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
		/*
		double A = 0.0177;
		double B = -3.065;
		double C = -56.23-RSSI;
		
		float distance = (float) ((-B-Math.sqrt(B*B-4*A*C))/(2*A));
				
		Log.i("BEACON", "DISTANCE_NEG: " + distance);
		
		if (distance < 0)
		{
			return 0.5f;
		}*/
		
		float distance = (float) Math.exp((-RSSI - 59.32)/12.7);
		
		if (distance < 0)
		{
			return 0.5f;
		}
		
		Log.i("BEACON", "DISTANCE: " + distance);
		
		return distance;
	}

	public float calculateDistance()
	{
		long currentTime = System.currentTimeMillis();

	   if (prevRSSI != 0 && prevDetectedTime != -1 && (currentTime - prevDetectedTime < 1500))
	   {
		   if (RSSI != 0 && lastDetectedTime != -1 && (currentTime - lastDetectedTime < 1500))
		   {
			   return (distanceFunction((prevRSSI + RSSI) / 2)); // running sum average
		   }
		   else
		   {
			   // this case should never happen
			   Log.e("ERROR", "PREVRSSI IS MORE RECENT AND VALID THAN CURRENT RSSI! THIS CASE SHOULD NEVER HAPPEN!!!!!");
			   return (distanceFunction(prevRSSI));
		   }
	   }
	   else
	   {
		   if (RSSI != 0 && lastDetectedTime != -1 && (currentTime - lastDetectedTime < 1500))
		   {
			   return distanceFunction(RSSI);
		   }
		   else
		   {
			   return -1;
		   }			   
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