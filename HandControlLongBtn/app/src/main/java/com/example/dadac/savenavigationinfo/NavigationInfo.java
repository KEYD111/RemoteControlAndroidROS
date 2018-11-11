package com.example.dadac.savenavigationinfo;


import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * @ Create by dadac on 2018/10/29.
 * @Function: 保存导航点的信息  当第一次创建表格需要插入数据时，在此写 sql 语句
 * @Return:
 */

@Table(name = "navigation_info", onCreated = "")
public class NavigationInfo {

    /**
     * id:    序列号
     * name:  名称
     * x_position: 位置信息 x
     * y_position:         y
     * z_position:         z
     * w_position:         w
     */
    /**
     * name = "_id"; 数据库表中的第一个字段
     * isId = true ;  是否主键
     * autoGen = true;  自动增长
     * property = "NOTNULL"; 不为空
     */
    @Column(name = "_id", isId = true, autoGen = true, property = "NOT NULL")
    private int _id;
    @Column(name = "u_name", property = "NOT NULL")
    private String u_name;
    @Column(name = "x_position", property = "NOT NULL")
    private double x_position;
    @Column(name = "y_position", property = "NOT NULL")
    private double y_position;
    @Column(name = "z_orientation", property = "NOT NULL")
    private double z_orientation;
    @Column(name = "w_orientation", property = "NOT NULL")
    private double w_orientation;

    public NavigationInfo() {
    }

    public NavigationInfo(int _id, String u_name, double x_position, double y_position, double z_orientation, double w_orientation) {
        this._id = _id;
        this.u_name = u_name;
        this.x_position = x_position;
        this.y_position = y_position;
        this.z_orientation = z_orientation;
        this.w_orientation = w_orientation;
    }

    public NavigationInfo(String u_name, double x_position, double y_position, double z_orientation, double w_orientation) {
        this.u_name = u_name;
        this.x_position = x_position;
        this.y_position = y_position;
        this.z_orientation = z_orientation;
        this.w_orientation = w_orientation;
    }

    @Override
    public String toString() {
        return "navigation_Info{" + "_id" + _id + " u_name" + u_name + " x_position" + x_position + " y_position" + y_position + " " +
                "z_orientation" + z_orientation + " " + "w_orientation" + w_orientation;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public double getX_position() {
        return x_position;
    }

    public void setX_position(double x_position) {
        this.x_position = x_position;
    }

    public double getY_position() {
        return y_position;
    }

    public void setY_position(double y_position) {
        this.y_position = y_position;
    }

    public double getZ_orientation() {
        return z_orientation;
    }

    public void setZ_orientation(double z_orientation) {
        this.z_orientation = z_orientation;
    }

    public double getW_orientation() {
        return w_orientation;
    }

    public void setW_orientation(double w_orientation) {
        this.w_orientation = w_orientation;
    }
}










































