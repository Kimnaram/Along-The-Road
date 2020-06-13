package com.example.along_the_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class localselectActivity extends AppCompatActivity {

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("길따라");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_40);

        ImageButton local_btn = (ImageButton)findViewById(R.id.local_conf_btn); //다음페이지

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(state == 1) {
                    Intent local_to_day = new Intent(getApplicationContext(), DaySelectActivity.class);

                    startActivity(local_to_day);
                } else if (state == 0) {
                    Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT);
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
                if(busan_toggle.isChecked()){
                    state = 1;
                    busan_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_busan)
                    );
                }
                else{
                    state = 0;
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
                if(daegu_toggle.isChecked()){
                    state = 1;
                    daegu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_daegu)
                    );
                }
                else{
                    state = 0;
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
                if(gangneung_toggle.isChecked()){
                    state = 1;
                    gangneung_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_gangneung)
                    );
                }
                else{
                    state = 0;
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
                if(gyeongju_toggle.isChecked()){
                    state = 1;
                    gyeongju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_gyeongju)
                    );
                }
                else{
                    state = 0;
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
                if(jeju_toggle.isChecked()){
                    state = 1;
                    jeju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_jeju)
                    );
                }
                else{
                    state = 0;
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
                if(jeonju_toggle.isChecked()){
                    state = 1;
                    jeonju_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_jeonju)
                    );
                }
                else{
                    state = 0;
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
                if(seoul_toggle.isChecked()){
                    state = 1;
                    seoul_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_seoul)
                    );
                }
                else{
                    state = 0;
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
                if(sockcho_toggle.isChecked()){
                    state = 1;
                    sockcho_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_sokcho)
                    );
                }
                else{
                    state = 0;
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
                if(yeosu_toggle.isChecked()){
                    state = 1;
                    yeosu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_yeosu)
                    );
                }
                else{
                    state = 0;
                    yeosu_toggle.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_yeosu)
                    );
                }
            }
        });
    }
}

