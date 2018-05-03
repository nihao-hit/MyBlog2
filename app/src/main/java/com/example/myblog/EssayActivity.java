package com.example.myblog;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EssayActivity extends AppCompatActivity {
    Toolbar toolbar;
    RecyclerView recycler;
    EssayAdapter adapter;
    TextView alert;
    FloatingActionButton add;
    public static List<Essay> essayList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_essay);
        initView();
    }
    private void initView(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        refreshAdapter();
        recycler = (RecyclerView)findViewById(R.id.essay_recycler);
        adapter = new EssayAdapter(essayList);
        if(essayList.size() != 0){
            if(recycler.getVisibility() == View.GONE)
                recycler.setVisibility(View.VISIBLE);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            recycler.setAdapter(adapter);
        }
        else{
            alert = (TextView)findViewById(R.id.alert);
            if(alert.getVisibility() == View.GONE)
                alert.setVisibility(View.VISIBLE);
            recycler.setVisibility(View.GONE);
        }

        add = (FloatingActionButton)findViewById(R.id.add);
        add.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(EssayActivity.this,AddEssayActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return super.onSupportNavigateUp();
    }
    public void myOnResume(){
        adapter.notifyDataSetChanged();
    }
    public static void refreshAdapter(){
        Cursor cursor = MainActivity.db.query("Essay",null,null,null,null,null,"essayId desc");
        if(cursor.moveToFirst()){
            essayList.clear();
            do{
                String id = cursor.getString(cursor.getColumnIndex("essayId"));
                String title = cursor.getString(cursor.getColumnIndex("essayTitle"));
                String date = cursor.getString(cursor.getColumnIndex("essayDate"));
                String content = cursor.getString(cursor.getColumnIndex("essayContent"));
                Essay essay = new Essay();
                essay.setId(id);
                essay.setTitle(title);
                essay.setDate(date);
                essay.setContent(content);
                Log.d("eee",id+"essayactivity");
                essayList.add(essay);
            }while(cursor.moveToNext());
            cursor.close();
        }
    }
}
