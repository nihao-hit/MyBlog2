package com.example.myblog;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.trity.floatingactionbutton.FloatingActionButton;

public class AddEssayActivity extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton addEssay;
    FloatingActionButton cancleEssay;
    EditText editTitle;
    EditText editContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_essay);
        initView();
    }
    private void initView(){
        editTitle = (EditText)findViewById(R.id.edit_title);
        editContent = (EditText)findViewById(R.id.edit_content);
        addEssay = (FloatingActionButton)findViewById(R.id.add_essay);
        cancleEssay = (FloatingActionButton)findViewById(R.id.cancle_essay);
        addEssay.setOnClickListener(this);
        cancleEssay.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.add_essay:
                if(!TextUtils.isEmpty(editTitle.getText()) && !TextUtils.isEmpty(editContent.getText())){
                    String title = editTitle.getText().toString();
                    String content = editContent.getText().toString();
                    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
                    Date date = new Date();
                    String essayDate = format.format(date);
                    ContentValues values = new ContentValues();
                    values.put("essayTitle",title);
                    values.put("essayDate",essayDate);
                    values.put("essayContent",content);
                    TabActivity.db.insert("Essay",null,values);
                    values.clear();

                    EssayActivity.refreshAdapter();
                    EssayActivity.adapter.notifyDataSetChanged();
                    finish();
                }
                break;
            case R.id.cancle_essay:
                finish();
                break;
        }
    }
}
