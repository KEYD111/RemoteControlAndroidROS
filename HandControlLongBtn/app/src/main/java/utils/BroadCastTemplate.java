package utils;

import android.content.Context;
import android.content.Intent;

/**
 * @ Create by dadac on 2018/10/19.
 * @Function:
 * @Return:
 */
public class BroadCastTemplate {

    public static void StartBroadCast(Context context, String setAction, String putExtraKey, String putExtraValue) {
        //创建Intent对象
        Intent intentBroadcast = new Intent();
        //设置Intent的action属性
        intentBroadcast.setAction(setAction);
        intentBroadcast.putExtra(putExtraKey, putExtraValue);
        //发送广播
        context.sendBroadcast(intentBroadcast);
    }


}
