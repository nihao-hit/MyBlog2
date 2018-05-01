package com.example.myblog;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class EssayAdapter extends RecyclerView.Adapter<EssayAdapter.ViewHolder>{
    private Context context;
    private List<Essay> essays;
    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView essayTitle;
        TextView essayContent;
        TextView essayDate;
        public ViewHolder(View v){
            super(v);
            cardView = (CardView)v;
            essayTitle = (TextView)v.findViewById(R.id.essay_title);
            essayContent = (TextView)v.findViewById(R.id.essay_content);
            essayDate = (TextView)v.findViewById(R.id.essay_date);
        }
    }
    public EssayAdapter(List<Essay> essayList){
        essays = essayList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        if(context == null){
            context = parent.getContext();
        }
        View v = LayoutInflater.from(context).inflate(R.layout.item_essayrecycler,parent,false);
        final ViewHolder holder = new ViewHolder(v);
        holder.cardView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                int position = holder.getAdapterPosition();
                Essay essay = essays.get(position);
                Intent intent = new Intent(context,UpdateEssayActivity.class);
                Log.d("eee",essay.getId()+"essayactivityadpater");
                intent.putExtra("id",essay.getId());
                context.startActivity(intent);
            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder,int position){
        Essay essay = essays.get(position);
        holder.essayTitle.setText(essay.getTitle());
        holder.essayDate.setText(essay.getDate());
        holder.essayContent.setText(essay.getContent());
    }
    @Override
    public int getItemCount(){
        return essays.size();
    }
}
