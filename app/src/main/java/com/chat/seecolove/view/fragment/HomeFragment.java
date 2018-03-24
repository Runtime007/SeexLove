package com.chat.seecolove.view.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.SeeCoSubMuenBean;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.FindActivity;
import com.chat.seecolove.widget.ToastUtils;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Request;



/**
 * Created by Administrator on 2017/12/20.
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener {


    private View view;
    private TabLayout tablayoutView;

    private void getStatusViewhight() {
        int resourceId = getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getActivity().getResources().getDimensionPixelSize(resourceId);
        View statusView = view.findViewById(R.id.status_bar_fix);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
    }

    FrameLayout contentView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            view = getActivity().getLayoutInflater().inflate(R.layout.fragment_home_ui, null);
            contentView = (FrameLayout) view.findViewById(R.id.content_fragment);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getStatusViewhight();
            }
            initViews();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        netWork = new NetWork(getActivity());
        jsonUtil = new JsonUtil(getActivity());

    }



    public static HomeFragment newInstance() {
        LogTool.setLog("HomeFragment newInstance:", "");
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }


    private void initViews() {
        tablView=(TextView)view.findViewById(R.id.tab_1);
        tablView.setOnClickListener(this);
        tab2View=(TextView)view.findViewById(R.id.tab_2);
        tab2View.setOnClickListener(this);
        view.findViewById(R.id.finduser).setOnClickListener(this);
        getCustomHomeMuen();
    }

    private TextView tablView,tab2View;

    //利用反射获取fragment实例，如果该实例已经创建，则用不再创建。
    protected Fragment getFragmentInstance(Class fragmentClass) {
        FragmentManager fm = getChildFragmentManager();
        //查找是否已存在,已存在则不需要重发创建,切换语言时系统会自动重新创建并attch,无需手动创建
        Fragment fragment = fm.findFragmentByTag(fragmentClass.getSimpleName());
        if (fragment != null) {
            return fragment;
        }
        return fragment;
    }

    /**
     * fragment切换
     * fragment
     * @return 最后显示的fragment
     */
    @SuppressLint("RestrictedApi")
    protected void switchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getChildFragmentManager();
        if (fragmentManager == null) {
            return;
        }
        //全局的fragment对象，用于记录最后一次切换的fragment(当前展示的fragment)
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (fragment != null) {
          List<Fragment> fragments = fragmentManager.getFragments();
            //先判断是否为空
            if (fragments != null) {
                for (Fragment mfragment : fragments) {
                    //再把现在显示的fragment 隐藏掉
                    if (!mfragment.isHidden()) {
                        transaction.hide(mfragment);
                    }
                }
            }
            //显示现在的fragment
            if (!fragment.isAdded()) {
                // transaction.addToBackStack(null);
                transaction.add(contentView.getId(), fragment, fragment.getClass().getSimpleName());
//
            } else {
                transaction.show(fragment);
            }
        }
        transaction.commit();
    }

    Fragment fragment;
    VipFragment vipFragment;
    SeeCoParkFragment  homeFragment;

    private void isCheckTabHot(){
        fragment = getFragmentInstance(SeeCoParkFragment.class);
        if (fragment == null) {
            if(muenModels!=null&&muenModels.size()>=1) {
                homeFragment=SeeCoParkFragment.newInstance(muenModels.get(0).getSubMenuList());
            }
        } else {
            homeFragment = (SeeCoParkFragment) fragment;
        }
        switchFragment(homeFragment);
    }
    private void isCheckVipTab(){
        fragment = getFragmentInstance(VipFragment.class);
        if (fragment == null) {
            if(muenModels!=null&&muenModels.size()>=1){
                vipFragment = VipFragment.newInstance(muenModels.get(1).getSubMenuList());
            }
        } else {
            vipFragment = (VipFragment) fragment;
        }
        switchFragment(vipFragment);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_1:
                ischeckView(1);
                isCheckTabHot();
                break;
            case R.id.tab_2:
                ischeckView(2);
                isCheckVipTab();
                break;
            case R.id.finduser:
                Intent intent = new Intent();
                intent.setClass(getActivity(), FindActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void ischeckView(int index){
        switch (index){
            case 1:
                tablView.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                tab2View.setTextColor(ContextCompat.getColor(getActivity(),R.color.hint));
                break;
            case 2:
                tablView.setTextColor(ContextCompat.getColor(getActivity(),R.color.hint));
                tab2View.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
                break;
        }
    }
    List<SeeCoSubMuenBean> muenModels;
    public void getCustomHomeMuen() {
        netWork = new NetWork(getActivity());
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        showProgress(getActivity(),R.string.seex_progress_text);
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        String head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());
        Map map = new HashMap();
        map.put("userId",userID);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().GetMuenTable,map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getCustomHomeMuen:====",jsonObject);
                progressDialog.dismiss();
                if (Tools.jsonResult(getActivity(), jsonObject, progressDialog)) {
                    return;
                }
                try {
                    muenModels  = jsonUtil.jsonToMuen(jsonObject.getString("dataCollection"));
                    tablView.setText(muenModels.get(0).getMenuName());
                    tab2View.setText(muenModels.get(1).getMenuName());
                    Fragment fragment = getFragmentInstance(SeeCoParkFragment.class);
                    if (fragment == null) {
                        homeFragment =  SeeCoParkFragment.newInstance(muenModels.get(0).getSubMenuList());
                    } else {
                        homeFragment = (SeeCoParkFragment) fragment;
                    }
                    switchFragment(homeFragment);
                    ischeckView(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
