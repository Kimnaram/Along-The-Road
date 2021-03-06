package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.example.along_the_road.R.drawable.main_menu_white;

public class HotelDetailActivity extends AppCompatActivity {

    private static final String TAG = "HotelDetailActivity";

    private static final String API_KEY = "API KEY";

    private static final int Seoul = 1;
    private static final int Daegu = 4;
    private static final int Busan = 6;
    private static final int Gangwondo = 32;
    private static final int Gyeongju = 35;
    private static final int Jeonju = 37;
    private static final int Yeosu = 38;
    private static final int Jeju = 39;

    private String RoomDetail;

    private int ContentID;
    private String HotelName;
    private int location;
    private int locationDetail;
    private String City;
    private String Start_Date;
    private String End_Date;
    private String Stay;
    private String URL;
    private String CheckIn;
    private String CheckOut;
    private byte[] hotelImage;
    private Drawable Image;

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
    private String[] roomintro;
    private String[] roomtype;
    private String[] roombasecount;
    private String[] roommaxcount;

    private boolean offseasonfeecheck;
    private boolean peakseasonfeecheck;
    private boolean basecountcheck;
    private boolean maxcountcheck;
    private boolean introcheck;
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
    private FlowLayout[] fl_fee_view;

    private int fl_count = 0;

    private TextView tv_hotel_name;
    private TextView tv_checkout_time;
    private TextView tv_checkin_time;
    private TextView tv_room_option;
    private TextView tv_room_type;

    private ImageView RoomImage1;

    private Button btn_reservation;
    private Button btn_plus_plan;

    private FirebaseAuth firebaseAuth;
    private DBOpenHelper dbOpenHelper;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private String username = "";
    private String area = "";

    private static String IP_ADDRESS = "IP ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_detail);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 메뉴버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(main_menu_white); // 메뉴 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3A7AFF"))); //툴바 배경색

        firebaseAuth = FirebaseAuth.getInstance();

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        //네비게이션 드로어 헤더 설정
        LinearLayout ll_navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navigation_item, null);
        ll_navigation_container.setBackground(getResources().getDrawable(R.color.basic_color_3A7AFF));
        ll_navigation_container.setPadding(20, 150, 40, 50);
        ll_navigation_container.setOrientation(LinearLayout.VERTICAL);
        ll_navigation_container.setGravity(Gravity.BOTTOM);
        ll_navigation_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquarebold.ttf");
        // 커스텀폰트 오류 발생, 시스템폰트 사용으로 변경
        Typeface typeface = null;
        try {
            typeface = Typeface.createFromAsset(this.getAssets(), "nanumsquarebold.ttf");
        } catch (Exception e) {
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        }

        final TextView tv_username = new TextView(this);
        tv_username.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_username.setTextSize(18);
        tv_username.setTypeface(typeface);
        tv_username.setPadding(0, 2, 0, 2);
        param1.setMargins(20, 20, 20, 5);
        tv_username.setLayoutParams(param1);

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_useremail = new TextView(this);
        tv_useremail.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_useremail.setTextSize(15);
        tv_useremail.setTypeface(typeface);
        tv_useremail.setPadding(0, 2, 0, 2);
        param2.setMargins(20, 0, 20, 10);
        tv_useremail.setLayoutParams(param2);

        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_login_or_out = new TextView(this);
        tv_login_or_out.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_login_or_out.setTextSize(14);
        tv_login_or_out.setTypeface(typeface);
        tv_login_or_out.setPadding(0, 2, 0, 2);
        param3.setMargins(20, 30, 20, 20);
        tv_login_or_out.setGravity(Gravity.RIGHT);

        tv_login_or_out.setLayoutParams(param3);

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            String uid = firebaseUser.getUid();

            dbOpenHelper = new DBOpenHelper(this);
            dbOpenHelper.open();

            Cursor iCursor = dbOpenHelper.selectColumn(uid);

            while (iCursor.moveToNext()) {

                String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
                username = tempName;
                String tempEmail = iCursor.getString(iCursor.getColumnIndex("email"));
                String tempCity = iCursor.getString(iCursor.getColumnIndex("city"));
                if(!iCursor.getString(iCursor.getColumnIndex("city")).equals("null")) {
                    area = tempCity;
                }

                tv_username.setText(tempName + " 님");
                tv_useremail.setText(tempEmail);

            }

