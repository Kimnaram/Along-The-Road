package com.example.along_the_road;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.along_the_road.models.Post;

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
                Intent intent = new Intent(getApplicationContext(), managebudgetActivity.class);

                startActivity(intent);
            }
        });

        ImageButton course_btn = (ImageButton) findViewById(R.id.traffic_button); // 페이지 전환 버튼 (코스 추천) // 버튼 변경 필요

        course_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseRecoActivity.class);

                startActivity(intent);
            }
        });

        ImageButton festival_btn = (ImageButton) findViewById(R.id.festival_button); // 페이지 전환 버튼 (준비물 체크)

        festival_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cntent = new Intent(getApplicationContext(), FestivalActivity.class);

                startActivity(cntent);
            }
        });

        ImageButton review_btn = findViewById(R.id.check_review); // 리뷰작성 버튼

        review_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);

                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setMessage("종료하시겠습니까?");

        // "예" 버튼을 누르면 실행되는 리스너
        alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish(); // 현재 액티비티를 종료한다.
            }
        });
        // "아니오" 버튼을 누르면 실행되는 리스너
        alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                return; // 아무런 작업도 하지 않고 돌아간다
            }
        });
        alBuilder.setTitle("프로그램 종료");
        alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.

    }
}


