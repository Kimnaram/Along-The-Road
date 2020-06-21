package com.example.along_the_road;


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;


public class InPostActivity extends AppCompatActivity implements View.OnClickListener {
    //사용자 우리는 안 가져옴 일단.. 로그인 회원가입 추후 토론
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore mStore = FirebaseFirestore.getInstance();
    private EditText mTitle,mContents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

//        mTitle = findViewById(R.id.post_title_edit);
//        mContents= findViewById(R.id.post_contents_edit);
//        findViewById(R.id.post_save_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(mAuth.getCurrentUser() !=null){
            String postId = mStore.collection(FirebaseID.post).document().getId();
            Map<String,Object> data = new HashMap<>();
            data.put(FirebaseID.documentId,mAuth.getCurrentUser().getUid());
            data.put(FirebaseID.title,mTitle.getText().toString());
            data.put(FirebaseID.contents,mContents.getText().toString());
            mStore.collection(FirebaseID.post).document(postId).set(data, SetOptions.merge());
            finish();

        }


    }
}
