package com.example.myblog;

/**
 * Created by Administrator on 2018/3/31.
 */

public class Blog {
    private String blogId;//博客id
    private String blogTitle;//博客标题
    private String blogSummary;//博客概要
    private String blogPublish;//博客发表时间
    private String authorName;//作者昵称
    private String authorAvatar;//作者头像
    private String authorUri;//作者主页
    private String blogLink;//博客链接
    private String blogComments;//博客评论数
    private String blogViews;//博客浏览数
    private String blogDiggs;//博客点赞数

    public String getBlogId() {
        return blogId;
    }

    public String getBlogTitle() {
        return blogTitle;
    }

    public String getBlogSummary() {
        return blogSummary;
    }

    public String getBlogPublish() {
        return blogPublish;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public String getAuthorUri() {
        return authorUri;
    }

    public String getBlogLink() {
        return blogLink;
    }

    public String getBlogComments() {
        return blogComments;
    }

    public String getBlogViews() {
        return blogViews;
    }

    public String getBlogDiggs() {
        return blogDiggs;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public void setBlogTitle(String blogTitle) {
        this.blogTitle = blogTitle;
    }

    public void setBlogSummary(String blogSummary) {
        this.blogSummary = blogSummary;
    }

    public void setBlogPublish(String blogPublish) {
        this.blogPublish = blogPublish;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public void setAuthorUri(String authorUri) {
        this.authorUri = authorUri;
    }

    public void setBlogLink(String blogLink) {
        this.blogLink = blogLink;
    }

    public void setBlogComments(String blogComments) {
        this.blogComments = blogComments;
    }

    public void setBlogViews(String blogViews) {
        this.blogViews = blogViews;
    }

    public void setBlogDiggs(String blogDiggs) {
        this.blogDiggs = blogDiggs;
    }
}
