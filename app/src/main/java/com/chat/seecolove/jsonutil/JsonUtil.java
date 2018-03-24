package com.chat.seecolove.jsonutil;

import android.content.Context;

import com.chat.seecolove.bean.Album;
import com.chat.seecolove.bean.AnchorHomeBean;
import com.chat.seecolove.bean.HobbyBean;
import com.chat.seecolove.bean.OpenWXCaseBean;
import com.chat.seecolove.bean.OrderListBean;
import com.chat.seecolove.bean.PageBean;
import com.chat.seecolove.bean.SeeCoSubMuenBean;
import com.chat.seecolove.bean.WorkBean;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import com.chat.seecolove.bean.Balance;
import com.chat.seecolove.bean.Banner;
import com.chat.seecolove.bean.BlackModel;
import com.chat.seecolove.bean.Call;
import com.chat.seecolove.bean.CallLog;
import com.chat.seecolove.bean.ChatEnjoy;
import com.chat.seecolove.bean.FriendBean;
import com.chat.seecolove.bean.FriendsRequest;
import com.chat.seecolove.bean.HttpHead;
import com.chat.seecolove.bean.Identify;
import com.chat.seecolove.bean.Notif;
import com.chat.seecolove.bean.Order;
import com.chat.seecolove.bean.ProfileBean;
import com.chat.seecolove.bean.Recharge;
import com.chat.seecolove.bean.Room;
import com.chat.seecolove.bean.RoomEnjoy;
import com.chat.seecolove.bean.ShareInfo;
import com.chat.seecolove.bean.ShareNumberBean;
import com.chat.seecolove.bean.TopShareBean;
import com.chat.seecolove.bean.TopOwn;
import com.chat.seecolove.bean.UserInfo;
import com.chat.seecolove.bean.VipMenu;

/**
 */
public class JsonUtil {
    public Gson gson;

    public JsonUtil(Context context) {
        if (gson == null) {
//            gson = new Gson();
            gson = new GsonBuilder().disableHtmlEscaping().create();
        }
    }

//    public Order jsonToOrder(String jsonStr) {
//        return gson.fromJson(jsonStr, Order.class);
//    }
    public String httpHeadToJson(Context context) {
        return gson.toJson(new HttpHead(context));
    }

    public UserInfo jsonToUserInfo(String jsonStr) {
        return gson.fromJson(jsonStr, UserInfo.class);
    }

    public String orderToJson(Order order) {
        return gson.toJson(order);
    }

    public Order jsonToOrder(String jsonStr) {
        return gson.fromJson(jsonStr, Order.class);
    }
    public Call jsonToCall(String jsonStr) {
        return gson.fromJson(jsonStr, Call.class);
    }


    public List<String> jsonToLists(String jsonStr) {
        List<String> list = gson.fromJson(jsonStr, new TypeToken<List<String>>() {
        }.getType());
        return list;
    }

    public List<VipMenu> jsonToVipMenus(String jsonStr) {
        List<VipMenu> vipMenus = gson.fromJson(jsonStr, new TypeToken<List<VipMenu>>() {
        }.getType());
        return vipMenus;
    }

    public List<Room> jsonToRooms(String jsonStr) {
        List<Room> rooms = gson.fromJson(jsonStr, new TypeToken<List<Room>>() {
        }.getType());
        return rooms;
    }
    public List<CallLog> jsonToCallLogs(String jsonStr) {
        List<CallLog> callLogs = gson.fromJson(jsonStr, new TypeToken<List<CallLog>>() {
        }.getType());
        return callLogs;
    }

    public  List<Balance> jsonToBalances(String jsonStr) {
        List<Balance> balances = gson.fromJson(jsonStr, new TypeToken<List<Balance>>() {
        }.getType());
        return balances;
    }
    public  List<Notif> jsonToNotifs(String jsonStr) {
        List<Notif> notifs = gson.fromJson(jsonStr, new TypeToken<List<Notif>>() {
        }.getType());
        return notifs;
    }
    public  List<BlackModel> jsonToBlackModels(String jsonStr) {
        List<BlackModel> blackModels = gson.fromJson(jsonStr, new TypeToken<List<BlackModel>>() {
        }.getType());
        return blackModels;
    }



    public  List<Banner> jsonToBanners(String jsonStr) {
        List<Banner> banners = gson.fromJson(jsonStr, new TypeToken<List<Banner>>() {
        }.getType());
        return banners;
    }

    public  List<ShareInfo> jsonToShareInfos(String jsonStr) {
        List<ShareInfo> shareInfos = gson.fromJson(jsonStr, new TypeToken<List<ShareInfo>>() {
        }.getType());
        return shareInfos;
    }


    public ProfileBean jsonToProfileBean(String jsonStr){
        return gson.fromJson(jsonStr, ProfileBean.class);
    }
    public TopOwn jsonToTopOwn(String jsonStr){
        return gson.fromJson(jsonStr, TopOwn.class);
    }

