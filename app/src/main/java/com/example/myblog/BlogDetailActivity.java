package com.example.myblog;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class BlogDetailActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsing;
    private TextView blogDetail;
    private String TITLE;
    private String ID;
    private FloatingActionButton floating;
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
        floating = (FloatingActionButton)findViewById(R.id.collect);
        initCollapsingToolbarLayout();
        initBlogDetail();
        floating.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(BlogDetailActivity.this,AddEssayActivity.class);
                startActivity(intent);
            }
        });
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
        if(MainActivity.flag){
            String url = "http://wcf.open.cnblogs.com/blog/post/body/"+ID;
            HttpUtils.sendRequest(url,new okhttp3.Callback(){
                @Override
                public void onResponse(Call call, Response response)throws IOException{
                    String data = response.body().string();
                    final String blogDetailData = HttpUtils.getBlogDetail(data);
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run(){
                            blogDetail.setText(Html.fromHtml(blogDetailData,mImageGetter,null));
                        }
                    });
                }
                @Override
                public void onFailure(Call call, IOException e){
                    Log.e("Error","BlogDetailActivity/initBlogDetail/onFailure");
                }
            });
        }
        else{
            Cursor cursor = MainActivity.db.query("BlogDetail",new String[]{"blogDetail"},"blogId=?",new String[]{ID},null,null,null);
            if(cursor.moveToFirst()){
                String data = cursor.getString(cursor.getColumnIndex("blogDetail"));
                blogDetail.setText(Html.fromHtml(data));
            }


        }
    }
    Html.ImageGetter mImageGetter = new Html.ImageGetter(){
        @Override
        public Drawable getDrawable(String source){
            final LevelListDrawable drawable = new LevelListDrawable();
            Glide.with(BlogDetailActivity.this).load(source).asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if(resource != null) {
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(resource);
                        drawable.addLevel(1, 1, bitmapDrawable);
                        drawable.setBounds(0, 0, resource.getWidth(),resource.getHeight());
                        drawable.setLevel(1);
                        blogDetail.invalidate();
                        blogDetail.setText(blogDetail.getText());
                    }
                }
            });
            return drawable;
        }
    };
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
