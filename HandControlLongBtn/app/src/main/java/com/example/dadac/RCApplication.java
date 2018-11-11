package com.example.dadac;

import android.app.Application;

import com.jilk.ros.rosbridge.ROSBridgeClient;

import org.xutils.x;


/**
 * @ Create by dadac on 2018/9/27.
 * @Function:  供给SQLite使用
 * @Return:
 */
public class RCApplication extends Application {
    //ROS
    ROSBridgeClient client;
    @Override
    public void onCreate() {
        super.onCreate();
        //EventBus  xUtils
        x.Ext.init(this);
        x.Ext.setDebug(false);   //输出debug日志，开启会影响性能
    }

    @Override  //ROS
    public void onTerminate() {
        if(client != null)
            client.disconnect();
        super.onTerminate();
    }

    public ROSBridgeClient getRosClient() {
        return client;
    }

    public void setRosClient(ROSBridgeClient client) {
        this.client = client;
    }
}
