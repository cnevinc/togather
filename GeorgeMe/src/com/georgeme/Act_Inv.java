package com.georgeme;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.android.volley.toolbox.NetworkImageView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Act_Inv extends Activity {
	EditText et_store_id, et_brand, et_model, et_os, et_date, et_notes;
	ImageView iv_add_photos, iv_photo1, iv_photo2, iv_photo3, iv_photo4,
			iv_photo5, iv_photo6;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_invatation);
		
		Restaurant r = new Restaurant();
		r = (Restaurant)this.getIntent().getExtras().getSerializable("shop");
		
		et_brand = (EditText) findViewById(R.id.et_brand);
		et_model = (EditText) findViewById(R.id.et_model);
		et_os = (EditText) findViewById(R.id.et_os);
		et_date = (EditText) findViewById(R.id.et_date);
		et_notes = (EditText) findViewById(R.id.et_notes);

//		iv_add_photos = (ImageView) findViewById(R.id.iv_add_photos);
		
//		et_brand.setText(r.getName());
//		et_model.setText(r.getAddress());
//		et_os.setText(r.getTime());
//
//		et_date.setText(r.getRating());
//		et_notes.setText(r.getDescription());

		et_brand.setText("發福廚房");
		et_model.setText("台北市松山區民權東路三段140巷7號");
		et_os.setText(r.getTime());

		et_date.setText("2014/07/30 19:00");
		et_notes.setText("巨無霸牛肉堡$290 半斤的牛肉，雙重的起司，無比的爽快 大口吃漢堡，不要再管什麼形象了啦");

	}
}
