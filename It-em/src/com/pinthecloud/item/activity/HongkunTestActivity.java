package com.pinthecloud.item.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.pinthecloud.item.R;
import com.pinthecloud.item.fragment.HongkunTestFragment;
import com.pinthecloud.item.fragment.ItFragment;

public class HongkunTestActivity extends ItActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frame);
		setFragment();
	}
	
	
	private void setFragment(){
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
		ItFragment fragment = new HongkunTestFragment();
		fragmentTransaction.add(R.id.activity_container, fragment);
		fragmentTransaction.commit();
	}
}