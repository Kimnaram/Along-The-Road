package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.example.along_the_road.localselectActivity.Code;
import static com.example.along_the_road.localselectActivity.Detail_Code;

public class CourseRecoActivity extends AppCompatActivity {

    private final String API_KEY = "tnOnzel9rbOTFxR4l9A%2BlrSFVAL9K8Fqn8Idq%2FIlJF9JzF0FF0AEWCwZDzobCLk5dGS%2B2oLEU88qVJaOIRy8EQ%3D%3D";
    private String area_Course = null; // URL
    private String detail_Course = null;
    private String what_url = null;

    private int areaCode = 1; // 테스트를 위함
//    private int areaCode = Code;
    private int detailCode = Detail_Code;
    private String Theme = "C0113";

    private String[] Title;
    private String[] subname;
    private String[] subdetailimg;
    private int[] ContentID;

    private static final String Family_C = "C0112";
    private static final String Healing_C = "C0114";
    private static final String Walking_C = "C0115";
    private static final String Camping_C = "C0116";
    private static final String Taste_C = "C0117";

    private int list_len = 0;
    private int d_list_len = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_reco);

        final Button send_btn = findViewById(R.id.course_btn);

        send_btn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {

                String resultText = "값이 없음";

                String total_Theme = "cat2=" + Theme;

                if (areaCode != 0 && detailCode != 0) {
                    area_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                            "ServiceKey=" + API_KEY + "&numOfRows=5&pageNo=1" +
                            "&areaCode=" + areaCode + detailCode + "&contentTypeId=25&cat1=C01&" +
                            total_Theme + "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                } else if (areaCode != 0 && detailCode == 0) {
                    area_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?" +
                            "ServiceKey=" + API_KEY + "&numOfRows=5&pageNo=1" +
                            "&areaCode=" + areaCode + "&contentTypeId=25&cat1=C01&" +
                            total_Theme + "&MobileOS=ETC&MobileApp=AppTest&_type=json";
                } else {
                    // Spinner
                }
                // areaCode : 여기서 contentId를 파싱

                try {

                    what_url = area_Course;
                    resultText = new Task().execute().get(); // URL에 있는 내용을 받아옴

                    what_url = null;

                    JSONObject Object = new JSONObject(resultText);

                    String response = Object.getString("response");
                    JSONObject responseObject = new JSONObject(response);

                    String body = responseObject.getString("body");
                    JSONObject bodyObject = new JSONObject(body);

                    String items = bodyObject.getString("items");
                    JSONObject itemsObject = new JSONObject(items);

                    String item = itemsObject.getString("item");
                    JSONArray itemArray = new JSONArray(item);
                    list_len = itemArray.length();

                    ContentID = new int[list_len];
                    Title = new String[list_len];

                    for (int i = 0; i < list_len; i++) {

                        JSONObject CourseObject = itemArray.getJSONObject(i);

                        String contentId = CourseObject.getString("contentid");
                        ContentID[i] = Integer.parseInt(contentId);

                        Title[i] = CourseObject.getString("title");

                    }

                    for (int i = 0; i < list_len; i++) {

                        System.out.println(ContentID[i] + "의 " + Title[i]);
                    }


                } catch (NullPointerException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                for (int k = 0; k < ContentID.length; k++) {
                    detail_Course = "http://api.visitkorea.or.kr/openapi/service/rest/KorService/detailInfo?" +
                            "ServiceKey=" + API_KEY + "&contentId=" + ContentID[k] + "&contentTypeId=25" +
                            "&MobileOS=ETC&MobileApp=AppTest&_type=json";

                    System.out.println(detail_Course);
                    System.out.println(Title[k]);

                    String resultText2 = "값이 없음";

                    try {

                        what_url = detail_Course;
                        resultText2 = new Task().execute().get(); // URL에 있는 내용을 받아옴

                        what_url = null;

                        JSONObject Object = new JSONObject(resultText2);

                        String response = Object.getString("response");
                        JSONObject responseObject = new JSONObject(response);

                        String body = responseObject.getString("body");
                        JSONObject bodyObject = new JSONObject(body);

                        String items = bodyObject.getString("items");
                        JSONObject itemsObject = new JSONObject(items);

                        String item = itemsObject.getString("item");
                        JSONArray itemArray = new JSONArray(item);
                        d_list_len = itemArray.length();

                        subname = new String[d_list_len];
                        subdetailimg = new String[d_list_len];

                        for (int i = 0; i < d_list_len; i++) {

                            JSONObject CourseObject = itemArray.getJSONObject(i);

                            subname[i] = CourseObject.getString("subname");

                            //subdetailimg[i] = CourseObject.getString("subdetailimg");

                        }

                        for (int i = 0; i < d_list_len; i++) {

                            System.out.println(subname[i]);
                        }


                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(what_url);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                if (conn.getResponseCode() == conn.HTTP_OK) {
                    InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                    BufferedReader reader = new BufferedReader(tmp);
                    StringBuffer buffer = new StringBuffer();
                    while ((str = reader.readLine()) != null) {
                        buffer.append(str);
                    }
                    receiveMsg = buffer.toString();

                    reader.close();
                } else {
                    Log.i("통신 결과", conn.getResponseCode() + "에러");
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return receiveMsg;
        }
    }
}
