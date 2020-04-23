package com.example.covid;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class MoveActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private FusedLocationProviderClient mFusedLocation;
    private Runnable gpsRunnable;
    private Handler handler = new Handler();
    //public double latitude;
    //public double longitude;
    private ImageView gpsStatus;

    private Switch switchAllow;
    public static final String SWITCH1 = "switch1";
    public static final String SHARED_PREFS = "sharedPrefs";

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String userID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    Button requestLocation, removeLocation;
    BackgroundService mService = null;
    boolean mBound = false;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder)iBinder;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setTransparentStatusBarOnly(MoveActivity.this);

        try
        {
            Objects.requireNonNull(this.getSupportActionBar()).hide();
        }
        catch (NullPointerException ignored){}
        setContentView(R.layout.activity_move);

        switchAllow = findViewById(R.id.allow);
        updateViews();

        Dexter.withActivity(this)
                .withPermissions(Arrays.asList(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ))
                .withListener(new MultiplePermissionsListener(){
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        requestLocation = findViewById(R.id.button1);
                        removeLocation = findViewById(R.id.button0);
                        gpsStatus = findViewById(R.id.gps_status);

                        requestLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mService.requestLocationUpdates();
                                gpsStatus.setImageResource(R.drawable.image_profile);
                            }
                        });

                        removeLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                mService.removeLocationUpdates();
                                gpsStatus.setImageResource(R.drawable.image_profile0);
                            }
                        });

                        setButtonState(Common.requestingLocationUpdates(MoveActivity.this));
                        bindService(new Intent(MoveActivity.this,
                                BackgroundService.class),
                                mServiceConnection,
                                Context.BIND_AUTO_CREATE);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }

//    private void getLocation(){
//        mFusedLocation.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null){
//                    Log.d("My Current location", "Lat : " + location.getLatitude() + " Long : " + location.getLongitude());
//                }
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(mBound){
            unbindService(mServiceConnection);
            mBound = false;
        }
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        if(s.equals(Common.KEY_REQUESTING_LOCATION_UPDATES)){
            setButtonState(sharedPreferences.getBoolean(Common.KEY_REQUESTING_LOCATION_UPDATES, false));
        }
    }

    private void setButtonState(boolean isRequestEnable) {
        if(isRequestEnable){
            requestLocation.setEnabled(false);
            removeLocation.setEnabled(true);
        }
        else{
            requestLocation.setEnabled(true);
            removeLocation.setEnabled(false);
        }
    }

    public void setTransparentStatusBarOnly(Activity activity) {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        // this lines ensure only the status-bar to become transparent without affecting the nav-bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    public void allowCheck(View view){
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(SWITCH1, switchAllow.isChecked());
        editor.apply();

        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    public void updateViews() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean switchOnOff = sharedPreferences.getBoolean(SWITCH1, false);
        switchAllow.setChecked(switchOnOff);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onListenerLocation(SendLocationToActivity event){
        if(event != null){
            String data = new StringBuilder()
                    .append(event.getLocation().getLatitude())
                    .append("/")
                    .append(event.getLocation().getLongitude())
                    .toString();
            Toast.makeText(mService, data, Toast.LENGTH_SHORT).show();
        }
        //latitude = event.getLocation().getLatitude();
        //longitude= event.getLocation().getLongitude();
    }
}
