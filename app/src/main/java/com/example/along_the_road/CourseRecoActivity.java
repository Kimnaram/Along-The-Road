package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

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
import java.util.concurrent.ExecutionException;

import static com.example.along_the_road.localselectActivity.Code;
import static com.example.along_the_road.localselectActivity.Detail_Code;

public class CourseRecoActivity extends AppCompatActivity {

    private final String API_KEY = "";
    private String area_Course = null; // URL
    private String detail_Course = null;
    private String what_url = null;
    private String selected_city_txt = null;
    private String selected_course_txt = null;

    //    private int areaCode = 1; // 테스트를 위함
    private int areaCode = Code;
    private int detailCode = Detail_Code;
    private String Theme = null;
    private String Total_Theme = null;

    private int[] ContentID;
    private String[] CourseImg;
    private String[] Title;
    private String[] subname;
    private String[] subdetailimg;

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

    public static final int AreaCodeIsNull = 1001;

    private int list_len = 0;
    private int d_list_len = 0;
    private int C_ll_count = 0;

    private int[] state;

    private RelativeLayout rl_course;
    private RelativeLayout rl_top;
    private RelativeLayout rl_info_popup;
    private RelativeLayout rl_popup_info_ok;
    private LinearLayout ll_course_list;
    private LinearLayout[] ll_course_text_box;
    private FlowLayout[] fl_course_text;

    private TextView tv_selected_course;
    private TextView tv_selected_city;
    private TextView tv_popup_msg;
    private TextView ith_course;

    private Drawable ea_img = null;
    private Drawable c_img = null;
    private Spinner spinner;
    private String[] spinnerArr;
    private String selected_spinner = null;

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

        if (areaCode == 0) {
            Intent course_to_local = new Intent(getApplicationContext(), localselectActivity.class);
            course_to_local.putExtra("REQUEST", AreaCodeIsNull);

            startActivity(course_to_local);
        }

        initView();

        if (C_ll_count > 0) {
            ll_course_list.removeAllViews();
            C_ll_count = 0;
        }

        rl_course = findViewById(R.id.rl_course);
        rl_top = findViewById(R.id.rl_top);
        rl_info_popup = findViewById(R.id.rl_info_popup);
        rl_popup_info_ok = findViewById(R.id.rl_popup_info_ok);
        ll_course_list = findViewById(R.id.ll_course_list);

        tv_selected_city = findViewById(R.id.tv_selected_city);
        tv_selected_course = findViewById(R.id.tv_selected_course);

        final RadioGroup Course_Group = findViewById(R.id.Course_Group);

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

        final Button send_btn = findViewById(R.id.course_select);

        send_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String resultText = "값이 없음";

                if (detailCode != 0) {
                    area_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                            "ServiceKey=" + API_KEY + "&numOfRows=5&pageNo=1" +
                            "&areaCode=" + areaCode + "&code=" + detailCode + "&contentTypeId=25&cat1=C01" +
                            Total_Theme + "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                } else {
                    area_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                            "ServiceKey=" + API_KEY + "&numOfRows=5&pageNo=1" +
                            "&areaCode=" + areaCode + "&contentTypeId=25&cat1=C01" +
                            Total_Theme + "&MobileOS=ETC&MobileApp=AppTest&_type=json";
                }
                // areaCode : 여기서 contentId를 파싱

                System.out.println(area_Course);

