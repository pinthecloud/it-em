package com.pinthecloud.item.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.view.SquareImageView;
import com.squareup.picasso.Picasso;

public class ItemFragment extends ItFragment {

	private SquareImageView mImage;
	private TextView mContent;
	private TextView mDate;
	private TextView mItNumber;
	private Button mDelete;
	private Item item;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = mActivity.getIntent();
		item = intent.getExtras().getParcelable(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_item, container, false);
		setHasOptionsMenu(true);
		setActionBar();
		findComponent(view);
		setText();
		setButton();
		setImageView();
		return view;
	}


	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.item, menu);
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		case R.id.item_it:
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void setActionBar(){
		ActionBar actionBar = mActivity.getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}


	private void findComponent(View view){
		mImage = (SquareImageView)view.findViewById(R.id.item_frag_image);
		mContent = (TextView)view.findViewById(R.id.item_frag_content);
		mDate = (TextView)view.findViewById(R.id.item_frag_date);
		mItNumber = (TextView)view.findViewById(R.id.item_frag_it_number);
		mDelete = (Button)view.findViewById(R.id.item_frag_delete);
	}


	private void setText(){
		mContent.setText(item.getContent());
		mItNumber.setText(""+item.getLikeItCount());
	}


	private void setButton(){
		mDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			}
		});
	}


	private void setImageView(){
		Picasso.with(mActivity)
		.load(BlobStorageHelper.getItemImgUrl(item.getId()))
		.placeholder(R.drawable.ic_launcher)
		.error(R.drawable.ic_launcher)
		.fit()
		.into(mImage);
	}
}
