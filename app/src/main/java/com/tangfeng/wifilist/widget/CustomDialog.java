package com.tangfeng.wifilist.widget;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.tangfeng.wifilist.R;

/**
 * Date :2018/8/2
 * Time :15:29
 * author:moyihen
 */

public class CustomDialog extends Dialog {
    private Context mContext;
    private onYesOnClickListener mOnYesOnClickListener;
    private onNoOnClickListener mOnNoOnClickListener;
    private Button mYes;
    private Button mNo;
    private TextView mCustom_dialog_title;
    private String titleStr;
    private TextView mWifi_state;
    private TextView mWifi_level;
    private TextView mWifi_speed;
    private TextView mWifi_hz;
    private TextView mWifi_type;


    /**------------------------------------------接口监听-------------------------------------------------------*/

    public interface onYesOnClickListener{
        void onYesClick();
    }

    public interface onNoOnClickListener{
        void onNoClick();
    }

    public void setYesOnClickListener(onYesOnClickListener onYesOnClickListener){
        this.mOnYesOnClickListener = onYesOnClickListener;
    }

    public void setOnNoClickListener (onNoOnClickListener onNoOnClickListener){
        this.mOnNoOnClickListener =onNoOnClickListener;
    }


    /**------------------------------------------构造方法-------------------------------------------------------*/
    public CustomDialog(@NonNull Context context) {
        super(context, R.style.MyDialog);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_layout);
        //按空白处不能取消动画
      //  setCanceledOnTouchOutside(false);

        initView();
        initData();
        initEvent();
    }

    private void initEvent() {
        mYes.setOnClickListener(view -> aa());
        mNo.setOnClickListener(view -> bb());
    }

    private void bb() {
        if (mOnNoOnClickListener!=null)
            mOnNoOnClickListener.onNoClick();
    }
    private void aa() {
        if (mOnYesOnClickListener!=null)
        mOnYesOnClickListener.onYesClick();
    }

    private void initData() {
        if (titleStr!=null)
            mCustom_dialog_title.setText(titleStr);
    }

    private void initView() {
        mYes = (Button) findViewById(R.id.yes);
        mNo = (Button) findViewById(R.id.no);
        mCustom_dialog_title = findViewById(R.id.custom_dialog_title);
        //连接
        mWifi_state = findViewById(R.id.custom_dialog_wifi_state);
       //强度
        mWifi_level = findViewById(R.id.custom_dialog_wifi_level);
        //速度
        mWifi_speed = findViewById(R.id.custom_dialog_wifi_speed);
       //频率
        mWifi_hz = findViewById(R.id.custom_dialog_wifi_hz);
       //加密类型
        mWifi_type = findViewById(R.id.custom_dialog_wifi_type);



        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER);//设置窗口位置
            dialogWindow.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口进出动画

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高
            if (lp != null) {
                lp.width = (int) (d.widthPixels * 0.95); // 设置为屏幕宽度
            }
            dialogWindow.setAttributes(lp);
        }
    }

    /**------------------------------------------外界set-------------------------------------------------------*/
    public void setTitle(String title){
        titleStr = title;
    }

    public void setInfo(ScanResult info){
        if (info==null)
            return;
        mCustom_dialog_title.setText(info.SSID);
        mWifi_state.setText("已连接");
        mWifi_level.setText(singlLevToStr(info.level));
        mWifi_speed.setText("贼快");
        mWifi_hz.setText(String.valueOf((float) info.frequency/1000)+"GHz");
        mWifi_type.setText(info.capabilities);
    }





    /**
     * Function:信号强度转换为字符串
     *
     * @author Xiho
     * @param level
     */
    public  String singlLevToStr(int level) {

        String resuString = "无信号";

        if (Math.abs(level) > 100) {
        } else if (Math.abs(level) > 80) {
            resuString = "弱";
        } else if (Math.abs(level) > 70) {
            resuString = "强";
        } else if (Math.abs(level) > 60) {
            resuString = "强";
        } else if (Math.abs(level) > 50) {
            resuString = "较强";
        } else {
            resuString = "极强";
        }
        return resuString;
    }
}
