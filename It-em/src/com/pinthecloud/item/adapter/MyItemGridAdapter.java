package com.pinthecloud.item.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;

public class MyItemGridAdapter extends ArrayAdapter<Item> {

	private Context mContext;
	private ItFragment mFrag;


	public MyItemGridAdapter(Context context, ItFragment frag) {
		super(context, 0);
		this.mContext = context;
		this.mFrag = frag;
	}


	private static class ViewHolder {
		public View view;
		public ImageView image;

		public ViewHolder(View view) {
			this.view = view;
			this.image = (ImageView)view.findViewById(R.id.row_my_item_grid_image);
		}
	}


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = onCreateViewHolder(convertView, parent);

		Item item = getItem(position);
		if (item != null) {
			onBindViewHolder(viewHolder, item);
		}

		return viewHolder.view;
	}


	private ViewHolder onCreateViewHolder(View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.row_my_item_grid, parent, false);
		}
		return new ViewHolder(view);
	}


	private void onBindViewHolder(ViewHolder holder, Item item) {
	}
}
