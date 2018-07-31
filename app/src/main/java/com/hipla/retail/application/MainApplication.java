package com.hipla.retail.application;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import com.navigine.naviginesdk.NavigationThread;
import com.navigine.naviginesdk.NavigineSDK;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.Locale;

import io.paperdb.Paper;

/**
 * Created by FNSPL on 10/31/2017.
 */

public class MainApplication extends Application {

    public static final String TAG           = "Navigine.Demo";
    public static final String SERVER_URL    = "https://api.navigine.com";
    public static final String USER_HASH     = "F102-6B94-6E3C-1AF4";
    //public static final String USER_HASH     = "0F17-DAE1-4D0A-1778";
    public static final int LOCATION_ID   = 2637;
    //public static final int LOCATION_ID   = 1894;

    public static NavigationThread Navigation    = null;
    public static boolean isNavigineInitialized = false;
    public static boolean isApplicationOpen = false;

    // Screen parameters
    public static float DisplayWidthPx            = 0.0f;
    public static float DisplayHeightPx           = 0.0f;
    public static float DisplayWidthDp            = 0.0f;
    public static float DisplayHeightDp           = 0.0f;
    public static float DisplayDensity            = 0.0f;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Paper.init(getApplicationContext());
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(2)
                .memoryCache(new WeakMemoryCache())
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).build();

        ImageLoader.getInstance().init(config);
    }

    public static boolean initialize(Context context)
    {
        NavigineSDK.setParameter(context, "debug_level", 2);
        NavigineSDK.setParameter(context, "apply_server_config_enabled",  false);
        NavigineSDK.setParameter(context, "actions_updates_enabled",      false);
        NavigineSDK.setParameter(context, "location_updates_enabled",     true);
        NavigineSDK.setParameter(context, "location_update_timeout",      30);
        NavigineSDK.setParameter(context, "post_beacons_enabled",         true);
        NavigineSDK.setParameter(context, "post_messages_enabled",        true);
        if (!NavigineSDK.initialize(context, USER_HASH, SERVER_URL)) {
            return false;
        }else{
            isNavigineInitialized=true;
        }

        Navigation = NavigineSDK.getNavigation();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        DisplayWidthPx  = displayMetrics.widthPixels;
        DisplayHeightPx = displayMetrics.heightPixels;
        DisplayDensity  = displayMetrics.density;
        DisplayWidthDp  = DisplayWidthPx  / DisplayDensity;
        DisplayHeightDp = DisplayHeightPx / DisplayDensity;

        Log.d(TAG, String.format(Locale.ENGLISH, "Display size: %.1fpx x %.1fpx (%.1fdp x %.1fdp, density=%.2f)",
                DisplayWidthPx, DisplayHeightPx,
                DisplayWidthDp, DisplayHeightDp,
                DisplayDensity));

        return true;
    }

    public static void finish()
    {
        isNavigineInitialized=false;
        if (Navigation == null)
            return;

        NavigineSDK.finish();
        Navigation = null;
    }

}
