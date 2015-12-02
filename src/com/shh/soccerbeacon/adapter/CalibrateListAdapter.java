package com.shh.soccerbeacon.adapter;

import java.util.ArrayList;

import com.google.gson.Gson;
import com.shh.soccerbeacon.R;
import com.shh.soccerbeacon.dto.BeaconLocationItem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CalibrateListAdapter extends BaseAdapter {

	public static ArrayList<BeaconLocationItem> beaconLocationListItems = null;

	Context mContext = null;
	LayoutInflater vi;
	LayoutInflater mInflater;
	Resources localResources;

	public CalibrateListAdapter(Context context, ArrayList<BeaconLocationItem> list) 
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
			convertView = vi.inflate(R.layout.row_calibrateitem, null);
		
			holder = new ViewHolder();
			holder.tvBeaconXpos = (TextView) convertView.findViewById(R.id.tvBeaconXpos);
			holder.tvBeaconYpos = (TextView) convertView.findViewById(R.id.tvBeaconYpos);
			holder.tvBeaconName = (TextView) convertView.findViewById(R.id.tvBeaconName);
			holder.tvBeaconMajor = (TextView) convertView.findViewById(R.id.tvBeaconMajor);
			holder.tvBeaconMinor = (TextView) convertView.findViewById(R.id.tvBeaconMinor);
			holder.tvCalibrated = (TextView) convertView.findViewById(R.id.tvCalibrated);
			holder.tvCalibrationA = (TextView) convertView.findViewById(R.id.tvCalibrationA);
			holder.tvCalibrationB = (TextView) convertView.findViewById(R.id.tvCalibrationB);
			holder.tvCalibrationC = (TextView) convertView.findViewById(R.id.tvCalibrationC);
			
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
		
		BeaconLocationItem currentItem = beaconLocationListItems.get(position);
		
		if (currentItem.isManual())
		{
			holder.tvCalibrated.setTextColor(Color.RED);
			holder.tvCalibrated.setText("MANUAL");
			
			holder.tvCalibrationA.setText(String.format("%.3f", currentItem.getManualA()));
			holder.tvCalibrationB.setText(String.format("%.3f", currentItem.getManualB()));
		}
		else
		{
			holder.tvCalibrated.setTextColor(Color.parseColor("#00EE11"));
			holder.tvCalibrated.setText("USING DEFAULT");
			
			holder.tvCalibrationA.setText(String.format("%.3f", beaconLocationListItems.get(position).getDefaultA()));
			holder.tvCalibrationB.setText(String.format("%.3f", beaconLocationListItems.get(position).getDefaultB()));			
		}
				
		if (currentItem.getShiftC() < 0)
		{
			holder.tvCalibrationC.setTextColor(Color.RED);
			holder.tvCalibrationC.setText("" + currentItem.getShiftC());
		}
		else if (currentItem.getShiftC() > 0)
		{
			holder.tvCalibrationC.setTextColor(Color.RED);
			holder.tvCalibrationC.setText("+" + currentItem.getShiftC());
		}
		else
		{
			holder.tvCalibrationC.setTextColor(Color.BLACK);
			holder.tvCalibrationC.setText("" + currentItem.getShiftC());
		}

		return convertView;
	}

	static class ViewHolder 
	{
		TextView tvBeaconXpos;
		TextView tvBeaconYpos;
		TextView tvBeaconName;
		TextView tvBeaconMajor;
		TextView tvBeaconMinor;
		TextView tvCalibrated;
		TextView tvCalibrationA;
		TextView tvCalibrationB;
		TextView tvCalibrationC;
	}

	public void updateAdapter(ArrayList<BeaconLocationItem> result)
	{
		beaconLocationListItems = result;
		notifyDataSetChanged();
	}
}