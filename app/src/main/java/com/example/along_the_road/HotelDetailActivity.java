package com.example.along_the_road;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.SearchManager;
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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private String Start_Date;
    private String End_Date;
    private String URL;
    private String CheckIn;
    private String CheckOut;
//    private String Member;

    private int list_len = 0;

    private int[] roomoffseasonminfee1;
    private int[] roompeakseasonminfee1;

    private String[] roomaircondition;
    private String[] roombathfacility;
    private String[] roominternet;
    private String[] roomtv;
    private String[] roomrefrigerator;
    private String[] roomhairdryer;
    private String[] roomtoiletries;
    private String[] roomcable;
    private String[] roomtable;
    private String[] roomimg1;
    private String[] roomimg2;
    private String[] roomimg3;
    private String[] roomimg4;
    private String[] roomimg5;
    private String[] roomtype;

    private boolean offseasonfeecheck;
    private boolean peakseasonfeecheck;
    private boolean airconditioncheck;
    private boolean bathfacilitycheck;
    private boolean toiletriescheck;
    private boolean cablecheck;
    private boolean tablecheck;
    private boolean refrigeratorcheck;
    private boolean internetcheck;
    private boolean tvcheck;
    private boolean hairdryercheck;

    private Bitmap bitmap;

    private Drawable ac_img;
    private Drawable bt_img;
    private Drawable tl_img;
    private Drawable in_img;
    private Drawable tv_img;
    private Drawable rf_img;
    private Drawable hd_img;
    private Drawable cb_img;
    private Drawable tb_img;

    private LinearLayout ll_room_list;
    private LinearLayout[] ll_room_option;
    private FlowLayout[] fl_option_list;

    private int fl_count = 0;

    private TextView tv_hotel_name;
    private TextView tv_checkout_time;
    private TextView tv_checkin_time;
    private TextView tv_room_option;
    private TextView tv_room_type;

    private ImageView RoomImage1;

    private Button btn_reservation;

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
            Start_Date = intent.getStringExtra("Start_Date");
            End_Date = intent.getStringExtra("End_Date");
            URL = intent.getStringExtra("URL");
            CheckIn = intent.getStringExtra("CheckIn");
            CheckOut = intent.getStringExtra("CheckOut");