//            tv_useremail.setText(firebaseUser.getEmail());
            tv_login_or_out.setText("로그아웃");

            tv_login_or_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uid = firebaseAuth.getCurrentUser().getUid();
                    dbOpenHelper.deleteColumn(uid);

                    firebaseAuth.signOut();

                    Intent navi_to_logout = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(navi_to_logout);
                }
            });

        } else if (firebaseUser == null) {

            tv_username.setText("로그인이 필요합니다.");
            tv_useremail.setVisibility(View.GONE);

            tv_login_or_out.setText("로그인");

            tv_login_or_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent navi_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                    startActivity(navi_to_login);
                }
            });

        }

        ll_navigation_container.addView(tv_username);
        ll_navigation_container.addView(tv_useremail);

        ll_navigation_container.addView(tv_login_or_out);

        navigationView.addHeaderView(ll_navigation_container);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                //길 찾기
                if (id == R.id.item_directions) {
                    startActivity(new Intent(getApplicationContext(), TrafficSearchActivity.class));
                }
                //내 여행 계획 보기
                if (id == R.id.item_checkplan) {
                    startActivity(new Intent(getApplicationContext(), UserPlanActivity.class));
                }
                //후기 구경하기
                if (id == R.id.item_review) {
                    Intent intent = new Intent(getApplicationContext(), PostListActivity.class);
                    intent.putExtra("name", username);

                    startActivity(intent);
                }
                // 예산 관리하기
                if (id == R.id.item_budget) {
                    startActivity(new Intent(getApplicationContext(), managebudgetActivity.class));
                }
                // 행사 안내받기
                if (id == R.id.item_festival) {
                    startActivity(new Intent(getApplicationContext(), FestivalActivity.class));
                }
                // 지역 선택하기
                if (id == R.id.item_local) {
                    startActivity(new Intent(getApplicationContext(), LocalSelectActivity.class));
                }

                return true;
            }
        });

        initAllComponent();

        Intent intent = getIntent();

        if (intent == null) {
            // 오류 메시지 팝업
        } else if (intent != null) {
            String strID = "";
            strID = intent.getStringExtra("contentID");
            ContentID = Integer.parseInt(strID);
            HotelName = intent.getStringExtra("hotelName");
            String s_location = intent.getStringExtra("location");
            String s_locationDetail = intent.getStringExtra("locationDetail");
            Start_Date = intent.getStringExtra("startDate");
            End_Date = intent.getStringExtra("endDate");
            Stay(Start_Date, End_Date);

            URL = intent.getStringExtra("URL");
            CheckIn = intent.getStringExtra("checkIn");
            CheckOut = intent.getStringExtra("checkOut");
            hotelImage = intent.getByteArrayExtra("hotelImage");
            Log.d(TAG, "hotelImage : " + hotelImage);

            location = Integer.parseInt(s_location);
            if (s_locationDetail != null) {
                locationDetail = Integer.parseInt(s_locationDetail);
            }

//            Member = intent.getStringExtra("Member");

            tv_hotel_name.setText(HotelName);
            tv_checkout_time.setText(CheckOut);
            tv_checkin_time.setText(CheckIn);

            RoomDetail = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?ServiceKey=" + API_KEY +
                    "&numOfRows=40&pageNo=1&areaCode=1&contentTypeId=32&contentId=" + ContentID +
                    "&MobileOS=ETC&MobileApp=AppTest&_type=json";

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
                    fl_fee_view = new FlowLayout[list_len];
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
                    roomintro = new String[list_len];
                    roomimg1 = new String[list_len];
                    roombasecount = new String[list_len];
                    roommaxcount = new String[list_len];

                    if (ItemIsWhat.equals("[{")) {

                        for (int i = 0; i < list_len; i++) {

                            if (fl_count > 0) {
//                                ll_room_option[i]
                            }

                            ll_room_option[i] = new LinearLayout(HotelDetailActivity.this);
                            LinearLayout.LayoutParams ll_param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            ll_param.bottomMargin = 50;
                            ll_room_option[i].setLayoutParams(ll_param);
                            ll_room_option[i].setOrientation(LinearLayout.VERTICAL);
                            ll_room_list.addView(ll_room_option[i]);

                            fl_fee_view[i] = new FlowLayout(this);
                            FlowLayout.LayoutParams fl_param1 = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            fl_param1.setGravity(Gravity.CENTER);
                            fl_fee_view[i].setLayoutParams(fl_param1);
                            fl_fee_view[i].setOrientation(FlowLayout.HORIZONTAL);

                            fl_option_list[i] = new FlowLayout(this);
                            FlowLayout.LayoutParams fl_param2 = new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            fl_param2.setGravity(Gravity.CENTER);
                            fl_option_list[i].setLayoutParams(fl_param2);
                            fl_option_list[i].setOrientation(FlowLayout.HORIZONTAL);

                            JSONObject HotelObject = itemArray.getJSONObject(i);

                            roomtype[i] = HotelObject.getString("roomtitle");

                            offseasonfeecheck = HotelObject.isNull("roomoffseasonminfee1");
                            if (offseasonfeecheck == false) {
                                roomoffseasonminfee1[i] = HotelObject.getInt("roomoffseasonminfee1");
                            }

                            peakseasonfeecheck = HotelObject.isNull("roompeakseasonminfee1");
                            if (peakseasonfeecheck == false) {
                                roompeakseasonminfee1[i] = HotelObject.getInt("roompeakseasonminfee1");
                            }

                            basecountcheck = HotelObject.isNull("roombasecount");
                            if (basecountcheck == false) {
                                int basecount = HotelObject.getInt("roombasecount");
                                roombasecount[i] = Integer.toString(basecount);

                            }

                            maxcountcheck = HotelObject.isNull("roommaxcount");
                            if (maxcountcheck == false) {
                                int maxcount = HotelObject.getInt("roommaxcount");
                                roommaxcount[i] = Integer.toString(maxcount);
                            }

                            introcheck = HotelObject.isNull("roomintro");
                            if (introcheck == false) {
                                roomintro[i] = HotelObject.getString("roomintro");
                            }

                            airconditioncheck = HotelObject.isNull("roomaircondition");
                            if (airconditioncheck == false) {
                                roomaircondition[i] = HotelObject.getString("roomaircondition");
                            }

                            bathfacilitycheck = HotelObject.isNull("roombathfacility");
                            if (bathfacilitycheck == false) {
                                roombathfacility[i] = HotelObject.getString("roombathfacility");
                            }

                            toiletriescheck = HotelObject.isNull("roomtoiletries");
                            if (toiletriescheck == false) {
                                roomtoiletries[i] = HotelObject.getString("roomtoiletries");
                            }

                            tablecheck = HotelObject.isNull("roomtable");
                            if (tablecheck == false) {
                                roomtable[i] = HotelObject.getString("roomtable");
                            }

                            cablecheck = HotelObject.isNull("roomcable");
                            if (cablecheck == false) {
                                roomcable[i] = HotelObject.getString("roomcable");
                            }

                            refrigeratorcheck = HotelObject.isNull("roomrefrigerator");
                            if (refrigeratorcheck == false) {
                                roomrefrigerator[i] = HotelObject.getString("roomrefrigerator");
                            }

                            internetcheck = HotelObject.isNull("roominternet");
                            if (internetcheck == false) {
                                roominternet[i] = HotelObject.getString("roominternet");
                            }

                            hairdryercheck = HotelObject.isNull("roomhairdryer");
                            if (hairdryercheck == false) {
                                roomhairdryer[i] = HotelObject.getString("roomhairdryer");
                            }

                            tvcheck = HotelObject.isNull("roomtv");
                            if (tvcheck == false) {
                                roomtv[i] = HotelObject.getString("roomtv");
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

                            String count = null;

                            if (basecountcheck == false || maxcountcheck == false) {
                                if ((basecountcheck == false && Integer.parseInt(roombasecount[i]) != 0)
                                        && (maxcountcheck == false && Integer.parseInt(roommaxcount[i]) != 0)) {
                                    count = roombasecount[i] + "인 기준   |   최대 " + roommaxcount[i] + "인";
                                } else if ((basecountcheck == false && Integer.parseInt(roombasecount[i]) != 0)
                                        && (maxcountcheck == true || Integer.parseInt(roommaxcount[i]) == 0)) {
                                    count = roombasecount[i] + "인 기준";
                                } else if ((basecountcheck == true || Integer.parseInt(roombasecount[i]) == 0)
                                        && (maxcountcheck == false && Integer.parseInt(roommaxcount[i]) != 0)) {
                                    count = "최대 " + roommaxcount[i] + "인";
                                }

                                MakeTextView(count, i, 18, 1, 10, 10, 0, "WHITE", "BLUE");
                            }


                            if (offseasonfeecheck == false || peakseasonfeecheck == false) {
                                String standard = "(1인 1객실 기준 가격)";
                                MakeTextView(standard, i, 14, 2, 20, 10, 0, null, null);
                            }

                            String minfee = null;

                            if ((offseasonfeecheck == false && roomoffseasonminfee1[i] != 0)
                                    && (peakseasonfeecheck == false && roompeakseasonminfee1[i] != 0)) {
                                String offminfee = Integer.toString(roomoffseasonminfee1[i]);
                                String peakminfee = Integer.toString(roompeakseasonminfee1[i]);
                                MakeTextView("비성수기 최소 ", i, 13, 0, 5, 0, 3, null, null);
                                MakeTextView(offminfee + "\\ ~ ", i, 19, 0, 5, 0, 1, null, null);
                                MakeTextView("성수기 최소 ", i, 13, 0, 5, 0, 3, null, null);
                                MakeTextView(peakminfee + "\\", i, 19, 0, 5, 0, 1, null, null);
                            } else if ((offseasonfeecheck == false && roomoffseasonminfee1[i] != 0)
                                    && (peakseasonfeecheck == true || roompeakseasonminfee1[i] == 0)) {
                                String offminfee = Integer.toString(roomoffseasonminfee1[i]);
                                MakeTextView("비성수기 최소 ", i, 13, 0, 5, 0, 3, null, null);
                                MakeTextView(offminfee + "\\", i, 19, 0, 5, 0, 1, null, null);
                            } else if ((offseasonfeecheck == true || roomoffseasonminfee1[i] == 0)
                                    && (peakseasonfeecheck == false && roompeakseasonminfee1[i] != 0)) {
                                String peakminfee = Integer.toString(roompeakseasonminfee1[i]);
                                MakeTextView("성수기 최소 ", i, 13, 0, 5, 0, 3, null, null);
                                MakeTextView(peakminfee + "\\", i, 19, 0, 5, 0, 1, null, null);
                            } else if ((offseasonfeecheck == true || roomoffseasonminfee1[i] == 0)
                                    && (peakseasonfeecheck == true || roompeakseasonminfee1[i] == 0)) {
                                MakeTextView("※ 가격 정보가 없습니다.", i, 19, 0, 5, 5, 1, null, null);
                            }

                            ll_room_option[i].addView(fl_fee_view[i]);

                            String text = "객실 내 시설";
                            MakeTextView(text, i, 18, 1, 45, 15, 0, "WHITE", "BLUE");

                            ll_room_option[i].addView(fl_option_list[i]);

                            Resources res = getResources();
                            if (roomaircondition != null || roomaircondition.equals("Y")) {
                                String ac = "에어컨";
                                ac_img = ResourcesCompat.getDrawable(res, R.drawable.rm_air_conditioner2_100, null);
                                MakeRoomOption(ac, i, ac_img);
                            }
                            if (roomrefrigerator != null || roomrefrigerator.equals("Y")) {
                                String rf = "냉장고";
                                rf_img = ResourcesCompat.getDrawable(res, R.drawable.rm_fridge_100, null);
                                MakeRoomOption(rf, i, rf_img);
                            }
                            if (roominternet != null || roominternet.equals("Y")) {
                                String in = "인터넷";
                                in_img = ResourcesCompat.getDrawable(res, R.drawable.rm_wifi_100, null);
                                MakeRoomOption(in, i, in_img);
                            }
                            if (roomhairdryer != null || roomhairdryer.equals("Y")) {
                                String hd = "헤어 드라이어";
                                hd_img = ResourcesCompat.getDrawable(res, R.drawable.rm_hair_dryer_100, null);
                                MakeRoomOption(hd, i, hd_img);
                            }
                            if (roomtv != null || roomtv.equals("Y")) {
                                String tv = "TV";
                                tv_img = ResourcesCompat.getDrawable(res, R.drawable.rm_tv_100, null);
                                MakeRoomOption(tv, i, tv_img);
                            }
                            if (roomcable != null || roomcable.equals("Y")) {
                                String cb = "케이블";
                                cb_img = ResourcesCompat.getDrawable(res, R.drawable.rm_cable_100, null);
                                MakeRoomOption(cb, i, cb_img);
                            }
                            if (roomtable != null || roomtable.equals("Y")) {
                                String tb = "테이블";
                                tb_img = ResourcesCompat.getDrawable(res, R.drawable.rm_table_100, null);
                                MakeRoomOption(tb, i, tb_img);
                            }
                            if (roombathfacility != null || roombathfacility.equals("Y")) {
                                String bt = "욕조";
                                bt_img = ResourcesCompat.getDrawable(res, R.drawable.rm_bath_100, null);
                                MakeRoomOption(bt, i, bt_img);
                            }
                            if (roomtoiletries != null || roomtoiletries.equals("Y")) {
                                String tl = "세면 용품";
                                tl_img = ResourcesCompat.getDrawable(res, R.drawable.rm_toiletries_100, null);
                                MakeRoomOption(tl, i, tl_img);
                            }

                            if (introcheck == false) {
                                MakeTextView(roomintro[i], i, 14, 2, 30, 50, 0, null, null);
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

        btn_plus_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    location(location, locationDetail);

                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    String uid = user.getUid();
                    String image;
                    if (hotelImage != null) {
                        image = "&image=" + byteArrayToBinaryString(hotelImage);
                        Log.d(TAG, "image : " + image);
                    } else {
                        image = null;
                    }

                    InsertData task = new InsertData();
                    task.execute("http://" + IP_ADDRESS + "/insertPlanHotel.php", uid, City, Start_Date, End_Date, Stay, HotelName, image, URL.split("\"")[1]);

                } else {
                    Toast.makeText(getApplicationContext(), "로그인이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Reservation_URL = null;
                if (URL != null) {
                    URL = URL.split("\"")[1];
                    Reservation_URL = URL;
                    URL = null;
                } else if (URL == null) {
//                    Reservation_URL = "https://www.hotelscombined.co.kr/hotels/" +
//                            Start_Date + "/" + End_Date + "/1adults/1rooms?&placeName=hotel:" + HotelName.split("\\(")[0] + ", 대한민국";
                    Reservation_URL = "https://www.google.co.kr/search?q=" + HotelName + " 예약하기";

                }

                Log.d(TAG, "Reservation URL : " + Reservation_URL);
                Intent detail_to_url = new Intent(Intent.ACTION_WEB_SEARCH);
                detail_to_url.putExtra(SearchManager.QUERY, Reservation_URL);
                // 구글로 검색

                if (detail_to_url.resolveActivity(getPackageManager()) != null) {
                    startActivity(detail_to_url);
                } else {
                    String msg = "웹페이지로 이동할 수 없습니다.";
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn_reservation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        btn_reservation.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                        btn_reservation.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        return false;

                    case MotionEvent.ACTION_UP:
                        btn_reservation.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                        btn_reservation.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        return false;
                }
                return false;
            }
        });
    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();

        ll_room_list = findViewById(R.id.ll_room_list);

        tv_hotel_name = findViewById(R.id.tv_hotel_name);
        tv_checkout_time = findViewById(R.id.tv_checkout_time);
        tv_checkin_time = findViewById(R.id.tv_checkin_time);

        btn_reservation = findViewById(R.id.btn_reservation);
        btn_plus_plan = findViewById(R.id.btn_plus_myplan);

        dbOpenHelper = new DBOpenHelper(this);
        dbOpenHelper.open();

    }

    public void Stay(String start_Date, String end_Date) {
        try {

            SimpleDateFormat transFormat = new SimpleDateFormat("yyyy-MM-dd");

            Date FirstDate = transFormat.parse(start_Date);
            Date SecondDate = transFormat.parse(end_Date);

            // Date로 변환된 두 날짜를 계산한 뒤 그 리턴값으로 long type 변수를 초기화 하고 있다.
            // 연산결과 -950400000. long type 으로 return 된다.
            long calDate = FirstDate.getTime() - SecondDate.getTime();

            // Date.getTime() 은 해당날짜를 기준으로1970년 00:00:00 부터 몇 초가 흘렀는지를 반환해준다.
            // 이제 24*60*60*1000(각 시간값에 따른 차이점) 을 나눠주면 일수가 나온다.
            long calDateDays = calDate / (24 * 60 * 60 * 1000);

            calDateDays = Math.abs(calDateDays);
            String days = Long.toString(calDateDays);
            int nights = Integer.parseInt(days) - 1;

            Stay = nights + "박" + days + "일";


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    // 바이너리 바이트 배열을 스트링으로
    public static String byteArrayToBinaryString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < b.length; ++i) {
            sb.append(byteToBinaryString(b[i]));
        }
        return sb.toString();
    }

    // 바이너리 바이트를 스트링으로
    public static String byteToBinaryString(byte n) {
        StringBuilder sb = new StringBuilder("00000000");
        for (int bit = 0; bit < 8; bit++) {
            if (((n >> bit) & 1) > 0) {
                sb.setCharAt(7 - bit, '1');
            }
        }
        return sb.toString();
    }

    public void location(int location, int locationDetail) {
        switch (location) {
            case Seoul:
                City = "서울";
                break;
            case Daegu:
                City = "대구";
                break;
            case Busan:
                City = "부산";
                break;
            case Gangwondo:
                if (locationDetail == 1) {
                    City = "강릉";
                } else if (locationDetail == 5) {
                    City = "속초";
                }
                break;
            case Gyeongju:
                City = "경주";
                break;
            case Jeonju:
                City = "전주";
                break;
            case Yeosu:
                City = "여수";
                break;
            case Jeju:
                City = "제주";
                break;
        }
    }

    public void MakeRoomType(String t, int i) {

        tv_room_type = new TextView(this);

        tv_room_type.setText(t);
        tv_room_type.setTextSize(22);
        tv_room_type.setPadding(0, 50, 0, 25);
        tv_room_type.setGravity(Gravity.CENTER);
        tv_room_type.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        tv_room_type.setTypeface(typeface);

        ll_room_option[i].addView(tv_room_type);
        ll_room_option[i].setBackgroundColor(getResources().getColor(R.color.basic_color_FFFFFF));
    }

    public void MakeTextView(String t, int i, int Size, int gravity,
                             int margintop, int marginbottom, int textform,
                             @Nullable String color, @Nullable String backgoundColor) {

        TextView NotOption = new TextView(this);

        NotOption.setText(t);
        NotOption.setTextSize(Size);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        NotOption.setTypeface(typeface);

        if (textform == 0) {
            NotOption.setPadding(15, 10, 15, 10);
        } else if (textform == 3) {
            NotOption.setPadding(10, 25, 10, 0);
        }

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        lp.topMargin = margintop;
        lp.bottomMargin = marginbottom;
        if (gravity == 0) {
            lp.gravity = Gravity.LEFT;
        } else if (gravity == 1) {
            lp.gravity = Gravity.CENTER;
        } else if (gravity == 2) {
            lp.gravity = Gravity.RIGHT;
        }
        NotOption.setLayoutParams(lp);

        if (color != null) {
            if (color.equals("BLUE"))
                NotOption.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
            else if (color.equals("YELLOW"))
                NotOption.setTextColor(getResources().getColor(R.color.basic_color_FFCD49));
            else if (color.equals("WHITE"))
                NotOption.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        }

        if (backgoundColor != null) {
            if (backgoundColor.equals("BLUE"))
                NotOption.setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));
            else if (color.equals("YELLOW"))
                NotOption.setTextColor(getResources().getColor(R.color.basic_color_FFCD49));
            else if (color.equals("WHITE"))
                NotOption.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        }

        if (textform == 0) {
            ll_room_option[i].addView(NotOption);
        } else if (textform == 1 || textform == 3) {
            fl_fee_view[i].addView(NotOption);
        }
    }

    public void MakeRoomOption(String t, int i, @Nullable Drawable img) {

        tv_room_option = new TextView(this);

        tv_room_option.setText(t);
        tv_room_option.setTextSize(14);
        tv_room_option.setGravity(Gravity.CENTER);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        tv_room_option.setTypeface(typeface);
        tv_room_option.setPadding(0, 10, 70, 50);

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//        );
//
//        tv_room_option.setLayoutParams(lp);

        if (img != null) {
            int h = 90;
            int w = 90;
            img.setBounds(0, 0, h, w);
            tv_room_option.setCompoundDrawables(null, null, null, img);
            tv_room_option.setCompoundDrawablePadding(20);
        }

        fl_option_list[i].addView(tv_room_option);
        fl_count += 1;
    }

    @Override
    public void onBackPressed() {

        //뒤로가기 버튼으로 네비게이션 드로어 닫기
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if (firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            case R.id.menu_signup:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                return true;
            case R.id.menu_logout:
                final ProgressDialog mDialog = new ProgressDialog(HotelDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                String uid = firebaseAuth.getCurrentUser().getUid();
                dbOpenHelper.deleteColumn(uid);

                FirebaseAuth.getInstance().signOut();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), HotelDetailActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    class InsertData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(HotelDetailActivity.this,
                    "일정 추가중입니다.", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Toast.makeText(getApplicationContext(), "일정을 만들었습니다!", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "POST response  - " + result);
        }


        @Override
        protected String doInBackground(String... params) {

            String uid = (String) params[1];
            String city = (String) params[2];
            String start_date = (String) params[3];
            String end_date = (String) params[4];
            String stay = (String) params[5];
            String hotel_name = (String) params[6];
            String image = (String) params[7];
            String hotel_url = (String) params[8];

            String serverURL = (String) params[0];
            String postParameters = "uid=" + uid + "&city=" + city + "&start_date=" + start_date + "&end_date=" + end_date
                    + "&stay=" + stay + "&hotel_name=" + hotel_name + image + "&url=" + hotel_url;

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "POST response code - " + responseStatusCode);

                InputStream inputStream;
                if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                } else {
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString();


            } catch (Exception e) {

                Log.d(TAG, "InsertData: Error ", e);

                return new String("Error: " + e.getMessage());
            }

        }
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
