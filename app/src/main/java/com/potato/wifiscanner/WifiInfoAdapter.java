package com.potato.wifiscanner;

import android.content.Context;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.util.Log;
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
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.wifi_info_item, parent, false);
            viewHolder = new ViewHolder(convertView.findViewById(R.id.wifi_name));
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        TextView textView = convertView.findViewById(R.id.wifi_name);
        if(Objects.equals(data.get(position).SSID,""))
            viewHolder.getTextView().setText("隐藏的WiFi" + "\t" + data.get(position).level);
        else
            viewHolder.getTextView().setText(data.get(position).SSID + "\t" + data.get(position).level);
        return convertView;
    }

    private static class ViewHolder {
        private TextView textView;

        private ViewHolder(TextView textView) {
            this.textView = textView;
        }

        public TextView getTextView() {
            return textView;
        }

        public void setTextView(TextView textView) {
            this.textView = textView;
        }
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }
}
