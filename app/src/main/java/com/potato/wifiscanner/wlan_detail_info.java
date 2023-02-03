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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Date;

public class wlan_detail_info extends AppCompatActivity {

    Handler handler;
    TextView textView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_wlan_info);
        Intent intent = getIntent();
        textView = findViewById(R.id.wifi_ssid);
        String target_ssid = intent.getStringExtra("SSID");
        textView.setText(target_ssid);
        textView = findViewById(R.id.wifi_level);

        // 为“返回”按钮添加事件处理函数
        Button button_back = findViewById(R.id.button_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wlan_detail_info.this.finish();
            }
        });

        //动态刷新信号强度和时间戳
        handler = new Handler(msg -> {
            if(msg.what == 1) {
                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                textView.setText(msg.getData().getInt("level")+"\t" + dateFormat.format(date));
            }
            return false;
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent intent = getIntent();
                String target_ssid = intent.getStringExtra("SSID");
                WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                if (ActivityCompat.checkSelfPermission(wlan_detail_info.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(wlan_detail_info.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},  1);
                    return;
                }
                List<ScanResult> results;
                while (true) {
                    try {
                        Message msg = handler.obtainMessage();
                        msg.what=1;
                        Bundle bundle = new Bundle();
                        results = wifiMgr.getScanResults();
                        for(int i = 0 ; i < results.size(); i++) {
                            if (Objects.equals(results.get(i).SSID, target_ssid)) {
                                bundle.putInt("level",results.get(i).level);
                                break;
                            }
                        }
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    }catch (InternalError | InterruptedException e){
                            throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }

}
