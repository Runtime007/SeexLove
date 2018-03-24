package com.chat.seecolove.view.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.view.activity.RoomActivity;


public class BeautyBeautyFrament extends BaseFragment implements View.OnClickListener {
    private View view;
    private RecyclerView recyclerView;
    private int position;
    private static final String POSITION = "position";
    private DiscreteSeekBar seeker_smooth,seeker_light;

    public static BeautyBeautyFrament newInstance(int position) {

        final BeautyBeautyFrament f = new BeautyBeautyFrament();
        final Bundle args = new Bundle();
        args.putInt(POSITION, position);
        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_beauty_beauty, null);
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
        seeker_smooth = (DiscreteSeekBar)view.findViewById(R.id.seeker_smooth);
        seeker_light = (DiscreteSeekBar)view.findViewById(R.id.seeker_light);
    }

    private void setListeners() {
        seeker_smooth.setOnProgressChangeListener(onProgressChangeListenerSmooth);
        seeker_light.setOnProgressChangeListener(onProgressChangeListenerLight);
    }

    public void initData() {
        float level_smooth =(float) SharedPreferencesUtils.get(getActivity(), Constants.LEVEL_SMOOTH,0.0f);
        float level_light = (float)SharedPreferencesUtils.get(getActivity(), Constants.LEVEL_LIGHT,1.0f);
        seeker_smooth.setMax(10);
        seeker_light.setMax(10);
        seeker_smooth.setProgress((int)(level_smooth*10));
        seeker_light.setProgress((int)(level_light*10));
    }

    DiscreteSeekBar.OnProgressChangeListener onProgressChangeListenerSmooth = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {

            LogTool.setLog("onProgressChanged value:",value);
            RoomActivity roomActivity = (RoomActivity ) getActivity();
//            roomActivity.setSmoothLevel(value/10f);

        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

        }
    };

    DiscreteSeekBar.OnProgressChangeListener onProgressChangeListenerLight = new DiscreteSeekBar.OnProgressChangeListener() {
        @Override
        public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
            RoomActivity roomActivity = (RoomActivity ) getActivity();
//            roomActivity.setLightLevel(value/10f);
        }

        @Override
        public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

        }
    };


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
