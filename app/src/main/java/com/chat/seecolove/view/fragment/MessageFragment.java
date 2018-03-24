package com.chat.seecolove.view.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chat.seecolove.view.activity.AddRequestActivity;
import com.chat.seecolove.view.activity.MainActivity;
import com.chat.seecolove.widget.recycleview.suspension.HeaderRecyclerAndFooterWrapperAdapter;
import com.chat.seecolove.widget.recycleview.suspension.ViewHolder;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.RequestCallbackWrapper;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;
import com.netease.nimlib.sdk.msg.model.QueryDirectionEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.umeng.analytics.MobclickAgent;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.CallLog;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.tools.EasyPermission;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.ChatActivity;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.view.activity.PerfectActivity;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.activity.LoadActivity;
import com.chat.seecolove.view.adaper.MsgRecentContactAdapter;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

import static com.chat.seecolove.tools.SharedPreferencesUtils.get;


public class MessageFragment extends BaseFragment implements View.OnClickListener, EasyPermission.PermissionCallback {

    private View view;
    private ViewPager message_fragment_viewpager;
    private SocketService socketService;
    private CallLog bean;
    private float orderPrice;
    private int status = 0;//0是全部，1是呼入，2是呼出，3是未接
    private RecyclerView msg_recyclerView;
    private TextView no_msg_data;
    private FrameLayout footerView;
    private int calllog_page = 1;
    private int totalPage;
    private List<CallLog> callLogs;
    private MsgRecentContactAdapter msgRecentContactAdapter = null;
    //    private TextView no_data;
    private LayoutInflater mInflater;
    private View imMsg;//页卡视图
    private List<View> mViewList = new ArrayList<>();//页卡视图集合

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    calllog_page = 1;
                    getInfo();
                    break;
            }
        }
    };
    public static Handler recordHandler = null;
    private HeaderRecyclerAndFooterWrapperAdapter mHeaderAndFooterWrapper;
    private TextView NodataView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.message_fragment_layout, null);
        NodataView=(TextView)view.findViewById(R.id.no_data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getStatusViewhight();
        }
        mInflater = inflater;
        initViews();
        recordHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        getMessageRecord();
