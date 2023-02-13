package com.potato.wifiscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_scan = findViewById(R.id.button_scan);
        // 添加“扫描”按钮事件处理函数
        button_scan.setOnClickListener(v -> {
            //Permission Check
            WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  1);
                return;
            }
            List<ScanResult> results = wifiMgr.getScanResults(); // 获取扫描结果
            results.sort(Comparator.comparingInt(a -> -a.level)); // 对结果按照信号由强到弱排序
            // 修改列表
            ListView wifi_list = findViewById(R.id.wifi_list);
            wifi_list.setAdapter(new WifiInfoAdapter(results, MainActivity.this));

            // 为列表添加点击事件处理函数，切换到WiFi详情界面
            wifi_list.setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(MainActivity.this, wlan_detail_info.class).putExtra("WiFi_Info",  (ScanResult)(wifi_list.getAdapter().getItem(position)))));
        });
        //添加“清除”按钮事件处理函数
        Button button_clean = findViewById(R.id.button_clean);
        button_clean.setOnClickListener(v -> {
            ListView wifi_list = findViewById(R.id.wifi_list);
            WifiInfoAdapter wifiInfoAdapter = (WifiInfoAdapter) wifi_list.getAdapter();
            if (wifiInfoAdapter != null)
                wifiInfoAdapter.clear();
        });
    }
}