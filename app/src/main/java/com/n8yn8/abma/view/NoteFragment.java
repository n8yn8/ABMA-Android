package com.n8yn8.abma.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.google.android.material.snackbar.Snackbar;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.DbManager;
import com.n8yn8.abma.model.entities.NoteEventPaper;
import com.n8yn8.abma.view.adapter.NoteListAdapter;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 */
@AndroidEntryPoint
public class NoteFragment extends Fragment {

    private NoteViewModel viewModel;
    private TextView noDataTextView;
    private Snackbar loginSnackbar;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private NoteListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteFragment() {
    }

    static NoteFragment newInstance() {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        noDataTextView = view.findViewById(R.id.emptyNoteListTextView);
        if (noDataTextView == null) {
            noDataTextView = view.findViewById(android.R.id.empty);
        }

        RecyclerView recyclerView = view.findViewById(R.id.item_list);
        recyclerView.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        mAdapter = new NoteListAdapter(new NoteListAdapter.OnClickListener() {
            @Override
            public void onClick(NoteEventPaper noteModel) {
                onItemClick(noteModel);
            }

            @Override
            public void onLongClick(NoteEventPaper noteModel) {
                onItemLongClick(noteModel);
            }
        });
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NoteViewModel.class);
        viewModel.getNotesData().observe(getViewLifecycleOwner(), new Observer<List<NoteEventPaper>>() {
            @Override
            public void onChanged(List<NoteEventPaper> notes) {
                mAdapter.submitList(notes);
                if (notes.size() == 0) {
                    noDataTextView.setText(R.string.no_notes);
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

    private void onItemClick(NoteEventPaper noteModel) {
        if (noteModel != null) {
            EventActivity.start(
                    getContext(),
                    noteModel.getEvent().objectId,
                    noteModel.getPaper() != null ? noteModel.getPaper().objectId : null);
        }
    }

    private void onItemLongClick(final NoteEventPaper noteModel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to delete this note?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                viewModel.deleteNote(noteModel);
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

}
