package customView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dadac.sqlitesavaname.IStatus;
import com.example.dadac.sqlitesavaname.R;


/**
 * @ Create by dadac on 2018/10/30.
 * @Function: 弹窗的显示
 * @Return:
 */
public class PopupView {

    public static void PopupWindow(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.first);
        builder.setTitle("Error");
        builder.setMessage("请检测 IP 是否正确！");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(context).inflate(R.layout.popupdialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    /*删除导航点*/
    public void DeleteNavi(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.first);
        builder.setTitle("Warning");
        builder.setMessage("禁止手动删除导航点，请联系开发者");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(context).inflate(R.layout.popupdialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    // 如果有参数传入 则不要添加 static
    public String EnterPassword(View v, final Context context) {
        final String[] password = new String[1];
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setIcon(R.drawable.first);
        builder.setTitle("请输入用户名和密码");
        //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
        View view = LayoutInflater.from(context).inflate(R.layout.passworddialog, null);
        //    设置我们自己定义的布局文件作为弹出框的Content
        builder.setView(view);

        final EditText DC_EditText_EnterPassWord = (EditText) view.findViewById(R.id.DC_EditText_EnterPassWord);

        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                password[0] = DC_EditText_EnterPassWord.getText().toString().trim();
                //    将输入的用户名和密码打印出来
                //  Toast.makeText(context, "密码: " + password[0], Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                password[0] = "1";
            }
        });
        builder.show();
        if (password[0] == null) {
            password[0] = "1";
        }
        Log.i(IStatus.STATE_Log_Info + "Password", password[0]);
        return password[0];
    }


}

