package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Map;

public class MapDetailActivity extends AppCompatActivity {

    private static final String EXTRA_MAP = "extra_map";

    public static void start(Context context, Map map) {
        Intent intent = new Intent(context, MapDetailActivity.class);
        intent.putExtra(EXTRA_MAP, map.url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);

        //TODO: get url only
        String mapUrl = getIntent().getStringExtra(EXTRA_MAP);

        TouchNetworkImageView imageView = findViewById(R.id.imageView);

        if (mapUrl != null) {
            Glide.with(this).load(mapUrl).placeholder(R.drawable.abma_logo).into(imageView);
        }
        imageView.setMaxZoom(4f);
    }
}
