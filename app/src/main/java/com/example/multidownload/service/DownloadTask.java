package com.example.multidownload.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.multidownload.db.ThreadDAO;
import com.example.multidownload.db.ThreadDAOImple;
import com.example.multidownload.entitis.FileInfo;
import com.example.multidownload.entitis.ThreadInfo;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadTask {
	private Context mComtext ;
	private FileInfo mFileInfo ;
	private ThreadDAO mDao ;
	private int mFinished = 0;
	private int mThreadCount = 1;
	public boolean mIsPause = false;
	private List<DownloadThread> mThreadlist = null;
	public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

	public DownloadTask(Context comtext, FileInfo fileInfo, int threadCount) {
		super();
		this.mThreadCount = threadCount;
		this.mComtext = comtext;
		this.mFileInfo = fileInfo;
		this.mDao = new ThreadDAOImple(mComtext);
	}

	public void download() {

		List<ThreadInfo> list = mDao.queryThreads(mFileInfo.getUrl());
		if (list.size() == 0) {
			int length = mFileInfo.getLength();
			int block = length / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {

				int start = i * block;
				int end = (i + 1) * block - 1;
				if (i == mThreadCount - 1) {
					end = length - 1;
				}
				ThreadInfo threadInfo = new ThreadInfo(i, mFileInfo.getUrl(), start, end, 0);
				list.add(threadInfo);
			}
		}
		mThreadlist = new ArrayList<DownloadThread>();
		for (ThreadInfo info : list) {
			DownloadThread thread = new DownloadThread(info);
//			thread.start();

			DownloadTask.sExecutorService.execute(thread);
			mThreadlist.add(thread);

			mDao.insertThread(info);
		}
	}

	public synchronized void checkAllFinished() {
		boolean allFinished = true;
		for (DownloadThread thread : mThreadlist) {
			if (!thread.isFinished) {
				allFinished = false;
				break;
			}
		}
		if (allFinished == true) {

			mDao.deleteThread(mFileInfo.getUrl());

			Intent intent = new Intent(DownloadService.ACTION_FINISHED);
			intent.putExtra("fileInfo", mFileInfo);
			mComtext.sendBroadcast(intent);

		}
	}

	class DownloadThread extends Thread {
		private ThreadInfo threadInfo = null;

		public boolean isFinished = false;

		public DownloadThread(ThreadInfo threadInfo) {
			this.threadInfo = threadInfo;
		}

		@Override
		public void run() {

			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream is = null;
			try {
				URL url = new URL(mFileInfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5 * 1000);
				conn.setRequestMethod("GET");

				int start = threadInfo.getStart() + threadInfo.getFinished();

				conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
				File file = new File(DownloadService.DownloadPath, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);
				mFinished += threadInfo.getFinished();
				Intent intent = new Intent();
				intent.setAction(DownloadService.ACTION_UPDATE);
				int code = conn.getResponseCode();
				if (code == HttpURLConnection.HTTP_PARTIAL) {
					is = conn.getInputStream();
					byte[] bt = new byte[1024];
					int len = -1;

					long time = System.currentTimeMillis();
					while ((len = is.read(bt)) != -1) {
						raf.write(bt, 0, len);

						mFinished += len;

						threadInfo.setFinished(threadInfo.getFinished() + len);

						if (System.currentTimeMillis() - time > 1000) {
							time = System.currentTimeMillis();

								intent.putExtra("finished", mFinished * 100 / mFileInfo.getLength());

								intent.putExtra("id", mFileInfo.getId());
								Log.i("test", mFinished * 100 / mFileInfo.getLength() + "");
								mComtext.sendBroadcast(intent);
						}
						if (mIsPause) {
							mDao.updateThread(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
							return;
						}
					}
				}

				isFinished = true;

				checkAllFinished();

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect();
				}
				try {
					if (is != null) {
						is.close();
					}
					if (raf != null) {
						raf.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			super.run();
		}
	}

}
