package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.apmem.tools.layouts.FlowLayout;

import android.widget.Spinner;
import android.widget.TextView;

import com.example.along_the_road.adapters.ListViewAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static android.view.View.GONE;
import static com.example.along_the_road.localselectActivity.Code;
import static com.example.along_the_road.localselectActivity.Detail_Code;

public class HotelSelectActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "HotelSelectActivity";

    private final String API_KEY = "c%2BrEUarPSYgJ%2FND6wKCRcSn1oSTDp1R8LM7EanqslnUCnQlIffN8q%2BIyuDljYHdOLwTD67w0LccbXpw0%2BFUJkA%3D%3D";

    // hotel theme code
    private final static String Hotel_t = "B02010100";
    private final static String Hotel_S = "B02010200";
    private final static String Hotel_tr = "B02010300";
    private final static String Hotel_f = "B02010400";

    private String Hotel_theme = null;

    // location code
    private static final int Seoul = 1;
    private static final int Daegu = 4;
    private static final int Busan = 6;
    private static final int Gangwondo = 32;
    private static final int Gyeongju = 35;
    private static final int Jeonju = 37;
    private static final int Yeosu = 38;
    private static final int Jeju = 39;

    public static final int AreaCodeIsNull = 1002;

    private int areaCode = Code;
    private int detailCode = Detail_Code;

    // parsing url
    private String area_Hotel = null;
    private String detail_Hotel = null;
    private String search_url = null;

    // list_length
    private int list_len = 0;

    private int ll_hl_count = 0;

    // save hotel information
    private int[] ContentID;
    private String[] HotelName;
    private String[] HotelImage;
    private String[] checkintime;
    private String[] checkouttime;
    private String[] parkinglodging;
    private String[] roomtype;
    private String[] foodplace;
    private String[] reservationurl;
    private String[] subfacility;

    private int[] state;

    // intent information
    private String startDate;
    private String endDate;

    // layout
    private RelativeLayout rl_info_popup;
    private RelativeLayout rl_popup_info_ok;
    private RelativeLayout rl_map_container;
    private LinearLayout ll_hotel_list;
    private FlowLayout fl_hotel_text;

    // listview
    private ListViewAdapter adapter;
    private ListView lv_hotel_list;
    private ListHotel listHotel;

    // component
    private ClearEditText et_search_text;
    private TextView tv_popup_msg;
    private TextView ith_hotel;
    private Button btn_map_remove;
    private ImageView hotelimg;
    private Drawable ea_img;
    private Drawable hotelImg;
    private Spinner spinner;


    private String[] spinnerArr;
    private String selected_spinner = null;

    private Bitmap bitmap;
    private String google_url;
    private String google_image;
    Handler handler = new Handler();

    private FirebaseAuth firebaseAuth;

    /************* Google Map API 관련 변수 *************/
    private GoogleMap mMap;
    private LatLng[] HotelLocation;
    private MarkerOptions markerOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_select);

        initAllComponent();

        Intent intent = getIntent();
        if (intent == null) {
            // 오류 메시지 팝업
        } else if (intent != null) {
            startDate = intent.getStringExtra("startDate");
            endDate = intent.getStringExtra("endDate");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_hotel_map);
        mapFragment.getMapAsync(this);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        initView();

        listviewSetting();

        et_search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                rl_map_container.setVisibility(GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.fillter(search);
            }
        });

        btn_map_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                rl_map_container.setVisibility(GONE);

            }
        });

        lv_hotel_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(), HotelDetailActivity.class);

                int listNumber = adapter.getItem(position).getListNumber();
                int number = adapter.getItem(position).getContentID();

                Drawable image = adapter.getItem(position).getHotelimage();
                byte[] hotelImage = null;
                if (image != null) {
                    Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    hotelImage = stream.toByteArray();
                }

                intent.putExtra("contentID", Integer.toString(number));
                intent.putExtra("hotelName", adapter.getItem(position).getHotelname());
                intent.putExtra("location", Integer.toString(areaCode));
                intent.putExtra("locationDetail", Integer.toString(detailCode));
                intent.putExtra("startDate", startDate);
                intent.putExtra("endDate", endDate);
                intent.putExtra("URL", reservationurl[listNumber]);
                intent.putExtra("checkIn", adapter.getItem(position).getCheckIn());
                intent.putExtra("checkOut", adapter.getItem(position).getCheckOut());
                intent.putExtra("hotelImage", hotelImage);
                Log.d(TAG, "hotelImage : " + hotelImage);

                adapter.notifyDataSetChanged();

                startActivity(intent);

            }
        });

        lv_hotel_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                int listNumber = adapter.getItem(position).getListNumber();

                rl_map_container.setVisibility(View.VISIBLE);

                markerOptions = new MarkerOptions();
                markerOptions.position(HotelLocation[listNumber]);

                BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_64);
                Bitmap b = bitmapdraw.getBitmap();
                Bitmap smallMarker = Bitmap.createScaledBitmap(b, 120, 120, false);
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
                markerOptions.title(HotelName[listNumber]);

                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(HotelLocation[listNumber]));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(17));

                onMapReady(mMap);

                return true;
            }
        });


    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();

        adapter = new ListViewAdapter();

        rl_info_popup = findViewById(R.id.rl_info_popup);
        rl_popup_info_ok = findViewById(R.id.rl_popup_info_ok);
