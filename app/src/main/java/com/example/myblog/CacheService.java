package com.example.myblog;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class CacheService extends Service {
    private DownloadBinder binder = new DownloadBinder();
    class DownloadBinder extends Binder {
        public void doCache(final SQLiteDatabase db,final List<Blog> blogList){
            db.beginTransaction();
            try{
                final ContentValues values = new ContentValues();
                List<Blog> cacheBlogList = blogList;
                for(final Blog blog:cacheBlogList){
                    values.put("blogId",blog.getBlogId());
                    values.put("blogTitle",blog.getBlogTitle());
                    values.put("blogSummary",blog.getBlogSummary());
                    values.put("blogPublish",blog.getBlogPublish());
                    values.put("blogLink",blog.getBlogLink());
                    values.put("authorName",blog.getAuthorName());
                    values.put("authorAvatar",blog.getAuthorAvatar());
                    values.put("authorUri",blog.getAuthorUri());
                    values.put("blogComments",blog.getBlogComments());
                    values.put("blogViews",blog.getBlogViews());
                    values.put("blogDiggs",blog.getBlogDiggs());
                    long number = db.insert("Blog",null,values);
                    values.clear();
                    Log.d("Debug:Blog缓存成功",String.valueOf(number));

                    doBlogDetailCache(blog,db);
                }
                db.setTransactionSuccessful();
            }finally{
                db.endTransaction();
            }
        }
    }
    public void doBlogDetailCache(final Blog blog,final SQLiteDatabase db){
        String dbUrl = "http://wcf.open.cnblogs.com/blog/post/body/";
        HttpUtils.sendRequest(dbUrl+blog.getBlogId(),new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response)throws IOException {
                String data = response.body().string();
                String parsedData = HttpUtils.getBlogDetail(data);
                ContentValues values = new ContentValues();
                values.put("blogId",blog.getBlogId());
                values.put("blogDetail",parsedData);
                long count = db.insert("BlogDetail",null,values);
                values.clear();
                Log.d("Debug:BlogDetail缓存成功",String.valueOf(count));
            }
            @Override
            public void onFailure(Call call,IOException e){
                Log.e("Error","MainActivity/doCache/onFailure");
            }
        });
    }
    public CacheService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("Debug:缓存服务已创建","CacheService/onCreate");
    }
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        Log.d("Debug:缓存服务已启动","CacheService/onStartCommand");
        return super.onStartCommand(intent,flags,startId);
    }
    @Override
    public void onDestroy(){
        Log.d("Debug:缓存服务已停止","CacheService/onDestroy");
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return binder;
    }
}
