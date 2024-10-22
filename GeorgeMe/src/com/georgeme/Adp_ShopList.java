package com.georgeme;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
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

import com.android.volley.toolbox.NetworkImageView;

import android.app.Activity;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Adp_ShopList extends BaseAdapter {

	List<Restaurant> mShopList;
	Context mContext;

	LayoutInflater inflater;

	public List<Restaurant> getData() {
		return mShopList;
	}

	public Adp_ShopList(Activity activity) {

		mContext = activity;
		DaoSession session = MyApplication.getDBSession(activity);
		RestaurantDao dao = session.getRestaurantDao();
		mShopList = dao.queryBuilder().list();
		Log.e("nevin", "-d------" + mShopList.size());

	}

	@Override
	public int getCount() {
		return mShopList.size();
	}

	static class Holder {
		// ImageView imageView;
		TextView title;
		TextView desc;
		TextView other_info;
		Button readBlog;
		Button preserve;

		NetworkImageView thumbnail;

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public Restaurant getItem(int position) {
		return mShopList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;

		final Restaurant r = mShopList.get(position);
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			holder = new Holder();
			convertView = inflater.inflate(R.layout.list_item_shop, null);
			holder.title = (TextView) convertView.findViewById(R.id.title);
			holder.desc = (TextView) convertView.findViewById(R.id.desc);
			holder.other_info = (TextView) convertView
					.findViewById(R.id.other_info);
			holder.thumbnail = (NetworkImageView) convertView
					.findViewById(R.id.thumbail);
			holder.preserve = (Button) convertView
					.findViewById(R.id.main_action);
			holder.readBlog = (Button) convertView
					.findViewById(R.id.other_action);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.title.setText(r.getName());
		Double d = (Double.valueOf(r.getDistance()));
		holder.desc.setText(r.getAddress() + "\n" + "評比:" + r.getRating()
				+ " 距離:" + Math.round(d * 1000));
		holder.other_info.setText("營業時間:" + r.getTime() + "\n特色:"
				+ r.getDescription());
		if (r.getCover() == null || r.getCover().equals(""))
			holder.thumbnail.setImageDrawable(null);
		else
			holder.thumbnail.setImageUrl(r.getCover(),
					MyVolley.getImageLoader());// setBackgroundResource(null);
		holder.preserve.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
			final
			Dialog mDialog = new Dialog(mContext);
				mDialog.setContentView(R.layout.datetime_picker_dialog);
				mDialog.setTitle("Custom Dialog");
				TimePicker tp = (TimePicker)mDialog.findViewById(R.id.timePicker1);
				DatePicker dp = (DatePicker)mDialog.findViewById(R.id.datePicker1);
				dp.setCalendarViewShown(false);
				mDialog.show();

				Button btnCancel =  (Button)mDialog.findViewById(R.id.bt_cancel);
				Button btnOK =  (Button)mDialog.findViewById(R.id.bt_ok);
		    // Attached listener for login GUI button
				btnCancel.setOnClickListener(new OnClickListener() {
		           @Override
		           public void onClick(View v) {
		        	   mDialog.dismiss();
		           }

		       });
				btnOK.setOnClickListener(new OnClickListener() {
			           @Override
			           public void onClick(final View v) {
			        	   
			        	   
			        	   final String text = " 你有一個來自Nevin Chen的邀請!";
			             //取得Notification服務
			                final NotificationManager notificationManager=(NotificationManager)mContext.getSystemService(Activity.NOTIFICATION_SERVICE);
			                //設定當按下這個通知之後要執行的activity
			                final Intent notifyIntent = new Intent(mContext,Act_Inv.class); 
			                notifyIntent.putExtra("shop", r);
			                notifyIntent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK);
			                final PendingIntent appIntent=PendingIntent.getActivity(mContext,0,
			                                                                  notifyIntent,0);
			                final Notification notification = new Notification();
			                //設定出現在狀態列的圖示
			                notification.icon=R.drawable.ic_launcher;
			                //顯示在狀態列的文字
			                //會有通知預設的鈴聲、振動、light
			                notification.defaults=Notification.DEFAULT_ALL;
			                //設定通知的標題、內容
			                notification.setLatestEventInfo(mContext,"一起揪一下吧",text,appIntent);
			                //送出Notification
			                notification.flags |= Notification.FLAG_AUTO_CANCEL;
			                notificationManager.notify(0,notification);
			               
			                mDialog.dismiss();
			                new DownloadFilesTask().execute(r);
							Toast.makeText(Adp_ShopList.this.mContext, "邀請已送出",
									1).show();
			               
			           }

			       });
		        // Make dialog box visible.
				mDialog.show();
			}
		});
		holder.readBlog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri
						.parse("http://felicia8188999.pixnet.net/blog/post/177540876"));
				mContext.startActivity(i);
				
			}
		});
		return convertView;

	}
	
	
	private class DownloadFilesTask extends AsyncTask<Restaurant , String, String> {

		protected String doInBackground(Restaurant... v) {
			try {
				Restaurant rest = v[0]; 
				String api_url = "http://apptrunks.com/calendar/set-event";
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");

				HttpPost httppost = new HttpPost(api_url);
				
				
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(3);

				nameValuePairs.add(new BasicNameValuePair("start_time", "20140730190000"));
				nameValuePairs.add(new BasicNameValuePair("end_time", "20140730210000"));
				nameValuePairs.add(new BasicNameValuePair("user_id", "3"));
				nameValuePairs.add(new BasicNameValuePair("restaurant_id", "647"));
				nameValuePairs.add(new BasicNameValuePair("location", "台北市松山區民權東路三段140巷7號"));
				nameValuePairs.add(new BasicNameValuePair("summary", "發福廚房"));
				nameValuePairs.add(new BasicNameValuePair("attendees", "[\"cnevinchen@gmail.com\",\"rex@huijun.org\"]"));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				String rString = EntityUtils.toString(response.getEntity());
				
				Log.d("nevin","-----RCODE-----"+response.getStatusLine().getStatusCode());
				Log.d("nevin","-----rString-----"+rString);
				if (response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK)
					return rString;
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
	}


}