    /**
     *  json转换成好友请求列表
     * @param jsonStr
     * @return
     */
    public  List<FriendsRequest> jsonToFriendsRequest(String jsonStr) {
        List<FriendsRequest> friendsRequests = gson.fromJson(jsonStr, new TypeToken<List<FriendsRequest>>() {
        }.getType());
        return friendsRequests;
    }
    /**
     *  json转换成好友列表
     * @param jsonStr
     * @return
     */
    public  List<FriendBean> jsonToFriendBeans(String jsonStr) {
        List<FriendBean> friendsRequests = gson.fromJson(jsonStr, new TypeToken<List<FriendBean>>() {
        }.getType());
        return friendsRequests;
    }
    /**
     *  json转换邀请的好友列表
     * @param jsonStr
     * @return
     */
    public  List<ShareNumberBean> jsonToShareNumberBean(String jsonStr) {
        List<ShareNumberBean> friendsRequests = gson.fromJson(jsonStr, new TypeToken<List<ShareNumberBean>>() {
        }.getType());
        return friendsRequests;
    }
    /**
     *  json转换邀请的TOP列表
     * @param jsonStr
     * @return
     */
    public  List<TopShareBean> jsonToTopShareBean(String jsonStr) {
        List<TopShareBean> friendsRequests = gson.fromJson(jsonStr, new TypeToken<List<TopShareBean>>() {
        }.getType());
        return friendsRequests;
    }
    /**
     *  json转换礼物列表
     * @param jsonStr
     * @return
     */
    public ArrayList<RoomEnjoy> jsonToRoomEbjoy(String jsonStr) {
        ArrayList<RoomEnjoy> friendsRequests = gson.fromJson(jsonStr, new TypeToken<List<RoomEnjoy>>() {
        }.getType());
        return friendsRequests;
    }
    /**
     *  json转换礼物列表
     * @param jsonStr
     * @return
     */
    public ArrayList<ChatEnjoy> jsonToChatEnjoy(String jsonStr) {
        ArrayList<ChatEnjoy> friendsRequests = gson.fromJson(jsonStr, new TypeToken<List<ChatEnjoy>>() {
        }.getType());
        return friendsRequests;
    }


    public Identify jsonToIdentify(String jsonStr){
        return gson.fromJson(jsonStr, Identify.class);
    }


    public ArrayList<Recharge> jsonToRoomRecharge(String jsonStr) {
        ArrayList<Recharge> rechargeArrayList = gson.fromJson(jsonStr, new TypeToken<List<Recharge>>() {
        }.getType());
        return rechargeArrayList;
    }

    /**
     * 职业
     * @param jsonStr
     * @return
     */
    public  List<WorkBean> jsonToWorkBean(String jsonStr) {
        List<WorkBean> workBeenModels = gson.fromJson(jsonStr, new TypeToken<List<WorkBean>>() {
        }.getType());
        return workBeenModels;
    }
    /**
     * 职业
     * @param jsonStr
     * @return
     */
    public  List<HobbyBean> jsonToHobbyBean(String jsonStr) {
        List<HobbyBean> workBeenModels = gson.fromJson(jsonStr, new TypeToken<List<HobbyBean>>() {
        }.getType());
        return workBeenModels;
    }
    /**
     * 主播界面
     * @param jsonStr
     * @return
     */
    public  List<AnchorHomeBean> jsonToAnchorHomeBean(String jsonStr) {
        List<AnchorHomeBean> workBeenModels = gson.fromJson(jsonStr, new TypeToken<List<AnchorHomeBean>>() {
        }.getType());
        return workBeenModels;
    }
    /**
     * 金主界面
     * @param jsonStr
     * @return
     */
    public  List<OrderListBean> jsonToJinzhuBean(String jsonStr) {
        List<OrderListBean> workBeenModels = gson.fromJson(jsonStr, new TypeToken<List<OrderListBean>>() {
        }.getType());
        return workBeenModels;
    }

    public ChatEnjoy jsonToChatEnjoybean(String jsonStr) {
        return gson.fromJson(jsonStr, ChatEnjoy.class);
    }

    public PageBean jsonToPageBean(String jsonStr) {
        return gson.fromJson(jsonStr, PageBean.class);
    }
    public  List<Album> jsonToImages(String jsonStr) {
        List<Album> albmBeenModels = gson.fromJson(jsonStr, new TypeToken<List<Album>>() {
        }.getType());
        return albmBeenModels;
    }
    public  List<SeeCoSubMuenBean> jsonToMuen(String jsonStr) {
        List<SeeCoSubMuenBean> muenModels = gson.fromJson(jsonStr, new TypeToken<List<SeeCoSubMuenBean>>() {
        }.getType());
        return muenModels;
    }

    public OpenWXCaseBean jsonToOpenWXCaseBean(String jsonStr) {
        return gson.fromJson(jsonStr, OpenWXCaseBean.class);
    }
}
