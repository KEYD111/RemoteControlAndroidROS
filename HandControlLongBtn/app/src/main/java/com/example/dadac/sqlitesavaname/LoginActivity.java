package com.example.dadac.sqlitesavaname;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.dadac.RCApplication;
import com.jilk.ros.ROSClient;
import com.jilk.ros.rosbridge.ROSBridgeClient;

import customView.PopupView;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /*欢迎界面的设计*/
    ROSBridgeClient client;
    private EditText DC_EditTextGetIP;
    private String IP = "192.168.2.119";
    private EditText DC_EditTextGetPort;
    private String port = "9090";
    private Button DC_Button_Connect;
    private boolean Flag_Connect = false;
    //String ip = "192.168.2.119";   //虚拟机的 IP
    // String ip = "192.168.10.20";     //半残废机器人的IP
    // String ip = "192.168.10.200";     //机器人的IP

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        InitMenuShow();
    }

    private void connect(String ip, String port) {
        client = new ROSBridgeClient("ws://" + ip + ":" + port);
        Flag_Connect = client.connect(new ROSClient.ConnectionStatusListener() {
            @Override
            public void onConnect() {
                client.setDebug(true);
                ((RCApplication) getApplication()).setRosClient(client);
                showTip("Connect ROS success");
                Log.d(IStatus.STATE_Log_Info, "Connect ROS success");
                if (Flag_Connect == true) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }

            @Override
            public void onDisconnect(boolean normal, String reason, int code) {
                Log.d(IStatus.STATE_Log_Info, "ROS Connect ERROR");
            }

            @Override
            public void onError(Exception ex) {
                ex.printStackTrace();

                showTip("ROS communication error");
                Log.d(IStatus.STATE_Log_Info, "ROS communication error");
            }
        });
//        if (Flag_Connect == false) {
//            PopupView.PopupWindow(getApplication());
//        }
    }

    private void showTip(final String tip) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoginActivity.this, tip, Toast.LENGTH_SHORT).show();
            }
        });
    }

    //初始化界面的设计
    private void InitMenuShow() {
        DC_EditTextGetIP = (EditText) findViewById(R.id.DC_EditTextGetIP);
        DC_EditTextGetPort = (EditText) findViewById(R.id.DC_EditTextGetPort);
        DC_Button_Connect = (Button) findViewById(R.id.DC_Button_Connect);
        DC_Button_Connect.setOnClickListener(this);
    }

    @Override  //按键的监听
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DC_Button_Connect:
                IP = DC_EditTextGetIP.getText().toString().trim();
                port = DC_EditTextGetPort.getText().toString().trim();
                connect(IP, port);
                break;
            default:
                break;
        }
    }

}
