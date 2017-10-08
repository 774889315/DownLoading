package com.example.downloading.adapter;

import java.util.List;

import com.example.downloading.R;
import com.example.downloading.entitis.FileInfo;
import com.example.downloading.service.DownloadService;
import com.example.downloading.util.CalculateUtil;
import com.example.downloading.util.OpenFileUtil;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
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

    public class OkAdapter extends RecyclerView.Adapter<com.example.downloading.adapter.OkAdapter.ViewHolder>{
        private Context mContext ;
        private List<FileInfo> mFilelist ;
        public OkAdapter( List<FileInfo> mFilelist) {
            this.mFilelist = mFilelist;
        }

        @Override
        public com.example.downloading.adapter.OkAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_2, parent, false);
            final com.example.downloading.adapter.OkAdapter.ViewHolder holder = new com.example.downloading.adapter.OkAdapter.ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final com.example.downloading.adapter.OkAdapter.ViewHolder holder, int position) {

            final FileInfo fileInfo = mFilelist.get(position);
            //文件名
            holder.textview.setText(fileInfo.getFileName());
             //开始按钮
            holder.openButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mContext = v.getContext();
                    OpenFileUtil.openFile(Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/download/"+fileInfo.getFileName(),mContext);
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

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView textview;
            Button openButton;
            TextView progress;

            ViewHolder(View itemView) {
                super(itemView);
                textview = (TextView) itemView.findViewById(R.id.file_textview);
                openButton = (Button) itemView.findViewById(R.id.open_button);
                progress = (TextView) itemView.findViewById(R.id.progress);
            }
        }
    }

