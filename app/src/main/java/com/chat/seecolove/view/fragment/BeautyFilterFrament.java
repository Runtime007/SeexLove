package com.chat.seecolove.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chat.seecolove.R;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.BeautyFilterAdapter;
import com.chat.seecolove.view.recycler.OnRecyclerItemClickListener;
import com.chat.seecolove.widget.SpacesItemDecoration;


public class BeautyFilterFrament extends BaseFragment implements View.OnClickListener {
    private View view;
    private RecyclerView recyclerView;
    private int position;
    private static final String POSITION = "position";

    public static BeautyFilterFrament newInstance(int position) {

        final BeautyFilterFrament f = new BeautyFilterFrament();
        final Bundle args = new Bundle();
        args.putInt(POSITION, position);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_beauty_filter, null);
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
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(3)));
        recyclerView.setAdapter(new BeautyFilterAdapter(getActivity(),null));
    }

    private void setListeners() {
        recyclerView.addOnItemTouchListener(new OnRecyclerItemClickListener(recyclerView) {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh) {
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh) {

            }
        });
    }

    public void initData() {


    }






    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        recycleDatas(balances);
    }
}
