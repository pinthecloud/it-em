package com.pinthecloud.item.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.helper.BlobStorageHelper;
import com.pinthecloud.item.model.ItUser;
import com.squareup.picasso.Picasso;

public class ProfileImageDialog extends ItDialogFragment {

	private ImageButton mClose;
	private TextView mNickName;
	private ImageView mProfileImage;
	private ItUser mUser;


	public static ProfileImageDialog newInstance(ItUser itUser) {
		ProfileImageDialog dialog = new ProfileImageDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(ItUser.INTENT_KEY, itUser);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = getArguments().getParcelable(ItUser.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_profile_image, container, false);
		mGaHelper.sendScreen(mThisFragment);
		findComponent(view);
		setComponent();
		return view;
	}


	@Override
	public void onStart() {
		super.onStart();
		setImageView();
	}


	@Override
	public void onStop() {
		super.onStop();
		mProfileImage.setImageBitmap(null);
	}


	private void findComponent(View view){
		mClose = (ImageButton)view.findViewById(R.id.profile_image_frag_close);
		mNickName = (TextView)view.findViewById(R.id.profile_image_frag_nick_name);
		mProfileImage = (ImageView)view.findViewById(R.id.profile_image_frag_profile_image);
	}


	private void setComponent(){
		mNickName.setText(mUser.getNickName());

		mClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}


	private void setImageView(){
		Picasso.with(mProfileImage.getContext())
		.load(BlobStorageHelper.getUserProfileUrl(mUser.getId()))
		.placeholder(R.drawable.profile_default_img)
		.fit()
		.into(mProfileImage);
	}
}
