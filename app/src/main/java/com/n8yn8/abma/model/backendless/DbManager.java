package com.n8yn8.abma.model.backendless;

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
}
