package com.potato.wifiscanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

public class WifiInfoAdapter extends BaseAdapter {
    private List<ScanResult> data;
    private final Context context;

    public WifiInfoAdapter(List<ScanResult> data, Context context) {
        this.data = data;
        this.context = context;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_info_item, parent, false);
            viewHolder = new ViewHolder(convertView.findViewById(R.id.wifi_name), convertView.findViewById(R.id.wifi_strength));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(Objects.equals(data.get(position).SSID,""))
            viewHolder.getTv_ssid().setText("隐藏的WiFi");
        else
            viewHolder.getTv_ssid().setText(data.get(position).SSID);
        viewHolder.getTv_level().setText(String.valueOf(data.get(position).level));
        return convertView;
    }

    private static class ViewHolder {
        private final TextView tv_ssid;
        private final TextView tv_level;
        private ViewHolder(TextView textView, TextView tv_level) {
            this.tv_ssid = textView;
            this.tv_level = tv_level;
        }
        public TextView getTv_ssid() {
            return tv_ssid;
        }
        public TextView getTv_level() {
            return tv_level;
        }

    }
    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void setData(List<ScanResult> data) {
        this.data = data;
        notifyDataSetChanged();
    }
}
