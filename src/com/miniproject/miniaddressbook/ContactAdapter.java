package com.miniproject.miniaddressbook;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ContactAdapter extends BaseAdapter{
	List<ContactInfo> list;
	private Context context;
	public boolean isShowNum = false;
	public ContactAdapter(Context context, List<ContactInfo> list ) {
		this.context = context;
		this.list = list;
	}

	
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	class ViewHolder {
		ImageView imPhoto;
		TextView tvItemName;
		TextView tvItemNum;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(list.size() == 0)
			 return null;
		ViewHolder viewHolder = null;
		if (null == convertView) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = inflater.inflate(R.layout.list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.imPhoto = (ImageView) convertView.findViewById(R.id.itemImage);
			viewHolder.tvItemName = (TextView) convertView.findViewById(R.id.itemText);
			viewHolder.tvItemNum = (TextView) convertView.findViewById(R.id.itemText1);
			//缓存一个ViewHolder重复利用
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
	
		ContactInfo person = list.get(position);
		Bitmap bitmap = person.getPhoto();
		viewHolder.imPhoto.setImageBitmap(bitmap);
		viewHolder.tvItemName.setText(person.getName());
		viewHolder.tvItemNum.setText(person.getPhone().get(0));
		return convertView;
	}
}

