package com.potato.wifiscanner;

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
    private final List<ScanResult> data;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_info_item, parent, false);
        }
        TextView textView = convertView.findViewById(R.id.wifi_name);
        if(Objects.equals(data.get(position).SSID,""))
            textView.setText("隐藏的WiFi" + "\t" + data.get(position).level);
        else
            textView.setText(data.get(position).SSID + "\t" + data.get(position).level);
        return convertView;
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
