package com.n8yn8.abma

import android.app.Application
import com.backendless.Backendless
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.n8yn8.abma.model.backendless.DbManager
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Nate on 2/18/15.
 */

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG);

//        Backendless.initApp(this, "7D06F708-89FA-DD86-FF95-C51A10425A00", "AA32ED18-4FEC-569C-FF5F-AE0F2F571E00") //Prod
                Backendless.initApp(this, "76269ABA-AF2E-5901-FF61-99AB83F57700", "25B7C6B5-E2E5-4B39-B058-1FA73D862A19") //Test
        DbManager.getInstance().checkUser { DbManager.getInstance().registerPush() }

    }

}