package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.Date;
import java.text.SimpleDateFormat;

public class localselectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        final Button s_btn = (Button)findViewById(R.id.sokcho_button);

        final SimpleDateFormat format1 = new SimpleDateFormat ( "MMddyyyy");

        Date date = new Date();

        final String min_date = format1.format(date);

        System.out.println(min_date);

        s_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), dayselectActivity.class);

                intent.putExtra("city", "sokcho");
                intent.putExtra("min_date", min_date);

                startActivity(intent);
            }
        });
    }
}
