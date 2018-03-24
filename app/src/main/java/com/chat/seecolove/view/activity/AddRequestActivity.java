package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import com.githang.statusbar.StatusBarCompat;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.fragment.FriendRequestFragment;
import com.chat.seecolove.view.fragment.MyRequestFragment;
import okhttp3.Request;




public class AddRequestActivity extends BaseAppCompatActivity {

    private int currentPage = 0;//0:好友请求；1：我的请求

    private TabLayout tabLayout;
    private ViewPager viewpager;

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private ArrayList<String> mTitleList = new ArrayList<>();

    private int userId;

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, AddRequestActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_add_request;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,android.R.color.transparent), false);
        initView();
    }

    private void initView() {
        title.setText(getResources().getString(R.string.seex_new_friends));
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewpager = (ViewPager) findViewById(R.id.viewpager);

        mTitleList.add(getResources().getString(R.string.seex_requst));
        mTitleList.add(getResources().getString(R.string.seex_my_request));
        fragmentList.add(new FriendRequestFragment());
        fragmentList.add(new MyRequestFragment());

        AddRequestAdapter adapter = new AddRequestAdapter(getSupportFragmentManager(), fragmentList, mTitleList);
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);
        viewpager.setCurrentItem(0);
        viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
                if (position == 0) {
                    MobclickAgent.onEvent(AddRequestActivity.this, "newFriend_recordtListClick_240");
                } else if (position == 1) {
                    MobclickAgent.onEvent(AddRequestActivity.this, "newFriend_requestListClick_240");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
    }
    private void setIndicator(TabLayout tabs, int leftDip, int rightDip) {
        Class<?> tabLayout = tabs.getClass();
        Field tabStrip = null;
        try {
            tabStrip = tabLayout.getDeclaredField("mTabStrip");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        tabStrip.setAccessible(true);
        LinearLayout ll_tab = null;
        try {
            ll_tab = (LinearLayout) tabStrip.get(tabs);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_request_clear, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_all:
                displaySureDialog();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displaySureDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (currentPage == 0) {
            builder.setMessage(R.string.seex_request_fragment_clear_friends);
        } else if (currentPage == 1) {
            builder.setMessage(R.string.seex_request_fragment_clear_my);
        }
        builder.setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                clearAllRequest();
                dialog.dismiss();
            }
        }).setNegativeButton(R.string.seex_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void clearAllRequest() {
        if (netWork == null || !netWork.isNetworkConnected()) {
            ToastUtil.showShortMessage(this, getString(R.string.seex_no_network));
            return;
        }
        Map map = new HashMap();
        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put(ConfigConstants.USER_ID, userId);

        int type = -1;
        if (currentPage == 0) {
            type = ConfigConstants.AddRequest.TYPE_FRIEND_REQUEST;
        } else if (currentPage == 1) {
            type = ConfigConstants.AddRequest.TYPE_MY_REQUEST;
        }
        map.put(ConfigConstants.AddRequest.TYPE, type);
        String str = userId + "" + type + "apply4893dlistr";
        String key = Tools.md5(str);
        map.put(ConfigConstants.KEY, key);
        LogTool.setLog("FriendRequestFragment--map", map);

        MyOkHttpClient.getInstance().asyncPost(head,new Constants().mailClearReq, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtil.showShortMessage(AddRequestActivity.this, getString(R.string.seex_getData_fail));
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("AddRequest------>>", jsonObject);
                if (!Tools.jsonResult(AddRequestActivity.this, jsonObject, null)) {
                    if (currentPage == 0) {
                        FriendRequestFragment friendFragment = (FriendRequestFragment) fragmentList.get(0);
                        friendFragment.clearAllData();
                    } else if (currentPage == 1) {
                        MyRequestFragment myFragment = (MyRequestFragment) fragmentList.get(1);
                        myFragment.clearAllData();
                        MobclickAgent.onEvent(AddRequestActivity.this, "newFriend_recordEmpty_240");
                    }
                }
            }
        });


    }

    public static class AddRequestAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragmentList;

        private ArrayList<String> titleList;

        public AddRequestAdapter(FragmentManager fm, ArrayList<Fragment> fragmentList, ArrayList<String> titleList) {
            super(fm);
            this.fragmentList = fragmentList;
            this.titleList = titleList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList == null ? null : fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList == null ? 0 : fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (titleList == null) {
                return "";
            }
            return titleList.get(position); //tab标题
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
