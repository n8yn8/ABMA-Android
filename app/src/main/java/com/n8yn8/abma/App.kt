package com.n8yn8.abma

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import android.util.LruCache
import androidx.core.util.Pair
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.Volley
import com.backendless.Backendless
import com.crashlytics.android.Crashlytics
import com.dd.plist.NSDictionary
import com.dd.plist.PropertyListParser
import com.n8yn8.abma.di.applicationModule
import com.n8yn8.abma.model.backendless.DbManager
import com.n8yn8.abma.model.old.Event
import com.n8yn8.abma.model.old.Note
import com.n8yn8.abma.model.old.Paper
import com.n8yn8.abma.model.old.Schedule
import io.fabric.sdk.android.Fabric
import org.koin.android.ext.android.startKoin

/**
 * Created by Nate on 2/18/15.
 */
private const val TAG = "App"

class App : Application() {

    var oldNotes: Map<Note, Pair<Event, Paper>>? = null
    var imageLoader: ImageLoader? = null
        private set

    override fun onCreate() {
        super.onCreate()
        if (!BuildConfig.DEBUG) {
            Fabric.with(this, Crashlytics())
        }

        startKoin(this, listOf(applicationModule))

        Backendless.initApp(this, "7D06F708-89FA-DD86-FF95-C51A10425A00", "AA32ED18-4FEC-569C-FF5F-AE0F2F571E00") //Prod
        //        Backendless.initApp(this, "05CFD853-3BFF-40F5-BAD0-E9CE8FA56630", "AA32ED18-4FEC-569C-FF5F-AE0F2F571E00"); //Test
        DbManager.getInstance().checkUser { DbManager.getInstance().registerPush() }

        val mRequestQueue = Volley.newRequestQueue(applicationContext)
        imageLoader = ImageLoader(mRequestQueue, object : ImageLoader.ImageCache {
            private val mCache = LruCache<String, Bitmap>(4 * 1024 * 1024) //4MB
            override fun putBitmap(url: String, bitmap: Bitmap) {
                mCache.put(url, bitmap)
            }

            override fun getBitmap(url: String?): Bitmap? {
                return mCache[url]
            }
        })
    }

    val oldSchedule: Schedule?
        get() {
            val scheduleDict: NSDictionary
            return try {
                val `is` = resources.openRawResource(R.raw.y2016)
                scheduleDict = PropertyListParser.parse(`is`) as NSDictionary
                Schedule(scheduleDict)
            } catch (ex: Exception) {
                Log.e(TAG, "" + ex.localizedMessage)
                null
            }
        }

}