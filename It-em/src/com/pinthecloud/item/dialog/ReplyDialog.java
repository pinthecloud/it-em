package com.pinthecloud.item.dialog;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pinthecloud.item.R;
import com.pinthecloud.item.adapter.ReplyListAdapter;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.ListCallback;
import com.pinthecloud.item.interfaces.ReplyCallback;
import com.pinthecloud.item.model.ItNotification;
import com.pinthecloud.item.model.ItUser;
import com.pinthecloud.item.model.Item;
import com.pinthecloud.item.model.Reply;
import com.pinthecloud.item.util.ViewUtil;

public class ReplyDialog extends ItDialogFragment implements ReplyCallback {

	private TextView mTitle;

	private ProgressBar mProgressBar;
	private View mListLayout;
	private TextView mListEmptyView;
	private RecyclerView mListView;
	private ReplyListAdapter mListAdapter;
	private LinearLayoutManager mListLayoutManager;
	private List<Reply> mReplyList;

	private EditText mInputText;
	private Button mInputSubmit;

	private Item mItem;
	private ItUser mUser;


	public static ReplyDialog newInstance(Item item) {
		ReplyDialog dialog = new ReplyDialog();
		Bundle bundle = new Bundle();
		bundle.putParcelable(Item.INTENT_KEY, item);
		dialog.setArguments(bundle);
		return dialog;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mUser = mObjectPrefHelper.get(ItUser.class);
		mItem = getArguments().getParcelable(Item.INTENT_KEY);
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.dialog_reply, container, false);

		mGaHelper.sendScreen(mThisFragment);
		findComponent(view);
		setComponent();
		setButton();
		setList();
		updateList();

		return view;
	}


	@Override
	public void deleteReply(final Reply reply){
		mApp.getAimHelper().del(reply, new EntityCallback<Boolean>() {

			@Override
			public void onCompleted(Boolean result) {
				if(!isAdded()){
					return;
				}

				mListAdapter.remove(reply);
				mItem.setReplyCount(mItem.getReplyCount()-1);

				setTitle(mItem.getReplyCount());
				showList(mItem.getReplyCount());
				ViewUtil.setListHeightBasedOnChildren(mListView, mListAdapter.getItemCount());
			}
		});
	}


	private void findComponent(View view){
		mTitle = (TextView)view.findViewById(R.id.reply_frag_title);
		mListLayout = view.findViewById(R.id.reply_frag_list_layout);
		mProgressBar = (ProgressBar)view.findViewById(R.id.custom_progress_bar);
		mListEmptyView = (TextView)view.findViewById(R.id.reply_frag_list_empty_view);
		mListView = (RecyclerView)view.findViewById(R.id.reply_frag_list);
		mInputText = (EditText)view.findViewById(R.id.reply_frag_inputbar_text);
		mInputSubmit = (Button)view.findViewById(R.id.reply_frag_inputbar_submit);
	}


	private void setComponent(){
		mInputText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String reply = s.toString().trim();
				mInputSubmit.setEnabled(reply.length() > 0);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}


	private void setButton(){
		mInputSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String content = mInputText.getText().toString().trim();
				Reply reply = new Reply(content, mUser.getNickName(), mUser.getId(), mItem.getId());
				ItNotification noti = new ItNotification(mUser.getNickName(), mUser.getId(), mItem.getId(),
						mItem.getWhoMade(), mItem.getWhoMadeId(), reply.getContent(), ItNotification.TYPE.Reply,
						mItem.getImageNumber(), mItem.getMainImageWidth(), mItem.getMainImageHeight());
				submitReply(reply, noti);

				mInputText.setText("");
			}
		});
	}


	private void setList(){
		mListView.setHasFixedSize(true);

		mListLayoutManager = new LinearLayoutManager(mActivity);
		mListView.setLayoutManager(mListLayoutManager);
		mListView.setItemAnimator(new DefaultItemAnimator());

		mReplyList = new ArrayList<Reply>();
		mListAdapter = new ReplyListAdapter(mActivity, mItem, mReplyList);
		mListAdapter.setReplyCallback(this);
		mListView.setAdapter(mListAdapter);
	}


	private void updateList() {
		mProgressBar.setVisibility(View.VISIBLE);
		mListLayout.setVisibility(View.INVISIBLE);

		mAimHelper.list(Reply.class, mItem.getId(), new ListCallback<Reply>() {

			@Override
			public void onCompleted(List<Reply> list, int count) {
				if(!isAdded()){
					return;
				}

				mProgressBar.setVisibility(View.GONE);
				mListLayout.setVisibility(View.VISIBLE);

				mReplyList.clear();
				mListAdapter.addAll(list);

				mItem.setReplyCount(count);
				setTitle(mItem.getReplyCount());
				showList(mItem.getReplyCount());
				ViewUtil.setListHeightBasedOnChildren(mListView, count);
			}
		});
	}


	private void showList(int count){
		if(count > 0){
			mListEmptyView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
		} else {
			mListEmptyView.setVisibility(View.VISIBLE);
			mListView.setVisibility(View.GONE);
		}
	}


	private void submitReply(final Reply reply, ItNotification noti){
		mListAdapter.add(mReplyList.size(), reply);
		showList(mItem.getReplyCount()+1);
		ViewUtil.setListHeightBasedOnChildren(mListView, mListAdapter.getItemCount());

		mAimHelper.add(reply, noti, new EntityCallback<Reply>() {

			@Override
			public void onCompleted(Reply addedReply) {
				if(!isAdded()){
					return;
				}

				mItem.setReplyCount(mItem.getReplyCount()+1);
				setTitle(mItem.getReplyCount());
				mListAdapter.replace(mReplyList.indexOf(reply), addedReply);
			}
		});
	}


	private void setTitle(int replyCount){
		String title = getResources().getString(R.string.comments) + (replyCount > 0 ? " " + replyCount : "");
		mTitle.setText(title);
	}
}
