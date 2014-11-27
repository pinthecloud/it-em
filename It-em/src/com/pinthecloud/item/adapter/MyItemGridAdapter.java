package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.SquareImageView;

public class MyItemGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		NORMAL,
		HEADER
	}

	private Context mContext;
	private ItFragment mFrag;
	private List<Item> mItemList;
	private int mGridColumnNum;


	public MyItemGridAdapter(Context context, ItFragment frag, List<Item> itemList) {
		this.mContext = context;
		this.mFrag = frag;
		this.mItemList = itemList;
		this.mGridColumnNum = mContext.getResources().getInteger(R.integer.my_page_item_grid_column_num);
	}


	private static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public TextView rank;
		public SquareImageView image;
		public TextView itNumber;
		public TextView reply;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.rank = (TextView)view.findViewById(R.id.row_my_item_grid_rank);
			this.image = (SquareImageView)view.findViewById(R.id.row_my_item_grid_image);
			this.itNumber = (TextView)view.findViewById(R.id.row_my_item_grid_it_number);
			this.reply = (TextView)view.findViewById(R.id.row_my_item_grid_reply);
		}
	}


	private static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public HeaderViewHolder(View view) {
			super(view);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_my_item_grid, parent, false);
			viewHolder = new NormalViewHolder(view);
		} else if(viewType == VIEW_TYPE.HEADER.ordinal()){
			view = inflater.inflate(R.layout.row_my_item_grid_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		}

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, final int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(position-mGridColumnNum);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalText(normalViewHolder, item);
			setNormalButton(normalViewHolder, item);
		}
	}


	@Override
	public int getItemCount() {
		return mItemList.size()+mGridColumnNum;
	}


	@Override
	public int getItemViewType(int position) {
		if (position < mGridColumnNum) {
			return VIEW_TYPE.HEADER.ordinal();
		} else{
			return VIEW_TYPE.NORMAL.ordinal();
		}
	}


	private void setNormalText(NormalViewHolder holder, Item item){
		holder.itNumber.setText(item.getLikeItCount());
		holder.reply.setText(item.getReplyCount());
	}


	private void setNormalButton(NormalViewHolder holder, Item item){
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, ItemActivity.class);
				mContext.startActivity(intent);
			}
		});
	}


	public void add(Item item, int position) {
		mItemList.add(position, item);
		notifyItemInserted(position);
	}


	public void remove(Item item) {
		int position = mItemList.indexOf(item);
		mItemList.remove(position);
		notifyItemRemoved(position);
	}
}