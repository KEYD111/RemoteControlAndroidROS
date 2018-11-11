package customView;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.example.dadac.sqlitesavaname.IStatus;
import com.example.dadac.sqlitesavaname.R;

/**
 * @ Create by dadac on 2018/10/31.
 * @Function:
 * @Return:
 */
public class DialogActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "DialogActivity";
    private EditText DC_EditTextPassWord;
    private Button DC_Button_unEnsure;
    private Button DC_Button_Ensure;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popupdialog);
        //定义弹出框，防止弹出框的高度和宽度变形
        WindowManager m = getWindowManager();
        Display d = m.getDefaultDisplay();//为获取屏幕宽、高
        WindowManager.LayoutParams p = getWindow().getAttributes();//获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 1.0);//高度设置为屏幕的1.0
        p.width = (int) (d.getWidth() * 1.0); //宽度设置为屏幕的1.0
        //p.alpha = 1.0f;//设置本身透明度
        p.dimAmount = 0.8f; //设置黑暗度
        getWindow().setAttributes(p);//设置生效
        initView();

    }

    private void initView() {
        DC_EditTextPassWord = (EditText) findViewById(R.id.DC_EditTextPassWord);
        DC_Button_unEnsure = (Button) findViewById(R.id.DC_Button_unEnsure);
        DC_Button_unEnsure.setOnClickListener(this);
        DC_Button_Ensure = (Button) findViewById(R.id.DC_Button_Ensure);
        DC_Button_Ensure.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.DC_Button_Ensure:   //确定
                String content = DC_EditTextPassWord.getText().toString().trim();
                Intent mIntent = new Intent();
                mIntent.putExtra(IStatus.STATE_Intent_ContentPsd, content);
                // 设置结果，并进行传送
                setResult(IStatus.STATE_IntentResultCode_Psd, mIntent);
                finish();
                break;
            case R.id.DC_Button_unEnsure:   //确定
                Intent mIntent1 = new Intent();
                mIntent1.putExtra(IStatus.STATE_Intent_ContentPsd, "a");
                // 设置结果，并进行传送
                setResult(IStatus.STATE_IntentResultCode_Psd, mIntent1);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
