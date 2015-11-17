package com.shh.soccerbeacon.dto;

public class BeaconListItem implements Comparable<BeaconListItem>
{
	private String beaconId;
	private int RSSI;
	
	public BeaconListItem(String beaconId, int RSSI)
	{
		this.beaconId = beaconId;
		this.RSSI = RSSI;
	}

	public String getBeaconId() {
		return beaconId;
	}

	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}
	
	public int getRSSI() {
		return RSSI;
	}
	
	public void setRSSI(int RSSI) {
		this.RSSI = RSSI;
	}

	@Override
	public int compareTo(BeaconListItem target) {
		// sort by descending RSSI order
		if (this.RSSI < target.RSSI)
			return 1;
		else if (this.RSSI == target.RSSI)
			return 0;
		else
			return -1;
	}
}