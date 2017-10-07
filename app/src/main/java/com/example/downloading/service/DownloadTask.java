package com.example.downloading.service;

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
import com.example.downloading.db.ThreadQAQ;
import com.example.downloading.db.ThreadQAQImple;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.entitis.ThreadInfo;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.R.id.list;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class DownloadTask {
	private Context mComtext ;
	private FileInfo mFileInfo ;
	private ThreadQAQ QAQ ;
	private long mFinished = 0;
	private int mThreadCount = 1;
	public boolean mIsPause = false;
	private List<DownloadThread> mThreadlist = null;
	public static ExecutorService sExecutorService = Executors.newCachedThreadPool();

	public DownloadTask(Context comtext, FileInfo fileInfo, int threadCount) {
		super();
		this.mThreadCount = threadCount;
		this.mComtext = comtext;
		this.mFileInfo = fileInfo;
		this.QAQ = new ThreadQAQImple(mComtext);
	}

	public void download() {

		List<ThreadInfo> list = QAQ.queryThreads(mFileInfo.getUrl());
		if (list.size() == 0) {
			long length = mFileInfo.getLength();
			long block = length / mThreadCount;
			for (int i = 0; i < mThreadCount; i++) {

				long start = i * block;
				long end = (i + 1) * block - 1;
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
			DownloadTask.sExecutorService.execute(thread);
			mThreadlist.add(thread);
			QAQ.insertThread(info);
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

			QAQ.deleteThread(mFileInfo.getUrl());
			Intent intent = new Intent(DownloadService.ACTION_FINISHED);
			intent.putExtra("fileInfo", mFileInfo);
			mComtext.sendBroadcast(intent);
		}
	}

	class DownloadThread extends Thread {
		private ThreadInfo threadInfo ;
		long totalength = mFileInfo.getLength();

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
//?
				long start = threadInfo.getStart() + threadInfo.getFinished();
				conn.setRequestProperty("Range", "bytes=" + start + "-" + threadInfo.getEnd());
				File file = new File(DownloadService.DownloadPath, mFileInfo.getFileName());
				raf = new RandomAccessFile(file, "rwd");
				raf.seek(start);

				mFinished += threadInfo.getFinished();
				Intent intent = new Intent();
				intent.setAction(DownloadService.ACTION_UPDATE);
				double token = 0;
				int code = conn.getResponseCode();
				if (code == HttpURLConnection.HTTP_PARTIAL) {
					is = conn.getInputStream() ;
					byte[] bt = new byte[1024];
					double len = -1;
					int times = 3;

					long time = System.currentTimeMillis();
					while ((len = is.read(bt)) != -1) {
						raf.write(bt, 0, (int) len);
						mFinished += len;
						threadInfo.setFinished((int) (threadInfo.getFinished() + len));

						if (System.currentTimeMillis() - time > 1000) {
							times++;
							time = System.currentTimeMillis();
							intent.putExtra("finished", mFinished * 100 / (totalength));
							intent.putExtra("id", mFileInfo.getId());
							Log.i("test", (mFinished * 100) / totalength + "");
							Log.i("test2", mFinished  + "");
							Log.i("test3",  totalength + "");
							mComtext.sendBroadcast(intent);
							if(times>=3) {
								double rate = (mFinished - token)/3;
								intent.putExtra("rate", rate);
								token = mFinished;
								times = 0;
							}
						}
						if (mIsPause) {
							QAQ.updateThread(threadInfo.getUrl(), threadInfo.getId(), threadInfo.getFinished());
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
