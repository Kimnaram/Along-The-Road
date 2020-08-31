package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;

public class UserPlanActivity extends AppCompatActivity {

    private ImageView iv_hotel_image;
    private TextView tv_area_name;
    private TextView tv_start_date;
    private TextView tv_end_date;
    private TextView tv_hotel_name;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_plan);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3a7aff"))); //툴바 배경색

        initAllComponent();

        if(firebaseAuth.getCurrentUser() != null) {
            String uid = firebaseAuth.getCurrentUser().getUid();

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference("users/" + uid + "/plan").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        if (dataSnapshot.getKey().equals("city")) {
                            tv_area_name.setText(dataSnapshot.getValue().toString());
                        } else if (dataSnapshot.getKey().equals("startDate")) {
                            tv_start_date.setText(dataSnapshot.getValue().toString());
                        } else if (dataSnapshot.getKey().equals("endDate")) {
                            tv_end_date.setText(dataSnapshot.getValue().toString());
                        } else if (dataSnapshot.getKey().equals("hotelName")) {
                            tv_hotel_name.setText(dataSnapshot.getValue().toString());
                        } else if (dataSnapshot.getKey().equals("hotelImage")) {
                            String image = dataSnapshot.getValue().toString();
                            byte[] b = binaryStringToByteArray(image);
                            Log.d("UserPlanActivity", "b : " + b);
                            ByteArrayInputStream is = new ByteArrayInputStream(b);
                            Drawable hotelImage = Drawable.createFromStream(is, "hotelImage");
                            iv_hotel_image.setImageDrawable(hotelImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

    }

    public void initAllComponent() {

        firebaseAuth = FirebaseAuth.getInstance();

        iv_hotel_image = findViewById(R.id.iv_hotel_image);
        tv_area_name = findViewById(R.id.tv_area_name);
        tv_start_date = findViewById(R.id.tv_start_date);
        tv_end_date = findViewById(R.id.tv_end_date);
        tv_hotel_name = findViewById(R.id.tv_hotel_name);

    }

    public static byte[] binaryStringToByteArray(String s) {
        int count = s.length() / 8;
        byte[] b = new byte[count];
        for (int i = 1; i < count; ++i) {
            String t = s.substring((i - 1) * 8, i * 8);
            b[i - 1] = binaryStringToByte(t);
        }
        return b;
    }

    public static byte binaryStringToByte(String s) {
        byte ret = 0, total = 0;
        for (int i = 0; i < 8; ++i) {
            ret = (s.charAt(7 - i) == '1') ? (byte) (1 << i) : 0;
            total = (byte) (ret | total);
        }
        return total;
    }

    @Override
    public void onBackPressed() {

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if(firebaseAuth.getCurrentUser() != null) {
            getMenuInflater().inflate(R.menu.toolbar_al_menu, menu);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: { //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login:
                startActivity(new Intent(getApplicationContext(), MEMBER_LoginActivity.class));
                return true;
            case R.id.menu_signup:
                startActivity(new Intent(getApplicationContext(), MEMBER_RegisterActivity.class));
                return true;
            case R.id.menu_logout:
                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(UserPlanActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                finish();
                mDialog.dismiss();

                startActivity(new Intent(getApplicationContext(), UserPlanActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
