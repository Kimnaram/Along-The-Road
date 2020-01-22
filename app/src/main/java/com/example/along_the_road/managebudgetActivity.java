package com.example.along_the_road;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class managebudgetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        final EditText input = (EditText) findViewById(R.id.input);
        Button budget = (Button) findViewById(R.id.budget);
        final TextView output = (TextView) findViewById(R.id.output);
        final TextView among = (TextView) findViewById(R.id.among);
        final int count = 0;
        final String s_among = Integer.toString(count);
        among.setText(s_among);

        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String add = input.getText().toString();
                String s_cnt = among.getText().toString();
                int cnt = count;
                if(s_cnt != null) {
                    cnt = Integer.parseInt(s_cnt);
                }
                cnt = cnt + Integer.parseInt(add);
                String str_among = Integer.toString(cnt);
                among.setText(str_among);
                output.append("\n");
                output.append(input.getText());

            }
        });
    }
}
