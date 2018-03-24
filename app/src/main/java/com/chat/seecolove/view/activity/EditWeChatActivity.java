package com.chat.seecolove.view.activity;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chat.seecolove.R;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.constants.Constants;
import com.chat.seecolove.emoji.ViewPagerAdapter;
import com.chat.seecolove.http.MyOkHttpClient;
import com.chat.seecolove.jsonutil.JsonUtil;
import com.chat.seecolove.tools.InputMethodUtils;
import com.chat.seecolove.tools.LogTool;
import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.tools.ToastUtil;
import com.chat.seecolove.tools.Tools;
import com.chat.seecolove.view.adaper.GiftAdapter;
import com.chat.seecolove.widget.ToastUtils;
import com.githang.statusbar.StatusBarCompat;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.Request;

/**
 * Created by 24316 on 2018/1/23.
 */

public class EditWeChatActivity extends BaseAppCompatActivity implements View.OnClickListener {
    @Override
    protected int getContentViewLayoutId() {
        return R.layout.eidt_wechat;
    }
    int mode;
    String mValeu;
    Button checkGiftView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarCompat.setStatusBarColor(this, ContextCompat.getColor(this,R.color.bottom_bg), false);
//        mode=getIntent().hasExtra(ModeSingen)?getIntent().getIntExtra(ModeSingen,Nick_Mode):Nick_Mode;
        initViews();
        title.setText(R.string.edit_title);
        checkGiftView=(Button)findViewById(R.id.button_1);
        checkGiftView.setOnClickListener(this);
        findViewById(R.id.button_2).setOnClickListener(this);
         editView=(EditText)findViewById(R.id.editview);
    }
    EditText editView;
    private void initViews() {
        int userId = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        getGiftList(userId+"");
    }

    @Override
    public void onClick(View view) {
       switch (view.getId()){
           case R.id.button_1:
               InputMethodUtils.hideInputMethod(editView);
               giftPopw();
               break;
           case R.id.button_2:
               String wxnum=editView.getText().toString();
               if(TextUtils.isEmpty(wxnum)){
                   ToastUtils.makeTextAnim(EditWeChatActivity.this, "微信ID必须输入").show();
                   return;
               }
               if(TextUtils.isEmpty(gifid)){
                   ToastUtils.makeTextAnim(EditWeChatActivity.this, "需要绑定礼物ID").show();
                   return;
               }
               getputWx(wxnum,gifid);
               break;
       }
    }

    private String gifid;

    ViewPager gitf_contains;
    List<List<ChatEnjoy>> giftEnjoys = new ArrayList<>();
    //git view
    private void giftPopw() {
        View popView = LayoutInflater.from(this).inflate(
                R.layout.seex_gift_pop_ui, null);
        View rootView = findViewById(R.id.poptag);
        giftpopupWindow = new PopupWindow(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        giftpopupWindow.setContentView(popView);
        giftpopupWindow.setBackgroundDrawable(new BitmapDrawable());
        giftpopupWindow.setFocusable(true);
        giftpopupWindow.setOutsideTouchable(true);
        giftpopupWindow.showAtLocation(rootView, Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0,
                0);
        giftlayout_point = (LinearLayout) popView.findViewById(R.id.iv_image);
        gitf_contains = (ViewPager) popView.findViewById(R.id.vp_contains);
        popView.findViewById(R.id.disop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopWindow();
            }
        });
        mBalanceView = (TextView) popView.findViewById(R.id.blanceview);

         popView.findViewById(R.id.sendgif).setVisibility(View.GONE);
          popView.findViewById(R.id.chongzhi).setVisibility(View.GONE);
          mBalanceView.setVisibility(View.GONE);
        popView.findViewById(R.id.chongzhi).setOnClickListener(this);
        popView.findViewById(R.id.sendgif).setOnClickListener(this);
        initGiftData();
    }

    public PopupWindow giftpopupWindow;
    TextView mBalanceView;
    private void dismissPopWindow() {
        if (giftpopupWindow != null) {
            giftpopupWindow.dismiss();
        }
    }
    void initGiftData() {
        if (giftEnjoys == null) {
            return;
        }
        Init_viewPager(giftEnjoys);

        gitf_contains.setAdapter(new ViewPagerAdapter(giftpageViews));
        gitf_contains.setCurrentItem(1);

        gitf_contains.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {

                draw_giftPoint(arg0);
                if (arg0 == giftpointViews.size() - 1 || arg0 == 0) {
                    if (arg0 == 0) {
                        gitf_contains.setCurrentItem(arg0 + 1);
                        giftpointViews.get(1).setBackgroundResource(R.drawable.d2);
                    } else {
                        gitf_contains.setCurrentItem(arg0 - 1);
                        giftpointViews.get(arg0 - 1).setBackgroundResource(
                                R.drawable.d2);
                    }
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }
    public void draw_giftPoint(int index) {
        for (int i = 1; i < giftpointViews.size(); i++) {
            if (index == i) {
                giftpointViews.get(i).setBackgroundResource(R.drawable.d2);
            } else {
                giftpointViews.get(i).setBackgroundResource(R.drawable.d1);
            }
        }
    }
    private List<ImageView> giftpointViews;
    private ArrayList<View> giftpageViews;
    private LinearLayout giftlayout_point;
    List<GiftAdapter> giffaceAdapters = new ArrayList<GiftAdapter>();

    private void Init_viewPager(List<List<ChatEnjoy>> giftEnjoys) {
        giftpageViews = new ArrayList<View>();
        View nullView1 = new View(this);
        nullView1.setBackgroundColor(Color.TRANSPARENT);
        giftpageViews.add(nullView1);
        giffaceAdapters.clear();
        for (int i = 0; i < giftEnjoys.size(); i++) {
            GridView view = new GridView(this);
            final GiftAdapter adapter = new GiftAdapter(this, giftEnjoys.get(i));
            view.setAdapter(adapter);
            giffaceAdapters.add(adapter);
            adapter.setOnItemClickListener(new GiftAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, ChatEnjoy data, int pos, GiftAdapter giftAdapter) {
                    gifid=data.getId();
                    dismissPopWindow();
                    checkGiftView.setBackgroundResource(R.drawable.seex_gift_green_box);
                    checkGiftView.setText(getString(R.string.seex_gift_check,data.getPicName()));
                }
            });
            view.setNumColumns(4);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setHorizontalSpacing(1);
            view.setVerticalSpacing(1);
            view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
            view.setCacheColorHint(0);
            view.setPadding(5, 0, 5, 0);
            view.setSelector(new ColorDrawable(Color.TRANSPARENT));
            view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            view.setGravity(Gravity.CENTER);
            giftpageViews.add(view);
        }
        View nullView2 = new View(this);
        nullView2.setBackgroundColor(Color.TRANSPARENT);
        giftpageViews.add(nullView2);
        Init_GiftPoint();
    }
    private void Init_GiftPoint() {
        giftpointViews = new ArrayList<ImageView>();
        ImageView imageView;
        for (int i = 0; i < giftpageViews.size(); i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.d1);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 10;
            layoutParams.rightMargin = 10;
            layoutParams.width = 8;
            layoutParams.height = 8;
            giftlayout_point.addView(imageView, layoutParams);
            if (i == 0 || i == giftpageViews.size() - 1) {
                imageView.setVisibility(View.GONE);
            }
            if (i == 1) {
                imageView.setBackgroundResource(R.drawable.d2);
            }
            giftpointViews.add(imageView);
        }
    }


    private void getGiftList(String userid) {
        Map<String, Object> map = new HashMap<String, Object>();

        String head = new JsonUtil(this).httpHeadToJson(this);
        map.put("head", head);
        map.put("u_id", userid);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().gift,map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {

            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("gift:", jsonObject);
                try {
                    int resultCode = jsonObject.getInt("code");

                    if (resultCode == 1) {
//                        String giftString = "[{\"actionCode\":0,\"createTime\":null,\"id\":\"10bc2edc5cf44804a8f24114380aa81f\",\"money\":1,\"picName\":\"一颗爱心\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22HFlGIj.png\",\"updateTime\":null},{\"actionCode\":1,\"createTime\":null,\"id\":\"98a63f0eec484782ba04e78cb339a187\",\"money\":6,\"picName\":\"一朵玫瑰\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22xn05V5.png\",\"updateTime\":null},{\"actionCode\":2,\"createTime\":null,\"id\":\"2b5ebe56e53d4998b442813b964915d4\",\"money\":18,\"picName\":\"一个新年红包\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-2226kPMj.png\",\"updateTime\":null},{\"actionCode\":3,\"createTime\":null,\"id\":\"3ccbd428b987480b9f88838ea604092d\",\"money\":52,\"picName\":\"一个么么哒\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22hdJGik.png\",\"updateTime\":null},{\"actionCode\":4,\"createTime\":null,\"id\":\"7501debdbf8d49c5bb030f3e40d08a8f\",\"money\":99,\"picName\":\"99朵玫瑰\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22dTsxHv.png\",\"updateTime\":null},{\"actionCode\":5,\"createTime\":null,\"id\":\"caf1a20137154191aea4c40954f10d83\",\"money\":188,\"picName\":\"I Love You\",\"picUrl\":\"http://ucloudfile.seex.im:888/staticResource/enjoy/gift/pic/2017-01-22MwVIge.png\",\"updateTime\":null}]";
                        String giftString = jsonObject.getString("data") + "";
                        ArrayList<ChatEnjoy> roomEnjoys = new JsonUtil(EditWeChatActivity.this).jsonToChatEnjoy(giftString);
                       setGifDate(roomEnjoys);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void setGifDate(List<ChatEnjoy> gifdata) {
        init(gifdata);
    }
    private void init(List<ChatEnjoy> datas) {
        giftEnjoys.clear();
        int pageCount = (int) Math.ceil(datas.size() / gifpageSize);
        for (int i = 0; i < pageCount; i++) {
            List<ChatEnjoy> pageData = getData(i, datas);
            if (pageData != null && pageData.size() != 0) {
                giftEnjoys.add(pageData);
            }
        }
    }
    private int gifpageSize = 8;

    private List<ChatEnjoy> getData(int page, List<ChatEnjoy> datas) {
        int startIndex = page * gifpageSize;
        int endIndex = startIndex + gifpageSize;
        if (endIndex > datas.size()) {
            endIndex = datas.size();
        }
        List<ChatEnjoy> list = new ArrayList<ChatEnjoy>();
        list.addAll(datas.subList(startIndex, endIndex));
        return list;
    }
    private void getputWx(String weixinNo,String giftId) {
        if (!netWork.isNetworkConnected()) {
            ToastUtils.makeTextAnim(this, R.string.seex_no_network).show();
            return;
        }
        showProgress(R.string.seex_progress_text);
        String head = new JsonUtil(this).httpHeadToJson(this);
        int userID = (int) SharedPreferencesUtils.get(this, Constants.USERID, -1);
        Map map = new HashMap();
        map.put("head", head);
        map.put("weixinNo",weixinNo);
        map.put("giftId",giftId);
        MyOkHttpClient.getInstance().asyncPost(head,new Constants().submitWeixinNo,map, new MyOkHttpClient.HttpCallBack() {
            @Override
            public void onError(Request request, IOException e) {
                dismiss();
                ToastUtils.makeTextAnim(EditWeChatActivity.this, R.string.seex_getData_fail).show();
            }

            @Override
            public void onSuccess(Request request, JSONObject jsonObject) {
                LogTool.setLog("aa",jsonObject);
//                dismiss();
                if (Tools.jsonSeexResult(EditWeChatActivity.this, jsonObject, null)) {
                    dismiss();
                    return;
                }
                try {
                    int resultCode = jsonObject.getInt("code");
                    if(resultCode==1){
                        ToastUtil.makeText(EditWeChatActivity.this,jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        EditWeChatActivity.this.finish();
                    }else{
                        dismiss();
                        ToastUtil.makeText(EditWeChatActivity.this,jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception E) {

                }
            }
        });
    }
}
