package com.n8yn8.abma;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class EventActivity extends ActionBarActivity {

    private final String TAG = "EventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            TextView titleTextView = (TextView) findViewById(R.id.titleTextView);
            TextView subtitleTextView = (TextView) findViewById(R.id.subtitleTextView);
            TextView detailTextView = (TextView) findViewById(R.id.detailTextView);
            TextView timeTextView = (TextView) findViewById(R.id.timeTextView);
            TextView placeTextView = (TextView) findViewById(R.id.placeTextView);

            titleTextView.setText(extras.getString("EXTRA_EVENT_TITLE"));
            subtitleTextView.setText(extras.getString("EXTRA_EVENT_SUBTITLE"));
            timeTextView.setText(extras.getString("EXTRA_EVENT_TIME"));
            placeTextView.setText(extras.getString("EXTRA_EVENT_LOCATION"));
            detailTextView.setText(extras.getString("EXTRA_EVENT_DETAIL"));
            detailTextView.setMovementMethod(new ScrollingMovementMethod());

            ImageButton backButton = (ImageButton) findViewById(R.id.backEventButton);
            ImageButton nextButton = (ImageButton) findViewById(R.id.nextEventButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
