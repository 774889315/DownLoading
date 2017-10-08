package com.example.downloading.util;

import java.util.HashMap;
import java.util.Map;

import com.example.downloading.MainActivity;
import com.example.downloading.R;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.service.DownloadService;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class NotificationUtil {

	private Context mContext;
	private NotificationManager mNotificationManager = null;
	private Map<Integer, Notification> mNotifications = null;

	public NotificationUtil(Context context) {
		this.mContext = context;
		mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotifications = new HashMap<Integer, Notification>();
	}

	public void showNotification(FileInfo fileInfo) {

		if(!mNotifications.containsKey(fileInfo.getId())){
			Notification notification = new Notification();
			notification.tickerText = fileInfo.getFileName() + "开始下载";
			notification.when = System.currentTimeMillis();
			notification.icon = R.drawable.ic_launcher;
			notification.flags = Notification.FLAG_AUTO_CANCEL;


			Intent intent = new Intent(mContext, MainActivity.class);
			PendingIntent pd = PendingIntent.getActivity(mContext, 0, intent, 0);
			notification.contentIntent = pd;

//			OpenFileUtil.openFile(fileInfo.getFileName(),mContext);

			RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.notification);

			Intent intentStart = new Intent(mContext, DownloadService.class);
			intentStart.setAction(DownloadService.ACTION_START);
			intentStart.putExtra("fileInfo", fileInfo);
			PendingIntent piStart = PendingIntent.getService(mContext, 0, intentStart, 0);
			remoteViews.setOnClickPendingIntent(R.id.start_button, piStart);
			

			Intent intentStop = new Intent(mContext, DownloadService.class);
			intentStop.setAction(DownloadService.ACTION_STOP);
			intentStop.putExtra("fileInfo", fileInfo);
			PendingIntent piStop = PendingIntent.getService(mContext, 0, intentStop, 0);
			remoteViews.setOnClickPendingIntent(R.id.stop_button, piStop);

			remoteViews.setTextViewText(R.id.file_textview, fileInfo.getFileName());

			notification.contentView = remoteViews;

			mNotificationManager.notify(fileInfo.getId(), notification);

			mNotifications.put(fileInfo.getId(), notification);
		}
	}


	public void cancelNotification(int id) {
		mNotificationManager.cancel(id);
		mNotifications.remove(id);
	}

	public void updataNotification(int id, long progress,double rate) {
		Notification notification = mNotifications.get(id);

		if (notification != null) {
			CalculateUtil calculateUtil = new CalculateUtil(rate);
			notification.contentView.setProgressBar(R.id.progressBar2, 100, (int) progress, false);
			notification.contentView.setTextViewText(R.id.rate2,String.format("%.0f", calculateUtil.Rate()).toString()+""+calculateUtil.Unit()+"/s");
			mNotificationManager.notify(id, notification);
		}
	}
}
