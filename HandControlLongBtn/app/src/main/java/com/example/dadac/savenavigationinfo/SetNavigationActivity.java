//package com.example.dadac.savenavigationinfo;
//
//import android.app.AlertDialog;
//import android.app.ListActivity;
//import android.content.DialogInterface;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.example.dadac.sqlitesavaname.R;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * @ Create by dadac on 2018/10/29.
// * @Function: 设置导航点  Activity  不用了
// * @Return:
// */
//public class SetNavigationActivity extends ListActivity implements View.OnClickListener {
//
//    private String[] IdString = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14"};
//    private String[] ShowString = {"导航点1", "导航点2", "导航点3", "导航点4", "导航点5", "导航点6", "导航点7",
//            "导航点8", "导航点9", "导航点10", "导航点11", "导航点12", "导航点13", "导航点14"};
//    private List<Map> list;
//    private MyAdapter myAdapter;
//    //保证窗口的显示每页刚好10个
//    private float fFirstx;
//    private float fSecx;
//
//    private Button DC_Button_AddNavigation;
//    private Button DC_Button_RemoveNavigation;
//    private Button DC_Button_SubmitNavigation;
//
//    //保存当前修改的导航点的名称
//    private String CurrentPositionName = null;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_setnavi);
//        //初始化按键
//        DC_Button_AddNavigation = (Button) findViewById(R.id.DC_Button_AddNavigation);
//        DC_Button_AddNavigation.setOnClickListener(this);
//        DC_Button_RemoveNavigation = (Button) findViewById(R.id.DC_Button_RemoveNavigation);
//        DC_Button_RemoveNavigation.setOnClickListener(this);
//        DC_Button_SubmitNavigation = (Button) findViewById(R.id.DC_Button_SubmitNavigation);
//        DC_Button_SubmitNavigation.setOnClickListener(this);
//
//        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView textView = (TextView) view.findViewById(R.id.DC_TextView_ShowIndex);
//                Log.i("msg", textView.getText().toString());
//            }
//        });
//
//        list = new ArrayList<>();
//        for (int i = 0; i < ShowString.length; i++) {
//            Map map = new HashMap();
//            map.put("name", ShowString[i]);
//            map.put("id", IdString[i]);
//            list.add(map);
//        }
//        myAdapter = new MyAdapter();
//        setListAdapter(myAdapter);
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.DC_Button_AddNavigation:  //增加导航点
//                Map map = new HashMap();
//                map.put("id", list.size() + 1 + "");
//                map.put("name", "导航点" + (list.size() + 1) + "");
//                list.add(map);
//                myAdapter.notifyDataSetChanged();
//                break;
//            case R.id.DC_Button_RemoveNavigation:  //删除导航点
//                int SaveIndex = list.size();
//                SaveIndex--;
//                if (SaveIndex < 0) {
//                    Toast.makeText(getApplicationContext(), "没得删了,请添加导航点", Toast.LENGTH_SHORT).show();
//                } else
//                    list.remove(SaveIndex);
//                myAdapter.notifyDataSetChanged();
//                break;
//            case R.id.DC_Button_SubmitNavigation:   //提交更改
//                //具体实现
//                myAdapter.notifyDataSetChanged();
//                break;
//            default:
//                break;
//        }
//    }
//
//    public class MyAdapter extends BaseAdapter {
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public View getView(final int position, View convertView, final ViewGroup parent) {
//            ViewHold viewHold = new ViewHold();
//            if (convertView == null) {
//                convertView = View.inflate(SetNavigationActivity.this, R.layout.rowshow_navlist, null);
//                viewHold.id = (TextView) convertView.findViewById(R.id.DC_TextView_ShowIndex);
//                viewHold.name = (TextView) convertView.findViewById(R.id.DC_TextView_ShowName);
//                viewHold.DC_Button_NotifyPoint = (Button) convertView.findViewById(R.id.DC_Button_NotifyPoint);
//                convertView.setTag(viewHold);
//            } else {
//                viewHold = (ViewHold) convertView.getTag();
//            }
//            viewHold.id.setText(list.get(position).get("id").toString());
//            viewHold.name.setText(list.get(position).get("name").toString());
//            viewHold.DC_Button_NotifyPoint.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {  //更改导航点，数据库的操作
//                    //具体实现
//                    PopupWindow(position);
//                    myAdapter.notifyDataSetChanged();
//                }
//            });
//
//            convertView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    switch (event.getAction()) {
//                        case MotionEvent.ACTION_UP:
//                            fSecx = event.getX();
//                            if ((fSecx - fFirstx) > 10) {
//                                ViewHold viewHold = (ViewHold) v.getTag();
//                                viewHold.DC_Button_NotifyPoint.setVisibility(Button.VISIBLE);
//                                viewHold.DC_Button_NotifyPoint.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        list.remove(position);
//                                        myAdapter.notifyDataSetChanged();
//                                    }
//                                });
//                            }
//                            break;
//                        case MotionEvent.ACTION_DOWN:
//                            fFirstx = event.getX();
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            break;
//                    }
//                    return true;
//                }
//            });
//            return convertView;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        class ViewHold {
//            private TextView id;
//            private TextView name;
//            private Button DC_Button_NotifyPoint;
//        }
//
//
//        private void PopupWindow(final int position) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(SetNavigationActivity.this);
//            builder.setIcon(R.drawable.first);
//            builder.setTitle("请输入新的导航点的名称");
//            //    通过LayoutInflater来加载一个xml的布局文件作为一个View对象
//            View view = LayoutInflater.from(SetNavigationActivity.this).inflate(R.layout.popupdialog, null);
//            //    设置我们自己定义的布局文件作为弹出框的Content
//            builder.setView(view);
//
//            final EditText DC_EditTextNewNavigation = (EditText) view.findViewById(R.id.DC_EditTextNewNavigation);
//
//            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    CurrentPositionName = DC_EditTextNewNavigation.getText().toString().trim();
//                    //修改显示的界面
//                    Map map = new HashMap();
//                    map.put("id", (position + 1) + "");
//                    map.put("name", CurrentPositionName);
//                    list.set(position + 1, map);
//                    myAdapter.notifyDataSetChanged();
//                    //保存新的导航点的名称
//                    Toast.makeText(SetNavigationActivity.this, "新的导航点的名称为" + CurrentPositionName, Toast.LENGTH_SHORT).show();
//                }
//            });
//            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Toast.makeText(SetNavigationActivity.this, "取消操作", Toast.LENGTH_SHORT).show();
//                }
//            });
//            builder.show();
//        }
//
//
//    }
//}
