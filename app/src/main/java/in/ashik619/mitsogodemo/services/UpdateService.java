package in.ashik619.mitsogodemo.services;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;

import in.ashik619.mitsogodemo.MainActivity;
import in.ashik619.mitsogodemo.R;
import in.ashik619.mitsogodemo.data.UpdateData;
import in.ashik619.mitsogodemo.data.WeatherRepository;
import in.ashik619.mitsogodemo.data.WeatherResponse;
import in.ashik619.mitsogodemo.db.UpdatesDBHandler;

import static android.net.ConnectivityManager.TYPE_MOBILE;
import static android.net.ConnectivityManager.TYPE_WIFI;

/**
 * Created by dilip on 3/5/18.
 */

public class UpdateService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private GoogleApiClient mGoogleApiClient;
    private UpdateData updateData = null;
    private static final String ERROR = "Error";
    private static final String INTENT_FILTER = "scanning_update";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("UPDATE","start");
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle("Update Service Started.")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
        startForeground(3195, notification);
        updateData = new UpdateData();
        if (intent.getBooleanExtra("isWeather",false)) {
            buildGoogleApiClient();
        }
        if (intent.getBooleanExtra("isBattery",false)) {
            updateData.setBattery(String.valueOf(getBatteryStatus()));
        }
        if (intent.getBooleanExtra("isDevice",false)) {
            updateData.setDeviceName(getDeviceName());
        }

        if (intent.getBooleanExtra("isNetworkType",false)) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null) {
                if (cm.getActiveNetworkInfo().getType() == TYPE_WIFI) {
                    updateData.setNetworkType("Wifi");
                } else if (cm.getActiveNetworkInfo().getType() == TYPE_MOBILE) {
                    updateData.setNetworkType("Mobile Data");
                }
            } else {
                updateData.setNetworkType("No Connection");
            }
        }

        if (intent.getBooleanExtra("isStorage",false)) {
            updateData.setDeviceStorage("Ext : "+getAvailableExternalMemorySize()+" Int : "+getAvailableInternalMemorySize());
        }
        if (!intent.getBooleanExtra("isWeather",false)){
            UpdatesDBHandler.getInstance(UpdateService.this).putUpdateDataData(updateData);
            Intent i = new Intent(INTENT_FILTER);
            sendBroadcast(i);
            stopForeground(true);
            stopSelf();
        }
        return START_STICKY;
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
        requestLocationUpdates();
    }

    private void requestLocationUpdates() {
        try {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, UpdateService.this);
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocReq(), UpdateService.this);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
       // Log.e("ALRM","loc");
        String _lat = String.valueOf(location.getLatitude());
        String _lng = String.valueOf(location.getLongitude());
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, UpdateService.this);
        new WeatherRepository().getWeatherData(_lat, _lng, new WeatherRepository.DataListner() {
            @Override
            public void onData(WeatherResponse response) {
                updateData.setWeatherResponse(String.valueOf(response.getMain().getTemp()));
                UpdatesDBHandler.getInstance(UpdateService.this).putUpdateDataData(updateData);
                Intent i = new Intent(INTENT_FILTER);
                sendBroadcast(i);
                stopForeground(true);
                stopSelf();
            }
        });
    }

    private LocationRequest createLocReq() {
        LocationRequest mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(2000);
        return mLocationRequest;
    }
    private int getBatteryStatus(){
        int batteryStatus = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            try {
                batteryStatus = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
            }catch (NullPointerException e){
            }
        }
        return batteryStatus;
    }
    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        String deviceOs = getOsName();
        return manufacturer+", "+model+", "+deviceOs;
    }
    private String getOsName(){
        String osName="";
        String[] mapper = new String[]{
                "ANDROID BASE", "ANDROID BASE 1.1", "CUPCAKE", "DONUT",
                "ECLAIR", "ECLAIR_0_1", "ECLAIR_MR1", "FROYO",
                "GINGERBREAD", "GINGERBREAD_MR1", "HONEYCOMB", "HONEYCOMB_MR1",
                "HONEYCOMB_MR2", "ICE_CREAM_SANDWICH", "ICE_CREAM_SANDWICH_MR1", "JELLY_BEAN",
                "JELLY_BEAN", "JELLY_BEAN", "KITKAT", "KITKAT",
                "LOLLIPOOP", "LOLLIPOOP_MR1", "MARSHMALLOW", "NOUGAT",
                "NOUGAT", "OREO", "OREO", "ANDROID P"};
        int index = Build.VERSION.SDK_INT - 1;
        osName = index < mapper.length ? mapper[index] : "UNKNOWN_VERSION";
        return osName;
    }

    private boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    private  String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return formatSize(availableBlocks * blockSize);
    }

    private String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long availableBlocks = stat.getAvailableBlocksLong();
            return formatSize(availableBlocks * blockSize);
        } else {
            return ERROR;
        }
    }

    private String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }


}
