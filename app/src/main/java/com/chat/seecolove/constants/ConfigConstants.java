package com.chat.seecolove.constants;

/**
 * 系统配置常量
 */
public class ConfigConstants {

    public static final String USER_ID = "userId";

    public static final String PAGE_NO = "pageNo";

    public static final String PAGE_SIZE = "pageSize";

    public static final String KEY = "key";

    /**
     * 通讯录——新的好友请求
     */
    public static final class AddRequest {

        public static final String TYPE = "type";

        public static final int TYPE_FRIEND_REQUEST = 0; // 好友请求

        public static final int TYPE_MY_REQUEST = 1; //我的请求

        public static final int REQUEST_PAGE_SIZE = 50; //我的请求

        public static final String REQUEST_FRIEND_ID = "id"; //好友ID

        public static final String FRIEND_REQ_ITEM = "friendItem"; //好友item
    }

    public static final class BecomeSeller {
        public static final String PARAM_USERINFO = "userInfo";

        public static final String PARAM_PAGE_FROM = "pageFrom";

        public static final String PARAM_FROM_AUTH = "becomeSeller";//来自主播认证

        public static final String PARAM_FROM_PROFILEINFO = "profileInfo";//来自profileInfo页面

        public static final String PARAM_BITMAP = "bitmap";

        public static final String PARAM_BITMAP_URL = "bitmapUrl";

        public static final String PARAM_ACTION_TYPE = "ActionType";

        public static final String PARAM_ACTION_TYPE_INFO = "1";//完善信息

        public static final String PARAM_ACTION_TYPE_PHOTO = "2";//更改头像
    }

    public static final class ProfileInfo {

        public static final String PROFILE_ID = "PROFILE_ID";


        public static final String FROM_PAGE = "fromPage";

        public static final String FROM_FRIENDS_LIST = "friendList";

        public static final String ITEM_PHOTO = "item_photo";

        public static final String ITEM_NICKNAME = "item_nickName";

        public static final String ITEM_POSITION = "position";

        public static final String ITEM_USERID = "item_userId";

        public static final String ACTION_TYPE = "action_type";

        public static final String TYPE_UPDATE_INFO = "type_update_info"; //修改昵称和头像

        public static final String TYPE_BLACK_LIST  = "type_blacklist"; //拉黑

        public static final String PROFILE_USERID = "profileUserId";
    }

}
