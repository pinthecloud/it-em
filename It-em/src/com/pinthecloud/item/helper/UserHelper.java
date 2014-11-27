package com.pinthecloud.item.helper;

import java.util.List;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.exception.ExceptionManager;
import com.pinthecloud.item.exception.ItException;
import com.pinthecloud.item.fragment.ItFragment;
import com.pinthecloud.item.interfaces.ItEntityCallback;
import com.pinthecloud.item.model.ItUser;

public class UserHelper {
	private MobileServiceClient mClient;
	private MobileServiceTable<ItUser> table;

	public UserHelper(ItApplication context) {
		this.mClient = context.getMobileClient();
		table = mClient.getTable(ItUser.class);
	}

	public void add(final ItFragment frag, ItUser user, final ItEntityCallback<ItUser> callback) {
		table.insert(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser result, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(result);
				} else {
					ExceptionManager.fireException(new ItException(frag, "add", ItException.TYPE.SERVER_ERROR, exception));
				}
			}
		});
	}

	public void get(final ItFragment frag, String id, final ItEntityCallback<ItUser> callback) {
		table.where().field("id").eq(id).execute(new TableQueryCallback<ItUser>() {

			@Override
			public void onCompleted(List<ItUser> result, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if (count == 1) {
						callback.onCompleted(result.get(0));
						return;
					}
				}
				ExceptionManager.fireException(new ItException(frag, "get", ItException.TYPE.SERVER_ERROR, exception));	
			}
		});
	}
}