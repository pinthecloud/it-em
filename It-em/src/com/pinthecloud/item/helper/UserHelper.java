package com.pinthecloud.item.helper;

import java.net.HttpURLConnection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microsoft.windowsazure.mobileservices.ApiJsonOperationCallback;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.TableQueryCallback;
import com.pinthecloud.item.ItApplication;
import com.pinthecloud.item.event.ItException;
import com.pinthecloud.item.interfaces.EntityCallback;
import com.pinthecloud.item.interfaces.PairEntityCallback;
import com.pinthecloud.item.model.ItDevice;
import com.pinthecloud.item.model.ItUser;

import de.greenrobot.event.EventBus;

public class UserHelper {

	private final String SIGNIN = "signin";
	private final String UPDATE_USER = "update_user";

	private ItApplication mApp;
	private MobileServiceClient mClient;
	private MobileServiceTable<ItUser> userTable;


	public UserHelper(ItApplication app) {
		this.mApp = app;
		this.mClient = app.getMobileClient();
		this.userTable = mClient.getTable(ItUser.class);
	}


	public void setMobileClient(MobileServiceClient client) {
		this.mClient = client;
		this.userTable = mClient.getTable(ItUser.class);
	}


	public void signin(ItUser user, ItDevice device, final PairEntityCallback<ItUser, ItDevice> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("signin", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		Gson gson = new Gson();
		JsonObject userJson = gson.fromJson(gson.toJson(user), JsonObject.class);
		JsonObject deviceJson = gson.fromJson(gson.toJson(device), JsonObject.class);
		JsonObject jo = new JsonObject();
		jo.add("user", userJson);
		jo.add("device", deviceJson);

		mClient.invokeApi(SIGNIN, jo, new ApiJsonOperationCallback() {

			@Override
			public void onCompleted(JsonElement _json, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					JsonObject json = _json.getAsJsonObject();
					ItUser user = new Gson().fromJson(json.get("user"), ItUser.class);
					ItDevice device = new Gson().fromJson(json.get("device"), ItDevice.class);
					callback.onCompleted(user, device);
				} else {
					EventBus.getDefault().post(new ItException("signin", ItException.TYPE.INTERNAL_ERROR, response));
				}
			}
		});
	}


	public void add(ItUser user, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("add", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		userTable.insert(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser user, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(user);
				} else {
					EventBus.getDefault().post(new ItException("add", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}


	public void get(String id, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("get", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		userTable.where().field("id").eq(id).execute(new TableQueryCallback<ItUser>() {

			@Override
			public void onCompleted(List<ItUser> userList, int count, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					if(userList.size() == 0){
						callback.onCompleted(null);	
					} else {
						callback.onCompleted(userList.get(0));
					}
				} else {
					EventBus.getDefault().post(new ItException("get", ItException.TYPE.INTERNAL_ERROR, exception));	
				}
			}
		});
	}


	public void update(ItUser user, final EntityCallback<ItUser> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("update", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		userTable.update(user, new TableOperationCallback<ItUser>() {

			@Override
			public void onCompleted(ItUser user, Exception exception,
					ServiceFilterResponse response) {
				if (exception == null) {
					callback.onCompleted(user);
				} else {
					EventBus.getDefault().post(new ItException("update", ItException.TYPE.INTERNAL_ERROR, exception));
				}
			}
		});
	}


	public void updateUser(ItUser user, final EntityCallback<Integer> callback) {
		if(!mApp.isOnline()){
			EventBus.getDefault().post(new ItException("updateUser", ItException.TYPE.NETWORK_UNAVAILABLE));
			return;
		}

		Gson gson = new Gson();
		JsonObject userJson = gson.fromJson(gson.toJson(user), JsonObject.class);
		JsonObject jo = new JsonObject();
		jo.add("user", userJson);

		mClient.invokeApi(UPDATE_USER, jo, new ApiJsonOperationCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public void onCompleted(JsonElement _json, Exception exception, ServiceFilterResponse response) {
				if(exception == null){
					callback.onCompleted(HttpURLConnection.HTTP_OK);
				} else {
					callback.onCompleted(response.getStatus().getStatusCode());
				}
			}
		});
	}
}
