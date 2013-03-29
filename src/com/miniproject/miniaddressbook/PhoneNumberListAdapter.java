package com.miniproject.miniaddressbook;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PhoneNumberListAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<String> phoneNumberList;

	public PhoneNumberListAdapter(Context context, List<String> list) {
		inflater = LayoutInflater.from(context);
		phoneNumberList = list;
	}

	@Override
	public int getCount() {
		return phoneNumberList.size();
	}

	@Override
	public Object getItem(int position) {
		return phoneNumberList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		TextView phoneNumberTv;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (null == convertView) {
			viewHolder = new ViewHolder();
			convertView = inflater.inflate(R.layout.phonenumberlist_item, null);
			viewHolder.phoneNumberTv = (TextView) convertView
					.findViewById(R.id.phoneitemText);
			//缓存一个ViewHolder重复利用
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.phoneNumberTv.setText(phoneNumberList.get(position));
		return convertView;
	}

}
