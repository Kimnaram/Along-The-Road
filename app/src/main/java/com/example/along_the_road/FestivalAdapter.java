package com.example.along_the_road;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
public class FestivalAdapter extends RecyclerView.Adapter<FestivalAdapter.ViewHolder>{
    private ArrayList<FestivalItem> festivalItems;
    private Context context;

    public FestivalAdapter(ArrayList<FestivalItem> festivalItems, Context context){
        this.festivalItems = festivalItems;
        this.context = context;
    }
    @NonNull
    @Override
    public FestivalAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parse_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FestivalAdapter.ViewHolder holder, int position) {
        FestivalItem festivalItem = festivalItems.get(position);
        holder.textView.setText(festivalItem.getTitle());
        if (festivalItems.isEmpty()) {
            holder.imageView.setImageResource(R.drawable.placeholder);
        } else Picasso.get().load(festivalItem.getImgUrl()).into(holder.imageView); //여기서 에러
    }
    @Override
    public int getItemCount() {
        return festivalItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            FestivalItem festivalItem = festivalItems.get(position);

            Intent intent = new Intent(context,DetailActivity.class);
            intent.putExtra("title", festivalItem.getTitle());
            intent.putExtra("image", festivalItem.getImgUrl());
            intent.putExtra("detailUrl", festivalItem.getDetailUrl());
            context.startActivity(intent);

        }
    }
}
