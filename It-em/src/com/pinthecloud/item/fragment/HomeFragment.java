package com.pinthecloud.item.fragment;

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

import com.google.common.collect.Lists;
import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.HomeItemListAdapter;
import com.pinthecloud.item.model.Item;

public class HomeFragment extends ItFragment {

	private ProgressBar mProgressBar;
	private SwipeRefreshLayout mListRefresh;
	private RecyclerView mListView;
	private HomeItemListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Item> mItemList;
	private boolean mIsAdding = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_home, container, false);
		findComponent(view);
		setRefreshLayout();
		setList();
		updateList();
		return view;
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.home_frag_progress_bar);
		mListRefresh = (SwipeRefreshLayout)view.findViewById(R.id.home_frag_item_list_refresh);
		mListView = (RecyclerView)view.findViewById(R.id.home_frag_item_list);
	}


	private void setRefreshLayout(){
		mListRefresh.setColorSchemeResources(R.color.brand_color, R.color.brand_color_dark, R.color.accent_color);
		mListRefresh.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				updateList();
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mItemList = Lists.newArrayList();
		mListAdapter = new HomeItemListAdapter(mActivity, mThisFragment, mItemList);
		mListView.setAdapter(mListAdapter);

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


	private void updateList() {
		mProgressBar.setVisibility(View.GONE);
		mListRefresh.setRefreshing(false);
		mListAdapter.notifyDataSetChanged();
	}


	private void addNextItemList() {
		mIsAdding = true;
		mListAdapter.setHasFooter(true);
		mListAdapter.notifyDataSetChanged();

		mIsAdding = false;
		mListAdapter.setHasFooter(false);
		mListAdapter.notifyDataSetChanged();
	}
}