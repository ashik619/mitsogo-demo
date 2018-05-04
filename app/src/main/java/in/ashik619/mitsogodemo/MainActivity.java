package in.ashik619.mitsogodemo;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.ashik619.mitsogodemo.base.BaseActivity;
import in.ashik619.mitsogodemo.data.UpdateData;
import in.ashik619.mitsogodemo.db.UpdatesDBHandler;
import in.ashik619.mitsogodemo.dialog.IntervalDialog;
import in.ashik619.mitsogodemo.services.AlarmReceiver;
import in.ashik619.mitsogodemo.services.UpdateService;

public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    @BindView(R.id.batteryLevel)
    CheckBox batteryLevel;
    @BindView(R.id.deviceName)
    CheckBox deviceName;
    @BindView(R.id.networkType)
    CheckBox networkType;
    @BindView(R.id.storage)
    CheckBox storage;
    @BindView(R.id.weatherReport)
    CheckBox weatherReport;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.resultsListView)
    RecyclerView resultsListView;
    @BindView(R.id.submitButton)
    Button submitButton;
    @BindView(R.id.intervalText)
    TextView intervalText;
    private GoogleApiClient mGoogleApiClient = null;
    private static final int REQ_LOC = 78;
    private boolean canReqLocUpdates = false;
    private BroadcastReceiver updatesBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        listners();

    }
    private int intHour = 0;
    private int intMin = 0;

    private void listners() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(intHour>0||intMin>0){
                    submitService();
                }else {
                  showToast("Select Interval");
                }
            }
        });
        intervalText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntervalDialog dialog = new IntervalDialog(new IntervalDialog.OnIntervalConfirmListner() {
                    @Override
                    public void onIntervalConfirmed(int hour, int min) {
                        if(min>=5||hour>0) {
                            intHour = hour;
                            intMin = min;
                            intervalText.setText(hour+"Hrs, "+min+"Mins");
                        }else showToast("Min interval is 5 min");
                    }
                });
                dialog.show(getFragmentManager(),"aja");
            }
        });
    }
    private void submitService(){
        stopService(new Intent(MainActivity.this,UpdateService.class));
        UpdatesDBHandler.getInstance(MainActivity.this).clearTable();
        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        alarmIntent.putExtra("isBattery", batteryLevel.isChecked());
        alarmIntent.putExtra("isDevice",deviceName.isChecked());
        alarmIntent.putExtra("isNetworkType",networkType.isChecked());
        alarmIntent.putExtra("isStorage",storage.isChecked());
        alarmIntent.putExtra("isWeather",weatherReport.isChecked());
        alarmIntent.setAction("in.ashik619.mitsogodemo.alarmBroadcast");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = (TimeUnit.MINUTES.toMillis(intMin)+TimeUnit.HOURS.toMillis(intHour));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendar.getTimeInMillis()+interval);
        manager.cancel(pendingIntent);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);
        showToast("Scanning Scheduled");
        fetchAndPopulateUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchAndPopulateUpdates();
        registerScanningUpdateReciever();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(updatesBroadcastReceiver);
        super.onStop();
    }

    private static final String INTENT_FILTER = "scanning_update";
    private void registerScanningUpdateReciever(){
        updatesBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                fetchAndPopulateUpdates();
            }
        };
        registerReceiver(updatesBroadcastReceiver, new IntentFilter(INTENT_FILTER));
    }
    private void fetchAndPopulateUpdates(){
        List<UpdateData> updateDataList = UpdatesDBHandler.getInstance(MainActivity.this).fetchUpdateData();
        resultsListView.setNestedScrollingEnabled(false);
        resultsListView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        UpdatesListAdapter adapter = new UpdatesListAdapter(updateDataList);
        resultsListView.setAdapter(adapter);
    }

    @Override
    protected void onNetworkConnected() {
        if (isPermissionGranted()) {
            buildGoogleApiClient();
        } else {
            requestPermissions();
        }
    }

    private void checkIfLocationOn() {
        final LocationRequest locationRequest = createLocReq();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        canReqLocUpdates = true;
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQ_LOC);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        break;
                }
            }
        });
    }

    @Override
    protected void onNoNetworkConnected() {

    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else return false;
        } else return true;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(MainActivity.this,
                permissions,
                420);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 420) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            } else {

            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_LOC: {
                buildGoogleApiClient();
                break;
            }
        }
    }

    private LocationRequest createLocReq() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        return mLocationRequest;
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkIfLocationOn();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private void showToast(String msg){
        Toast.makeText(MainActivity.this,msg,Toast.LENGTH_SHORT).show();
    }


}
