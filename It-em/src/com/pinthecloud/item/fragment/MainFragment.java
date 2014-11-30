package com.pinthecloud.item.fragment;

import android.os.Bundle;
import android.support.v4.util.SparseArrayCompat;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.MainPagerAdapter;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.MainTabHolder;
import com.pinthecloud.item.view.PagerSlidingTabStrip;
import com.pinthecloud.item.view.ParentViewPager;

public class MainFragment extends ItFragment {

	private PagerSlidingTabStrip mTab;
	private ParentViewPager mViewPager;
	private MainPagerAdapter mViewPagerAdapter;

	private boolean[] mIsUpdatedTabs;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		int startTab = mPrefHelper.getInt(PrefHelper.MAIN_EXIT_TAB);
		setToolbar(view);
		findComponent(view);
		setTab(startTab);
		setActionBar(startTab);

		return view;
	}


	private void setToolbar(View view){
		View toolbarShadow = view.findViewById(R.id.main_frag_toolbar_shadow);
		toolbarShadow.bringToFront();
	}


	private void setActionBar(int position){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(mViewPagerAdapter.getPageTitle(position));
	}


	private void findComponent(View view){
		mTab = (PagerSlidingTabStrip) view.findViewById(R.id.main_frag_tab);
		mViewPager = (ParentViewPager) view.findViewById(R.id.main_frag_pager);
	}


	private void setTab(int position){
		mViewPagerAdapter = new MainPagerAdapter(getFragmentManager(), mActivity);
		mViewPager.setOffscreenPageLimit(mViewPagerAdapter.getCount());
		mViewPager.setAdapter(mViewPagerAdapter);
		mViewPager.setCurrentItem(position);

		mIsUpdatedTabs = new boolean[mViewPagerAdapter.getCount()];

		mTab.setViewPager(mViewPager);
		mTab.setStartTab(position);
		mTab.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mPrefHelper.put(PrefHelper.MAIN_EXIT_TAB, position);
				setActionBar(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				if(!mIsUpdatedTabs[position]){
					SparseArrayCompat<MainTabHolder> mainTabHolders = mViewPagerAdapter.getMainTabHolders();
					MainTabHolder fragmentContent = mainTabHolders.valueAt(position);
					fragmentContent.updateTab();
					mIsUpdatedTabs[position] = true;
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
	}
}
