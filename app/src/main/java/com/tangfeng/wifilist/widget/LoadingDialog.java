package com.tangfeng.wifilist.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tangfeng.wifilist.R;

/**
 * Date :2018/8/14
 * Time :10:22
 * author:moyihen
 */

public class LoadingDialog extends Dialog {
    private Context mContext;


    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    public LoadingDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.mContext = context;
    }

    @Override
    public void show() {
        super.show();
        LoadingShow();
    }

    private void LoadingShow() {
        Window dialogWindow = getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM | Gravity.CENTER);//设置窗口位置
            dialogWindow.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口进出动画

            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            DisplayMetrics d =mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高
            if (lp != null) {
                lp.width = (int) (d.widthPixels * 0.9); // 设置为屏幕宽度
            }
            dialogWindow.setAttributes(lp);
        }
    }

    public static class Builder{
        private  Context context;
        private String message;
        private boolean isShowMessage=true;
        private boolean isCancelable=false;
        private boolean isCancelOutside=false;
        private TextView mTv_message;
        private LinearLayout mLl_message_success;
        private LinearLayout mLl_message_error;
        private TextView mTv_message_error;

        public Builder(Context context) {
            this.context =context;
        }

        /**
         * 设置提示信息
         * @param message
         * @return
         */
        public Builder setMessage(String message){
            this.message = message;
            return this;
        }

        /**
         * 设置是否显示提示信息
         * @param isShowMessage
         * @return
         */
        public Builder setShowMessage(boolean isShowMessage){
            this.isShowMessage = isShowMessage;
            return this;
        }

        /**
         * 设置是否可以按返回键取消
         * @param isCancelable
         * @return
         */
        public Builder setCancelable(boolean isCancelable){
            this.isCancelable = isCancelable;
            return this;
        }

        public Builder setCancelOutside(boolean isCancelOutside){
            this.isCancelOutside = isCancelOutside;
            return this;
        }

        /**
         * 设置显示属性
         * @param type 1正确    2错误
         * @param message 显示信息.
         */
        public int setBuilderMessage(int type ,String message){
            if (null!=mTv_message){
                if (type ==1){
                    mLl_message_success.setVisibility(View.VISIBLE);
                    mLl_message_error.setVisibility(View.GONE);
                    mTv_message.setText(message);
                    return  type;
                }else if (type==2){
                    mLl_message_success.setVisibility(View.GONE);
                    mLl_message_error.setVisibility(View.VISIBLE);
                    mTv_message_error.setText(message);
                    return type;
                }

            }
            return -1;
        }


        public LoadingDialog create(){
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.dialog_loading, null);
            LoadingDialog dialog = new LoadingDialog(context, R.style.MyDialog);
            mLl_message_success = view.findViewById(R.id.ll_message_success);
            mTv_message = view.findViewById(R.id.tv_message);

            mLl_message_error = view.findViewById(R.id.ll_message_error);
            mTv_message_error = view.findViewById(R.id.tv_message_error);
            Button bt_message_close = view.findViewById(R.id.bt_message_close);
            bt_message_close.setOnClickListener(view1 ->mCloseListener.onClickListener(view1));
            view.findViewById(R.id.bt_message_close);
            if (isShowMessage){
                mTv_message.setText(message);
            }else {
                mTv_message.setVisibility(View.GONE);
            }
            dialog.setContentView(view);
            dialog.setCancelable(isCancelable);
            dialog.setCanceledOnTouchOutside(isCancelOutside);

            return dialog;
        }
        /**---------------------------------------关闭按钮处理----------------------------------------*/
        private CloseListener mCloseListener;
        public interface CloseListener{
            void onClickListener(View view);
        }

        public void setCloseOnClickListener(CloseListener closeListener){
            this.mCloseListener =closeListener;
        }
    }

}
