package com.tangfeng.wifilist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.tangfeng.wifilist.utils.WifiUtils;
import com.tangfeng.wifilist.widget.LoadingDialog;

import java.util.List;

/**
 * Date :2018/8/3
 * Time :13:42
 * author:moyihen
 */

public class ConnectWifiActivity extends AppCompatActivity implements LoadingDialog.Builder.CloseListener {
    private String TAG = "ConnectWifiActivity";
    private WifiUtils mWifiUtils;
    private WifiManager mWifiManager;
    private ScanResult mScanResult;
    private EditText mEd_pwd;
    private ImageView mImg_eyes;
    private LoadingDialog mLoading_dialog;
    private LoadingDialog.Builder mLoadBuilder;
    private boolean show_tag;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wifi);
        Intent intent = getIntent();
        mScanResult = (ScanResult) intent.getParcelableExtra("ScanResult");
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(mScanResult.SSID);
        }
        Log.i(TAG, "onCreate: " + mScanResult.toString());
        mWifiUtils = WifiUtils.getInstance();
        mWifiManager = mWifiUtils.initWifiManager(this);
        mWifiUtils.setWifiListener(mWifiListener);
        initView();
        initLoadingDialog();
    }

    private void initLoadingDialog() {
        mLoadBuilder = new LoadingDialog.Builder(this)
                .setMessage("连接中...")
                .setCancelable(true)
                .setCancelOutside(false);
        mLoading_dialog = mLoadBuilder.create();
        mLoadBuilder.setCloseOnClickListener(this);

    }

    private void initView() {
        Button bt_yes = findViewById(R.id.bt_connect_wifi_yes);
        Button bt_no = findViewById(R.id.bt_connect_wifi_no);
        mEd_pwd = findViewById(R.id.ed_connect_wifi_pwd);
        //mEd_pwd .setImeOptions(EditorInfo.IME_ACTION_DONE);
        bt_yes.setOnClickListener(view -> connectWifi());
        bt_no.setOnClickListener(view -> finish());
        mImg_eyes = findViewById(R.id.img_eyes);
        mImg_eyes.setOnClickListener(this::hidePwd);
    }

    /**
     * hide show pwd
     *
     * @param view
     */
    private boolean pwc_tag = true;

    private void hidePwd(View view) {
        if (pwc_tag) {
            mImg_eyes.setImageResource(R.mipmap.eyes_close);
            pwc_tag = false;
            //mEd_pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mEd_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mEd_pwd.setSelection(mEd_pwd.getText().length());
        } else {
            mImg_eyes.setImageResource(R.mipmap.eyes_open);
            pwc_tag = true;
            //mEd_pwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            mEd_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            mEd_pwd.setSelection(mEd_pwd.getText().length());
        }

    }


    private void connectWifi() {
        /*WifiUtils.WifiType type = null;
        if (mScanResult.capabilities.toUpperCase().contains("WEP")) {
            type = WifiUtils.WifiType.WIFI_CIPHER_WEP;
        } else if (mScanResult.capabilities.toUpperCase().contains("WPA2")) {
            type = WifiUtils.WifiType.WIFI_CIPHER_WPA2;
        } else if (mScanResult.capabilities.toUpperCase().contains("WPA")) {
            type = WifiUtils.WifiType.WIFI_CIPHER_WPA;
        } else {
            type = WifiUtils.WifiType.WIFI_CIPHER_NOPASS;
        }
        String trim = mEd_pwd.getText().toString().trim();
        Log.i(TAG, "connectWifi: 输入密码=" + trim + "type=" + type.name());
        boolean connect = mWifiUtils.Connect(mScanResult.SSID, trim, type);
        Log.i(TAG, "connectWifi:---------------------------" + connect);*/
        String pwd = mEd_pwd.getText().toString().trim();
        WifiConfiguration configuration = configWifiInfo(this, mScanResult.SSID, pwd, getType(mScanResult));
        int netId = configuration.networkId;
        if (netId == -1) {
            netId = mWifiManager.addNetwork(configuration);
        }
        mWifiManager.enableNetwork(netId, true);
        mLoadBuilder.setBuilderMessage(1, "正在连接\"" + mScanResult.SSID + "\"");
        mLoading_dialog.show();
    }

    private WifiConfiguration  configWifiInfo(Context context, String ssid, String password, int type) {
        WifiConfiguration config = null;
        //WifiManager mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (mWifiManager != null) {
            List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig == null) continue;
                if (existingConfig.SSID.equals("\"" + ssid + "\"")  /*&&  existingConfig.preSharedKey.equals("\""  +  password  +  "\"")*/) {
                    config = existingConfig;
                    break;
                }
            }
        }
        if (config == null) {
            config = new WifiConfiguration();
        }
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";
        // 分为三种情况：0没有密码1用wep加密2用wpa加密
        if (type == 0) {// WIFICIPHER_NOPASSwifiCong.hiddenSSID = false;
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else if (type == 1) {  //  WIFICIPHER_WEP
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == 2) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private int getType(ScanResult scanResult) {
        int type;
        if (scanResult.capabilities.contains("WPA"))
            type = 2;
        else if (scanResult.capabilities.contains("WEP"))
            type = 1;
        else
            type = 0;
        return type;
    }

    /**
     * 密码错误 取消按钮
     * @param view
     */
    @Override
    public void onClickListener(View view) {
            //关闭dialog
        if (null!=mLoading_dialog){
            mLoading_dialog.dismiss();
        }
        int networkId = mWifiManager.getConnectionInfo().getNetworkId();
        boolean remove = mWifiUtils.remove(networkId);
        Log.i(TAG, "onClickListener:移除密码错误的wifi "+networkId+"---code="+remove);
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

    @Override
    protected void onStart() {
        super.onStart();
        show_tag = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWifiListener = null;
        Log.i(TAG, "onStop: ------------------");
        show_tag = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        show_tag = false;

    }

    private WifiUtils.wifiListener mWifiListener = new WifiUtils.wifiListener() {
        @Override
        public void disabled() {

        }

        @Override
        public void disabling() {

        }

        @Override
        public void enabled() {

        }

        @Override
        public void enabling() {

        }

        @Override
        public void unknown() {

        }

        @Override
        public void wifi_connected(String wifi_name) {
            if (show_tag && null != mLoading_dialog) {
                Log.i(TAG, "wifi_connected: 连接到name="+wifi_name);
                mLoading_dialog.dismiss();
                setResult(814);
                finish();
            }
        }

        @Override
        public void wifi_disconnect() {

        }

        @Override
        public void wifi_pwd_error() {
            if (show_tag && null != mLoading_dialog) {
                int type = mLoadBuilder.setBuilderMessage(2, "密码错误，连接失败。");
                if (type == 2 && !mLoading_dialog.isShowing()){
                    mLoading_dialog.show();
                }

            }
        }
    };

}
