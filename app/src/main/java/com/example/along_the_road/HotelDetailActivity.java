package com.example.along_the_road;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class HotelDetailActivity extends AppCompatActivity {

    private final String API_KEY = "";
    private String RoomDetail;

    private int ContentID;
    private String HotelName;

    private int list_len = 0;

    private String[] roomaircondition;
    private String[] roombathfacility;
    private String[] roominternet;
    private String[] roomtv;
    private String[] roomrefrigerator;
    private String[] roomhairdryer;
    private String[] roomimg1;
    private String[] roomimg2;
    private String[] roomimg3;
    private String[] roomimg4;
    private String[] roomimg5;
    private String[] roomtype;

    boolean airconditioncheck;
    boolean bathfacilitycheck;
    boolean refrigeratorcheck;
    boolean internetcheck;
    boolean tvcheck;
    boolean hairdryercheck;

    private Bitmap bitmap;

    private Drawable ac_img;
    private Drawable bt_img;
    private Drawable in_img;
    private Drawable tv_img;
    private Drawable rf_img;
    private Drawable hd_img;

    private LinearLayout ll_room_list;
    private LinearLayout[] ll_room_option;
    private FlowLayout[] fl_option_list;

    private int fl_count = 0;

    private TextView tv_hotel_name;
    private TextView tv_room_option;
    private TextView tv_room_type;

    private ImageView RoomImage1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        ll_room_list = findViewById(R.id.ll_room_list);

        Intent intent = getIntent();

        if (intent == null) {
            // 오류 메시지 팝업
        } else if (intent != null) {
            String strID = "";
            strID = intent.getStringExtra("ID");
            System.out.println(strID);
            ContentID = Integer.parseInt(strID);
            HotelName = intent.getStringExtra("Name");

            tv_hotel_name = findViewById(R.id.tv_hotel_name);
            tv_hotel_name.setText(HotelName);

            RoomDetail = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?ServiceKey=" + API_KEY +
                    "&numOfRows=40&pageNo=1&areaCode=1&contentTypeId=32&contentId=" + ContentID +
                    "&MobileOS=ETC&MobileApp=AppTest&_type=json";

            System.out.println(RoomDetail);

            String ResultText = "값이 없음";

            try {
                ResultText = new Task().execute().get();

                JSONObject Object = new JSONObject(ResultText);

                String response = Object.getString("response");
                JSONObject responseObject = new JSONObject(response);

                String body = responseObject.getString("body");
                JSONObject bodyObject = new JSONObject(body);

                boolean itemscheck = bodyObject.isNull("items");
                if (itemscheck == true || bodyObject.getString("items").equals("")) {
                    // 호텔 룸 정보가 존재하지 않는다면
                    // 팝업 메세지를 표시
                } else {

                    String items = bodyObject.getString("items");
                    JSONObject itemsObject = new JSONObject(items);

                    String item = itemsObject.getString("item");
                    String ItemIsWhat = item.split("\"")[0];

                    JSONObject itemObject = null;
                    JSONArray itemArray = null;

                    if (ItemIsWhat.equals("{")) { // 방 타입이 하나라면

                        itemObject = new JSONObject(item);
                        list_len = 1;

                    } else { // 타입이 여러개라면

                        itemArray = new JSONArray(item);
                        list_len = itemArray.length();

                    }

                    ll_room_option = new LinearLayout[list_len];
                    fl_option_list = new FlowLayout[list_len];

                    roomaircondition = new String[list_len];
                    roombathfacility = new String[list_len];
                    roomrefrigerator = new String[list_len];
                    roominternet = new String[list_len];
                    roomhairdryer = new String[list_len];
                    roomtv = new String[list_len];
                    roomtype = new String[list_len];
                    roomimg1 = new String[list_len];

                    if (ItemIsWhat.equals("[{")) {

                        for (int i = 0; i < list_len; i++) {

                            if(fl_count > 0) {
//                                ll_room_option[i]
                            }

                            ll_room_option[i] = new LinearLayout(HotelDetailActivity.this);
                            LinearLayout.LayoutParams ll_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            ll_param.bottomMargin = 50;
                            ll_room_option[i].setLayoutParams(ll_param);
                            ll_room_option[i].setOrientation(LinearLayout.VERTICAL);
                            ll_room_list.addView(ll_room_option[i]);

                            fl_option_list[i] = new FlowLayout(this);
                            FlowLayout.LayoutParams fl_param = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            fl_option_list[i].setLayoutParams(fl_param);
                            fl_option_list[i].setOrientation(FlowLayout.HORIZONTAL);

                            JSONObject HotelObject = itemArray.getJSONObject(i);

                            roomtype[i] = HotelObject.getString("roomtitle");

                            airconditioncheck = HotelObject.isNull("roomaircondition");
                            if(airconditioncheck == false) {
                                roomaircondition[i] = HotelObject.getString("roomaircondition");
                            }

                            bathfacilitycheck = HotelObject.isNull("roombathfacility");
                            if(bathfacilitycheck == false) {
                                roombathfacility[i] = HotelObject.getString("roombathfacility");
                            }

                            refrigeratorcheck = HotelObject.isNull("roomrefrigerator");
                            if(refrigeratorcheck == false) {
                                roomrefrigerator[i] = HotelObject.getString("roomrefrigerator");
                            }

                            internetcheck = HotelObject.isNull("roominternet");
                            if(internetcheck == false) {
                                roominternet[i] = HotelObject.getString("roominternet");
                            }

                            tvcheck = HotelObject.isNull("roomtv");
                            if(tvcheck == false) {
                                roomtv[i] = HotelObject.getString("roomtv");
                            }

                            hairdryercheck = HotelObject.isNull("roomhairdryer");
                            if(hairdryercheck == false) {
                                roomhairdryer[i] = HotelObject.getString("roomhairdryer");
                            }

                            MakeRoomType(roomtype[i], i);

                            boolean image1check = HotelObject.isNull("roomimg1");
                            if (image1check == false) {
                                roomimg1[i] = HotelObject.getString("roomimg1");

                                RoomImage1 = new ImageView(this);

                                final String img_url = roomimg1[i];
                                final int no = i;

                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO Auto-generated method stub
                                        try {
                                            URL url = new URL(img_url);
                                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                            conn.setDoInput(true);
                                            conn.connect();

                                            InputStream is = conn.getInputStream();
                                            bitmap = BitmapFactory.decodeStream(is);
                                        } catch (MalformedURLException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                thread.start();

                                try {
                                    thread.join();

                                    RoomImage1.setImageBitmap(bitmap);
                                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.WRAP_CONTENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                                    lp.width = size;
                                    lp.height = size;
                                    RoomImage1.setLayoutParams(lp);
                                    RoomImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    ll_room_option[i].addView(RoomImage1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                            String text = "객실 내 시설";
                            MakeFacility(text, i);

                            ll_room_option[i].addView(fl_option_list[i]);

                            Resources res = getResources();
                            if(roomaircondition != null || roomaircondition.equals("Y")) {
                                String ac = "에어컨";
                                ac_img = ResourcesCompat.getDrawable(res, R.drawable.rm_air_conditioner2_100, null);
                                MakeRoomOption(ac, i, ac_img);
                            }
                            if(roombathfacility != null || roombathfacility.equals("Y")) {
                                String bt = "욕조";
                                bt_img = ResourcesCompat.getDrawable(res, R.drawable.rm_bath_100, null);
                                MakeRoomOption(bt, i, bt_img);
                            }
                            if(roomrefrigerator != null || roomrefrigerator.equals("Y")) {
                                String rf = "냉장고";
                                rf_img = ResourcesCompat.getDrawable(res, R.drawable.rm_fridge_100, null);
                                MakeRoomOption(rf, i, rf_img);
                            }
                            if(roominternet != null || roominternet.equals("Y")) {
                                String in = "인터넷";
                                in_img = ResourcesCompat.getDrawable(res, R.drawable.rm_wifi_100, null);
                                MakeRoomOption(in, i, in_img);
                            }
                            if(roomtv != null || roomtv.equals("Y")) {
                                String tv = "TV";
                                tv_img = ResourcesCompat.getDrawable(res, R.drawable.rm_tv_100, null);
                                MakeRoomOption(tv, i, tv_img);
                            }
                            if(roomhairdryer != null || roomhairdryer.equals("Y")) {
                                String hd = "헤어 드라이어";
                                hd_img = ResourcesCompat.getDrawable(res, R.drawable.rm_hair_dryer_100, null);
                                MakeRoomOption(hd, i, hd_img);
                            }


                        }
                    }
                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void MakeRoomType(String t, int i) {

        tv_room_type = new TextView(this);

        tv_room_type.setText(t);
        tv_room_type.setTextSize(22);
        tv_room_type.setPadding(0, 20, 0, 20);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        tv_room_type.setTypeface(typeface);

        ll_room_option[i].addView(tv_room_type);
    }

    public void MakeFacility(String t, int i) {

        TextView facility = new TextView(this);

        facility.setText(t);
        facility.setTextSize(18);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        facility.setTypeface(typeface);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp.topMargin = 20;
        lp.bottomMargin = 20;
        facility.setLayoutParams(lp);

        ll_room_option[i].addView(facility);
    }

    public void MakeRoomOption(String t, int i,  @Nullable Drawable img) {

        tv_room_option = new TextView(this);

        tv_room_option.setText(t);
        tv_room_option.setTextSize(14);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        tv_room_option.setTypeface(typeface);
        tv_room_option.setPadding(0, 10, 50, 10);

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//
//        tv_room_option.setLayoutParams(lp);

        if(img != null) {
            int h = 70;
            int w = 70;
            img.setBounds(0, 0, h, w);
            tv_room_option.setCompoundDrawables(null, null, null, img);
        }

        fl_option_list[i].addView(tv_room_option);
        fl_count += 1;
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(RoomDetail);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
    }

}
