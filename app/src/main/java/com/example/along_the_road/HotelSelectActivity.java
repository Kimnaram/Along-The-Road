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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

//import com.naver.maps.geometry.LatLng;
//import com.naver.maps.map.CameraAnimation;
//import com.naver.maps.map.CameraPosition;
//import com.naver.maps.map.CameraUpdate;
//import com.naver.maps.map.MapFragment;
//import com.naver.maps.map.NaverMap;
//import com.naver.maps.map.NaverMapSdk;
//import com.naver.maps.map.OnMapReadyCallback;
//import com.naver.maps.map.overlay.Marker;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

import static com.example.along_the_road.localselectActivity.Code;
import static com.example.along_the_road.localselectActivity.Detail_Code;

public class HotelSelectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final String API_KEY = "";

    private final static String Hotel_t = "B02010100";
    private final static String Hotel_S = "B02010200";
    private final static String Hotel_tr = "B02010300";
    private final static String Hotel_f = "B02010400";

    public static final int AreaCodeIsNull = 1002;

    private String Hotel_theme = null;

    private int areaCode = Code;
    private int detailCode = Detail_Code;

    private String area_Hotel = null;
    private String detail_Hotel = null;
    private String search_url = null;

    private int list_len = 0;

    private int ll_hl_count = 0;

    private int[] ContentID;
    private String[] HotelName;
    private String[] HotelImage;
    private String checkintime;
    private String checkouttime;
    private String parkinglodging;
    private String roomtype;
    private String reservationurl;
    private String subfacility;

    private int[] state;

    private RelativeLayout rl_info_popup;
    private RelativeLayout rl_popup_info_ok;
    private RelativeLayout rl_map_container;
    private LinearLayout ll_hotel_list;
    private LinearLayout[] ll_hotel_list_view;
    private LinearLayout[] ll_hotel_text_box;
    private FlowLayout[] fl_hotel_text;

    private TextView tv_popup_msg;
    private TextView ith_hotel;

    private ImageView hotelimg;

    private Drawable ea_img;

    private Spinner spinner;

    private String[] spinnerArr;
    private String selected_spinner = null;

    private Bitmap bitmap;
    Handler handler = new Handler();

//    /************* Naver Map API 관련 변수 *************/
//    private NaverMap nMap;
//    private LatLng[] HotelLocation;

    /************* Google Map API 관련 변수 *************/
    private GoogleMap mMap;
    private LatLng[] HotelLocation;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_select);

//        NaverMapSdk.getInstance(this).setClient(
//                new NaverMapSdk.NaverCloudPlatformClient("ujnor2ft1p"));
//
//        // 네이버 지도 객체 받아오기
//        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.fr_hotel_map);
//        if (mapFragment == null) {
//            mapFragment = MapFragment.newInstance();
//            getSupportFragmentManager().beginTransaction().add(R.id.fr_hotel_map, mapFragment).commit();
//        }
//
//        mapFragment.getMapAsync(this);

        rl_map_container = findViewById(R.id.rl_map_container);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_hotel_map);
        mapFragment.getMapAsync(this);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        rl_info_popup = findViewById(R.id.rl_info_popup);
        rl_popup_info_ok = findViewById(R.id.rl_popup_info_ok);
        ll_hotel_list = findViewById(R.id.ll_hotel_list);

        initView();

        HotelListPrint(); // 호텔 리스트 출력

    }

