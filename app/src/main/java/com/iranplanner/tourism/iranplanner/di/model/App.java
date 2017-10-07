package com.iranplanner.tourism.iranplanner.di.model;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.iranplanner.tourism.iranplanner.R;
import com.iranplanner.tourism.iranplanner.di.data.component.DaggerNetComponent;
import com.iranplanner.tourism.iranplanner.di.data.component.NetComponent;
import com.iranplanner.tourism.iranplanner.di.data.module.AppModule;
import com.iranplanner.tourism.iranplanner.di.data.module.NetModule;

import java.util.HashMap;
import java.util.Map;

import server.Config;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

import static android.content.ContentValues.TAG;


/**
 * Created by Hoda
 */
public class App extends Application {
    private NetComponent mNetComponent;
    private NetComponent googleNetComponent;
    public FirebaseAnalytics mFirebaseAnalytics;

    public FirebaseAnalytics getmFirebaseAnalytics() {
        return mFirebaseAnalytics;
    }

    private static final String TAG = App.class.getSimpleName();

    private LocationManager locationManager;
    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        mNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule(Config.BASEURL, this))
                .build();
        googleNetComponent = DaggerNetComponent.builder()
                .appModule(new AppModule(this))
                .netModule(new NetModule("http://maps.googleapis.com/", this))
                .build();
        //

        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "1.0.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.sembozdemir.renstagram");

        firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activateFetched();
                        }
                    }
                });

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/IRANSansMobile.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        enableLocationCheck();
    }

    public NetComponent getNetComponent() {
        return mNetComponent;
    }

    public NetComponent getGoogleNetComponent() {
        return googleNetComponent;
    }

    public void enableLocationCheck() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    }

    public Location getLastLocation() {
        return location;
    }

//    public String getLastLocation() {
//        return "amin is awesome";
//    }

    private void storeLocation(Location location) {
        this.location = location;
    }

    private LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            storeLocation(location);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };
}
