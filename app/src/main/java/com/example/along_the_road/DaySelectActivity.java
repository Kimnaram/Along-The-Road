package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;
import com.yongbeom.aircalendar.core.AirCalendarIntent;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DaySelectActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        AirCalendarIntent intent = new AirCalendarIntent(this);
        intent.isBooking(false);
        intent.isSelect(false);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat Year = new SimpleDateFormat("yyyy");
        String Start_Year = Year.format(cal.getTime());
        int yyyy = Integer.parseInt(Start_Year);
        // 현재 연도 계산
        yyyy += 1;
        intent.setMaxYear(yyyy);
        intent.setActiveMonth(2);
        intent.setWeekStart(Calendar.SUNDAY);
        intent.setWeekDaysLanguage(AirCalendarIntent.Language.KO); //language for the weekdays

        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            if(data != null){
                Intent intent = new Intent(getApplicationContext(), HotelSelectActivity.class);
                intent.putExtra("Start_Date", data.getStringArrayExtra(AirCalendarDatePickerActivity.RESULT_SELECT_START_DATE));
                intent.putExtra("End_Date", data.getStringExtra(AirCalendarDatePickerActivity.RESULT_SELECT_END_DATE));

                startActivity(intent);
            }
        }
    }
}
