package com.example.myapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final String PEW = "pew";
    private static final String TS = "ts";

    private String pewdsURLString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&id=UC-lHJZR3Gqxm24_Vd_AJ5Yw&key=AIzaSyBU_oWEIULi3-n96vWKETYCMsldYDAlz2M";
    private String tseriesURLString = "https://www.googleapis.com/youtube/v3/channels?part=statistics&forUsername=tseries&key=AIzaSyBU_oWEIULi3-n96vWKETYCMsldYDAlz2M";

    private TextView pewdsTextView;
    private TextView tseriesTextView;
    private TextView diffCount;

    private ImageView viewPewd;
    private ImageView viewT;
    private RequestQueue mQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pewdsTextView = (TextView) findViewById(R.id.pewds_text);
        tseriesTextView = (TextView) findViewById(R.id.tseries_text);
        diffCount = (TextView) findViewById(R.id.text_sub_gap);
        viewPewd = (ImageView) findViewById(R.id.imageView);
        viewT = (ImageView) findViewById(R.id.imageView2);
        Button serveLasagnaButton = (Button) findViewById(R.id.button_lasagna);
        Button subscribeButton = (Button) findViewById(R.id.sub_button);
        Button play = (Button) findViewById(R.id.play_bich_lasagna);
        Button pause = (Button) findViewById(R.id.pause_bich_lasagna);

        mQueue = Volley.newRequestQueue(this);
        final MediaPlayer mp = MediaPlayer.create(super.getApplicationContext(), R.raw.sample);

        setImages();

        setMediaPlayerListeners(play, pause, mp);
        setOtherListeners(serveLasagnaButton, subscribeButton);

        getSubs();
    }

    private void setImages() {
        int imageResourcePew = getResources().getIdentifier("@drawable/unnamed", null, this.getPackageName());
        int imageResourceT = getResources().getIdentifier("@drawable/aaaaaaaaaaaaaaaa", null, this.getPackageName());
        viewPewd.setImageResource(imageResourcePew);
        viewT.setImageResource(imageResourceT);
    }

    private void setMediaPlayerListeners(Button play, Button pause, final MediaPlayer mp) {
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
    }

    private void setOtherListeners(Button serveLasagnaButton, Button subscribeButton) {
        subscribeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent viewIntent = new Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("http://www.youtube.com/channel/UC-lHJZR3Gqxm24_Vd_AJ5Yw?sub_confirmation=1")
                );
                startActivity(viewIntent);
            }
        });
        serveLasagnaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSubs();
            }
        });
    }


    private void getSubs() {
        final HashMap<String, String> subs = new HashMap<>();

        JsonFetchedCallback callback = new JsonFetchedCallback() {
            @Override
            public void jsonFetched(JSONObject json, String who) {
                subs.put(who, parseSubs(json));
                if (subs.size() > 1) {
                    String diff = countDiff(subs);
                    displayTexts(subs);
                    displayDiff(diff);
                }
            }
        };
        requestSubsJson(pewdsURLString, callback);
        requestSubsJson(tseriesURLString, callback);
    }

    private void requestSubsJson(final String url, final JsonFetchedCallback callback) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (url.contains(pewdsURLString))
                            callback.jsonFetched(response, PEW);
                        if (url.contains(tseriesURLString))
                            callback.jsonFetched(response, TS);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }

    private String countDiff(HashMap<String, String> subs) {
        String pewsString = subs.get(PEW);
        String tsString = subs.get(TS);

        if (pewsString != null && tsString != null) {
            return String.valueOf(Integer.parseInt(pewsString) - Integer.parseInt(tsString));
        }
        return "";
    }


    private String parseSubs(JSONObject json) {
        String subs = null;
        try {
            JSONArray jsonArray = json.getJSONArray("items");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                JSONObject stats = item.getJSONObject("statistics");
                subs = stats.getString("subscriberCount");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return subs;
    }

    private void displayTexts(HashMap<String, String> subs) {
        for (Map.Entry<String, String> subsKeyValue : subs.entrySet()) {
            if (subsKeyValue.getKey().equals(PEW))
                pewdsTextView.setText(subsKeyValue.getValue());
            if (subsKeyValue.getKey().equals(TS))
                tseriesTextView.setText(subsKeyValue.getValue());
        }
    }

    private void displayDiff(String diff) {
        diffCount.setText(diff);
    }


    interface JsonFetchedCallback {
        void jsonFetched(JSONObject json, String who);
    }
}
