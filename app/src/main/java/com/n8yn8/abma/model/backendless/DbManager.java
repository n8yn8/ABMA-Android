package com.n8yn8.abma.model.backendless;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.n8yn8.abma.Utils;

import java.util.Date;
import java.util.List;

/**
 * Created by Nate on 2/19/17.
 */
public class DbManager {
    private static DbManager ourInstance = new DbManager();

    public static DbManager getInstance() {
        return ourInstance;
    }

    private DbManager() {
    }

    public interface OnLoginResponse {
        void onLogin(@Nullable String error);
    }

    public void register(final String email, final String password, final OnLoginResponse callback) {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(email);
        user.setPassword(password);
        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                login(email, password, callback);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.onLogin(fault.getMessage());
            }
        });
    }

    public void login(String email, String password, final OnLoginResponse callback) {
        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                callback.onLogin(null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.onLogin(fault.getMessage());
            }
        }, true);
    }

    public void checkUser() {
        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        if (!TextUtils.isEmpty(currentUserObjectId)) {
            Backendless.Data.of( BackendlessUser.class ).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {
                    Backendless.UserService.setCurrentUser(response);
                    Log.d("Nate", "logged in");
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Log.d("Nate", "error");
                }
            });
        }
    }

    public void isValidLogin(AsyncCallback<Boolean> callback) {
        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        if (!TextUtils.isEmpty(userToken)) {
            Backendless.UserService.isValidLogin(callback);
        } else {
            callback.handleResponse(false);
        }
    }

    public interface YearsResponse {
        void onYearsReceived(List<BYear> years);
    }

    public void getYears(Context context, final YearsResponse callback) {
        String queryString = "publishedAt is not null";
        Date lastUpdate = Utils.getLastUpdated(context);
        if (lastUpdate != null) {
            queryString += " AND updated > " + lastUpdate.getTime();
        }
        BackendlessDataQuery query = new BackendlessDataQuery(queryString);
        Backendless.Persistence.of(BYear.class).find(query, new AsyncCallback<BackendlessCollection<BYear>>() {
            @Override
            public void handleResponse(BackendlessCollection<BYear> response) {
                Log.d("Nate", "" + response);
                callback.onYearsReceived(response.getCurrentPage());
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("Nate", "" + fault);
            }
        });
    }

    public interface OnNoteSavedCallback {
        void noteSaved(BNote note, String error);
    }

    public void addNote(BNote note, final OnNoteSavedCallback callback) {
        BackendlessUser user = Backendless.UserService.CurrentUser();
        if (user == null) {
            callback.noteSaved(null, "No User");
            return;
        }
        note.setUser(user);
        Backendless.Persistence.of(BNote.class).save(note, new AsyncCallback<BNote>() {
            @Override
            public void handleResponse(BNote response) {
                callback.noteSaved(response, null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.e("DbManager", "Note save error: " + fault.getMessage());
                callback.noteSaved(null, fault.getMessage());
            }
        });
    }

    public interface OnGetNotesCallback {
        void notesRetrieved(List<BNote> notes, String error);
    }

    public void getAllNotes(final OnGetNotesCallback callback) {
        String userId = UserIdStorageFactory.instance().getStorage().get();
        BackendlessDataQuery query = new BackendlessDataQuery("user.objectId = \'" + userId + "\'");
        Backendless.Persistence.of(BNote.class).find(query, new AsyncCallback<BackendlessCollection<BNote>>() {
            @Override
            public void handleResponse(BackendlessCollection<BNote> response) {
                callback.notesRetrieved(response.getCurrentPage(), null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                callback.notesRetrieved(null, fault.getMessage());
            }
        });
    }

    public void registerPush() {
        Backendless.Messaging.registerDevice("1099001155411", new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                Log.d("Nate", "push reg response: " + response);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Log.d("Nate", "push reg fault: " + fault.getMessage());
            }
        });
    }
}