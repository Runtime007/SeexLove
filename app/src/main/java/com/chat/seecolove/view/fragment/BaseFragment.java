package com.chat.seecolove.view.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.widget.CustomProgressDialog;


public abstract class BaseFragment extends Fragment {
    protected NetWork netWork;
    protected JsonUtil jsonUtil;
    protected CustomProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        netWork = new NetWork(getActivity());
        jsonUtil = new JsonUtil(getActivity());
    }
    protected void showProgress(Activity activity, int resID) {
        if(activity!=null&&!activity.isFinishing()){
            if (progressDialog != null) {
                progressDialog.cancel();
            }
            progressDialog = new CustomProgressDialog(activity, getResources()
                    .getString(resID));

            progressDialog.show();
        }

    }
    protected  void recycleDatas(Object object){
        if(object instanceof List){
            ((List) object).clear();
        }
        object=null;
    }

    protected void dissMissProgress(){
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

}
