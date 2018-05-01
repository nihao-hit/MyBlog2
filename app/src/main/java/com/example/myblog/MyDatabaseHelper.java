package com.example.myblog;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Administrator on 2018/4/6.
 */

public class MyDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_BLOG = "create table Blog ("
            +"blogId text primary key,"
            +"blogTitle text,"
            +"blogSummary text,"
            +"blogPublish text,"
            +"authorName text,"
            +"authorAvatar text,"
            +"authorUri text,"
            +"blogLink text,"
            +"blogComments text,"
            +"blogViews text,"
            +"blogDiggs text)";
    public static final String CREATE_BLOGDETAIL = "create table BlogDetail("
            +"blogId text primary key,"
            +"blogDetail text)";
    public static final String CREATE_ESSAY = "create table Essay("
            +"essayId integer primary key autoincrement,"
            +"essayTitle text,"
            +"essayDate text,"
            +"essayContent text)";
    private Context mContext;
    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
        mContext = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_BLOG);
        db.execSQL(CREATE_BLOGDETAIL);
        Log.d("Debug:数据库创建成功","MyDatabaseHelper/onCreate");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db,int oldVersion,int newVersion){
        db.execSQL("drop table if exists Blog");
        db.execSQL("drop table if exists BlogDetail");
        db.execSQL("drop table if exists Essay");
        db.execSQL(CREATE_BLOG);
        db.execSQL(CREATE_BLOGDETAIL);
        db.execSQL(CREATE_ESSAY);
        Log.d("Debug:数据库更新成功","MyDatabaseHelper/onUpgrade");
    }
}
