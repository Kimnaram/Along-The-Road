package com.example.along_the_road;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class TrafficSearchActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker marker;

    /****************************** Directions API 관련 변수 *******************************/
    private static final String API_KEY="";
    private LinearLayout container;
    private String str_url = null;
    private String option = "";
    private String travel_mode = "transit";
    private String step = null;
    private String entire_step = null;
    private String departure_lat;
    private String departure_lng;
    private String[][] going_lat;
    private String[][] going_lng;
    private String arrival_lat;
    private String arrival_lng;

    private int r_list_len = 0;
    private int[] list_len;

    private int text_count = 0;
    private int count = 0;
    private int num = 0;

    // Drawable bus_img = getResources().getDrawable(R.drawable.bus);
    // Drawable walk_img = container.getContext().getResources().getDrawable(R.drawable.walking);
    // Drawable subway_img = getApplicationContext().getResources().getDrawable(R.drawable.subway);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_traffic_search);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {

        switch (item.getItemId()) {
            /* case R.id.walking:
                travel_mode = "walking";
                break;
            */
            case R.id.transit:
                travel_mode = "transit";
                break;
            /*
            case R.id.driving:
                travel_mode = "driving";
                break;
            */
            case R.id.less_walk:
                option = "&transit_routing_preference=less_walking";
                break;
            case R.id.fewer_transfers:
                option = "&transit_routing_preference=fewer_transfers";
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendClick(View view) {

        mMap.clear();

        container = findViewById(R.id.container);
        EditText dep_loc = findViewById(R.id.depart_loc);
        EditText arr_loc = findViewById(R.id.arrive_loc);

        String depart = dep_loc.getText().toString();
        String arrival = arr_loc.getText().toString();

        directions(depart, arrival);

    }

    public void directions(String depart, String arrival) {

        if(travel_mode.equals("transit")) {
            str_url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + depart + "&destination=" + arrival + "&mode=" + travel_mode + "&departure_time=now" +
                    "&alternatives=true" + option + "&language=Korean&key=" + API_KEY;
        } else {
            str_url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + depart + "&destination=" + arrival + "&mode=" + travel_mode + "&departure_time=now" +
                    "&alternatives=true" + "&language=Korean&key=" + API_KEY;
        }

        String resultText = "값이 없음";

        try {

            if(text_count > 0) {
                container.removeViewsInLayout(0, text_count);
                text_count = 0;
            } // 재검색 시, 이전의 TextView 삭제

            resultText = new Task().execute().get();

            JSONObject jsonObject = new JSONObject(resultText);
            String routes = jsonObject.getString("routes");
            JSONArray routesArray = new JSONArray(routes);


            for(int j = 0; j <= routesArray.length(); j++) {

                r_list_len = routesArray.length();

                entire_step = null;

                JSONObject subJsonObject = routesArray.getJSONObject(j);

                String legs = subJsonObject.getString("legs");
                JSONArray LegArray = new JSONArray(legs);
                JSONObject legJsonObject = LegArray.getJSONObject(0);

                String leg_duration = legJsonObject.getString("duration");
                JSONObject legdurObject = new JSONObject(leg_duration);
                String amountDuration = legdurObject.getString("text");

                String steps = legJsonObject.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);

                list_len = new int[r_list_len];
                list_len[j] = stepsArray.length();

                String[] getInstructions = new String[list_len[j]];
                String[] getDistance = new String[list_len[j]];
                String[] getDuration = new String[list_len[j]];
                String[] getEnd_lat = new String[list_len[j]];
                String[] getEnd_lng = new String[list_len[j]];
                String[] getStart_lat = new String[list_len[j]];
                String[] getStart_lng = new String[list_len[j]];
                String[] arrival_name = new String[list_len[j]];
                String[] depart_name = new String[list_len[j]];
                String[] getHeadsign = new String[list_len[j]];
                String[] getTransit = new String[list_len[j]];
                going_lat = new String[r_list_len][list_len[j]];
                going_lng = new String[r_list_len][list_len[j]];

                for (int i = 0; i < list_len[j]; i++) {
                    JSONObject stepsObject = stepsArray.getJSONObject(i);
                    getInstructions[i] = stepsObject.getString("html_instructions");
                    String[] Check = getInstructions[i].split(" ");
                    String TransitCheck = Check[0];

                    String end_location = stepsObject.getString("end_location");
                    JSONObject endJsonObject = new JSONObject(end_location);
                    getEnd_lat[i] = endJsonObject.getString("lat");
                    getEnd_lng[i] = endJsonObject.getString("lng");
                    if(i < (list_len[j] - 1)) {
                        going_lat[j][i] = getEnd_lat[i];
                        going_lng[j][i] = getEnd_lng[i];
                        System.out.println("진행 : " + going_lat[j][i] + ", " + going_lng[j][i]);
                    } else {
                        arrival_lat = getEnd_lat[i];
                        arrival_lng = getEnd_lng[i];
                    }

                    String start_location = stepsObject.getString("start_location");
                    JSONObject startJsonObject = new JSONObject(start_location);
                    getStart_lat[i] = startJsonObject.getString("lat");
                    getStart_lng[i] = startJsonObject.getString("lng");
                    if(i == 0) {
                        departure_lat = getStart_lat[i];
                        departure_lng = getStart_lng[i];
                    } else {
                        going_lat[j][i] = getStart_lat[i];
                        going_lng[j][i] = getStart_lng[i];
                    }

                    String duration = stepsObject.getString("duration");
                    JSONObject durJsonObject = new JSONObject(duration);
                    String tempDuration = durJsonObject.getString("text");
                    getDuration[i] = tempDuration.split(" ")[0];

                    String distance = stepsObject.getString("distance");
                    JSONObject disJsonObject = new JSONObject(distance);
                    getDistance[i] = disJsonObject.getString("text");

                    if (TransitCheck.equals("Bus") || TransitCheck.equals("Subway")
                            || TransitCheck.equals("train") || TransitCheck.equals("rail")) {

                        System.out.println(TransitCheck);

                        String transit_details = stepsObject.getString("transit_details");
                        JSONObject transitObject = new JSONObject(transit_details);

                        String arrival_stop = transitObject.getString("arrival_stop");
                        JSONObject arrivalObject = new JSONObject(arrival_stop);
                        arrival_name[i] = arrivalObject.getString("name");

                        String depart_stop = transitObject.getString("departure_stop");
                        JSONObject departObject = new JSONObject(depart_stop);
                        depart_name[i] = departObject.getString("name");

                        getHeadsign[i] = transitObject.getString("headsign");

                        String line = transitObject.getString("line");
                        JSONObject lineObject = new JSONObject(line);
                        getTransit[i] = lineObject.getString("short_name");

                    }

                    if (!TransitCheck.equals("Bus") && !TransitCheck.equals("Subway")
                            && !TransitCheck.equals("train") && !TransitCheck.equals("rail")) {

                        step = "도보 " + getDuration[i] + "분\n";
                        System.out.println(getInstructions[i] + "\n");

                    } else if (TransitCheck.equals("Bus") || TransitCheck.equals("Subway")
                            || TransitCheck.equals("train") || TransitCheck.equals("rail")) {
                        step = getTransit[i] + "\n";
                        System.out.println(getInstructions[i] + "\n");
                    }

                    if (entire_step == null) {
                        entire_step = step;
                        step = null;
                    } else {
                        entire_step += step;
                        step = null;
                    }
                }

                if(count == 0) {
                    recommend_view("추천 경로");
                } else if(count == 1) {
                    recommend_view("다른 경로 더 보기");
                } else if(count > 1) {
                    recommend_view(" ");
                }
                method_view(amountDuration);
                method_view(entire_step);
                count += 1;
                text_count += 3;
                // 동적 TextView 추가
            }

        } catch(InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void method_view(String a) {
        TextView method = new TextView(this);

        ++num;

        method.setId(num);
        System.out.println("텍스트뷰 : " + num + "\n");
        method.setText(a);
        method.setTextSize(15);
        method.setTextColor(Color.GRAY);
        // method.setCompoundDrawables(bus_img, null, null, null);

        method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();

                double dlatitude = Double.parseDouble(departure_lat);
                double dlngtitude = Double.parseDouble(departure_lng);

                LatLng Start = new LatLng(dlatitude, dlngtitude);

                marker = mMap.addMarker(new MarkerOptions().position(Start).title("출발"));

                onMapReady(mMap);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(Start));

                for (int j = 0; j < r_list_len; j++) {

                    for (int i = 0; i < list_len[j]; i++) {

                        double glatitude = Double.parseDouble(going_lat[j][i]);
                        double glngtitude = Double.parseDouble(going_lng[j][i]);
                        LatLng End = new LatLng(glatitude, glngtitude);

                        marker = mMap.addMarker(new MarkerOptions().position(End).title("진행 : " + glatitude + "," + glngtitude));

                        onMapReady(mMap);

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(End));
                    }
                }

                double alatitude = Double.parseDouble(arrival_lat);
                double alngtitude = Double.parseDouble(arrival_lng);
                LatLng End = new LatLng(alatitude, alngtitude);

                marker = mMap.addMarker(new MarkerOptions().position(End).title("도착"));

                onMapReady(mMap);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(End));

            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        method.setLayoutParams(lp);

        container.addView(method);
    }

    public void recommend_view(String a) {
        TextView recommend = new TextView(this);
        recommend.setText(a);
        recommend.setTextSize(13);
        recommend.setTextColor(Color.rgb(102, 102, 102));

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        recommend.setLayoutParams(lp);

        container.addView(recommend);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng SEOUL = new LatLng(37.56, 126.97);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12));

    }

    public class Task extends AsyncTask<String, Void, String> {

        private String str, receiveMsg;

        @Override
        protected String doInBackground(String... params) {
            URL url = null;
            try {
                url = new URL(str_url);

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