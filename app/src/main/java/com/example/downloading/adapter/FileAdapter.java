package com.example.downloading.adapter;

import java.util.List;


import com.example.downloading.R;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.service.DownloadService;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

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
		holder.textview.setText(fileInfo.getFileName());
		holder.progressBar.setMax(100);
		holder.progressBar.setProgress(fileInfo.getFinished());
		holder.startButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext = v.getContext();
				Intent intent = new Intent(mContext, DownloadService.class);
				intent.setAction(DownloadService.ACTION_START);
				intent.putExtra("fileInfo", fileInfo);
				mContext.startService(intent);
			}
		});
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

	public void updataProgress(int id, int progress) {
		FileInfo info = mFilelist.get(id);
		info.setFinished(progress);
		notifyDataSetChanged();
	}

	 class ViewHolder extends RecyclerView.ViewHolder {
		TextView textview;
		Button startButton;
		Button stopButton;
		ProgressBar progressBar;

		 ViewHolder(View itemView) {
			 super(itemView);
			 textview = (TextView) itemView.findViewById(R.id.file_textview);
			 startButton = (Button) itemView.findViewById(R.id.start_button);
			 stopButton = (Button) itemView.findViewById(R.id.stop_button);
			 progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
		}
	}

}
