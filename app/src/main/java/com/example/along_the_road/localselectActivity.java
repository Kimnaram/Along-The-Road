package com.example.along_the_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class localselectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        final Button seoul_btn = (Button)findViewById(R.id.seoul_button);
        final Button sokcho_btn = (Button)findViewById(R.id.sokcho_button);
        final Button incheon_btn = (Button)findViewById(R.id.incheon_button);
        final Button jeonju_btn = (Button)findViewById(R.id.jeonju_button);
        final Button daegu_btn = (Button)findViewById(R.id.daegu_button);
        final Button busan_btn = (Button)findViewById(R.id.busan_button);

        seoul_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DaySelectActivity.class);

                intent.putExtra("city", seoul_btn.getText());

                startActivity(intent);
            }
        });

        sokcho_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DaySelectActivity.class);

                intent.putExtra("city", sokcho_btn.getText());

                startActivity(intent);
            }
        });

        incheon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DaySelectActivity.class);

                intent.putExtra("city", incheon_btn.getText());

                startActivity(intent);
            }
        });

        jeonju_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DaySelectActivity.class);

                intent.putExtra("city", jeonju_btn.getText());

                startActivity(intent);
            }
        });

        daegu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DaySelectActivity.class);

                intent.putExtra("city", daegu_btn.getText());

                startActivity(intent);
            }
        });

        busan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DaySelectActivity.class);

                intent.putExtra("city", busan_btn.getText());

                startActivity(intent);
            }
        });
    }
}
