package com.example.along_the_road;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.yongbeom.aircalendar.core.AirCalendarIntent;
import com.yongbeom.aircalendar.AirCalendarDatePickerActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DaySelectActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp_day);

        AirCalendarIntent intent = new AirCalendarIntent(this);
        intent.isBooking(false);
        intent.isSelect(false);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat Year = new SimpleDateFormat("yyyy");
        String Start_Year = Year.format(cal.getTime());
        int yyyy = Integer.parseInt(Start_Year);
        SimpleDateFormat Month = new SimpleDateFormat("MM");
        String Start_Month = Month.format(cal.getTime());
        int MM = Integer.parseInt(Start_Month);
        SimpleDateFormat Date = new SimpleDateFormat("dd");
        String Start_Date = Date.format(cal.getTime());
        int dd = Integer.parseInt(Start_Date);
        // 오늘 날짜 계산

        if(MM >= 10) {
            yyyy += 1;
        }
        cal.add(Calendar.MONTH, 3);
        String End_Month = Month.format(cal.getTime());
        int End_MM = Integer.parseInt(End_Month);

        intent.setStartDate(yyyy, MM, dd); // int
        intent.setEndDate(yyyy, End_MM, dd); // int
        intent.isMonthLabels(false);
        intent.setSelectButtonText("선택"); //the select button text
        intent.setResetBtnText("Reset"); //the reset button text
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
