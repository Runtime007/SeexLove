package com.chat.seecolove.view.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ViewCache;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.network.NetWork;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SeexAgeActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String EXTRA_MIN_VALUE = "min_value";
    public static final String EXTRA_MAX_VALUE = "max_value";
    public static final String EXTRA_VALUE_UNIT = "value_unit";
    public static final String EXTRA_IS_STAR = "is_star"; // 是否为星座选择

    private String mSelectValue;
    private int minValue;
    private int maxValue;
    private String valueUnit;
    private boolean isStar;
    private MyApplication app;
    private NetWork netWork;
    private ListView recyclerView;
    private TextView ageText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = this.getWindowManager().getDefaultDisplay();
        int width = display.getWidth();
        int height = display.getHeight();
        LayoutInflater inflater = LayoutInflater.from(this);
        View viewDialog = inflater.inflate(R.layout.seex_age_dialog_ui, null);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(width, height);
        setContentView(viewDialog, layoutParams);

        getIntentExtra();

        app = MyApplication.getContext();
        app.allActivity.add(this);
        netWork = new NetWork(this);
        findView();
    }

    private void getIntentExtra() {
        Intent intent = getIntent();
        if (intent != null) {
            mSelectValue = intent.getStringExtra(Constants.IntentKey);
            minValue = intent.getIntExtra(EXTRA_MIN_VALUE, 1);
            maxValue = intent.getIntExtra(EXTRA_MAX_VALUE, 100);
            valueUnit = intent.getStringExtra(EXTRA_VALUE_UNIT);
            isStar = intent.getBooleanExtra(EXTRA_IS_STAR, false);
        }
    }

    void findView() {
        findViewById(R.id.ok).setOnClickListener(this);
        findViewById(R.id.cancle).setOnClickListener(this);

        recyclerView = (ListView) findViewById(R.id.recyclerView);
        List<String> valueList = new ArrayList<>();

        if (isStar) {
            valueList = Arrays.asList(getResources().getStringArray(R.array.start_list));
            minValue = 0;
            maxValue = valueList.size() - 1;
        } else {
            for (int i = minValue; i <= maxValue; i++) {
                valueList.add(i + (valueUnit != null ? valueUnit : ""));
            }
        }

        int selection = 0;
        if (valueList != null && mSelectValue != null) {
            for (String value : valueList) {
                if (mSelectValue.equals(value))
                    break;

                selection++;
            }
        }

        AgeAdapter ageadpter = new AgeAdapter(this, valueList);
        recyclerView.setAdapter(ageadpter);
        recyclerView.setSelection(selection);

        final String[] valueArray = new String[valueList.size()];
        valueList.toArray(valueArray);

        NumberPicker np1 = (NumberPicker) findViewById(R.id.picker);
        setNumberPickerDividerColor(np1);
        np1.setMinValue(minValue);
        np1.setMaxValue(maxValue);
        np1.setDisplayedValues(valueArray);
        np1.setValue(minValue + selection);
        np1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                Log.i("aa", newVal + "==========");
                mSelectValue = valueArray[newVal - minValue];
//                if (isStar) {
//
//                } else {
//                    mSelectValue = String.valueOf(newVal);
//                }
//                showSelectedPrice();
            }
        });

    }


    private void setNumberPickerDividerColor(NumberPicker numberPicker) {
        NumberPicker picker = numberPicker;
        Field[] pickerFields = NumberPicker.class.getDeclaredFields();
        for (Field pf : pickerFields) {
            if (pf.getName().equals("mSelectionDivider")) {
                pf.setAccessible(true);
                try {
                    //设置分割线的颜色值 透明
                    pf.set(picker, new ColorDrawable(ContextCompat.getColor(this, android.R.color.transparent)));
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }


    class AgeAdapter extends BaseAdapter {

        private List<String> blackModels = null;
        private Context context = null;

        public AgeAdapter(Context context, List<String> blackModels) {
            super();
            this.context = context;
            this.blackModels = blackModels;
        }

        @Override
        public int getCount() {
            return blackModels.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ViewCache viewcache;

        @Override
        public View getView(int position, View contentView, ViewGroup parent) {
            if (contentView == null) {
                contentView = LayoutInflater.from(SeexAgeActivity.this).inflate(R.layout.seex_age_item, null);
                viewcache = new ViewCache();
                viewcache.tView1 = (TextView) contentView.findViewById(R.id.ID);
                contentView.setTag(viewcache);
            } else {
                viewcache = (ViewCache) contentView.getTag();
            }

            final String text = blackModels.get(position);

            viewcache.tView1.setText(text + (valueUnit != null ? valueUnit : ""));

            if (text.equals(mSelectValue)) {
                viewcache.tView1.setTextColor(ContextCompat.getColor(SeexAgeActivity.this, R.color.black));
            } else {
                viewcache.tView1.setTextColor(ContextCompat.getColor(SeexAgeActivity.this, R.color.gray_id));
            }

            viewcache.tView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mSelectValue = text;
                    notifyDataSetChanged();
                }
            });

            return contentView;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ok:
                Intent intent = new Intent();
                intent.putExtra(Constants.IntentKey, mSelectValue);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.cancle:
                this.finish();
                break;
        }
    }


}
