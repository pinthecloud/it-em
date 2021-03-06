package com.pinthecloud.item.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.ItConstant;
import com.pinthecloud.item.ItIntentService;
import com.pinthecloud.item.R;
import com.pinthecloud.item.dialog.ItAlertDialog;
import com.pinthecloud.item.dialog.ItDialogFragment;
import com.pinthecloud.item.event.GcmRegistrationIdEvent;
import com.pinthecloud.item.helper.PrefHelper;
import com.pinthecloud.item.interfaces.DialogCallback;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;

public class SplashActivity extends ItActivity {

	private final int SPLASH_TIME = 1000;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		if(mApp.isAdmin()){
			Toast.makeText(mThisActivity, "Debugging : " + ItApplication.isDebugging(), Toast.LENGTH_LONG).show();;
		}

		// Remove noti
		NotificationManager notificationManger = (NotificationManager) mThisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManger.cancel(ItIntentService.NOTIFICATION_ID);

		// Check google play service
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(mThisActivity) != ConnectionResult.SUCCESS) {
			installGooglePlayService();
			return;
		}

		// Check update
		float lastVersion = mPrefHelper.getFloat(ItConstant.MANDATORY_APP_VERSION_KEY);
		float clientVersion = getClientAppVersion();
		if(lastVersion < clientVersion){
			mPrefHelper.put(ItConstant.MANDATORY_APP_VERSION_KEY, clientVersion);
		} else if(lastVersion > clientVersion) {
			updateApp();
			return;
		}

		runItem();
	}


	@Override
	public View getToolbarLayout() {
		return null;
	}


	private void runItem() {
		// If mobile id doesn't exist, get it
		ItDevice device = mObjectPrefHelper.get(ItDevice.class);
		if(device.getMobileId().equals(PrefHelper.DEFAULT_STRING)) {
			String mobileId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
			device.setMobileId(mobileId);
			mObjectPrefHelper.put(device);
		}

		// If registration id doesn't exist, get it
		device = mObjectPrefHelper.get(ItDevice.class);
		if(device.getRegistrationId().equals(PrefHelper.DEFAULT_STRING)) {
			setRegistrationId();
			return;
		}

		gotoNextActivity();
	}


	private void installGooglePlayService(){
		String message = getResources().getString(R.string.google_play_services_message);
		ItAlertDialog gcmDialog = ItAlertDialog.newInstance(message, null, null, null, false, true);
		gcmDialog.setCallback(new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ItConstant.GOOGLE_PLAY_SERVICE_APP_ID));
				startActivity(intent);
				finish();
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing
			}
			@Override
			public void doNegative(Bundle bundle) {
				finish();
			}
		});
		gcmDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
	}


	private void updateApp(){
		String message = getResources().getString(R.string.update_app_message);
		ItAlertDialog updateDialog = ItAlertDialog.newInstance(message, null, null, null, false, true);
		updateDialog.setCallback(new DialogCallback() {

			@Override
			public void doPositive(Bundle bundle) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + ItConstant.GOOGLE_PLAY_APP_ID));
				startActivity(intent);
				finish();
			}
			@Override
			public void doNeutral(Bundle bundle) {
				// Do nothing				
			}
			@Override
			public void doNegative(Bundle bundle) {
				finish();
			}
		});
		updateDialog.show(getSupportFragmentManager(), ItDialogFragment.INTENT_KEY);
	}


	private float getClientAppVersion() {
		try {
			String version = mApp.getPackageManager().getPackageInfo(mApp.getPackageName(), 0).versionName;
			return Float.parseFloat(version);
		} catch (NameNotFoundException e) {
			return 0.1f;
		}
	}


	private void gotoNextActivity() {
		new Handler().postDelayed(new Runnable(){

			@Override
			public void run() {
				ItUser user = mObjectPrefHelper.get(ItUser.class);
				Intent intent = new Intent();

				if (!user.checkLoggedIn()){
					// New User
					intent.setClass(mThisActivity, LoginActivity.class);
				} else {
					// Has Loggined
					intent.setClass(mThisActivity, MainActivity.class);
				}

				startActivity(intent);
				finish();
			}
		}, SPLASH_TIME);
	}


	private void setRegistrationId() {
		// Get registration id
		mDeviceHelper.getRegistrationIdAsync(mThisActivity, new EntityCallback<String>() {

			@Override
			public void onCompleted(String entity) {
				if(entity != null){
					onEvent(new GcmRegistrationIdEvent(entity));
				} else {
					// Get registration id in ItBroadCastReceiver.class
					// After get id, goto OnEvent()
				}
			}
		});
	}


	public void onEvent(GcmRegistrationIdEvent event){
		ItDevice deviceInfo = mObjectPrefHelper.get(ItDevice.class);
		deviceInfo.setRegistrationId(event.getRegistrationId());
		mObjectPrefHelper.put(deviceInfo);

		runItem();
	}
}
