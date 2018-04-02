package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.NetworkImageView;
import com.n8yn8.abma.App;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BMap;

public class MapDetailActivity extends AppCompatActivity {

    private static final String EXTRA_MAP = "extra_map";

    public static void start(Context context, BMap map) {
        Intent intent = new Intent(context, MapDetailActivity.class);
        intent.putExtra(EXTRA_MAP, map);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);

        BMap map = getIntent().getParcelableExtra(EXTRA_MAP);

        NetworkImageView imageView = (NetworkImageView) findViewById(R.id.imageView);

        imageView.setImageUrl(map.getUrl(), ((App) getApplicationContext()).getImageLoader());
    }
}
