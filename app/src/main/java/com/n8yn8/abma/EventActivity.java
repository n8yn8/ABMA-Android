package com.n8yn8.abma;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;


public class EventActivity extends ActionBarActivity {

    private final String TAG = "EventActivity";
    Schedule schedule;

    TextView dayTextView;
    TextView dateTextView;
    TextView titleTextView;
    TextView subtitleTextView;
    TextView detailTextView;
    TextView timeTextView;
    TextView placeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        dayTextView = (TextView) findViewById(R.id.dayTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        subtitleTextView = (TextView) findViewById(R.id.subtitleTextView);
        detailTextView = (TextView) findViewById(R.id.detailTextView);
        timeTextView = (TextView) findViewById(R.id.timeTextView);
        placeTextView = (TextView) findViewById(R.id.placeTextView);

            schedule = Cache.getInstance().getSchedule();
            Event event = schedule.getCurrentEvent();
        displayEvent(event);

            ImageButton backButton = (ImageButton) findViewById(R.id.backEventButton);
            ImageButton nextButton = (ImageButton) findViewById(R.id.nextEventButton);
            backButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event nextEvent = schedule.getPrevEvent();
                    if (nextEvent != null) {
                        displayEvent(nextEvent);
                    } else {
                        Toast.makeText(getApplicationContext(), "First event reached", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Event nextEvent = schedule.getNextEvent();
                    if (nextEvent != null) {
                        displayEvent(nextEvent);
                    } else {
                        Toast.makeText(getApplicationContext(), "Last event reached", Toast.LENGTH_SHORT).show();
                    }
                }
            });


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

    public void displayEvent (Event event) {
        Date date = schedule.getCurrentDate();
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));
        titleTextView.setText(event.getTitle());
        subtitleTextView.setText(event.getSubtitle());
        timeTextView.setText(event.getTime());
        placeTextView.setText(event.getPlace());
        detailTextView.setText(event.getDetails());
        detailTextView.setMovementMethod(new ScrollingMovementMethod());
    }
}
