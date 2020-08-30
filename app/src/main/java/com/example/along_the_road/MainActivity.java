package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.along_the_road.models.Post;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.along_the_road.R.drawable.main_menu;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private ImageButton local_btn;
    private ImageButton course_btn; // 페이지 전환 버튼 (코스 추천)
    private ImageButton budget_btn; // 페이지 전환 버튼 (예산 관리)
    private ImageButton festival_btn; // 페이지 전환 버튼 (지역 축제)
    private ImageButton review_btn; // 페이지 전환 버튼 (리뷰 작성)

    private String username;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAllComponent();


        // 상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 메뉴버튼 생성
        getSupportActionBar().setHomeAsUpIndicator(main_menu); // 메뉴 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3A7AFF"))); //툴바 배경색

        firebaseAuth = FirebaseAuth.getInstance();

        //네비게이션 드로어 헤더 설정
        LinearLayout ll_navigation_container = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.navigation_item, null);
        ll_navigation_container.setBackground(getResources().getDrawable(R.color.basic_color_3A7AFF));
        ll_navigation_container.setPadding(20, 150, 40, 50);
        ll_navigation_container.setOrientation(LinearLayout.VERTICAL);
        ll_navigation_container.setGravity(Gravity.BOTTOM);
        ll_navigation_container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        //Typeface typeface = Typeface.createFromAsset(getAssets(), "font/nanumsquarebold.ttf");
        // 커스텀폰트 오류 발생, 시스템폰트 사용으로 변경
        Typeface typeface = null;
        try{
            typeface = Typeface.createFromAsset(this.getAssets(),"nanumsquarebold.ttf");
        } catch(Exception e){
            typeface = Typeface.defaultFromStyle(0);
        }

        final TextView tv_username = new TextView(this);
        tv_username.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_username.setTextSize(18);
        tv_username.setTypeface(typeface);
        tv_username.setPadding(0, 2, 0, 2);
        param1.setMargins(20, 20, 20, 5);
        tv_username.setLayoutParams(param1);

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_useremail = new TextView(this);
        tv_useremail.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_useremail.setTextSize(15);
        tv_useremail.setTypeface(typeface);
        tv_useremail.setPadding(0, 2, 0, 2);
        param2.setMargins(20, 0, 20, 10);
        tv_useremail.setLayoutParams(param2);

        LinearLayout.LayoutParams param3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView tv_login_or_out = new TextView(this);
        tv_login_or_out.setTextColor(getResources().getColor(R.color.basic_color_FFFFFF));
        tv_login_or_out.setTextSize(14);
        tv_login_or_out.setTypeface(typeface);
        tv_login_or_out.setPadding(0, 2, 0, 2);
        param3.setMargins(20, 30, 20, 20);
        tv_login_or_out.setGravity(Gravity.RIGHT);

        tv_login_or_out.setLayoutParams(param3);

        final FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if(firebaseUser != null) {
            String uid = firebaseUser.getUid();
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseDatabase.getReference("users").child(uid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot dataSnapshot : snapshot.getChildren())
                        if(dataSnapshot.getKey().equals("name")) {
                            username = dataSnapshot.getValue().toString();
                            tv_username.setText(username + " 님");
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            tv_useremail.setText(firebaseUser.getEmail());
            tv_login_or_out.setText("로그아웃");

            tv_login_or_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    firebaseAuth.signOut();

                    Intent navi_to_logout = new Intent(getApplicationContext(), MainActivity.class);

                    startActivity(navi_to_logout);
                }
            });

        } else if(firebaseUser == null) {

            tv_username.setText("로그인이 필요합니다.");
            tv_useremail.setVisibility(View.GONE);

            tv_login_or_out.setText("로그인");

            tv_login_or_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent navi_to_login = new Intent(getApplicationContext(), MEMBER_LoginActivity.class);

                    startActivity(navi_to_login);
                }
            });

        }

        ll_navigation_container.addView(tv_username);
        ll_navigation_container.addView(tv_useremail);

        ll_navigation_container.addView(tv_login_or_out);

        navigationView.addHeaderView(ll_navigation_container);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                int id = menuItem.getItemId();

                //길 찾기
                if(id == R.id.item_directions) {
                    startActivity(new Intent(getApplicationContext(), TrafficSearchActivity.class));
                }
                //내 여행 계획 보기
                if (id == R.id.item_checkplan) {
                    startActivity(new Intent(getApplicationContext(), UserPlanActivity.class));
                }
                //후기 구경하기
                if (id == R.id.item_review) {
                }

                return true;
            }
        });


        local_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), localselectActivity.class);

                startActivity(intent);
            }
        });

        budget_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), managebudgetActivity.class);

                startActivity(intent);
            }
        });

        course_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CourseRecoActivity.class);

                startActivity(intent);
            }
        });

        festival_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cntent = new Intent(getApplicationContext(), FestivalActivity.class);

                startActivity(cntent);
            }
        });

        review_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PostActivity.class);

                startActivity(intent);
            }
        });

    }

    public void initAllComponent() {

        navigationView = findViewById(R.id.navigationView);
        drawerLayout = findViewById(R.id.drawerLayout);

        local_btn = (ImageButton) findViewById(R.id.local_select); // 페이지 전환 버튼 (지역 선택)
        course_btn = (ImageButton) findViewById(R.id.course_button); // 페이지 전환 버튼 (코스 추천) // 버튼 변경 필요
        budget_btn = (ImageButton) findViewById(R.id.budget_setting); // 페이지 전환 버튼 (예산 관리)
        festival_btn = (ImageButton) findViewById(R.id.festival_button);
        review_btn = (ImageButton) findViewById(R.id.check_review);

    }

    // 메인 화면에서 메뉴 버튼을 클릭했을 때의 행동
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : {
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        //뒤로가기 버튼으로 네비게이션 드로어 닫기
        DrawerLayout drawer = findViewById(R.id.drawerLayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            AlertDialog.Builder alBuilder = new AlertDialog.Builder(this, R.style.AlertDialogStyle);
            alBuilder.setMessage("종료하시겠습니까?");

            // "예" 버튼을 누르면 실행되는 리스너
            alBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            // "아니오" 버튼을 누르면 실행되는 리스너
            alBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    return; // 아무런 작업도 하지 않고 돌아간다
                }
            });
            alBuilder.setTitle("프로그램 종료");
            alBuilder.show(); // AlertDialog.Bulider로 만든 AlertDialog를 보여준다.
        }
    }
}