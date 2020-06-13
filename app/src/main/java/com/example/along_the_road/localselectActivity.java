package com.example.along_the_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class localselectActivity extends AppCompatActivity {

    private int state = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle("길따라");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_40);

        Button local_btn = (Button)findViewById(R.id.local_conf_btn); //다음페이지

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(state == 1) {
                    Intent local_to_day = new Intent(getApplicationContext(), DaySelectActivity.class);

                    startActivity(local_to_day);
                } else if (state == 0) {
                    Toast.makeText(getApplicationContext(), "지역을 선택하셔야 합니다.", Toast.LENGTH_SHORT);
                }
            }
        });


        final ToggleButton tb2 =
                (ToggleButton) this.findViewById(R.id.locate_btn_busan);

        tb2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tb2.isChecked()){
                    state = 1;
                    tb2.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_busan) //위치크기설정필요
                    );
                }
                else{
                    state = 0;
                    tb2.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_busan)
                    );
                }
            }
        });
    }

}

