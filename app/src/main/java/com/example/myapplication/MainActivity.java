package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private String pewdsURLString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC-lHJZR3Gqxm24_Vd_AJ5Yw&key=AIzaSyBU_oWEIULi3-n96vWKETYCMsldYDAlz2M";
    private String tseriesURLString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&forUsername=tseries&key=AIzaSyBU_oWEIULi3-n96vWKETYCMsldYDAlz2M";
    private TextView pewdsTextView;
    private TextView tseriesTextView;
    private RequestQueue mQueue;
    private ImageView viewPewd;
    private ImageView viewT;
    private TextView diffCount;
    private int pewdsSub;
    private int tSub;
    private int difference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pewdsTextView = (TextView) findViewById(R.id.pewds_text);
        tseriesTextView = (TextView) findViewById(R.id.tseries_text);
        final Button serveLasagnaButton = (Button) findViewById(R.id.button_lasagna);
        viewPewd = (ImageView) findViewById(R.id.imageView);
        viewT = (ImageView) findViewById(R.id.imageView2);
        diffCount = (TextView) findViewById(R.id.text_sub_gap);
        Button subscribeButton = (Button) findViewById(R.id.sub_button);
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent viewIntent =
                        new Intent("android.intent.action.VIEW",
                                Uri.parse("http://www.youtube.com/channel/UC-lHJZR3Gqxm24_Vd_AJ5Yw?sub_confirmation=1"));
                startActivity(viewIntent);
            }
        });
        Button play = (Button) findViewById(R.id.play_bich_lasagna);
        Button pause = (Button) findViewById(R.id.pause_bich_lasagna);
        final MediaPlayer mp = MediaPlayer.create(super.getApplicationContext(), R.raw.sample);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.start();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.pause();
            }
        });
        int imageResourcePew = getResources().getIdentifier("@drawable/unnamed",
                null, this.getPackageName());
        int imageResourceT = getResources().getIdentifier("@drawable/aaaaaaaaaaaaaaaa",
                null, this.getPackageName());
        viewPewd.setImageResource(imageResourcePew);
        viewT.setImageResource(imageResourceT);
        mQueue = Volley.newRequestQueue(this);
        jsonParsePewds(pewdsURLString, pewdsTextView);
        jsonParsePewds(tseriesURLString, tseriesTextView);
        serveLasagnaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pewdsTextView.setText("");
                tseriesTextView.setText("");
                jsonParsePewds(pewdsURLString, pewdsTextView);
                jsonParsePewds(tseriesURLString, tseriesTextView);
                difference = pewdsSub-tSub;
//              difference=-2;
                if (difference!=0) {
                    diffCount.setText("Sub Gap: \n" + String.valueOf(difference));
                }
                if (difference<0) {
                    serveLasagnaButton.setText("OOPSIE! Press F");
                }
            }
        });
    }

    protected void jsonParsePewds(final String url, final TextView view) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("items");
                            for (int i=0; i<jsonArray.length(); i++) {
                                JSONObject item = jsonArray.getJSONObject(i);
                                String stats = item.getString("statistics");
                                String subs = stats.substring(stats.indexOf("subscriberCount")+18,
                                        stats.indexOf(",", stats.indexOf("subscriberCount"))-1);
                                if (url.contains(pewdsURLString)) {
                                    pewdsSub = Integer.valueOf(subs);
                                }
                                if (url.contains(tseriesURLString)) {
                                    tSub = Integer.valueOf(subs);
                                }
                                view.append(subs+"\n\n");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }
//    protected void jsonParseTseries(String url, final TextView view) {
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            JSONArray jsonArray = response.getJSONArray("items");
//                            for (int i=0; i<jsonArray.length(); i++) {
//                                JSONObject item = jsonArray.getJSONObject(i);
//                                String stats = item.getString("statistics");
//                                String subs = stats.substring(stats.indexOf("subscriberCount")+18,
//                                        stats.indexOf(",", stats.indexOf("subscriberCount"))-1);
//                                tSub = Integer.valueOf(subs);
//                                view.append(subs+"\n\n");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                error.printStackTrace();
//            }
//        });
//
//        mQueue.add(request);
//    }
}
