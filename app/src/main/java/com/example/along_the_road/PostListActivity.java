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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.along_the_road.adapters.PostAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PostListActivity extends AppCompatActivity {

    private final static String TAG = "PostListActivity";

    // Review list
    private RecyclerView rv_review_container;
    private PostAdapter adapter;
    private ListReview listReview;

    // Other component
    private ImageButton ib_write_review;

    // Firebase
    private String username = "";
    private int reviewlist = 0;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;


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

        adapter.setOnItemClickListener(new PostAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                int _id = adapter.getItem(pos).get_id();

                Intent intent = new Intent(getApplicationContext(), PostDetailActivity.class);
                intent.putExtra("_id", Integer.toString(_id));
                Log.d(TAG, "_id : " + _id);

                startActivity(intent);
            }
        });

        ib_write_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firebaseAuth.getCurrentUser() != null) {
                    Intent list_to_create = new Intent(getApplicationContext(), InPostActivity.class);
                    list_to_create.putExtra("username", username);

                    startActivity(list_to_create);

                } else {

                    Toast.makeText(getApplicationContext(), "후기 작성을 위해서는 로그인이 필요합니다!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onStart() {

        recyclerviewSetting();

        super.onStart();

    }

    @Override
    protected void onPause() {

        adapter.clearAllItem();
        adapter.notifyDataSetChanged();

        super.onPause();

    }

    @Override
    protected void onResume() {

        super.onResume();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

    }

    public void initAllComponent() {

        firebaseDatabase = FirebaseDatabase.getInstance();

        rv_review_container = findViewById(R.id.rv_review_container);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_review_container.setLayoutManager(linearLayoutManager);

        adapter = new PostAdapter();

        ib_write_review = findViewById(R.id.ib_write_review);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void recyclerviewSetting() {

        adapter.clearAllItem();

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

                            Log.d(TAG, "id : " + id + ", title : " + title);
                            listReview = new ListReview(id, title, name, like);
                            adapter.addItem(listReview);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Log.d(TAG, "review list : " + reviewlist);

        rv_review_container.setAdapter(adapter);

    }

//    public void showDatabase(String sort){
//        Cursor iCursor = mDBOpenHelper.sortColumn(sort);
//        Log.d("showDatabase", "DB Size: " + iCursor.getCount());
//        adapter.clearAllItem();
//        while(iCursor.moveToNext()){
//            String tempIndex = iCursor.getString(iCursor.getColumnIndex("_id"));
//            String tempTitle = iCursor.getString(iCursor.getColumnIndex("title"));
////            tempTitle = setTextLength(tempTitle, 10);
//            String tempUID = iCursor.getString(iCursor.getColumnIndex("userid"));
////            tempID = setTextLength(tempID,10);
//            String tempName = iCursor.getString(iCursor.getColumnIndex("name"));
////            tempName = setTextLength(tempName,10);
//            String tempContent = iCursor.getString(iCursor.getColumnIndex("content"));
////            tempContent = setTextLength(tempContent,50);
//            String tempLike = iCursor.getString(iCursor.getColumnIndex("like"));
//            Drawable tempImage = getResources().getDrawable(R.mipmap.ic_launcher);
//
//            int tempID = Integer.parseInt(tempIndex);
//
//            listReview = new ListReview(tempID, tempTitle, tempUID, tempName, Integer.parseInt(tempLike), tempContent, tempImage);
//            adapter.addItem(listReview);
//        }
//        adapter.notifyDataSetChanged();
//    }

//    public void showFirebase() {
//
//        Log.d(TAG, "showFirebase Start");
//
//        for (int i = 0; i < reviewlist; i++) {
//            final int reviewid = i;
//
//            firebaseDatabase.getReference("board/" + i).addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                    Log.d(TAG, "리뷰 보이긴 하니?");
//
//                    int id = reviewid;
//                    String title = "";
//                    String name = "";
//                    int like = 0;
//
//                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                        if (dataSnapshot.getKey().equals("title")) {
//                            title = dataSnapshot.getValue().toString();
//                        } else if (dataSnapshot.getKey().equals("name")) {
//                            name = dataSnapshot.getValue().toString();
//                        } else if (dataSnapshot.getKey().equals("like")) {
//                            like = Integer.parseInt(dataSnapshot.getValue().toString());
//                        }
//
//                        Log.d(TAG, "id : " + id + "title : " + title);
//                        listReview = new ListReview(id, title, name, like);
//                        adapter.addItem(listReview);
//
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error) {
//
//                }
//            });
//        }
//
//    }

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
