package com.example.downloading.adapter;

import java.util.List;

import com.example.downloading.R;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.service.DownloadService;
import com.example.downloading.util.CalculateUtil;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ViewHolder>{
	private Context mContext ;
	private List<FileInfo> mFilelist ;
	public FileAdapter( List<FileInfo> mFilelist) {
		this.mFilelist = mFilelist;
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.item, parent, false);
		final ViewHolder holder = new ViewHolder(view);
		return holder;
	}

	@Override
	public void onBindViewHolder(final ViewHolder holder, int position) {

		final FileInfo fileInfo = mFilelist.get(position);
		//下载速度
		holder.rate.setText(String.format("%.2f", fileInfo.getRate()).toString()+fileInfo.getUtil2()+"/s");
		//文件名
		holder.textview.setText(fileInfo.getFileName());
		//应用内进度条
		holder.progressBar.setMax(100);
		holder.progressBar.setProgress((int) fileInfo.getFinished());
		//应用内下载百分比
		holder.progress.setText(fileInfo.getFinished()+"%");
		//应用内下载进度（*/**表示）
		holder.finish.setText(String.format("%.0f", 0.01*fileInfo.getFinished()*fileInfo.getRealong()).toString()+fileInfo.getUtil()+"/"+fileInfo.getRealong()+fileInfo.getUtil());
		//开始按钮
		holder.startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext = v.getContext();
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra("fileInfo", fileInfo);
				Log.i("lenggggggth", String.valueOf(fileInfo.getLength()));
				mContext.startService(intent);
			}
		});
		//停止按钮
		holder.stopButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext = v.getContext();
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_STOP);
				intent.putExtra("fileInfo", fileInfo);
				mContext.startService(intent);
			}
		});
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemCount() {
		return mFilelist.size();
	}

	//更新进度类
	public void updataProgress(int id, long progress, double rate) {
		FileInfo info = mFilelist.get(id);
		//更新进度条
		info.setFinished(progress);
//更新 下载速度
		CalculateUtil calculate = new CalculateUtil(rate);
		info.setRate(calculate.Rate());
		info.setUtil2(calculate.Unit());
		notifyDataSetChanged();
	}
//	设定总大小
	public void settotal(int id,long length) {
		FileInfo info = mFilelist.get(id);
		int munit = 0;
		String unit = " ";
		CalculateUtil calculate = new CalculateUtil(length);
		info.setRealong((long) calculate.Rate());
		info.setUtil(calculate.Unit());
	}

	 class ViewHolder extends RecyclerView.ViewHolder {
		 TextView textview;
		 Button startButton;
	  	 Button stopButton;
		 ProgressBar progressBar;
		 TextView progress;
         TextView rate;
         TextView finish;

		 ViewHolder(View itemView) {
             super(itemView);
             textview = (TextView) itemView.findViewById(R.id.file_textview);
             startButton = (Button) itemView.findViewById(R.id.start_button);
             stopButton = (Button) itemView.findViewById(R.id.stop_button);
             progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
             progress = (TextView) itemView.findViewById(R.id.progress);
             rate = (TextView) itemView.findViewById(R.id.rate);
             finish = (TextView) itemView.findViewById(R.id.finish);
         }
	}
}
