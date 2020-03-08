package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.n8yn8.abma.R;
import com.n8yn8.abma.Utils;
import com.n8yn8.abma.model.entities.Event;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.model.entities.Paper;
import com.n8yn8.abma.view.adapter.PaperListAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class EventActivity extends AppCompatActivity {

    private static final String EXTRA_EVENT_ID = "event_id";
    private static final String EXTRA_PAPER_ID = "paper_id";

    private final String TAG = "EventActivity";

    TextView dayTextView;
    TextView dateTextView;
    TextView titleTextView;
    TextView subtitleTextView;
    TextView detailTextView;
    TextView timeTextView;
    TextView placeTextView;
    EditText noteEditText;
    Button saveButton;

    EventViewModel viewModel;

    public static void start(Context context, @Nullable String eventId, @Nullable String paperId) {
        Intent intent = new Intent(context, EventActivity.class);
        intent.putExtra(EXTRA_EVENT_ID, eventId);
        intent.putExtra(EXTRA_PAPER_ID, paperId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(EventViewModel.class);

        dayTextView = findViewById(R.id.dayTextView);
        dateTextView = findViewById(R.id.dateTextView);
        titleTextView = findViewById(R.id.titleTextView);
        subtitleTextView = findViewById(R.id.subtitleTextView);
        detailTextView = findViewById(R.id.detailTextView);
        timeTextView = findViewById(R.id.timeTextView);
        placeTextView = findViewById(R.id.placeTextView);
        noteEditText = findViewById(R.id.noteEditText);

        viewModel.getEvent().observe(this, new Observer<Event>() {
            @Override
            public void onChanged(Event event) {
                Log.d(TAG, "on event changed: " + event);
                displayEvent(event, null, null);
            }
        });
        viewModel.setSelectedEvent(getIntent().getStringExtra(EXTRA_EVENT_ID));

        viewModel.getPaper().observe(this, new Observer<Paper>() {
            @Override
            public void onChanged(Paper paper) {
                displayEvent(viewModel.getEvent().getValue(), paper, null);
            }
        });

        ImageButton backButton = findViewById(R.id.backEventButton);
        ImageButton nextButton = findViewById(R.id.nextEventButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewModel.getPrevious()) {
                    Toast.makeText(getApplicationContext(), "First reached", Toast.LENGTH_SHORT).show();
                }
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewModel.getNext()) {
                    Toast.makeText(getApplicationContext(), "Last reached", Toast.LENGTH_SHORT).show();
                }

            }
        });

        saveButton = findViewById(R.id.saveNoteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);
                }

                boolean result = viewModel.saveNote(noteEditText.getText().toString());
                Toast.makeText(EventActivity.this, "This note has been " + (result ? "saved" : "deleted"), Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == android.R.id.home) {
            if (viewModel.getPaper().getValue() != null) {
                viewModel.getPaper().postValue(null);
            } else {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (viewModel.getPaper().getValue() != null) {
            viewModel.getPaper().postValue(null);
        } else {
            super.onBackPressed();
        }
    }

    public void displayEvent (final Event event, @Nullable Paper paper, @Nullable Note note) {
        Date date = new Date(event.startDate);
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));


        timeTextView.setText(Utils.getTimes(event));
        placeTextView.setText(event.place);

        List<Paper> papers = viewModel.getPapers(event.objectId);

        if (paper == null) {
            titleTextView.setText(event.title);
            subtitleTextView.setText(event.subtitle);
            detailTextView.setText(event.details);
            detailTextView.setMovementMethod(LinkMovementMethod.getInstance());
            detailTextView.scrollTo(0,0);
            final PaperListAdapter adapter = new PaperListAdapter(this, papers);
            ListView papersListView = findViewById(R.id.papersListView);
            papersListView.setVisibility(View.VISIBLE);
            papersListView.setAdapter(adapter);
            papersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Paper paper = adapter.getItem(position);
                    viewModel.getPaper().postValue(paper);
                }
            });
        } else {
            findViewById(R.id.papersListView).setVisibility(View.GONE);
            titleTextView.setText(paper.title);
            subtitleTextView.setText(paper.author);
            detailTextView.setText(paper.synopsis);
            detailTextView.setMovementMethod(LinkMovementMethod.getInstance());
            detailTextView.scrollTo(0,0);
        }
        note = viewModel.getNote(event.objectId, paper == null ? null : paper.objectId);
        if (note != null) {
            noteEditText.setText(note.content);
        } else {
            noteEditText.setText("");
        }
    }
}
