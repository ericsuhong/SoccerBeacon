package com.shh.soccerbeacon.adapter;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.shh.soccerbeacon.R;
import com.shh.soccerbeacon.dto.BeaconLocationItem;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BeaconLocationsListAdapter extends BaseAdapter {

	public static ArrayList<BeaconLocationItem> beaconLocationListItems = null;

	Context mContext = null;
	LayoutInflater vi;
	LayoutInflater mInflater;
	Resources localResources;

	public BeaconLocationsListAdapter(Context context, ArrayList<BeaconLocationItem> list) 
	{
		mContext = context;
		beaconLocationListItems = list;
		vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		localResources = mContext.getResources();		
	}

	@Override
	public int getCount() {
		return beaconLocationListItems.size();
	}

	@Override
	public Object getItem(int arg0) {
		return beaconLocationListItems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
					
		if (convertView == null) 
		{
			convertView = vi.inflate(R.layout.row_beaconlocation, null);
		
			holder = new ViewHolder();
			holder.tvBeaconXpos = (TextView) convertView.findViewById(R.id.tvBeaconXpos);
			holder.tvBeaconYpos = (TextView) convertView.findViewById(R.id.tvBeaconYpos);
			holder.tvBeaconName = (TextView) convertView.findViewById(R.id.tvBeaconName);
			holder.tvBeaconMajor = (TextView) convertView.findViewById(R.id.tvBeaconMajor);
			holder.tvBeaconMinor = (TextView) convertView.findViewById(R.id.tvBeaconMinor);
			holder.tvBeaconRSSI = (TextView) convertView.findViewById(R.id.tvBeaconRSSI);
			holder.ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
			convertView.setTag(holder);
		}
		else 
		{
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvBeaconXpos.setText("" + beaconLocationListItems.get(position).getX());
		holder.tvBeaconYpos.setText("" + beaconLocationListItems.get(position).getY());
		holder.tvBeaconName.setText(beaconLocationListItems.get(position).getBeaconName());
		holder.tvBeaconMajor.setText("" + beaconLocationListItems.get(position).getMajor());
		holder.tvBeaconMinor.setText("" + beaconLocationListItems.get(position).getMinor());
		holder.tvBeaconRSSI.setText("" + beaconLocationListItems.get(position).getRSSI());
		
		final int xPos =  beaconLocationListItems.get(position).getX();
		final int yPos =  beaconLocationListItems.get(position).getY();
		
		holder.ivDelete.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View arg0) 
			{
				beaconLocationListItems.remove(position);
				notifyDataSetChanged();
				
				String beaconLocationsJSON = new Gson().toJson(beaconLocationListItems);
	    		
	    		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
	    		SharedPreferences.Editor editor = sharedPref.edit();
	    		editor.putString("BeaconLocations", beaconLocationsJSON);
	    		editor.commit();
			}			
		});
			
		return convertView;
	}

	static class ViewHolder 
	{
		TextView tvBeaconXpos;
		TextView tvBeaconYpos;
		TextView tvBeaconName;
		TextView tvBeaconMajor;
		TextView tvBeaconMinor;
		TextView tvBeaconRSSI;
		ImageView ivDelete;
	}

	public void updateAdapter(ArrayList<BeaconLocationItem> result)
	{
		beaconLocationListItems = result;
		notifyDataSetChanged();
	}
}