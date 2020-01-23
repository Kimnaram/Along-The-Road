package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

public class dayselectActivity extends AppCompatActivity {

    private DatePickerDialog.OnDateSetListener acallbackMethod;
    private DatePickerDialog.OnDateSetListener scallbackMethod;
    private Button start_date;
    private Button arrive_date;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        this.InitiallizeView();
        this.InitializesListener();
        this.InitializeaListener();

        Intent intent = getIntent();
        final String city = intent.getExtras().getString("city");
        final Button next_btn = (Button)findViewById(R.id.next);
        final Button start_btn = (Button)findViewById(R.id.startdate_btn);
        final Button arrive_btn = (Button)findViewById(R.id.arrivedate_btn);

        final String start_date = start_btn.getText().toString();
        final String arrive_date = arrive_btn.getText().toString();

       //CalendarView 인스턴스 만들기
       CalendarView calendar = (CalendarView)findViewById(R.id.calendar);

       //리스너 등록
       calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

           @Override
           public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

               // TODO Auto-generated method stub
               String start_day = year + "-" + month+1 + "-" + dayOfMonth;
               Toast.makeText(dayselectActivity.this, year+"-"+(month+1)+"-"+dayOfMonth, Toast.LENGTH_SHORT).show();
           }
       });

        next_btn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(getApplicationContext(), hotelselectActivity.class);

               intent.putExtra("city", city);
               intent.putExtra("s_date ", start_date);
               intent.putExtra("a_date", arrive_date);

               startActivity(intent);
           }
       });

    }

    public void InitiallizeView() {
       start_date = (Button) findViewById(R.id.startdate_btn);
       arrive_date = (Button) findViewById(R.id.arrivedate_btn);
    }

    public void InitializesListener() {
       scallbackMethod = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               start_date.setText(year + "년" + month + "월" + dayOfMonth + "일을 선택하셨습니다.");
           }
       };
    }

    public void InitializeaListener() {
       acallbackMethod = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               arrive_date.setText(year + "년" + month + "월" + dayOfMonth + "일을 선택하셨습니다.");
           }
       };
    }

    public void OnaclickHandler(View view) {
       DatePickerDialog dialog = new DatePickerDialog(this, acallbackMethod, 2020, 1, 20);

       dialog.show();
    }

    public void OnsclickHandler(View view) {
        DatePickerDialog dialog = new DatePickerDialog(this, scallbackMethod, 2020, 1, 20);

        dialog.show();
    }
}