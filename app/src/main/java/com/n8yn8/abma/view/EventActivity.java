package com.n8yn8.abma.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

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
    RecyclerView papersListView;
    PaperListAdapter adapter;

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

        viewModel.getEventPaper().observe(this, new Observer<EventPaperModel>() {
            @Override
            public void onChanged(EventPaperModel eventPaperModel) {
                Log.d(TAG, "on event changed: " + eventPaperModel);
                displayEvent(eventPaperModel.getEvent(), eventPaperModel.getPaper());
            }
        });
        viewModel.setSelectedEvent(getIntent().getStringExtra(EXTRA_EVENT_ID));

        viewModel.getNoteLiveData().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if (note != null) {
                    noteEditText.setText(note.content);
                } else {
                    noteEditText.setText("");
                }
            }
        });

        viewModel.getDirectionLimit().observe(this, new Observer<EventViewModel.DirectionLimit>() {
            @Override
            public void onChanged(EventViewModel.DirectionLimit directionLimit) {
                String displayText;
                switch (directionLimit) {
                    case EVENT_MAX:
                        displayText = "Last event reached";
                        break;
                    case EVENT_MIN:
                        displayText = "First event reached";
                        break;
                    case PAPER_MAX:
                        displayText = "Last paper reached";
                        break;
                    case PAPER_MIN:
                        displayText = "First paper reached";
                        break;
                    default:
                        displayText = "Limit reached";
                        break;
                }
                Toast.makeText(getApplicationContext(), displayText, Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton backButton = findViewById(R.id.backEventButton);
        ImageButton nextButton = findViewById(R.id.nextEventButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getPrevious();
            }
        });
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getNext();
            }
        });

        saveButton = findViewById(R.id.saveNoteButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(noteEditText.getWindowToken(), 0);
                }

                boolean result = viewModel.saveNote(noteEditText.getText().toString());
                Toast.makeText(EventActivity.this, "This note has been " + (result ? "saved" : "deleted"), Toast.LENGTH_SHORT).show();
            }
        });

        papersListView = findViewById(R.id.papersListView);
        papersListView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new PaperListAdapter(new PaperListAdapter.OnClickListener() {
            @Override
            public void onClick(Paper paper) {
                viewModel.selectPaper(paper);
            }
        });
        papersListView.setAdapter(adapter);
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
            EventPaperModel eventPaperModel= viewModel.getEventPaper().getValue();
            if (eventPaperModel != null && eventPaperModel.getPaper() != null) {
                viewModel.selectPaper( null);
            } else {
                finish();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        EventPaperModel eventPaperModel= viewModel.getEventPaper().getValue();
        if (eventPaperModel != null && eventPaperModel.getPaper() != null) {
            viewModel.selectPaper( null);
        } else {
            super.onBackPressed();
        }
    }

    public void displayEvent(final Event event, @Nullable Paper paper) {
        Date date = new Date(event.startDate);
        SimpleDateFormat dayFormatter = new SimpleDateFormat("EEEE");
        dayFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        dayTextView.setText(dayFormatter.format(date).toUpperCase());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("d");
        dateTextView.setText(dateFormatter.format(date));

        timeTextView.setText(Utils.getTimes(event));
        placeTextView.setText(event.place);

        List<Paper> papers = viewModel.getPapers(event.objectId);
        adapter.submitList(papers);

        if (paper == null) {
            titleTextView.setText(event.title);
            subtitleTextView.setText(event.subtitle);
            detailTextView.setText(event.details);
            detailTextView.setMovementMethod(LinkMovementMethod.getInstance());
            detailTextView.scrollTo(0, 0);
            papersListView.setVisibility(View.VISIBLE);
        } else {
            papersListView.setVisibility(View.GONE);
            titleTextView.setText(paper.title);
            subtitleTextView.setText(paper.author);
            detailTextView.setText(paper.synopsis);
            detailTextView.setMovementMethod(LinkMovementMethod.getInstance());
            detailTextView.scrollTo(0, 0);
        }
    }
}
