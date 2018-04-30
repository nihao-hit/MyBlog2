package com.example.myblog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Administrator on 2018/3/31.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder>{
    private List<Blog> blogList;
    private Context recyclerContext;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView authorAvatar;
        TextView authorName;
        TextView blogPulish;
        TextView blogTitle;
        TextView blogSummary;
        TextView blogDiggs;
        TextView blogComments;
        TextView blogViews;
        public ViewHolder(View v){
            super(v);
            cardView = (CardView)v;
            authorAvatar = (ImageView)v.findViewById(R.id.authorAvatar);
            authorName = (TextView)v.findViewById(R.id.authorName);
            blogPulish = (TextView)v.findViewById(R.id.blogPulish);
            blogTitle = (TextView)v.findViewById(R.id.blogTitle);
            blogSummary = (TextView)v.findViewById(R.id.blogSummary);
            blogDiggs = (TextView)v.findViewById(R.id.blogDiggs);
            blogComments = (TextView)v.findViewById(R.id.blogComments);
            blogViews = (TextView)v.findViewById(R.id.blogViews);
        }
    }
    public BlogAdapter(List<Blog> mBlogList){
        blogList = mBlogList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        if(recyclerContext == null){
            recyclerContext = parent.getContext();
        }
        View view = LayoutInflater.from(recyclerContext).inflate(R.layout.item_recycler,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Blog blog = blogList.get(position);
                Intent intent = new Intent(recyclerContext,BlogDetailActivity.class);
                intent.putExtra("TITLE",blog.getBlogTitle());
                intent.putExtra("ID",blog.getBlogId());
                recyclerContext.startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Blog blog = blogList.get(position);
        Glide.with(recyclerContext).load(blog.getAuthorAvatar()).into(holder.authorAvatar);
        holder.authorName.setText(blog.getAuthorName());
        String publish = TimeUtils.dateToPublish(TimeUtils.parseUTCDate(blog.getBlogPublish()));
        holder.blogPulish.setText(publish);
        holder.blogTitle.setText(blog.getBlogTitle());
        holder.blogSummary.setText(blog.getBlogSummary());
        holder.blogDiggs.setText(blog.getBlogDiggs()+" 推荐");
        holder.blogComments.setText(blog.getBlogComments()+" 评论");
        holder.blogViews.setText(blog.getBlogViews()+" 阅读");
    }
    @Override
    public int getItemCount(){
        return blogList.size();
    }
}
