package com.example.myblog;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TabActivity extends AppCompatActivity{
    TabLayout tab;
    public static ViewPager viewpager;
    private LocalActivityManager manager;
    private MyViewPageAdapter adapter;
    private String[] titles = {"博客","随笔"};
    public MyDatabaseHelper helper;
    public static SQLiteDatabase db;

    private CacheService.DownloadBinder binder;
    private Timer timer;
    private ServiceConnection connection = new ServiceConnection(){
        @Override
        public void onServiceDisconnected(ComponentName name){
            Log.d("Debug","onServiceDisconnected");
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            binder = (CacheService.DownloadBinder)service;
            timer = new Timer();
            timer.schedule(new TimerTask(){
                @Override
                public void run(){
                    Log.d("Debug","onServiceConnected");
                    binder.doCache(TabActivity.db,MainActivity.blogList);
                }
            },3000,120000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);

        helper = new MyDatabaseHelper(this,"MyBlogDB.db",null,1);
        db = helper.getWritableDatabase();
        initService();

        tab = (TabLayout)findViewById(R.id.tab);
        tab.addTab(tab.newTab().setText(titles[0]));
        tab.addTab(tab.newTab().setText(titles[1]));
        tab.setTabMode(TabLayout.MODE_FIXED);
        manager = new LocalActivityManager(this,true);
        manager.dispatchCreate(savedInstanceState);
        viewpager = (ViewPager)findViewById(R.id.viewpager);
        List<View> views = new ArrayList<>();
        Intent intent = new Intent();
        intent.setClass(this,MainActivity.class);
        views.add(manager.startActivity("MainActivity",intent).getDecorView());
        intent.setClass(this,EssayActivity.class);
        views.add(manager.startActivity("EssayActivity",intent).getDecorView());

        adapter = new MyViewPageAdapter(views);
        viewpager.setAdapter(adapter);
        tab.setupWithViewPager(viewpager);
    }

    private void initService(){
        Log.d("Debug","initService");
        Intent intent = new Intent(TabActivity.this,CacheService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
        /*while(binder == null){
            Log.d("Debug","binder is null");
            unbindService(connection);
            bindService(intent,connection,BIND_AUTO_CREATE);
        }*/
    }

    private class MyViewPageAdapter extends PagerAdapter{
        private List<View> views;
        private MyViewPageAdapter(List<View> views){
            this.views = views;
        }
        @Override
        public CharSequence getPageTitle(int position){
            return titles[position];
        }
        @Override
        public int getCount(){
            return views.size();
        }
        @Override
        public boolean isViewFromObject(View arg0,Object arg1){
            return arg0 == arg1;
        }
        @Override
        public Object instantiateItem(ViewGroup container,int position){
            ViewPager pager = (ViewPager)container;
            View view = views.get(position);
            pager.addView(view);
            return view;
        }
        @Override
        public void destroyItem(ViewGroup container,int position,Object object){
            super.destroyItem(container,position,object);
            ViewPager pager = (ViewPager)container;
            View view = views.get(position);
            pager.removeView(view);
        }
    }

    /*@Override
    protected void onResume(){
        super.onResume();
        manager.dispatchResume();
        if(viewpager != null){
            switch(viewpager.getCurrentItem()){
                case 1:
                    Log.d("Debug","TabActivity/onResume/2");
                    Activity activity2 = manager.getActivity("EssayActivity");
                    if(activity2 != null){
                        ((EssayActivity)activity2).myOnResume();
                    }
                    break;
            }
        }
    }*/

    @Override
    protected void onDestroy(){
        super.onDestroy();
        timer.cancel();
        unbindService(connection);
        db.close();
    }
}
