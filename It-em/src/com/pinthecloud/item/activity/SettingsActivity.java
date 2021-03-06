package com.pinthecloud.item.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.SettingsFragment;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.util.ViewUtil;

public class SettingsActivity extends ItActivity {

	private View mToolbarLayout;
	private Toolbar mToolbar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_in_right, R.anim.zoom_out);
		setContentView(R.layout.activity_toolbar_frame);

		setToolbar();
		setFragment(new SettingsFragment());
	}


	@Override
	public void onBackPressed() {
		ViewUtil.hideKeyboard(mThisActivity);
		super.onBackPressed();
	}


	@Override
	public void finish() {
		ItUser user = mObjectPrefHelper.get(ItUser.class);
		Intent intent = new Intent().putExtra(ItUser.INTENT_KEY, user);
		setResult(Activity.RESULT_OK, intent);

		super.finish();
		overridePendingTransition(R.anim.zoom_in, R.anim.slide_out_right);
	}


	@Override
	public View getToolbarLayout() {
		return mToolbarLayout;
	}


	private void setToolbar(){
		mToolbarLayout = findViewById(R.id.toolbar_layout);
		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(mToolbar);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		mToolbar.setNavigationOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		
		mToolbarLayout.bringToFront();
	}
}