//                        LogTool.setLog("222222222222","");
                        break;
                }
            }
        };
        return view;
    }
    private  void getStatusViewhight(){
        int resourceId = getActivity().getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getActivity().getResources().getDimensionPixelSize(resourceId);
        View statusView = view.findViewById(R.id.status_bar_fix);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                statusBarHeight);
        statusView.setLayoutParams(params);
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
        initData(false);
        getMessageRecord();
        Intent notify = new Intent(Constants.ACTION_NOTIFY_NUM);
        getActivity().sendBroadcast(notify);
        Intent mIntent = new Intent(Constants.ACTION_MSG_CHANGE);
        getActivity().sendBroadcast(mIntent);
    }

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    public Map<String, String> getNMap() {
        if (msgRecentContactAdapter != null) {
            return msgRecentContactAdapter.getnMap();
        } else {
            return null;
        }

    }

    /**
     * 刷新系统消息数量
     */
    public void notifyDataSetChanged() {
        msgRecentContactAdapter.notifyDataSetChanged();

        if(msgRecentContactAdapter.getList().size()==0){
            NodataView.setVisibility(View.VISIBLE);
        }else{
            NodataView.setVisibility(View.GONE);
        }

        getMessageRecord();
    }

    public void msgDataSetChanged() {
        queryRecentContacts();
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);


            // 滑到底部
            if (!recyclerView.canScrollVertically(1)) {
                if (footerView.getVisibility() == View.GONE) {
                    calllog_page++;
                    if (calllog_page > totalPage) {
                        footerView.setVisibility(View.GONE);
                        return;
                    }
                    getInfo();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            //dx表示横向
        }
    };
    private void setListeners() {
    }

    /**
     * callFlush   true  呼叫、通话结束 刷新
     **/
    public void initData(boolean callFlush) {
        if (callFlush) {
            handler.obtainMessage(0).sendToTarget();
        } else {
        }
    }

    private void initViews() {
        message_fragment_viewpager = (ViewPager) view.findViewById(R.id.message_fragment_viewpager);

        TextView tv = new TextView(getActivity());
        tv.setText("IM");
        imMsg = mInflater.inflate(R.layout.msg_notify_layout, null);

        msg_recyclerView = (RecyclerView) imMsg.findViewById(R.id.msg_recyclerView);
        no_msg_data = (TextView) imMsg.findViewById(R.id.no_msg_data);
        msg_recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //添加分割线
        mViewList.add(imMsg);
        MyPagerAdapter mAdapter = new MyPagerAdapter(mViewList);
        message_fragment_viewpager.setAdapter(mAdapter);//给ViewPager设置适配器

    }


    @Override
    public void onClick(View v) {

    }

    private List<RecentContact> recents;


    private void queryRecentContacts() {
        NIMClient.getService(MsgService.class).queryRecentContacts()
                .setCallback(new RequestCallbackWrapper<List<RecentContact>>() {
                    @Override
                    public void onResult(int code, List<RecentContact> recents, Throwable e) {
                        // recents参数即为最近联系人列表（最近会话列表）
                        if (recents == null) {
                            recents = new ArrayList<RecentContact>();
                        }
                        ArrayList<RecentContact> tempRecent = new ArrayList<>();
                        ArrayList<RecentContact> mRecent = new ArrayList<>();
                        for (int i = 0; i < recents.size(); i++) {
                            RecentContact recentContact = recents.get(i);

                            if (recentContact.getFromAccount().equals(Constants.sys_buyer) || recentContact.getFromAccount().equals(Constants.sys_seller)
                                    || recentContact.getContactId().equals(Constants.sys_buyer) || recentContact.getContactId().equals(Constants.sys_seller)) {
                                mRecent.add(recentContact);
                            } else {
                                tempRecent.add(recentContact);
                            }
                        }

                        for (int i = mRecent.size() - 1; i >= 0; i--) {
                            tempRecent.add(0, mRecent.get(i));
                        }

                        MessageFragment.this.recents = tempRecent;
                        if (msgRecentContactAdapter == null) {
                            msgRecentContactAdapter = new MsgRecentContactAdapter(getActivity(), MessageFragment.this.recents);

                            if(MessageFragment.this.recents.size()==0){
                                NodataView.setVisibility(View.VISIBLE);
                            }else{
                                NodataView.setVisibility(View.GONE);
                            }
                            mHeaderAndFooterWrapper = new HeaderRecyclerAndFooterWrapperAdapter(msgRecentContactAdapter) {
                                @Override
                                protected void onBindHeaderHolder(ViewHolder holder, int headerPos, int layoutId, Object o) {
                                    switch (layoutId) {
                                        case R.layout.seex_interset_ui:
                                            View friends_notify = holder.getView(R.id.friends_notify);
                                            final TextView tvNewFriendCount = (TextView) friends_notify.findViewById(R.id.tv_new_friend_count);
                                            int friendReqNum = (int) o;
                                            if (friendReqNum > 0) {
                                                tvNewFriendCount.setVisibility(View.VISIBLE);
                                                if (friendReqNum > Constants.MAIL_MAX_FRIENDS_REQ) {
                                                    tvNewFriendCount.setText(Constants.MAIL_MAX_FRIENDS_REQ + "");
                                                } else {
                                                    tvNewFriendCount.setText(friendReqNum + "");
                                                }
                                            } else {
                                                tvNewFriendCount.setVisibility(View.GONE);
                                            }
                                            friends_notify.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    MainActivity mainActivity = (MainActivity) getActivity();
                                                    //                                tvNewFriendCount.setVisibility(View.GONE);
                                                    SharedPreferencesUtils.put(getActivity(), Constants.SHOWREDPOINT_INSET_NUM, 0);

                                                    int friendReqNum = (int) get(getActivity(), Constants.SHOWREDPOINT_INSET_NUM, 0);
                                                    LogTool.setLog("FriendsFragment--friendReqNum---->", friendReqNum);
                                                    mHeaderAndFooterWrapper.clearHeaderView();
                                                    mHeaderAndFooterWrapper.setHeaderView(0, R.layout.seex_interset_ui, friendReqNum);
                                                    mHeaderAndFooterWrapper.notifyDataSetChanged();
                                                    AddRequestActivity.startActivity(getActivity());
                                                    MobclickAgent.onEvent(getActivity(), "addressBook_newFriendClick_240");
                                                }
                                            });
                                            break;
                                    }
                                }
                            };
                            int friendReqNum = (int) get(getActivity(), Constants.SHOWREDPOINT_INSET_NUM, 0);
//                            mHeaderAndFooterWrapper.setHeaderView(0, R.layout.seex_interset_ui, friendReqNum);

                            noitfJson = (String) SharedPreferencesUtils.get(getActivity(), new Constants().getMessageRecord, "[]");
                            msgRecentContactAdapter.setOnItemClickListener(new MsgRecentContactAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view, RecentContact data) {
                                    String info = (String) SharedPreferencesUtils.get(getActivity(), data.getContactId(), "");

                                    LogTool.setLog("info-----------", info);
                                    if (!Tools.isEmpty(info)) {
                                        try {
                                            LogTool.setLog(data.getFromAccount() + "-----------", info);
                                            JSONObject jsonObject = new JSONObject(info);
                                            String id = jsonObject.getString("userid");
                                            String nickname = jsonObject.getString("nickname");
                                            String headurl = jsonObject.getString("headurl");

                                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                                            intent.putExtra(ChatActivity.CHAT_ID, id);
                                            intent.putExtra(ChatActivity.CHAT_NAME, nickname);
                                            intent.putExtra(ChatActivity.CHAT_YXID, data.getContactId() + "");
                                            intent.putExtra(ChatActivity.CHAT_ICON, headurl);
                                            startActivity(intent);
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }

                                    } else {
                                        if (data.getFromAccount().equals(Constants.sys_buyer) || data.getFromAccount().equals(Constants.sys_seller)
                                                || data.getContactId().equals(Constants.sys_buyer) || data.getContactId().equals(Constants.sys_seller)
                                                ) {

                                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                                            intent.putExtra(ChatActivity.CHAT_ID, data.getFromAccount());
                                            intent.putExtra(ChatActivity.CHAT_NAME, "西可客服");
                                            intent.putExtra(ChatActivity.CHAT_YXID, data.getFromAccount() + "");
                                            intent.putExtra(ChatActivity.CHAT_ICON, "");
                                            startActivity(intent);
                                            Map<String, String> map = new HashMap<String, String>();
                                            map.put("from", "msg");
                                            MobclickAgent.onEvent(getActivity(), "xike_small_Secretary_clicked", map);
                                        } else {
                                            queryAndSaveUserInfo(data.getFromAccount());
                                        }

                                    }

                                }
                            });
                            msgRecentContactAdapter.setOnItemLongClickListener(new MsgRecentContactAdapter.OnRecyclerViewItemLongClickListener() {
                                @Override
                                public void onItemLongClick(View view, final RecentContact data) {
                                    if (data != null && view.getTag() instanceof RecentContact) {
                                        new AlertDialog.Builder(getActivity())
                                                .setMessage(R.string.seex_delete_tip)
                                                .setNegativeButton(R.string.seex_cancle, null)
                                                .setPositiveButton(R.string.seex_sure, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        deleteMsgHistory(data);
                                                        msgRecentContactAdapter.deleteViewOfID(data);
                                                        if(msgRecentContactAdapter.getList().size()==0){
                                                            NodataView.setVisibility(View.VISIBLE);
                                                        }else{
                                                            NodataView.setVisibility(View.GONE);
                                                        }
                                                    }
                                                })
                                                .create()
                                                .show();
                                    }

                                }
                            });

                            msg_recyclerView.setAdapter(msgRecentContactAdapter);

                            msgRecentContactAdapter.notifyDataSetChanged();
                        } else {
                            msgRecentContactAdapter.setList(MessageFragment.this.recents);
                        }

                        String getNoticeNew = (String) SharedPreferencesUtils.get(getActivity(), "getNoticeNew", "");
                        try {
                            setNoticeData(new JSONObject(getNoticeNew));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                        msgRecentContactAdapter.setNoitfJson(noitfJson);
                        msgRecentContactAdapter.notifyDataSetChanged();
                        try {
                            if(MessageFragment.this.recents.size()==0){
                                NodataView.setVisibility(View.VISIBLE);
                            }else{
                                NodataView.setVisibility(View.GONE);
                            }
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 当用户信息本地未保存时查询,查询成功后跳转profile
     *
     * @param accountId
     */
    private void queryAndSaveUserInfo(final String accountId) {

        IMMessage messages = MessageBuilder.createTextMessage(
                accountId, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                "" // 文本内容
        );

        NIMClient.getService(MsgService.class).queryMessageListEx(messages, QueryDirectionEnum.QUERY_OLD, 1, false).setCallback(new RequestCallback<List<IMMessage>>() {
            @Override
            public void onSuccess(List<IMMessage> list) {
                if (list.size() > 0) {
                    IMMessage msg = list.get(0);
                    Map<String, Object> map = msg.getRemoteExtension();
                    if (map == null || map.size() == 0) {
                        return;
                    }
                    JSONObject jsonObject = new JSONObject(map);
                    SharedPreferencesUtils.put(getActivity(), accountId, jsonObject.toString());

                    String id = (String) map.get("userid");
                    Intent intent = new Intent(getActivity(), UserProfileInfoActivity.class);
                    intent.putExtra(UserProfileInfoActivity.PROFILE_ID, id + "");
                    startActivity(intent);
                }

            }

            @Override
            public void onFailed(int i) {
                LogTool.setLog(" 失败代码：", i);
            }

            @Override
            public void onException(Throwable throwable) {
                LogTool.setLog(" 异常代码：", "");
                throwable.printStackTrace();
            }
        });
    }

    /**
     * 删除会话
     */
    private void deleteMsgHistory(RecentContact data) {
        NIMClient.getService(MsgService.class).clearChattingHistory(data.getFromAccount(), SessionTypeEnum.P2P);
        NIMClient.getService(MsgService.class).clearChattingHistory(data.getContactId(), SessionTypeEnum.P2P);
        NIMClient.getService(MsgService.class).deleteRecentContact(data);
        this.recents.remove(data);
        msgRecentContactAdapter.notifyDataSetChanged();

        try {
            if(MessageFragment.this.recents.size()==0){
                NodataView.setVisibility(View.VISIBLE);
            }else{
                NodataView.setVisibility(View.GONE);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

    }


    private void getInfo() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        String head = jsonUtil.httpHeadToJson(getActivity());
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        final int userType = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0);
        Map map = new HashMap();
        map.put("head", head);
        map.put("userId", userID);
        map.put("userType", userType);
        map.put("status", status);
        map.put("pageNo", calllog_page);
        map.put("pageSize", 100);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getCallHistory, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getCallHistory:", jsonObject);
                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
                    try {
                        totalPage = jsonObject.getInt("totalPage");
                        String dataCollection = jsonObject.getString("dataCollection");
                        if (!Tools.isEmpty(dataCollection)) {
                            if (calllog_page > 1) {
                                if (callLogs != null) {
                                    List<CallLog> temps = jsonUtil.jsonToCallLogs(dataCollection);
                                    callLogs.addAll(temps);
                                }
                                footerView.setVisibility(View.GONE);
                            } else {
                                callLogs = jsonUtil.jsonToCallLogs(dataCollection);
                                int itemCount = 1;
                                List<CallLog> temps = new ArrayList<>();
                                /****
                                 * 第一种
                                 * **/
                                StringBuffer buffer;
                                int size = callLogs.size();
                                for (int i = 0; i < callLogs.size(); i++) {
                                    itemCount = 1;
                                    buffer = new StringBuffer();
                                    CallLog callLog, callLogNext = null;
                                    callLog = callLogs.get(i);
                                    buffer.append(callLog.getId());
                                    callLog.setItemCount(itemCount);
                                    int id, idNext;
                                    id = userType == 0 ? callLog.getSellerId() : callLog.getBuyerId();

                                    List<CallLog> temps2 = new ArrayList<>();
                                    for (int j = i + 1; j < callLogs.size(); j++) {
                                        callLogNext = callLogs.get(j);
                                        idNext = userType == 0 ? callLogNext.getSellerId() : callLogNext.getBuyerId();
                                        if (id == idNext && callLog.getCallFlag() == callLogNext.getCallFlag()
                                                && callLog.getIsAnswer() == callLogNext.getIsAnswer()) {
                                            itemCount++;
                                            callLog.setItemCount(itemCount);
                                            temps2.add(callLogNext);
                                            buffer.append("," + callLogNext.getId());

                                        } else {
                                            break;
                                        }
                                    }
                                    callLogs.removeAll(temps2);
                                    callLog.setIds(buffer + "");
                                    temps.add(callLog);
                                }

                            }
                        }


                    } catch (JSONException e) {

                    }
                }

            }
        });

    }


    public void createOrder() {
        String session = SharedPreferencesUtils.get(getActivity(), Constants.SESSION, "") + "";
        if (Tools.isEmpty(session)) {//本地session不存在
            Intent intent = new Intent(getActivity(), LoadActivity.class);
            startActivity(intent);
            return;
        }
        int isPerfect = (int) SharedPreferencesUtils.get(getActivity(), Constants.ISPERFECT, 1);
        if (isPerfect == 1) {
            View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alertdialog_dog, null);
            final android.app.AlertDialog dialog = DialogTool.createDogDialogSingle(getActivity(), layout, R.string.seex_noperfect, R.string.seex_to_userinfo);
            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(getActivity(), PerfectActivity.class);
                    startActivity(intent);
                }
            });
            return;
        }
        int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
        if (userType == bean.getUserType()) {
            if (userType == 0) {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_c_c_video).show();
            } else {
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_b_b_video).show();
            }
            return;
        }
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        if (socketService == null) {
            socketService = SocketService.getInstance();
            if (socketService == null) {
                Intent intent = new Intent(getActivity(), LoadActivity.class);
                startActivity(intent);
                return;
            }
        }
        showProgress(getActivity(), R.string.seex_progress_text);
        if (Build.VERSION.SDK_INT >= 23) {
            EasyPermission.with(this)
                    .addRequestCode(Constants.CAMERA_RECORD)
                    .permissions(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
                    .request();
        } else {
            ThreadTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    if (!Tools.isCameraCanUse()) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(R.string.seex_no_Camera_Permission)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                                startActivity(intent);
                                            }
                                        })
                                        .create()
                                        .show();
                                progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    if (!Tools.isVoicePermission(getActivity())) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(getActivity())
                                        .setMessage(R.string.seex_no_Voice_Permission)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                                                startActivity(intent);
                                            }
                                        })
                                        .create()
                                        .show();
                                progressDialog.dismiss();
                            }
                        });
                        return;
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toCreate();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onPermissionGranted(int requestCode, List<String> perms) {
        switch (requestCode) {
            case Constants.CAMERA_RECORD:
                toCreate();
                break;
        }
    }

    @Override
    public void onPermissionDenied(int requestCode, List<String> perms) {
        //可选的,跳转到Settings界面
        //        EasyPermission.checkDeniedPermissionsNeverAskAgain(this, getString(R.string.rationale_ask_again),
        //                R.string.setting, R.string.cancel, null, perms);
        new AlertDialog.Builder(getActivity())
                .setMessage(R.string.seex_tip_Permission)
                .setCancelable(false)
                .setPositiveButton(R.string.seex_goto_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        startActivity(intent);
                    }
                })
                .create()
                .show();
        progressDialog.dismiss();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    private void toCreate() {
        if (socketService.mSocket == null || !socketService.mSocket.connected()) {
            socketService.initSocket(true, 1);
            return;
        }
        final int usertype = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
        final int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);

        int sex = (int) SharedPreferencesUtils.get(getActivity(), Constants.SEX, 0);
        final int sellerId = usertype == 0 ? bean.getSellerId() : userID;
        final int buyerId = usertype == 0 ? userID : bean.getBuyerId();
        int sellerSex = usertype == 0 ? bean.getSex() : sex;
        final int callFlag = usertype == 0 ? 1 : 2;
        Map map = new HashMap();
        String head = jsonUtil.httpHeadToJson(getActivity());
        map.put("head", head);
        map.put("sellerId", sellerId);
        map.put("buyerId", buyerId);
        map.put("price", orderPrice);
        map.put("sellerSex", sellerSex);
        map.put("callFlag", callFlag);
        String str = "" + sellerId + buyerId + orderPrice + sellerSex + callFlag + "order";
        String key = Tools.md5(str);
        map.put("key", key);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().create, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                progressDialog.dismiss();
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                if (Tools.jsonResult(getActivity(), jsonObject, progressDialog)) {
                    return;
                }
                progressDialog.dismiss();
                try {
                    if (callFlag == 1) {
                        int resultCode = jsonObject.getInt("resultCode");
                        if (resultCode == 6) {//卖家价格与本地价格不一致，需要弹框提示买家
                            final double newPrice = jsonObject.getDouble("dataCollection");
                            orderPrice = (float) newPrice;
                            String text = "您呼叫的播主最新价格为" + newPrice + "元/分钟，" + "是否继续呼叫？";
                            View layout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alertdialog_dog_nor, null);
                            final android.app.AlertDialog dialog = DialogTool.createDogDialog(getActivity(), layout, text, R.string.seex_cancle, R.string.seex_sure);
                            dialog.setCancelable(false);
                            layout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    createOrder();
                                }
                            });
                            return;
                        }
                    }
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (dataCollection == null || dataCollection.length() == 0) {
                        return;
                    }
                    Order order = jsonUtil.jsonToOrder(dataCollection);
                    if (order == null) {
                        ToastUtils.makeTextAnim(getActivity(), "呼叫失败！").show();
                        return;
                    }
                    String account = (String) SharedPreferencesUtils.get(getActivity(), Constants.YUNXINACCID, "");
                    //声网
                    JSONArray jsonArray = jsonObject.getJSONArray("enjoyCollection");
                    socketService.gotoRoom(getActivity(), jsonArray, order, bean.getNickName(), bean.getHead(), order.friend(), order.getTargetYunxinAccid(), order.getToYunxinAccid(), order.getFromYunxinAccid());


                } catch (JSONException e) {

                }

            }
        });
    }

    private Observer<List<RecentContact>> messageObserver =
            new Observer<List<RecentContact>>() {
                @Override
                public void onEvent(List<RecentContact> messages) {

                }
            };

    private void addObserveRecentContact(boolean on_off) {

        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class)
                .observeRecentContact(messageObserver, on_off);
    }

    @Override
    public void onResume() {
        super.onResume();
        queryRecentContacts();
        // 进入最近联系人列表界面，建议放在onResume中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_ALL, SessionTypeEnum.None);
        addObserveRecentContact(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        // 退出聊天界面或离开最近联系人列表界面，建议放在onPause中
        NIMClient.getService(MsgService.class).setChattingAccount(MsgService.MSG_CHATTING_ACCOUNT_NONE, SessionTypeEnum.None);
        addObserveRecentContact(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleDatas(callLogs);
        recycleDatas(bean);
    }


    //ViewPager适配器
    class MyPagerAdapter extends PagerAdapter {
        private List<View> mViewList;

        public MyPagerAdapter(List<View> mViewList) {
            this.mViewList = mViewList;
        }

        @Override
        public int getCount() {
            return mViewList.size();//页卡数
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;//官方推荐写法
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mViewList.get(position));//添加页卡
            return mViewList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViewList.get(position));//删除页卡
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";//页卡标题
        }

    }

    private String noitfJson = "";

    /**
     * 查询系统通知
     */
    private void getMessageRecord() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        final JsonUtil jsonUtil = new JsonUtil(getActivity());
        String head = jsonUtil.httpHeadToJson(getActivity());
        Map map = new HashMap();
        map.put("head", head);
        map.put("page", 1);
        map.put("rows", 20);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getMessageRecord, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getMessageRecord---onSuccess:", jsonObject);
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }
                try {
                    noitfJson = jsonObject.getString("dataCollection");
                    SharedPreferencesUtils.put(getActivity(), new Constants().getMessageRecord, noitfJson);
                    if (msgRecentContactAdapter != null) {
                        msgRecentContactAdapter.setNoitfJson(noitfJson);
                        msgRecentContactAdapter.notifyDataSetChanged();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final String NOTICE_TIME = "NOTICE_TIME";

    public static final String IF_NOTICE = "IF_NOTICE";

    public interface NoticeNew {
        public void onSuccess();
    }

    /**
     * 获取最新公告
     */
    public void getNoticeNew(final NoticeNew noticeNew) {
        int userType = (int) SharedPreferencesUtils.get(getContext(), Constants.USERTYPE, 0);
        String noticeTime = (String) SharedPreferencesUtils.get(getContext(), NOTICE_TIME, "");

        final JsonUtil jsonUtil = new JsonUtil(getActivity());
        String head = jsonUtil.httpHeadToJson(getActivity());
        Map map = new HashMap();
        map.put("head", head);
        map.put("sysCode", 2 + "");
        map.put("userType", userType + "");
        map.put("noticeTime", noticeTime + "");


        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getNoticeNew, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                LogTool.setLog("getNoticeNew---onError:", request.toString());
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getNoticeNew---onSuccess:", jsonObject);
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }

                setNoticeData(jsonObject);
                if (noticeNew != null) {
                    noticeNew.onSuccess();
                }
            }
        });
    }

    private void setNoticeData(JSONObject jsonObject) {
        try {
            JSONArray array = jsonObject.getJSONObject("dataCollection").getJSONArray("dataList");
            int dataCount = jsonObject.getJSONObject("dataCollection").getInt("dataCount");
            Map<String, String> nMap = new HashMap<String, String>();
            if (array.length() > 0) {
                nMap.put("dataCount", "" + dataCount);
                nMap.put("sendTime", "" + array.getJSONObject(0).getString("sendTime"));
                nMap.put("noticeName", "" + array.getJSONObject(0).getString("noticeName"));
                if (msgRecentContactAdapter != null) {
                    msgRecentContactAdapter.setnMap(nMap);
                    SharedPreferencesUtils.put(getContext(), "getNoticeNew", jsonObject);
                    msgRecentContactAdapter.notifyDataSetChanged();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void updateNickName(String id) {
        String nn = (String) SharedPreferencesUtils.get(getContext(), Constants.NN_ + id, "");
        LogTool.setLog(id + ":", nn);
        if (!Tools.isEmpty(nn)) {
            View view = msgRecentContactAdapter.getViewMap().get(id);
            if (view != null) {
                TextView tv = (TextView) view.findViewById(R.id.chat_user_name);
                tv.setText(nn);
            }
        }
    }

}


