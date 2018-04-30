package com.example.myblog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2018/3/31.
 */

public class HttpUtils {
    public static void sendRequest(String url,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
    public static List<Blog> getBlogList(String response){
        List<Blog> blogList = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try{

                Blog blog = null;
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(response));
                int eventType = parser.getEventType();
                while(eventType != XmlPullParser.END_DOCUMENT){
                    String nodeName = parser.getName();
                    switch(eventType){
                        case XmlPullParser.START_DOCUMENT:
                            blog = new Blog();
                            break;
                        case XmlPullParser.START_TAG:
                            if("entry".equals(nodeName)){
                                blog = new Blog();
                                break;
                            }
                            if("id".equals(nodeName)){
                                blog.setBlogId(parser.nextText());
                                break;
                            }
                            if("title".equals(nodeName)){
                                blog.setBlogTitle(parser.nextText());
                                break;
                            }
                            if("summary".equals(nodeName)){
                                blog.setBlogSummary(parser.nextText());
                                break;
                            }
                            if("published".equals(nodeName)){
                                blog.setBlogPublish(parser.nextText());
                                break;
                            }
                            if("name".equals(nodeName)){
                                blog.setAuthorName(parser.nextText());
                                break;
                            }
                            if("uri".equals(nodeName)){
                                blog.setAuthorUri(parser.nextText());
                                break;
                            }
                            if("avatar".equals(nodeName)){
                                blog.setAuthorAvatar(parser.nextText());
                                break;
                            }
                            if("link".equals(nodeName)){
                                if (parser.getAttributeName(0).equals("rel")) {
                                    blog.setBlogLink(parser.getAttributeValue(1));
                                }
                                break;
                            }
                            if("diggs".equals(nodeName)){
                                blog.setBlogDiggs(parser.nextText());
                                break;
                            }
                            if("views".equals(nodeName)){
                                blog.setBlogViews(parser.nextText());
                                break;
                            }
                            if("comments".equals(nodeName)){
                                blog.setBlogComments(parser.nextText());
                                break;
                            }
                        case XmlPullParser.END_TAG:
                            if("entry".equals(nodeName)){
                                blogList.add(blog);
                                blog = null;
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
                return blogList;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String getBlogDetail(String response){
        if(!TextUtils.isEmpty(response)){
            String blogDetail = "";
            try{
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new StringReader(response));
                int eventType = parser.getEventType();
                while(eventType != XmlPullParser.END_DOCUMENT){
                    String nodeName = parser.getName();
                    switch(eventType){
                        case XmlPullParser.START_TAG:
                            if ("string".equals(nodeName)){
                                blogDetail = parser.nextText();
                            }
                            break;
                        default:
                            break;
                    }
                    eventType = parser.next();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
            return blogDetail;
        }
        return "";
    }
    public static boolean isNetworkConnected(Context context){
        if(context != null){
            ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            if(info != null){
                return true;
            }
        }
        return false;
    }
}
