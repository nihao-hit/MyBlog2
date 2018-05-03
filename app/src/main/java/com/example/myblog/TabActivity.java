package com.example.myblog;

import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TabActivity extends AppCompatActivity {
    TabLayout tab;
    ViewPager viewpager;
    private LocalActivityManager manager;
    private MyViewPageAdapter adapter;
    private String[] titles = {"博客","随笔"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tab);
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
    @Override
    protected void onResume(){
        super.onResume();
        manager.dispatchResume();
        if(viewpager != null){
            switch(viewpager.getCurrentItem()){
                case 1:
                    Activity activity2 = manager.getActivity("EssayActivity");
                    if(activity2 != null){
                        ((EssayActivity)activity2).myOnResume();
                    }
                    break;
            }
        }
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
}
