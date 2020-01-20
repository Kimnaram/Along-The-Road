package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

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

//        TextView tx1 = (TextView)findViewById(R.id.city);

//        Intent intent = getIntent();
//        String city = intent.getExtras().getString("city");
//        tx1.setText(city);
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