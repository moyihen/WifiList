package com.tangfeng.wifilist.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.tangfeng.wifilist.R;
import com.tangfeng.wifilist.utils.WifiUtils;

import java.util.List;

/**
 * Date :2018/8/2
 * Time :8:59
 * author:moyihen
 */

public class WifiAdapter extends RecyclerView.Adapter<WifiAdapter.ViewHolder> {


    private  ConnectivityManager cm;
    private  WifiManager wifiManager;
    private  List<ScanResult> mList;
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mItemLongClickListener;
    private String TAG="WifiAdapter";

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.mOnItemClickListener = onItemClickListener;
    }
    public void setItemLongClickListener(OnItemLongClickListener onItemLongClickListener){
        this.mItemLongClickListener = onItemLongClickListener;
    }
    public WifiAdapter(Context context, List<ScanResult> list, WifiManager wifiManager) {
        this.mList = list;
        this.wifiManager=wifiManager;
        cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifi_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mName.setText(mList.get(position).SSID);
        // Wifi 描述
        String desc = "";
        String capabilities = mList.get(position).capabilities;
        if (capabilities.toUpperCase().contains("WPA-PSK"))
            desc = "WPA";
        if (capabilities.toUpperCase().contains("WPA2-PSK"))
            desc = "WPA2";
        if (capabilities.toUpperCase().contains("WPA-PSK") && capabilities.toUpperCase().contains("WPA2-PSK"))
            desc = "WPA/WPA2";

        int level = mList.get(position).level;
        setLevel(level,desc,holder);

        if (WifiUtils.getInstance().isConnected(mList.get(position).SSID)){
            desc = "已连接";
        }
        holder.mInfo.setText(desc);

        int adapterPosition = holder.getAdapterPosition();
        if (mOnItemClickListener !=null){
            holder.itemView.setOnClickListener(new MyOnClickListener(position,mList.get(adapterPosition)));
        }
        if (mItemLongClickListener!=null){
            holder.itemView.setOnLongClickListener(new MyOnLongClickListener(position,mList.get(adapterPosition)));
        }

    }
    //设置值网络强度
    private void setLevel(int level, String desc, ViewHolder holder) {

        if (TextUtils.isEmpty(desc)){
            //desc 为空 免密连接   不为空 密码连接
            //信号其强度
            if (TextUtils.isEmpty(desc)) {
                // desc = "未受保护的网络";
                // 网络信号强度
                int imgId=R.mipmap.wifi_level_4;
                if (Math.abs(level) > 100) {
                    imgId = R.mipmap.wifi_level_0;
                } else if (Math.abs(level) > 80) {
                    imgId = R.mipmap.wifi_level_1;
                } else if (Math.abs(level) > 70) {
                    imgId = R.mipmap.wifi_level_2;
                } else if (Math.abs(level) > 60) {
                    imgId = R.mipmap.wifi_level_3;
                } else if (Math.abs(level) > 50) {
                    imgId = R.mipmap.wifi_level_3;
                } else {
                    imgId = R.mipmap.wifi_level_4;
                }
                holder.mLevel.setImageResource(imgId);
            }
        }else {
            int imgId = R.mipmap.wifi_level_lock_4;
            if (Math.abs(level) > 100) {
                imgId = R.mipmap.wifi_level_0;
            } else if (Math.abs(level) > 80) {
                imgId = R.mipmap.wifi_level_lock_1;
            } else if (Math.abs(level) > 70) {
                imgId = R.mipmap.wifi_level_lock_2;
            } else if (Math.abs(level) > 60) {
                imgId = R.mipmap.wifi_level_lock_3;
            } else if (Math.abs(level) > 50) {
                imgId = R.mipmap.wifi_level_lock_3;
            } else {
                imgId = R.mipmap.wifi_level_lock_4;
            }
            holder.mLevel.setImageResource(imgId);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        private  TextView mName;
        private  TextView mInfo;
        private  ImageView mLevel;

        ViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.item_wifi_list_name);
            mInfo = itemView.findViewById(R.id.item_wifi_list_info);
            mLevel = itemView.findViewById(R.id.item_wifi_list_name_level);
        }
    }


    class MyOnClickListener implements View.OnClickListener {
        private  int position;
        private  ScanResult data;

        MyOnClickListener(int position, ScanResult data){
            this.position = position;
            this.data = data;
        }
        @Override
        public void onClick(View view) {
            mOnItemClickListener.onItemClick(view,position,data);
        }
    }

    class MyOnLongClickListener implements View.OnLongClickListener{
        private ScanResult data;
        private int position;

        public MyOnLongClickListener(int position, ScanResult data){
            this.position = position;
            this.data = data;
        }
        @Override
        public boolean onLongClick(View view) {
            mItemLongClickListener.onItemLongClick(view,position,data);
            return true;
        }
    }
    public interface OnItemClickListener{
        void onItemClick(View view,int position,ScanResult data);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position ,ScanResult data);
    }
}
