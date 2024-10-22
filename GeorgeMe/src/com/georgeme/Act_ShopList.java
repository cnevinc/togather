package com.georgeme;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import com.google.android.gms.maps.model.LatLng;

import com.geogreme.view.BaseSwipeListViewListener;

public class Act_ShopList extends Activity {

	static final String TAG = "nevin";

	Adp_ShopList mAdapter;
	SwipeListView mListView;
	private ProgressDialog mProgressDialog;
	HashMap<Integer, Restaurant> mResult = new HashMap<Integer, Restaurant>();
	LatLng mCenterLatLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_user_list);

		LatLng ll = (LatLng)this.getIntent().getParcelableExtra("latlng");
		if (ll!=null)
			this.mCenterLatLng = ll;
		else
			return;
		
		// Shop result
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setMessage("loading");
		mProgressDialog.setCancelable(false);

		mAdapter = new Adp_ShopList(this);

		mListView = (SwipeListView) findViewById(R.id.store_listview);
		mListView.setItemsCanFocus(false);
		mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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

		mListView.setAdapter(mAdapter);

		new DownloadFilesTask().execute();
		// swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
		// swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
		// swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
		// swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
		// swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
		// swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
	}

	private class DownloadFilesTask extends AsyncTask<Void, String, String> {

		@Override
		protected void onPreExecute() {
			mProgressDialog.show();

		}

		protected String doInBackground(Void... v) {
			try {

				String api_url = "http://54.248.92.109/api/nears";
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(api_url);
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
						3);
				nameValuePairs.add(new BasicNameValuePair("latitude", String
						.valueOf(mCenterLatLng.latitude)));
				nameValuePairs.add(new BasicNameValuePair("longitude", String
						.valueOf(mCenterLatLng.longitude)));
				nameValuePairs.add(new BasicNameValuePair("p", "1"));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
					return EntityUtils.toString(response.getEntity());
				else
					return null;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}

		protected void onPostExecute(String result) {

			Log.d("nevin", "-----result----" + result);
			mAdapter.getData().clear();
			// mAdapter.notifyDataSetChanged();
			try {
				JSONObject rs = new JSONObject(result);
				JSONArray entries = rs.getJSONArray("restaurant");
				for (int i = 0; i < entries.length(); i++) {
					JSONObject entry = entries.getJSONObject(i);
					Restaurant r = new Restaurant();
					Log.d("nevin", "--src---" + entry.getString("source"));
					r.setAddress(entry.getString("address"));
					r.setCover(entry.getString("cover"));
					r.setDescription(entry.getString("description"));
					r.setDistance(entry.getString("distance"));
					;
					r.setLatitude(entry.getString("latitude"));
					r.setLongitude(entry.getString("longitude"));
					r.setName(entry.getString("name"));
					r.setPrice(entry.getString("price"));
					r.setRating(entry.getString("rating"));
					r.setTime(entry.getString("time"));

					
					mAdapter.getData().add(r);
					mAdapter.notifyDataSetChanged();
				}
				mProgressDialog.dismiss();
			} catch (JSONException e) {
			} catch (NumberFormatException e) {
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

	}
}
