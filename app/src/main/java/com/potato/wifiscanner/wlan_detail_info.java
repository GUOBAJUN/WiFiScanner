package com.potato.wifiscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Date;

public class wlan_detail_info extends AppCompatActivity {

    TextView wifiLevel;
    TextView realTime;
    private WifiManager wifiMgr;
    private boolean isUpdated = false;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (ActivityCompat.checkSelfPermission(wlan_detail_info.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(wlan_detail_info.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  1);
                    return;
                }
                List<ScanResult> results = wifiMgr.getScanResults();
                isUpdated = false;
                //更新WiFi强度
                boolean flag = false;
                String targetSSID = wlan_detail_info.this.getIntent().getStringExtra("SSID");
                for(ScanResult result : results) {
                    if (Objects.equals(result.SSID, targetSSID)) {

                        wifiLevel.setText("level: " + result.level);
                        flag = true;
                        break;
                    }
                }
                if(!flag)
                    wifiLevel.setText("该WiFi已超出信号范围");
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_wlan_info);
        Intent intent = getIntent();
        wifiLevel = findViewById(R.id.wifi_ssid);
        String target_ssid = intent.getStringExtra("SSID");
        wifiLevel.setText(target_ssid);
        wifiLevel = findViewById(R.id.wifi_level);
        realTime = findViewById(R.id.real_time);

        // 为“返回”按钮添加事件处理函数
        Button button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wlan_detail_info.this.finish();
            }
        });
        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, intentFilter);

        wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //检查WiFi扫描列表是否更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isUpdated) {
                    wifiMgr.startScan(); // 旧版api，不知道新版Android怎么出发WiFi扫描
                    isUpdated = true;
                    try {
                        Thread.sleep(2000);
                    }catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();

        //时间更新
        Handler handler = new Handler(msg -> {
            if(msg.what == 1) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                realTime.setText(dateFormat.format(new Date()));
            }
            return false;
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Message msg = handler.obtainMessage();
                    msg.what = 1;
                    handler.sendMessage(msg);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
        
    }

}