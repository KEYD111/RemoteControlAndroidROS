package com.example.dadac.sqlitesavaname;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.dadac.RCApplication;
import com.example.dadac.savenavigationinfo.SaveData;
import com.fragment.SetNavigationFragemnt;
import com.fragment.ShowRobotLocationFragment;
import com.jilk.ros.rosbridge.ROSBridgeClient;

import java.util.Timer;
import java.util.TimerTask;

import customView.BatteryView;
import de.greenrobot.event.EventBus;
import utils.SerialUtils;


/**
 * @ Create by dadac on 2018/10/30.
 * @Function:
 * @Return:
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    /* Fragemnt 的切换*/
    private Button DC_Button_SetNavLocation;
    private Button DC_Button_ShowRobot;
    //定义Fragment管理器
    public FragmentManager fragmentManager;
    private SetNavigationFragemnt setNavigationFragemnt;
    private ShowRobotLocationFragment showRobotLocationFragment;

    /*ROS端的数据*/
    ROSBridgeClient client;
    //惯导的角度信息  保留一个比较值   ROSAngle为当前的角度值 初始值为 0
    private String angle_imu_for_xf = null;
    private static String save_angle_imu_for_xf = "181";
    private static volatile int ROSAngle = 0;

    //电量和充放电 0  放电   1充电 powers[0] 电量数据  power[1] 电量方向
    private static int powerData_number = 102;
    private static int powerData_dir = 3;

    //检测到人体
    private boolean Flag_DetectPeople;
    private static boolean save_Flag_DetectPeople = false;

    /*界面初始化*/
    private TextView DC_TextViewShowElectricPercentage;
    private BatteryView DC_Custom_Battery_view;
    private ImageButton DC_ImgBtn_Up;
    private ImageButton DC_ImgBtn_Down;
    private ImageButton DC_ImgBtn_Left;
    private ImageButton DC_ImgBtn_Right;

    /*导航点的设置*/
    private Button DC_Button_Navi1;
    private Button DC_Button_Navi2;
    private Button DC_Button_Navi3;
    private Button DC_Button_Navi4;
    private Button DC_Button_Navi5;
    private Button DC_Button_CancelNavi;
    private Button DC_Button_NaviPatrol;

    /*获取导航点  以 5个导航点为例子*/
    private double[] GetNaviPos_x;
    private double[] GetNaviPos_y;
    private double[] GetNaviOri_z;
    private double[] GetNaviOri_w;

    //保存当前的导航点
    private static double pos_x = 0.0;
    private static double pos_y = 0.0;
    private static double ori_z = 0.0;
    private static double ori_w = 0.0;

    View main;

    //数据库
    SaveData saveData;
    // 到达导航点的标志位
    private static String NaviIndexRes = "a";  //"Goal reached."
    private static int SelectNavi = 0;    //按键触发选择导航点
    private static boolean BtnInvokeNavi = false;   //按键激活的标志位
    private static int ArriveNaviNumber = 0;     //跑了第几圈
    //巡逻的标志位 巡逻的次数、 计数
    private static int PatrolNumberCount = 0;
    private static boolean FlagPatrol = false;
    //到达导航点的标志位
    private static boolean ArriveNavi1 = false;
    private static boolean ArriveNavi2 = false;
    private static boolean ArriveNavi3 = false;
    private static boolean ArriveNavi4 = false;
    private static boolean ArriveNavi5 = false;
    private static int saveAllPatrolNumbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = getLayoutInflater().from(this).inflate(R.layout.activity_main, null);
        main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        main.setOnClickListener(this);
        setContentView(main);
        getSupportActionBar().hide();
        // Fragment中不能使用，只能讲数据传递过去
        EventBus.getDefault().register(this);  //必须注册 ROS端的处理  除非重新创建
        //setContentView(R.layout.activity_main);
        //获取ROS 端的实例
        client = ((RCApplication) getApplication()).getRosClient();
        // 创建数据库
        saveData = new SaveData();
        saveData.InitDaoConfig(MainActivity.this);
        // saveData.CreateSQLData(14);
        // saveData.deletetable();
        //saveData.deleteSQL();
        /*当第一次创建表的时候，添加数据*/
        String a = saveData.QuerySQLData(1).toString();
        if (a.equals("null")) {
            saveData.CreateSQLData(14);
        }
        //        for (int i = 0; i < saveData.GetSQLALLID().length; i++) {
        //            Log.i("dachen", saveData.GetSQLALLID()[i] + "");
        //            Log.i("dachen", saveData.GetSQLALLName()[i]);
        //        }

        //获取Fragment的实例
        fragmentManager = getSupportFragmentManager();
        //隐藏底部导航栏
        hideBottomUIMenu();
        //假装这是开机默认显示的Fragment
        selectWhichFragmentToShow(IStatus.STATE_Fragement_ShowRobot);
        InitMainMenuShow();

        //初始化导航点
        String msg_startnav = "{\"op\":\"publish\",\"topic\":\"/ROS_START_NAV\",\"msg\":{\"data\":\"startnav\"}}";
        try {
            client.send(msg_startnav);
        } catch (Exception e) {
            Log.d("weizhicuowu", "未知错误");
        }
        //向ROS端订阅数据
        SubscribleRos();
        //电量的显示
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                myhandler.sendEmptyMessage(IStatus.STATE_Message_ShowPowerView);
            }
        }, 0, 700);

        //传递数据给Fragment
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                myhandler.sendEmptyMessage(IStatus.STATE_Message_Act2FraContent);
            }
        }, 0, 500);

        //获取数据库存储的导航点
        GetTheSQLPosi();
    }

    /**
     * @Function:获取数据库存储的导航点 暂时定5个导航点   多的没什么必要
     * @Return:
     */
    private void GetTheSQLPosi() {
        GetNaviPos_x = new double[5];
        GetNaviPos_y = new double[5];
        GetNaviOri_z = new double[5];
        GetNaviOri_w = new double[5];
        for (int i = 1; i <= 5; i++) {
            GetNaviPos_x[i - 1] = Double.parseDouble(((JSONObject.parseObject(saveData.QuerySQLData(i))).get("x_position")).toString());
            GetNaviPos_y[i - 1] = Double.parseDouble(((JSONObject.parseObject(saveData.QuerySQLData(i))).get("y_position")).toString());
            GetNaviOri_z[i - 1] = Double.parseDouble(((JSONObject.parseObject(saveData.QuerySQLData(i))).get("z_orientation")).toString());
            GetNaviOri_w[i - 1] = Double.parseDouble(((JSONObject.parseObject(saveData.QuerySQLData(i))).get("w_orientation")).toString());
        }
    }


    //向ROS端订阅数据
    private void SubscribleRos() {
        //        //获取电量信息
        //        String RosMegPower = "{\"op\":\"subscribe\",\"topic\":\"/power\"}";
        //        client.send(RosMegPower);
        //        //获取惯导唤醒信息
        //        String RosMegAngleMoveDetailName = "/angle_imu_for_xf";
        //        String RosMegAngleMove = "{\"op\":\"subscribe\",\"topic\":\"" + RosMegAngleMoveDetailName + "\"}";
        //        client.send(RosMegAngleMove);
        //获取地图
        //        String RosMegMapDetailName = "/map";
        //        String RosMegMap = "{\"op\":\"subscribe\",\"topic\":\"" + RosMegMapDetailName + "\"}";
        //        client.send(RosMegMap);
        //获取当前位置
        String RosMegCurrentPoseDetailName = "/amcl_pose";
        String RosMegCurrentPost = "{\"op\":\"subscribe\",\"topic\":\"" + RosMegCurrentPoseDetailName + "\"}";
        client.send(RosMegCurrentPost);
        //        //到达航点的结果
        String RosMegMoveResultDetailName = "/move_base/result";
        String RosMegMoveResult = "{\"op\":\"subscribe\",\"topic\":\"" + RosMegMoveResultDetailName + "\"}";
        client.send(RosMegMoveResult);
        //        //检测到人
        //        String RosMegPeopleDetectDetailName = "/Dart_ROS_People_detect";
        //        String RosMegPeopleDetect = "{\"op\":\"subscribe\",\"topic\":\"" + RosMegPeopleDetectDetailName + "\"}";
        //        client.send(RosMegPeopleDetect);
    }

    /**
     * @Function: 接受订阅的消息
     * @Return:
     */
    public void onEvent(final PublishEvent event) {
        //        //接受电量信息
        //        if ("/power".equals(event.name)) {
        //            parsePowerInfo(event);
        //            return;
        //        }
        //        //获取惯导的角度信息
        //        if ("/angle_imu_for_xf".equals(event.name)) {
        //            parseAngle_imu_for_xf(event);
        //            return;
        //        }
        //获取地图
        //        if ("/map".equals(event.name)) {
        //            parseMapTopic(event);
        //            return;
        //        }
        //        //获取当前位置
        if ("/amcl_pose".equals(event.name)) {
            parseGetCurrentPosition(event);
            return;
        }
        //到达航点的结果
        if ("/move_base/result".equals(event.name)) {
            parseMoveResult(event);
            return;
        }
        //        //检测到人
        //        if ("/Dart_ROS_People_detect".equals(event.name)) {
        //            parseDetectBody(event);
        //            return;
        //        }
    }

    /**
     * @Function: 地图的显示
     * @Return:
     */
    private void parseMapTopic(PublishEvent event) {

    }

    /**
     * @Function: 有没有检测到人体
     * @Return:
     */
    private void parseDetectBody(PublishEvent event) {
        JSONObject jsonObject = JSONObject.parseObject(event.msg);
        Flag_DetectPeople = jsonObject.getBoolean("data");
        if (save_Flag_DetectPeople != Flag_DetectPeople) {
            save_Flag_DetectPeople = Flag_DetectPeople;
            Log.i(IStatus.STATE_Log_Info + "People_Detect", String.valueOf(save_Flag_DetectPeople));
        }
    }

    /**
     * @Function: 到达航点的结果
     * @Return:
     */


    private void parseMoveResult(PublishEvent event) {
        JSONObject jsonObject1 = JSONObject.parseObject(event.msg);
        JSONObject jsonObject2 = jsonObject1.getJSONObject("status");
        String NaviIndexRes = (String) jsonObject2.getString("text");
        if (NaviIndexRes.equals("Goal reached.")) {
            if ((ArriveNavi1 == true) || (ArriveNavi2 == true) || (ArriveNavi3 == true) || (ArriveNavi4 == true) || (ArriveNavi5 == true)) {
                if (ArriveNavi1) {  //到达导航点1
                    Log.i(IStatus.STATE_Log_Info + "到达导航点:", "我已经到达： " + saveData.GetSQLALLName()[0]);
                    ArriveNavi1 = false;
                }
                if (ArriveNavi2) { //到达导航点2
                    Log.i(IStatus.STATE_Log_Info + "到达导航点:", "我已经到达：" + saveData.GetSQLALLName()[1]);
                    ArriveNavi2 = false;
                }
                if (ArriveNavi3) { //到达导航点3
                    Log.i(IStatus.STATE_Log_Info + "到达导航点:", "我已经到达：" + saveData.GetSQLALLName()[2]);
                    ArriveNavi3 = false;
                }
                if (ArriveNavi4) { //到达导航点4
                    Log.i(IStatus.STATE_Log_Info + "到达导航点:", "我已经到达：" + saveData.GetSQLALLName()[3]);
                    ArriveNavi4 = false;
                }
                if (ArriveNavi5) { //到达导航点5
                    Log.i(IStatus.STATE_Log_Info + "到达导航点:", "我已经到达：" + saveData.GetSQLALLName()[4]);
                    ArriveNavi5 = false;
                }
            }
            //手动挡
            Log.i(IStatus.STATE_Log_Info + "到达导航点的标志:", NaviIndexRes);
            if (FlagPatrol == false && BtnInvokeNavi == true) {  //手动触发
                Log.i(IStatus.STATE_Log_Info + "导航", "手动挡");
                if (SelectNavi == 1) {
                    Log.i(IStatus.STATE_Log_Info + "手动去导航点:", SelectNavi + " Name:" + saveData.GetSQLALLName()[0]);
                    BtnInvokeNavi = false;   //手动挡取消
                    ArriveNavi1 = true;     //到达第一个导航点
                }
                if (SelectNavi == 2) {
                    Log.i(IStatus.STATE_Log_Info + "手动去导航点:", SelectNavi + " Name:" + saveData.GetSQLALLName()[1]);
                    BtnInvokeNavi = false;
                    ArriveNavi2 = true;
                }
                if (SelectNavi == 3) {
                    Log.i(IStatus.STATE_Log_Info + "手动去导航点:", SelectNavi + " Name:" + saveData.GetSQLALLName()[2]);
                    BtnInvokeNavi = false;
                    ArriveNavi3 = true;
                }
                if (SelectNavi == 4) {
                    Log.i(IStatus.STATE_Log_Info + "手动去导航点:", SelectNavi + " Name:" + saveData.GetSQLALLName()[3]);
                    BtnInvokeNavi = false;
                    ArriveNavi3 = true;
                }
                if (SelectNavi == 5) {
                    Log.i(IStatus.STATE_Log_Info + "手动去导航点:", SelectNavi + " Name:" + saveData.GetSQLALLName()[4]);
                    BtnInvokeNavi = false;
                    ArriveNavi4 = true;
                }
            }
            if (FlagPatrol == true && BtnInvokeNavi == false) {  //自动巡逻
                Log.i(IStatus.STATE_Log_Info + "导航", "自动挡" + PatrolNumberCount + "圈 " + " 第 " + (PatrolNumberCount - PatrolNumberCount) + 1 + " 趟");
                ArriveNaviNumber++;
                if (PatrolNumberCount > 0) {
                    if (ArriveNaviNumber == 1) {  //起点
                        Log.i(IStatus.STATE_Log_Info + "自动去导航点", ArriveNaviNumber + ":" + " Name:" + saveData.GetSQLALLName()[0] + "Going");
                        myhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ArriveNavi1 = true;
                                Message msgnav1 = new Message();
                                msgnav1.what = IStatus.STATE_Message_Nav1;
                                myhandler.sendMessage(msgnav1);
                            }
                        }, 1000);
                    }
                    if (ArriveNaviNumber == 2) {   //2号窗口
                        Log.i(IStatus.STATE_Log_Info + "自动去导航点", ArriveNaviNumber + ":" + " Name:" + saveData.GetSQLALLName()[1] + "Going");
                        myhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ArriveNavi2 = true;
                                Message msgnav2 = new Message();
                                msgnav2.what = IStatus.STATE_Message_Nav2;
                                myhandler.sendMessage(msgnav2);
                            }
                        }, 1000);
                    }
                    if (ArriveNaviNumber == 3) {   //3号窗口
                        Log.i(IStatus.STATE_Log_Info + "自动去导航点", ArriveNaviNumber + ":" + " Name:" + saveData.GetSQLALLName()[2] + "Going");
                        myhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ArriveNavi3 = true;
                                Message msgnav3 = new Message();
                                msgnav3.what = IStatus.STATE_Message_Nav3;
                                myhandler.sendMessage(msgnav3);
                            }
                        }, 1000);
                    }
                    if (ArriveNaviNumber == 4) {   //4号窗口
                        Log.i(IStatus.STATE_Log_Info + "自动去导航点", ArriveNaviNumber + ":" + " Name:" + saveData.GetSQLALLName()[3] + "Going");
                        myhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ArriveNavi4 = true;
                                Message msgnav4 = new Message();
                                msgnav4.what = IStatus.STATE_Message_Nav4;
                                myhandler.sendMessage(msgnav4);
                            }
                        }, 1000);
                    }
                    if (ArriveNaviNumber == 5) {  //终点
                        Log.i(IStatus.STATE_Log_Info + "自动去导航点", ArriveNaviNumber + ":" + " Name:" + saveData.GetSQLALLName()[4] + "Going");
                        myhandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ArriveNavi5 = true;
                                Message msgnav5 = new Message();
                                msgnav5.what = IStatus.STATE_Message_Nav5;
                                myhandler.sendMessage(msgnav5);
                            }
                        }, 1000);
                    }
                    if (ArriveNaviNumber > 5) {
                        PatrolNumberCount--;
                        ArriveNaviNumber = 0;
                        ArriveNavi1 = true;
                        Message msgnav1 = new Message();
                        msgnav1.what = IStatus.STATE_Message_Nav1;
                        myhandler.sendMessage(msgnav1);
                    }
                } else {
                    FlagPatrol = false;
                    BtnInvokeNavi = false;
                }
            }
        }
    }

    /**
     * @Function: 获取当前的位置
     * @Return:
     */
    private void parseGetCurrentPosition(PublishEvent event) {
        JSONObject jsonObject = JSONObject.parseObject(event.msg);
        JSONObject jsonObject2 = jsonObject.getJSONObject("pose");
        JSONObject jsonObject21 = jsonObject2.getJSONObject("pose");
        JSONObject jsonObjectOrientation = jsonObject21.getJSONObject("orientation");
        JSONObject jsonObjectPosition = jsonObject21.getJSONObject("position");
        String x1 = jsonObjectPosition.getString("x");
        String y1 = jsonObjectPosition.getString("y");
        String z1 = jsonObjectOrientation.getString("z");
        String w1 = jsonObjectOrientation.getString("w");
        pos_x = Double.parseDouble(x1);
        pos_y = Double.parseDouble(y1);
        ori_z = Double.parseDouble(z1);
        ori_w = Double.parseDouble(w1);
        //将 x y z w 定义成全局变量即可*/
        Log.i(IStatus.STATE_Log_Info + "CurrentPose:", "x" + pos_x + "---y" + pos_y + "---z" + ori_z + "----w" + ori_w);
    }


    /**
     * @Function: 获取惯导的角度信息
     * @Return:
     */
    private void parseAngle_imu_for_xf(PublishEvent event) {
        JSONObject jsonObjectAngle = JSONObject.parseObject(event.msg);
        angle_imu_for_xf = (String) jsonObjectAngle.get("data");
        //保留小数点后两位
        if (SerialUtils.savePointEndNumberFloat(save_angle_imu_for_xf, "2") != SerialUtils.savePointEndNumberFloat
                (angle_imu_for_xf, "2")) {
            //角度值不变化 不接受数据
            save_angle_imu_for_xf = angle_imu_for_xf;
            ROSAngle = (int) Float.parseFloat(String.valueOf(SerialUtils.savePointEndNumberFloat(angle_imu_for_xf, "0")));
            Log.i(IStatus.STATE_Log_Info + "ROSAngle", ROSAngle + "");
            //  Log.i(IStatus.STATE_Log_Info + "Start", "开始旋转");
        }
    }


    /**
     * @Function: 获取电量信息
     * @Return: 0   毫安时  1 电量   2     3 电压    4：0 放电 1充电   5 电流的大小
     */
    private void parsePowerInfo(PublishEvent event) {
        //还是阿里的包好用
        JSONObject jsonObject = JSONObject.parseObject(event.msg);
        String power_ah = (String) jsonObject.getString("power_ah");
        String power = (String) jsonObject.getString("power");
        String power_tem = (String) jsonObject.getString("power_tem");
        String power_v = (String) jsonObject.getString("power_v");
        String power_dir = (String) jsonObject.getString("power_dir");
        String power_i = (String) jsonObject.getString("power_i");
        if (powerData_number + "" != power || powerData_dir + "" != power_dir) {
            powerData_number = Integer.parseInt(power);
            powerData_dir = Integer.parseInt(power_dir);
            Log.i(IStatus.STATE_Log_Info + "powerNumber", powerData_number + "");
            Log.i(IStatus.STATE_Log_Info + "powerdir", powerData_dir + "");
        }
    }


    //界面的初始化
    private void InitMainMenuShow() {
        DC_TextViewShowElectricPercentage = (TextView) findViewById(R.id.DC_TextViewShowElectricPercentage);
        DC_Custom_Battery_view = (BatteryView) findViewById(R.id.DC_Custom_Battery_view);
        DC_ImgBtn_Up = (ImageButton) findViewById(R.id.DC_ImgBtn_Up);
        //  DC_ImgBtn_Up.setOnClickListener(this);
        DC_ImgBtn_Up.setOnTouchListener(new ComponentOnLongTouch());
        DC_ImgBtn_Down = (ImageButton) findViewById(R.id.DC_ImgBtn_Down);
        //  DC_ImgBtn_Down.setOnClickListener(this);
        DC_ImgBtn_Down.setOnTouchListener(new ComponentOnLongTouch());
        DC_ImgBtn_Left = (ImageButton) findViewById(R.id.DC_ImgBtn_Left);
        //  DC_ImgBtn_Left.setOnClickListener(this);
        DC_ImgBtn_Left.setOnTouchListener(new ComponentOnLongTouch());
        DC_ImgBtn_Right = (ImageButton) findViewById(R.id.DC_ImgBtn_Right);
        // DC_ImgBtn_Right.setOnClickListener(this);
        DC_ImgBtn_Right.setOnTouchListener(new ComponentOnLongTouch());
        DC_Button_SetNavLocation = (Button) findViewById(R.id.DC_Button_SetNavLocation);
        DC_Button_SetNavLocation.setOnClickListener(this);
        DC_Button_ShowRobot = (Button) findViewById(R.id.DC_Button_ShowRobot);
        DC_Button_ShowRobot.setOnClickListener(this);
        /*导航点的设置*/
        DC_Button_Navi1 = (Button) findViewById(R.id.DC_Button_Navi1);
        DC_Button_Navi1.setText(saveData.GetSQLALLName()[0]);
        DC_Button_Navi1.setOnClickListener(this);
        DC_Button_Navi2 = (Button) findViewById(R.id.DC_Button_Navi2);
        DC_Button_Navi2.setText(saveData.GetSQLALLName()[1]);
        DC_Button_Navi2.setOnClickListener(this);
        DC_Button_Navi3 = (Button) findViewById(R.id.DC_Button_Navi3);
        DC_Button_Navi3.setText(saveData.GetSQLALLName()[2]);
        DC_Button_Navi3.setOnClickListener(this);
        DC_Button_Navi4 = (Button) findViewById(R.id.DC_Button_Navi4);
        DC_Button_Navi4.setText(saveData.GetSQLALLName()[3]);
        DC_Button_Navi4.setOnClickListener(this);
        DC_Button_Navi5 = (Button) findViewById(R.id.DC_Button_Navi5);
        DC_Button_Navi5.setText(saveData.GetSQLALLName()[4]);
        DC_Button_Navi5.setOnClickListener(this);
        DC_Button_CancelNavi = (Button) findViewById(R.id.DC_Button_CancelNavi);
        DC_Button_CancelNavi.setOnClickListener(this);
        DC_Button_NaviPatrol = (Button) findViewById(R.id.DC_Button_NaviPatrol);
        DC_Button_NaviPatrol.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //触摸屏幕实现  虚拟按键的时隐时现
        int i = main.getSystemUiVisibility();
        if (i == View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            hideBottomUIMenu();
        } else if (i == View.SYSTEM_UI_FLAG_VISIBLE) {
            main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            hideBottomUIMenu();
        } else if (i == View.SYSTEM_UI_FLAG_LOW_PROFILE) {
            //注释掉  没有状态栏 不会时隐时现
            //main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
        switch (v.getId()) {
            case R.id.DC_Button_Navi1:    //去导航点1
                BtnInvokeNavi = true;
                FlagPatrol = false;
                SelectNavi = 1;
                Message msgnav1 = new Message();
                msgnav1.what = IStatus.STATE_Message_Nav1;
                myhandler.sendMessage(msgnav1);
                break;
            case R.id.DC_Button_Navi2:    //去导航点2
                BtnInvokeNavi = true;
                FlagPatrol = false;
                SelectNavi = 2;
                Message msgnav2 = new Message();
                msgnav2.what = IStatus.STATE_Message_Nav2;
                myhandler.sendMessage(msgnav2);
                break;
            case R.id.DC_Button_Navi3:    //去导航点3
                BtnInvokeNavi = true;
                FlagPatrol = false;
                SelectNavi = 3;
                Message msgnav3 = new Message();
                msgnav3.what = IStatus.STATE_Message_Nav3;
                myhandler.sendMessage(msgnav3);
                break;
            case R.id.DC_Button_Navi4:    //去导航点4
                BtnInvokeNavi = true;
                FlagPatrol = false;
                SelectNavi = 4;
                Message msgnav4 = new Message();
                msgnav4.what = IStatus.STATE_Message_Nav4;
                myhandler.sendMessage(msgnav4);
                break;
            case R.id.DC_Button_Navi5:    //去导航点5
                BtnInvokeNavi = true;
                FlagPatrol = false;
                SelectNavi = 5;
                Message msgnav5 = new Message();
                msgnav5.what = IStatus.STATE_Message_Nav5;
                myhandler.sendMessage(msgnav5);
                break;
            case R.id.DC_Button_CancelNavi:  //取消导航
                String msg6 = "{\"op\":\"publish\",\"topic\":\"/move_base/cancel\",\"msg\":{\"stamp\":0,\"id\":\"\"}}";
                client.send(msg6);
                break;
            case R.id.DC_Button_NaviPatrol:  //巡逻
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setIcon(R.drawable.first);
                builder.setTitle("巡逻");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.popupdialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder.setView(view);
                final EditText DC_EditText_EnterPassWord = (EditText) view.findViewById(R.id.DC_EditTextNewNavigation);
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {  //开始巡逻
                        BtnInvokeNavi = false;
                        FlagPatrol = true;
                        ArriveNaviNumber = 0;
                        String PatrolNumber1 = DC_EditText_EnterPassWord.getText().toString().trim();
                        PatrolNumberCount = (Integer.parseInt(PatrolNumber1));  //根据导航点的名称来改的
                        saveAllPatrolNumbers = PatrolNumberCount;
                        Message message111 = new Message();  //开始巡逻先去第一个导航点 起点
                        message111.what = IStatus.STATE_Message_Nav1;
                        myhandler.sendMessage(message111);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
                break;
            case R.id.DC_Button_ShowRobot:  //显示图片 机器人的位置
                selectWhichFragmentToShow(IStatus.STATE_Fragement_ShowRobot);
                break;
            case R.id.DC_Button_SetNavLocation:     //显示导航点的信息
                // selectWhichFragmentToShow(IStatus.STATE_Fragemnt_SetNavi);
                //显示密码界面
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setIcon(R.drawable.first);
                builder1.setTitle("请输入用户名和密码");
                //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
                View view1 = LayoutInflater.from(MainActivity.this).inflate(R.layout.passworddialog, null);
                //    设置我们自己定义的布局文件作为弹出框的Content
                builder1.setView(view1);

                final EditText DC_EditText_EnterPassWord1 = (EditText) view1.findViewById(R.id
                        .DC_EditText_EnterPassWord);

                builder1.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String psd = DC_EditText_EnterPassWord1.getText().toString().trim();
                        if (psd.equals("1234")) {
                            selectWhichFragmentToShow(IStatus.STATE_Fragemnt_SetNavi);
                        } else {
                            Toast.makeText(MainActivity.this, "密码错误:", Toast.LENGTH_SHORT).show();
                        }
                        DC_EditText_EnterPassWord1.getText().toString().trim();
                    }
                });
                builder1.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder1.show();
                break;
            case R.id.DC_ImgBtn_Up:    //前进
                // TurnUp();
                break;
            case R.id.DC_ImgBtn_Down:  //后退
                //TurnDown();
                break;
            case R.id.DC_ImgBtn_Left:  //左自转
                // TurnLeft();
                break;
            case R.id.DC_ImgBtn_Right: //右自转
                //TurnRight();
                break;
            default:
                break;
        }
    }

    /**
     * @Function: 控制前后左右的运动的长按 触发
     * @Return:
     */
    private class ComponentOnLongTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (v.getId()) {
                case R.id.DC_ImgBtn_Up:    //前进
                    onTouchChange("Up", event.getAction());
                    break;
                case R.id.DC_ImgBtn_Down:  //后退
                    onTouchChange("Down", event.getAction());
                    break;
                case R.id.DC_ImgBtn_Left:  //左转
                    onTouchChange("Left", event.getAction());
                    break;
                case R.id.DC_ImgBtn_Right: //右转
                    onTouchChange("Right", event.getAction());
                    break;
                default:
                    break;
            }
            return true;
        }
    }

    private boolean Btn_LongPressUp = false;
    private boolean Btn_LongPressDown = false;
    private boolean Btn_LongPressLeft = false;
    private boolean Btn_LongPressRight = false;

    //控制方向的转动
    class ControlMoveThread extends Thread {
        @Override
        public void run() {
            while (Btn_LongPressUp) {
                TurnUp();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            while (Btn_LongPressDown) {
                TurnDown();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            while (Btn_LongPressLeft) {
                TurnLeft();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
            while (Btn_LongPressRight) {
                TurnRight();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    //判断事件
    private void onTouchChange(String methodName, int eventAction) {
        //接触误按，多个按键一起按下
        if (Btn_LongPressUp && Btn_LongPressDown && Btn_LongPressLeft && Btn_LongPressRight) {
            Btn_LongPressUp = false;
            Btn_LongPressDown = false;
            Btn_LongPressLeft = false;
            Btn_LongPressRight = false;
        }
        ControlMoveThread controlMoveThread = new ControlMoveThread();
        //判断按下了哪个
        if ("Up".equals(methodName)) {  //前进
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressUp = true;
                DC_ImgBtn_Up.setImageResource(R.drawable.move_up_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressUp = false;
                DC_ImgBtn_Up.setImageResource(R.drawable.move_up);
            }
        }
        if ("Down".equals(methodName)) {  //后退
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressDown = true;
                DC_ImgBtn_Down.setImageResource(R.drawable.move_down_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressDown = false;
                DC_ImgBtn_Down.setImageResource(R.drawable.move_down);
            }
        }
        if ("Left".equals(methodName)) {  //左转
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressLeft = true;
                DC_ImgBtn_Left.setImageResource(R.drawable.move_left_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressLeft = false;
                DC_ImgBtn_Left.setImageResource(R.drawable.move_left);
            }
        }
        if ("Right".equals(methodName)) {  //右转
            if (eventAction == MotionEvent.ACTION_DOWN) {
                controlMoveThread.start();
                Btn_LongPressRight = true;
                DC_ImgBtn_Right.setImageResource(R.drawable.move_right_press);
            } else if (eventAction == MotionEvent.ACTION_UP) {
                TurnStop();
                if (controlMoveThread != null)
                    Btn_LongPressRight = false;
                DC_ImgBtn_Right.setImageResource(R.drawable.move_right);
            }
        }
    }

    public Handler myhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case IStatus.STATE_Message_Nav1:
                    String msg1 = " {\"op\":\"publish\",\"topic\":\"/move_base_simple/goal\",\"msg\":{\"header\":{\"seq\":0,\"stamp\":0," +
                            "\"frame_id\":\"map\"},\"pose\":{\"position\":{\"x\":" + GetNaviPos_x[0] + ",\"y\":" + GetNaviPos_y[0] + "," +
                            "\"z\":0}," +
                            "\"orientation\":{\"x\":0,\"y\":0,\"z\":" + GetNaviOri_z[0] + ",\"w\":" + GetNaviOri_w[0] + "}}}}";
                    client.send(msg1);
                    break;
                case IStatus.STATE_Message_Nav2:
                    String msg2 = " {\"op\":\"publish\",\"topic\":\"/move_base_simple/goal\",\"msg\":{\"header\":{\"seq\":0,\"stamp\":0," +
                            "\"frame_id\":\"map\"},\"pose\":{\"position\":{\"x\":" + GetNaviPos_x[1] + ",\"y\":" + GetNaviPos_y[1] + "," +
                            "\"z\":0}," +
                            "\"orientation\":{\"x\":0,\"y\":0,\"z\":" + GetNaviOri_z[1] + ",\"w\":" + GetNaviOri_w[1] + "}}}}";
                    client.send(msg2);
                    break;
                case IStatus.STATE_Message_Nav3:
                    String msg3 = " {\"op\":\"publish\",\"topic\":\"/move_base_simple/goal\",\"msg\":{\"header\":{\"seq\":0,\"stamp\":0," +
                            "\"frame_id\":\"map\"},\"pose\":{\"position\":{\"x\":" + GetNaviPos_x[2] + ",\"y\":" + GetNaviPos_y[2] + "," +
                            "\"z\":0}," +
                            "\"orientation\":{\"x\":0,\"y\":0,\"z\":" + GetNaviOri_z[2] + ",\"w\":" + GetNaviOri_w[2] + "}}}}";
                    client.send(msg3);
                    break;
                case IStatus.STATE_Message_Nav4:
                    String msg4 = " {\"op\":\"publish\",\"topic\":\"/move_base_simple/goal\",\"msg\":{\"header\":{\"seq\":0,\"stamp\":0," +
                            "\"frame_id\":\"map\"},\"pose\":{\"position\":{\"x\":" + GetNaviPos_x[3] + ",\"y\":" + GetNaviPos_y[3] + "," +
                            "\"z\":0}," +
                            "\"orientation\":{\"x\":0,\"y\":0,\"z\":" + GetNaviOri_z[3] + ",\"w\":" + GetNaviOri_w[3] + "}}}}";
                    client.send(msg4);
                    break;
                case IStatus.STATE_Message_Nav5:
                    String msg5 = " {\"op\":\"publish\",\"topic\":\"/move_base_simple/goal\",\"msg\":{\"header\":{\"seq\":0,\"stamp\":0," +
                            "\"frame_id\":\"map\"},\"pose\":{\"position\":{\"x\":" + GetNaviPos_x[4] + ",\"y\":" + GetNaviPos_y[4] + "," +
                            "\"z\":0}," +
                            "\"orientation\":{\"x\":0,\"y\":0,\"z\":" + GetNaviOri_z[4] + ",\"w\":" + GetNaviOri_w[4] + "}}}}";
                    client.send(msg5);
                    break;
                case IStatus.STATE_Message_ShowPowerView:
                    if (powerData_number == 100) {
                        Toast.makeText(getApplicationContext(), "电量已经充满", Toast.LENGTH_SHORT).show();
                        powerData_dir = 0;
                    }
                    if (powerData_dir == 1) {   //充电
                        DC_Custom_Battery_view.setPower(powerData_number += 5, 1);
                        if (powerData_number == 100) {
                            powerData_number = 0;
                        }
                    } else if (powerData_dir == 0) {
                        DC_TextViewShowElectricPercentage.setText(powerData_number + " %");
                    }
                    break;
                case IStatus.STATE_Message_Act2FraContent:
                    Bundle bundle = new Bundle();
                    if (pos_x != 0.00 || pos_y != 0.00 || ori_z != 0.00 || ori_w != 0.00) {
                        bundle.putDouble("pos_x", pos_x);
                        bundle.putDouble("pos_y", pos_y);
                        bundle.putDouble("ori_z", ori_z);
                        bundle.putDouble("ori_w", ori_w);
                        if (setNavigationFragemnt != null) {
                            setNavigationFragemnt.setArguments(bundle);
                            //  Log.i("dachen", "Activity2Fragment");   //作为标记位
                        }
                        if (showRobotLocationFragment != null) {
                            showRobotLocationFragment.setArguments(bundle);
                            //  Log.i("dachen", "Activity2Fragment");   //作为标记位
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //根据 index 选择哪个 Fragment 来显示，
    public void selectWhichFragmentToShow(int index) {
        FragmentTransaction myfragmentTransaction = fragmentManager.beginTransaction();
        hideFragments(myfragmentTransaction);
        switch (index) {
            case IStatus.STATE_Fragemnt_SetNavi:   //设置导航点
                if (setNavigationFragemnt == null) {
                    setNavigationFragemnt = new SetNavigationFragemnt();
                    myfragmentTransaction.add(R.id.DC_Fragemnt_ShowMain,
                            setNavigationFragemnt);
                } else {
                    myfragmentTransaction.show(setNavigationFragemnt);
                    setNavigationFragemnt.onResume();
                }
                myfragmentTransaction.commit();
                break;
            case IStatus.STATE_Fragement_ShowRobot:  //显示当前的位置
                if (showRobotLocationFragment == null) {
                    showRobotLocationFragment = new ShowRobotLocationFragment();
                    myfragmentTransaction.add(R.id.DC_Fragemnt_ShowMain,
                            showRobotLocationFragment);
                } else {
                    myfragmentTransaction.show(showRobotLocationFragment);
                    showRobotLocationFragment.onResume();
                }
                myfragmentTransaction.commit();
                break;
            default:
                break;
        }
    }

    //隐藏界面
    public void hideFragments(FragmentTransaction myfragmentTransaction) {
        if (setNavigationFragemnt != null) {   //设置导航点
            myfragmentTransaction.hide(setNavigationFragemnt);
        }
        if (showRobotLocationFragment != null) {  //显示当前的位置
            myfragmentTransaction.hide(showRobotLocationFragment);
        }
    }

    //  前进
    private void TurnUp() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0.4 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0 + "}}}";
        client.send(MegRight);
        Log.i(IStatus.STATE_Log_Info, "老子在前进");
    }

    //后退
    private void TurnDown() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + -0.2 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0 + "}}}";
        client.send(MegRight);
        Log.i(IStatus.STATE_Log_Info, "老子在后退");
    }

    //左转
    private void TurnLeft() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + (0.8) + "}}}";
        client.send(MegRight);
        Log.i(IStatus.STATE_Log_Info, "老子在左转");
    }

    //右转
    private void TurnRight() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + (-0.8) + "}}}";
        client.send(MegRight);
        Log.i(IStatus.STATE_Log_Info, "老子在右转");
    }

    //停止
    private void TurnStop() {
        String MegRight = "{\"op\":\"publish\",\"topic\":\"/cmd_vel\",\"msg\":{\"linear\":{\"x\":" + 0 + ",\"y\":" +
                0 + ",\"z\":0},\"angular\":{\"x\":0,\"y\":0,\"z\":" + 0 + "}}}";
        client.send(MegRight);
        Log.i(IStatus.STATE_Log_Info, "老子在不想转了");
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

