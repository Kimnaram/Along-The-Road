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

        budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                output.append("\n");
                output.append(input.getText());
            }
        });
    }
}
