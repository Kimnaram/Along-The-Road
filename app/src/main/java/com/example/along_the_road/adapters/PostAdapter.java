package com.example.along_the_road.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.along_the_road.ListHotel;
import com.example.along_the_road.ListReview;
import com.example.along_the_road.R;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ItemViewHolder> {

    private final static String TAG = "PostAdapter";

    // adapter에 들어갈 list 입니다.
    private ArrayList<ListReview> listReviewArrayList = new ArrayList<>(); // 커스텀 리스너 인터페이스
    private ArrayList<ListReview> displayItemList = new ArrayList<ListReview>();

    // 리스너 객체 참조를 저장하는 변수
    private OnItemClickListener mListener = null;
    private OnItemLongClickListener mLongListener = null;

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(displayItemList.get(position));

    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return displayItemList.size();
    }

    public int getItemPosition(int postId) {
        for(int i = 0; i < listReviewArrayList.size(); i++) {
            if(listReviewArrayList.get(i).get_id() == postId) {
                return i;
            }
        }
        return -1;
    }

    public void addItem(ListReview listReview) {
        // 외부에서 item을 추가시킬 함수입니다.

        listReviewArrayList.add(listReview);
        displayItemList.add(listReview);
        notifyDataSetChanged();
        Log.d(TAG, "Whyrano... " + listReview.getTitle());

    }

    public void addItemLocation(int position, int like) {

        listReviewArrayList.get(position).setLike(like);
        notifyDataSetChanged();

    }

    public void clearAllItem() {

        listReviewArrayList.clear();
        displayItemList.clear();
        notifyDataSetChanged();
        Log.d(TAG, "삭제");

    }

    public void fillter(String search) {

        displayItemList.clear();

        if(search.length() == 0) {
            displayItemList.addAll(listReviewArrayList);
        } else {
            for(ListReview item : listReviewArrayList) {
                if(item.getTitle().toLowerCase().contains(search)
                        || item.getTitle().toUpperCase().contains(search)
                        || item.getName().toLowerCase().contains(search)
                        || item.getName().toUpperCase().contains(search)) {

                    displayItemList.add(item);
                }
            }
        }

        notifyDataSetChanged();

    }

    public ListReview getItem(int position) {
        return listReviewArrayList.get(position) ;
    }

    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_item_title;
        private TextView tv_item_userid;
        private TextView tv_item_like;

        ItemViewHolder(View itemView) {
            super(itemView);

            tv_item_title = itemView.findViewById(R.id.tv_item_title);
            tv_item_userid = itemView.findViewById(R.id.tv_item_userid);
            tv_item_like = itemView.findViewById(R.id.tv_item_like);

            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION)
                    {
                        mListener.onItemClick(v, pos);
                    }

                }
            });

        }

        void onBind(ListReview listReview) {
            tv_item_title.setText(listReview.getTitle());
            tv_item_userid.setText(listReview.getName());
            tv_item_like.setText(Integer.toString(listReview.getLike()));
        }

    }

    public interface OnItemClickListener
    {
        void onItemClick(View v, int pos);
    }

    public interface OnItemLongClickListener
    {
        void onItemLongClick(View v, int pos);
    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        this.mListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener)
    {
        this.mLongListener = listener;
    }

    //
    public void TextAdapter(ArrayList<ListReview> list)
    {
        listReviewArrayList = list;
    }

}