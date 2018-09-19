package com.tangfeng.wifilist;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.tangfeng.wifilist.utils.WifiAPUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Date :2018/8/16
 * Time :14:26
 * author:moyihen
 */

public class AccessPoint extends AppCompatActivity {

    private WifiManager wifiManager;
    private TextView textview;
    private String TAG="AccessPoint";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_access_point);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("热点配置");
        }
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        initView();

        //createWifiHotspot();
        WifiAPUtils.getInstance(this).setWifiAPListener(new WifiAPUtils.wifiAPListener() {
            @Override
            public void enabled() {
                String ssid = WifiAPUtils.getInstance(AccessPoint.this).getValidApSsid();
                String pw = WifiAPUtils.getInstance(AccessPoint.this).getValidPassword();
                int security = WifiAPUtils.getInstance(AccessPoint.this).getValidSecurity();
                Log.i(TAG, "enabled: "+"wifi热点开启成功"+"\n"
                        +"SSID = "+ssid+"\n"
                        +"Password = "+pw +"\n"
                        +"Security = "+security);
            }

            @Override
            public void disabled() {
                Log.i(TAG, "disabled: wifi 热点关闭");
            }
        });
    }


    private void initView() {
        textview = findViewById(R.id.tv);
        Button bt_open = findViewById(R.id.bt_open);
        bt_open.setOnClickListener(view -> {
            if (WifiAPUtils.getInstance(this).turnOnWifiAP("wozuishuai--!",
                    "qaqwqeqr", WifiAPUtils.WifiSecurityType.WIFICIPHER_WPA2)){
                Log.i(TAG, "initView: 打开热点成功");
            }
        });
        Button bt_close = findViewById(R.id.bt_close);
        bt_close.setOnClickListener(view -> WifiAPUtils.getInstance(this).closeWifiAp());
    }


    /**
     * 7.0之前创建Wifi热点
     */
    private void createWifiHotspot() {
        if (wifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            wifiManager.setWifiEnabled(false);
        }
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = "P20";
        config.preSharedKey = "123456789";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = wifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            boolean enable = (Boolean) method.invoke(wifiManager, config, true);
            if (enable) {
                textview.setText("热点已开启 SSID:p20"+ " password:123456789");
            } else {
                textview.setText("创建热点失败1");
            }
        } catch (Exception e) {
            e.printStackTrace();
            textview.setText("创建热点失败2");
        }
    }

    /**
     * 关闭热点,并开启wifi
     */
    public  void closeWifiHotspot(WifiManager wifiManager) {

        try {
            Method method  = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
            Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method2.invoke(wifiManager, config, false);
            //开启wifi
            wifiManager.setWifiEnabled(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            textview.setText("关闭热点失败");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            textview.setText("关闭热点失败");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            textview.setText("关闭热点失败");
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WifiAPUtils.getInstance(this).ApDestory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
