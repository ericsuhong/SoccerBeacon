package com.shh.soccerbeacon.adapter;

import java.util.ArrayList;

import com.shh.soccerbeacon.R;
import com.shh.soccerbeacon.dto.BeaconListItem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BeaconLocationsListAdapter extends BaseAdapter {

	public static ArrayList<BeaconListItem> beaconListItems = null;

	Context mContext = null;
	LayoutInflater vi;
	LayoutInflater mInflater;
	Resources localResources;

	public BeaconLocationsListAdapter(Context context, ArrayList<BeaconListItem> list) 
	{
		mContext = context;
		beaconListItems = list;
		vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		localResources = mContext.getResources();		
	}

	@Override
	public int getCount() {
		return beaconListItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return beaconListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
					
		return convertView;
	}

	static class ViewHolder 
	{
		TextView tvBeaconId;
		TextView tvBeaconMajor;
		TextView tvBeaconMinor;
		TextView tvBeaconRSSI;
	}

	public void updateAdapter(ArrayList<BeaconListItem> result)
	{
		beaconListItems = result;
		notifyDataSetChanged();
	}
}