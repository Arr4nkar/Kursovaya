package com.mirea.kt.mskafisha;

import static com.mirea.kt.mskafisha.HTTPRequestRunnable.getQueryString;
import static com.mirea.kt.mskafisha.LoginActivity.showNote;

import static java.lang.String.valueOf;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mirea.kt.mskafisha.adapter.EventAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EventsActivity extends AppCompatActivity {

    private final String TAG = "EventsActivity";
    private EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        RecyclerView recyclerView = findViewById(R.id.rvEvents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(getEvents(), this);
        recyclerView.setAdapter(eventAdapter);
    }

    private List<Map<String, String>> getEvents() {
        HashMap<String, String> params = new HashMap<>();
        params.put("page", "1");
        params.put("page_size", "10");
        params.put("location", "msk");
        params.put("fields", "dates,site_url,place,description,body_text,age_restriction,price,images,title");
        params.put("text_format", "text");
        params.put("expand", "place");
        params.put("actual_since", valueOf(System.currentTimeMillis() / 1000L));
        params.put("actual_until", valueOf((System.currentTimeMillis() / 1000L) + 2629743));

        HTTPRequestRunnable httpRequestRunnable = new HTTPRequestRunnable(
                "GET",
                "https://kudago.com/public-api/v1.4/events/?" + getQueryString(params),
                null
        );

        Thread thread = new Thread(httpRequestRunnable);
        thread.start();
        List<Map<String, String>> parsedEventResponse = null;

        try {
            thread.join();
            Log.d(TAG, "getEvents request succeed");
            Log.d(TAG, "Response: " + httpRequestRunnable.getResponseBody());
        }
        catch (InterruptedException e) {
            Log.e(TAG, "http request failed");
            showNote(EventsActivity.this,"Ошибка подключения");
        }
        try {
            JSONObject jsonResponse = new JSONObject(httpRequestRunnable.getResponseBody());
            parsedEventResponse = parseEventResponse(jsonResponse.toString());
        } catch (JSONException e) {
            Log.e(TAG,"getEvents request failed", e);
        }
        return parsedEventResponse;
    }

    private static List<Map<String, String>> parseEventResponse(String response) {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, String>> eventsList = new ArrayList<>();

        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode resultsNode = rootNode.path("results");
            if (resultsNode.isArray()) {
                for (JsonNode node : resultsNode) {
                    Map<String, String> eventMap = new HashMap<>();
                    long currentTime = System.currentTimeMillis() / 1000;


                    eventMap.put("title", node.path("title").asText());
                    eventMap.put("full_description", node.path("body_text").asText());
                    eventMap.put("description", node.path("description").asText());
                    eventMap.put("age_restriction", node.path("age_restriction").asText());
                    eventMap.put("price", node.path("price").asText());
                    eventMap.put("site_url", node.path("site_url").asText());
                    if (!(node.get("place").path("title").asText().isEmpty())) {
                        eventMap.put("place", node.get("place").path("title").asText());
                        eventMap.put("place_geo",
                                node.get("place").get("coords").path("lat").asText()
                                + ","
                                + node.get("place").get("coords").path("lon").asText());
                    } else eventMap.put("place", "Место неизвестно");

                    if (node.has("images") && node.get("images").isArray() && !node.get("images").isEmpty()) {
                        eventMap.put("image", node.get("images").get(0).path("image").asText());
                    }
                    long lastDate = node.get("dates").get(node.get("dates").size()-1).path("end").asLong();
                    if (!node.get("dates").isEmpty() && lastDate > currentTime) {
                        Date date = new Date(lastDate * 1000L);
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, HH:mm", new Locale("ru"));
                        eventMap.put("date", sdf.format(date));
                    } else eventMap.put("date", "Дата неизвестна");
                    eventsList.add(eventMap);
                }
            }
        } catch (IOException e) {
            Log.e("parseEventRespone", "parseEventRespone method failed:", e);
        }
        return eventsList;
    }
}
