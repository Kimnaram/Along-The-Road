package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

public class dayselectActivity extends AppCompatActivity {

    private TextView textView_Date;
    private DatePickerDialog.OnDateSetListener callbackMethod;

   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayselect);

        this.InitiallizeView();
        this.InitializeListener();

        TextView tx1 = (TextView)findViewById(R.id.city);

        Intent intent = getIntent();

        String city = intent.getExtras().getString("city");
        tx1.setText(city);
    }

    public void InitiallizeView() {
       textView_Date = (TextView)findViewById(R.id.dayselect);
    }

    public void InitializeListener() {
       callbackMethod = new DatePickerDialog.OnDateSetListener() {
           @Override
           public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               textView_Date.setText(year + "년" + month + "월" + dayOfMonth + "일을 선택하셨습니다.");
           }
       };
    }

    public void OnclickHandler(View view) {
       DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod, 2019, 5, 24);

       dialog.show();
    }
}
