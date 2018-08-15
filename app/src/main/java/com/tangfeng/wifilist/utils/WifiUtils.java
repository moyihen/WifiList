package com.tangfeng.wifilist.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Date :2018/8/1
 * Time :9:12
 * author:moyihen
 */

public class WifiUtils {

    private static WifiManager wifiManager;
    private WifiBroadCastReceiver mReceiver;
    private static String TAG = "WifiUtils";
   // private Context mContext = null;
    private static WifiUtils wifiUtils;
    private wifiListener mWifiListener;

    public static WifiUtils getInstance() {
        if (wifiManager == null) {
            synchronized (WifiUtils.class) {
                if (wifiManager == null) {
                    wifiUtils = new WifiUtils();
                }
            }
        }
        return wifiUtils;
    }

   public WifiManager initWifiManager(Context context) {
        //this.mContext = context;
        return wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    // 开启/关闭 WIFI.
    private boolean setWifiEnabled(WifiManager manager, boolean enabled) {
        return manager != null && manager.setWifiEnabled(enabled);
    }

    /**
     * 获取 WIFI 的状态.
     * 注意:
     * WiFi 的状态目前有五种, 分别是:
     * WifiManager.WIFI_STATE_ENABLING: WiFi正要开启的状态, 是 Enabled 和 Disabled 的临界状态;
     * WifiManager.WIFI_STATE_ENABLED: WiFi已经完全开启的状态;
     * WifiManager.WIFI_STATE_DISABLING: WiFi正要关闭的状态, 是 Disabled 和 Enabled 的临界状态;
     * WifiManager.WIFI_STATE_DISABLED: WiFi已经完全关闭的状态;
     * WifiManager.WIFI_STATE_UNKNOWN: WiFi未知的状态, WiFi开启, 关闭过程中出现异常, 或是厂家未配备WiFi外挂模块会出现的情况;
     */
    int getWifiState(WifiManager manager) {
        return manager == null ? WifiManager.WIFI_STATE_UNKNOWN : manager.getWifiState();
    }

    // 开始扫描 WIFI.
    public void startScanWifi(WifiManager manager) {
        if (manager != null) {
            manager.startScan();
            Log.i(TAG, "startScanWifi: 扫描");
        }else {
            Log.i(TAG, "startScanWifi: null 不扫描");
        }
    }

    // 获取扫描 WIFI 的热点:
    public List<ScanResult> getScanResult(WifiManager manager) {
        return manager == null ? null : manager.getScanResults();
    }

    // 获取已经保存过的/配置好的 WIFI 热点.
    public List<WifiConfiguration> getConfiguredNetworks(WifiManager manager) {
        return manager == null ? null : manager.getConfiguredNetworks();
    }

    // 使用 WifiConfiguration 连接.
    public void connectByConfig(WifiManager manager, WifiConfiguration config) {
        if (manager == null) {
            return;
        }
        try {
            Method connect = manager.getClass().getDeclaredMethod("connect", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (connect != null) {
                connect.setAccessible(true);
                connect.invoke(manager, config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 使用 networkId 连接.
    public void connectByNetworkId(WifiManager manager, int networkId) {
        if (manager == null) {
            return;
        }
        try {
            Method connect = manager.getClass().getDeclaredMethod("connect", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (connect != null) {
                connect.setAccessible(true);
                connect.invoke(manager, networkId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 保存网络.
    public void saveNetworkByConfig(WifiManager manager, WifiConfiguration config) {
        if (manager == null) {
            return;
        }
        try {
            Method save = manager.getClass().getDeclaredMethod("save", WifiConfiguration.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (save != null) {
                save.setAccessible(true);
                save.invoke(manager, config, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 添加网络.
    public int addNetwork(WifiManager manager, WifiConfiguration config) {
        if (manager != null) {
            return manager.addNetwork(config);
        }
        return -1;
    }

    // 忘记网络.
    public void Nforgetetwork(WifiManager manager, int networkId) {
        if (manager == null) {
            return;
        }
        try {
            Method forget = manager.getClass().getDeclaredMethod("forget", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (forget != null) {
                forget.setAccessible(true);
                forget.invoke(manager, networkId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 禁用网络.
    public void disableNetwork(WifiManager manager, int netId) {
        if (manager == null) {
            return;
        }
        try {
            Method disable = manager.getClass().getDeclaredMethod("disable", int.class, Class.forName("android.net.wifi.WifiManager$ActionListener"));
            if (disable != null) {
                disable.setAccessible(true);
                disable.invoke(manager, netId, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 断开连接.
    public  boolean disconnectNetwork(WifiManager manager) {
        return manager != null && manager.disconnect();
    }
    // 查看以前是否也配置过这个网络
    public WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }
    public boolean Connect(String SSID,String Password,WifiType type){
        if (!wifiManager.isWifiEnabled())
            return false;
        if (SSID == null || Password == null || SSID.equals("")) {
            Log.e(this.getClass().getName(),
                    "addNetwork() ## nullpointer error!");
            return false;
        }
        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, type);
        // wifi的配置信息
        if (wifiConfig == null) {
            return false;
        }
        // 查看以前是否也配置过这个网络
        WifiConfiguration tempConfig = this.isExsits(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }
        // 添加一个新的网络描述为一组配置的网络。
        int netID = wifiManager.addNetwork(wifiConfig);
        Log.d("WifiListActivity", "wifi的netID为：" + netID);
        // 断开连接
        wifiManager.disconnect();
        // 重新连接
        Log.d("WifiListActivity", "Wifi的重新连接netID为：" + netID);
        // 设置为true,使其他的连接断开
        boolean mConnectConfig = wifiManager.enableNetwork(netID, true);
        wifiManager.reconnect();
        return mConnectConfig;
    }

    /**
     * 忘记网络
     * @param networkId
     * @return
     */
    public boolean remove(int networkId){
        if (null == wifiManager){
            Log.e(TAG,"WifiManager 没有初始化");
            return false;
        }
        boolean isRemoved = wifiManager.removeNetwork(networkId);

        if (!isRemoved) {
            int index = 0;
            while (!isRemoved && index < 10) {
                index ++;
                isRemoved =  wifiManager.removeNetwork(networkId);
            }
        }

        if (isRemoved) {
            wifiManager.saveConfiguration();
        }
        return isRemoved;
    }

    /**
     * 检查wifi 是否连接.
     * @param SSID
     * @return
     */
    public boolean isConnected(String SSID){
        WifiInfo info = getConnectionInfo();
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }
        if (info == null) {
            return false;
        }
        String replace = info.getSSID().replace("\"", "");
        Log.i(TAG, "isConnected: 传入的值"+SSID+"---获取的值"+replace);
        if (replace.equals(SSID)) {
            return true;
        }

        return false;
    }

    /**
     * 获取当前连接wifi的配置.
     * @return
     */
    public WifiInfo getConnectionInfo(){
        if (null == wifiManager) {
            Log.e(TAG,"WifiManager 没有初始化");
            return null;
        }
        WifiInfo info = wifiManager.getConnectionInfo();
        if (SupplicantState.COMPLETED != info.getSupplicantState()) {
            return null;
        }
        if (-1 == info.getNetworkId()) {
            return null;
        }
        if (0 == info.getIpAddress()) {
            return null;
        }
        return info;
    }
    /**
     * 创建WifiConfiguration
     * 三个安全性的排序为：WEP<WPA<WPA2。
     * WEP是Wired Equivalent Privacy的简称，有线等效保密（WEP）协议是对在两台设备间无线传输的数据进行加密的方式，
     * 用以防止非法用户窃听或侵入无线网络
     * WPA全名为Wi-Fi Protected Access，有WPA和WPA2两个标准，是一种保护无线电脑网络（Wi-Fi）安全的系统，
     * 它是应研究者在前一代的系统有线等效加密（WEP）中找到的几个严重的弱点而产生的
     * WPA是用来替代WEP的。WPA继承了WEP的基本原理而又弥补了WEP的缺点：WPA加强了生成加密密钥的算法，
     * 因此即便收集到分组信息并对其进行解析，也几乎无法计算出通用密钥；WPA中还增加了防止数据中途被篡改的功能和认证功能
     * WPA2是WPA的增强型版本，与WPA相比，WPA2新增了支持AES的加密方式
     *
     * @param SSID
     * @param pwd
     * @param type
     * @return
     **/
   public WifiConfiguration createWifiInfo(String SSID ,String pwd,WifiType type){
       WifiConfiguration config = new WifiConfiguration();
       config.allowedAuthAlgorithms.clear();
       config.allowedGroupCiphers.clear();
       config.allowedKeyManagement.clear();
       config.allowedPairwiseCiphers.clear();
       config.allowedProtocols.clear();
       config.SSID = "\"" + SSID + "\"";

       if (type == WifiType.WIFI_CIPHER_NOPASS) {
           config.wepKeys[0] = "";
           config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
           config.wepTxKeyIndex = 0;
       } else if (type == WifiType.WIFI_CIPHER_WEP) {
           config.hiddenSSID = true;
           config.wepKeys[0] = "\"" + pwd + "\"";
           config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
           config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
           config.wepTxKeyIndex = 0;
       } else if (type == WifiType.WIFI_CIPHER_WPA) {
           config.preSharedKey = "\"" + pwd + "\"";
           config.hiddenSSID = true;
           config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
           config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
           config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
           config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
           config.status = WifiConfiguration.Status.ENABLED;
       } else if (type == WifiType.WIFI_CIPHER_WPA2) {
           config.preSharedKey = "\"" + pwd + "\"";
           config.hiddenSSID = true;
           config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
           config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
           config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
           config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
           config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
           config.status = WifiConfiguration.Status.ENABLED;
       }
       return config;
   }


   public enum WifiType {
       WIFI_CIPHER_NOPASS, WIFI_CIPHER_WEP, WIFI_CIPHER_WPA, WIFI_CIPHER_WPA2;
   }
    /*---------------------------------------WIFI状态监听-------------------------------------------------------*/

    class WifiBroadCastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_DISABLED://WIFI已关闭
                    mWifiListener.disabled();
                  //  Log.i(TAG, "onReceive: WIFI已关闭");
                    break;
                case WifiManager.WIFI_STATE_DISABLING://WIFI正在关闭中
                    mWifiListener.disabling();
                   // Log.i(TAG, "onReceive: WIFI正在关闭中");
                    break;
                case WifiManager.WIFI_STATE_ENABLED://WIFI已启用
                    mWifiListener.enabled();
                   // Log.i(TAG, "onReceive: WIFI已启用");
                    break;
                case WifiManager.WIFI_STATE_ENABLING://WIFI正在启动中
                    mWifiListener.enabling();
                    //Log.i(TAG, "onReceive: WIFI正在启动中");
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN://未知WIFI状态
                    mWifiListener.unknown();
                   // Log.i(TAG, "onReceive: 未知WIFI状态");
                    break;
            }

            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {
                Log.i(TAG, "wifi信号强度变化");
            }
            //wifi连接上与否
            if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {

                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    mWifiListener.wifi_disconnect();
                    //Log.i(TAG, "wifi断开");
                } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                    //获取当前wifi名称
                    mWifiListener.wifi_connected(wifiInfo.getSSID());
                    //Log.i(TAG, "连接到网络 " + wifiInfo.getSSID());
                }
            }

            if (intent.getAction().equals(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                int error = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR,
                        0);
                if (WifiManager.ERROR_AUTHENTICATING == error){
                    Log.d(TAG, "密码认证错误Code为：" + error);
                    remove(wifiManager.getConnectionInfo().getNetworkId());
                    //Toast.makeText(context, "wifi密码认证错误！"+error, Toast.LENGTH_SHORT).show();
                    mWifiListener.wifi_pwd_error();
                }
            }


        }
    }

    /**
     * 注册广播监听wifi状态
     */
   public void registerReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION); //wifi打开关闭
        //连接
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mReceiver = new WifiBroadCastReceiver();
        context.registerReceiver(mReceiver, filter);
        Log.i(TAG, "registerReceiver: 注册广播");
    }

    /**
     * 注销广播
     */
    public void unregisterReceiver(Context context) {
        if (mReceiver != null) {
            context.unregisterReceiver(mReceiver);
            mReceiver = null;
        }
        Log.i(TAG, "unregisterReceiver: 注销广播");
    }

    public interface wifiListener {
        //已关闭
        void disabled();
        //正在关闭
        void disabling();
        //已启用
        void enabled();
        //启动中
        void enabling();
        //未知
        void unknown();
        //连接到wifi
        void wifi_connected(String wifi_name);
        //断开
        void wifi_disconnect();
        //密码错误
        void wifi_pwd_error();
    }
    public void setWifiListener(wifiListener wifiListener){
        this.mWifiListener= wifiListener;
    }


    /**
     * 密码加密类型
     */
    public enum Data {
        WIFI_CIPHER_NOPASS(0), WIFI_CIPHER_WEP(1), WIFI_CIPHER_WPA(2), WIFI_CIPHER_WPA2(3);

        private final int value;

        //构造器默认也只能是private, 从而保证构造函数只能在内部使用
        Data(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }



}
