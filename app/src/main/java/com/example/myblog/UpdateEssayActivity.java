package com.example.myblog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;

import cc.trity.floatingactionbutton.FloatingActionButton;

public class UpdateEssayActivity extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton addEssay;
    FloatingActionButton cancleEssay;
    FloatingActionButton deleteEssay;
    EditText editTitle;
    EditText editContent;
    Essay essay = new Essay();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_update_essay);
        initView();

    }
    private void initView(){
        editTitle = (EditText)findViewById(R.id.edit_title);
        editContent = (EditText)findViewById(R.id.edit_content);
        addEssay = (FloatingActionButton)findViewById(R.id.add_essay);
        cancleEssay = (FloatingActionButton)findViewById(R.id.cancle_essay);
        deleteEssay = (FloatingActionButton)findViewById(R.id.delete_essay);
        addEssay.setOnClickListener(this);
        cancleEssay.setOnClickListener(this);
        deleteEssay.setOnClickListener(this);
        initEditView();
    }
    private void initEditView(){
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        Log.d("eee",id+"updateessayactivity");
        Cursor cursor = MainActivity.db.query("Essay",null,"essayId=?",new String[]{id},null,null,null);
        if(cursor.moveToFirst()) {
            essay.setId(cursor.getString(cursor.getColumnIndex("essayId")));
            essay.setTitle(cursor.getString(cursor.getColumnIndex("essayTitle")));
            essay.setContent(cursor.getString(cursor.getColumnIndex("essayContent")));
            essay.setDate(cursor.getString(cursor.getColumnIndex("essayDate")));
            editTitle.setText(essay.getTitle());
            editContent.setText(essay.getContent());
        }
    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.add_essay:
                if(!TextUtils.isEmpty(editTitle.getText()) && !TextUtils.isEmpty(editContent.getText())){
                    String title = editTitle.getText().toString();
                    String content = editContent.getText().toString();
                    ContentValues values = new ContentValues();
                    values.put("essayTitle",title);
                    values.put("essayContent",content);
                    MainActivity.db.update("Essay",values,"essayId=?",new String[]{String.valueOf(essay.getId())});
                    values.clear();

                    EssayActivity.refreshAdapter();
                    finish();
                }
                break;
            case R.id.cancle_essay:
                finish();
                break;
            case R.id.delete_essay:
                MainActivity.db.delete("Essay","essayId=?",new String[]{String.valueOf(essay.getId())});
                EssayActivity.refreshAdapter();
                finish();
                break;
        }
    }
}
