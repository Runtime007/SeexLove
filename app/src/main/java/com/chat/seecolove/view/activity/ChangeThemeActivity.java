/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chat.seecolove.view.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.umeng.analytics.MobclickAgent;

import com.chat.seecolove.R;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.Theme;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.ThemeAdapter;
import com.chat.seecolove.widget.ToastUtils;
import com.chat.seecolove.widget.SpacesItemDecoration;

public class ChangeThemeActivity extends BaseAppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    private ImageView btn_sure;
    private int[] themes = new int[]{R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher, R.mipmap.ic_launcher};

    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_change_theme;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();

    }

    private void initViews() {
        title.setText(R.string.seex_change_theme);
        btn_sure = (ImageView) findViewById(R.id.btn_sure);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        GridLayoutManager mgr = new GridLayoutManager(this, 2) {
//            @Override
//            public boolean canScrollVertically() {
//                return false;
//            }
//        };
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2) );
        recyclerView.addItemDecoration(new SpacesItemDecoration(Tools.dip2px(15)));
    }

    private void setListeners() {
        btn_sure.setOnClickListener(this);
    }

    private ImageView icon_choice;
    private int currPos;

    private void initData() {

        int index = (int) SharedPreferencesUtils.get(MyApplication.getContext(), Constants.THEME_INDEX,0);
        final ThemeAdapter themeAdapter = new ThemeAdapter(this, themes,index);
        recyclerView.setAdapter(themeAdapter);
        themeAdapter.setOnItemClickListener(new ThemeAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

//                ImageView imageView = (ImageView) view.findViewById(R.id.icon_choice);
//                if (icon_choice == null) {
////                    if(position!=0){
////                        ImageView tv = (ImageView) recyclerView.getChildAt(0).findViewById(R.id.icon_choice);
////                        tv.setVisibility(View.GONE);
////                    }
//                    icon_choice = imageView;
//                    icon_choice.setVisibility(View.VISIBLE);
//                    currPos = position;
//                } else {
//                    if (currPos == position) {
//                        icon_choice.setVisibility(View.GONE);
//                        currPos = -1;
//                        icon_choice = null;
//                    } else {
//                        icon_choice.setVisibility(View.GONE);
//                        icon_choice = imageView;
//                        currPos = position;
//                        icon_choice.setVisibility(View.VISIBLE);
//
//                    }
//                }

                String eventName = "theme_" + position + "_clicked";
                MobclickAgent.onEvent(ChangeThemeActivity.this, eventName);
                currPos = position;
                themeAdapter.updateList(position);


            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                Theme.setCurrentTheme(currPos);
                ToastUtils.makeTextAnim(ChangeThemeActivity.this, "主题设置成功！").show();
                MobclickAgent.onEvent(ChangeThemeActivity.this, "theme_sure_clicked");
                finish();
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                MobclickAgent.onEvent(this, "theme_back_clicked");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
