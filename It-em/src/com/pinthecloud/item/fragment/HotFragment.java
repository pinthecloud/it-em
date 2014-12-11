package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.eowise.recyclerview.stickyheaders.StickyHeadersBuilder;
import com.eowise.recyclerview.stickyheaders.StickyHeadersItemDecoration;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.HotItemListAdapter;
import com.pinthecloud.item.adapter.HotItemListHeaderAdapter;
import com.pinthecloud.item.interfaces.ItListCallback;
import com.pinthecloud.item.model.ItDateTime;
import com.pinthecloud.item.model.Item;

public class HotFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mListRefresh;
	private RecyclerView mListView;
	private HotItemListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;

	private ItDateTime currentDate = ItDateTime.getToday();
	private boolean mIsAdding = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_hot, container, false);
		findComponent(view);
		setRefreshLayout();
		setList();
		updateList();
		return view;
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.hot_frag_progress_bar);
		mListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.hot_frag_item_list_refresh);
		mListView = (RecyclerView)view.findViewById(R.id.hot_frag_item_list);
	}


	private void setRefreshLayout(){
		mListRefresh.setColorSchemeResources(R.color.accent_color);
		mListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				currentDate = ItDateTime.getToday();
				updateList();
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mListAdapter = new HotItemListAdapter(mActivity, mThisFragment, mItemList);
		mListView.setAdapter(mListAdapter);

		StickyHeadersItemDecoration decoration = new StickyHeadersBuilder()
		.setAdapter(mListAdapter)
		.setRecyclerView(mListView)
		.setStickyHeadersAdapter(new HotItemListHeaderAdapter(mActivity, mItemList))
		.build();
		mListView.addItemDecoration(decoration);

		mListView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				int lastVisibleItem = mListLayoutManager.findLastVisibleItemPosition();
				int totalItemCount = mListLayoutManager.getItemCount();

				if (lastVisibleItem >= totalItemCount-3 && !mIsAdding) {
					addNextItemList();
				}
			}
		});
	}


	public void updateList(){
		mAimHelper.getRank10(mThisFragment, currentDate, new ItListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				if(list.size() < 1){
					currentDate = currentDate.getYesterday();
					updateList();
				} else {
					mProgressBar.setVisibility(View.GONE);
					mListRefresh.setRefreshing(false);
					mListRefresh.setVisibility(View.VISIBLE);

					mItemList.clear();
					mListAdapter.addAll(list);
				}
			}
		});
	}


	private void addNextItemList() {
		mIsAdding = true;
		mListAdapter.setHasFooter(true);
		mListAdapter.notifyItemInserted(mItemList.size());

		currentDate = currentDate.getYesterday();
		mAimHelper.getRank10(mThisFragment, currentDate, new ItListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mIsAdding = false;
				mListAdapter.setHasFooter(false);
				mListAdapter.addAll(list);
			}
		});
	}
}
