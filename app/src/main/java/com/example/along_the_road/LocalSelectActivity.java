package com.example.along_the_road;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;
import com.yongbeom.aircalendar.core.AirCalendarIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.along_the_road.R.drawable.main_menu_white;

public class LocalSelectActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private DBOpenHelper dbOpenHelper;

    private boolean state = false;
    public static int Code = 0;
    public static int Detail_Code = 0;

    public static final int CALENDAR_REQUEST_CODE = 1001;
    public static final int COURSE_REQUEST_CODE = 1002;
    public static final int HOTEL_REQUEST_CODE = 1003;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private String username = "";
    private String area = "";

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

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


        final ImageButton local_btn = findViewById(R.id.local_conf_btn);
//        final Button local_btn = findViewById(R.id.btn_local_select);

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (savedInstanceState == null) {
                    Bundle extras = getIntent().getExtras();

                    if (extras == null) {
                        if (state == true) {
                            if (Code != 0) {
                                AirCalendarIntent intent = new AirCalendarIntent(getApplicationContext());
                                intent.isBooking(false);
                                intent.isSelect(false);

                                Calendar cal = Calendar.getInstance();
                                SimpleDateFormat Year = new SimpleDateFormat("yyyy");
                                String Start_Year = Year.format(cal.getTime());
                                int yyyy = Integer.parseInt(Start_Year);
                                // 현재 연도 계산
                                yyyy += 1;
                                intent.setMaxYear(yyyy);
                                intent.setActiveMonth(2);
                                intent.setWeekStart(Calendar.SUNDAY);
                                intent.setWeekDaysLanguage(AirCalendarIntent.Language.KO); //language for the weekdays

                                startActivityForResult(intent, CALENDAR_REQUEST_CODE);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (extras != null) {

                        int REQUEST_CODE = extras.getInt("REQUEST");
                        String fbareaName = extras.getString("fbareaName");

                        if (state == true && REQUEST_CODE == COURSE_REQUEST_CODE) {
                            if (Code != 0) {
                                Intent local_to_course = new Intent(getApplicationContext(), CourseRecoActivity.class);
                                local_to_course.putExtra("fbareaName", fbareaName);
                                Log.d("LocalSelectActivity", "area : " + Code);
                                Log.d("LocalSelectActivity", "detail area : " + Detail_Code);

                                finish();
                                startActivity(local_to_course);
                            } else {
                                Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else if(state == true && REQUEST_CODE == HOTEL_REQUEST_CODE) {
                            if (Code != 0) {
                                Intent local_to_hotel = new Intent(getApplicationContext(), HotelSelectActivity.class);

                                startActivity(local_to_hotel);
                            } else {
                                Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }
        });


        final ToggleButton busan_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_busan);

        final ToggleButton daegu_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_daegu);

        final ToggleButton gangneung_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_gangneung);

        final ToggleButton gyeongju_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_gyeongju);

        final ToggleButton jeju_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_jeju);

        final ToggleButton jeonju_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_jeonju);

        final ToggleButton seoul_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_seoul);

        final ToggleButton sockcho_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_sokcho);

        final ToggleButton yeosu_toggle =
                (ToggleButton) this.findViewById(R.id.locate_btn_yeosu);


        busan_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (busan_toggle.isChecked()) {
                    Code = 6;
                    state = true;
                    busan_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_busan)
                    );
                } else {
                    Code = 0;
                    state = false;
                    busan_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_busan)
                    );
                }
            }
        });

        daegu_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (daegu_toggle.isChecked()) {
                    Code = 4;
                    state = true;
                    daegu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_daegu)
                    );
                } else {
                    Code = 0;
                    state = false;
                    daegu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_daegu)
                    );
                }
            }
        });

        gangneung_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (gangneung_toggle.isChecked()) {
                    Code = 32;
                    Detail_Code = 1;
                    state = true;
                    gangneung_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_gangneung)
                    );
                } else {
                    Code = 0;
                    Detail_Code = 0;
                    state = false;
                    gangneung_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_gangneung)
                    );
                }
            }
        });

        gyeongju_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (gyeongju_toggle.isChecked()) {
                    state = true;
                    Code = 35;
                    Detail_Code = 2;
                    gyeongju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_gyeongju)
                    );
                } else {
                    Code = 0;
                    Detail_Code = 0;
                    state = false;
                    gyeongju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_gyeongju)
                    );
                }
            }
        });

        jeju_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (jeju_toggle.isChecked()) {
                    state = true;
                    Code = 39;
                    jeju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_jeju)
                    );
                } else {
                    Code = 0;
                    state = false;
                    jeju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_jeju)
                    );
                }
            }
        });

        jeonju_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (jeonju_toggle.isChecked()) {
                    state = true;
                    Code = 37;
                    Detail_Code = 12;
                    jeonju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_jeonju)
                    );
                } else {
                    Code = 0;
                    Detail_Code = 0;
                    state = false;
                    jeonju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_jeonju)
                    );
                }
            }
        });

        seoul_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (seoul_toggle.isChecked()) {
                    state = true;
                    Code = 1;
                    seoul_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_seoul)
                    );
                } else {
                    Code = 0;
                    state = false;
                    seoul_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_seoul)
                    );
                }
            }
        });

        sockcho_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sockcho_toggle.isChecked()) {
                    state = true;
                    Code = 32;
                    Detail_Code = 5;
                    sockcho_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_sokcho)
                    );
                } else {
                    Code = 0;
                    Detail_Code = 0;
                    state = false;
                    sockcho_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_sokcho)
                    );
                }
            }
        });

        yeosu_toggle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (yeosu_toggle.isChecked()) {
                    state = true;
                    Code = 38;
                    Detail_Code = 13;
                    yeosu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_yeosu)
                    );
                } else {
                    Code = 0;
                    Detail_Code = 0;
                    state = false;
                    yeosu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_yeosu)
                    );
                }
            }
        });
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
        if(firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(firebaseAuth.getCurrentUser() != null) {
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
                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(LocalSelectActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), LocalSelectActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == CALENDAR_REQUEST_CODE) {
            if(data != null){
                Intent intent = new Intent(getApplicationContext(), HotelSelectActivity.class);
                intent.putExtra("startDate", data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE));
                intent.putExtra("endDate", data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE));

                startActivity(intent);
            }
        }
    }
}

