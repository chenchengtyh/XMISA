package com.nti56.xmisa.adapter;

import com.nti56.xmisa.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MySpinnerAdapter extends BaseAdapter {
	private Context mContext;
	private String[] mList;
	private int mLayoutId;

	public MySpinnerAdapter(Context context, int layoutId, String[] list) {
		this.mContext = context;
		this.mList = list;
		this.mLayoutId = layoutId;
	}

	@Override
	public int getCount() {
		return mList.length;
	}

	@Override
	public Object getItem(int position) {
		return mList[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {//返回选中项
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
			// drawable =
			// mContext.getResources().getDrawable(R.drawable.list_item_select);
			// convertView.setBackground(drawable);
			// mHolder = setViewHolder(convertView);

			// convertView.setTag(R.id.tag_holder, mHolder);
		} else {
			// mHolder = (ViewHolder) convertView.getTag(R.id.tag_holder);
		}
		TextView _TextView1 = (TextView) convertView.findViewById(R.id.spinner_test1);
		_TextView1.setText(mList[position]);

		return convertView;
	}

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {//返回下拉列表项
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(mLayoutId, null);
		}
		TextView label = (TextView) convertView.findViewById(R.id.spinner_test1);
		label.setText((position + 10) + "");
		return convertView;

	}

	public void UpdateData(String[] list) {
		this.mList = list;
	}

}
