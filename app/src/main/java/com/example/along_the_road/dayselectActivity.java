package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class dayselectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        TextView tx1 = (TextView)findViewById(R.id.textView);

        Intent intent = getIntent();

        String city = intent.getExtras().getString("city");
        tx1.setText(city);
    }
}
