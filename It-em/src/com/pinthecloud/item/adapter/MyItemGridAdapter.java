package com.pinthecloud.item.adapter;

import java.util.List;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.ItActivity;
import com.pinthecloud.item.activity.ItemActivity;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.helper.GAHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.util.ImageUtil;

public class MyItemGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private enum VIEW_TYPE{
		HEADER,
		NORMAL
	}

	private ItApplication mApp;
	private ItActivity mActivity;
	private ItFragment mFrag;

	private List<Item> mItemList;
	private int mGridColumnNum;
	private int mHeaderHeight;


	public MyItemGridAdapter(ItActivity activity, ItFragment frag, int gridColumnNum, List<Item> itemList) {
		this.mApp = ItApplication.getInstance();
		this.mActivity = activity;
		this.mFrag = frag;
		this.mItemList = itemList;
		this.mGridColumnNum = gridColumnNum;
	}


	public static class HeaderViewHolder extends RecyclerView.ViewHolder {
		public HeaderViewHolder(View view) {
			super(view);
		}
	}


	public static class NormalViewHolder extends RecyclerView.ViewHolder {
		public View view;
		public ImageView itemImage;

		public NormalViewHolder(View view) {
			super(view);
			this.view = view;
			this.itemImage = (ImageView)view.findViewById(R.id.row_my_item_item_image);
		}
	}


	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = null;
		ViewHolder viewHolder = null;
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());

		if(viewType == VIEW_TYPE.HEADER.ordinal()){
			view = inflater.inflate(R.layout.row_my_item_grid_header, parent, false);
			viewHolder = new HeaderViewHolder(view);
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			view = inflater.inflate(R.layout.row_my_item_grid, parent, false);
			viewHolder = new NormalViewHolder(view);
		} 

		return viewHolder;
	}


	@Override
	public void onBindViewHolder(ViewHolder holder, int position) {
		int viewType = getItemViewType(position);
		if(viewType == VIEW_TYPE.HEADER.ordinal()){
			HeaderViewHolder headerViewHolder = (HeaderViewHolder)holder;
			headerViewHolder.itemView.getLayoutParams().height = mHeaderHeight;
			headerViewHolder.itemView.requestLayout();
		} else if(viewType == VIEW_TYPE.NORMAL.ordinal()){
			Item item = mItemList.get(position-mGridColumnNum);
			NormalViewHolder normalViewHolder = (NormalViewHolder)holder;
			setNormalButton(normalViewHolder, item);
			setNormalImageView(normalViewHolder, item);
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


	private void setNormalButton(final NormalViewHolder holder, final Item item){
		holder.view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.getGaHelper().sendEvent(mFrag.getClass().getSimpleName(), GAHelper.VIEW_ITEM, GAHelper.MY_PAGE);

				Intent intent = new Intent(mActivity, ItemActivity.class);
				intent.putExtra(Item.INTENT_KEY, item);
				mActivity.startActivity(intent);
			}
		});
	}


	private void setNormalImageView(NormalViewHolder holder, Item item) {
		mApp.getPicasso()
		.load(BlobStorageHelper.getItemImageUrl(item.getId()+ImageUtil.ITEM_THUMBNAIL_IMAGE_POSTFIX))
		.placeholder(R.drawable.feed_loading_default_img)
		.fit()
		.into(holder.itemImage);
	}


	public void addAll(List<Item> itemList) {
		mItemList.addAll(itemList);
		notifyDataSetChanged();
	}


	public void notifyHeader(int headerHeight){
		this.mHeaderHeight = headerHeight;
		for(int i=0 ; i<mGridColumnNum ; i++){
			notifyItemChanged(i);
		}
	}
}
