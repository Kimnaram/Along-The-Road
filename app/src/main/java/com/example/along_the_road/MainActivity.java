package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button local_btn;
    Button budget_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        local_btn = (Button) findViewById(R.id.localselect_button); // 페이지 전환 버튼

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), localselectActivity.class);

                startActivity(intent);
            }
        });

        budget_btn = (Button) findViewById(R.id.budgetsetting_button); // 페이지 전환 버튼

        budget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), managebudgetActivity.class);

                startActivity(intent);
            }
        });
    }
}


