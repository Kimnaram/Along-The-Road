package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button local_btn = (Button) findViewById(R.id.local_select); // 페이지 전환 버튼

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), localselectActivity.class);

                startActivity(intent);
            }
        });

        Button budget_btn = (Button) findViewById(R.id.budget_setting); // 페이지 전환 버튼

        budget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bntent = new Intent(getApplicationContext(), managebudgetActivity.class);

                startActivity(bntent);
            }
        });

        Button traffic_btn = findViewById(R.id.traffic_button);

        traffic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tntent = new Intent(getApplicationContext(), TrafficSearchActivity.class);

                startActivity(tntent);
            }
        });
    }
}


