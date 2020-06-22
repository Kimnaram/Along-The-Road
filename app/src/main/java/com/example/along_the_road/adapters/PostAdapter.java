package com.example.along_the_road.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.along_the_road.R;
import com.example.along_the_road.models.Post;


import java.util.List;
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> datas;

    public PostAdapter(List<Post> datas) {
        this.datas = datas;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post data = datas.get(position); // post 모델 객체 만듦
        holder.title.setText(data.getTitle()); //data를 가져와서 리스트에 넣어줌
        holder.contents.setText(data.getContents()); //리스트가 여러가지가 있을 때 data get postion 0,1,2,3..에 아이템 하나를 넣어준다는 뜻

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class PostViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView contents;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_post_title);
            contents= itemView.findViewById(R.id.item_post_contents);

        }
    }
}
