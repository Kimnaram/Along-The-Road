package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apmem.tools.layouts.FlowLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static com.example.along_the_road.LocalSelectActivity.Code;
import static com.example.along_the_road.LocalSelectActivity.Detail_Code;

public class CourseRecoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = "CourseRecoActivity";

    private final String COURSE_API_KEY = "API KEY";
    private final String DIRECTIONS_API_KEY = "API KEY";
    private String area_Course = null; // URL
    private String detail_Course = null;
    private String time_and_distance = null;
    private String search_url = null;
    private String selected_city_txt = null;
    private String selected_course_txt = null;
    private String d_and_t = null;

    private int areaCode = Code;
    private int detailCode = Detail_Code;
    private String areaName = "";
    private String fbareaName = "";
    private String city = "";
    private String Theme = null;
    private String Total_Theme = null;
    private boolean bodycheck;

    private int[] ContentID;
    private String[] CourseImg;
    private String[] Title;
    private String[][] subname;
    private String[][] subdetailimg;

    private String from = null;
    private String to = null;

    private static final String Family_C = "C0112";
    private static final String Solo_C = "C0113";
    private static final String Healing_C = "C0114";
    private static final String Walking_C = "C0115";
    private static final String Camping_C = "C0116";
    private static final String Taste_C = "C0117";

    private static final int Seoul = 1;
    private static final int Daegu = 4;
    private static final int Busan = 6;
    private static final int Gangwondo = 32;
    private static final int Gyeongju = 35;
    private static final int Jeonju = 37;
    private static final int Yeosu = 38;
    private static final int Jeju = 39;

    public static final int AreaCodeIsNull = 1002;

    private int list_len = 0;
    private int d_list_len = 0;
    private int p_list_len = 0;
    private int C_ll_count = 0;

    private int[] state;

    private RelativeLayout rl_course;
    private RelativeLayout rl_top;
    private RelativeLayout rl_info_popup;
    private RelativeLayout rl_popup_info_ok;
    private RelativeLayout rl_popup_info_cancel;
    private LinearLayout ll_course_list;
    private LinearLayout[] ll_course_text_box;
    private FlowLayout[] fl_course_text;

    private TextView tv_selected_course;
    private TextView tv_selected_city;
    private TextView tv_popup_msg;
    private TextView ith_course;
    private ImageButton ib_map_remove;
    private Button btn_plus_myplan;
    private Button btn_course_select;

    private Drawable ea_img = null;
    private Drawable c_img = null;
    private Spinner spinner;
    private String[] spinnerArr;
    private String selected_spinner = null;
    private Bitmap bitmap;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    /************* Google Map API 관련 변수 *************/
    private RelativeLayout rl_course_map;
    private GoogleMap mMap;

    private String[][] getPolyline;
    private LatLng[][] getStartLocation;
    private LatLng[][] getEndLocation;
    private String via_arr = "&optimize:true";
    private String Origin = null;
    private String Destination = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_reco);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fr_course_map);
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        if (intent != null) {
            fbareaName = intent.getStringExtra("fbareaName");
            Log.d(TAG, "fbareaName : " + fbareaName);
        }

        initAllComponent();

        if (areaCode == 0 && fbareaName.isEmpty()) {
            String popup_msg = "지역을 선택해야 합니다.";
            tv_popup_msg.setText(popup_msg);
            rl_info_popup.setVisibility(View.VISIBLE);
            rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rl_info_popup.setVisibility(View.GONE);
                    Intent course_to_local = new Intent(getApplicationContext(), LocalSelectActivity.class);
                    course_to_local.putExtra("REQUEST", AreaCodeIsNull);

                    startActivity(course_to_local);
                }
            });
        } else if (areaCode == 0 && !fbareaName.isEmpty()) {
            FixAreaCode(fbareaName);
            AreaCodetoCity();
        } else {
            AreaCodetoCity();
        }

        initView();

        if (C_ll_count > 0) {
            ll_course_list.removeAllViews();
            C_ll_count = 0;
        }

        RadioGroup Course_Group = findViewById(R.id.Course_Group);

        Course_Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.Family_Course:
                        Theme = Family_C;
                        Total_Theme = "&cat2=" + Theme;
                        break;
                    case R.id.Solo_Course:
                        Theme = Solo_C;
                        Total_Theme = "&cat2=" + Theme;
                        break;
                    case R.id.Healing_Course:
                        Theme = Healing_C;
                        Total_Theme = "&cat2=" + Theme;
                        break;
                    case R.id.Walking_Course:
                        Theme = Walking_C;
                        Total_Theme = "&cat2=" + Theme;
                        break;
                    case R.id.Camping_Course:
                        Theme = Camping_C;
                        Total_Theme = "&cat2=" + Theme;
                        break;
                    case R.id.Taste_Course:
                        Theme = Taste_C;
                        Total_Theme = "&cat2=" + Theme;
                        break;
                    default:
                        Total_Theme = "";
                } // 코스를 선택하지 않으면 모든 코스가 출력
            }
        });

        btn_course_select.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                btn_plus_myplan.setVisibility(View.VISIBLE);

                String resultText = "값이 없음";

                if (detailCode != 0) {
                    area_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                            "ServiceKey=" + COURSE_API_KEY + "&numOfRows=15&pageNo=1" +
                            "&areaCode=" + areaCode + "&sigunguCode=" + detailCode + "&contentTypeId=25&cat1=C01" +
                            Total_Theme + "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                    detailCode = 0;

                } else {
                    area_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                            "ServiceKey=" + COURSE_API_KEY + "&numOfRows=15&pageNo=1" +
                            "&areaCode=" + areaCode + "&contentTypeId=25&cat1=C01" +
                            Total_Theme + "&MobileOS=ETC&MobileApp=AppTest&_type=json";
                }
                // areaCode : 여기서 contentId를 파싱

                try {

                    search_url = area_Course;
                    resultText = new Task().execute().get(); // URL에 있는 내용을 받아옴

                    search_url = null;

                    JSONObject Object = new JSONObject(resultText);

                    String response = Object.getString("response");
                    JSONObject responseObject = new JSONObject(response);

                    String body = responseObject.getString("body");
                    JSONObject bodyObject = new JSONObject(body);

                    String items = bodyObject.getString("items");

                    if (items.isEmpty()) { // 코스가 존재하지 않는다면
                        String popup_msg = "코스가 존재하지 않습니다.";
                        tv_popup_msg.setText(popup_msg);
                        rl_top.setVisibility(View.GONE);
                        btn_plus_myplan.setVisibility(View.GONE);
                        rl_info_popup.setVisibility(View.VISIBLE); // 팝업을 띄움

                        rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rl_info_popup.setVisibility(View.GONE);
                                rl_course.setVisibility(View.VISIBLE);
                            }
                        });

                    } else {

                        JSONObject itemsObject = new JSONObject(items);

                        String item = itemsObject.getString("item");
                        String ItemIsWhat = item.split("\"")[0];

                        JSONObject itemObject = null;
                        JSONArray itemArray = null;

                        if (ItemIsWhat.equals("{")) { // 추천 코스가 하나 뿐이라면

                            itemObject = new JSONObject(item);
                            list_len = 1;

                        } else { // 추천 코스가 여러개라면

                            itemArray = new JSONArray(item);
                            list_len = itemArray.length();

                        }

                        ContentID = new int[list_len];
                        CourseImg = new String[list_len];
                        Title = new String[list_len];
                        state = new int[list_len];

                        ll_course_text_box = new LinearLayout[list_len];
                        fl_course_text = new FlowLayout[list_len];

                        if (ItemIsWhat.equals("[{")) {

                            for (int i = 0; i < list_len; i++) {

                                state[i] = 0;

                                JSONObject CourseObject = itemArray.getJSONObject(i);

                                String contentId = CourseObject.getString("contentid");
                                ContentID[i] = Integer.parseInt(contentId);
//                                CourseImg[i] = CourseObject.getString("firstimage");

                                time_and_distance = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?" +
                                        "ServiceKey=" + COURSE_API_KEY + "&contentId=" + ContentID[i] + "&contentTypeId=25" +
                                        "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                                String resultText3 = "값이 없음";

                                try {

                                    resultText3 = new TADTask().execute().get(); // URL에 있는 내용을 받아옴

                                    JSONObject Object2 = new JSONObject(resultText3);

                                    String response2 = Object2.getString("response");
                                    JSONObject responseObject2 = new JSONObject(response2);

                                    boolean bodycheck = responseObject2.isNull("body");
                                    if (bodycheck == false) {
                                        String body2 = responseObject2.getString("body");
                                        JSONObject bodyObject2 = new JSONObject(body2);

                                        String items2 = bodyObject2.getString("items");
                                        JSONObject itemsObject2 = new JSONObject(items2);

                                        String item2 = itemsObject2.getString("item");
                                        JSONObject itemObject2 = new JSONObject(item2);

                                        String distance = itemObject2.getString("distance");

                                        String taketime = itemObject2.getString("taketime");

                                        d_and_t = "코스 총 거리 : " + distance + ", 소요 시간 : " + taketime;
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

                                Title[i] = CourseObject.getString("title");

                                ll_course_text_box[i] = new LinearLayout(CourseRecoActivity.this);
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                param.bottomMargin = 70;
                                param.topMargin = 10;
                                ll_course_text_box[i].setLayoutParams(param);
                                ll_course_text_box[i].setOrientation(LinearLayout.VERTICAL);
                                ll_course_text_box[i].setBackgroundColor(getResources().getColor(R.color.basic_color_FFFFFF));
                                ll_course_list.addView(ll_course_text_box[i]);

                                fl_course_text[i] = new FlowLayout(CourseRecoActivity.this);
                                fl_course_text[i].setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                fl_course_text[i].setOrientation(FlowLayout.HORIZONTAL);
                                fl_course_text[i].setVisibility(View.GONE);
                                fl_course_text[i].setBackground(getResources().getDrawable(R.drawable.rounded));
                                fl_course_text[i].setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));

                                if (bodycheck == false) {
                                    MakeDANDT(d_and_t, i);
                                }
                                MakeListTextView(Title[i], i);

                            }

                        } else if (ItemIsWhat.equals("{")) {

                            String contentId = itemObject.getString("contentid");
                            ContentID[0] = Integer.parseInt(contentId);

                            time_and_distance = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailIntro?" +
                                    "ServiceKey=" + COURSE_API_KEY + "&contentId=" + ContentID[0] + "&contentTypeId=25" +
                                    "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                            String resultText3 = "값이 없음";

                            try {

                                search_url = time_and_distance;
                                resultText3 = new Task().execute().get(); // URL에 있는 내용을 받아옴

                                JSONObject Object2 = new JSONObject(resultText3);

                                String response2 = Object2.getString("response");
                                JSONObject responseObject2 = new JSONObject(response2);

                                String body2 = responseObject2.getString("body");
                                JSONObject bodyObject2 = new JSONObject(body2);

                                String items2 = bodyObject2.getString("items");
                                JSONObject itemsObject2 = new JSONObject(items2);

                                String item2 = itemsObject2.getString("item");
                                JSONObject itemObject2 = new JSONObject(item2);

                                String distance = itemObject2.getString("distance");

                                String taketime = itemObject2.getString("taketime");

                                d_and_t = "코스 총 거리 : " + distance + ", 소요 시간 : " + taketime;
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            Title[0] = itemObject.getString("title");

                            ll_course_text_box[0] = new LinearLayout(CourseRecoActivity.this);
                            ll_course_text_box[0].setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            ll_course_text_box[0].setOrientation(LinearLayout.VERTICAL);
                            ll_course_list.addView(ll_course_text_box[0]);

                            fl_course_text[0] = new FlowLayout(CourseRecoActivity.this);
                            fl_course_text[0].setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT));
                            fl_course_text[0].setOrientation(FlowLayout.HORIZONTAL);
                            fl_course_text[0].setVisibility(View.GONE);
                            fl_course_text[0].setBackground(getResources().getDrawable(R.drawable.rounded));
                            fl_course_text[0].setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));

                            MakeDANDT(d_and_t, 0);
                            MakeListTextView(Title[0], 0);

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

                    subname = new String[ContentID.length][];
                    subdetailimg = new String[ContentID.length][];
                    getPolyline = new String[ContentID.length][];
                    getStartLocation = new LatLng[ContentID.length][];
                    getEndLocation = new LatLng[ContentID.length][];

                    for (int k = 0; k < ContentID.length; k++) {

                        detail_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?" +
                                "ServiceKey=" + COURSE_API_KEY + "&contentId=" + ContentID[k] + "&contentTypeId=25" +
                                "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                        String resultText2 = "값이 없음";

                        try {

                            search_url = detail_Course;
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
                            JSONArray itemArray = new JSONArray(item);
                            d_list_len = itemArray.length();

                            subname[k] = new String[d_list_len];
                            subdetailimg[k] = new String[d_list_len];

                            for (int i = 0; i < d_list_len; i++) {

                                JSONObject CourseObject = itemArray.getJSONObject(i);

                                subname[k][i] = CourseObject.getString("subname");

                                boolean imgcheck = CourseObject.isNull("subdetailimg");
                                if (imgcheck == false) {
                                    subdetailimg[k][i] = CourseObject.getString("subdetailimg");
                                }

                                MakeCourseDetail(subname[k][i], i, k);

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

                    rl_course.setVisibility(View.GONE);
                    rl_top.setVisibility(View.VISIBLE);

                    String course = null;

                    Resources res = getResources();

                    switch (Theme) {
                        case Family_C:
                            course = "가족 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.csr_family_64, null);
                            break;
                        case Solo_C:
                            course = "나홀로 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.csr_solo_64, null);
                            break;
                        case Healing_C:
                            course = "힐링 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.csr_healing_64, null);
                            break;
                        case Walking_C:
                            course = "도보 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.csr_walk_64, null);
                            break;
                        case Camping_C:
                            course = "캠핑 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.csr_camping_64, null);
                            break;
                        case Taste_C:
                            course = "맛집 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.csr_taste_64, null);
                    }

                    selected_city_txt = city;
                    Log.d(TAG, "area : " + areaCode);
                    selected_course_txt = "에서 " + course;

                    tv_selected_city.setText(selected_city_txt);
                    tv_selected_course.setText(selected_course_txt);

                    c_img.setBounds(0, 0, 130, 130);
                    tv_selected_course.setCompoundDrawables(null, null, c_img, null);

                }
            }

        });

        btn_course_select.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN :
                        btn_course_select.setBackground(getResources().getDrawable(R.drawable.btn_style_common_reversal));
                        btn_course_select.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
                        return false;

                    case MotionEvent.ACTION_UP :
                        btn_course_select.setBackground(getResources().getDrawable(R.drawable.btn_style_common));
                        btn_course_select.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
                        return false;
                }
                return false;
            }
        });

        ib_map_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl_course_map.setVisibility(View.GONE);
            }
        });

    }

    private void initView() {
        spinnerArr = getResources().getStringArray(R.array.reselect_course);
        selected_spinner = spinnerArr[0];
        final ArrayAdapter<CharSequence> spinnerLargerAdapter =
                ArrayAdapter.createFromResource(this, R.array.reselect_course, R.layout.spinner_item);
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
                        Intent course_to_local = new Intent(getApplicationContext(), LocalSelectActivity.class);
                        course_to_local.putExtra("fbareaName", fbareaName);
                        course_to_local.putExtra("REQUEST", AreaCodeIsNull);

                        startActivity(course_to_local);
                        selected_spinner = spinnerArr[0];
                        break;
                    case 2:
                        Total_Theme = "";
                        rl_course.setVisibility(View.VISIBLE);

                        rl_top.setVisibility(View.GONE);
                        rl_course_map.setVisibility(View.GONE);

                        if (C_ll_count > 0) {
                            ll_course_list.removeAllViews();
                            C_ll_count = 0;
                        }
                        selected_spinner = spinnerArr[1];
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

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        spinner = findViewById(R.id.sp_reselect);

        rl_info_popup = findViewById(R.id.rl_info_popup);
        rl_popup_info_ok = findViewById(R.id.rl_popup_info_ok);
        rl_popup_info_cancel = findViewById(R.id.rl_popup_info_cancel);
        rl_course_map = findViewById(R.id.rl_course_map);
        rl_course = findViewById(R.id.rl_course);
        rl_top = findViewById(R.id.rl_top);
        ll_course_list = findViewById(R.id.ll_course_list);

        ib_map_remove = findViewById(R.id.ib_map_remove);
        btn_plus_myplan = findViewById(R.id.btn_plus_myplan);
        btn_course_select = findViewById(R.id.btn_course_select);

        tv_selected_city = findViewById(R.id.tv_selected_city);
        tv_selected_course = findViewById(R.id.tv_selected_course);
        tv_popup_msg = findViewById(R.id.tv_popup_msg);

    }

    public void FixAreaCode(String areaName) {
        switch (fbareaName) {
            case "서울":
                areaCode = 1;
                break;
            case "대구":
                areaCode = 4;
                break;
            case "부산":
                areaCode = 6;
                break;
            case "강릉":
                areaCode = 32;
                detailCode = 1;
                break;
            case "속초":
                areaCode = 32;
                detailCode = 5;
                break;
            case "경주":
                areaCode = 35;
                break;
            case "전주":
                areaCode = 37;
                break;
            case "여수":
                areaCode = 38;
                break;
            case "제주":
                areaCode = 39;
                break;
            case "x":
                break;
        }
    }

    public void AreaCodetoCity() {
        switch (areaCode) {
            case Seoul:
                city = "서울";
                break;
            case Daegu:
                city = "대구";
                break;
            case Busan:
                city = "부산";
                break;
            case Gangwondo:
                if (detailCode == 1) {
                    city = "강릉";
                } else if (detailCode == 5) {
                    city = "속초";
                }
                break;
            case Gyeongju:
                city = "경주";
                break;
            case Jeonju:
                city = "전주";
                break;
            case Yeosu:
                city = "여수";
                break;
            case Jeju:
                city = "제주";
                break;
        }
    }

    public void MakeListTextView(String t, int i) {

        ith_course = new TextView(this);
        ith_course.setId(i);
        ith_course.setText("Course " + (i + 1) + ". " + t);
        ith_course.setTextSize(22);
        ith_course.setTextColor(getResources().getColor(R.color.basic_color_3A7AFF));
        ith_course.setPadding(16, 16, 16, 16);
        ith_course.setCompoundDrawablePadding(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        ith_course.setTypeface(typeface);

        final Resources res = getResources();

        ea_img = ResourcesCompat.getDrawable(res, R.drawable.cm_expand_arrow_48, null);
        ea_img.setBounds(0, 0, 40, 40);
        ith_course.setCompoundDrawables(null, null, ea_img, null);
        ith_course.setCompoundDrawablePadding(20);

        ith_course.setGravity(Gravity.CENTER_VERTICAL);

        C_ll_count += 1;
        ll_course_text_box[i].addView(ith_course);
        ll_course_text_box[i].addView(fl_course_text[i]);

        ll_course_list.setVisibility(View.VISIBLE);

        final int no = i;

        ith_course.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 코스가 눌리면 상세 코스가 나오도록
                if (state[no] == 0) {
                    mMap.clear();
                    fl_course_text[no].setVisibility(View.VISIBLE);
                    rl_course_map.setVisibility(View.VISIBLE);
                    state[no] = 1;

                    for (int k = 0; k < getPolyline[no].length; k++) {
                        ArrayList<LatLng> path_points = decodePolyPoints(getPolyline[no][k]); // 폴리라인 포인트 디코드 후 ArrayList에 저장

                        mMap.addMarker(new MarkerOptions().position(getStartLocation[no][k]));
                        mMap.addMarker(new MarkerOptions().position(getEndLocation[no][k]));


                        LatLng START_LOC = new LatLng(path_points.get(0).latitude, path_points.get(0).longitude);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(START_LOC));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

                        Polyline line = null;

                        if (line == null) {
                            line = mMap.addPolyline(new PolylineOptions()
                                    .color(Color.rgb(58, 122, 255))
                                    .geodesic(true)
                                    .addAll(path_points));
                        } else {
                            line.remove();
                            line = mMap.addPolyline(new PolylineOptions()
                                    .color(Color.rgb(58, 122, 255))
                                    .geodesic(true)
                                    .addAll(path_points));
                        }
                    }

                    btn_plus_myplan.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (firebaseAuth.getCurrentUser() != null) {

                                if (fbareaName == null || fbareaName.equals(city)) {
                                    Log.d(TAG, "fbareaName : " + fbareaName);

                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    String uid = user.getUid();

                                    HashMap<Object, String> CourseMap = new HashMap<>();

                                    for (int k = 0; k < subname[no].length; k++) {
                                        String course = subname[no][k].split("\\(")[0];
                                        course = course.split("\\[")[0];
                                        CourseMap.put(Integer.toString(k), course);
                                    }

                                    HashMap<Object, String> ImageMap = new HashMap<>();

                                    for (int k = 0; k < subdetailimg[no].length; k++) {
                                        ImageMap.put(Integer.toString(k), subdetailimg[no][k]);
                                    }

                                    firebaseDatabase = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = firebaseDatabase.getReference("users/" + uid + "/plan");
                                    reference.child("city").setValue(city);
                                    reference.child("course").setValue(CourseMap);
                                    reference.child("courseImage").setValue(ImageMap);

                                    Toast.makeText(getApplicationContext(), "일정을 만들었습니다!", Toast.LENGTH_SHORT).show();
                                } else {
                                    String popup_msg = "초기의 여행 계획과 다른 지역입니다.\n그래도 저장하시겠습니까?";
                                    tv_popup_msg.setText(popup_msg);
                                    rl_top.setVisibility(View.GONE);
                                    btn_plus_myplan.setVisibility(View.GONE);
                                    rl_info_popup.setVisibility(View.VISIBLE); // 팝업을 띄움
                                    rl_popup_info_cancel.setVisibility(View.VISIBLE);

                                    rl_popup_info_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            rl_info_popup.setVisibility(View.GONE);
                                            rl_top.setVisibility(View.VISIBLE);

                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            String uid = user.getUid();

                                            HashMap<Object, String> CourseMap = new HashMap<>();

                                            for (int k = 0; k < subname[no].length; k++) {
                                                String course = subname[no][k].split("\\(")[0];
                                                course = course.split("\\[")[0];
                                                CourseMap.put(Integer.toString(k), course);
                                            }

                                            HashMap<Object, String> ImageMap = new HashMap<>();

                                            for (int k = 0; k < subdetailimg[no].length; k++) {
                                                ImageMap.put(Integer.toString(k), subdetailimg[no][k]);
                                            }

                                            firebaseDatabase = FirebaseDatabase.getInstance();
                                            DatabaseReference reference = firebaseDatabase.getReference("users/" + uid + "/plan");
                                            reference.child("city").setValue(city);
                                            reference.child("course").setValue(CourseMap);
                                            reference.child("courseImage").setValue(ImageMap);

                                            Toast.makeText(getApplicationContext(), "일정을 만들었습니다!", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    rl_popup_info_cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            rl_info_popup.setVisibility(View.GONE);
                                            rl_top.setVisibility(View.VISIBLE);
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "로그인이 필요한 기능입니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                } else if (state[no] == 1) {
                    fl_course_text[no].setVisibility(View.GONE);
                    rl_course_map.setVisibility(View.GONE);
                    state[no] = 0;
                }
            }
        });

    }

    public void MakeDANDT(String t, int i) {

        TextView time_dist = new TextView(this);
        time_dist.setId(i);
        time_dist.setText(t);
        time_dist.setTextSize(18);
        time_dist.setPadding(16, 16, 16, 16);
        time_dist.setGravity(Gravity.RIGHT);
        time_dist.setCompoundDrawablePadding(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        time_dist.setTypeface(typeface);

        ll_course_text_box[i].addView(time_dist);

    }

    public void MakeCourseDetail(String t, int i, int k) {

        TextView course_txt = null;

        if (i == 0) {
            Origin = subname[k][i]; // Origin에는 i가 0일 때의 변수를 집어넣고
        } else if (i > 0 && i < d_list_len - 1) {
            if (subname[k][i].split("\\(")[0].equals("점심식사")) {
                String via = subname[k][i].split("\\(")[1];
                via = via.split("\\)")[0];
                via = via.split(",")[0];
                via_arr += "|" + via;
            } else {
                via_arr += "|" + subname[k][i]; // via_arr에는 i가 0이나 d_list_len이 되기 전까지의 변수를 집어넣고
            }
        } else {
            Destination = subname[k][i]; // Destination에는 i가 d_list_len - 1일 때의 변수를 집어넣은 후에
        }

        if (i >= d_list_len - 1) {
            String url_part1 = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                    Origin + "&destination=" + Destination;
            String url_part2 = "&mode=transit&departure_time=now" +
                    "&alternatives=true&key=" + DIRECTIONS_API_KEY; // URL을 만들고
            String Directions_URL = null;

            Directions_URL = url_part1 + via_arr + url_part2;
            via_arr = "&optimize:true";

            String resultText3 = "값이 없음";

            final int no = k;

            try {

                search_url = Directions_URL;
                resultText3 = new Task().execute().get(); // URL에 있는 내용을 받아옴

                JSONObject jsonObject = new JSONObject(resultText3);
                boolean routecheck = jsonObject.isNull("routes");
                if (routecheck == true) {
                    Toast.makeText(getApplicationContext(), "경로가 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    String routes = jsonObject.getString("routes");

                    JSONArray routesArray = new JSONArray(routes);

                    JSONObject subJsonObject = routesArray.getJSONObject(0);
                    String legs = subJsonObject.getString("legs");
                    JSONArray LegArray = new JSONArray(legs);
                    JSONObject legJsonObject = LegArray.getJSONObject(0);

                    String steps = legJsonObject.getString("steps");
                    JSONArray stepsArray = new JSONArray(steps);

                    p_list_len = stepsArray.length();

                    getPolyline[k] = new String[p_list_len];
                    getStartLocation[k] = new LatLng[p_list_len];
                    getEndLocation[k] = new LatLng[p_list_len];

                    for (int j = 0; j < p_list_len; j++) {

                        JSONObject stepsObject = stepsArray.getJSONObject(j);

                        String end_location = stepsObject.getString("end_location");
                        JSONObject endJsonObject = new JSONObject(end_location);
                        String arrival_lat = endJsonObject.getString("lat");
                        String arrival_lng = endJsonObject.getString("lng");
                        Double arr_lat = Double.parseDouble(arrival_lat);
                        Double arr_lng = Double.parseDouble(arrival_lng);
                        getEndLocation[k][j] = new LatLng(arr_lat, arr_lng);

                        String start_location = stepsObject.getString("start_location");
                        JSONObject startJsonObject = new JSONObject(start_location);
                        String departure_lat = startJsonObject.getString("lat");
                        String departure_lng = startJsonObject.getString("lng");
                        Double dep_lat = Double.parseDouble(departure_lat);
                        Double dep_lng = Double.parseDouble(departure_lng);
                        getStartLocation[k][j] = new LatLng(dep_lat, dep_lng);

                        String polyline = stepsObject.getString("polyline");
                        JSONObject polylineObject = new JSONObject(polyline);
                        getPolyline[k][j] = polylineObject.getString("points");
                    }

                }

            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        course_txt = new TextView(this);
        if (i >= d_list_len - 1) {
            course_txt.setText(t);
        } else {
            course_txt.setText(t + " >");
        }
        course_txt.setTextSize(20);
        course_txt.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        course_txt.setPadding(16, 16, 16, 16);
        course_txt.setCompoundDrawablePadding(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        course_txt.setTypeface(typeface);

        course_txt.setGravity(Gravity.CENTER_VERTICAL);

        fl_course_text[k].addView(course_txt);

        final String loc_valid = t;

        course_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String loc = null;

                if (loc_valid.split("\\(")[0].equals("점심식사")) {
                    loc = loc_valid.split("\\(")[1];
                    loc = loc.split("\\)")[0];
                } else {
                    loc = loc_valid;
                }

                if (from == null && to == null) {
                    from = loc;
                } else if (from != null && to == null) {
                    to = loc;
                    // 눌리면 교통검색 페이지로 이동?
                    Intent course_to_traffic = new Intent(getApplicationContext(), TrafficSearchActivity.class);
                    course_to_traffic.putExtra("from", from);
                    course_to_traffic.putExtra("to", to);

                    startActivity(course_to_traffic);
                } else if (from != null && to != null) {
                    from = loc;
                    to = null;
                }
            }
        });
    }

    public static ArrayList<LatLng> decodePolyPoints(String encodedPath) {
        int len = encodedPath.length();

        final ArrayList<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
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

    public class TADTask extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(time_and_distance);

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

                final ProgressDialog mDialog = new ProgressDialog(CourseRecoActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), CourseRecoActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        areaCode = 0;
        detailCode = 0;
        area_Course = null; // URL
        detail_Course = null;
        search_url = null;

        finish();
    }

}