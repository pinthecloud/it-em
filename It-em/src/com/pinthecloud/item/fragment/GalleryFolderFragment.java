package com.pinthecloud.item.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.GalleryFolderAdapter;
import com.pinthecloud.item.model.Gallery;
import com.pinthecloud.item.model.GalleryFolder;

public class GalleryFolderFragment extends ItFragment {

	private TextView mEmptyView;
	private RecyclerView mListView;
	private GalleryFolderAdapter mListAdapter;
	private LinearLayoutManager mLinearLayoutManager;
	private List<GalleryFolder> mFolderList;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_gallery_folder, container, false);

		mGaHelper.sendScreen(mThisFragment);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		setList();

		if(mFolderList.size() < 1){
			updateList();
		}

		return view;
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setTitle(getResources().getString(R.string.select_album));
	}


	private void findComponent(View view){
		mEmptyView = (TextView)view.findViewById(R.id.gallery_folder_frag_list_empty);
		mListView = (RecyclerView)view.findViewById(R.id.gallery_folder_frag_list);
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mLinearLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mLinearLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		if(mFolderList == null){
			mFolderList = new ArrayList<GalleryFolder>();
		}
		mListAdapter = new GalleryFolderAdapter(mActivity, mFolderList);
		mListView.setAdapter(mListAdapter);
	}


	private void updateList() {
		mFolderList.clear();
		mListAdapter.addAll(getGalleryFolderList());

		if (mFolderList.size() > 0) {
			mEmptyView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		} else {
			mEmptyView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}


	private List<GalleryFolder> getGalleryFolderList() {
		List<GalleryFolder> folderList = new ArrayList<GalleryFolder>();
		
		String[] columns = new String[]{
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DATA
		};
		String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";
		Cursor cursor = mActivity.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				columns, null, null, orderBy);
		
		if (cursor.moveToFirst()) {
			List<String> folderNameList = new ArrayList<String>();
			int bucketColumn = cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
			int dataColumn = cursor.getColumnIndex(MediaStore.Images.Media.DATA);

			do {
				String bucket = cursor.getString(bucketColumn);
				String data = cursor.getString(dataColumn);

				if(!folderNameList.contains(bucket)){
					folderNameList.add(bucket);
					folderList.add(new GalleryFolder(bucket));
				}

				int bucketIndex = folderNameList.indexOf(bucket);
				folderList.get(bucketIndex).getGalleryList().add(new Gallery(data));
			} while (cursor.moveToNext());
		}

		Collections.sort(folderList, new Comparator<GalleryFolder>(){

			@Override
			public int compare(GalleryFolder lhs, GalleryFolder rhs) {
				return lhs.getName().compareToIgnoreCase(rhs.getName());
			}
		});
		
		cursor.close();
		return folderList;
	}
}
