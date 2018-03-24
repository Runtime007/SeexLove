package com.chat.seecolove.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.bean.NewFriendReqResult;
import com.chat.seecolove.constants.ConfigConstants;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.network.NetWork;
import com.chat.seecolove.service.SocketService;
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.FriendsRequestAdapter;
import com.chat.seecolove.widget.DeletePopWindow;
import com.chat.seecolove.widget.recycleview.EmptyRecyclerView;
import com.chat.seecolove.widget.recycleview.OnRecyclerItemClickListener;
import com.chat.seecolove.widget.recycleview.RecyclerOnScrollListener;
import io.socket.client.Ack;
import okhttp3.Request;



public class FriendRequestFragment extends BaseFragment {

    private FriendsRequestAdapter adapter;
    private Context mContext;
    private ArrayList<FriendBean> list = new ArrayList<>();
    private EmptyRecyclerView emptyRecyclerView;

    private int userId;
    private int currentPage = 1;

    private SocketService socketService;
    private TextView no_data;

    public static FriendRequestFragment newInstance() {
        Bundle args = new Bundle();
        FriendRequestFragment friendReqFragment = new FriendRequestFragment();
        friendReqFragment.setArguments(args);
        return friendReqFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bd = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_request, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initListener();
        initData();
    }

    private void initView(View view) {
        emptyRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.empty_rclv);
        no_data = (TextView) view.findViewById(R.id.no_data);

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        emptyRecyclerView.setLayoutManager(manager);

