package com.n8yn8.abma.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.n8yn8.abma.R;
import com.n8yn8.abma.model.backendless.BNote;
import com.n8yn8.abma.model.old.DatabaseHandler;
import com.n8yn8.abma.view.adapter.NoteListAdapter;

import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class NoteFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    private OnFragmentInteractionListener mListener;
    List<BNote> noteList;
    TextView noDataTextView;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DatabaseHandler db = new DatabaseHandler(getActivity().getApplicationContext());

        noteList = db.getAllNotes();

        mAdapter = new NoteListAdapter(getActivity(), noteList);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note, container, false);

        noDataTextView = (TextView) view.findViewById(R.id.emptyNoteListTextView);
        if (noteList.size() == 0) {
            noDataTextView.setText("No notes have been saved yet.");
        } else {
            noDataTextView.setText("");
        }
        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateLoginVisibility();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BNote note = mAdapter.getItem(position);
        EventActivity.start(getContext(), note.getEventId(), note.getPaperId());
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Would you like to delete this note?");
        builder.setCancelable(true);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                BNote note = mAdapter.getItem(position);
                DatabaseHandler db = new DatabaseHandler(getActivity());
                db.deleteNote(note);
                noteList.remove(position);
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "This note has been deleted", Toast.LENGTH_SHORT).show();
                if (noteList.size() == 0) {
                    noDataTextView.setText("No notes have been saved yet.");
                }
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
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user == null) {
            Snackbar loginSnackbar = Snackbar.make(getView(), "Log in to save your notes online", Snackbar.LENGTH_INDEFINITE);
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
        final AlertDialog dialog = builder.show();
        view.setCallback(new LoginDialog.OnLoginSuccess() {
            @Override
            public void loginSuccess() {
                dialog.dismiss();
                Toast.makeText(getContext(), "Logged in successfully", Toast.LENGTH_SHORT).show();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
//        public void onFragmentInteraction(String id);
    }

}
