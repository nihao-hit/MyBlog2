package com.example.myblog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private SwipeRefreshLayout refresh;
    private RecyclerView recyclerView;
    private LinearLayoutManager manager;
    private BlogAdapter adapter;
    private int number = 1;
    private String url;
    private static List<Blog> blogList = new ArrayList<>();
    private MyDatabaseHelper dbHelper = null;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_drawer);
        initView();
    }
    private void initView(){
        drawerLayout = (DrawerLayout)findViewById(R.id.layout_drawer);
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
        dbHelper = new MyDatabaseHelper(MainActivity.this,"MyBlogDB.db",null,1);
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
        boolean flag = HttpUtils.isNetworkConnected(MainActivity.this);
        if (flag){
            url = "http://wcf.open.cnblogs.com/blog/sitehome/paged/"+number+"/20";
            HttpUtils.sendRequest(url,new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response)throws IOException{
                    String data = response.body().string();
                    blogList = HttpUtils.getBlogList(data);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            if(blogList != null) {
                                adapteRecyclerView();
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
                adapteRecyclerView();
                Log.d("Debug:未联网，读取数据库成功","MainActivity/initRecyclerView");
            }
            else{Log.d("Error:未联网，读取数据库失败","MainActivity/initRecyclerView");}
        }
    }
    private void adapteRecyclerView(){
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
                    number++;
                    url = "http://wcf.open.cnblogs.com/blog/sitehome/paged/"+number+"/20";
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
                                        adapter.notifyDataSetChanged();
                                        refresh.setRefreshing(false);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
                break;
        }
        return true;
    }
    private void doCache(){
        Log.d("eee","fuck");
        db.delete("Blog",null,null);
        db.delete("BlogDetail",null,null);
        final ContentValues values = new ContentValues();
        List<Blog> cacheBlogList = blogList;
        for (Blog blog:cacheBlogList){
            Log.d("eee",blog.getBlogId());
        }
        if (cacheBlogList != null){
            for (Blog blog:cacheBlogList){
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
                db.insert("Blog",null,values);
                values.clear();
                Log.d("Debug:Blog缓存成功","MainActivity/doCache");

                String dbUrl = "http://wcf.open.cnblogs.com/blog/post/body/";
                HttpUtils.sendRequest(dbUrl+blog.getBlogId(),new okhttp3.Callback(){
                    @Override
                    public void onResponse(Call call,Response response)throws IOException{
                        String data = response.body().string();
                        String parsedData = HttpUtils.getBlogDetail(data);
                        values.put("blogId",parsedData);
                        db.insert("BlogDetail",null,values);
                        values.clear();
                        Log.d("Debug:BlogDetail缓存成功","MainActivity/doCache/onResponse");
                    }
                    @Override
                    public void onFailure(Call call,IOException e){
                        Log.e("Error","MainActivity/doCache/onFailure");
                    }
                });
            }
            db.close();
        }
        else{
            Log.d("Error:blogList is null","MainActivity/doCache");
        }
    }
}
