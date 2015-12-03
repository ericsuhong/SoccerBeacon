package com.shh.soccerbeacon.dto;

import java.util.ArrayList;

import android.util.Log;

public class BeaconLocationItem implements Comparable<BeaconLocationItem>
{
	private float xPos;
	private float yPos;
	private String beaconName;
	private int major;
	private int minor;
	
	private ArrayList<Integer> RSSIArray = new ArrayList<Integer>();

	private int RSSI = 0;
	
	private float distance = -1;
	
	private int runningSumCount = -1;

	// TWO DEFAULT CALIBRATION PARAMETERS
	private double defaultA = -56;
	private double defaultB = -11;

	// manual calibration data
	private boolean isManual = false;
	private double manualA = 0;
	private double manualB = 0;

	private int shiftC = 0;
	
	public BeaconLocationItem(float xPos, float yPos, String beaconName, int major, int minor)
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
	
	public void clearRSSI()
	{
		RSSIArray = null;
		this.RSSI = 0;
	}
	
	public void setRSSI(int RSSI) 
	{
		if (RSSIArray == null)
			RSSIArray = new ArrayList<Integer>();
				
		if (runningSumCount >= 1)
		{			
			if (RSSIArray.size() >= this.runningSumCount)
			{
				RSSIArray.remove(0);
			}
		
			RSSIArray.add(RSSI);
			this.RSSI = RSSI;
		}
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
	
	public float getX() {
		return xPos;
	}

	public void setX(float xPos) {
		this.xPos = xPos;
	}

	public float getY() {
		return yPos;
	}

	public void setY(int yPos) {
		this.yPos = yPos;
	}
	
	public void setRunningSumCount(int runningSumCount) {
		this.runningSumCount = runningSumCount;
	}
	
	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
	
	
	public double getDefaultA() {
		return defaultA;
	}

	public void setDefaultA(double defaultA) {
		this.defaultA = defaultA;
	}

	public double getDefaultB() {
		return defaultB;
	}

	public void setDefaultB(double defaultB1) {
		this.defaultB = defaultB1;
	}
	
	public int getShiftC() {
		return shiftC;
	}
	
	public void setShiftC(int shiftC) {
		this.shiftC = shiftC;
	}

	public boolean isManual()
	{
		return isManual;
	}
	
	public void setManual(boolean isManual)
	{
		this.isManual = isManual;
	}
	
	public double getManualA()
	{
		return manualA;
	}
	
	public void setManualA(double manualA)
	{
		this.manualA = manualA;
	}
	
	public double getManualB()
	{
		return manualB;
	}
	
	public void setManualB(double manualB)
	{
		this.manualB = manualB;
	}
	
	public float distanceFunction(float RSSI)
	{			
		//float distance = (float) Math.exp((-RSSI - 59.32)/12.7);
		
		float distance;
		
		if (isManual)
		{			
			distance = (float) Math.exp((RSSI-manualA-shiftC)/manualB);
			Log.i("BEACON",  "manualA: " + manualA + ", manualB: " + manualB + ", shiftC: " + shiftC + ", DISTANCE: " + distance);		
		}
		else
		{
			distance = (float) Math.exp((RSSI-defaultA-shiftC)/defaultB);
			Log.i("BEACON", "DISTANCE: " + distance + ", RSSI: " + RSSI + " defaultA: " + defaultA + ", defaultB: " + defaultB + ", shiftC: " + shiftC);		
		}
								
		if (distance < 0)
		{
			return 0.5f;
		}
		
				
		return distance;
	}

	public float calculateDistance()
	{
		if (RSSIArray == null)
		{
			RSSIArray = new ArrayList<Integer>();
			return -1;
		}	
		
		if (RSSIArray.size() == 0)
		{
			return -1;
		}
		
		int runningSum = 0;
		float avgRSSI = 0;
		
		for (int i = 0; i < RSSIArray.size(); i++)
		{
			runningSum += RSSIArray.get(i);
		}
		
		avgRSSI = ((float) runningSum) / RSSIArray.size();
		
		return (distanceFunction(avgRSSI));
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
			if (this.yPos > target.yPos)
			{
				return 1;
			}
			else if (this.yPos == target.yPos)
			{
				if (this.xPos > target.xPos)
					return 1;
				else if (this.xPos == target.xPos)
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