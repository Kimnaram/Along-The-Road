package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class dayselectActivity extends AppCompatActivity {

    private int count = 0;
    private String start_date = null;
    private String end_date = null;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        Intent intent = getIntent();
        final String city = intent.getExtras().getString("city");
        final Button next_btn = (Button)findViewById(R.id.next);

        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);
        final SimpleDateFormat format1 = new SimpleDateFormat ( "MMddyyyy");
        Date date = new Date();

        final String min_date = format1.format(date);
        int temp_md = Integer.parseInt(min_date);
        temp_md = temp_md + 50000000;
        final String max_date = Integer.toString(temp_md);
        final long max_d = Long.parseLong(max_date);
        final long min_d = Long.parseLong(min_date);
        calendar.setMinDate(min_d);
        calendar.setMaxDate(max_d);
       /****************************** min_date, max_date를 어떻게 설정할 것인지 ******************************/


       //리스너 등록
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

           @Override
           public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

               // TODO Auto-generated method stub
               count += 1;

               if(count == 1) {
                   start_date = "" +year + (month + 1) + dayOfMonth;
                   end_date = null;
                   Toast.makeText(dayselectActivity.this, "출발 날짜 : "+year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                   System.out.println(end_date);
               }

               else if(count == 2) {
                   String temp = ""+year + (month + 1) + dayOfMonth;
                   int s_d = Integer.parseInt(start_date);
                   int e_d = Integer.parseInt(temp);
                   if(s_d >= e_d) {
                       Toast.makeText(getApplicationContext(), "도착 날짜가 출발 날짜보다 먼저일 수 없습니다.", Toast.LENGTH_SHORT).show();
                   }
                   else if(s_d < e_d) {
                       end_date = ""+year + (month + 1) + dayOfMonth;
                       Toast.makeText(dayselectActivity.this, "도착 날짜 : "+year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                   }
                   if(start_date != null && end_date != null) {
                       next_btn.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Intent intent = new Intent(getApplicationContext(), hotelselectActivity.class);

                               intent.putExtra("city", city);
                               intent.putExtra("start_date ", start_date);
                               intent.putExtra("end_date", end_date);

                               startActivity(intent);
                           }
                       });
                   }
                   count = 0;
               }

           }
       });

        next_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Toast.makeText(getApplicationContext(), "여행 기간을 선택해주세요.", Toast.LENGTH_SHORT).show();
           }
       });

    }
}