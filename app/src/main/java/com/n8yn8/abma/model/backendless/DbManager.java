package com.n8yn8.abma.model.backendless;

import android.util.Log;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

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

    public void register(final String email, final String password) {
        BackendlessUser user = new BackendlessUser();
        user.setEmail(email);
        user.setPassword(password);
        Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {
                login(email, password);
            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        });
    }

    public void login(String email, String password) {
        Backendless.UserService.login(email, password, new AsyncCallback<BackendlessUser>() {
            @Override
            public void handleResponse(BackendlessUser response) {

            }

            @Override
            public void handleFault(BackendlessFault fault) {

            }
        }, true);
    }

    public interface YearsResponse {
        void onYearsReceived(List<BYear> years);
    }

    public void getYears(final YearsResponse callback) {
        Backendless.Persistence.of(BYear.class).find(new AsyncCallback<BackendlessCollection<BYear>>() {
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
}
