package com.tangfeng.wifilist.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

/**
 * Date :2018/9/6
 * Time :16:01
 * author:moyihen
 */

public class GPSUtils  {
    private static GPSUtils instance;
    private Context mContext;
    private LocationManager locationManager;
    private String TAG="GPSUtils";

    private GPSUtils(Context context) {
        this.mContext = context;
    }

    public static GPSUtils getInstance(Context context) {
        if (instance == null) {
            instance = new GPSUtils(context);
        }
        return instance;
    }

    /**
     * 获取经纬度
     *
     * @return
     */
    public String getLngAndLat(OnLocationResultListener onLocationResultListener) {
        double latitude = 0.0;
        double longitude = 0.0;

        mOnLocationListener = onLocationResultListener;

        String locationProvider = null;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providers = locationManager.getProviders(true);

        if (providers.contains(LocationManager.GPS_PROVIDER)) {
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
            Log.i(TAG, "getLngAndLat: GPS 定位");
        } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
            Log.i(TAG, "getLngAndLat: 网络 定位");
        } else {
            Intent i = new Intent();
            i.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            mContext.startActivity(i);
            Log.i(TAG, "getLngAndLat: ----ACTION_LOCATION_SOURCE_SETTINGS");
            return null;
        }

        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        if (location != null) {
            //不为空,显示地理位置经纬度
            if (mOnLocationListener != null) {
                mOnLocationListener.onLocationResult(location);
            }

        }
        //监视地理位置变化
        locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);
        return null;
    }


    public LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {

        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (mOnLocationListener != null) {
                mOnLocationListener.OnLocationChange(location);
            }
        }
    };

    public void removeListener() {
        locationManager.removeUpdates(locationListener);
    }

    private OnLocationResultListener mOnLocationListener;

    public interface OnLocationResultListener {
        void onLocationResult(Location location);

        void OnLocationChange(Location location);
    }
}
