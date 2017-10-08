package com.example.downloading.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * Created by Unreal Lover on 2017/10/8.
 */

public class fileHelper extends SQLiteOpenHelper {
    public static final String CREATE_BOOK = "create table Book ("
                + "id integer primary key autoincrement, "
                + "finish integer, "
                + "url text)";
    private Context mContext;
    public fileHelper(Context context, String name,
                      SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BOOK);
        Toast.makeText(mContext, "Create succeeded", Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
