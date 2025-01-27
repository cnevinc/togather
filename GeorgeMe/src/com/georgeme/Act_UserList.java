package com.georgeme;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.geogreme.view.SwipeListView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import com.geogreme.view.BaseSwipeListViewListener;


public class Act_UserList extends Activity {

	static final String TAG = "nevin";

	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	String SENDER_ID = "Your-Sender-ID";

	String mRegid;
	Context mContext;
	AtomicInteger mMsgId = new AtomicInteger();
	GoogleCloudMessaging mGcm;

	Adp_UserList mAdapter;
	SwipeListView mListView;
	private ProgressDialog mProgressDialog;
	HashMap<Integer,User> mResult= new  HashMap<Integer,User>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_list);
		mContext = this;

		
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("loading");
		mProgressDialog.setCancelable(false);
		mProgressDialog.show();

		// Registering the device on server after SignIn
		// registerToGcm();

		mListView = (SwipeListView) findViewById(R.id.store_listview);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		mAdapter = new Adp_UserList(this);
		Log.d("nevin", "----5-----" + mAdapter.getCount());
		
		mListView
				.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

					@Override
					public void onItemCheckedStateChanged(ActionMode mode,
							int position, long id, boolean checked) {
						mode.setTitle("Selected ("
								+ mListView.getCountSelected() + ")");
					}

					@Override
					public boolean onActionItemClicked(ActionMode mode,
							MenuItem item) {
						switch (item.getItemId()) {
						case R.id.menu_delete:
							mListView.dismissSelected();
							mode.finish();
							return true;
						default:
							return false;
						}
					}

					@Override
					public boolean onCreateActionMode(ActionMode mode, Menu menu) {
						MenuInflater inflater = mode.getMenuInflater();
						inflater.inflate(R.menu.menu_choice_items, menu);
						return true;
					}

					@Override
					public void onDestroyActionMode(ActionMode mode) {
						mListView.unselectedChoiceStates();
					}

					@Override
					public boolean onPrepareActionMode(ActionMode mode,
							Menu menu) {
						return false;
					}
				});
		mListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
            	mResult.put(position, mAdapter.getData().get(position));
            	
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            	mResult.remove(position);
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                Log.d("swipe", String.format("onStartOpen %d - action %d", position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));
            }

            @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

            @Override
            public void onDismiss(int[] reverseSortedPositions) {
                for (int position : reverseSortedPositions) {
                	mAdapter.getData().remove(position);
                }
                mAdapter.notifyDataSetChanged();
            }

        });
		mListView.setAdapter(mAdapter);
		updateUser();

//        swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
//        swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
//        swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
//        swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
//        swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
//        swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
	}

	public void updateUser() {

		String api_url = "http://apptrunks.com/fake.json";

		JsonArrayRequest jsonRequest = new JsonArrayRequest(api_url,

		new Response.Listener<JSONArray>() {
			@Override
			public void onResponse(JSONArray response) {
				try {
					for (int i = 0; i < response.length(); i++) {
						JSONObject entry = response.getJSONObject(i);
						User u = new User();
						u.setName(entry.getString("name"));
						u.setPicture(entry.getString("picture"));
						u.setLantitude(entry.getString("lantitude"));
						u.setLongitude(entry.getString("longitude"));

						mAdapter.getData().add(u);
						mAdapter.notifyDataSetChanged();
					}
					mProgressDialog.dismiss();
				} catch (JSONException e) {
				} catch (NumberFormatException e) {
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
		MyVolley.getRequestQueue().add(jsonRequest);
	}

	private void registerToGcm() {
		// Check device for Play Services APK. If check succeeds, proceed with
		// GCM registration.
		if (checkPlayServices()) {
			mGcm = GoogleCloudMessaging.getInstance(this);
			mRegid = getRegistrationId(mContext);

			if (mRegid.isEmpty()) {
				registerInBackground();
			}
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check device for Play Services APK.
		checkPlayServices();
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	/**
	 * Stores the registration ID and the app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Gets the current registration ID for application on GCM service, if there
	 * is one.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and the app versionCode in the application's
	 * shared preferences.
	 */
	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (mGcm == null) {
						mGcm = GoogleCloudMessaging.getInstance(mContext);
					}
					mRegid = mGcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + mRegid;

					// You should send the registration ID to your server over
					// HTTP, so it can use GCM/HTTP or CCS to send messages to
					// your app.
					sendRegistrationIdToBackend();

					// Persist the regID - no need to register again.
					storeRegistrationId(mContext, mRegid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Toast.makeText(mContext,
							"Can't connect to notifcation server", 1).show();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(), msg, 0).show();
			}
		}.execute(null, null, null);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGcmPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(Act_UserList.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_app, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		boolean handled = false;
		switch (item.getItemId()) {
		case android.R.id.home: // Actionbar home/up icon
			finish();
			break;
		case R.id.menu_settings:
			Intent intent = new Intent(this, Act_Circle.class);
			intent.putExtra("result", mResult);
			startActivity(intent);
			break;
		}
		return handled;
	}

}
