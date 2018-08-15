package com.tangfeng.wifilist;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.suke.widget.SwitchButton;
import com.tangfeng.wifilist.adapter.WifiAdapter;
import com.tangfeng.wifilist.utils.WifiUtils;
import com.tangfeng.wifilist.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiUtils.wifiListener {

    private String TAG = "MainActivity";
    private List<ScanResult> mList = new ArrayList<>();
    private WifiUtils mWifiUtils;
    private WifiManager mWifiManager;
    private CustomDialog mCustomDialog;
    private SwitchButton mSw_bt;
    private WifiAdapter mWifiAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //获取wifi管理服务
        mWifiUtils = WifiUtils.getInstance();
        mWifiManager = mWifiUtils.initWifiManager(this);
        initView();
        //注册状态监听
        mWifiUtils.registerReceiver(this);
        //设置wifi状态监听
        mWifiUtils.setWifiListener(this);
        mWifiUtils.startScanWifi(mWifiManager);
        initData();
        mCustomDialog = new CustomDialog(this);
        mCustomDialog.setOnNoClickListener(new CustomDialog.onNoOnClickListener() {
            @Override
            public void onNoClick() {
                mCustomDialog.dismiss();
            }
        });
        mCustomDialog.setYesOnClickListener(new CustomDialog.onYesOnClickListener() {
            @Override
            public void onYesClick() {
                int networkId = mWifiManager.getConnectionInfo().getNetworkId();
                //断开网络
                mWifiUtils.disconnectNetwork(mWifiManager);
                //忘记网络
                mWifiUtils.remove(networkId);
                initData();
                mCustomDialog.dismiss();
            }
        });

    }

    private void initData() {
        List<ScanResult> scanResult = mWifiUtils.getScanResult(mWifiManager);
        Log.i(TAG, "initData: "+scanResult.toString());
        mList.clear();
        for (ScanResult scan : scanResult) {
            //Log.i(TAG, "onCreate:扫描到的网络 " + scan.toString());
            String ssid = scan.SSID;
            if (!ssid.equals(""))
                mList.add(scan);
        }
        mWifiAdapter.notifyDataSetChanged();
    }

    private void initView() {
        mSw_bt = (SwitchButton) findViewById(R.id.sw_bt);
        mSw_bt.setOnCheckedChangeListener(this::swListener);
        RecyclerView recycle_view = findViewById(R.id.recycle_view);
        recycle_view.setLayoutManager(new LinearLayoutManager(this));

        mWifiAdapter = new WifiAdapter(this,mList,mWifiManager);
        recycle_view.setAdapter(mWifiAdapter);
        mWifiAdapter.setOnItemClickListener(new WifiAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, ScanResult data) {
               // if (mWifiManager.getConnectionInfo().getSSID()!=null&& stringReplace(mWifiManager.getConnectionInfo().getSSID()).equals(data.SSID)){
                if (mWifiUtils.isConnected(data.SSID)){
                    mCustomDialog.show();
                    mCustomDialog.setInfo(data);
                }else {
                    Intent intent = new Intent(MainActivity.this, ConnectWifiActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("ScanResult",data);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,815);
                }

                Log.i(TAG, "onItemClick: name=" + data.SSID+"-------name="+mWifiManager.getConnectionInfo().getSSID());

            }
        });
        mWifiAdapter.setItemLongClickListener(new WifiAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, int position, ScanResult data) {
                Log.i(TAG, "onItemLongClick: name=" + data.SSID);
               // mCustomDialog.show();
            }
        });
        findViewById(R.id.iv_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initData();
            }
        });
    }

    public String stringReplace(String str) {
        //去掉" "号
        String str1 = str.replace("\"", "").trim();
        return str1;

    }

    private void swListener(SwitchButton view, boolean isChecked) {
        if (isChecked) {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
            }
        } else {
            if (mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(false);
            }
        }

        Log.i(TAG, "swListener: " + isChecked);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWifiUtils.unregisterReceiver(this);
    }
/**---------------------------------------wifi状态监听----------------------------------------------*/
    @Override
    public void disabled() {
        //已关闭
        if (mSw_bt.isChecked())
            mSw_bt.setChecked(false);
        Log.i(TAG, "disabled: 已关闭");
    }

    @Override
    public void disabling() {
        //正在关闭
        //initData();
        Log.i(TAG, "disabling:正在关闭 ");
    }

    @Override
    public void enabled() {
        //已启用
        if (!mSw_bt.isChecked())
            mSw_bt.setChecked(true);
        Log.i(TAG, "enabled: 已启用");
        //不延时 获取不到数据.
        try {
            Thread.sleep(300);
            initData();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enabling() {
        //启用中
        Log.i(TAG, "enabling: enabling");
    }

    @Override
    public void unknown() {
        //未知
        Log.i(TAG, "unknown: 未知");
    }

    @Override
    public void wifi_connected(String wifi_name) {
        initData();
        Log.i(TAG, "wifi_connected: 连接到网络"+wifi_name);
        //连接上wifi
    }

    @Override
    public void wifi_disconnect() {
            initData();
        Log.i(TAG, "wifi_disconnect: 断开wifi");
        //断开wifi
    }

    @Override
    public void wifi_pwd_error() {
        //密码错误.
        Log.i(TAG, "wifi_pwd_error: 密码错误");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 815 && resultCode ==814){
            initData();
            Log.i(TAG, "连接成功刷新列表:requestCode= "+requestCode+"--resultCode="+resultCode);
        }
    }
}
