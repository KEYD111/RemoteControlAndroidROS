package com.example.dadac.sqlitesavaname;

/**
 * @ Create by dadac on 2018/9/10.
 * @Function: 存储各种变量
 * @Return:
 */
public class IStatus {

    /************************ Fragment 的状态变量 **************************/
    public static final int STATUS_NONE = 0x01;
    public static final int STATUS_Fragment_AskForQuestiton = 0x02;
    public static final int STATUS_Fragment_BigRoomGuide = 0x03;
    public static final int STATUS_Fragment_QuenueForNumber = 0x04;
    public static final int STATUS_Fragment_TuLingRobot = 0x05;


    /*******************  Message 中使用的变量 ***************************/
    public static final int STATE_Message_GetSysDataAndTimeSerive = 0x20;
    public static final int STATE_Message_GetSerialPortAngle = 0x21;
    public static final int STATE_Message_GetIdentifyContent = 0x22;
    public static final int STATE_Message_GetPowerInfo = 0x23;
    public static final int STATE_Message_ShowPowerView = 0x24;
    public static final int STATE_Message_SetNavi = 0x25;
    public static final int STATE_Message_StartPatrol = 0x26;
    public static final int STATE_Message_Nav1 = 0x27;
    public static final int STATE_Message_Nav2 = 0x28;
    public static final int STATE_Message_Nav3 = 0x29;
    public static final int STATE_Message_Nav4 = 0x30;
    public static final int STATE_Message_Nav5 = 0x31;
    public static final int STATE_Message_Act2FraContent= 0x32;




    /********************  各种Fragment标志位   ***********/

    public static final int STATE_Flag_AskForQuestionToTuLing = 0x40;
    public static final int STATE_Flag_AskForQuesyionPageShowOK = 0x41;
    public static final int STATE_Flag_AskForQuesyionPageShowFailed = 0x42;
    public static final int STATE_Flag_TuLingRobotCustom_SEND = 0x43;
    public static final int STATE_Flag_TuLingRobotRobot_RECEIVE = 0x44;


    /******************** LOG 出现的标志位  *******************/
    public static final String STATE_Log_Info = "dachenI";
    public static final String STATE_Log_Debug = "dachenD";
    public static final String STATE_Log_Error = "dachenE";

    /***********************广播的标志位Action位   及其对应的广播的Key位***********/
    public static final String BroadCast_Action_WakeUpAngle = "BroadCast_Action_WakeUp_Angle";
    public static final String BroadCast_Key_WakeUpAngle = "BroadCast_Key_WakeUp_Angle";


    /********************  机器人转动的标志位 左 右   ***********/

    public static final int STATE_Robot_TurnLeft = 0x51;
    public static final int STATE_Robot_TurnRight = 0x52;

    /*********************** remote 显示 Fragement  ********************************/
    public static final int STATE_Fragement_ShowRobot = 0x61;
    public static final int STATE_Fragemnt_SetNavi = 0x62;

    /*********************** Intent 的标志位 ****************************************/
    public static final int STATE_IntentResultCode_Psd = 0x72;
    public static final String STATE_Intent_ContentPsd = "Intent_EnterPassword";







}
