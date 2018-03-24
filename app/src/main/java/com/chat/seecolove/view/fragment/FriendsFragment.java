package com.chat.seecolove.view.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.chat.seecolove.tools.DialogTool;
import com.chat.seecolove.view.activity.CallLogActivity;
import com.chat.seecolove.view.activity.SeexEditActivity;
import com.chat.seecolove.view.activity.UserProfileInfoActivity;
import com.chat.seecolove.view.adaper.SeexFollowAdapter;
import com.chat.seecolove.widget.PopwindowFriend;
import com.chat.seecolove.widget.recycleview.RecyclerOnScrollListener;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.bean.FriendResule;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.db.SessionDao;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.BackgroundAlphaUtils;
import com.chat.seecolove.tools.InputMethodUtils;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.StringUtils;
import com.chat.seecolove.tools.ThreadTool;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.ChatActivity;
import com.chat.seecolove.view.activity.FindActivity;
import com.chat.seecolove.view.activity.MyApplication;
import com.chat.seecolove.widget.ClearableEditText;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

import static com.chat.seecolove.tools.GsonUtil.fromJson;
import static com.chat.seecolove.tools.SharedPreferencesUtils.get;

/**
 * 关注
 */
public class FriendsFragment extends BaseFragment implements View.OnClickListener {

    private View view;
    private View btn_add;
    private ImageView close_tip;
    private TextView no_data;
    private RecyclerView recyclerView;

    private LinearLayoutManager linearLayoutManager;
    private SocketService socketService;
    private float orderPrice;
    private int usertype;
    private PopupWindow editPop = null;
    private LayoutInflater mInflater = null;
    private int userId;
    private SessionDao sessionDao;
    //请求的起始页，默认为1
    private int currentPage = 1;
    //数据总页数，默认为0
    public int mTotalPage = 0;
    private int receiverUpdateNum;
    private Long updateTime;
    private Button but_calllog;
    private SmartRefreshLayout ptrClassicFrameLayout;

    public static FriendsFragment newInstance(String param1) {
        FriendsFragment fragment = new FriendsFragment();
        Bundle args = new Bundle();
        args.putString("agrs1", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_friends, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getStatusViewhight();
        }
        initViews();

        return view;
    }

    private void getStatusViewhight() {
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
        initData();
    }

    public void initData() {
        currentPage = 1;
        mTotalPage = 0;
        sessionDao = new SessionDao(getActivity());
        socketService = SocketService.getInstance();
        userId = (int) get(getActivity(), Constants.USERID, -1);
        updateTime = (Long) SharedPreferencesUtils.get(getActivity(), userId + Constants.MAIL_UPDATE_TIME, 0L);
        receiverUpdateNum = (int) SharedPreferencesUtils.get(getActivity(), userId + Constants.MAIL_UPDATE_NUM, 0);
        getDatas(mPage, false);
    }

    int mPage = 1;

