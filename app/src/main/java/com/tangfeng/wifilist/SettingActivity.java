package com.tangfeng.wifilist;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tangfeng.wifilist.utils.GPSUtils;
import com.tangfeng.wifilist.utils.LocationUtils;

import java.util.Map;
import java.util.Set;

/**
 * Date :2018/9/6
 * Time :15:28
 * author:moyihen
 */

public class SettingActivity extends AppCompatActivity {
    private String TAG="SettingActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();

        String lngAndLat = GPSUtils.getInstance(this).getLngAndLat(new GPSUtils.OnLocationResultListener() {
            @Override
            public void onLocationResult(Location location) {
                Log.i(TAG, "onLocationResult: 纬度:"+location.getLatitude()+"--经度:"+location.getLongitude());

            }

            @Override
            public void OnLocationChange(Location location) {
                Log.i(TAG, "位置变化OnLocationChange: 纬度:"+location.getLatitude()+"--经度:"+location.getLongitude());
            }
        });
        Log.i(TAG, "onCreate: 位置信息"+lngAndLat);
       /* LocationUtils instance = LocationUtils.getInstance(this);
        Map<String, String> location = LocationUtils.getLocation();
        Set<Map.Entry<String, String>> entries = location.entrySet();

        for (Map.Entry<String, String> entry:entries){
            Log.i(TAG, "onCreate: 经度:"+entry.getKey()+"----纬度:"+entry.getValue());
        }
*/
    }

    private void initView() {
        findViewById(R.id.bt_location).setOnClickListener(view -> openGPS(true));
    }

    private void openGPS(boolean open) {
        if (Build.VERSION.SDK_INT < 19) {
            Settings.Secure.setLocationProviderEnabled(this.getContentResolver(),
                    LocationManager.GPS_PROVIDER, open);
        } else {
            if (!open) {
                Settings.Secure.putInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
            } else {
                Settings.Secure.putInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING);
            }
        }
    }


}
