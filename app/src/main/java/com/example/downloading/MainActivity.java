package com.example.downloading;

import java.util.ArrayList;
import java.util.List;

import com.example.downloading.adapter.FileAdapter;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.service.DownloadService;
import com.example.downloading.util.NotificationUtil;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity{
	private RecyclerView recyclerView;
	private List<FileInfo> mFileList;
	private FileAdapter mAdapter;
	private NotificationUtil mNotificationUtil = null;
	private String urlone = "http://s1.music.126.net/download/android/CloudMusic_3.4.1.133604_official.apk";
	

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

		mFileList = new ArrayList<>();
		recyclerView = (RecyclerView) findViewById(R.id.recyclerv_view);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);
		mAdapter = new FileAdapter(mFileList);
		recyclerView.setAdapter(mAdapter);



		FileInfo fileInfo1 = new FileInfo(0, urlone, getfileName(urlone), 0, 0);

		mFileList.add(fileInfo1);
		

		

		mNotificationUtil = new NotificationUtil(MainActivity.this);

		
		mRecive = new UIRecive();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DownloadService.ACTION_UPDATE);
		intentFilter.addAction(DownloadService.ACTION_FINISHED);
		intentFilter.addAction(DownloadService.ACTION_START);
		registerReceiver(mRecive, intentFilter);


	}



	@Override
	protected void onDestroy() {
		unregisterReceiver(mRecive);
		super.onDestroy();
	}

	private String getfileName(String url) {

		return url.substring(url.lastIndexOf("/") + 1);
	}


	class UIRecive extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (DownloadService.ACTION_UPDATE.equals(intent.getAction())) {

				int finished = intent.getIntExtra("finished", 0);
				int id = intent.getIntExtra("id", 0);
				mAdapter.updataProgress(id, finished);
				mNotificationUtil.updataNotification(id, finished);
			} else if (DownloadService.ACTION_FINISHED.equals(intent.getAction())){

				FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
				mAdapter.updataProgress(fileInfo.getId(), 0);
				Toast.makeText(MainActivity.this, mFileList.get(fileInfo.getId()).getFileName() + "下载完成", Toast.LENGTH_SHORT).show();

				mNotificationUtil.cancelNotification(fileInfo.getId());
			} else if (DownloadService.ACTION_START.equals(intent.getAction())){

				mNotificationUtil.showNotification((FileInfo) intent.getSerializableExtra("fileInfo"));
				
			} 
		}

	}

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
}
