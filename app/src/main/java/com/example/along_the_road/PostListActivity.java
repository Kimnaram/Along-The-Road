package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.along_the_road.adapters.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.view.View.GONE;

public class PostListActivity extends AppCompatActivity {

    private final static String TAG = "PostListActivity";

    // Review list
    private RecyclerView rv_review_container;
    private PostAdapter adapter;
    private ListReview listReview;

    // Other component
    private EditText et_search_text;
    private ImageButton ib_write_review;

    // Firebase
    private String username = "";
    private int reviewlist = 0;
    private boolean state = false;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    private ProgressDialog progressDialog;
    private Handler handler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_list);

        //상단 툴바 설정
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayShowTitleEnabled(false); //xml에서 titleview 설정
        getSupportActionBar().setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //툴바 뒤로가기 생성
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_icon); //뒤로가기 버튼 모양 설정
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#3A7AFF"))); //툴바 배경색

        initAllComponent();

        Intent intent = getIntent();
        if (intent != null) {
            username = intent.getStringExtra("username");
        }

        recyclerviewSetting();

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                int _id = adapter.getItem(pos).get_id();

                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("PostId", Integer.toString(_id));
                // 수정, 삭제 시에 어떻게 할건지

                startActivity(intent);
            }
        });

        et_search_text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == event.KEYCODE_ENTER) {
                    return true;
                }
                return false;
            }
        });

        et_search_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String search = et_search_text.getText().toString();
                adapter.fillter(search);
            }
        });


        ib_write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent list_to_create = new Intent(getApplicationContext(), InPostActivity.class);
                    list_to_create.putExtra("username", username);
                    adapter.notifyDataSetChanged();

                    startActivity(list_to_create);

                } else {

                    Toast.makeText(getApplicationContext(), "후기 작성을 위해서는 로그인이 필요합니다!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

//    @Override
//    protected void onStart() {
//
//        super.onStart();
//
//    }
//
//    @Override
//    protected void onPause() {
//
//        super.onPause();
//
//    }
//
//    @Override
//    protected void onResume() {
//
//        super.onResume();
//
//    }
//
//    @Override
//    protected void onDestroy() {
//
//        super.onDestroy();
//
//    }

    public void initAllComponent() {

        firebaseDatabase = FirebaseDatabase.getInstance();

        rv_review_container = findViewById(R.id.rv_review_container);
        rv_review_container.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_review_container.setLayoutManager(linearLayoutManager);

        adapter = new PostAdapter();

        et_search_text = findViewById(R.id.et_search_text);
        ib_write_review = findViewById(R.id.ib_write_review);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void recyclerviewSetting() {

        firebaseDatabase.getReference("reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reviewlist = Integer.parseInt(Long.toString(snapshot.getChildrenCount()));
                for (final DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    firebaseDatabase.getReference("reviews/" + dataSnapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int id = Integer.parseInt(dataSnapshot.getKey());
                            String title = "";
                            String name = "";
                            int like = 0;

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                if (dataSnapshot.getKey().equals("title")) {
                                    title = dataSnapshot.getValue().toString();
                                } else if (dataSnapshot.getKey().equals("name")) {
                                    name = dataSnapshot.getValue().toString();
                                } else if (dataSnapshot.getKey().equals("like")) {
                                    like = Integer.parseInt(dataSnapshot.getValue().toString());
                                }

                            }

                            for (int i = 0; i < adapter.getItemCount(); i++) {
                                if (id == adapter.getItem(i).get_id()) {
                                    state = true;
                                }
                            }

                            if (state == false) {
                                listReview = new ListReview(id, title, name, like);
                                adapter.addItem(listReview);
                                adapter.notifyDataSetChanged();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.d(TAG, error.getMessage());
                        }

                    });


                    state = false;

                }

                rv_review_container.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (firebaseAuth.getCurrentUser() == null) {
            getMenuInflater().inflate(R.menu.toolbar_bl_menu, menu);
        } else if (firebaseAuth.getCurrentUser() != null) {
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
                Intent revlist_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revlist_to_login);
                return true;
            case R.id.menu_signup:
                Intent revlist_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revlist_to_signup);
                return true;
            case R.id.menu_logout:

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(PostListActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revlist = new Intent(getApplicationContext(), PostListActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revlist);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
