package com.example.along_the_road;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import androidx.appcompat.app.AppCompatActivity;

public class localselectActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);


        Button local_btn = (Button)findViewById(R.id.local_conf_btn); //다음페이지

        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent local_to_day = new Intent(getApplicationContext(), DaySelectActivity.class);
                startActivity(local_to_day);
            }
        });


        final ToggleButton tb2 =
                (ToggleButton) this.findViewById(R.id.locate_btn_busan);

        tb2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(tb2.isChecked()){
                    tb2.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.s_loc_busan) //위치크기설정필요
                    );
                }
                else{
                    tb2.setBackgroundDrawable(
                            getResources().
                                    getDrawable(R.drawable.loc_busan)
                    );
                }
            }
        });
    }

}

