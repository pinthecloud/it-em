package com.pinthecloud.item.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.kakao.APIErrorResult;
import com.kakao.LogoutResponseCallback;
import com.kakao.SessionCallback;
import com.kakao.UserManagement;
import com.kakao.exception.KakaoException;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.R;
import com.pinthecloud.item.activity.BeProActivity;
import com.pinthecloud.item.activity.LoginActivity;
import com.pinthecloud.item.activity.MainActivity;
import com.pinthecloud.item.activity.ProfileSettingsActivity;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class SettingsFragment extends ItFragment {

	private final int PROFILE_SETTINGS = 0;

	private View mProfileSettings;
	private View mBePro;

	private ToggleButton mNotiMyItem;
	private ToggleButton mNotiItItem;
	private ToggleButton mNotiReplyItem;

	private TextView mNickName;
	private RelativeLayout mLogout;

	private ItUser mMyItUser;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mMyItUser = mObjectPrefHelper.get(ItUser.class);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_settings, container, false);
		setHasOptionsMenu(true);
		findComponent(view);
		setProfile();
		setButton();
		setAdminComponent(view);
		return view;
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case PROFILE_SETTINGS:
			if (resultCode == Activity.RESULT_OK){
				mMyItUser = data.getParcelableExtra(ItUser.INTENT_KEY);
				setProfile();
			}
			break;
		}
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			mActivity.onBackPressed();
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private void findComponent(View view){
		mProfileSettings = view.findViewById(R.id.settings_frag_profile_settings);
		mBePro = view.findViewById(R.id.settings_frag_be_pro);
		mNotiMyItem = (ToggleButton)view.findViewById(R.id.settings_frag_noti_my_item);
		mNotiItItem = (ToggleButton)view.findViewById(R.id.settings_frag_noti_it_item);
		mNotiReplyItem = (ToggleButton)view.findViewById(R.id.settings_frag_noti_reply_item);
		mNickName = (TextView)view.findViewById(R.id.settings_frag_nick_name);
		mLogout = (RelativeLayout)view.findViewById(R.id.settings_frag_logout);
	}


	private void setProfile(){
		mNickName.setText(mMyItUser.getNickName());
	}


	private void setButton(){
		mProfileSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, ProfileSettingsActivity.class);
				startActivityForResult(intent, PROFILE_SETTINGS);
			}
		});

		mBePro.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mActivity, BeProActivity.class);
				startActivity(intent);
			}
		});

		mNotiMyItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
			}
		});

		mNotiItItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		mNotiReplyItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		mLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = getResources().getString(R.string.logout_message);
				ItAlertDialog logoutDialog = ItAlertDialog.newInstance(message, null, null, true);

				final EntityCallback<Boolean> logoutCallback = new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						if (entity) {
							mApp.dismissProgressDialog();
							removePreference();

							Intent intent = new Intent(mActivity, LoginActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(intent);
						}
					}
				};

				logoutDialog.setCallback(new DialogCallback() {

					@Override
					public void doPositiveThing(Bundle bundle) {
						mApp.showProgressDialog(mActivity);
						if (mMyItUser.getPlatform().equalsIgnoreCase(ItUser.PLATFORM.FACEBOOK.toString())) {
							facebookLogout(logoutCallback);
						} else if (mMyItUser.getPlatform().equalsIgnoreCase(ItUser.PLATFORM.KAKAO.toString())) {
							kakaoLogout(logoutCallback);
						}
					}
					@Override
					public void doNegativeThing(Bundle bundle) {
						// Do nothing
					}
				});
				logoutDialog.show(getFragmentManager(), ItDialogFragment.INTENT_KEY);
			}
		});
	}


	private void setAdminComponent(View view) {
		if (!mApp.isAdmin()) return;

		RadioGroup rg = new RadioGroup(mActivity);
		RadioButton real = new RadioButton(mActivity);
		RadioButton test = new RadioButton(mActivity);

		real.setId(100001);
		real.setText("Real");
		real.setChecked(!ItApplication.isDebugging());

		test.setId(100002);
		test.setText("Test");
		test.setChecked(ItApplication.isDebugging());

		rg.addView(real);
		rg.addView(test);

		LinearLayout layout = (LinearLayout)view.findViewById(R.id.settings_frag_layout);
		layout.addView(rg);

		real.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);
				mPrefHelper.put(ItConstant.CURRENT_MODE, ItApplication.REAL);
				mApp.switchClient(new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						switchClientCallback();
					}
				});
			}
		});

		test.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mApp.showProgressDialog(mActivity);
				mPrefHelper.put(ItConstant.CURRENT_MODE, ItApplication.TEST);
				mApp.switchClient(new EntityCallback<Boolean>() {

					@Override
					public void onCompleted(Boolean entity) {
						switchClientCallback();
					}
				});
			}
		});
	}


	private void switchClientCallback(){
		mApp.dismissProgressDialog();
		Intent intent = new Intent(mActivity, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}


	private void facebookLogout(EntityCallback<Boolean> callback){
		com.facebook.Session session = com.facebook.Session.getActiveSession();
		if (session == null) {
			session = new com.facebook.Session(mActivity);
			com.facebook.Session.setActiveSession(session);
		}
		session.closeAndClearTokenInformation();
		callback.onCompleted(true);
	}


	private void kakaoLogout(final EntityCallback<Boolean> callback){
		boolean initalizing = com.kakao.Session.initializeSession(mActivity, new SessionCallback() {

			@Override
			public void onSessionOpened() {
			}
			@Override
			public void onSessionClosed(final KakaoException exception) {
			}
		});

		if (!initalizing && com.kakao.Session.getCurrentSession().isOpened()){
			UserManagement.requestLogout(new LogoutResponseCallback() {

				@Override
				protected void onSuccess(long userId) {
					callback.onCompleted(true);
				}

				@Override
				protected void onFailure(APIErrorResult errorResult) {
					callback.onCompleted(false);
					EventBus.getDefault().post(new ItException("onFailure", ItException.TYPE.INTERNAL_ERROR, errorResult));
				}
			});
		}
	}


	private void removePreference(){
		mObjectPrefHelper.remove(ItUser.class);
	}
}
