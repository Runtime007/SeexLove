package com.chat.seecolove.view.activity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.chat.seecolove.widget.UploadProgressDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Theme;
import com.chat.seecolove.widget.CustomProgressDialog;

public abstract class BaseAppCompatActivity extends BaseSwipeBackActivity {
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    protected boolean isSystem = false;
    protected Toolbar toolbar;
    protected TextView title;
    protected NetWork netWork;

    protected abstract int getContentViewLayoutId();

    protected MyApplication app;
    public static BaseAppCompatActivity instance;
    protected JsonUtil jsonUtil;
    protected CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setnotififull();
        super.onCreate(savedInstanceState);
        setContentView(getContentViewLayoutId());
        app = MyApplication.getContext();
        app.allActivity.add(this);
        jsonUtil = new JsonUtil(this);
        netWork = new NetWork(this);

        LogTool.setLog("CurrentActivity---->", getClass().getSimpleName());

        if (getLocalClassName().contains("UserProfileInfoActivity")
                || getLocalClassName().contains("UserInfoActivity")
                ) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle("");//去掉默认居左的title
                title = (TextView) toolbar.findViewById(R.id.title);
            }
        } else {
            View toolbarView = findViewById(R.id.toolbar);
            if (toolbarView != null) {
                toolbar = (Toolbar) toolbarView.findViewById(R.id.toolbar_ti);
                if (toolbar != null) {
                    toolbar.setTitle("");//去掉默认居左的title
                    title = (TextView) toolbar.findViewById(R.id.title);
                }
            }
        }

        setupActionBar();
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        instance = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    private void setupActionBar() {
        setSupportActionBar(toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextAppearance(this, R.style.ToolbarTitle);
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.icon_nav_return);
        }
    }




    private int getStatusBarColor() {
        return getColorPrimary();
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    private int getMinePrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.color.mine_statusbar, typedValue, true);
        return typedValue.data;
    }

    protected void setMineStatusBar(int mark) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            String colorRes = mark == 0 ? "#000000" : Theme.getCurrentTheme().title_color;
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(colorRes));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            String colorParse = mark == 0 ? "#000000" : Theme.getCurrentTheme().title_color;
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //            window.setFlags(
            //                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
            //                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏
            tintManager.setTintColor(Color.parseColor(colorParse));
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void showProgress(int resID) {
        if (!isFinishing()) {
            if (progressDialog != null) {
                progressDialog.cancel();
            }
            progressDialog = new CustomProgressDialog(this, getResources()
                    .getString(resID));
            progressDialog.show();
        }
    }

    protected void dismiss() {
        if (progressDialog != null) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    progressDialog.dismiss();
                }
            }, 300);

        }
    }

    protected void recycleDatas(Object object) {
        if (object == null) {
            return;
        }
        if (object instanceof List) {
            ((List) object).clear();
        }
        LogTool.setLog("object1:", object);
        object = null;
        LogTool.setLog("object2:", object);
    }


    /**
     * 请求权限
     *
     * @param id                   请求授权的id 唯一标识即可
     * @param permission           请求的权限
     * @param allowableRunnable    同意授权后的操作
     * @param disallowableRunnable 禁止权限后的操作
     */
    protected void requestPermission(int id, String permission, Runnable allowableRunnable, Runnable disallowableRunnable) {
        if (allowableRunnable == null) {
            throw new IllegalArgumentException("allowableRunnable == null");
        }

        allowablePermissionRunnables.put(id, allowableRunnable);
        if (disallowableRunnable != null) {
            disallowablePermissionRunnables.put(id, disallowableRunnable);
        }

        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //检查是否拥有权限
            int checkPermission = ContextCompat.checkSelfPermission(getApplicationContext(), permission);
            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                ActivityCompat.requestPermissions(BaseAppCompatActivity.this, new String[]{permission}, id);
                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }

    UploadProgressDialog pDialog;
    public void showUploadDialog(){
        if (pDialog != null) {
            pDialog.cancel();
        }
        pDialog = new UploadProgressDialog(this);
        pDialog.show();
    }
    public void dismissUploadDialog() {
        if (pDialog != null) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //execute the task
                    pDialog.dismiss();
                }
            }, 300);

        }
    }
}
