package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class CheckListActivity extends AppCompatActivity {

    CheckBox part1_1, part1_2, part1_3, part1_4, part1_5, part1_6, part1_7, part1_8,
            part2_1, part2_2, part2_3, part2_4, part2_5, part2_6, part2_7, part2_8, part2_9,
            part3_1, part3_2, part3_3, part3_4, part3_5,
            part4_1, part4_2, part4_3, part4_4, part4_5, part4_6, part4_7, part4_8, part4_9, part4_10,
            part5_1, part5_2, part5_3, part5_4, part5_5, part5_6, part5_7, part5_8;
    Button clear_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);

        part1_1 = (CheckBox) findViewById(R.id.checkBox1);
        part1_2 = (CheckBox) findViewById(R.id.checkBox2);
        part1_3 = (CheckBox) findViewById(R.id.checkBox3);
        part1_4 = (CheckBox) findViewById(R.id.checkBox4);
        part1_5 = (CheckBox) findViewById(R.id.checkBox5);
        part1_6 = (CheckBox) findViewById(R.id.checkBox6);
        part1_7 = (CheckBox) findViewById(R.id.checkBox7);
        part1_8 = (CheckBox) findViewById(R.id.checkBox8);

        part2_1 = (CheckBox) findViewById(R.id.checkBox9);
        part2_2 = (CheckBox) findViewById(R.id.checkBox10);
        part2_3 = (CheckBox) findViewById(R.id.checkBox11);
        part2_4 = (CheckBox) findViewById(R.id.checkBox12);
        part2_5 = (CheckBox) findViewById(R.id.checkBox13);
        part2_6 = (CheckBox) findViewById(R.id.checkBox14);
        part2_7 = (CheckBox) findViewById(R.id.checkBox15);
        part2_8 = (CheckBox) findViewById(R.id.checkBox16);
        part2_9 = (CheckBox) findViewById(R.id.checkBox17);

        part3_1 = (CheckBox) findViewById(R.id.checkBox18);
        part3_2 = (CheckBox) findViewById(R.id.checkBox19);
        part3_3 = (CheckBox) findViewById(R.id.checkBox20);
        part3_4 = (CheckBox) findViewById(R.id.checkBox21);
        part3_5 = (CheckBox) findViewById(R.id.checkBox22);

        part4_1 = (CheckBox) findViewById(R.id.checkBox23);
        part4_2 = (CheckBox) findViewById(R.id.checkBox24);
        part4_3 = (CheckBox) findViewById(R.id.checkBox25);
        part4_4 = (CheckBox) findViewById(R.id.checkBox26);
        part4_5 = (CheckBox) findViewById(R.id.checkBox27);
        part4_6 = (CheckBox) findViewById(R.id.checkBox28);
        part4_7 = (CheckBox) findViewById(R.id.checkBox29);
        part4_8 = (CheckBox) findViewById(R.id.checkBox30);
        part4_9 = (CheckBox) findViewById(R.id.checkBox31);
        part4_10 = (CheckBox) findViewById(R.id.checkBox32);

        part5_1 = (CheckBox) findViewById(R.id.checkBox33);
        part5_2 = (CheckBox) findViewById(R.id.checkBox34);
        part5_3 = (CheckBox) findViewById(R.id.checkBox35);
        part5_4 = (CheckBox) findViewById(R.id.checkBox36);
        part5_5 = (CheckBox) findViewById(R.id.checkBox37);
        part5_6 = (CheckBox) findViewById(R.id.checkBox38);
        part5_7 = (CheckBox) findViewById(R.id.checkBox39);
        part5_8 = (CheckBox) findViewById(R.id.checkBox40);

        clear_btn = (Button) findViewById(R.id.check_clear);

        clear_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                part1_1.setChecked(false);
                part1_2.setChecked(false);
                part1_3.setChecked(false);
                part1_4.setChecked(false);
                part1_5.setChecked(false);
                part1_6.setChecked(false);
                part1_7.setChecked(false);
                part1_8.setChecked(false);

                part2_1.setChecked(false);
                part2_2.setChecked(false);
                part2_3.setChecked(false);
                part2_4.setChecked(false);
                part2_5.setChecked(false);
                part2_6.setChecked(false);
                part2_7.setChecked(false);
                part2_8.setChecked(false);
                part2_9.setChecked(false);

                part3_1.setChecked(false);
                part3_2.setChecked(false);
                part3_3.setChecked(false);
                part3_4.setChecked(false);
                part3_5.setChecked(false);

                part4_1.setChecked(false);
                part4_2.setChecked(false);
                part4_3.setChecked(false);
                part4_4.setChecked(false);
                part4_5.setChecked(false);
                part4_6.setChecked(false);
                part4_7.setChecked(false);
                part4_8.setChecked(false);
                part4_9.setChecked(false);
                part4_10.setChecked(false);

                part5_1.setChecked(false);
                part5_2.setChecked(false);
                part5_3.setChecked(false);
                part5_4.setChecked(false);
                part5_5.setChecked(false);
                part5_6.setChecked(false);
                part5_7.setChecked(false);
                part5_8.setChecked(false);

            }
        });

    }
}