package com.mirea.kt.mskafisha.adapter;


import static com.mirea.kt.mskafisha.HTTPRequestRunnable.getQueryString;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mirea.kt.mskafisha.EventDetailActivity;
import com.mirea.kt.mskafisha.R;
import com.squareup.picasso.Picasso;

import java.text.BreakIterator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private final List<Map<String, String>> eventsList;
    private final Context context;

    public EventAdapter(List<Map<String, String>> eventsList, Context context) {
        this.eventsList = eventsList;
        this.context = context;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Map<String, String> event = eventsList.get(position);
        holder.titleTextView.setText(event.get("title"));
        holder.dateTextView.setText(event.get("date"));
        holder.placeTextView.setText(event.get("place"));
        holder.descriptionTextView.setText(event.get("description"));
        if (Objects.equals(event.get("age_restriction"), "0"))
            holder.ageRestrictionTextView.setText("Ограничений по возрасту нет!");
        else
            holder.ageRestrictionTextView.setText(event.get("age_restriction"));
        holder.priceTextView.setText(event.get("price"));
        Picasso.get()
                .load(event.get("image"))
                .placeholder(R.drawable.placeholder_image)
                .fit()
                .into(holder.imageView);

        if (!Objects.equals(event.get("place"), "Место неизвестно")){
            String placeUrl = "geo:" + event.get("place_geo")
                    + "?q="
                    + getQueryString("text",event.get("place"));
            holder.placeTextView.setOnClickListener(v -> {
                Log.d("Event " + position,"Maps redirect succeed");
                openUrl(placeUrl);
            });
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EventDetailActivity.class);
            intent.putExtra("event", (HashMap<String, String>) event);
            context.startActivity(intent);
        });
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(browserIntent);
    }

    @Override
    public int getItemCount() {
        return eventsList != null ? eventsList.size() : 0;
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dateTextView;
        TextView placeTextView;
        TextView descriptionTextView;
        TextView ageRestrictionTextView;
        TextView priceTextView;
        ImageView imageView;

        public EventViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            placeTextView = itemView.findViewById(R.id.placeTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            ageRestrictionTextView = itemView.findViewById(R.id.ageRestrictionTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            imageView = itemView.findViewById(R.id.imageView);
        }

    }
}