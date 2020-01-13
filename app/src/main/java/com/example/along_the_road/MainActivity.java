package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button intent_lsbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("ACTIVITY_LC", "onCreate 호출됨");
        Toast.makeText(getApplicationContext(), "onCreate 호출됨", Toast.LENGTH_SHORT).show();

        intent_lsbtn = findViewById(R.id.localselectbutton);

        intent_lsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onLocalSelectClick(View view) {
                Intent intent = new Intent(MainActivity.this, localselectActivity.class);
                startActivity(intent);
            }
        });
    }
}


