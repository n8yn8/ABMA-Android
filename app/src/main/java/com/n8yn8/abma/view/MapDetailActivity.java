package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.n8yn8.abma.App;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.entities.Map;

public class MapDetailActivity extends AppCompatActivity {

    private static final String EXTRA_MAP = "extra_map";

    public static void start(Context context, Map map) {
        Intent intent = new Intent(context, MapDetailActivity.class);
        intent.putExtra(EXTRA_MAP, map);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);

        Map map = getIntent().getParcelableExtra(EXTRA_MAP);

        TouchNetworkImageView imageView = findViewById(R.id.imageView);

        imageView.setImageUrl(map.url, ((App) getApplicationContext()).getImageLoader());
        imageView.setMaxZoom(4f);
    }
}
