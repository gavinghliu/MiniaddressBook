package com.miniproject.miniaddressbook;

import java.util.List;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

	private Context myContext;
	public List<String> groupList;
	public List<List<ContactInfo>> childList;
	
	public ExpandableListAdapter(Context context,List<String> groupList,List<List<ContactInfo>> childList) {
		this.myContext = context;
		this.groupList = groupList;
		this.childList = childList;
	}
	
	@Override
	public Object getChild(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getChildId(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	class ViewHolder {
		ImageView imPhoto;
		TextView tvItemName;
		TextView tvItemNum;
	}
	
	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		if(childList.size() == 0)
			 return null;
		ViewHolder viewHolder = null;
		if (null == convertView) {
			LayoutInflater inflater = LayoutInflater.from(myContext);
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
	
		ContactInfo person = childList.get(groupPosition).get(childPosition);
		Bitmap bitmap = person.getPhoto();
		viewHolder.imPhoto.setImageBitmap(bitmap);
		viewHolder.tvItemName.setText(person.getName());
		viewHolder.tvItemNum.setText(person.getPhone().get(0));
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return childList.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return groupList.size();
	}

	@Override
	public long getGroupId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) myContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
			convertView = inflater.inflate(R.layout.groupitem, null);
		}

		TextView tvGroupName = (TextView) convertView.findViewById(R.id.group);
		tvGroupName.setText(groupList.get(groupPosition));

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int arg0, int arg1) {
		// TODO Auto-generated method stub
		return true;
	}

}
