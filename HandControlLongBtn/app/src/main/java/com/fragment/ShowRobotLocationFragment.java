package com.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dadac.sqlitesavaname.R;

/**
 * @ Create by dadac on 2018/10/30.
 * @Function:
 * @Return:
 */
public class ShowRobotLocationFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View ShowRobotView = inflater.inflate(R.layout.fragemnt_showrobot, container, false);

        return ShowRobotView;
    }


}
