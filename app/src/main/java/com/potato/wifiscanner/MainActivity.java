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
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_scan = findViewById(R.id.button_scan);
        WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        AtomicReference<ListView> wifi_list = new AtomicReference<>(findViewById(R.id.wifi_list));
        Timer timer = new Timer();

        Handler handler = new Handler(msg -> {
            if(msg.what == 1) {
                //Permission Check
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  1);
                }
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "无定位权限，无法获取WiFi信息", Toast.LENGTH_SHORT).show();
                    return false;
                }
                if (!wifiMgr.isWifiEnabled()) {
                    Toast.makeText(MainActivity.this, "请打开WiFi开关以进行扫描", Toast.LENGTH_SHORT).show();
                    return false;
                }
                button_scan.setText("刷新");
                List<ScanResult> results = wifiMgr.getScanResults();
                results.sort(Comparator.comparingInt(a -> -a.level));
                wifi_list.set(findViewById(R.id.wifi_list));
                if (wifi_list.get().getAdapter() == null)
                    wifi_list.get().setAdapter(new WifiInfoAdapter(results, MainActivity.this));
                else
                    ((WifiInfoAdapter)wifi_list.get().getAdapter()).setData(results);
            }
            return false;
        });
        AtomicReference<TimerTask> refresh = new AtomicReference<>();

        // 添加“扫描”按钮事件处理函数
        button_scan.setOnClickListener(v -> {
            if(Objects.equals(button_scan.getText(), "刷新")) {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
                return;
            }
            refresh.set(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            });
            timer.schedule(refresh.get(), 0 ,2000);

        });

        //添加“清除”按钮事件处理函数
        Button button_clean = findViewById(R.id.button_clean);
        button_clean.setOnClickListener(v -> {
            if(Objects.equals(button_scan.getText(), "扫描"))
                return;
            refresh.get().cancel();
            button_scan.setText("扫描");
            wifi_list.set(findViewById(R.id.wifi_list));
            WifiInfoAdapter wifiInfoAdapter = (WifiInfoAdapter) wifi_list.get().getAdapter();
            if (wifiInfoAdapter != null)
                wifiInfoAdapter.clear();
        });

        // 为列表添加点击事件处理函数，切换到WiFi详情界面
        wifi_list.get().setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(MainActivity.this, wlan_detail_info.class).putExtra("WiFi_Info",  (ScanResult)(wifi_list.get().getAdapter().getItem(position)))));
    }
}