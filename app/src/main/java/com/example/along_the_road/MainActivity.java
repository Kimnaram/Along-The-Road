package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton local_btn = (ImageButton) findViewById(R.id.local_select); // 페이지 전환 버튼 (지역 선택)

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), localselectActivity.class);

                startActivity(intent);
            }
        });

        ImageButton budget_btn = (ImageButton) findViewById(R.id.budget_setting); // 페이지 전환 버튼 (예산 관리)

        budget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bntent = new Intent(getApplicationContext(), managebudgetActivity.class);

                startActivity(bntent);
            }
        });

        ImageButton traffic_btn = (ImageButton) findViewById(R.id.traffic_button); // 페이지 전환 버튼 (교통 확인)

        traffic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tntent = new Intent(getApplicationContext(), TrafficSearchActivity.class);

                startActivity(tntent);
            }
        });

        ImageButton checklist_btn = (ImageButton) findViewById(R.id.checklist_button); // 페이지 전환 버튼 (준비물 체크)

        checklist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cntent = new Intent(getApplicationContext(), CheckListActivity.class);

                startActivity(cntent);
            }
        });

    }
}


