package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MyItemGridAdapter;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;

public class MyItemFragment extends ItUserPageScrollTabFragment {

	private ProgressBar mProgressBar;
	private RelativeLayout mGridLayout;
	private TextView mGridEmptyView;
	private RecyclerView mGridView;
	private MyItemGridAdapter mGridAdapter;
	private GridLayoutManager mGridLayoutManager;
	private List<Item> mItemList;


	public static ItUserPageScrollTabFragment newInstance(int position, ItUser itUser) {
		ItUserPageScrollTabFragment fragment = new MyItemFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(POSITION_KEY, position);
		bundle.putParcelable(ItUser.INTENT_KEY, itUser);
		fragment.setArguments(bundle);
		return fragment;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(POSITION_KEY);
		mItUser = getArguments().getParcelable(ItUser.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_my_item, container, false);
		findComponent(view);
		setComponent();
		setGrid();
		updateGrid();
		return view;
	}


	@Override
	public void adjustScroll(final int scrollHeight) {
		if (scrollHeight - ItUserPageFragment.mTabHeight != 0 || mGridLayoutManager.findFirstVisibleItemPosition() < mGridLayoutManager.getSpanCount()) {
			mGridLayoutManager.scrollToPositionWithOffset(mGridLayoutManager.getSpanCount(), scrollHeight);
		}
	}


	private void findComponent(View view){
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mGridLayout = (RelativeLayout)view.findViewById(R.id.my_item_frag_grid_layout);
		mGridEmptyView = (TextView)view.findViewById(R.id.my_item_frag_grid_empty_view);
		mGridView = (RecyclerView)view.findViewById(R.id.my_item_frag_grid);
	}


	private void setComponent(){
		mGridEmptyView.setText(getResources().getString(R.string.no_my_item));
	}
	
	
	private void setGrid(){
		mGridView.setHasFixedSize(true);

		int gridColumnNum = getResources().getInteger(R.integer.my_item_grid_column_num);
		mGridLayoutManager = new GridLayoutManager(mActivity, gridColumnNum);
		mGridView.setLayoutManager(mGridLayoutManager);
		mGridView.setItemAnimator(new DefaultItemAnimator());

		mItemList = new ArrayList<Item>();
		mGridAdapter = new MyItemGridAdapter(mActivity, gridColumnNum, mItemList);
		mGridView.setAdapter(mGridAdapter);

		mGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				// Scroll Header
				if (mItUserPageScrollTabHolder != null){
					mItUserPageScrollTabHolder.onScroll(recyclerView, mGridLayoutManager, mPosition);
				}
			}
		});
	}
	
	
	public void updateGrid() {
		mProgressBar.setVisibility(View.VISIBLE);
		mGridLayout.setVisibility(View.GONE);

		mAimHelper.listMyItem(mItUser.getId(), new ListCallback<Item>() {

			@Override
			public void onCompleted(List<Item> list, int count) {
				mProgressBar.setVisibility(View.GONE);
				mGridLayout.setVisibility(View.VISIBLE);

				if(count <= 0){
					mGridEmptyView.setVisibility(View.VISIBLE);
					mGridView.setVisibility(View.GONE);
				} else {
					mGridEmptyView.setVisibility(View.GONE);
					mGridView.setVisibility(View.VISIBLE);
				}

				mItemList.clear();
				mGridAdapter.addAll(list);
				mGridView.scrollToPosition(0);
				mItUserPageScrollTabHolder.updateTabNumber(mPosition, mItemList.size());
			}
		});
	}
}
