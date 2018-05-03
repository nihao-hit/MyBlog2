package com.example.myblog;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BlogAdapter adapter;

    public static boolean flag;
    private int page = 1;
    private String url;
    private static List<Blog> blogList = new ArrayList<>();
    private MyDatabaseHelper dbHelper = null;
    public static SQLiteDatabase db;
    private CacheService.DownloadBinder binder;
    private ServiceConnection connection = new ServiceConnection(){
        @Override
        public void onServiceDisconnected(ComponentName name){
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service){
            binder = (CacheService.DownloadBinder)service;
        }
    };
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer);
        initView();
        StartTimer();
    }

    private void initView(){
        drawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer);
        navigationView = (NavigationView)findViewById(R.id.view_navigation);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item){
                switch(item.getItemId()){
                    case R.id.essay:
                        Intent intent = new Intent(MainActivity.this,EssayActivity.class);
                        startActivity(intent);
                        break;

                }
                return true;
            }
        });
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        refresh = (SwipeRefreshLayout)findViewById(R.id.layout_refresh);
        recyclerView = (RecyclerView)findViewById(R.id.view_recycler);
        initDB();
        initToolbar();
        initRecyclerView();
        initSwipeRefreshLayout();
        Log.d("Debug:initView执行成功","MainActivity/initView");
    }

    private void initDB(){
        dbHelper = new MyDatabaseHelper(MainActivity.this,"MyBlogDB.db",null,3);
        db = dbHelper.getWritableDatabase();
    }

    private void initToolbar(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.icon_home);
        }
        Log.d("Debug:initToolbar执行成功","MainActivity/initToolbar");
    }

    private void initRecyclerView(){
        flag = HttpUtils.isNetworkConnected(MainActivity.this);
        if (flag){
            url = "http://wcf.open.cnblogs.com/blog/sitehome/paged/"+page+"/20";
            HttpUtils.sendRequest(url,new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response)throws IOException{
                    String data = response.body().string();
                    blogList = HttpUtils.getBlogList(data);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            if(blogList != null) {
                                initAdapteRecyclerView();
                                Log.d("Debug:已联网，初始化列表","MainActivity/initRecyclerView/onResponse");
                            }
                            else{
                                Toast.makeText(MainActivity.this,"BlogList is Null",Toast.LENGTH_SHORT).show();
                                Log.d("Error:已联网，返回blogList为空","MainActivity/initRecyclerView/onResponse");
                            }
                        }
                    });
                }
                @Override
                public void onFailure(Call call, IOException e){
                    Log.d("Error:获取网络数据失败","MainActivity/initRecyclerView/onFailure");
                }
            });
        }
        else{
            AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
            dialog.setTitle("未联网");
            dialog.setMessage("请连接网络！！！");
            dialog.setCancelable(true);
            dialog.show();
            Cursor cursor = db.query("Blog",null,null,null,null,null,null,null);
            if (cursor.moveToFirst()){
                //
                //blogList = new ArrayList<>();
                do{
                    Blog blog = new Blog();
                    blog.setBlogId(cursor.getString(cursor.getColumnIndex("blogId")));
                    blog.setBlogTitle(cursor.getString(cursor.getColumnIndex("blogTitle")));
                    blog.setBlogSummary(cursor.getString(cursor.getColumnIndex("blogSummary")));
                    blog.setBlogPublish(cursor.getString(cursor.getColumnIndex("blogPublish")));
                    blog.setAuthorName(cursor.getString(cursor.getColumnIndex("authorName")));
                    blog.setAuthorAvatar(cursor.getString(cursor.getColumnIndex("authorAvatar")));
                    blog.setAuthorUri(cursor.getString(cursor.getColumnIndex("authorUri")));
                    blog.setBlogLink(cursor.getString(cursor.getColumnIndex("blogLink")));
                    blog.setBlogComments(cursor.getString(cursor.getColumnIndex("blogComments")));
                    blog.setBlogViews(cursor.getString(cursor.getColumnIndex("blogViews")));
                    blog.setBlogDiggs(cursor.getString(cursor.getColumnIndex("blogDiggs")));
                    blogList.add(blog);
                }while(cursor.moveToNext());
                cursor.close();
                initAdapteRecyclerView();
                Log.d("Debug:未联网，读取数据库成功","MainActivity/initRecyclerView");
            }
            else{Log.d("Error:未联网，读取数据库失败","MainActivity/initRecyclerView");}
        }
    }

    private void initAdapteRecyclerView(){
        manager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(manager);
        adapter = new BlogAdapter(blogList);
        recyclerView.setAdapter(adapter);
    }

    private void initSwipeRefreshLayout(){
        refresh.setColorSchemeResources(R.color.colorPrimary);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                boolean flag = HttpUtils.isNetworkConnected(MainActivity.this);
                if (flag){
                    page++;
                    url = "http://wcf.open.cnblogs.com/blog/sitehome/paged/"+page+"/20";
                    HttpUtils.sendRequest(url,new okhttp3.Callback(){
                        @Override
                        public void onResponse(Call call,Response response)throws IOException{
                            final String data = response.body().string();
                            List<Blog> tmpBlogList = HttpUtils.getBlogList(data);
                            if (tmpBlogList != null){
                                blogList.clear();
                                for (Blog blog:tmpBlogList){
                                    blogList.add(blog);
                                }
                                tmpBlogList.clear();
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run(){
                                        if(adapter == null){
                                            initAdapteRecyclerView();
                                        }
                                        else{
                                            adapter.notifyDataSetChanged();
                                            refresh.setRefreshing(false);
                                        }
                                        Log.d("Debug:刷新数据成功","MainActivity/initSwipeRefreshLayout/onRefresh/onResponse");
                                    }
                                });
                            }
                            else{
                                Log.d("Error:返回blogList为空","MainActivity/initSwipeRefreshLayout/onRefresh/nFailure");
                            }
                        }
                        @Override
                        public void onFailure(Call call,IOException e){
                            Log.d("Error:获取网络数据失败","MainActivity/initSwipeRefreshLayout/onRefresh/nFailure");
                        }
                    });
                }
                else{
                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                    dialog.setTitle("未联网");
                    dialog.setMessage("请连接网络！！！");
                    dialog.setCancelable(true);
                    dialog.show();
                    refresh.setRefreshing(false);
                }
            }
        });
    }

    private void StartTimer(){
        Intent intent = new Intent(this,CacheService.class);
        bindService(intent,connection,BIND_AUTO_CREATE);
        timer = new Timer();
        timer.schedule(new TimerTask(){
            @Override
            public void run(){
                binder.doCache(db,blogList);
            }
        },3000,600000);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        timer.cancel();
        unbindService(connection);
        db.close();
    }
}