        adapter = new FriendsRequestAdapter(getActivity());
        emptyRecyclerView.setAdapter(adapter);
        adapter.setList(list);
        emptyRecyclerView.setEmptyView(no_data);
        adapter.setCanLoadMore(true);
        emptyRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(mContext)
                .color(ContextCompat.getColor(mContext, R.color.line))
                .sizeResId(R.dimen.divider)
                .build());

        userId = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
    }

    private void initListener() {
        emptyRecyclerView.addOnScrollListener(new RecyclerOnScrollListener() {
            @Override
            public void onLoadMore() {
                if (adapter.isCanLoadMore()) {
                    initData();
                }
            }
        });
        emptyRecyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(emptyRecyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {

            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {
                View contView = vh.itemView.findViewById(R.id.content);
                final int position = vh.getLayoutPosition();
                if (position == list.size()) {
                    return;
                }

                DeletePopWindow deletePop = new DeletePopWindow(mContext);
                deletePop.displayDeletePop(contView);
                deletePop.setPopDeleteListener(new DeletePopWindow.IPopDeleteListener() {
                    @Override
                    public void delete() {
                        deleteFriendRequest(position);
                        MobclickAgent.onEvent(getActivity(),"newFriend_request_delete_240");
                    }
                });
            }
        });

        adapter.setAgreenListener(new FriendsRequestAdapter.AgreenListener() {
            @Override
            public void agreen(int position) {
                if (list != null && list.size() > 0) {
                    agreenReq(list.get(position).getId(), position);
                    MobclickAgent.onEvent(getActivity(),"newFriend_requestAgreed_Click_240");
                }
            }
        });
    }

    private void initData() {
        if (netWork == null || !netWork.isNetworkConnected()) {
            ToastUtil.showShortMessage(mContext, getString(R.string.seex_no_network));
            return;
        }
        showProgress(getActivity(), R.string.seex_progress_text);
        Map map = new HashMap();
        String head = new JsonUtil(mContext).httpHeadToJson(getActivity());
        map.put("head", head);
        map.put(ConfigConstants.PAGE_NO, currentPage);
        map.put(ConfigConstants.PAGE_SIZE, ConfigConstants.AddRequest.REQUEST_PAGE_SIZE);
        map.put(ConfigConstants.USER_ID, userId);
        map.put(ConfigConstants.AddRequest.TYPE, ConfigConstants.AddRequest.TYPE_FRIEND_REQUEST);

        String str = userId + "" + ConfigConstants.AddRequest.TYPE_FRIEND_REQUEST + "apply4893dlistr";
        String key = Tools.md5(str);
        map.put(ConfigConstants.KEY, key);
        LogTool.setLog("FriendRequestFragment---fiend-map", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().friendRequest, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                ToastUtil.showShortMessage(mContext, getString(R.string.seex_getData_fail));
                dissMissProgress();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dissMissProgress();
                LogTool.setLog("FriendRequestFragment------>>", jsonObject);
                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
                    NewFriendReqResult resule = GsonUtil.fromJson(jsonObject + "", NewFriendReqResult.class);

                    if (resule.getTotalPage() > currentPage) {
                        currentPage++;
                    } else if (resule.getTotalPage() == currentPage) {
                        adapter.setCanLoadMore(false);
                    }

                    int oldListSize = list.size();
                    if (resule.getDataCollection() != null && resule.getDataCollection().size() > 0) {
                        list.addAll(resule.getDataCollection());
                        adapter.notifyItemInserted(oldListSize);
                    }
                }

            }
        });
    }

    private void deleteFriendRequest(final int position) {
        if (netWork == null || !netWork.isNetworkConnected()) {
            ToastUtil.showShortMessage(mContext, getString(R.string.seex_no_network));
            return;
        }
        showProgress(getActivity(), R.string.seex_progress_text);

        Map map = new HashMap();
        String head = new JsonUtil(mContext).httpHeadToJson(getActivity());
        map.put("head", head);
        map.put("id", list.get(position).getId());

        String str = list.get(position).getId() + "apply4893delur";
        String key = Tools.md5(str);
        map.put(ConfigConstants.KEY, key);
        LogTool.setLog("clearAll-my-map", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().deleteFriendReq, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dissMissProgress();
                ToastUtil.showShortMessage(mContext, getString(R.string.seex_getData_fail));
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dissMissProgress();
                if (!Tools.jsonResult(getActivity(), jsonObject, null)) {
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                }
            }
        });

    }

    /**
     * 清空数据
     */
    public void clearAllData() {
        list.clear();
        adapter.notifyDataSetChanged();
        if (list.size() == 0) {
            no_data.setVisibility(View.VISIBLE);
            emptyRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleDatas(list);
    }

    private void agreenReq(String reqId, final int position) {
        final FriendBean item = list.get(position);
        NetWork netWork = new NetWork(mContext);
        if (!netWork.isNetworkConnected()) {
            ToastUtil.showShortMessage(mContext, mContext.getResources().getString(R.string.seex_no_network));
            return;
        }
        showProgress(getActivity(), R.string.seex_progress_text);
        Map map = new HashMap();
        String head = new JsonUtil(mContext).httpHeadToJson(mContext);
        map.put("head", head);
        if (!TextUtils.isEmpty(reqId)) {
            map.put(ConfigConstants.AddRequest.REQUEST_FRIEND_ID, reqId);
            String str = reqId + "apply4893ur";
            String key = Tools.md5(str);
            map.put(ConfigConstants.KEY, key);
        }

        LogTool.setLog("FriendRequestFragment--agree", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().mailPassFriendReq, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dissMissProgress();
                ToastUtil.showShortMessage(mContext, mContext.getResources().getString(R.string.seex_getData_fail));
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dissMissProgress();
                LogTool.setLog("FriendRequestFragment---agree--->>", jsonObject);
                if (!Tools.jsonResult(mContext, jsonObject, null)) {
                    //                    final FriendResule resule = GsonUtil.fromJson(jsonObject + "", FriendResule.class);
                    list.get(position).setStatus(2);
                    adapter.notifyItemChanged(position);

                    String umid = SharedPreferencesUtils.get(mContext, Constants.UMID, "-1") + "";

                    if (socketService == null) {
                        socketService = SocketService.getInstance();
                    }
                    if (socketService != null) {
                        socketService.addFriends(item.getTargetId(), 0, umid, new Ack() {// type 1请求 0回复消息 2删除好友
                            @Override
                            public void call(Object... args) {
                                ToastUtil.showShortMessage(mContext, "你已关注对方！");
                            }
                        });
                    }

                    int receiverUpdateNum = (int) SharedPreferencesUtils.get(getActivity(), userId + Constants.MAIL_UPDATE_NUM, 0);
                    SharedPreferencesUtils.put(getActivity(), userId + Constants.MAIL_UPDATE_NUM, ++receiverUpdateNum);

                    LogTool.setLog("FriendsRequestAdapter--AgreeApply--->", item.getTargetId());

                    /**
                     *更改通讯里列表数据
                     */
                    Intent intent = new Intent();
                    intent.setAction(Constants.ACTION_MAIL_REQ_AGREE);
                    Bundle bd = new Bundle();
                    bd.putParcelable(ConfigConstants.AddRequest.FRIEND_REQ_ITEM, item);
                    intent.putExtras(bd);
                    mContext.sendBroadcast(intent);
                }
            }
        });
    }
}