//            Member = intent.getStringExtra("Member");

            tv_hotel_name = findViewById(R.id.tv_hotel_name);
            tv_hotel_name.setText(HotelName);

            tv_checkout_time = findViewById(R.id.tv_checkout_time);
            tv_checkout_time.setText(CheckOut);

            tv_checkin_time = findViewById(R.id.tv_checkin_time);
            tv_checkin_time.setText(CheckIn);

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

                    roomoffseasonminfee1 = new int[list_len];
                    roompeakseasonminfee1 = new int[list_len];

                    roomaircondition = new String[list_len];
                    roombathfacility = new String[list_len];
                    roomrefrigerator = new String[list_len];
                    roominternet = new String[list_len];
                    roomhairdryer = new String[list_len];
                    roomtv = new String[list_len];
                    roomtoiletries = new String[list_len];
                    roomtable = new String[list_len];
                    roomcable = new String[list_len];
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
                            fl_param.setGravity(Gravity.CENTER);
                            fl_option_list[i].setLayoutParams(fl_param);
                            fl_option_list[i].setOrientation(FlowLayout.HORIZONTAL);

                            JSONObject HotelObject = itemArray.getJSONObject(i);

                            roomtype[i] = HotelObject.getString("roomtitle");

                            offseasonfeecheck = HotelObject.isNull("roomoffseasonminfee1");
                            if(offseasonfeecheck == false) {
                                roomoffseasonminfee1[i] = HotelObject.getInt("roomoffseasonminfee1");
                            }
                            peakseasonfeecheck = HotelObject.isNull("roompeakseasonminfee1");
                            if(peakseasonfeecheck == false) {
                                roompeakseasonminfee1[i] = HotelObject.getInt("roompeakseasonminfee1");
                            }

                            airconditioncheck = HotelObject.isNull("roomaircondition");
                            if(airconditioncheck == false) {
                                roomaircondition[i] = HotelObject.getString("roomaircondition");
                            }

                            bathfacilitycheck = HotelObject.isNull("roombathfacility");
                            if(bathfacilitycheck == false) {
                                roombathfacility[i] = HotelObject.getString("roombathfacility");
                            }

                            toiletriescheck = HotelObject.isNull("roomtoiletries");
                            if(toiletriescheck == false) {
                                roomtoiletries[i] = HotelObject.getString("roomtoiletries");
                            }

                            tablecheck = HotelObject.isNull("roomtable");
                            if(tablecheck == false) {
                                roomtable[i] = HotelObject.getString("roomtable");
                            }

                            cablecheck = HotelObject.isNull("roomcable");
                            if(cablecheck == false) {
                                roomcable[i] = HotelObject.getString("roomcable");
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
                                    int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 350, getResources().getDisplayMetrics());
                                    lp.width = size;
                                    lp.height = size;
                                    lp.gravity = Gravity.CENTER;
                                    lp.topMargin = 5;
                                    lp.bottomMargin = 5;
                                    RoomImage1.setLayoutParams(lp);
                                    RoomImage1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                    ll_room_option[i].addView(RoomImage1);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                            }

                            if(offseasonfeecheck == false || peakseasonfeecheck == false) {
                                String standard = "(1인 1객실 기준)";
                                MakeTextView(standard, i, null, 14, 2, 20, 0);
                            }
                            if(offseasonfeecheck == false && roomoffseasonminfee1[i] != 0) {
                                String minfee = Integer.toString(roomoffseasonminfee1[i]);
                                String offminfee = "비성수기 최소 가격 : " + minfee + " \\";
                                MakeTextView(offminfee, i, "BLUE", 19, 0, 20, 20);
                            }

                            if(peakseasonfeecheck == false && roompeakseasonminfee1[i] != 0) {
                                String minfee = Integer.toString(roompeakseasonminfee1[i]);
                                String peakminfee = "성수기 최소 가격 : " + minfee + " \\";
                                MakeTextView(peakminfee, i, "BLUE", 19, 0, 20, 20);
                            }

                            String text = "객실 내 시설";
                            MakeTextView(text, i, null, 18, 0, 20, 20);

                            ll_room_option[i].addView(fl_option_list[i]);

                            Resources res = getResources();
                            if(roomaircondition != null || roomaircondition.equals("Y")) {
                                String ac = "에어컨";
                                ac_img = ResourcesCompat.getDrawable(res, R.drawable.rm_air_conditioner2_100, null);
                                MakeRoomOption(ac, i, ac_img);
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
                            if(roomcable != null || roomcable.equals("Y")) {
                                String cb = "케이블";
                                cb_img = ResourcesCompat.getDrawable(res, R.drawable.rm_cable_100, null);
                                MakeRoomOption(cb, i, cb_img);
                            }
                            if(roomtable != null || roomtable.equals("Y")) {
                                String tb = "테이블";
                                tb_img = ResourcesCompat.getDrawable(res, R.drawable.rm_table_100, null);
                                MakeRoomOption(tb, i, tb_img);
                            }
                            if(roomhairdryer != null || roomhairdryer.equals("Y")) {
                                String hd = "헤어 드라이어";
                                hd_img = ResourcesCompat.getDrawable(res, R.drawable.rm_hair_dryer_100, null);
                                MakeRoomOption(hd, i, hd_img);
                            }
                            if(roombathfacility != null || roombathfacility.equals("Y")) {
                                String bt = "욕조";
                                bt_img = ResourcesCompat.getDrawable(res, R.drawable.rm_bath_100, null);
                                MakeRoomOption(bt, i, bt_img);
                            }
                            if(roomtoiletries != null || roomtoiletries.equals("Y")) {
                                String tl = "세면 용품";
                                tl_img = ResourcesCompat.getDrawable(res, R.drawable.rm_toiletries_100, null);
                                MakeRoomOption(tl, i, tl_img);
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

        btn_reservation = findViewById(R.id.btn_reservation);
        btn_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Reservation_URL = null;
                if (URL != null) {
                    URL = URL.split("\"")[1];
                    Reservation_URL = URL;
                    URL = null;
                } else if (URL == null) {
                    System.out.println(Start_Date);
                    System.out.println(End_Date);
//                    Reservation_URL = "https://www.hotelscombined.co.kr/hotels/" +
//                            Start_Date + "/" + End_Date + "/1adults/1rooms?&placeName=hotel:" + HotelName.split("\\(")[0] + ", 대한민국";
                    Reservation_URL = "https://www.agoda.com/ko-kr/search?asq=NQVGXW6jsE3tbdY9S%2BqUCm0fIC6ullFI1P8fQM5sxSLBXZwWj6Holnstfd7HRUeb92T76PjbadYnBzfdCsXMZOlsP%2" +
                            "BnCWKwIe71Jv7u3e0MMriZKGNHSjteYroWkqcyRCeQfjsVtn6EgeauKhlth6cQ37cc3nlA%2BJXhCpn0ev1XBeXpNLyGArn6dMLK033Dp2yCLlkitcJbjMSEGlHpsuIpK0j93MeO9eTQGF7dDDKk6k9J" + "" +
                            "qQ8%2FRWmcOQgB30Le1&selectedproperty=42779&hotel=42779&cid=1844104&tick=637294915929&languageId=9&userId=c332323e-0c3a-43fb-bb5d-9b9ee1a6c5f9&" +
                            "sessionId=htvj0a3zeq2dh5xrk1tvc5tj&pageTypeId=1&origin=KR&locale=ko-KR&aid=130589&currencyCode=KRW&htmlLanguage=ko-kr&cultureInfoName=ko-KR&" +
                            "checkIn=" + Start_Date + "&checkOut=" + End_Date + "&rooms=1&adults=1&children=0&priceCur=KRW&los=4&textToSearch=" + HotelName.split("\\(")[0] +
                            "&productType=-1&travellerType=0&familyMode=off";

                }

                System.out.println(Reservation_URL);
                Intent detail_to_url = new Intent(Intent.ACTION_WEB_SEARCH);
                detail_to_url.putExtra(SearchManager.QUERY, Reservation_URL);
                // 구글로 검색

                if(detail_to_url.resolveActivity(getPackageManager()) != null) {
                    startActivity(detail_to_url);
                } else {
                    String msg = "웹페이지로 이동할 수 없습니다.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void MakeRoomType(String t, int i) {

        tv_room_type = new TextView(this);

        tv_room_type.setText(t);
        tv_room_type.setTextSize(22);
        tv_room_type.setPadding(0, 20, 0, 20);
        tv_room_type.setGravity(Gravity.CENTER);
        tv_room_type.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        tv_room_type.setTypeface(typeface);

        ll_room_option[i].addView(tv_room_type);
        ll_room_option[i].setBackgroundColor(getResources().getColor(R.color.basic_color_FFFFFF));
    }

    public void MakeTextView(String t, int i, @Nullable String color, int Size, int gravity, int mt, int mb) {

        TextView NotOption = new TextView(this);

        NotOption.setText(t);
        NotOption.setTextSize(Size);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        NotOption.setTypeface(typeface);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp.topMargin = mt;
        lp.bottomMargin = mb;
        if(gravity == 0) {
            lp.gravity = Gravity.LEFT;
        } else if(gravity == 1) {
            lp.gravity = Gravity.CENTER;
        } else if(gravity == 2) {
            lp.gravity = Gravity.RIGHT;
        }
        NotOption.setLayoutParams(lp);

        if(color != null) {
            if(color.equals("BLUE"))
                NotOption.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
        }

        ll_room_option[i].addView(NotOption);
    }

    public void MakeRoomOption(String t, int i,  @Nullable Drawable img) {

        tv_room_option = new TextView(this);

        tv_room_option.setText(t);
        tv_room_option.setTextSize(14);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        tv_room_option.setTypeface(typeface);
        tv_room_option.setPadding(0, 10, 50, 50);

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
