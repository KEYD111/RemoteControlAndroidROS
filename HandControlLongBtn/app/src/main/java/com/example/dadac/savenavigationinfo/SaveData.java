package com.example.dadac.savenavigationinfo;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.xutils.DbManager;

import org.xutils.db.sqlite.SqlInfo;
import org.xutils.db.table.DbModel;
import org.xutils.db.table.TableEntity;
import org.xutils.ex.DbException;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @ Create by dadac on 2018/10/29.
 * @Function: 使用SQLite来存储数据
 * @Return:
 */
public class SaveData {

    //新建数据库
    DbManager dbManager;
    DbManager.DaoConfig daoConfig = new DbManager.DaoConfig();

    /**
     * @Function: 初始化 DaoConfig 配置
     * @Return:
     * @attention: 不管什么使用 SaveData saveData = new SavaData()  都
     * 必须使用初始化的函数  即： saveData.InitDaoConfig()
     */
    public void InitDaoConfig(Context context) {
        String path = context.getExternalFilesDir("Documents").getPath();   //平板路径
        //平板存放SQL的路径
        daoConfig.setDbName("navigation_info.db")
                //设置数据库的名字 默认的是 xutils.db
                .setDbDir(new File("/mnt/sdcard/SQLite"))   //设置数据库路径，默认存储在 app 的私有目录 RK
                //.setDbDir(new File(path + "/SQLite"))    //平板的路径
                .setDbVersion(1)    //设置当前的版本号
                .setDbOpenListener(new DbManager.DbOpenListener() {   //设置数据库打开的监听
                    @Override
                    public void onDbOpened(DbManager db) {
                        //适用于大量数据写的时候 开启数据库支持多线程的操作
                        db.getDatabase().enableWriteAheadLogging();
                    }
                }).setDbUpgradeListener(new DbManager.DbUpgradeListener() {  //设置数据库更新的监听
            @Override
            public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                Log.i("dachenSQLite", "数据库已经更新了 oldversion: " + oldVersion + "  newversion" + newVersion);
            }
        }).setTableCreateListener(new DbManager.TableCreateListener() {  //设置表创建的监听
            @Override
            public void onTableCreated(DbManager db, TableEntity<?> table) {
                Log.i("dachenSQLite", "表已经被创建了" + table.getName());
            }
        }).setAllowTransaction(true);    //设置是否允许事务  默认为 true
        dbManager = x.getDb(daoConfig);
    }

    /*创建数据库*/
    public void CreateSQLData(int count) {
        ArrayList<NavigationInfo> navigationInfos = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            navigationInfos.add(new NavigationInfo("导航点" + i, 0.0, 0.0, 0.0, 0.0));
        }
        try {
            dbManager.save(navigationInfos);
            Log.i("dachenSQLite", "数据库已经创建成功");
        } catch (DbException e) {
            e.printStackTrace();
            Log.e("dachenSQLite", e.getMessage() + "数据库创建失败");
        }
    }


    /**
     * @Function: 查询表中的指定的数据  都是保存为 JSON 的格式
     * @Return:
     * @attention: 数据库索引没有 0
     */
    public String QuerySQLData(@Nullable int Index) {
        NavigationInfo navigationIndex = null;
        String jsonObject = "Error";
        try {
            navigationIndex = dbManager.findById(NavigationInfo.class, Index);
            // javaToJson 将 Java 对象转换成 JSON 对象扔出去
            jsonObject = JSON.toJSONString(navigationIndex);
        } catch (DbException e) {
            e.printStackTrace();
            Log.e("dachenSQLite", "找不到指定的数据");
        }
        return jsonObject;
    }

    /**
     * @Function: 删除数据库指定的内容
     * @Return:
     */
    public void DeleteSQLIndex(int index) {
        try {
            dbManager.deleteById(NavigationInfo.class, index);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 删除数据库
     * @Return:
     */
    public void deleteSQL() {
        try {
            dbManager.dropDb();
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 删除表
     * @Return:
     */
    public void deletetable() {
        try {
            dbManager.dropTable(NavigationInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 新增表中的数据
     * @Return:
     */
    public void newSQLData(int id, String name, double x, double y, double z, double w) {
        try {
            NavigationInfo navigationInfo = new NavigationInfo(id, name, x, y, z, w);
            dbManager.save(navigationInfo);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 修改表中的数据
     * @Return:
     */
    public void updateSQLData(int Index, String name, double x, double y, double z, double w) {
        try {
            //第一种写法
            NavigationInfo navigationInfo = dbManager.findById(NavigationInfo.class, Index);
            navigationInfo.setU_name(name);
            navigationInfo.setX_position(x);
            navigationInfo.setY_position(y);
            navigationInfo.setZ_orientation(z);
            navigationInfo.setW_orientation(w);
            dbManager.update(navigationInfo, "u_name");   // u_name   表中的字段名
            dbManager.update(navigationInfo, "x_position");   // x_position   表中的字段名
            dbManager.update(navigationInfo, "y_position");   // y_position   表中的字段名
            dbManager.update(navigationInfo, "z_orientation");   // z_position   表中的字段名
            dbManager.update(navigationInfo, "w_orientation");   // w_position   表中的字段名
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Function: 返回 sql 语句的长度
     * @Return:
     */
    public int GetTheTableSize() {
        DbManager dbManager = x.getDb(daoConfig);
        try {
            List<DbModel> ed = dbManager.findDbModelAll(new SqlInfo("select * from navigation_info"));
            return ed.size();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return 1;
    }


    /**
     * @Function: UI 异步执行
     * @Return:
     */
    public void AsyUISQL() {
        x.task().run(new Runnable() {
            @Override
            public void run() {
                //异步代码
            }
        });
    }

    /**
     * @Function: 同步代码
     * @Return:
     */
    public void TogetherUISQL() {
        x.task().post(new Runnable() {
            @Override
            public void run() {
                //同步代码

            }
        });
    }

    /**
     * @Function: 获取 数据库中所有的的名字
     * @Return:
     */
    public String[] GetSQLALLName() {
        //获取长度
        int size = GetTheTableSize();
        String[] contents = new String[size];
        for (int i = 1; i <= size; i++) {
            contents[i - 1] = (String) (JSONObject.parseObject(QuerySQLData(i))).get("u_name");
        }
        return contents;
    }

    /**
     * @Function: 获取 数据库中所有的的ID
     * @Return:
     */
    public String[] GetSQLALLID() {
        //获取长度
        int size = GetTheTableSize();
        String[] contents = new String[size];
        for (int i = 1; i <= size; i++) {
            contents[i - 1] = i + "";
        }
        return contents;
    }


}

























