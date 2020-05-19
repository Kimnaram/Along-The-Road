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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class TrafficSearchActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private GoogleMap mMap; // 구글 지도
    private Marker start_m; // 시작 마커
    private Marker end_m; // 도착 마커
    private Marker[][] marker_arr; // 중간 마커 배열
    private LatLng End_location; // 도착 위치 표시

    /****************************** Directions API 관련 변수 *******************************/
    private static final String API_KEY = "";
    private LinearLayout container;
    private String str_url = null; // URL
    private String option = null;
    private String travel_mode = "transit";
    private String step = null;
    private String entire_step = null;
    private String departure_lat = null;
    private String departure_lng = null;
    private String[][] goingS_lat;
    private String[][] goingS_lng;
    private String[][] goingE_lat;
    private String[][] goingE_lng;
    private String arrival_lat = null;
    private String arrival_lng = null;
    private String[][] TransitName;
    private String[][] getPolyline;
    private String getOverview = null;

    private int r_list_len = 0;
    private int[] list_len = null;

    private int text_count = 0;
    private int count = 0;
    private int num = 0;

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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.less_walk:
                option = "&transit_routing_preference=less_walking";
                break;
            case R.id.fewer_transfers:
                option = "&transit_routing_preference=fewer_transfers";
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void sendClick(View view) { // 검색 버튼을 클릭하면

        mMap.clear(); // 맵을 clear
        num = 0; // 텍스트뷰 id를 0으로 초기화

        container = findViewById(R.id.container);
        EditText dep_loc = findViewById(R.id.depart_loc);
        EditText arr_loc = findViewById(R.id.arrive_loc);

        String depart = dep_loc.getText().toString();
        String arrival = arr_loc.getText().toString();

        directions(depart, arrival);

        if (getOverview != null) {
            ArrayList<LatLng> entire_path = decodePolyPoints(getOverview);

            for (int i = 0; i < entire_path.size(); i++) {
                if (i == 0) {
                    mMap.addMarker(new MarkerOptions().position(entire_path.get(i)).title("출발"));
                } else if (i >= entire_path.size() - 1) {
                    mMap.addMarker(new MarkerOptions().position(entire_path.get(i)).title("도착"));
                    End_location = entire_path.get(i);
                }
            }

            Polyline line = null;

            if (line == null) {
                line = mMap.addPolyline(new PolylineOptions()
                        .color(Color.rgb(0, 153, 255))
                        .geodesic(true)
                        .addAll(entire_path));
            } else {
                line.remove();
                line = mMap.addPolyline(new PolylineOptions()
                        .color(Color.rgb(0, 153, 255))
                        .geodesic(true)
                        .addAll(entire_path));
            }
        }

        onMapReady(mMap);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(End_location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));

    }

    public void directions(String depart, String arrival) {

        str_url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + depart + "&destination=" + arrival + "&mode=transit" + "&departure_time=now" +
                "&alternatives=true&language=Korean&key=" + API_KEY;

        System.out.println(str_url);

        String resultText = "값이 없음";

        try {

            if (text_count > 0) {
                container.removeViewsInLayout(0, text_count);
                text_count = 0;
            } // 재검색 시, 이전의 TextView 삭제

            resultText = new Task().execute().get(); // URL에 있는 내용을 받아옴

            JSONObject jsonObject = new JSONObject(resultText);
            String routes = jsonObject.getString("routes");
            JSONArray routesArray = new JSONArray(routes);

            r_list_len = routesArray.length();

            System.out.println("r_list_len : " + r_list_len);

            list_len = new int[r_list_len]; // route의 개수만큼 배열 동적 생성

            JSONObject preferredObject = routesArray.getJSONObject(0);
            String singleRoute = preferredObject.getString("overview_polyline");
            JSONObject pointsObject = new JSONObject(singleRoute);
            String points = pointsObject.getString("points");
            getOverview = points;

            for (int j = 0; j < routesArray.length(); j++) { // 배열들 생성 및 초기화

                JSONObject subJsonObject = routesArray.getJSONObject(j);

                String legs = subJsonObject.getString("legs");
                JSONArray LegArray = new JSONArray(legs);
                JSONObject legJsonObject = LegArray.getJSONObject(0);

                String steps = legJsonObject.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);

                list_len[j] = stepsArray.length(); // j번째 route에 step이 몇개인지 저장

                goingS_lat = new String[r_list_len][list_len[j]]; // route의 개수만큼 그리고 j번째 route의 step 수만큼 배열 동적 생성
                goingS_lng = new String[r_list_len][list_len[j]];
                goingE_lat = new String[r_list_len][list_len[j]];
                goingE_lng = new String[r_list_len][list_len[j]];
                getPolyline = new String[r_list_len][list_len[j]];
                TransitName = new String[r_list_len][list_len[j]];
                marker_arr = new Marker[2][list_len[j]];

                for(int i = 0; i < list_len[j]; i++) {
                    goingS_lat[j][i] = null;
                    goingS_lng[j][i] = null;
                    goingE_lat[j][i] = null;
                    goingE_lng[j][i] = null;
                    getPolyline[j][i] = null;
                    TransitName[j][i] = null;
                    marker_arr[0][i] = null;
                    marker_arr[1][i] = null;
                }

            }

            for (int j = 0; j < r_list_len; j++) {

                entire_step = null; // 경로를 저장하는 변수 초기화

                JSONObject subJsonObject = routesArray.getJSONObject(j);

                String legs = subJsonObject.getString("legs");
                JSONArray LegArray = new JSONArray(legs);
                JSONObject legJsonObject = LegArray.getJSONObject(0);

                String leg_duration = legJsonObject.getString("duration");
                JSONObject legdurObject = new JSONObject(leg_duration);
                String amountDuration = legdurObject.getString("text");

                String steps = legJsonObject.getString("steps");
                JSONArray stepsArray = new JSONArray(steps);

                String[] getTravelMode = new String[list_len[j]]; // j번째 route의 step 수만큼 배열 동적 생성
                //String[] getDistance = new String[list_len[j]];
                String[] getDuration = new String[list_len[j]];
                String[] arrival_name = new String[list_len[j]];
                String[] depart_name = new String[list_len[j]];
                //String[] getHeadsign = new String[list_len[j]];
                String[] getTransit = new String[list_len[j]];

                for (int i = 0; i < list_len[j]; i++) {

                    JSONObject stepsObject = stepsArray.getJSONObject(i);
                    getTravelMode[i] = stepsObject.getString("travel_mode");

                    String end_location = stepsObject.getString("end_location");
                    JSONObject endJsonObject = new JSONObject(end_location);
                    if (i >= list_len[j] - 1) {
                        arrival_lat = endJsonObject.getString("lat");
                        arrival_lng = endJsonObject.getString("lng");
                    } else {
                        goingE_lat[j][i] = endJsonObject.getString("lat");
                        goingE_lng[j][i] = endJsonObject.getString("lng");
                    }

                    String start_location = stepsObject.getString("start_location");
                    JSONObject startJsonObject = new JSONObject(start_location);
                    if (i == 0) {
                        departure_lat = startJsonObject.getString("lat");
                        departure_lng = startJsonObject.getString("lng");
                    } else {
                        goingS_lat[j][i] = startJsonObject.getString("lat");
                        goingS_lng[j][i] = startJsonObject.getString("lng");
                    }

                    String polyline = stepsObject.getString("polyline");
                    JSONObject polyJsonObject = new JSONObject(polyline);
                    getPolyline[j][i] = polyJsonObject.getString("points"); // 인코딩 된 포인트를 얻어옴

                    String duration = stepsObject.getString("duration");
                    JSONObject durJsonObject = new JSONObject(duration);
                    String tempDuration = durJsonObject.getString("text");
                    getDuration[i] = tempDuration.split(" ")[0];

                    //String distance = stepsObject.getString("distance");
                    //JSONObject disJsonObject = new JSONObject(distance);
                    //getDistance[i] = disJsonObject.getString("text");

                    if (getTravelMode[i].equals("TRANSIT")) {

                        String transit_details = stepsObject.getString("transit_details");
                        JSONObject transitObject = new JSONObject(transit_details);

                        String arrival_stop = transitObject.getString("arrival_stop");
                        JSONObject arrivalObject = new JSONObject(arrival_stop);
                        arrival_name[i] = arrivalObject.getString("name");

                        String depart_stop = transitObject.getString("departure_stop");
                        JSONObject departObject = new JSONObject(depart_stop);
                        depart_name[i] = departObject.getString("name");
                        // getHeadsign[i] = transitObject.getString("headsign");

                        String line = transitObject.getString("line");
                        JSONObject lineObject = new JSONObject(line);
                        getTransit[i] = lineObject.getString("short_name");
                        TransitName[j][i] = getTransit[i];

                    }

                    if (i < list_len[j] - 1) {
                        if (getTravelMode[i].equals("WALKING")) {
                            step = "도보 " + getDuration[i] + "분 > ";

                        } else if (getTravelMode[i].equals("TRANSIT")) {
                            step = getTransit[i] + " > ";
                        }
                    } else {
                        if (getTravelMode[i].equals("WALKING")) {
                            step = "도보 " + getDuration[i] + "분\n";

                        } else if (getTravelMode[i].equals("TRANSIT")) {
                            step = getTransit[i] + "\n";
                        }
                    }

                    if (entire_step == null) {
                        entire_step = step;
                        step = null;
                    } else {
                        entire_step += step;
                        step = null;
                    }
                }

                if (count == 0) {
                    recommend_view("추천 경로");
                    text_count += 2;
                } else if (count == 1) {
                    recommend_view("다른 경로 더 보기");
                    text_count += 2;
                } else if (count > 1) {
                    text_count += 1;
                }
                method_view(amountDuration + "\n" + entire_step);
                count += 1;
                // 동적 TextView 추가
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        count = 0;

    }

    public static ArrayList<LatLng> decodePolyPoints(String encodedPath) {
        int len = encodedPath.length();

        final ArrayList<LatLng> path = new ArrayList<LatLng>();
        int index = 0;
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int result = 1;
            int shift = 0;
            int b;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lat += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            result = 1;
            shift = 0;
            do {
                b = encodedPath.charAt(index++) - 63 - 1;
                result += b << shift;
                shift += 5;
            } while (b >= 0x1f);
            lng += (result & 1) != 0 ? ~(result >> 1) : (result >> 1);

            path.add(new LatLng(lat * 1e-5, lng * 1e-5));
        }

        return path;
    }

    public void method_view(String a) {
        final TextView method = new TextView(this);

        method.setId(num);
        method.setText(a);
        method.setTextSize(15);
        method.setTextColor(Color.GRAY);
        //method.setCompoundDrawables(getResources().getDrawable(R.drawable.subway), null, null, null);
        // 이미지 추가 고민...

        final int t_num = method.getId();

        if (num < r_list_len - 1) {
            num += 1;
        }

        method.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mMap.clear();
                int j = t_num;

                double dlatitude = Double.parseDouble(departure_lat);
                double dlngtitude = Double.parseDouble(departure_lng);

                LatLng Start = new LatLng(dlatitude, dlngtitude);

                start_m = mMap.addMarker(new MarkerOptions().position(Start).title("출발"));

                for (int i = 0; i < list_len[j]; i++) {

                    ArrayList<LatLng> path_points = decodePolyPoints(getPolyline[j][i]); // 폴리라인 포인트 디코드 후 ArrayList에 저장
                    System.out.println("polyline["+j+"]["+i+"] : " + getPolyline[j][i]);

                    Polyline line = null;

                    if (line == null) {
                        line = mMap.addPolyline(new PolylineOptions()
                                .color(Color.rgb(0, 153, 255))
                                .geodesic(true)
                                .addAll(path_points));
                    } else {
                        line.remove();
                        line = mMap.addPolyline(new PolylineOptions()
                                .color(Color.rgb(0, 153, 255))
                                .geodesic(true)
                                .addAll(path_points));
                    }

                    if (goingE_lat[j][i] != null) {

                        double gelatitude = Double.parseDouble(goingE_lat[j][i]);
                        double gelngtitude = Double.parseDouble(goingE_lng[j][i]);
                        String Transit_n = TransitName[j][i];
                        String next_Transit_n = null;

                        if (i + 1 < list_len[j]) {
                            if (TransitName[j][i + 1] != null)
                                next_Transit_n = TransitName[j][i + 1];
                        }

                        LatLng GoingE = new LatLng(gelatitude, gelngtitude);

                        if (Transit_n == null) {
                            Transit_n = "도보";
                            if (next_Transit_n != null) {
                                marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 후, " + next_Transit_n + " 승차"));
                            } else {
                                if (i == list_len[j] - 1) {
                                    marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 후, 도착"));
                                }
                                marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n));
                            }
                        } else {
                            if (i == list_len[j] - 1) {
                                marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 하차 후, 도착"));
                            }
                            marker_arr[1][i] = mMap.addMarker(new MarkerOptions().position(GoingE).title(Transit_n + " 하차"));
                        }

                        onMapReady(mMap);
                    }

                    if (goingS_lat[j][i] != null) {

                        double gslatitude = Double.parseDouble(goingS_lat[j][i]);
                        double gslngtitude = Double.parseDouble(goingS_lng[j][i]);
                        String Transit_n = TransitName[j][i];
                        String prev_Transit_n = null;
                        if (i != 0) {
                            prev_Transit_n = TransitName[j][i - 1];
                        }

                        LatLng GoingS = new LatLng(gslatitude, gslngtitude);

                        if (Transit_n == null) {
                            Transit_n = "도보";
                            // 도보 전 지하철 하차, 버스 하차 등을 표시할 수 있도록
                            if (prev_Transit_n != null) {
                                marker_arr[0][i] = mMap.addMarker(new MarkerOptions().position(GoingS).title(prev_Transit_n + "하차 후, " + Transit_n));
                            } else {
                                marker_arr[0][i] = mMap.addMarker(new MarkerOptions().position(GoingS).title(Transit_n));
                            }
                        } else {
                            marker_arr[0][i] = mMap.addMarker(new MarkerOptions().position(GoingS).title(Transit_n + " 승차"));
                        }

                        onMapReady(mMap);
                    }
                }

                double alatitude = Double.parseDouble(arrival_lat);
                double alngtitude = Double.parseDouble(arrival_lng);

                LatLng End = new LatLng(alatitude, alngtitude);

                end_m = mMap.addMarker(new MarkerOptions().position(End).title("도착"));

                onMapReady(mMap);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(End));
                //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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
        recommend.setTextColor(Color.rgb(133, 133, 133));

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
        mMap.animateCamera(CameraUpdateFactory.zoomTo(10));
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