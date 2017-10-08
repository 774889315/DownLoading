package com.example.downloading;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.example.downloading.adapter.FileAdapter;
import com.example.downloading.adapter.OkAdapter;
import com.example.downloading.db.fileHelper;
import com.example.downloading.entitis.FileInfo;

import java.util.ArrayList;
import java.util.List;

public class AchieveActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    public List<FileInfo> aFileList;
    private OkAdapter mAdapter;
    public String url ;
    private fileHelper dbHelper;
    int finish;
    int mid = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achieve);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        String name = intent.getStringExtra("extra_data");

//
//		dbHelper = new fileHelper(this, "Book.db", null, 1);
//
//		dbHelper.getWritableDatabase();
//
//		SQLiteDatabase db = dbHelper.getWritableDatabase();

        //初始化recyclerview
        aFileList = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new OkAdapter(aFileList);
        recyclerView.setAdapter(mAdapter);
        FileInfo fileInfo = new FileInfo(0,null , name, 0, 0);
        aFileList.add(fileInfo);
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
//				if(finish == 100) {
//
//					FileInfo fileInfo2 = new FileInfo(mid, url, getfileName(url), 0, finish);
//					aFileList.add(fileInfo2);
//				}
//
//			} while (cursor.moveToNext());
//		}
//		cursor.close();
//		Log.i("iDDDDDDD",url+"    "+finish+ "onCreate: "+mid);
    }
}
