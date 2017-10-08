package com.example.downloading;

import java.util.ArrayList;
import java.util.List;

import com.example.downloading.adapter.FileAdapter;
import com.example.downloading.db.fileHelper;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.service.DownloadService;
import com.example.downloading.util.NotificationUtil;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Unreal Lover on 2017/10/1.
 */


public class MainActivity extends AppCompatActivity {
	private RecyclerView recyclerView;
	private List<FileInfo> mFileList;
	public List<FileInfo> aFileList;
	private FileAdapter mAdapter;
	private NotificationUtil mNotificationUtil = null;
	public String url ;
	private fileHelper dbHelper;
	int finish;
	int mid = 0;
	public String name;


	//先问系统要一手存储权限
	private UIRecive mRecive;
	private static final int REQUEST_EXTERNAL_STORAGE = 1;
	private static String[] PERMISSIONS_STORAGE = {
			Manifest.permission.READ_EXTERNAL_STORAGE,
			Manifest.permission.WRITE_EXTERNAL_STORAGE
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		verifyStoragePermissions(this);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//
//		//下载列表数据库
//		dbHelper = new fileHelper(this, "Book.db", null, 1);
//
//		dbHelper.getWritableDatabase();
//
//		SQLiteDatabase db = dbHelper.getWritableDatabase();
//


		//初始化recyclerview
		mFileList = new ArrayList<>();
		recyclerView = (RecyclerView) findViewById(R.id.recyclerv_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		mAdapter = new FileAdapter(mFileList);
		recyclerView.setAdapter(mAdapter);
		Log.i("QAQ", "onActivityResult: "+url);

//
//// 查询 Book 表中所有的数据
//		Cursor cursor = db.query("Book", null, null, null, null, null, null);
//		if (cursor.moveToFirst()) {
//			do {
//// 遍历 Cursor 对象，取出数据并打印
//					url = cursor.getString(cursor.getColumnIndex
//						("url"));
//					finish = cursor.getInt(cursor.getColumnIndex("finish"));
//					mid = cursor.getInt(cursor.getColumnIndex("id"));
//				//demo1
//				if(finish!=100) {
//					FileInfo fileInfo1 = new FileInfo(mid, url, getfileName(url), 0, finish);
//					//filelist
//					mFileList.add(fileInfo1);
//				}
//				else{
//					FileInfo fileInfo2 = new FileInfo(mid, url, getfileName(url), 0, finish);
//					aFileList.add(fileInfo2);
//				}
//
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		Log.i("iDDDDDDD",url+"    "+finish+ "onCreate: "+mid);


		registerClipEvents();

	}






	@Override
	protected void onDestroy() {
		unregisterReceiver(mRecive);

		super.onDestroy();

	}

	//获取文件名
	private String getfileName(String url) {
		return url.substring(url.lastIndexOf("/") + 1);
	}



    //定义广播
	class UIRecive extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			//更新进度条
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {
				long finished = intent.getLongExtra("finished", 0);
				int id = intent.getIntExtra("id", 0);
				double rate = intent.getDoubleExtra("rate", 0);
				//更新界面内数据
				mAdapter.updataProgress(id, finished, rate);
				//更新通知栏数据
				mNotificationUtil.updataNotification(id, finished, rate);
				Log.i("yon", "onReceive: "+finished);

			} else if (DownloadService.ACTION_FINISHED.equals(intent.getAction())){
				FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				name = fileInfo.getFileName();
				mAdapter.updataProgress(fileInfo.getId(), 100, 0);
				Toast.makeText(MainActivity.this, mFileList.get(fileInfo.getId()).getFileName() + "下载完成", Toast.LENGTH_SHORT).show();
				//下载结束后cancle通知栏
				delete();
				mFileList.remove(0);
				mAdapter.notifyItemRemoved(0);
				mNotificationUtil.cancelNotification(fileInfo.getId());
			} else if (DownloadService.ACTION_START.equals(intent.getAction())){
				//下载开始时启动通知栏
				FileInfo fileInfo = ((FileInfo) intent.getSerializableExtra("fileInfo"));
				mNotificationUtil.showNotification(fileInfo);
				mAdapter.settotal(fileInfo.getId(),fileInfo.getLength());
			} 
		}
	}
	//获取权限的类
	public static void verifyStoragePermissions(Activity activity) {
		int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if (permission != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(
					activity,
					PERMISSIONS_STORAGE,
					REQUEST_EXTERNAL_STORAGE
			);
		}
	}

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.achieve:
				Intent intente = new Intent(MainActivity.this, AchieveActivity.class);
				intente.putExtra("extra_data", name);
				startActivity(intente);
                break;
            case R.id.add:
				Intent intenta = new Intent(MainActivity.this, AddActivity.class);
				startActivityForResult(intenta, 1);
                break;
            default:
        }
        return true;
    }

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			case 1:
				if (resultCode == RESULT_OK) {
					url = data.getStringExtra("data_return");

//					SQLiteDatabase db = dbHelper.getWritableDatabase();
//					ContentValues values = new ContentValues();
//					// 开始组装第一条数据
//					values.put("finish", 0);
//					values.put("url", url);
//					db.insert("Book", null, values); // 插入第一条数据
//					values.clear();

					//初始化recyclerview
					mFileList = new ArrayList<>();
					recyclerView = (RecyclerView) findViewById(R.id.recyclerv_view);
					LinearLayoutManager layoutManager = new LinearLayoutManager(this);
					recyclerView.setLayoutManager(layoutManager);
					mAdapter = new FileAdapter(mFileList);
					recyclerView.setAdapter(mAdapter);
					Log.i("QAQ", "onActivityResult: "+url);


					//demo1
					FileInfo fileInfo1 = new FileInfo(mid, url, getfileName(url), 0, 0);
					mid++;
					//filelist
					mFileList.add(fileInfo1);

					//初始化notification
					mNotificationUtil = new NotificationUtil(MainActivity.this);

					mRecive = new UIRecive();
					//初始化intentfilter
					IntentFilter intentFilter = new IntentFilter();
					intentFilter.addAction(DownloadService.ACTION_UPDATE);
					intentFilter.addAction(DownloadService.ACTION_FINISHED);
					intentFilter.addAction(DownloadService.ACTION_START);
					registerReceiver(mRecive, intentFilter);
				}
				break;
			default:
		}
	}
	public void delete () {

	}

	private void registerClipEvents() {

		final ClipboardManager manager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		manager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
			@Override
			public void onPrimaryClipChanged() {

				if (manager.hasPrimaryClip() && manager.getPrimaryClip().getItemCount() > 0) {

					CharSequence addedText = manager.getPrimaryClip().getItemAt(0).getText();

					if (addedText != null) {
						url = (String) addedText;
					}
				}
			}
		});
	}
}
