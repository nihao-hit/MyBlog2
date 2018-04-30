package com.example.myblog;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class BlogDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing;
    private TextView blogDetail;
    private String TITLE;
    private String ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_blog_detail);
        initIntent();
        initView();
    }
    private void initIntent(){
        Intent intent = getIntent();
        TITLE = intent.getStringExtra("TITLE");
        ID = intent.getStringExtra("ID");
    }
    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        collapsing = (CollapsingToolbarLayout)findViewById(R.id.layout_collapsing);
        blogDetail = (TextView)findViewById(R.id.blogDetail);
        initCollapsingToolbarLayout();
        initBlogDetail();
    }
    private void initCollapsingToolbarLayout(){
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsing.setTitle(TITLE);
    }
    private void initBlogDetail(){
        String url = "http://wcf.open.cnblogs.com/blog/post/body/"+ID;
        HttpUtils.sendRequest(url,new okhttp3.Callback(){
            @Override
            public void onResponse(Call call, Response response)throws IOException{
                String data = response.body().string();
                final String blogDetailData = HttpUtils.getBlogDetail(data);
                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        blogDetail.setText(Html.fromHtml(blogDetailData));
                    }
                });
            }
            @Override
            public void onFailure(Call call, IOException e){
                Log.e("Error","BlogDetailActivity/initBlogDetail/onFailure");
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return true;
    }
}
