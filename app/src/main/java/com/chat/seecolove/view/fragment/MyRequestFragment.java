package com.chat.seecolove.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.chat.seecolove.tools.GsonUtil;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.MyRequestAdapter;
import com.chat.seecolove.widget.DeletePopWindow;
import com.chat.seecolove.widget.recycleview.EmptyRecyclerView;
import com.chat.seecolove.widget.recycleview.OnRecyclerItemClickListener;
import com.chat.seecolove.widget.recycleview.RecyclerOnScrollListener;
import okhttp3.Request;


public class MyRequestFragment extends BaseFragment {

    private Context mContext;
    private EmptyRecyclerView emptyRecyclerView;
    private MyRequestAdapter adapter;
    private ArrayList<FriendBean> list = new ArrayList();

    private int currentPage = 1;

    private int userId;
    private TextView no_data;

    public static MyRequestFragment newInstance() {
        Bundle args = new Bundle();
        MyRequestFragment myReqFragment = new MyRequestFragment();
        myReqFragment.setArguments(args);
        return myReqFragment;
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
        no_data.setText(getResources().getString(R.string.seex_no_my_req));

        LinearLayoutManager manager = new LinearLayoutManager(mContext);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        emptyRecyclerView.setLayoutManager(manager);

        adapter = new MyRequestAdapter(mContext);
        emptyRecyclerView.setAdapter(adapter);
        adapter.setList(list);

        emptyRecyclerView.setEmptyView(no_data);
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
                final int position = vh.getLayoutPosition();
                if (position == list.size()) {
                    return;
                }
                View contView = vh.itemView.findViewById(R.id.content);

                DeletePopWindow deletePop = new DeletePopWindow(mContext);
                deletePop.displayDeletePop(contView);
                deletePop.setPopDeleteListener(new DeletePopWindow.IPopDeleteListener() {
                    @Override
                    public void delete() {
                        deleteFriendRequest(position);
                        MobclickAgent.onEvent(getActivity(),"newFriend_record_deleteClick_240");
                    }
                });
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
        map.put(ConfigConstants.AddRequest.TYPE, ConfigConstants.AddRequest.TYPE_MY_REQUEST);

        String str = userId + "" + ConfigConstants.AddRequest.TYPE_MY_REQUEST + "apply4893dlistr";
        String key = Tools.md5(str);
        map.put(ConfigConstants.KEY, key);
        LogTool.setLog("FriendRequestFragment-my-map", map);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().friendRequest, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dissMissProgress();
                ToastUtil.showShortMessage(mContext, getString(R.string.seex_getData_fail));
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                dissMissProgress();
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
}
