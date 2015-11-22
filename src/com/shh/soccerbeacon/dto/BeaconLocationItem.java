package com.shh.soccerbeacon.dto;

public class BeaconLocationItem implements Comparable<BeaconLocationItem>
{
	private int xPos;
	private int yPos;
	private String beaconName;
	private int major;
	private int minor;
	private int RSSI;
	
	public BeaconLocationItem(int xPos, int yPos, String beaconName, int major, int minor, int RSSI)
	{
		this.xPos = xPos;
		this.yPos = yPos;
		this.beaconName = beaconName;
		this.major = major;
		this.minor = minor;
		this.RSSI = RSSI;
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

	@Override
	public int compareTo(BeaconLocationItem target) {
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