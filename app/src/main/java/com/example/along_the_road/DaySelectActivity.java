package com.example.along_the_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DaySelectActivity extends AppCompatActivity {

    private int count = 0;
    private String start_date = null;
    private String end_date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        Intent intent = getIntent();
        final String city = intent.getExtras().getString("city");
        final Button next_btn = findViewById(R.id.next);
        final TextView depart_date = findViewById(R.id.depart_date);
        final TextView arrive_date = findViewById(R.id.arrive_date);

        CalendarView calendar = findViewById(R.id.calendar);
        long today = calendar.getDate();
        calendar.setDate(today);
        calendar.setMinDate(today);
        Date date = new Date();

        SimpleDateFormat format1 = new SimpleDateFormat ("MM/dd/yyyy");
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat format2 = new SimpleDateFormat ("MM.dd");
        String depart = format2.format(cal.getTime());
        depart_date.setText(depart);

        cal.add(Calendar.DATE, 2);
        String arrive = format1.format(cal.getTime());
        arrive_date.setText(arrive);

        cal.add(Calendar.MONTH, 2);
        String after = format1.format(cal.getTime());
        long max_date = Long.parseLong(after);
        calendar.setMaxDate(max_date);

        //리스너 등록
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                count += 1;

                if(count == 1) {

                    start_date = "" +year + (month + 1) + dayOfMonth;
                    end_date = null;

                    Toast.makeText(DaySelectActivity.this, "출발 날짜 : "+year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                    System.out.println("도착일 : "+end_date);
                }

                else if(count == 2) {

                    String temp = ""+year + (month + 1) + dayOfMonth;
                    int s_d = Integer.parseInt(start_date);
                    int e_d = Integer.parseInt(temp);

                    if(s_d > e_d) {
                        Toast.makeText(getApplicationContext(), "도착 날짜가 출발 날짜보다 먼저일 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                    else if(s_d <= e_d) {
                        end_date = ""+year + (month + 1) + dayOfMonth;

                        Toast.makeText(DaySelectActivity.this, "도착 날짜 : "+year + "-" + (month + 1) + "-" + dayOfMonth, Toast.LENGTH_SHORT).show();
                        // 출발일부터 도착일까지 캘린더에 칠해서 가시적으로 보이게 수정
                    }
                    if(start_date != null && end_date != null) {
                        next_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), HotelSelectActivity.class);

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