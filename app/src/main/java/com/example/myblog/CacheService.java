package com.example.myblog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class CacheService extends Service {
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
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
