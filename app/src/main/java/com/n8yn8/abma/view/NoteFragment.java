package com.n8yn8.abma.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.material.snackbar.Snackbar;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.Note;
import com.n8yn8.abma.view.adapter.NoteListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
public class NoteFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    private NoteViewModel viewModel;
    private List<Note> noteList = new ArrayList<>();
    private TextView noDataTextView;
    private Snackbar loginSnackbar;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private NoteListAdapter mAdapter;

    public static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        noDataTextView = view.findViewById(R.id.emptyNoteListTextView);
        if (noDataTextView == null) {
            noDataTextView = view.findViewById(android.R.id.empty);
        }
        // Set the adapter
        mListView = view.findViewById(android.R.id.list);
        mAdapter = new NoteListAdapter(getActivity(), noteList);
        mListView.setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        viewModel.getNotesData().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteList.clear();
                noteList.addAll(notes);
                mAdapter.notifyDataSetChanged();
                if (noteList.size() == 0) {
                    noDataTextView.setText("No notes have been saved yet.");
                } else {
                    noDataTextView.setText("");
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginVisibility();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (loginSnackbar != null && loginSnackbar.isShown()) {
            loginSnackbar.dismiss();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Note note = mAdapter.getItem(position);
        if (note != null) {
            EventActivity.start(getContext(), note.eventId, note.paperId);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to delete this note?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Note note = mAdapter.getItem(position);
                viewModel.deleteNote(note);
                Toast.makeText(getActivity(), "This note has been deleted", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        return true;
    }

    private void updateLoginVisibility() {
        DbManager.getInstance().isValidLogin(new AsyncCallback<Boolean>() {
            @Override
            public void handleResponse(Boolean response) {
                if (!response) {
                    showSnackBar();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                showSnackBar();
            }
        });

    }

    private void showSnackBar() {
        if (getView() != null) {
            loginSnackbar = Snackbar.make(getView(), "Log in to save your notes online", Snackbar.LENGTH_INDEFINITE);
            loginSnackbar.setAction("Log In", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showLogin();
                }
            });
            loginSnackbar.show();
        }
    }

    private void showLogin() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Log In");
        LoginDialog view = new LoginDialog(getActivity());
        builder.setView(view);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                updateLoginVisibility();
            }
        });
        final AlertDialog dialog = builder.show();
        view.setCallback(new LoginDialog.OnLoginSuccess() {
            @Override
            public void loginSuccess() {
                dialog.dismiss();
                Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
                viewModel.getRemoteNotes();
            }
        });
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

}