//        ll_hotel_list = findViewById(R.id.ll_hotel_list);
        rl_map_container = findViewById(R.id.rl_map_container);
        fl_hotel_text = findViewById(R.id.fl_hotel_text);
        lv_hotel_list = findViewById(R.id.lv_hotel_list);

        et_search_text = findViewById(R.id.et_search_text);

        btn_map_remove = findViewById(R.id.btn_map_remove);

        spinner = findViewById(R.id.sp_reselect);

    }

    private void listviewSetting() {

        String resultText = "값이 없음";

        Hotel_theme = Hotel_f;

        if (areaCode == 32 || areaCode == 35 || areaCode == 37 || areaCode == 38) {
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
            resultText = new Task().execute().get();

            search_url = null;

            JSONObject Object = new JSONObject(resultText);

            String response = Object.getString("response");
            JSONObject responseObject = new JSONObject(response);

            String body = responseObject.getString("body");
            JSONObject bodyObject = new JSONObject(body);

            boolean itemscheck = bodyObject.isNull("items");
            if (itemscheck == true || bodyObject.getString("items").equals("")) { // 호텔이 존재하지 않는다면
                String popup_msg = "조건에 해당되는 호텔이 존재하지 않습니다.";
                tv_popup_msg = findViewById(R.id.tv_popup_msg);
                tv_popup_msg.setText(popup_msg);
                rl_info_popup.setVisibility(View.VISIBLE); // 팝업을 띄움

                rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        rl_info_popup.setVisibility(GONE);
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
                state = new int[list_len];

                checkintime = new String[list_len];
                checkouttime = new String[list_len];
                parkinglodging = new String[list_len];
                roomtype = new String[list_len];
                foodplace = new String[list_len];
                reservationurl = new String[list_len];
                subfacility = new String[list_len];

                if (ItemIsWhat.equals("[{")) {

                    for (int i = 0; i < list_len; i++) {

                        JSONObject HotelObject = itemArray.getJSONObject(i);

                        String contentId = HotelObject.getString("contentid");
                        ContentID[i] = Integer.parseInt(contentId);
                        HotelName[i] = HotelObject.getString("title");
                        HotelName[i] = HotelName[i].split("\\[")[0];

                        boolean imagecheck = HotelObject.isNull("firstimage");
                        if (imagecheck == false) {
                            HotelImage[i] = HotelObject.getString("firstimage");

                            final String img_url = HotelImage[i];
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

                                hotelImg = new BitmapDrawable(bitmap);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        } else if (imagecheck == true) {
                            google_url = "https://www.google.com/search?q=" + HotelName[i] + "&tbm=isch";

                            final String google_img = new ImageTask().execute().get();
                            Log.d(TAG, "google_img : " + google_img);

//                            Thread thread = new Thread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    // TODO Auto-generated method stub
//                                    try {
//                                        URL url = new URL(google_img);
//                                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                                        conn.setDoInput(true);
//                                        conn.connect();
//
//                                        InputStream is = conn.getInputStream();
//                                        bitmap = BitmapFactory.decodeStream(is);
//                                    } catch (MalformedURLException e) {
//                                        e.printStackTrace();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//
//                            thread.start();
//
//                            try {
//                                thread.join();
//
//                                hotelImg = new BitmapDrawable(bitmap);
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }

                        }

                        final int no = i;

                        Double Hotelmapx = Double.parseDouble(HotelObject.getString("mapx"));
                        Double Hotelmapy = Double.parseDouble(HotelObject.getString("mapy"));

                        HotelLocation[i] = new LatLng(Hotelmapy, Hotelmapx);

                        detail_Hotel = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?" +
                                "ServiceKey=" + API_KEY + "&contentId=" + ContentID[i] + "&contentTypeId=32" +
                                "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                        System.out.println(detail_Hotel);

                        String resultText2 = "값이 없음";

                        try {

                            search_url = detail_Hotel;
                            resultText2 = new Task().execute().get(); // URL에 있는 내용을 받아옴

                            search_url = null;

                            JSONObject Object2 = new JSONObject(resultText2);

                            String response2 = Object2.getString("response");
                            JSONObject responseObject2 = new JSONObject(response2);

                            boolean bodycheck = responseObject2.isNull("body");
                            if (bodycheck == true) {
                                String errmsg = "LIMITED NUMBER OF SERVICE REQUESTS EXCEEDS ERROR\n" +
                                        "다음날 다시 시도해주세요.";
                            } else if (bodycheck == false) {
                                String body2 = responseObject2.getString("body");

                                JSONObject bodyObject2 = new JSONObject(body2);

                                String items2 = bodyObject2.getString("items");
                                JSONObject itemsObject2 = new JSONObject(items2);

                                String item2 = itemsObject2.getString("item");

                                itemArray = new JSONArray(item);
                                list_len = itemArray.length();

                                JSONObject itemObject2 = new JSONObject(item2);

                                checkintime[i] = itemObject2.getString("checkintime");
                                checkouttime[i] = itemObject2.getString("checkouttime");
                                parkinglodging[i] = itemObject2.getString("parkinglodging");
                                roomtype[i] = itemObject2.getString("roomtype");
                                foodplace[i] = itemObject2.getString("foodplace");
                                boolean urlcheck = itemObject2.isNull("reservationurl");
                                if (urlcheck == false) {
                                    reservationurl[i] = itemObject2.getString("reservationurl");
                                }
                                subfacility[i] = itemObject2.getString("subfacility");

                                if (hotelImg == null) {
                                    listHotel = new ListHotel(ContentID[i], HotelName[i], checkintime[i], checkouttime[i], parkinglodging[i], i);
                                } else {
                                    listHotel = new ListHotel(ContentID[i], hotelImg, HotelName[i], checkintime[i], checkouttime[i], parkinglodging[i], i);
                                }

                                Log.d(TAG, "listHotel : " + listHotel.getHotelname());

                                adapter.addItem(listHotel);

                                hotelImg = null;

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

                } else if (ItemIsWhat.equals("{")) {

                    String contentId = itemObject.getString("contentid");
                    ContentID[0] = Integer.parseInt(contentId);
                    boolean imagecheck = itemObject.isNull("firstimage");
                    if (imagecheck == false) {
                        HotelImage[0] = itemObject.getString("firstimage");

                        final String img_url = HotelImage[0];

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

                            hotelImg = new BitmapDrawable(bitmap);
                            bitmap = null;

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else if (imagecheck == true) {

                        google_url = "https://www.google.com/search?q=" + HotelName[0] + "&tbm=isch";
                        Log.d(TAG, "google_url : " + google_url);

                        String google_img = new ImageTask().execute().get();

                    }

                    final int no = 0;

                    HotelName[0] = itemObject.getString("title");
                    Double Hotelmapx = Double.parseDouble(itemObject.getString("mapx"));
                    Double Hotelmapy = Double.parseDouble(itemObject.getString("mapy"));

                    HotelLocation[0] = new LatLng(Hotelmapy, Hotelmapx);

                    checkintime[0] = "14:00";
                    checkouttime[0] = "11:00";
                    parkinglodging[0] = "52대";

                    if (hotelImg == null) {
                        listHotel = new ListHotel(ContentID[0], HotelName[0], checkintime[0], checkouttime[0], parkinglodging[0], 0);
                    } else {
                        listHotel = new ListHotel(ContentID[0], hotelImg, HotelName[0], checkintime[0], checkouttime[0], parkinglodging[0], 0);
                    }

                    Log.d(TAG, "listHotel : " + listHotel.getHotelname());
                    Log.d(TAG, "hotelImg : " + hotelImg.getAlpha());

                    adapter.addItem(listHotel);

                    hotelImg = null;

                }

                lv_hotel_list.setAdapter(adapter);

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


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

    }

    private void initView() {

        spinnerArr = getResources().getStringArray(R.array.reselect_local_from_hotel);
        selected_spinner = spinnerArr[0];
        final ArrayAdapter<CharSequence> spinnerLargerAdapter =
                ArrayAdapter.createFromResource(this, R.array.reselect_local_from_hotel, R.layout.spinner_item);
        spinner.setAdapter(spinnerLargerAdapter);
        spinner.setSelection(0);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        areaCode = Seoul;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 3:
                        areaCode = Gangwondo;
                        detailCode = 5;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 4:
                        areaCode = Gangwondo;
                        detailCode = 1;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 5:
                        areaCode = Daegu;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 6:
                        areaCode = Gyeongju;
                        detailCode = 2;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 7:
                        areaCode = Busan;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 8:
                        areaCode = Jeonju;
                        detailCode = 12;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 9:
                        areaCode = Yeosu;
                        detailCode = 13;
                        adapter.clearAllItem();
                        listviewSetting();
                        break;
                    case 10:
                        areaCode = Jeju;
                        adapter.clearAllItem();
                        listviewSetting();
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
    public void onBackPressed() {

        finish();
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
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                return true;
            case R.id.menu_signup:
                startActivity(new Intent(getApplicationContext(), SignupActivity.class));
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(HotelSelectActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), HotelSelectActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

//        private ProgressDialog mDialog = new ProgressDialog(HotelSelectActivity.this);
//
//        @Override
//        protected void onPreExecute() {
//
//            mDialog.setMessage("로딩중입니다.");
//
//            mDialog.show();
//            super.onPreExecute();
//
//        }

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

    public class ImageTask extends AsyncTask<String, Void, String> {

        private String str = "", receiveMsg = "";

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                Document doc = Jsoup.connect(google_url).get();
                Log.d(TAG, "url : " + google_url);
                final Elements hotel_image = doc.body().select("div#islrg div.islrc div.isv-r a.wXeWr div.bRMDJf img");
                Handler handler = new Handler(Looper.getMainLooper()); // 객체생성
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // 이미지정보
                        for(Element element : hotel_image) {
                            receiveMsg = element.absUrl("src");
                        }
                        Log.d(TAG, "google_image : " + receiveMsg);
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } catch (NetworkOnMainThreadException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
    }
}