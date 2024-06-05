package com.mirea.kt.mskafisha;

import static com.mirea.kt.mskafisha.HTTPRequestRunnable.getQueryString;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.util.Map;
import java.util.Objects;

public class EventDetailActivity extends AppCompatActivity {

    private final String TAG = "EventDetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);

        ImageView ivDetailImage = findViewById(R.id.ivDetailImage);
        TextView tvDetailTitle = findViewById(R.id.tvDetailTitle);
        TextView tvDetailPlace = findViewById(R.id.tvDetailPlace);
        TextView tvDetailAgeRestriction = findViewById(R.id.tvDetailAgeRestriction);
        TextView tvDetailDescription = findViewById(R.id.tvDetailDescription);
        TextView tvDetailPrice = findViewById(R.id.tvDetailPrice);
        TextView tvDetailWebsite = findViewById(R.id.tvDetailWebsite);
        Button btnPlaceGeo = findViewById(R.id.btnPlaceGeo);

        Intent intent = getIntent();
        Map<String, String> event = (Map<String, String>) intent.getSerializableExtra("event");

        if (event != null) {
            tvDetailTitle.setText(event.get("title"));
            tvDetailPlace.setText(event.get("place"));
            if (Objects.equals(event.get("age_restriction"), "0")) tvDetailAgeRestriction.setText("Ограничений по возрасту нет!");
            else tvDetailAgeRestriction.setText(event.get("age_restriction"));
            if (Objects.equals(event.get("price"), "0")) tvDetailPrice.setText("Цена неизвестна");
            else tvDetailPrice.setText(event.get("price"));
            tvDetailWebsite.setText(event.get("site_url"));
            tvDetailDescription.setText(event.get("full_description"));

            Picasso.get()
                    .load(event.get("image"))
                    .placeholder(R.drawable.placeholder_image)
                    .fit()
                    .into(ivDetailImage);

            btnPlaceGeo.setOnClickListener(v -> {
                String placeUrl = "geo:" + event.get("place_geo")
                        + "?q="
                        + getQueryString("text",event.get("place"));
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(placeUrl));
                startActivity(browserIntent);
                Log.d(TAG, "onCreate: go-to-map Button Pressed");
            });
        }
    }
}
