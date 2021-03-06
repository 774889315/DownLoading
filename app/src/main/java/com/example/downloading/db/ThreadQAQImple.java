package com.example.downloading.db;

import java.util.ArrayList;
import java.util.List;

import com.example.downloading.entitis.ThreadInfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Unreal Lover on 2017/10/2.
 */

public class ThreadQAQImple implements ThreadQAQ {
	private DBHelper dbHelper = null;

	public ThreadQAQImple(Context context) {
		super();
		this.dbHelper = DBHelper.getInstance(context);
	}


	@Override
	public synchronized void insertThread(ThreadInfo info) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		ContentValues values = new ContentValues();
		values.put("thread_id", info.getId());
		values.put("url", info.getUrl());
		values.put("start", info.getStart());
		values.put("end", info.getEnd());
		values.put("finished", info.getFinished());
		db.insert("thread_info", null, values);
		
		db.close();
	}


	@Override
	public synchronized void deleteThread(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		db.delete("thread_info", "url = ?", new String[] { url});
		
		db.close();

	}


	@Override
	public synchronized void updateThread(String url, int thread_id, int finished) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();

		db.execSQL("update thread_info set finished = ? where url = ? and thread_id = ?",
				 new Object[]{finished, url, thread_id});
		db.close();
	}
	@Override
	public List<ThreadInfo> queryThreads(String url) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		List<ThreadInfo> list = new ArrayList<ThreadInfo>();
		
		Cursor cursor = db.query("thread_info", null, "url = ?", new String[] { url }, null, null, null);
		while (cursor.moveToNext()) {
			ThreadInfo thread = new ThreadInfo();
			thread.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			thread.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			thread.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			thread.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			thread.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			list.add(thread);
		}
		cursor.close();
		db.close();
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id) {
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query("thread_info", null, "url = ? and thread_id = ?", new String[] { url, thread_id + "" },
				null, null, null);
		boolean exists = cursor.moveToNext();
		db.close();
		cursor.close();
		return exists;
	}
}
