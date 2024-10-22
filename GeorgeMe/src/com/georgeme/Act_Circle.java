/* Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georgeme;

import com.geogreme.view.BaseSwipeListViewListener;
import com.geogreme.view.SwipeListView;
import com.georgeme.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This shows how to draw circles on a map.
 */
public class Act_Circle extends FragmentActivity implements
		OnMarkerDragListener, OnMapLongClickListener {
	// private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);
	HashMap<String, LatLng> mPos = new HashMap<String, LatLng>();

	private static final LatLng SAMPLE_1 = new LatLng(25.0682162, 121.5210069);
	private static final LatLng SAMPLE_2 = new LatLng(25.0563789, 121.5171016);
	private static final LatLng SAMPLE_3 = new LatLng(25.0553195, 121.5321863);

	private LatLng mCenterLatLng = new LatLng(0, 0);

	public static final double RADIUS_OF_EARTH_METERS = 6371009;

	private static final float SCALE = 12.0f;

	private GoogleMap mMap;

	private List<MapUser> mUserLatLng = new ArrayList<MapUser>();

	public Circle mCenterCircle;
	private double mCenterRadius = 0;

	// result list
	Adp_ShopList mAdapter;
	SwipeListView mListView;
	private ProgressDialog mProgressDialog;
	HashMap<Integer, Restaurant> mResult = new HashMap<Integer, Restaurant>();

	// Date time picker
	Dialog mDialog  ; 
	
	class MapUser {
		String id;
		Marker marker;

		public MapUser(LatLng ll, String name) {
			marker = mMap.addMarker(new MarkerOptions().position(ll)
					.snippet(name).draggable(true));
			id = marker.getId();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_circle_demo);
		
		
//		tp.setOnTimeChangedListener(myOnTimechangedListener);
		

		setUpMapIfNeeded();

	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			// Try to obtain the map from the SupportMapFragment.
			mMap = ((SupportMapFragment) getSupportFragmentManager()
					.findFragmentById(R.id.map)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				setUpMap();

			}
		}
	}

	private void sampleData(){
		MapUser user1 = new MapUser(SAMPLE_1, "Nevin Chen");
		mUserLatLng.add(user1);
		MapUser user2 = new MapUser(SAMPLE_2, "Ugene");
		mUserLatLng.add(user2);
		MapUser user3 = new MapUser(SAMPLE_3, "rex");
		mUserLatLng.add(user3);
	}
	
	private void setUpMap() {
		mMap.setOnMarkerDragListener(this);
		mMap.setOnMapLongClickListener(this);

		if (this.getIntent().getSerializableExtra("result") != null) {
			HashMap<Integer, User> result = (HashMap<Integer, User>) this
					.getIntent().getSerializableExtra("result");
			if (result.size()==0){
				sampleData();
			}
			for (User u : result.values()) {
				mUserLatLng.add(new MapUser(new LatLng(Double.valueOf(u
						.getLantitude()), Double.valueOf(u.getLongitude())), u
						.getName()));
			}
		} else {
			sampleData();
		}

		updateCenterLatLng();

		// Move the map so that it is centered on the initial circle
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
				mUserLatLng.get(0).marker.getPosition(), SCALE));
	}

	private void updateCenterLatLng() {
		// initial with one of the LL
		double minLat = SAMPLE_1.latitude;
		double minLng = SAMPLE_1.longitude;
		double maxLat = SAMPLE_1.latitude;
		double maxLng = SAMPLE_1.longitude;

		for (MapUser c : this.mUserLatLng) {
			LatLng ll = c.marker.getPosition();
			if (minLat > ll.latitude)
				minLat = ll.latitude;
			if (minLng > ll.longitude)
				minLng = ll.longitude;
			if (maxLat < ll.latitude)
				maxLat = ll.latitude;
			if (maxLng < ll.longitude)
				maxLng = ll.longitude;

		}

		double centerLat = minLat + ((maxLat - minLat) / 2);
		double centerLng = minLng + ((maxLng - minLng) / 2);
		mCenterLatLng = new LatLng(centerLat, centerLng);
		// Log.d("nevin","1-----minLat:"+minLat+"-----minLng:"+minLng+"---------");
		// Log.d("nevin","2-----maxLat:"+maxLat+"-----maxLng:"+maxLng+"---------");

		double maxRadius = 0;
		// find new Radisu
		for (MapUser c : mUserLatLng) {
			LatLng ll = c.marker.getPosition();
			double distance = CalculationByDistance(mCenterLatLng, ll);
			// Log.d("nevin","3--distance["+distance+"]---mCenterRadius["+mCenterRadius+"]");
			if (distance > this.mCenterRadius) {
				maxRadius = distance;
			}

		}
		Log.d("nevin", "5--mCenterCircle[" + mCenterLatLng.latitude + "/"
				+ mCenterLatLng.longitude + "]---mCenterRadius["
				+ mCenterRadius * 1.1D + "]");

		this.mCenterRadius = maxRadius;
		if (mCenterCircle == null) {
			mCenterCircle = mMap.addCircle(new CircleOptions()
					.center(mCenterLatLng).strokeWidth(1f)
					.fillColor(0x8058FAF4).radius(mCenterRadius * 1.1D));
		}
		mCenterCircle.setRadius(this.mCenterRadius * 1.1D);
		mCenterCircle.setCenter(mCenterLatLng);

		// / 123
	}

	public double CalculationByDistance(LatLng StartP, LatLng EndP) {
		int Radius = 6371;// radius of earth in Km
		double lat1 = StartP.latitude;
		double lat2 = EndP.latitude;
		double lon1 = StartP.longitude;
		double lon2 = EndP.longitude;
		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
				* Math.sin(dLon / 2);
		double c = 2 * Math.asin(Math.sqrt(a));
		return Radius * c * 1000;
	}

	@Override
	public void onMarkerDragStart(Marker marker) {
		onMarkerMoved(marker);
	}

	@Override
	public void onMarkerDragEnd(Marker marker) {
		onMarkerMoved(marker);
	}

	@Override
	public void onMarkerDrag(Marker marker) {
		onMarkerMoved(marker);
	}

	private void onMarkerMoved(Marker marker) {
		String id = marker.getId();
		for (MapUser u : this.mUserLatLng) {
			if (u.id.equals(id)) {
				u.marker.setPosition(marker.getPosition());
			}
		}
		this.updateCenterLatLng();

	}

	@Override
	public void onMapLongClick(LatLng point) {
		// We know the center, let's place the outline at a point 3/4 along the
		// view.
		// View view = ((SupportMapFragment) getSupportFragmentManager()
		// .findFragmentById(R.id.map)).getView();
		// LatLng radiusLatLng = mMap.getProjection().fromScreenLocation(
		// new Point(view.getHeight() * 3 / 4, view.getWidth() * 3 / 4));
		//
		// // ok create it
		// DraggableCircle circle = new DraggableCircle(point, radiusLatLng);
		// mCircles.add(circle);
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
			Intent intent = new Intent(this, Act_ShopList.class);
			intent.putExtra("latlng", this.mCenterLatLng);
			startActivity(intent);
			break;
		}
		return handled;
	}



}
