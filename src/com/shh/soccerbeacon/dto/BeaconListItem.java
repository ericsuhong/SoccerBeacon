package com.shh.soccerbeacon.dto;

public class BeaconListItem
{
	private String beaconId;
	
	public BeaconListItem(String beaconId)
	{
		this.beaconId = beaconId;
	}

	public String getBeaconId() {
		return beaconId;
	}

	public void setBeaconId(String beaconId) {
		this.beaconId = beaconId;
	}
}