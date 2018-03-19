package com.n8yn8.abma.model.backendless;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;
import com.backendless.persistence.LoadRelationsQueryBuilder;
import com.backendless.persistence.local.UserIdStorageFactory;
import com.backendless.persistence.local.UserTokenStorageFactory;
import com.n8yn8.abma.Utils;

import java.util.ArrayList;
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
                Utils.logSignUp(true);
                login(email, password, callback);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.logSignUp(false);
                Utils.logError("Register", fault.getMessage());
                callback.onLogin(fault.getMessage());
            }
        });
    }

    public void login(String email, String password, final OnLoginResponse callback) {
        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                Utils.logLogIn(true);
                callback.onLogin(null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.logLogIn(false);
                Utils.logError("Login", fault.getMessage());
                callback.onLogin(fault.getMessage());
            }
        }, true);
    }

    public void logout(@Nullable final CheckUserCallback callback) {
        Backendless.UserService.logout(new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void response) {
                if (callback != null) {
                    callback.onDone();
                }
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.logError("Logout", fault.getMessage());
                if (callback != null) {
                    callback.onDone();
                }
            }
        });
    }

    public interface CheckUserCallback {
        void onDone();
    }
    public void checkUser(final CheckUserCallback callback) {
        String currentUserObjectId = UserIdStorageFactory.instance().getStorage().get();
        if (!TextUtils.isEmpty(currentUserObjectId)) {
            Backendless.Data.of( BackendlessUser.class ).findById(currentUserObjectId, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser response) {
                    Backendless.UserService.setCurrentUser(response);
                    callback.onDone();
                }

                @Override
                public void handleFault(BackendlessFault fault) {
                    Utils.logError("CheckUser", fault.getMessage());
                    logout(callback);
                }
            });
        }
    }

    public BackendlessUser getCurrentUser() {
        return Backendless.UserService.CurrentUser();
    }

    public void isValidLogin(AsyncCallback<Boolean> callback) {
        String userToken = UserTokenStorageFactory.instance().getStorage().get();
        if (!TextUtils.isEmpty(userToken)) {
            Backendless.UserService.isValidLogin(callback);
        } else {
            callback.handleResponse(false);
        }
    }

    public interface Callback<T> {
        public void onDone(T t, String error);
    }

    public void getYears(Context context, final Callback<List<BYear>> callback) {
        String queryString = "publishedAt is not null";
        Date lastUpdate = Utils.getLastUpdated(context);
        if (lastUpdate != null) {
            queryString += " AND updated > " + lastUpdate.getTime();
        }
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause(queryString);
        Backendless.Persistence.of(BYear.class).find(queryBuilder, new AsyncCallback<List<BYear>>() {
            @Override
            public void handleResponse(List<BYear> response) {
                callback.onDone(response, null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.logError("GetYears", fault.getMessage());
                callback.onDone(new ArrayList<BYear>(), fault.getMessage());
            }
        });
    }

    public void getEvents(String yearId, final Callback<List<BEvent>> callback) {
        LoadRelationsQueryBuilder<BEvent> loadRelationsQueryBuilder = LoadRelationsQueryBuilder.of( BEvent.class )
                .setRelationName( "events" )
                .setPageSize(100);

        Backendless.Data.of( BYear.class ).loadRelations(yearId, loadRelationsQueryBuilder, new AsyncCallback<List<BEvent>>() {
            @Override
            public void handleResponse(List<BEvent> response) {
                callback.onDone(response, null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.logError("GetEvents", fault.getMessage());
                callback.onDone(new ArrayList<BEvent>(), fault.getMessage());
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
                Utils.logError("AddNote", fault.getMessage());
                callback.noteSaved(null, fault.getMessage());
            }
        });
    }

    public interface OnGetNotesCallback {
        void notesRetrieved(List<BNote> notes, String error);
    }

    public void getAllNotes(final OnGetNotesCallback callback) {
        String userId = UserIdStorageFactory.instance().getStorage().get();
        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setWhereClause("user.objectId = \'" + userId + "\'");
        Backendless.Persistence.of(BNote.class).find(queryBuilder, new AsyncCallback<List<BNote>>() {
            @Override
            public void handleResponse(List<BNote> response) {
                callback.notesRetrieved(response, null);
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                Utils.logError("GetAllNotes", fault.getMessage());
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
                Utils.logError("RegisterPush", fault.getMessage());
            }
        });
    }
}