                try {

                    what_url = area_Course;
                    resultText = new Task().execute().get(); // URL에 있는 내용을 받아옴

                    what_url = null;

                    JSONObject Object = new JSONObject(resultText);

                    String response = Object.getString("response");
                    JSONObject responseObject = new JSONObject(response);

                    String body = responseObject.getString("body");
                    JSONObject bodyObject = new JSONObject(body);

                    String items = bodyObject.getString("items");
                    System.out.println("아이템즈 : " + items);

                    if (items.isEmpty()) { // 코스가 존재하지 않는다면
                        String popup_msg = "코스가 존재하지 않습니다.";
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

                        JSONObject itemsObject = new JSONObject(items);

                        String item = itemsObject.getString("item");
                        System.out.println(item);
                        String ItemIsWhat = item.split("\"")[0];
                        System.out.println(ItemIsWhat);

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
                                Title[i] = CourseObject.getString("title");

                                ll_course_text_box[i] = new LinearLayout(CourseRecoActivity.this);
                                LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                param.bottomMargin = 10;
                                param.topMargin = 10;
                                ll_course_text_box[i].setLayoutParams(param);
                                ll_course_text_box[i].setOrientation(LinearLayout.VERTICAL);
                                ll_course_list.addView(ll_course_text_box[i]);

                                fl_course_text[i] = new FlowLayout(CourseRecoActivity.this);
                                fl_course_text[i].setLayoutParams(new FlowLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT));
                                fl_course_text[i].setOrientation(FlowLayout.HORIZONTAL);
                                fl_course_text[i].setVisibility(View.GONE);
                                fl_course_text[i].setBackground(getResources().getDrawable(R.drawable.rounded));
                                fl_course_text[i].setBackgroundColor(getResources().getColor(R.color.basic_color_3A7AFF));

                                MakeListTextView(Title[i], i);

                            }

                        } else if (ItemIsWhat.equals("{")) {

                            String contentId = itemObject.getString("contentid");
                            ContentID[0] = Integer.parseInt(contentId);
//                            CourseImg[0] = itemObject.getString("firstimage");
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

                    for (int k = 0; k < ContentID.length; k++) {
                        detail_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?" +
                                "ServiceKey=" + API_KEY + "&contentId=" + ContentID[k] + "&contentTypeId=25" +
                                "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                        System.out.println(detail_Course);
                        System.out.println(Title[k]);

                        String resultText2 = "값이 없음";

                        try {

                            what_url = detail_Course;
                            resultText2 = new Task().execute().get(); // URL에 있는 내용을 받아옴

                            what_url = null;

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

                            subname = new String[d_list_len];
                            subdetailimg = new String[d_list_len];

                            for (int i = 0; i < d_list_len; i++) {

                                JSONObject CourseObject = itemArray.getJSONObject(i);

                                subname[i] = CourseObject.getString("subname");

                                //subdetailimg[i] = CourseObject.getString("subdetailimg");

                                MakeTextView(subname[i], i, k);

                            }

                            for (int i = 0; i < d_list_len; i++) {

                                System.out.println(subname[i]);
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

                    String city = null;
                    String course = null;

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
                            if(detailCode == 1) {
                                city = "강릉";
                            } else if(detailCode == 5) {
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

                    Resources res = getResources();

                    switch(Theme) {
                        case Family_C:
                            course = "가족 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.family_40, null);
                            break;
                        case Solo_C:
                            course = "나홀로 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.solo_40, null);
                            break;
                        case Healing_C:
                            course = "힐링 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.healing_40, null);
                            break;
                        case Walking_C:
                            course = "도보 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.walk_40, null);
                            break;
                        case Camping_C:
                            course = "캠핑 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.camping_40, null);
                            break;
                        case Taste_C:
                            course = "맛집 코스";
                            c_img = ResourcesCompat.getDrawable(res, R.drawable.taste_40, null);
                    }

                    selected_city_txt = city;
                    selected_course_txt = "에서 " + course;

                    tv_selected_city.setText(selected_city_txt);
                    tv_selected_course.setText(selected_course_txt);

                    int h = 90;
                    int w = 90;
                    c_img.setBounds(0, 0, h, w);
                    tv_selected_course.setCompoundDrawables(null, null, c_img, null);

                }
            }

        });

    }

    private void initView() {
        spinner = findViewById(R.id.sp_reselect);
        spinnerArr = getResources().getStringArray(R.array.reselect);
        selected_spinner = spinnerArr[0];
        final ArrayAdapter<CharSequence> spinnerLargerAdapter =
                ArrayAdapter.createFromResource(this, R.array.reselect, R.layout.spinner_item);
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
                        Total_Theme = "";
                        rl_course.setVisibility(View.VISIBLE);

                        rl_top.setVisibility(View.GONE);

                        if(C_ll_count > 0) {
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

        ea_img = ResourcesCompat.getDrawable(res, R.drawable.expand_arrow_48, null);
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
                if(state[no] == 0) {
                    fl_course_text[no].setVisibility(View.VISIBLE);
                    state[no] = 1;
//                    Drawable ca_img = ResourcesCompat.getDrawable(res, R.drawable.collapse_arrow_48, null);
//                    ca_img.setBounds(0, 0, 40, 40);
//
//                    ith_course.setCompoundDrawables(null, null, ca_img, null);
                }
                else if(state[no] == 1) {
                    fl_course_text[no].setVisibility(View.GONE);
                    state[no] = 0;
//                    ea_img.setBounds(0, 0, 40, 40);
//                    ith_course.setCompoundDrawables(null, null, ea_img, null);
                }
            }
        });

    }

    public void MakeTextView(String t, int i, int k) {

        TextView course_txt = null;

        course_txt = new TextView(this);
        if(i >= d_list_len - 1) {
            course_txt.setText(t);
        } else {
            course_txt.setText(t + " >");
        }
        course_txt.setTextSize(18);
        course_txt.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        course_txt.setPadding(16, 16, 16, 16);
        course_txt.setCompoundDrawablePadding(2);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquare.ttf");
        course_txt.setTypeface(typeface);

        course_txt.setGravity(Gravity.CENTER_VERTICAL);

        fl_course_text[k].addView(course_txt);

        final String loc = t;

        course_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("from :" + from);
                System.out.println("to : " + to);

                if(from == null && to == null) {
                    from = loc;
                } else if(from != null && to == null) {
                    to = loc;
                    // 눌리면 교통검색 페이지로 이동?
                    Intent course_to_traffic = new Intent(getApplicationContext(), TrafficSearchActivity.class);
                    course_to_traffic.putExtra("from", from);
                    course_to_traffic.putExtra("to", to);

                    startActivity(course_to_traffic);
                } else if(from != null && to != null) {
                    from = loc;
                    to = null;
                }
            }
        });
    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(what_url);

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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{ //툴바 뒤로가기 동작
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        areaCode = 0;
        detailCode = 0;
        area_Course = null; // URL
        detail_Course = null;
        what_url = null;

        Intent course_to_main = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(course_to_main);
    }

}