    /**
     * 阈值判断，获取数据的途径
     *
     * @param receiverUpdateNum
     * @param updateTime
     * @param mailList
     */
    private void validateNumAndTime(int receiverUpdateNum, Long updateTime, ArrayList<FriendBean> mailList) {
        if (receiverUpdateNum < Constants.MAIL_UPDATE_MAX_NUM) {
            //当变化条数大于Constants.MAIL_UPDATE_MAX_NUM时，请求数据，否则，当时间变化大于Constants.MAIL_UPDATE_MAX_TIME时，请求数据
            if ((System.currentTimeMillis() - updateTime) > Constants.MAIL_UPDATE_MAX_TIME) {
            } else {
                showNoData();
                updateFriendList();
            }
        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<FriendsFragment> mActivity;

        public MyHandler(FriendsFragment activity) {
            mActivity = new WeakReference<FriendsFragment>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            final FriendsFragment fragment = mActivity.get();
            switch (msg.what) {
                case 0:
                    ArrayList<FriendBean> mailList = (ArrayList<FriendBean>) msg.obj;
                    fragment.validateNumAndTime(fragment.receiverUpdateNum, fragment.updateTime, mailList);
                    break;
                case 1:
                    final ArrayList<FriendBean> friendListResult = (ArrayList<FriendBean>) msg.obj;
                    ThreadTool.getInstance().execute(new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < friendListResult.size(); i++) {
                                fragment.sessionDao.saveMail(friendListResult.get(i));
                            }
                        }
                    });
                    if (Integer.valueOf(fragment.mTotalPage) > fragment.currentPage) {
                        //当totalPage > currentPage 时,添加数据，并且刷新列表
                        fragment.currentPage++;
                    } else {
                        fragment.showNoData();
                        fragment.updateFriendList();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public static FriendsFragment newInstance() {
        FriendsFragment fragment = new FriendsFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initViews() {
        close_tip = (ImageView) view.findViewById(R.id.close_tip);
        but_calllog = (Button) view.findViewById(R.id.but_calllog);
        no_data = (TextView) view.findViewById(R.id.no_data);
        btn_add = view.findViewById(R.id.btn_add);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        usertype = (int) get(MyApplication.getContext(), Constants.USERTYPE, 0);
        userId = (int) get(getActivity(), Constants.USERID, -1);
        view.findViewById(R.id.small_secretary).setOnClickListener(this);
        String userYunxinID = (String) SharedPreferencesUtils.get(getActivity(), Constants.YUNXINACCID, "");
        if (userYunxinID.equals(Constants.sys_buyer)) {
            view.findViewById(R.id.small_secretary).setVisibility(View.GONE);
        }
        mAdapter = new SeexFollowAdapter(getActivity());
        mAdapter.setList(infos);
//        View footview=getActivity().getLayoutInflater().inflate(R.layout.item_friends_header,null);
//        mHeaderAndFooterWrapper.setFooterView(footview);
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(ContextCompat.getColor(getActivity(), R.color.line_tran40))
                .sizeResId(R.dimen.divider)
                .build());
        recyclerView.setAdapter(mAdapter);
        ptrClassicFrameLayout = (SmartRefreshLayout) view.findViewById(R.id.refreshLayout);
        ptrClassicFrameLayout.setEnableLoadmore(false);
        ptrClassicFrameLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                mPage = 1;
                mAdapter.setdefFootView(false);
                getDatas(mPage, false);
            }
        });
        mAdapter.setOnItemClickListener(new SeexFollowAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, FriendBean data) {
                Intent intent=new Intent();
                switch (view.getId()){
                        case R.id.call_status:
                            checkBean=data;
                            intent.setClass(getActivity(),SeexEditActivity.class);
                            intent.putExtra(SeexEditActivity.ModeSingen,SeexEditActivity.ReMark_Mode);
                            intent.putExtra(Constants.IntentKey,data.getNickName());
                            startActivityForResult(intent,1);
                            break;
                            default:
                                 intent = new Intent(getActivity(), UserProfileInfoActivity.class);
                                intent.putExtra(UserProfileInfoActivity.PROFILE_ID, data.getFollowId() + "");
                                startActivity(intent);
                                break;
                }
            }
        });
        mAdapter.setOnItemLongClickListener(new SeexFollowAdapter.OnRecyclerViewItemLongClickListener() {
            @Override
            public void onItemLongClick(View view, FriendBean data) {
                deletaDialog(data);
            }
        });
    }


    RecyclerOnScrollListener recyclerOnScrollListener = new RecyclerOnScrollListener() {
        @Override
        public void onLoadMore() {
            Log.i("aa", "onLoadMore===================");
            if (mAdapter.isCanLoadMore()) {
                if (infos == null || infos.size() == 0) {
                    return;
                }
                mPage++;
                LogTool.setLog("底部加载page:", mPage);
                if (mPage > mTotalPage) {
                    mAdapter.setCanLoadMore(false);
                    mAdapter.setdefFootView(true);
                    mAdapter.notifyDataSetChanged();
//                    ToastUtils.makeTextAnim(getActivity(), "数据已经加载到最底部了").show();
                    return;
                }
                getDatas(mPage, true);
            }
        }
    };
    private void deletaDialog(final FriendBean data) {
        View viewlayout = LayoutInflater.from(getActivity()).inflate(R.layout.custom_alertdialog_dog_nor, null);
        final android.app.AlertDialog eixtdialog = DialogTool.createDogDialog(getActivity(), viewlayout,
                "你确定取消对" + data.getNickName() + "的关注？", R.string.seex_cancle, R.string.seex_sure);
        viewlayout.findViewWithTag(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eixtdialog.dismiss();
                delFollow(data);
            }
        });
    }

    private void delFollow(final FriendBean bean) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        showProgress(getActivity(), R.string.seex_progress_text);
        Log.i("aa", "======delFollow=============");
        String head = jsonUtil.httpHeadToJson(getActivity());
        Map map = new HashMap();
        map.put("head", head);
        map.put("followId", bean.getFollowId());
        map.put("userId", userId);
        LogTool.setLog("profile_info----->", map);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().delFollow, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dissMissProgress();
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("delFollow", jsonObject);
                dissMissProgress();
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }
                try {
                    if (jsonObject.getInt("resultCode") == 1) {
                        mAdapter.getList().remove(bean);
                        if(mAdapter.getList().size()==0){
                            mAdapter.setCanLoadMore(false);
                            mAdapter.setdefFootView(false);
                        }
                        if(mAdapter.getList().size()<page_size){
                            mAdapter.setCanLoadMore(false);
                            mAdapter.setdefFootView(true);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    List<FriendBean> infos = new ArrayList<>();
    SeexFollowAdapter mAdapter;

    private void setListeners() {
        btn_add.setOnClickListener(this);
        close_tip.setOnClickListener(this);
        but_calllog.setOnClickListener(this);
        recyclerView.addOnScrollListener(recyclerOnScrollListener);
    }

    /**
     * 当把好友拉黑后，刷新数据
     *
     * @param userId
     */
    public void updateAfterBlackList(final int userId) {
        Iterator<FriendBean> iterator = infos.iterator();
        while (iterator.hasNext()) {
            FriendBean bean = iterator.next();
            int targetId = (int) bean.getTargetId();
            String id = (String) bean.getId();
            LogTool.setLog("tagetId--->id---->", targetId + "----" + id);
            LogTool.setLog("userId---->", userId);
            if (bean.getTargetId() == userId) {
                iterator.remove();
            }
        }
        showNoData();
        updateFriendList();
    }

    /**
     * 当收到ProfileInfo页面的通知，刷新列表数据
     *
     * @param position
     * @param itemNickName
     * @param itemPhoto
     */
    public void updateItemAfterProfile(int position, String itemNickName, String itemPhoto) {
        FriendBean item = infos.get(position);
        if (item != null) {
            item.setNickName(itemNickName);
            item.setPhoto(itemPhoto);

            showNoData();
            updateFriendList();
        }
    }

    /**
     * 好友请求列表—同意之后，更新通讯录列表,并保存数据到数据库
     */
    public void addFriendAfterAgree(final FriendBean item) {
        if (item != null) {
            ThreadTool.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    sessionDao.saveMail(item);
                }
            });
            infos.add(item);
            showNoData();
            updateFriendList();
        }
    }

    /**
     * 当请求被对方同意后，添加好友
     *
     * @param item
     */
    public void addFriendAfterOtherAgree(FriendBean item) {
        boolean haveSameData = false;
        if (item != null) {
            for (int i = 0; i < infos.size(); i++) {
                FriendBean beanItem = infos.get(i);
                if (beanItem.getTargetId() == item.getTargetId()) {
                    beanItem.setNickName(item.getNickName());
                    beanItem.setPhoto(item.getPhoto());
                    haveSameData = true;
                }
            }
            if (!haveSameData) {
                infos.add(item);
            }
            showNoData();
            updateFriendList();
        }
    }

    private static byte[] lock = new byte[0];


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.close_tip:
                SharedPreferencesUtils.put(getActivity(), Constants.SHOWFRIENDTIP, 0);
                break;
            case R.id.btn_add:
                intent = new Intent(getActivity(), FindActivity.class);
                startActivity(intent);
                MobclickAgent.onEvent(getActivity(), "addressBook_addFriendClick_240");
                break;
            case R.id.but_calllog:
                intent = new Intent(getActivity(), CallLogActivity.class);
                startActivity(intent);
                break;
            case R.id.small_secretary:
                String usertype = get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
                intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(ChatActivity.CHAT_YXID, Constants.sys_buyer);
                intent.putExtra(ChatActivity.CHAT_ID, Constants.sys_buyer_id);
                intent.putExtra(ChatActivity.CHAT_NAME, "西可客服");
                intent.putExtra(ChatActivity.CHAT_ICON, "");
                startActivity(intent);
                break;
        }
    }

    /**
     * 获取好友列表
     * *
     */
    private static final int page_size = 20;

    private void getDatas(int page, final boolean isLoad) {
        if (netWork == null || !netWork.isNetworkConnected()) {
            no_data.setVisibility(View.VISIBLE);
            return;
        }
        Log.i("aa", "=======getDatas=======");
        showProgress(getActivity(), R.string.seex_progress_text);
        Map map = new HashMap();
        String head = new JsonUtil(getContext()).httpHeadToJson(getActivity());
        map.put("head", head);
        map.put("page_no", page);
        map.put("page_size", page_size);
        map.put("userId", userId);
        LogTool.setLog("FriendFragment--friends-map", map);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().getFollowlist, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dissMissProgress();
                ToastUtil.showShortMessage(getActivity(), getResources().getString(R.string.seex_getData_fail));
                ptrClassicFrameLayout.finishRefresh();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                ptrClassicFrameLayout.finishLoadmore();
                dissMissProgress();
                Log.i("aa", "getDatas------>>" + jsonObject);
                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
                    final FriendResule resule = fromJson(jsonObject + "", FriendResule.class);
                    mTotalPage = Integer.valueOf(resule.getTotalPage());
                    if (mPage == 1) {
                        infos.clear();
                        infos.addAll(resule.getDataCollection());
                    } else {
                        infos.addAll(resule.getDataCollection());
                    }
                }
                if (!isLoad) {
                    ptrClassicFrameLayout.finishRefresh();
                }
                if (infos == null || infos.size() == 0) {
                    mAdapter.setCanLoadMore(false);
                    mAdapter.setdefFootView(false);
                    no_data.setVisibility(View.VISIBLE);
                } else {
                    no_data.setVisibility(View.GONE);
                }
                if (mPage == mTotalPage) {
                    mAdapter.setCanLoadMore(false);
                    mAdapter.setdefFootView(true);
                }
                if (infos.size() < page_size) {
                    mAdapter.setCanLoadMore(false);
                    mAdapter.setdefFootView(true);
                } else {
                    mAdapter.setCanLoadMore(true);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    private void showNoData() {
        if (infos == null || infos.size() == 0) {
            no_data.setVisibility(View.VISIBLE);
        } else {
            no_data.setVisibility(View.GONE);
        }
    }

    private void updateFriendList() {

    }

    /**
     * 编辑好友昵称
     *
     * @param position 好有所在列表位置
     * @param nickName 昵称
     */
    private void displayEditPop(final int position, String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            return;
        }
        if (null == mInflater) {
            mInflater = LayoutInflater.from(getActivity());
        }
        View view = mInflater.inflate(R.layout.pop_friend_rename, null);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        final ClearableEditText clearet_name = (ClearableEditText) view.findViewById(R.id.clearet_name);
        TextView btn_sure = (TextView) view.findViewById(R.id.btn_sure);
        TextView btn_cancel = (TextView) view.findViewById(R.id.btn_cancel);

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        if (editPop == null) {
            editPop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, view.getMeasuredHeight(), true); //设置根据pop_class_group的宽高显示大小，nLayoutParams.Wrap_Conten
            //            editPop.setFocusable(true);
            editPop.setOutsideTouchable(true);
            editPop.setBackgroundDrawable(new BitmapDrawable());
            editPop.setAnimationStyle(R.anim.push_bottom_in);
            editPop.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            editPop.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        } else {
            editPop.setContentView(view);
        }

        InputMethodUtils.showInputMethod(getActivity());

        if (editPop != null && !editPop.isShowing()) {
            editPop.showAtLocation(recyclerView, Gravity.TOP, 0, 0);
            BackgroundAlphaUtils.setAlpha(getActivity(), 0.5f);
        }
        editPop.update();
        editPop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                BackgroundAlphaUtils.setAlpha(getActivity(), 1f);
            }
        });
        String renameTips = getActivity().getResources().getString(R.string.seex_rename_setting, nickName);
        if (!TextUtils.isEmpty(nickName)) {
            SpannableString spanStr = new SpannableString(renameTips);
            ForegroundColorSpan span = new ForegroundColorSpan(ContextCompat.getColor(getActivity(), R.color.ranme_text));
            spanStr.setSpan(span, 3, nickName.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_name.setText(spanStr);
        } else {
            tv_name.setText(renameTips);
        }
        clearet_name.setText(nickName);
        clearet_name.setSelection(nickName.length());

        btn_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String renameStr = String.valueOf(clearet_name.getEditableText()).trim();
                if (!StringUtils.isEmpty(renameStr)) {
                    if (renameStr.length() > 12) {
                        ToastUtil.showShortMessage(getActivity(), "最多输入12个字符！");
                    } else {
//                        updateName(position, renameStr);
                        editPop.dismiss();
                    }
                } else {
                    ToastUtil.showShortMessage(getActivity(), "备注名不能为空");
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPop.dismiss();
            }
        });
    }

    /**
     * 修改备注名
     *
     * @param name     修改的备注名
     */
    private void updateName(final FriendBean mBean, final String name) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getContext(), R.string.seex_no_network).show();
            return;
        }
        showProgress(getActivity(), R.string.seex_progress_text);
        Log.i("aa", "=======updateName=======");

        Map map = new HashMap();
        String head = new JsonUtil(getContext()).httpHeadToJson(getActivity());
        map.put("head", head);
        map.put("remarkId", mBean.getFollowId());
        map.put("remark", name);
//        String str = "" + userId + friendId + name + "friend489remark3dlistr";
//        String key = Tools.md5(str);
//        map.put("key", key);
        LogTool.setLog("FriendFragment---UpdateName--map->>", map);
        MyOkHttpClient.getInstance().asyncPost(head, new Constants().addRemark, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dissMissProgress();
                ToastUtils.makeTextAnim(getActivity(), R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dissMissProgress();
                LogTool.setLog("FriendFragment---UpdateName--->>", jsonObject);
                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
//                    infos.get(position).setNickName(name);
                    mBean.setNickName(name);
                    updateFriendList();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleDatas(infos);
    }

    private FriendBean checkBean;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                switch (resultCode){
                    case Activity.RESULT_OK:
                        String remark =data.hasExtra(Constants.IntentKey)?data.getStringExtra(Constants.IntentKey):"";
                        updateName(checkBean,remark);
                        break;
                }
                break;
        }
    }




}
