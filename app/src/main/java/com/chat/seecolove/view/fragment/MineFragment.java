package com.chat.seecolove.view.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.view.activity.AnchorDialogActvity;
import com.chat.seecolove.view.activity.BecomeSellerActivity;
import com.chat.seecolove.view.activity.LoadActivity;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.NotifActivity;
import com.chat.seecolove.view.activity.SCWebView;
import com.chat.seecolove.view.activity.ShareWebActivity;
import com.chat.seecolove.view.activity.UserInfoNewActivity;
import com.chat.seecolove.view.activity.UserInfoNewActivity123;
import com.chat.seecolove.view.activity.WithdrawInfoActivity;
import com.chat.seecolove.widget.CustomRoundAngleTextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.BlackListActivity;
import com.chat.seecolove.view.activity.EditPriceActivity;
import com.chat.seecolove.view.activity.FeedBackActivity;
import com.chat.seecolove.view.activity.MyWebView;
import com.chat.seecolove.view.activity.PerfectActivity;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.activity.RechargeActivity;
import com.chat.seecolove.view.activity.SettingActivity;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

import static com.chat.seecolove.tools.SharedPreferencesUtils.get;


/**
 *个人中心
 */
public class MineFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private RelativeLayout btn_sao, view_share, view_black, view_feedback,view_us,giftpackage,view_task;
    private View btn_price,noti_task;
    private LinearLayout btn_account, view_integral,view_account;
    private SimpleDraweeView user_icon, grade;

    private LinearLayout view_income;
    private TextView  userStatus,tx_curr, tx_next,red_theme,user_name,btn_share, user_finance, user_id, answerPercent, income, tx_balance, btn_regcharge, btn_advance, text_sao, tx_price, text_integral, jobView/*年龄*/;

    private UserInfo userInfo;

    private View level_progress_bottom, level_progress_top,view_line;
    private CustomRoundAngleTextView noti_View;
    private ImageView gradeView;
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_mine, null);
        initViews();
        return view;
    }
    public static MineFragment newInstance() {
        MineFragment fragment = new MineFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
        initData();
    }
    SmartRefreshLayout   ptrClassicFrameLayout;
    private void initViews() {
        ptrClassicFrameLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        ptrClassicFrameLayout.setEnableLoadmore(false);
//        ptrClassicFrameLayout.setEnableRefresh(false);
        ptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                try {
                    if (!netWork.isNetworkConnected()) {
                        ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
                        refreshlayout.finishRefresh();
                        return;
                    }
                    if(sleepTime!=null){
                        sleepTime.cancel();
                        sleepTime=null;
                    }
                    sleepTime = new sleepTimeCount(3000, 1000);
                    sleepTime.start();
//                    conniSocket();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        noti_task = view.findViewById(R.id.noti_task);
        view_black = (RelativeLayout) view.findViewById(R.id.view_black);
        view_task = (RelativeLayout) view.findViewById(R.id.view_task);
        view_feedback = (RelativeLayout) view.findViewById(R.id.view_feedback);
        btn_account = (LinearLayout) view.findViewById(R.id.btn_account);
        view_account = (LinearLayout) view.findViewById(R.id.view_account);
        view_integral = (LinearLayout) view.findViewById(R.id.view_integral);
        btn_price =  view.findViewById(R.id.linear_price);

        user_icon = (SimpleDraweeView) view.findViewById(R.id.user_icon);
        user_name = (TextView) view.findViewById(R.id.user_name);
        view_income = (LinearLayout) view.findViewById(R.id.view_income);
        income = (TextView) view.findViewById(R.id.income);
        user_id = (TextView) view.findViewById(R.id.user_id);

        tx_balance = (TextView) view.findViewById(R.id.tx_balance);

        btn_share = (TextView) view.findViewById(R.id.btn_share);
        jobView= (TextView) view.findViewById(R.id.jobview);
        btn_sao = (RelativeLayout) view.findViewById(R.id.btn_sao);
        text_sao = (TextView) view.findViewById(R.id.text_sao);
        tx_price = (TextView) view.findViewById(R.id.tx_price);
        red_theme = (TextView) view.findViewById(R.id.red_theme);
        text_integral = (TextView) view.findViewById(R.id.text_integral);

        gradeView = (ImageView) view.findViewById(R.id.sex);
        tx_curr = (TextView) view.findViewById(R.id.tx_curr);
        tx_next = (TextView) view.findViewById(R.id.tx_next);
        userStatus  = (TextView) view.findViewById(R.id.userStatus);
        user_finance = (TextView) view.findViewById(R.id.user_finance);
        level_progress_bottom = view.findViewById(R.id.level_progress_bottom);
        level_progress_top = view.findViewById(R.id.level_progress_top);
        view_line = view.findViewById(R.id.view_line);
        view.findViewById(R.id.view_tongzhi).setOnClickListener(this);
        view.findViewById(R.id.view_gonggao).setOnClickListener(this);

        view_us = (RelativeLayout) view.findViewById(R.id.view_us);
        giftpackage = (RelativeLayout) view.findViewById(R.id.giftpackage);
        noti_View=(CustomRoundAngleTextView)view.findViewById(R.id.noti_numView);

        switch_DND = (CheckBox)view.findViewById(R.id.switch_DND);
        switch_Voice = (CheckBox)view.findViewById(R.id.switch_voice);

        voideText = (TextView) view.findViewById(R.id.voideText);
        voiceText = (TextView)view.findViewById(R.id.voiceText);
//        int notrouble = (int) SharedPreferencesUtils.get(getActivity(), Constants.NOTROUBLE, 2);
//        Log.i("aa",notrouble+"==========notrouble");
        disNoti();
    }
    CheckBox switch_DND,switch_Voice;
    TextView voideText,voiceText;
    private void setListeners() {
        user_icon.setOnClickListener(this);
        btn_account.setOnClickListener(this);
        btn_share.setOnClickListener(this);
        //contact_us.setOnClickListener(this);
        view_us.setOnClickListener(this);
        //btn_advance.setOnClickListener(this);
        btn_sao.setOnClickListener(this);
        btn_price.setOnClickListener(this);
        user_name.setOnClickListener(this);
        giftpackage.setOnClickListener(this);
        user_finance.setOnClickListener(this);

        //view_share.setOnClickListener(this);
        view_black.setOnClickListener(this);
        view_feedback.setOnClickListener(this);
        view_integral.setOnClickListener(this);
        view.findViewById(R.id.set).setOnClickListener(this);
        view.findViewById(R.id.edit).setOnClickListener(this);
        view_task.setOnClickListener(this);
    }

    public void initData() {
        getUserInfo();
    }

    public void reInitData() {
        if (userInfo == null) {
            getUserInfo();
        }

    }

    /**
     * 是否显示进度动画
     */
    private boolean ifAnim = true;

    /**
     * 设置当前经验进度
     *
     * @param num
     */
    private void setProgress(int num) {
        ViewGroup.LayoutParams lpbottom = level_progress_bottom.getLayoutParams();
        ViewGroup.LayoutParams lptop = level_progress_top.getLayoutParams();
        int newWidth = level_progress_bottom.getWidth() * num / 100;
        lptop.width = newWidth;
        level_progress_top.setLayoutParams(lptop);
        if (ifAnim) {
            Animation progress_anim = AnimationUtils.loadAnimation(getActivity(), R.anim.progress_anim);
            level_progress_top.startAnimation(progress_anim);
            SharedPreferencesUtils.get(getActivity(), "ifAnim", true);
            ifAnim = false;
        }
    }


    public void hideRed() {
        red_theme.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (userInfo == null) {
            return;
        }
        Intent intent;
        switch (v.getId()) {
            case R.id.btn_share:
                                intent = new Intent(getActivity(), ShareWebActivity.class);
                intent.putExtra("sex", userInfo.getSex());
                startActivityForResult(intent, 0);
//                intent = new Intent(getActivity(), SCWebView.class);
//                intent.putExtra("URL",Constants.h5_url_share);
//                intent.putExtra("TITLE", "分享");
//                intent.putExtra("NEED_TOKEN", true);
//                startActivity(intent);
                MobclickAgent.onEvent(getActivity(), "person_center_share_selected");
                break;
            case R.id.set:
                intent = new Intent(getActivity(), SettingActivity.class);
                intent.putExtra("sex", userInfo.getSex());
                startActivityForResult(intent, 0);
                MobclickAgent.onEvent(getActivity(), "person_center_setting_selected");
                break;
//                intent = new Intent(getActivity(), ShareWebActivity.class);
//                intent.putExtra("sex", userInfo.getSex());
//                startActivityForResult(intent, 0);
//                MobclickAgent.onEvent(getActivity(), "person_center_setting_selected");
//                break;
            case R.id.user_icon:
                if (userInfo.getIsPerfect() == 1) {
                    intent = new Intent(getActivity(), PerfectActivity.class);
                    startActivity(intent);
                    return;
                }

                intent = new Intent(getActivity(), UserProfileInfoActivity.class);
                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, userInfo.getId() + "");
                intent.putExtra(UserProfileInfoActivity.PROFILE_MINE, true);
                Bundle bd = new Bundle();
                bd.putSerializable(ConfigConstants.BecomeSeller.PARAM_USERINFO, userInfo);
                intent.putExtras(bd);
                startActivity(intent);

                MobclickAgent.onEvent(getActivity(),"NKProfile_header_Click_240");
                break;
            case R.id.view_integral:
//                intent = new Intent(getActivity(), IntegralActivity.class);
//                intent.putExtra("userInfo", userInfo);
//                startActivity(intent);
//                MobclickAgent.onEvent(getActivity(), "person_experience_scroll_clicked");

                int userType = (int)SharedPreferencesUtils.get(getActivity(),Constants.USERTYPE,0);
                String accessUrl = userType==0?"http://h5.seex.im/#/rules_tabel?body=c":"http://h5.seex.im/#/rules_tabel?body=b";
                 intent = new Intent(getActivity(), MyWebView.class);
                intent.putExtra(MyWebView.TITLE, "积分规则");
                intent.putExtra(MyWebView.WEB_URL, accessUrl);
                startActivity(intent);
                MobclickAgent.onEvent(getActivity(),"integral_rules_btn_clicked");
                break;
            case R.id.view_us:
                intent = new Intent(getActivity(), SCWebView.class);
                intent.putExtra("URL",Constants.h5_url_contact_us);
                intent.putExtra("TITLE", "联系我们");
                intent.putExtra("NEED_TOKEN", false);
                startActivity(intent);
                break;
            case R.id.giftpackage:
                intent = new Intent(getActivity(), SCWebView.class);
                intent.putExtra("URL",Constants.h5_url_gifts);
                intent.putExtra("TITLE", "我的礼包");
                intent.putExtra("NEED_TOKEN", true);
                startActivity(intent);
                break;
            case R.id.view_task:
                intent = new Intent(getActivity(), SCWebView.class);
                intent.putExtra("URL",String.format(Constants.h5_url_task, (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1), (int)SharedPreferencesUtils.get(getActivity(),Constants.USERTYPE,0)));
                intent.putExtra("TITLE", "活动中心");
                intent.putExtra("NEED_TOKEN", false);
                startActivityForResult(intent, 22);
                break;
            case R.id.btn_account:
//                intent = new Intent(getActivity(), RoyaltyActivityNew.class);
//                intent.putExtra("userType",userInfo.getUserType());
//                startActivity(intent);

                String title = "我的收益";
                String url = Constants.seller_bill_income_usl;
                if(userInfo.getUserType() == 0){
                    title = "我的消费";
                    url = Constants.buyer_bill_outcome_usl;
                }

                intent = new Intent(getActivity(), SCWebView.class);
                intent.putExtra("URL",url);
                intent.putExtra("TITLE", title);
                intent.putExtra("NEED_TOKEN", true);
                startActivity(intent);
                break;
//            case R.id.btn_regcharge:
//                intent = new Intent(getActivity(), RechargeActivity.class);
//                intent.putExtra("balance",userInfo.getMoney());
//                startActivity(intent);
//                MobclickAgent.onEvent(getActivity(), "recharge_btn_clicked");
//                break;
            case R.id.user_finance:
                if (userInfo.getIsPerfect() == 1) {
                    intent = new Intent(getActivity(), PerfectActivity.class);
                    startActivity(intent);
                    return;
                }
                if(user_finance.getText().equals("充值")){
                    intent = new Intent(getActivity(), RechargeActivity.class);
                    intent.putExtra("balance",userInfo.getMoney());
                    startActivity(intent);
                    MobclickAgent.onEvent(getActivity(), "recharge_btn_clicked");

                }else{

                    intent = new Intent(getActivity(), WithdrawInfoActivity.class);
                    //intent = new Intent(getActivity(), PayChooseActivity.class);
                    //intent.putExtra("money", Long.parseLong(userInfo.getMoney()));
                    startActivityForResult(intent, 10086);
                }

                break;
//            case R.id.btn_advance:
//                if (userInfo != null) {
//                    if (userInfo.getIsHaveBank() == 1) {
//                        intent = new Intent(getActivity(), BankActivity.class);
//                        intent.putExtra("balance", userInfo.getMoney());
//                    } else {
//                        intent = new Intent(getActivity(), AdvanceActivity.class);
//                        intent.putExtra("balance", userInfo.getMoney());
//                        LogTool.setLog("userInfo.getMoney():", userInfo.getMoney());
//                    }
//                    startActivity(intent);
//                }
//                break;
            case R.id.btn_sao:
                if (userInfo.getIsPerfect() == 1) {
                    intent = new Intent(getActivity(), PerfectActivity.class);
                    startActivity(intent);
                    return;
                }
                if(userInfo.getSex()==1){
                    intent = new Intent(getActivity(), AnchorDialogActvity.class);
                    startActivity(intent);
                }else{
                    if(userInfo.getIsVideoAudit()==3){

                    }else{
                        intent = new Intent(getActivity(), BecomeSellerActivity.class);
                        startActivity(intent);
                    }
                }

//                float money = Float.parseFloat(userInfo.getMoney());
//                LogTool.setLog("余额 money:",money);
//                if(!Tools.isEmpty(userInfo.getMoney()) &&money>10){//余额超过10元，不允许申请播主
//                    ToastUtils.makeTextAnim(getActivity(), R.string.money_more_10).show();
//                    return;
//                }
//                if (userInfo.getIsPerfect() == 1) {
//                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alertdialog_dog, null);
//                    final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(getActivity(), layout, R.string.noperfect, R.string.to_userinfo);
//                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                            Intent intent = new Intent(getActivity(), PerfectActivity.class);
//                            startActivity(intent);
//                        }
//                    });
//                    return;
//                }
//                if (userInfo.getIsVideoAudit() == 3) {
//                    btn_regcharge.setVisibility(View.VISIBLE);
//                    btn_advance.setVisibility(View.GONE);
//                    ToastUtils.makeTextAnim(getActivity(), R.string.authing).show();
//                    return;
//                }
//                if (userInfo.getIsVideoAudit() == 4) {
//                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alertdialog_dog, null);
//                    final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(getActivity(), layout, R.string.authing_black, R.string.i_know);
//                    layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    return;
//                }
//
//                View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alertdialog_dog_nor, null);
//                final android.app.AlertDialog dialog = DialogTool.createDogDialog(getActivity(), layout, R.string.tobeSeller, R.string.cancle, R.string.tobe);
//                layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        dialog.dismiss();
//                        /*Intent intent = new Intent(getActivity(), AgreementActivity.class);
//                        intent.putExtra("isReg", false);
//                        intent.putExtra("headURL", userInfo.getPortrait());
//                        startActivity(intent);*/
//
//                        BecomeSellerWebActivity.skipActivity(getActivity(), userInfo);
//                    }
//                });
//                MobclickAgent.onEvent(getActivity(), "apply_master_certification_clicked");
                break;

            case R.id.linear_price:
                intent = new Intent(getActivity(), EditPriceActivity.class);
                intent.putExtra(Constants.IntentKey, userInfo);
                startActivityForResult(intent, 0);
                break;
            case R.id.user_name:
//                intent = new Intent(getActivity(), RegActivity.class);
//                startActivity(intent);
                break;
            case R.id.view_black:
                intent = new Intent(getActivity(), BlackListActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(getActivity(), "black_list_selected");
                break;
            case R.id.view_feedback:
                intent = new Intent(getActivity(), FeedBackActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(getActivity(), "feedback_selected");
                break;
//            case R.id.view_share:
////                getShareInfo();
//                /*Intent intent1 = new Intent(getActivity(), ShareAndAward.class);
//                startActivity(intent1);
//                MobclickAgent.onEvent(getActivity(), "shares_money_selected");*/
//
//                Intent intent1 = new Intent(getActivity(), ShareEarnMoneyActivity.class);
//                startActivity(intent1);
//                MobclickAgent.onEvent(getActivity(), "shares_money_selected");
//                break;
            case R.id.edit:
                if (userInfo.getIsPerfect() == 1) {
                    intent = new Intent(getActivity(), PerfectActivity.class);
                    startActivity(intent);
                    return;
                }
                intent= new Intent(getActivity(), UserInfoNewActivity123.class);
                intent.putExtra("userInfo", userInfo);
                intent.putExtra(ConfigConstants.BecomeSeller.PARAM_PAGE_FROM, ConfigConstants.BecomeSeller.PARAM_FROM_PROFILEINFO);
                startActivityForResult(intent,55);
                break;
            case R.id.view_tongzhi:
                intent = new Intent(getActivity(), NotifActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.view_gonggao:
//                intent = new Intent(getActivity(), MyWebView.class);
//                intent.putExtra(MyWebView.TITLE, "公告");
//                intent.putExtra(MyWebView.WEB_URL, Constants.AD_URL);
//                getActivity().startActivity(intent);
                break;

        }
    }


    @Override
    public void onResume() {
        super.onResume();
        disNoti();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void getUserInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        Map map = new HashMap();
        String head = jsonUtil.httpHeadToJson(getActivity());
        map.put("head", head);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getUserInfo, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ptrClassicFrameLayout.finishRefresh();
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                ptrClassicFrameLayout.finishRefresh();
                LogTool.setLog("getUserInfo:", jsonObject);
                if (jsonObject == null) {
                    ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("resultCode");
                    if (resultCode == 2) {
                        user_name.setText("请登录");
//                        user_id.setText("");
//                        btn_edit_name.setVisibility(View.GONE);
                        Intent intent = new Intent(getActivity(), LoadActivity.class);
                        startActivity(intent);
                        return;
                    }
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.equals("null") || dataCollection.length() == 0) {
                        return;
                    }

                    userInfo = jsonUtil.jsonToUserInfo(dataCollection);
                    LogTool.setLog("getUserInfo:", userInfo);
                    SharedPreferencesUtils.put(getActivity(), Constants.USERICON, userInfo.getPortrait());
                    SharedPreferencesUtils.put(getActivity(), Constants.NICKNAME, userInfo.getNickName());
                    SharedPreferencesUtils.put(getActivity(), Constants.ISVIDEOAUDIT, userInfo.getIsVideoAudit());
                    SharedPreferencesUtils.put(getActivity(), Constants.USERTYPE, userInfo.getUserType());
                    SharedPreferencesUtils.put(getActivity(), Constants.USERPRICE, userInfo.getUserPrice());
                    SharedPreferencesUtils.put(getActivity(), Constants.SEX, userInfo.getSex());
                    SharedPreferencesUtils.put(getActivity(), Constants.ISPERFECT, userInfo.getIsPerfect());
                    SharedPreferencesUtils.put(getActivity(), Constants.NOTROUBLE, userInfo.getIsNotrouble());
                    SharedPreferencesUtils.put(getActivity(), Constants.ISVIDEOSWHIC, userInfo.getVideoAvaliable());
                    SharedPreferencesUtils.put(getActivity(), Constants.ISShowGIFT, userInfo.getGiftShowFlag());
                    String headIconURL = userInfo.getPortrait();
                    String nickname = userInfo.getNickName();

                    if(userInfo.getActivityRedFlag() == 2){
                        SharedPreferencesUtils.put(getActivity(), "TASK_READED", true);
                        noti_task.setVisibility(View.GONE);
                    }else{

                        SharedPreferencesUtils.put(getActivity(), "TASK_READED", false);
                    }
                    user_name.setText(nickname);
                    user_id.setText("ID:" + userInfo.getShowId());
                    tx_balance.setText(userInfo.getMoney());
                    //String str = "投诉率:" + (int) userInfo.getFuckedPercent() / 100 + "%";
                    int allIntegral = userInfo.getEndValue();
                    int currIntegral = userInfo.getIntegral();
//                    setProgress(currIntegral * 100 / allIntegral);
//                    text_need_integral.setText(Html.fromHtml("当前" + currIntegral / 10 + ",升级还需" +
//                            "<font color=#FF8A8A>" + (allIntegral - currIntegral) / 10+ "</font>"+"点"));
                    tx_curr.setText("VIP"+userInfo.getGrade());
                    int nextGrade=1;
                    if(!Tools.isEmpty(userInfo.getGrade())){
                        nextGrade = Integer.parseInt(userInfo.getGrade())+1;
                    }
                    switch_DND.setChecked(userInfo.getChatStatus()==2?true:false);
                    voideText.setText(userInfo.getChatStatus()==2?getString(R.string.seex_video_open):getString(R.string.seex_video_close));
                    switch_Voice.setChecked(userInfo.getVoiceChatStatus()==2?true:false);
                    voiceText.setText(userInfo.getVoiceChatStatus()==2?getString(R.string.seex_voice_open):getString(R.string.seex_voice_close));
                    tx_next.setText("VIP"+nextGrade);
                    tx_price.setText(userInfo.getUserPrice() + getString(R.string.seex_unit_prise_monye));
                    setOnlinStatus();
                    if(userInfo.getUserType()==Constants.AnchorTag){
                            switch_DND.setOnCheckedChangeListener(onCheckedChangeListener);
                            switch_Voice.setOnCheckedChangeListener(onCheckedChangeListener);
                    }
                    int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);

                    if(userInfo.getIsVideoAudit()==2){
                        btn_sao.setVisibility(View.GONE);
                    }

                    if(userInfo.getIsVideoAudit() == 3){
                        text_sao.setVisibility(View.VISIBLE);
                    }else{
                        text_sao.setVisibility(View.GONE);
                    }

                    if(userInfo.getUserType() == Constants.UserTag){
                        giftpackage.setVisibility(View.VISIBLE);
                        user_finance.setText("充值");
                        btn_price.setVisibility(View.GONE);
                        view.findViewById(R.id.view_DND).setVisibility(View.GONE);
                        view.findViewById(R.id.view_voice).setVisibility(View.GONE);
                    }else{
                        giftpackage.setVisibility(View.GONE);
                        user_finance.setText("提现");
                        btn_price.setVisibility(View.VISIBLE);
                        view.findViewById(R.id.view_DND).setVisibility(View.VISIBLE);
                        view.findViewById(R.id.view_voice).setVisibility(View.VISIBLE);
                    }
                    /*******************************************************************************************/
                    if (!Tools.isEmpty(headIconURL)) {
                        Uri uri = Uri.parse(headIconURL);
                        user_icon.setImageURI(uri);
                    } else {
                        user_icon.setImageResource(R.mipmap.xikemimi);
                    }
                    if(userInfo.getSex()==1){
                        gradeView.setImageResource(R.mipmap.my_boy_icon);
                    }else {
                        gradeView.setImageResource(R.mipmap.my_girl_icon);
                    }

                    jobView.setText(TextUtils.isEmpty(userInfo.getCustomJobName())?"未设置":userInfo.getCustomJobName());
                } catch (JSONException e) {
                    LogTool.setLog("getUserInfo  JSONException:", e.getMessage());
                } catch (Exception e) {
                    LogTool.setLog("getUserInfoException:", e.getMessage());
                }
            }
        });
