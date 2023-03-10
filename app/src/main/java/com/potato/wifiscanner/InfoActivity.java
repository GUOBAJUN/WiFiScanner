package com.potato.wifiscanner;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class InfoActivity extends AppCompatActivity {

    TextView wifiLevel;
    TextView realTime;
    TextView updateTime;
    private WifiManager wifiMgr;
    private boolean isUpdated = false;
    long bootTime;
    @SuppressLint("SetTextI18n")
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                if (ActivityCompat.checkSelfPermission(InfoActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InfoActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  1);
                }
                List<ScanResult> results = wifiMgr.getScanResults();
                isUpdated = false;
                //更新WiFi强度
                boolean flag = false;
                ScanResult scanResult = InfoActivity.this.getIntent().getParcelableExtra("WiFi_Info");
                String target_ssid = scanResult.SSID;
                String target_bssid = scanResult.BSSID;
                for(ScanResult result : results) {
                    if (Objects.equals(result.SSID, target_ssid) && Objects.equals(result.BSSID, target_bssid)) {
                        wifiLevel.setText("level: " + result.level);
                        flag = true;
                        break;
                    }
                }
                if(!flag)
                    wifiLevel.setText("该WiFi已超出信号范围");
                updateTime.setText("Last update: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(new Date())));
            }
        }
    };
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        bootTime = System.currentTimeMillis()-SystemClock.elapsedRealtime();
        Intent intent = getIntent();
        wifiLevel = findViewById(R.id.wifi_ssid);
        ScanResult scanResult = intent.getParcelableExtra("WiFi_Info");
        String target_ssid = scanResult.SSID;
        wifiLevel.setText(target_ssid);
        wifiLevel = findViewById(R.id.wifi_level);
        wifiLevel.setText("level: " + scanResult.level);
        realTime = findViewById(R.id.real_time);
        updateTime = findViewById(R.id.update_time);
        updateTime.setText("Last update: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.CHINA).format(scanResult.timestamp/1000 + bootTime)));

        // 为“返回”按钮添加事件处理函数
        Button button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(v -> InfoActivity.this.finish());
        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(receiver, intentFilter);

        wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        //检查WiFi扫描列表是否更新
        TimerTask checkUpdate = new TimerTask() {
            @Override
            public void run() {
                if (!isUpdated)
                    isUpdated = true;
            }
        };

        //时间更新
        Handler handler = new Handler(msg -> {
            if(msg.what == 1) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                realTime.setText(dateFormat.format(new Date()));
            }
            return false;
        });
        TimerTask refresh = new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                msg.what = 1;
                handler.sendMessage(msg);
            }
        };

        Timer timer =new Timer();
        timer.schedule(refresh,0,1000);
        timer.schedule(checkUpdate, 0, 2000);
    }

}
