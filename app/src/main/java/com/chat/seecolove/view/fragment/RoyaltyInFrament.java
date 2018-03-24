package com.chat.seecolove.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.Balance;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.activity.RoyaltyDetailActivity;
import com.chat.seecolove.view.adaper.RoyaltyListNewAdapter;
import com.chat.seecolove.widget.ToastUtils;
import okhttp3.Request;

/**
 */
public class RoyaltyInFrament extends BaseFragment implements View.OnClickListener {
    private View view;
    private RecyclerView recyclerView;
    private List<Balance> balances;
    private RoyaltyListNewAdapter royaltyListNewAdapter;
    private ProgressBar progressBar;
    private TextView no_data;
    private FrameLayout footerView;
    private int totalPage;
    private int page = 1;//RecyclerView数据页数
    private int position;
    private static final String POSITION = "position";

    public static RoyaltyInFrament newInstance(int position) {

        Log.e("cccccccccccc", "newInstance");
        final RoyaltyInFrament f = new RoyaltyInFrament();
        final Bundle args = new Bundle();
        args.putInt(POSITION, position);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_royalty_content, null);
        initViews();
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments() != null ? getArguments().getInt(POSITION)
                : 0;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListeners();
        initData();
    }

    private void initViews() {
        no_data = (TextView) view.findViewById(R.id.no_data);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        // 创建一个线性布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        // 设置布局管理器
        recyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .color(getResources().getColor(R.color.line))
                .margin(Tools.dip2px(81), 0)
                .sizeResId(R.dimen.divider)
                .build());

        footerView = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.recy_footer, recyclerView, false);

    }

    private void setListeners() {
        recyclerView.addOnScrollListener(scrollListener);
    }

    public void initData() {
        getBillHistory();

    }


    private void getBillHistory() {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(getActivity(), R.string.seex_no_network).show();
            return;
        }
        final int isIncome = 0;
        String head = jsonUtil.httpHeadToJson(getActivity());
        int userID = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERID, -1);
        int userType = (int) SharedPreferencesUtils.get(getActivity(), Constants.USERTYPE, 0);
        Map map = new HashMap();
        map.put("head", head);
        map.put("pageNo", page);
        map.put("pageSize", 20);
        map.put("userID", userID);
        map.put("userType", userType);
        map.put("isIncome", isIncome);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().getBillHistory, map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                LogTool.setLog("getBillHistory onError :", e.getMessage());
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("getBillHistory:", jsonObject);
                if (Tools.jsonResult(getActivity(), jsonObject, null)) {
                    return;
                }

                try {
                    totalPage = jsonObject.getInt("totalPage");
                    String dataCollection = jsonObject.getString("dataCollection");
                    if (page > 1) {
                        if (balances != null) {
                            List<Balance> temps = jsonUtil.jsonToBalances(dataCollection);
                            balances.addAll(temps);
                            royaltyListNewAdapter.updateList(balances);
                        }
                        footerView.setVisibility(View.GONE);
                    } else {
                        balances = jsonUtil.jsonToBalances(dataCollection);
                        if (balances == null || balances.size() == 0) {
                            no_data.setVisibility(View.VISIBLE);
                            return;
                        }
                        recyclerView.getRecycledViewPool().clear();
                        if (royaltyListNewAdapter == null) {
                            royaltyListNewAdapter = new RoyaltyListNewAdapter(getActivity(), balances, isIncome);
                            recyclerView.setAdapter(royaltyListNewAdapter);
                            royaltyListNewAdapter.setOnItemClickListener(new RoyaltyListNewAdapter.OnRecyclerViewItemClickListener() {
                                @Override
                                public void onItemClick(View view, Balance data) {
                                    Intent intent = new Intent(getActivity(), RoyaltyDetailActivity.class);
                                    intent.putExtra("Balance", data);
                                    intent.putExtra("isIncome", isIncome);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            royaltyListNewAdapter.updateList(balances);
                        }
                        footerView.setVisibility(View.GONE);
                        royaltyListNewAdapter.setFooterView(footerView);


                    }


                } catch (JSONException e) {

                }


            }
        });
    }


    RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);


            // 滑到底部
            if (!recyclerView.canScrollVertically(1)) {
                if (balances == null || balances.size() == 0) {
                    return;
                }
                if (footerView.getVisibility() == View.GONE) {
                    page++;
                    if (page > totalPage) {
                        footerView.setVisibility(View.GONE);
                        return;
                    }
                    footerView.setVisibility(View.VISIBLE);
                    LogTool.setLog("滑到底部:", page);
                    getBillHistory();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            //dx表示横向

        }
    };


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.floatingActionButton:
//                getHotInfo();
//                break;

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleDatas(balances);
    }
}
