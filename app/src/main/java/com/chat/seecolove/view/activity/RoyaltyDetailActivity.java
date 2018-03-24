package com.chat.seecolove.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.umeng.analytics.MobclickAgent;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import com.chat.seecolove.R;
import com.chat.seecolove.bean.Balance;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;

public class RoyaltyDetailActivity extends BaseAppCompatActivity implements View.OnClickListener {


    private int type,  isIncome;
    private Balance balance;
    private SimpleDraweeView user_icon;
    private TextView order_ID,user_name,user_id,tx_money,tx_info,tx_type,tx_status,create_time,tx_feed;
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.activity_royalty_detail;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
        setListeners();
        initData();
    }

    private void initViews() {
        title.setText(R.string.seex_royalty_detail);
        user_icon = (SimpleDraweeView)findViewById(R.id.user_icon);
        order_ID = (TextView) findViewById(R.id.order_ID);
        tx_money = (TextView) findViewById(R.id.tx_money);
        create_time = (TextView) findViewById(R.id.create_time);
        tx_status = (TextView) findViewById(R.id.tx_status);
        user_name = (TextView) findViewById(R.id.user_name);
        user_id = (TextView) findViewById(R.id.user_id);
        tx_info = (TextView) findViewById(R.id.tx_info);
        tx_type = (TextView) findViewById(R.id.tx_type);
        tx_feed = (TextView) findViewById(R.id.tx_feed);
    }

    private void setListeners() {
        tx_feed.setOnClickListener(this);
    }

    private void initData() {
        balance = (Balance)getIntent().getSerializableExtra("Balance");
         isIncome = getIntent().getIntExtra("isIncome",0);
        type = balance.getType();
        tx_info.setVisibility(View.VISIBLE);
        if(type==2||type==3){
            if(balance.getStatus_code()==1){
                tx_feed.setVisibility(View.VISIBLE);
            }
            long mss = balance.getBusiness_times();
            String DateTimes = null;
            long hours = (mss % ( 60 * 60 * 24)) / (60 * 60);
            long minutes = (mss % ( 60 * 60)) /60;
            long seconds = mss % 60;
           if(hours>0){
                DateTimes=hours + "小时" + minutes + "分"
                        + seconds + "秒";
            }else if(minutes>0){
                DateTimes=minutes + "分"
                        + seconds + "秒";
            }else{
                DateTimes=seconds + "秒";
            }


            tx_info.setText("通话时长："+DateTimes);
        }else if(type==10||type==11){
            tx_info.setText("消息数量："+balance.getIm_number()+"条");
        }else {
            tx_info.setVisibility(View.GONE);
        }

        if(type==2||type==3||type==8||type==9){
            user_name.setText(balance.getNick_name());
            user_id.setText("ID："+balance.getShow_id());
        }else{
            user_name.setText("");
            user_id.setText("");
        }

        if(isIncome==0){
            tx_money.setText("+"+balance.getMoney()/100);
        }else{
            tx_money.setText("-"+balance.getMoney()/100);
        }

        tx_type.setText("交易类型："+balance.getTypeName());
        tx_status.setText("交易状态："+balance.getStatus());

        if(type==10||type==11||type==16||type==17) {
            order_ID.setVisibility(View.GONE);
        }else{
            order_ID.setVisibility(View.VISIBLE);
            order_ID.setText("订单号：" + balance.getBusiness_code());
        }

        SimpleDateFormat format =  new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Long time=new Long(balance.getCreate_time());
        String d = format.format(time);
        create_time.setText("创建时间："+d);


        Uri uri = null;
        int res=R.color.white;
        switch (type){

        }

        if(uri!=null){
            user_icon.setImageURI(uri);
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tx_feed:
//                Intent intent = new Intent(RoyaltyDetailActivity.this,OptionActivity.class);
//                intent.putExtra("balance",balance);
//                startActivityForResult(intent, 0);

                String usertype = SharedPreferencesUtils.get(MyApplication.getContext(), Constants.USERTYPE, 0) + "";
                Intent intent = new Intent(RoyaltyDetailActivity.this, ChatActivity.class);
                if(usertype.equals(1)){
                    intent.putExtra(ChatActivity.CHAT_YXID, Constants.sys_buyer);
                    intent.putExtra(ChatActivity.CHAT_ID, Constants.sys_buyer);
                }else{
                    intent.putExtra(ChatActivity.CHAT_YXID, Constants.sys_buyer);
                    intent.putExtra(ChatActivity.CHAT_ID, Constants.sys_buyer);
                }
                intent.putExtra(ChatActivity.CHAT_NAME, "西可客服");
                intent.putExtra(ChatActivity.CHAT_ICON, "");
                startActivity(intent);
                Map<String,String> map = new HashMap<String, String>();
                map.put("from","complaint");
                MobclickAgent.onEvent(RoyaltyDetailActivity.this,"liaobo_small_Secretary_clicked",map);
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            LogTool.setLog("requestCode:", requestCode);
            switch (requestCode) {
                case 0:
                    if(data!=null){
                        if(data.getBooleanExtra("status_code_change",false)){
                            tx_feed.setVisibility(View.GONE);
                            Intent mIntent = new Intent(Constants.ACTION_ROYALTY_CHANGE);
                            mIntent.putExtra("isIncome",isIncome);
                            sendBroadcast(mIntent);

                        }

                    }
                    break;

            }
        }
    }

}
