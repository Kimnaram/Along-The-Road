package com.example.along_the_road;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;

public class PostDetailActivity extends AppCompatActivity {

    private final static String TAG = "PostDetailActivity";

    // Other component
    private ImageView iv_review_image;
    private TextView tv_review_title;
    private TextView tv_review_user;
    private TextView tv_review_content;
    private Button btn_review_delete;
    private Button btn_review_update;
    private Button btn_review_like;

    private int PostId;
    private int PostLike;

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

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
            String TempID = intent.getStringExtra("PostId");
            PostId = Integer.parseInt(TempID);
            selectFirebase(PostId);
        }


        btn_review_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 수정 화면으로 이동
                Intent detail_to_update = new Intent(getApplicationContext(), PostUpdateActivity.class);
                detail_to_update.putExtra("PostId", Long.toString(PostId));

                startActivity(detail_to_update);
            }
        });

        btn_review_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 다이얼로그 바디
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(PostDetailActivity.this, R.style.AlertDialogStyle);
                // 메세지
                deleteBuilder.setTitle("삭제하시겠습니까?");

                deleteBuilder.setPositiveButton("예", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // 파이어베이스에서 삭제하는 코드 필요
                        firebaseDatabase.getReference("reviews").child(PostId + "").removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(getApplicationContext(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), "삭제에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        });

                        finish();

                    }
                });
                // "아니오" 버튼을 누르면 실행되는 리스너
                deleteBuilder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return; // 아무런 작업도 하지 않고 돌아간다
                    }
                });

                deleteBuilder.show();

            }
        });

        btn_review_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firebaseAuth.getCurrentUser() != null) {
                    PostLike += 1;
                    firebaseDatabase.getReference("reviews/" + PostId + "/like").setValue(PostLike);
                    // 좋아요 누르는거 어떻게 컨트롤할지가 필요!

                    Toast.makeText(getApplicationContext(), "해당 글을 추천하셨습니다!", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void initAllComponent() {

        iv_review_image = findViewById(R.id.iv_review_image);

        tv_review_title = findViewById(R.id.tv_review_title);
        tv_review_user = findViewById(R.id.tv_review_user);
        tv_review_content = findViewById(R.id.tv_review_content);

        btn_review_update = findViewById(R.id.btn_review_update);
        btn_review_delete = findViewById(R.id.btn_review_delete);
        btn_review_like = findViewById(R.id.btn_review_like);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

    }


    public void selectFirebase(int index) {

        final int id = index;

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseDatabase.getReference("reviews/" + index).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if(dataSnapshot.getKey().equals("title"))
                        tv_review_title.setText(dataSnapshot.getValue().toString());
                    else if(dataSnapshot.getKey().equals("name"))
                        tv_review_user.setText(dataSnapshot.getValue().toString());
                    else if(dataSnapshot.getKey().equals("content"))
                        tv_review_content.setText(dataSnapshot.getValue().toString());
                    else if(dataSnapshot.getKey().equals("image")) {
                        String image = dataSnapshot.getValue().toString();
                        byte[] b = binaryStringToByteArray(image);
                        Log.d(TAG, "b : " + b);
                        ByteArrayInputStream is = new ByteArrayInputStream(b);
                        Drawable reviewImage = Drawable.createFromStream(is, "reviewImage");
                        iv_review_image.setImageDrawable(reviewImage);
                        iv_review_image.setVisibility(View.VISIBLE);
                    } else if(dataSnapshot.getKey().equals("like")) {
                        String like = dataSnapshot.getValue().toString();
                        PostLike = Integer.parseInt(like);
                        btn_review_like.setText("추천 " + PostLike);
                    } else if(dataSnapshot.getKey().equals("uid")) {
                        if (firebaseAuth.getCurrentUser() != null) {
                            if (firebaseAuth.getCurrentUser().getUid().equals(dataSnapshot.getValue().toString())) {
                                btn_review_update.setVisibility(View.VISIBLE);
                                btn_review_delete.setVisibility(View.VISIBLE);
                                btn_review_like.setVisibility(View.GONE);
                            } else {
                                btn_review_update.setVisibility(View.GONE);
                                btn_review_delete.setVisibility(View.GONE);
                                btn_review_like.setVisibility(View.VISIBLE);
                            }
                        } else if (firebaseAuth.getCurrentUser() == null) {
                            btn_review_update.setVisibility(View.GONE);
                            btn_review_delete.setVisibility(View.GONE);
                            btn_review_like.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        switch (item.getItemId()){
            case android.R.id.home:{ //툴바 뒤로가기 동작
                finish();
                return true;
            }
            case R.id.menu_login :
                Intent revdetail_to_login = new Intent(getApplicationContext(), LoginActivity.class);

                startActivity(revdetail_to_login);
                return true;
            case R.id.menu_signup :
                Intent revdetail_to_signup = new Intent(getApplicationContext(), SignupActivity.class);

                startActivity(revdetail_to_signup);
                return true;
            case R.id.menu_logout :

                FirebaseAuth.getInstance().signOut();

                final ProgressDialog mDialog = new ProgressDialog(PostDetailActivity.this);
                mDialog.setMessage("로그아웃 중입니다.");
                mDialog.show();

                Intent logout_to_revdetail = new Intent(getApplicationContext(), PostDetailActivity.class);
                mDialog.dismiss();

                startActivity(logout_to_revdetail);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
