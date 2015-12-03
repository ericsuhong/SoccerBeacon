package com.shh.soccerbeacon.adapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.shh.soccerbeacon.R;
import com.shh.soccerbeacon.dto.BeaconLocationItem;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class CalibrationPaneAdapter extends BaseAdapter {

	public static ArrayList<BeaconLocationItem> beaconLocationListItems = null;

	Context mContext = null;
	LayoutInflater vi;
	LayoutInflater mInflater;
	Resources localResources;

	public CalibrationPaneAdapter(Context context, ArrayList<BeaconLocationItem> list) 
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
		
		final BeaconLocationItem currentItem = beaconLocationListItems.get(position);
					
		if (convertView == null) 
		{
			convertView = vi.inflate(R.layout.row_calibrationpaneitem, null);
		
			holder = new ViewHolder();
			holder.tvBeaconXpos = (TextView) convertView.findViewById(R.id.tvBeaconXpos);
			holder.tvBeaconYpos = (TextView) convertView.findViewById(R.id.tvBeaconYpos);
			holder.tvBeaconName = (TextView) convertView.findViewById(R.id.tvBeaconName);
			holder.tvBeaconMajor = (TextView) convertView.findViewById(R.id.tvBeaconMajor);
			holder.tvBeaconMinor = (TextView) convertView.findViewById(R.id.tvBeaconMinor);
			holder.tvCalibrationC = (TextView) convertView.findViewById(R.id.tvCalibrationC);
			holder.btnPlus = (Button) convertView.findViewById(R.id.btnPlus);
			holder.btnMinus = (Button) convertView.findViewById(R.id.btnMinus);
			
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
				
		holder.btnPlus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) 
			{				
				currentItem.setShiftC(currentItem.getShiftC() + 1);	    		   
	    		notifyDataSetChanged();
			}
		});
		
		holder.btnMinus.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				currentItem.setShiftC(currentItem.getShiftC() - 1);	
			    notifyDataSetChanged();
			}
		});

		if (currentItem.getShiftC() < 0)
		{
			holder.tvCalibrationC.setTextColor(Color.RED);
			holder.tvCalibrationC.setText("" + currentItem.getShiftC());
		}
		else if (currentItem.getShiftC() > 0)
		{
			holder.tvCalibrationC.setTextColor(Color.parseColor("#35AE2F"));
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
		Button btnPlus;
		Button btnMinus;
	}

	public void updateAdapter(ArrayList<BeaconLocationItem> result)
	{
		beaconLocationListItems = result;
		notifyDataSetChanged();
	}
}