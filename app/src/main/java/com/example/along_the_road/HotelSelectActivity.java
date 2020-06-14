package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class HotelSelectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_select);
    }

    @Override
    public void onBackPressed() {
        Intent hotel_to_day = new Intent(getApplicationContext(), DaySelectActivity.class);

        startActivity(hotel_to_day);
    }
}