//        user_icon_bg.setImageResource(R.mipmap.my_background);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 22:
            case 55:
            case 10086:
                getUserInfo();
                break;
            case 0:
                if (data != null) {
                    int mark = data.getIntExtra("mark", 0);
                    if (mark == 0) {
                        return;
                    }
                    int price_result = data.getIntExtra(Constants.IntentKey, 0);
                    tx_price.setText((int)price_result + ""+getString(R.string.seex_unit_prise_monye));
                    LogTool.setLog("onActivityResult data:", data);
                    getUserInfo();
                }
                break;
        }
    }


    private void conniSocket(){
        String session = get(getActivity(), Constants.SESSION, "") + "";
        if (!Tools.isEmpty(session)) {
            int isPerfect = (int) get(getActivity(), Constants.ISPERFECT, 1);
            if (isPerfect == 2) {
                if(!isServiceWork(getActivity(),"com.chat.seecolove.service.SocketService")){
                    Intent serviceIntent = new Intent(getActivity(), SocketService.class);
                    getActivity().startService(serviceIntent);
                }else{
                    try {
                        SocketService.getInstance().setPINGstatus("2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(sleepTime!=null){
                    sleepTime.cancel();
                    sleepTime=null;
                }
                sleepTime = new sleepTimeCount(3000, 1000);
                sleepTime.start();
            }
        }
    }
    private sleepTimeCount sleepTime;
    private class sleepTimeCount extends CountDownTimer {

        public sleepTimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            initData();
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }
    }

    public boolean isServiceWork(Context mContext, String serviceName) {
        boolean isWork = false;
        ActivityManager myAM = (ActivityManager) mContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> myList = myAM.getRunningServices(40);
        if (myList.size() <= 0) {
            return false;
        }
        for (int i = 0; i < myList.size(); i++) {
            String mName = myList.get(i).service.getClassName().toString();
            if (mName.equals(serviceName)) {
                isWork = true;
                break;
            }
        }
        return isWork;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleDatas(userInfo);
        if(sleepTime!=null){
            sleepTime.cancel();
            sleepTime=null;
        }
    }

    public void disNoti(){
        boolean ifnotice = (boolean) SharedPreferencesUtils.get(getActivity(), MessageFragment.IF_NOTICE, true);
        try {
            if(ifnotice){
                noti_View.setVisibility(View.VISIBLE);
            }else{
                noti_View.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            int isPerfect = (int) SharedPreferencesUtils.get(getActivity(), Constants.ISPERFECT, 1);
            if (isPerfect == 1) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_noperfect).show();
                compoundButton.setChecked(false);
                return;
            }
            if( !switch_Voice.isChecked()&&!switch_DND.isChecked()){
                if(userInfo!=null){
                    userInfo.setStatus(1);
                }
            }else {
                if(userInfo!=null){
                    try {
                        SocketService.getInstance().setPINGstatus("2");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
           switch (compoundButton.getId()){
               case R.id.switch_DND:
                   if(b){
                       userInfo.setChatStatus(2);
                       notrouble(0,2);
                   }else{
                       userInfo.setChatStatus(1);
                       notrouble(0,1);
                   }
                   if(b){
                       voideText.setText(getString(R.string.seex_video_open));
                   }else{
                       voideText.setText(getString(R.string.seex_video_close));
                   }
                   break;
               case R.id.switch_voice:
                   if(b){
                       userInfo.setVoiceChatStatus(2);
                       notrouble(1,2);
                   }else{
                       userInfo.setVoiceChatStatus(1);
                       notrouble(1,1);
                   }
                   if(b){
                       voiceText.setText(getString(R.string.seex_voice_open));
                   }else{
                       voiceText.setText(getString(R.string.seex_voice_close));
                   }
                   break;
           }
            setOnlinStatus();
        }
    };

    private void notrouble(final int mType,final int notrouble) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        if(userInfo==null){
            return;
        }
        String head = new JsonUtil(getActivity()).httpHeadToJson(getActivity());

        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        final int usertype = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        String str = "" + userID +""+ userInfo.getSex() +""+ notrouble + "notrouble";
        String key = Tools.md5(str);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        map.put("sex", userInfo.getSex());
        map.put("flag", notrouble);
        map.put("key", key);
        map.put("type", mType);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().notrouble, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("----notrouble onSuccess:", jsonObject);
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }
//                if(!isServiceWork(getActivity(),"com.chat.seecolove.service.SocketService")){
//                    Intent serviceIntent = new Intent(getActivity(), SocketService.class);
//                    getActivity().startService(serviceIntent);
//                }else{
//                    try {
//                        SocketService.getInstance().setPINGstatus("2");
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }

//                SharedPreferencesUtils.put(getActivity(), Constants.NOTROUBLE, flag);
//                Intent mIntent = new Intent(Constants.ACTION_SELLER_ISONLINE);
//                mIntent.putExtra("isNotrouble", 1 + "");
//               getActivity().sendBroadcast(mIntent);
            }
        });
    }


    private void setOnlinStatus(){
       if(userInfo.getChatStatus()==1&&userInfo.getVoiceChatStatus()==1){
           userStatus.setText("勿扰");
           userStatus.setBackgroundResource(R.drawable.btn_state_nodis);
           userStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
       }else{
           userStatus.setText("在线");
           userStatus.setBackgroundResource(R.drawable.btn_state_online);
           userStatus.setTextColor(ContextCompat.getColor(getActivity(),R.color.white));
       }
    }


}