//    @UiThread
//    @Override
//    public void onMapReady(NaverMap naverMap) {
//
//        nMap = naverMap;
//        naverMap.setLayerGroupEnabled(NaverMap.LAYER_GROUP_TRANSIT, false);
////
////        CameraPosition cameraPosition = new CameraPosition(new LatLng(37.56, 126.97), 14);
////        naverMap.setCameraPosition(cameraPosition);
//
//    }

    private void initView() {
        spinner = findViewById(R.id.sp_reselect);
        spinnerArr = getResources().getStringArray(R.array.reselect_hotel);
        selected_spinner = spinnerArr[0];
        final ArrayAdapter<CharSequence> spinnerLargerAdapter =
                ArrayAdapter.createFromResource(this, R.array.reselect_hotel, R.layout.spinner_item);
        spinner.setAdapter(spinnerLargerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        areaCode = 0;
                        Intent course_to_local = new Intent(getApplicationContext(), localselectActivity.class);
                        course_to_local.putExtra("REQUEST", AreaCodeIsNull);

                        startActivity(course_to_local);
                        selected_spinner = spinnerArr[0];
                        break;
                    case 2:
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

    }

    public void HotelListPrint() {
        String resultText = "값이 없음";

        Hotel_theme = Hotel_f;

        if (detailCode != 0) {
            area_Hotel = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                    "ServiceKey=" + API_KEY + "&numOfRows=20&pageNo=1" + "&areaCode=" + areaCode +
                    "&sigunguCode=" + detailCode + "&contentTypeId=32&cat1=B02&cat2=B0201&cat3=" +
                    Hotel_t + "&arrange=B&MobileOS=ETC&MobileApp=AppTest&_type=json";

            detailCode = 0;

        } else {
            area_Hotel = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                    "ServiceKey=" + API_KEY + "&numOfRows=20&pageNo=1" +
                    "&areaCode=" + areaCode + "&contentTypeId=32&cat1=B02&cat2=B0201&cat3=" +
                    Hotel_t + "&arrange=B&MobileOS=ETC&MobileApp=AppTest&_type=json";
        }
        // areaCode : 여기서 contentId를 파싱

        try {

            search_url = area_Hotel;
            System.out.println(area_Hotel);
            resultText = new Task().execute().get();

            search_url = null;

            JSONObject Object = new JSONObject(resultText);

            String response = Object.getString("response");
            JSONObject responseObject = new JSONObject(response);

            String body = responseObject.getString("body");
            JSONObject bodyObject = new JSONObject(body);

            boolean itemscheck = bodyObject.isNull("items");
            if(itemscheck == true || bodyObject.getString("items").equals("")) { // 호텔이 존재하지 않는다면
                String popup_msg = "조건에 해당되는 호텔이 존재하지 않습니다.";
                tv_popup_msg = findViewById(R.id.tv_popup_msg);
                tv_popup_msg.setText(popup_msg);
                rl_info_popup.setVisibility(View.VISIBLE); // 팝업을 띄움

                rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rl_info_popup.setVisibility(View.GONE);
                    }
                });

            } else {

                String items = bodyObject.getString("items");
                JSONObject itemsObject = new JSONObject(items);

                String item = itemsObject.getString("item");
                String ItemIsWhat = item.split("\"")[0];

                JSONObject itemObject = null;
                JSONArray itemArray = null;

                if (ItemIsWhat.equals("{")) { // 호텔이 하나라면

                    itemObject = new JSONObject(item);
                    list_len = 1;

                } else { // 호텔이 여러개라면

                    itemArray = new JSONArray(item);
                    list_len = itemArray.length();

                }

                ContentID = new int[list_len];
                HotelName = new String[list_len];
                HotelImage = new String[list_len];
                HotelLocation = new LatLng[list_len];
                ll_hotel_list_view = new LinearLayout[list_len];
                ll_hotel_text_box = new LinearLayout[list_len];
                fl_hotel_text = new FlowLayout[list_len];
                state = new int[list_len];

                if (ItemIsWhat.equals("[{")) {

                    for (int i = 0; i < list_len; i++) {

                        ll_hotel_text_box[i] = new LinearLayout(HotelSelectActivity.this);
                        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        param.bottomMargin = 10;
                        param.topMargin = 10;
                        ll_hotel_text_box[i].setLayoutParams(param);
                        ll_hotel_text_box[i].setOrientation(LinearLayout.VERTICAL);
                        ll_hotel_list.addView(ll_hotel_text_box[i]);

                        ll_hotel_list_view[i] = new LinearLayout(HotelSelectActivity.this);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        ll_hotel_list_view[i].setOrientation(LinearLayout.HORIZONTAL);
                        ll_hotel_list_view[i].setBackgroundColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        ll_hotel_text_box[i].addView(ll_hotel_list_view[i]);

                        fl_hotel_text[i] = new FlowLayout(HotelSelectActivity.this);
                        fl_hotel_text[i].setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT));
                        fl_hotel_text[i].setOrientation(FlowLayout.HORIZONTAL);
                        fl_hotel_text[i].setVisibility(View.GONE);
                        fl_hotel_text[i].setBackground(getResources().getDrawable(R.drawable.rounded));
                        fl_hotel_text[i].setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        // 레이아웃 생성

                        state[i] = 0;

                        JSONObject HotelObject = itemArray.getJSONObject(i);

                        String contentId = HotelObject.getString("contentid");
                        ContentID[i] = Integer.parseInt(contentId);
                        HotelName[i] = HotelObject.getString("title");
                        HotelName[i] = HotelName[i].split("\\[")[0];

                        boolean imagecheck = HotelObject.isNull("firstimage");
                        if(imagecheck == false) {
                            HotelImage[i] = HotelObject.getString("firstimage");

                            hotelimg = new ImageView(this);

                            final String img_url = HotelImage[i];
                            final int no = i;

                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    try {
                                        URL url = new URL(img_url);
                                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
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

                                hotelimg.setImageBitmap(bitmap);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT
                                );
                                int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
                                lp.width = size;
                                lp.height = size;
                                hotelimg.setLayoutParams(lp);
                                hotelimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                ll_hotel_list_view[no].addView(hotelimg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }

                        final int no = i;

                        ll_hotel_list_view[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (state[no] == 0) {
                                    fl_hotel_text[no].setVisibility(View.VISIBLE);
                                    // 해당 호텔의 주소를 네이버 지도에 표시
//                    CameraUpdate cameraUpdate = CameraUpdate.toCameraPosition(new CameraPosition(HotelLocation[no].toLatLng(), 16));
//                    nMap.moveCamera(cameraUpdate);
//                    onMapReady(nMap);
//
//                    Marker marker = new Marker();
//                    marker.setPosition(HotelLocation[no]);
//
//                    marker.setMap(nMap);

                                    rl_map_container.setVisibility(View.VISIBLE);
                                    state[no] = 1;

                                    markerOptions = new MarkerOptions();
                                    markerOptions.position(HotelLocation[no]);

                                    BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_64);
                                    Bitmap b = bitmapdraw.getBitmap();
                                    Bitmap smallMarker = Bitmap.createScaledBitmap(b, 120, 120, false);
                                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                                    mMap.addMarker(markerOptions);
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(HotelLocation[no]));
                                    mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                                    onMapReady(mMap);
//                    Drawable ca_img = ResourcesCompat.getDrawable(res, R.drawable.collapse_arrow_48, null);
//                    ca_img.setBounds(0, 0, 40, 40);
//
//                    ith_course.setCompoundDrawables(null, null, ca_img, null);
                                } else if (state[no] == 1) {
                                    fl_hotel_text[no].setVisibility(View.GONE);
                                    mMap.clear();
                                    rl_map_container.setVisibility(View.GONE);

                                    state[no] = 0;
//                    ea_img.setBounds(0, 0, 40, 40);
//                    ith_course.setCompoundDrawables(null, null, ea_img, null);
                                }
                            }
                        });

                        Double Hotelmapx = Double.parseDouble(HotelObject.getString("mapx"));
                        Double Hotelmapy = Double.parseDouble(HotelObject.getString("mapy"));

                        HotelLocation[i] = new LatLng(Hotelmapy, Hotelmapx);

                        MakeListTextView(HotelName[i], HotelImage[i], i);

                    }

                } else if (ItemIsWhat.equals("{")) {

                    ll_hotel_text_box[0] = new LinearLayout(HotelSelectActivity.this);
                    ll_hotel_text_box[0].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    ll_hotel_text_box[0].setOrientation(LinearLayout.HORIZONTAL);
                    ll_hotel_list.addView(ll_hotel_text_box[0]);

                    fl_hotel_text[0] = new FlowLayout(HotelSelectActivity.this);
                    fl_hotel_text[0].setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
                    fl_hotel_text[0].setOrientation(FlowLayout.HORIZONTAL);
                    fl_hotel_text[0].setVisibility(View.GONE);
                    fl_hotel_text[0].setBackground(getResources().getDrawable(R.drawable.rounded));
                    fl_hotel_text[0].setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));

                    String contentId = itemObject.getString("contentid");
                    ContentID[0] = Integer.parseInt(contentId);
                    boolean imagecheck = itemObject.isNull("firstimage");
                    if(imagecheck == false) {
                        HotelImage[0] = itemObject.getString("firstimage");

                        hotelimg = new ImageView(this);

                        final String img_url = HotelImage[0];

                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                try {
                                    URL url = new URL(img_url);
                                    HttpURLConnection conn = (HttpURLConnection)url.openConnection();
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

                            hotelimg.setImageBitmap(bitmap);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                            );
                            int size = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
                            lp.width = size;
                            lp.height = size;
                            hotelimg.setLayoutParams(lp);
                            hotelimg.setScaleType(ImageView.ScaleType.CENTER_CROP);
                            ll_hotel_list_view[0].addView(hotelimg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }

                    final int no = 0;

                    ll_hotel_list_view[0].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (state[no] == 0) {
                                fl_hotel_text[no].setVisibility(View.VISIBLE);
                                // 해당 호텔의 주소를 네이버 지도에 표시
//                    CameraUpdate cameraUpdate = CameraUpdate.toCameraPosition(new CameraPosition(HotelLocation[no].toLatLng(), 16));
//                    nMap.moveCamera(cameraUpdate);
//                    onMapReady(nMap);
//
//                    Marker marker = new Marker();
//                    marker.setPosition(HotelLocation[no]);
//
//                    marker.setMap(nMap);

                                rl_map_container.setVisibility(View.VISIBLE);
                                state[no] = 1;

                                markerOptions = new MarkerOptions();
                                markerOptions.position(HotelLocation[no]);

                                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_64);
                                Bitmap b = bitmapdraw.getBitmap();
                                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 120, 120, false);
                                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));

                                mMap.addMarker(markerOptions);
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(HotelLocation[no]));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                                onMapReady(mMap);
//                    Drawable ca_img = ResourcesCompat.getDrawable(res, R.drawable.collapse_arrow_48, null);
//                    ca_img.setBounds(0, 0, 40, 40);
//
//                    ith_course.setCompoundDrawables(null, null, ca_img, null);
                            } else if (state[no] == 1) {
                                fl_hotel_text[no].setVisibility(View.GONE);
                                mMap.clear();
                                rl_map_container.setVisibility(View.GONE);

                                state[no] = 0;
//                    ea_img.setBounds(0, 0, 40, 40);
//                    ith_course.setCompoundDrawables(null, null, ea_img, null);
                            }
                        }
                    });

                    HotelName[0] = itemObject.getString("title");
                    Double Hotelmapx = Double.parseDouble(itemObject.getString("mapx"));
                    Double Hotelmapy = Double.parseDouble(itemObject.getString("mapy"));

                    HotelLocation[0] = new LatLng(Hotelmapy, Hotelmapx);

                    MakeListTextView(HotelName[0], HotelImage[0], 0);

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

        if (ContentID != null) {
            HotelDetailPrint();
        }

    }

    public void HotelDetailPrint() {

        for (int k = 0; k < ContentID.length; k++) {
            detail_Hotel = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?" +
                    "ServiceKey=" + API_KEY + "&contentId=" + ContentID[k] + "&contentTypeId=32" +
                    "&MobileOS=ETC&MobileApp=AppTest&_type=json";

            System.out.println(detail_Hotel);

            String resultText2 = "값이 없음";

            try {

                search_url = detail_Hotel;
                resultText2 = new Task().execute().get(); // URL에 있는 내용을 받아옴

                search_url = null;

                JSONObject Object = new JSONObject(resultText2);

                String response = Object.getString("response");
                JSONObject responseObject = new JSONObject(response);

                String body = responseObject.getString("body");

                JSONObject bodyObject = new JSONObject(body);

                String items = bodyObject.getString("items");
                JSONObject itemsObject = new JSONObject(items);

                String item = itemsObject.getString("item");
                JSONObject itemObject = new JSONObject(item);

                checkintime = itemObject.getString("checkintime");
                checkouttime = itemObject.getString("checkouttime");
                parkinglodging = itemObject.getString("parkinglodging");
                roomtype = itemObject.getString("roomtype");
                boolean urlcheck = itemObject.isNull("reservationurl");
                if(urlcheck == false) {
                    reservationurl = itemObject.getString("reservationurl");
                }
                subfacility = itemObject.getString("subfacility");

                //subdetailimg[i] = CourseObject.getString("subdetailimg");

                String Text = "체크인 시간 " + checkintime + ", 체크아웃 시간" + checkouttime +
                        "\n주차 가능 여부 : " + parkinglodging +
                        "\n룸 타입 : " + roomtype +
                        "\n사용 가능 시설 : " + subfacility;
                        // "\n예약 URL : " + reservationurl;

                MakeTextView(Text, k);

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

    public void MakeListTextView(String t, @Nullable String iurl, int i) {

        ith_hotel = new TextView(this);
        ith_hotel.setText(t);
        ith_hotel.setTextSize(18);
        ith_hotel.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
        ith_hotel.setPadding(16, 16, 16, 16);
        ith_hotel.setCompoundDrawablePadding(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/robotobold.ttf");
        ith_hotel.setTypeface(typeface);

        final Resources res = getResources();

        ea_img = ResourcesCompat.getDrawable(res, R.drawable.expand_arrow_48, null);
        ea_img.setBounds(0, 0, 40, 40);
        ith_hotel.setCompoundDrawables(null, null, ea_img, null);
        ith_hotel.setCompoundDrawablePadding(20);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        ith_hotel.setLayoutParams(lp);
        ith_hotel.setGravity(Gravity.CENTER_VERTICAL);

        ll_hl_count += 1;
        ll_hotel_list_view[i].addView(ith_hotel);
        ll_hotel_text_box[i].addView(fl_hotel_text[i]);
    }

    public void MakeTextView(String t, int k) {

        TextView hotel_txt = null;

        hotel_txt = new TextView(this);
        hotel_txt.setText(t);
        hotel_txt.setTextSize(16);
        hotel_txt.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        hotel_txt.setPadding(16, 16, 16, 16);
        hotel_txt.setCompoundDrawablePadding(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/robotobold.ttf");
        hotel_txt.setTypeface(typeface);

        hotel_txt.setGravity(Gravity.CENTER_VERTICAL);

        fl_hotel_text[k].addView(hotel_txt);

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
                url = new URL(search_url);

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