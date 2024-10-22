package com.georgeme;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.NetworkImageView;


import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View; 
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adp_UserList extends BaseAdapter {

	List<User> mUserList;
	Context mContext;

	LayoutInflater inflater;

	public List<User> getData(){
		return mUserList;
	}
	public Adp_UserList(Activity activity) {

		mContext = activity;
		DaoSession session = MyApplication.getDBSession(activity);
		UserDao dao = session.getUserDao();
		mUserList = dao.queryBuilder().list();
		Log.e("nevin","-d------"+mUserList.size());
		
	}

	@Override
	public int getCount() {
		return mUserList.size();
	}

	static class Holder {
		// ImageView imageView;
		TextView name;
		TextView location;
		TextView invited_user;
		NetworkImageView roundimageView;

	}
	

	@Override
	public long getItemId(int position) {
		return position;
	}

	public User getItem(int position) {
		return mUserList.get(position);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		
		User u = mUserList.get(position);
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			holder= new Holder();
			convertView = inflater.inflate(R.layout.list_item_user, null);
			holder.name = (TextView) convertView.findViewById(R.id.user_name);
			holder.invited_user = (TextView) convertView.findViewById(R.id.user_invited);
			holder.location = (TextView) convertView
					.findViewById(R.id.user_location);
			holder.roundimageView = (NetworkImageView) convertView
					.findViewById(R.id.user_image);


			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.name.setText(u.getName());
		holder.invited_user.setText(u.getName()+" is invited!");
		holder.location.setText(u.getLantitude()+","+u.getLongitude());
		holder.roundimageView.setImageUrl(u.getPicture(),MyVolley.getImageLoader());//setBackgroundResource(null);

		return convertView;

	}

}
