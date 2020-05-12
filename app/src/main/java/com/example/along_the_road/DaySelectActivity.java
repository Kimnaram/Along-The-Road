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

    private static int count = 0;
    private String start_date = null;
    private String end_date = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        Intent intent = getIntent();
//        final String city = intent.getExtras().getString("city");
        final Button next_btn = findViewById(R.id.next);
        final TextView depart_date = findViewById(R.id.depart_date);
        final TextView arrive_date = findViewById(R.id.arrive_date);

        CalendarView calendar = findViewById(R.id.calendar);

        long today = calendar.getDate();
        calendar.setDate(today);
        calendar.setMinDate(today); // 오늘 날짜를 minDate로 설정
        Date date = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        SimpleDateFormat format2 = new SimpleDateFormat ("MM.dd");
        String depart = format2.format(cal.getTime());
        depart_date.setText(depart);

        String arrive = getDate(2);
        arrive_date.setText(arrive);

        long max_Date = getMonth(9);
        calendar.setMaxDate(max_Date);

        //리스너 등록
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                count += 1;
                System.out.println("횟수 : "+count);

                if(count % 2 != 0) {

                    start_date = String.valueOf((month + 1) + dayOfMonth);
                    System.out.println("출발일 : " + start_date);
                    end_date = null;
                    System.out.println("도착일 : " + end_date);

                    String mm;
                    if((month+1) < 10) {
                        mm = "0" + (month+1);
                    }
                    else {
                        mm = String.valueOf(month+1);
                    }

                    String dd;
                    if(dayOfMonth < 10) {
                        dd = "0" + (dayOfMonth);
                    }
                    else {
                        dd = String.valueOf(dayOfMonth);
                    }

                    String startDate = mm + "." + dd;
                    depart_date.setText(startDate);
                    arrive_date.setText(startDate);

                    next_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                Toast.makeText(getApplicationContext(), "도착 날짜를 선택해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                else if(count % 2 == 0) {

                    String temp = String.valueOf((month + 1) + dayOfMonth);
                    int s_d = Integer.parseInt(start_date);
                    int e_d = Integer.parseInt(temp);

                    if(s_d > e_d) {
                        Toast.makeText(getApplicationContext(), "도착 날짜가 출발 날짜보다 먼저일 수 없습니다.", Toast.LENGTH_SHORT).show();
                        count = 0;
                    }

                    else if(s_d <= e_d) {
                        end_date = String.valueOf((month + 1) + dayOfMonth);
                        System.out.println("출발일 : " + start_date);
                        System.out.println("도착일 : " + end_date);

                        String mm;
                        if((month+1) < 10) {
                            mm = "0" + (month+1);
                        }
                        else {
                            mm = String.valueOf(month+1);
                        }

                        String dd;
                        if(dayOfMonth < 10) {
                            dd = "0" + (dayOfMonth);
                        }
                        else {
                            dd = String.valueOf(dayOfMonth);
                        }

                        String endDate = mm + "." + dd;
                        arrive_date.setText(endDate);
                        // 출발일부터 도착일까지 캘린더에 칠해서 가시적으로 보이게 수정

                        next_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(getApplicationContext(), HotelSelectActivity.class);

                                // intent.putExtra("city", city);
                                intent.putExtra("start_date ", start_date);
                                intent.putExtra("end_date", end_date);

                                startActivity(intent);
                            }
                        });


                    } // 도착일이 출발일보다 클 때
                } // 두 번째 날짜가 선택됐을 때
            }

        });
    }

    public String getDate (int plusDay) { // 현재 날짜에서 일정 기간 후의 날짜 구하기
        Calendar temp=Calendar.getInstance ( );
        temp.add (Calendar.DAY_OF_MONTH, plusDay);

        int nMonth = temp.get (Calendar.MONTH) + 1;
        int nDay = temp.get (Calendar.DAY_OF_MONTH);

        StringBuffer sbDate=new StringBuffer ();

        if (nMonth < 10)
            sbDate.append ("0");
        sbDate.append (nMonth);
        sbDate.append(".");

        if (nDay < 10)
            sbDate.append ("0");
        sbDate.append (nDay);

        return sbDate.toString ();
    }

    public long getMonth (int plusMonth) { // 현재 날짜에서 일정 기간 후의 달 구하기
        Calendar temp=Calendar.getInstance ( );
        temp.add(Calendar.MONTH, plusMonth);

        long endOfCal = temp.getTimeInMillis();

        return endOfCal;
    }

}