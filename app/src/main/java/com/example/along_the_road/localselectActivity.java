package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.text.SimpleDateFormat;

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

        final SimpleDateFormat format1 = new SimpleDateFormat ( "MMddyyyy");

        Date date = new Date();

        final String min_date = format1.format(date);

        System.out.println(min_date);

        seoul_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "seoul");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });

        sokcho_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "sokcho");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });

        incheon_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "incheon");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });

        jeonju_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "jeonju");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });

        daegu_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "daegu");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });

        busan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "busan");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });
    }
}
