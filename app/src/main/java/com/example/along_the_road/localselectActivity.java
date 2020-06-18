package com.example.along_the_road;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class localselectActivity extends AppCompatActivity {

    private boolean state = false;
    private String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        ImageButton local_btn = (ImageButton) findViewById(R.id.local_conf_btn); //다음페이지

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (state == true) {
                    Intent local_to_day = new Intent(getApplicationContext(), DaySelectActivity.class);
                    local_to_day.putExtra("city", city);

                    startActivity(local_to_day);
                } else {
                    Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT).show();
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
                    city = "부산";
                    state = true;
                    busan_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_busan)
                    );
                } else {
                    city = null;
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
                    city = "대구";
                    state = true;
                    daegu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_daegu)
                    );
                } else {
                    city = null;
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
                    city = "강릉";
                    state = true;
                    gangneung_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_gangneung)
                    );
                } else {
                    city = null;
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
                    city = "경주";
                    gyeongju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_gyeongju)
                    );
                } else {
                    city = null;
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
                    city = "제주";
                    jeju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_jeju)
                    );
                } else {
                    city = null;
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
                    city = "전주";
                    jeonju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_jeonju)
                    );
                } else {
                    city = null;
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
                    city = "서울";
                    seoul_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_seoul)
                    );
                } else {
                    city = null;
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
                    city = "속초";
                    sockcho_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_sokcho)
                    );
                } else {
                    city = null;
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
                    city = "여수";
                    yeosu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_yeosu)
                    );
                } else {
                    city = null;
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
        Intent local_to_main = new Intent(getApplicationContext(), MainActivity.class);

        startActivity(local_to_main);
    }
}

