package com.example.along_the_road;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;
import com.yongbeom.aircalendar.core.AirCalendarIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class localselectActivity extends AppCompatActivity {

    private boolean state = false;
    public static int Code = 0;
    public static int Detail_Code = 0;

    public static final int CALENDAR_REQUEST_CODE = 1001;
    public static final int COURSE_REQUEST_CODE = 1001;
    public static final int HOTEL_REQUEST_CODE = 1002;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        firebaseAuth = FirebaseAuth.getInstance();

        final ImageButton local_btn = findViewById(R.id.local_conf_btn);

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

                        if (state == true && REQUEST_CODE == 1001) {
                            if (Code != 0) {
                                Intent local_to_course = new Intent(getApplicationContext(), CourseRecoActivity.class);

                                startActivity(local_to_course);
                            } else {
                                Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                            }
                        } else if(state == true && REQUEST_CODE == 1002) {
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
        finish();
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
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), MEMBER_LoginActivity.class));
                return true;
            case R.id.menu_signup:
                startActivity(new Intent(getApplicationContext(), MEMBER_RegisterActivity.class));
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(localselectActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), localselectActivity.class));
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

