package com.pinthecloud.item.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

import com.pinthecloud.item.activity.ItActivity;

public class ItDialogFragment extends DialogFragment {

	public static final String INTENT_KEY = "DIALOG_INTENT_KEY";
	
	protected ItActivity mActivity;
	protected ItDialogFragment mThisFragment;
	private boolean isShowing = false;


	public ItDialogFragment() {
		super();
		this.mThisFragment = this;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = (ItActivity) getActivity();
	}


	@Override
	public void show(FragmentManager manager, String tag) {
		if(isShowing){
			return;
		}else{
			super.show(manager, tag);
			isShowing = true;
		}
	}


	@Override
	public void onDismiss(DialogInterface dialog) {
		isShowing = false;
		super.onDismiss(dialog);
	}


	public boolean isShowing(){
		return isShowing;
	}
}
