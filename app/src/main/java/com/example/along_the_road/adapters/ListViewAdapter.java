package com.example.along_the_road.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.along_the_road.ListHotel;
import com.example.along_the_road.R;

import java.util.ArrayList;

public class ListViewAdapter extends BaseAdapter {
    private static final String TAG = "ListViewAdapter";

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListHotel> listViewItemList = new ArrayList<ListHotel>();
    private ArrayList<ListHotel> displayItemList = new ArrayList<ListHotel>();

    // ListViewAdapter의 생성자
    public ListViewAdapter() {
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return displayItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iv_item_image = (ImageView) convertView.findViewById(R.id.iv_item_image);
        TextView tv_item_name = (TextView) convertView.findViewById(R.id.tv_item_name);
        TextView tv_item_detail = (TextView) convertView.findViewById(R.id.tv_item_detail);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListHotel listViewItem = getItem(position);

        // 아이템 내 각 위젯에 데이터 반영
        if(listViewItem.getHotelname() != null) {
            iv_item_image.setImageDrawable(listViewItem.getHotelimage());
            iv_item_image.setVisibility(View.VISIBLE);
        }
        tv_item_name.setText(listViewItem.getHotelname());

        String detail = "체크인 시간 " + listViewItem.getCheckIn() +
                ", 체크아웃 시간 " + listViewItem.getCheckOut() +
                "\n주차 가능 여부 : " + listViewItem.getParking();

        tv_item_detail.setText(detail);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public ListHotel getItem(int position) {
        return displayItemList.get(position);
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(ListHotel listHotel) {

        listViewItemList.add(listHotel);
        displayItemList.add(listHotel);

        notifyDataSetChanged();
    }

    public void clearAllItem() {
        listViewItemList.clear();
        displayItemList.clear();
    }

    public void fillter(String search) {

        displayItemList.clear();

        Log.d(TAG, "search keyword : " + search);

        if(search.length() == 0) {
            displayItemList.addAll(listViewItemList);
        } else {
            for(ListHotel item : listViewItemList) {
                if(item.getHotelname().toLowerCase().contains(search)
                        || item.getHotelname().toUpperCase().contains(search)) {

                    displayItemList.add(item);
                }
            }
        }

        notifyDataSetChanged();

    }

}

