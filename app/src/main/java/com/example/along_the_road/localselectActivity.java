package com.example.along_the_road;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class localselectActivity extends AppCompatActivity {

    private boolean state = false;
    public static int Code = 0;
    public static int Detail_Code = 0;

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

        ImageButton local_btn = findViewById(R.id.local_conf_btn);

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (savedInstanceState == null) {
                    Bundle extras = getIntent().getExtras();

                    if (extras == null) {
                        if (state == true) {
                            if (Code != 0) {
                                Intent local_to_day = new Intent(getApplicationContext(), DaySelectActivity.class);

                                startActivity(local_to_day);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (extras != null) {

                        int REQUEST_CODE = extras.getInt("REQUEST");

                        if (state == true && REQUEST_CODE == 1001) {
                            if (Code != 0) {
                                Intent local_to_day = new Intent(getApplicationContext(), CourseRecoActivity.class);

                                startActivity(local_to_day);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
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
}

