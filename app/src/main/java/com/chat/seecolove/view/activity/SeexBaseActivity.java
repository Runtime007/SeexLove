package com.chat.seecolove.view.activity;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.chat.seecolove.R;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.Theme;
import com.chat.seecolove.widget.CustomProgressDialog;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.umeng.analytics.MobclickAgent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeexBaseActivity extends FragmentActivity {
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap<>();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap<>();

    protected boolean isSystem = false;
    protected Toolbar toolbar;
    protected TextView title;
    protected NetWork netWork;


    protected MyApplication app;
    public static BaseAppCompatActivity instance;
    protected JsonUtil jsonUtil;
    protected CustomProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

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

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);

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





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
                ActivityCompat.requestPermissions(SeexBaseActivity.this, new String[]{permission}, id);
                return;
            } else {
                allowableRunnable.run();
            }
        } else {
            allowableRunnable.run();
        }
    }
}
