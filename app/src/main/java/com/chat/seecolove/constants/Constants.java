package com.chat.seecolove.constants;


import com.facebook.common.util.ByteConstants;

import com.chat.seecolove.tools.SharedPreferencesUtils;
import com.chat.seecolove.view.activity.MyApplication;


public class Constants {
    public static Constants constants;
    public static final String chinalValue="chinalValue";
    public synchronized static Constants getInstance() {
        if (constants == null) {
            constants = new Constants();
        }
        return constants;
    }
    public static final String expired_key="expired";//退出标志
    public static final String IMAGE_PIPELINE_CACHE_DIR = "coquettish";//默认图所放路径的文件夹名
    public static final int MAX_DISK_CACHE_SIZE = 200 * ByteConstants.MB;//默认图磁盘缓存的最大值
    public static final int MAX_DISK_CACHE_LOW_SIZE = 100 * ByteConstants.MB;//默认图低磁盘空间缓存的最大值
    public static final int MAX_DISK_CACHE_VERYLOW_SIZE = 80 * ByteConstants.MB;//默认图极低磁盘空间缓存的最大值
    public static final int MAX_MEMORY_CACHE_SIZE = (int) Runtime.getRuntime().maxMemory() / 4;

    public static final String MP4NAME = "/seex_auth.mp4";

    /* 裁剪图片 */
    public static final int REQUEST_CODE_PHOTO_CUT = 64412;

    /* 从相册选择 */
    public static final int REQUEST_CODE_PHOTO_ALBUM = 64410;
    /* 拍照 */
    public static final int REQUEST_CODE_PHOTO_GRAPH_UserICON = 64411;
    /**
     *
     */
    public static final int REQUEST_CODE_PHOTO_UserICON = 64415;
    /* 拍照 */
    public static final int REQUEST_CODE_PHOTO_GRAPH_UserImages = 64413;

    public static final int REQUEST_PHOTO_UserICON = 64417;
    public static final int NOTIFY_ID = 0x90;

    public static final String LOCAL_FOLDER_NAME = "local_folder_name";//跳转到相册页的文件夹名称
    public static final int UPLOAD_IMAGE = 64;

    //用户信息
    public static final String oldUserId = "oldUserId";//存放用户的旧Id,应用中会结合userId,拼节为唯一字段
    public static final String oldUserType = "oldUserType";//存放用户的旧userType，应用中会结合userId,拼节为唯一字段
    public static final int type_Seller = 1;//卖家

    public static final String SESSION = "session";
    public static final String USERID = "userId";
    public static final String USERICON = "portrait";
    public static final String NICKNAME = "nickName";
    public static final String USERPHONE = "userPhone";
    public static final String ISVIDEOAUDIT = "isVideoAudit";
    public static final String USERTYPE = "userType"; //0：买家
    public static final String WELCOME = "welcome";
    public static final String USERPRICE = "userPrice";
    public static final String YUNXINACCID = "yunxinAccid";
    public static final String SEX = "sex";  // 1:男     2：女
    public static final String SHOWID = "showId";
    public static final String ISPERFECT = "isPerfect";//1：资料未完善  2：已完善
    public static final String NOTROUBLE = "notrouble";
    public static final String SHOWFRIENDTIP = "showFriendTip"; // 0：不显示    1：显示
    //    public static final String SHOWREDPOINT = "showRedPoint"; //骚友"+"号上红点      0：不显示  1：显示
    public static final String SHOWREDPOINT_INSET_NUM = "showRedPoint_inset_num"; //骚友 好友请求数量 默认为0
    public static final String ISVIDEOSWHIC = "videoAvaliable "; // 1：显示    2：不显示
    public static final String SHOWREDPOINT_NOTIF = "showRedPoint_notify"; //通知 红点      0：不显示  1：显示
    public static final String ISShowGIFT = "ISShowGIFT "; // 1：显示    2：不显示
    public static final String SHOWREDPOINT_NOTIF_NUM = "showRedPoint_notify_num"; //通知计数
    public static final String FIRSTLOAD = "firstload";
    public static final String GUIDE = "guide";
    public static final String VERSIONCODE = "versionCode";
    public static final String ISNEW = "isNew";
    public static final String APP_STATUS = "app_status";//app是否启动 0:未启动(未在前台)  1：已启动
    public static final String UMID = "umid";// U盟朋友圈ID
    public static final String FRIEND_UMID = "friend_umid_";// U盟朋友圈ID
    public static final String PUSH_ID = "push_id";//小米推送ID或友盟token
    public static final String IFHAVERED = "isHaveRed";
    public static final String EVENT_TIME = "event_time";//活动时间
    public static final String DIVERTENY = "coquettish_diverteny";
    public static final String CHECK_PERM = "check_permission";
    public static final String DIALOG_TIP = "dialog_tip";
    public static final String THEME_INDEX = "theme_index";
    public static final String GUIDE_ONLINE = "guide_online";
    public static final String GUIDE_MINE = "guide_mine";
    public static final String GUIDE_PROFILE = "guide_profile";
    public static final String GUIDE_LIST = "guide_list";
    public static final String GUIDE_THEME= "guide_theme";
    public static final String LEVEL_SMOOTH= "level_smooth";
    public static final String LEVEL_LIGHT= "level_light";

    public static final String IntentKey="Intentkey";
    public static final String DurationKey="Durationkey";
    public static final String UserMonyeKey="UserMonyeKey";

    public static final String DIALOG_VOICE_TIP = "dialog_voice_tip";

    /**
     * 聊天备注缓存开头
     */
    public static final String NN_= "NN_";

    public static final String MAIN_IF_START = "MAIN_IF_START";// U盟朋友圈ID

    public static final String AGORA_KEY = "05d406dce35748ab97f9712d3166f8e6";
    public static final int UMENG_CHANNEL = 13;//  13服务端渠道号


    //通讯录——更新列表的时间
    public static final String MAIL_UPDATE_TIME = "mail_update_time";
    //通讯录——对方同意/删除的变化总条数
    public static final String MAIL_UPDATE_NUM = "mail_update_num";

    /*//通讯录中，添加或者删除最大变化数
    public static final int MAIL_UPDATE_MAX_NUM = 30;
    //通讯录中，不请求后台，使用数据库的最大时间
    public static final long MAIL_UPDATE_MAX_TIME = 2*24 * 60 * 60 * 1000;*/


    //通讯录中，添加或者删除最大变化数
    public static final int MAIL_UPDATE_MAX_NUM = 5;// 预计10
    //通讯录中，不请求后台，使用数据库的最大时间
    public static final long MAIL_UPDATE_MAX_TIME = 10 * 60 * 1000; //预计一周

    public static final long MAIL_MAX_FRIENDS_REQ = 10;

    //权限标记
    public static final int ACCESS_COARSE_LOCATION = 0;
    public static final int READ_PHONE_STATE = 1;
    public static final int WRITE_EXTERNAL_STORAGE = 2;
    public static final int CAMERA = 3;
    public static final int RECORD_AUDIO = 4;
    public static final int CAMERA_RECORD = 5;
    public static final int CAMERA_Images = 6;
    // IM自定义字段 key
    public static final String userid = "userid";
    public static final String nickname = "nickname";
    public static final String headurl = "headurl";
    public static final String netid = "netid";
    public static final String sys_buyer_id = "14066989";
    // 买家客服
    public static final String sys_buyer = "sechax-yxline-14067022";
    // 卖家客服
    public static final String sys_seller = "sechax-yxline-14067056";
    //广播action
    public static final String ACTION_MAIN_SESSION = "Seex_action_main_seesion";
    public static final String ACTION_LOGOUT = "Seex_action_logout";
    public static final String ACTION_CREATE_ORDER = "Seex_action_create_order";
    public static final String ACTION_CREATE_ORDER_PROFILE = "Seex_action_create_order_profile";
    public static final String ACTION_CREATE_ORDER_ALL = "Seex_action_create_order_all";
    public static final String ACTION_CREATE_ORDER_TOP = "Seex_action_create_order_top";
    public static final String ACTION_SELLER_STATUS = "Seex_action_seller_status";
    public static final String ACTION_SELLER_ISONLINE = "Seex_action_seller_isonline";
    public static final String ACTION_USERICON_ISERROE = "Seex_action_usericon_iserror";
    public static final String ACTION_ROOM_CALL = "Seex_action_room_call";
    public static final String ACTION_ROOM_VOICE = "Seex_action_room_voice";
    public static final String ACTION_ROOM_HANG_UP = "Seex_action_room_hang_up";
    public static final String ACTION_NOT_READ = "Seex_action_not_read";
    public static final String ACTION_HEARTBEAT = "Seex_action_heartbeat";
    public static final String ACTION_SELLER_OFFONLINE = "Seex_action_seller_offonline";
    public static final String ACTION_FRIEND = "Seex_action_seller";
    public static final String ACTION_NOTIFY = "Seex_action_notify";
    public static final String ACTION_NOTIFY_NUM = "Seex_action_notify_num"; // 系统消息通知
    public static final String ACTION_AD = "Seex_action_ad"; // 公告消息
    public static final String ACTION_THEME_MARK = "Seex_action_theme_mark"; // 主题功能红点
    public static final String ACTION_ENJOY = "Seex_ACTION_ENJOY"; // 打赏
    public static final String ACTION_BUYERS_REARGE = "Seex_ACTION_BUYERS_REARGE"; // 买家充值
    public static final String ACTION_PORNOGRAPHIC = "Seex_ACTION_PORNOGRAPHIC"; // 收到涉黄
    public static final String ACTION_MSG_CHANGE = "Seex_ACTION_MSG_CHANGE"; // 聊天消息通知
    public static final String ACTION_ROYALTY_CHANGE= "Seex_action_royalty_change"; // 账单状态变化
    public static final String ACTION_BlUR_BG= "Seex_action_blur_bg"; // 视频模糊开关
    public static final String ACTION_MAIL_REQ_AGREE = "Seex_action_mail_req_agree"; // 好友请求列表同意
    public static final String ACTION_UPDATE_FRIEND_DATABASE = "Seex_update_friend_and_Database"; //进入profile后，头像和昵称变化，更新数据库和好友列表

    public static final String ACTION_UPDATE_PHOTO = "Seex_update_photo"; // 更新头像

    public static final String ACTION_UPDATE_NICKNAME = "Seex_update_name"; // 更新备注


    public static final String ACTION_PROFILE_UPDATE_PHOTO = "Seex_profile_update_photo"; //profile页更新头像
    public static final String ACTION_ADD_FRIEND_REQ_IN_VIDEO = "Seex_add_friend_req_in_video"; //视屏内请求加好友
    public static final String ACTION_ADD_FRIEND_AGREE_IN_VIDEO = "Seex_add_friend_agree_in_video"; //视屏内同意加好友
    public static final String ACTION_AGREE_UPDATE_CALL_RECORD = "Seex_agree_update_call_record"; //收到好友请求同意后，profile页面刷新

    public static final String WX_AppId = "wx07eda8e868c3a16e";
    public static final String CUSTOM_TYPE= "type";
    public static final String CUSTOM_DATA= "data";
    public static final int GIFT_COMBO= 1;
    public static final int GIFT_ORTHER= 4;
    public static final String CHAT_NAME= "chat_name";



    public static final String NetId_Buyer = "NetId_Buyer";
    public static final String NetId_SeBuyer = "NetId_SeBuyer";

    public static final int UserTag =0;
    public static final int AnchorTag =1;
    public static int ROOM_ID = 88;

     /** * 正式---公网 **/
    public String SOCKET = SharedPreferencesUtils.get(MyApplication.getContext(), "SOCKET", "http://36.255.223.213:9092") + "";
    public String BaseURL = SharedPreferencesUtils.get(MyApplication.getContext(), "BaseURL", "http://api.seecolove.com/") + "";
    public String URL = SharedPreferencesUtils.get(MyApplication.getContext(), "URL", "http://api.seecolove.com/match/user/") + "";
    //public String URL = SharedPreferencesUtils.get(MyApplication.getContext(), "URL", "http://23.91.98.54:8725/match/user/") + "";
    public String MATCHURL = SharedPreferencesUtils.get(MyApplication.getContext(), "MATCHURL", "http://api.seecolove.com/match/") + "";
    public String NEW_MATCHURL = SharedPreferencesUtils.get(MyApplication.getContext(), "NEW_MATCHURL", "http://api.seecolove.com/mapper/") + "";
    public String ORDER = SharedPreferencesUtils.get(MyApplication.getContext(), "ORDER", "http://api.seecolove.com/") + "";


//    public String SOCKET = SharedPreferencesUtils.get(MyApplication.getContext(), "SOCKET", "http://106.75.104.68:9092") + "";
//    public String BaseURL = SharedPreferencesUtils.get(MyApplication.getContext(), "BaseURL", "http://106.75.104.68:8725/") + "";
//    public String URL = SharedPreferencesUtils.get(MyApplication.getContext(), "URL", "http://106.75.104.68:8725/match/user/") + "";
//    public String MATCHURL = SharedPreferencesUtils.get(MyApplication.getContext(), "MATCHURL", "http://106.75.104.68:8725/match/") + "";
//    public String NEW_MATCHURL = SharedPreferencesUtils.get(MyApplication.getContext(), "NEW_MATCHURL", "http://106.75.104.68:8725/mapper/") + "";
//    public String ORDER = SharedPreferencesUtils.get(MyApplication.getContext(), "ORDER", "http://106.75.104.68:8725/") + "";

//     **/





    /**
     * 注册获取手机验证码
     * userMobile  手机号码
     * *
     */

    public String getSmsCode = URL + "userRegister/getSmsCode.do";

    public String getNewSmsCode=URL+"password/sendCode.do";

    /**
     * 修改密码 获取手机验证码
     * userMobile  手机号码
     * *
     */
    public String getUpdatePwdSmsCode = URL + "userLogin/getUpdatePwdSmsCode.do";

    /**
     * 修改密码
     * userMobile	String	手机号
     * smsCode	String	获取的验证码
     * password	String	用户设置的密码
     * *
     */
    public String updatePwd = URL + "userLogin/updatePwd220.do";


    /**
     * 验证用户session
     * userId   int     用户id
     * session String
     * *
     */
    public String sessionIsInvalid = URL + "userLogin/sessionIsInvalid.do";

    /**
     * 注册
     * userMobile	String	手机号
     * smsCode	String	获取的验证码
     * password	String	用户设置的密码
     * channel	int	用户渠道来源
     * *
     */
//    public String register = URL + "userRegister/register220.do";
     public String register = URL + "userLogin/loginForCode.do";//快速登录
     public String New_register = URL + "password/register.do";//注册
     public String userLogin = URL + "password/userLogin.do";//登录
     public String New_editPw = URL + "password/editPw.do";//修改密码
    /**
     * 登录
     * userMobile	String	手机号
     * password	String	用户设置的密码
     * *
     */
//    public String login = URL + "userLogin/userLogin220.do";
      public String login = URL + "userLogin/loginForCode.do";


    /**
     * 充值
     * money	String	充值金额	是	单位:元。 保留两位小数
     * cipher	String	数据签名	是	MD5加密的签名，参与签名的参数以及顺序如下：
     * （ userId+ money+” top-up”）
     * <p>
     * *
     */
    public String addMoney = URL + "isLogin/userInfo/addMoney.do";


    /**
     * 银联
     * *
     */
    public String getUnionpayNo = URL + "isLogin/unionpay/getUnionpayNo.do";


    /**
     * 卖家上线
     * <p/>
     * sellerId	int	卖家id	是
     * key	String	md5校验码	( sellerId + "" + unitPrice + "chat")
     * unitPrice	float	卖家的单价	是	2位小数
     * *
     */
//    public String matchSeller = MATCHURL + "isLogin/matchSeller.do";


    /**
     * 卖家下线
     * <p>
     * sellerId 	int	用户id
     * key	String	md5校验码	是
     * <p>
     * *
     */
    public String cancleMatch = MATCHURL + "isLogin/cancleMatch.do";

    /**
     * 获取房间列表
     * <p>
     * buyerId	int	买家id	是
     * pageNo	int	分页	是	默认1
     * pageSize	int	页大小	是	默认10
     * key	String	md5校验码	是
     * <p>
     * *
     */
    public String matchBuyer = MATCHURL + "matchBuyer.do";


    /**
     * 绑定银行卡
     * <p>
     * body	String	用户银行相关信息的加密密文
     * *
     */
    public String bindBank = URL + "isLogin/userInfo/bindBank.do";

    /**
     * 修改银行卡
     * <p>
     * body	String	用户银行相关信息的加密密文
     * *
     */
    public String updateBank = URL + "isLogin/userInfo/updateBank.do";


    /**
     * 获取银行卡
     * <p>
     * *
     */
    public String getUserBank = URL + "isLogin/userInfo/getUserBank.do";


    /**
     * 获取个人信息
     * <p>
     * *
     */
    public String getUserInfo = URL + "isLogin/userInfo/getUserInfo.do";


    /**
     * 上传头像
     * file     File头像文件
     * *
     */
    public String uploadPortrait = URL + "isLogin/userInfo/uploadPortrait.do";

    /**
     * 上传头像--成为B端后上传头像
     * file     File头像文件
     * *
     */
    public String seller_uploadPortrait = URL + "isLogin/sellerInfo/uploadPortrait.do";

    public String UPLOAD_COMMON_FILE = MATCHURL + "file/fileUpload.do";


    /**
     * 修改昵称
     * usName     String  昵称
     * *
     */
    public String updateNickName = URL + "isLogin/userInfo/updateNickName.do";


    /**
     * 创建订单
     * sellerId	int	卖家id	是
     * buyerId	int	买家id	是
     * price	float	单价	是	单元（元/分钟），2位小数
     * sellerSex	    int	卖家性别	是	1是男，2是女，默认2
     * callFlag	   int	主叫标识	是	1是买家主叫，2是卖家主叫，默认1
     * key	String	   MD5加密这段	是	sellerId + buyerId + price + sellerSex + callFlag+"order"
     * <p>
     * *
     */
    public String create = ORDER + "order/video/create";

    public String createVoice = ORDER + "order/voice/create";
    /**
     * 卖家查询订单
     * sellerId     int  卖家ID
     * *
     */
    public String getSellerOrder = ORDER + "order/video/seller/batchquery";
    public String getSellerVoiceOrder = ORDER + "order/voice/seller/batchquery";
    /**
     * 分页获取账单列表信息
     * page	String	当前多少页	否	默认第一页
     * rows	String	每页显示的条目数	否	默认10条。如果该值小于0或者大于30也会被系统处理为默认值
     * *
     */
    public String getBillDetails = URL + "isLogin/userInfo/page/getBillDetails.do";

    /**
     * 举报
     * <p>
     * informerId	int	举报者的id	是
     * beInformersId	int	被举报者的id	是
     * file	File	抓取的图片	是
     * <p>
     * *
     */
    public String report = URL + "isLogin/userInfo/informUser.do";


    /**
     * 提现
     * <p>
     * *
     */
    public String applyWithdrawal = URL + "isLogin/userInfo/applyWithdrawal.do";


    /**
     * 推送
     * 注册 token
     * <p>
     * id	        String	Im账号
     * token	String	回话id 友盟
     * type	String	 设备类型 1为 ios；2为 Android
     * key	    String	MD5加密这段     id+token+type
     * *
     */
//    public String storeAccount = TOKEN + "chatmepush/send/storeAccount.do";


    /**
     * 推送
     * 注册 token
     * <p>
     * id	        String	Im账号
     * token	String	回话id 友盟
     * type	String	 设备类型 1为 ios；2为 Android
     * firm	    String	 设备系统类型  0为其他   1： 小米
     * key	    String	MD5加密这段     id+token+type+firm
     * *
     */
//    public String storeAccountFirm = TOKEN + "chatmepush/send/storeAccountFirm.do";


    /**
     * 版本更新
     * <p>
     * *
     */
    public String updateVersion = URL + "userLogin/updateVersion.do";


    /**
     * 判断有无红包
     * <p>
     * *
     */
    public String isHaveRedEnvelope = MATCHURL + "sysAward/isHaveRedEnvelope.do";


    /**
     * 获取系统赠送的红包
     * <p>
     * *
     */
    public String getRedEnvelope = MATCHURL + "sysAward/isLogin/getRedEnvelope.do";


    /**
     * 分页获取历史通知消息
     * page	String	当前多少页	否	默认第一页
     * rows	String	每页显示的条目数	否	默认10条。如果该值小于0或者大于30也会被系统处理为默认值
     * *
     */
    public String getMessageRecord = MATCHURL + "message/isLogin/messageInfo/page/getMessageRecord.do";


    /**
     * 查询是否在线
     * sellerId	int	用户id
     * key	String	md5校验码	是
     * *
     */
    public String isOnline = MATCHURL + "isLogin/isOnline.do";

    /**
     * 查询  收益累计和当日呼叫时长
     * *
     */
    public String incomeStatistic = URL + "isLogin/userInfo/incomeStatistic.do";


    // v2.0.0*************************************************************
    /**
     * nickName	String	用户昵称	是	12个字符内
     * userBirthday	String	出生日期	是
     * sex	Int	用户性别	是	1：男。2：女。
     * portrait	String	用户头像	否	个人中心头像是单独接口，请调用相关接口上传头像。这个接口里不是必填的。
     * <p>
     * *
     */
    public String perfectUserInfo = URL + "isLogin/sellerInfo/perfectUserInfo220.do";
    public String perfectUserInfo_220 = URL + "isLogin/sellerInfo/perfectUserInfoNew.do";

    /**
     * 上传视频
     * file	File	视频文件	是
     * videoNumber	String	视频验证码	是
     * <p>
     * *
     */
    public String uploadVideo = URL + "isLogin/sellerInfo/uploadVideo.do";


    /**
     * 获取新神列表
     * userId 	int
     * sex	int	卖家性别	是	0是全部，1是男，2是女，默认查询全部
     * pageNo	int	分页	是	默认1，取值范围是1 - 10
     * pageSize	int	页大小	是	默认10
     * key	String	md5校验码	是	sex+pageNo + pageSize + "mapper"
     * <p>
     * *
     */
    public String matchNew = NEW_MATCHURL + "matchNew";

    /**
     * 获取骚神列表
     * userId 	int
     * sex	int	卖家性别	是	0是全部，1是男，2是女，默认查询全部
     * pageNo	int	分页	是	默认1，取值范围是1 - 10
     * pageSize	int	页大小	是	默认10
     * key	String	md5校验码	是	sex+pageNo + pageSize + "mapper"
     * <p>
     * *
     */
    public String matchSeller = NEW_MATCHURL + "matchSeller";


    /**
     * 获取骚榜列表
     * flag	int	匹配的标识	是	1是买家周榜，2是买家月榜，3是卖家周榜，4是卖家月榜
     * userId	int	用户id	是
     * pageNo	int	分页	是	默认1，取值范围是1 - 10
     * pageSize	int	页大小	是	默认10
     * key	String	md5校验码	是	flag+ userId +pageNo + pageSize + "getTop100"
     * <p>
     * <p>
     * *
     */
    public String getTop100 = NEW_MATCHURL + "getTop100";


    /**
     * 上传地理位置
     * province	 String	省名	是
     * city	String	市名	是
     * <p>
     * longitude	String	经度	否	建议上传
     * <p>
     * latitude	String	纬度	否	建议上传
     * <p>
     * *
     */
    public String uploadLBS = URL + "isLogin/userInfo/userPosition.do";
    /**
     * 获取用户的profile
     * <p>
     * *
     */
    public String getProfile = URL + "userInfo/getProfile230.do";
    /**
     * 申请好友验证
     * <p>
     * *
     */
    public String checkFriend = MATCHURL + "userFriend/isLogin/checkFriend.do";
    /**
     * 获取好友申请列表
     * <p>
     * *
     */
    public String getFriendsRequest = MATCHURL + "friend/isLogin/list.do";
    /**
     * 处理好友申请拒绝或者接受
     * <p>
     * *
     */
    public String dealFriendApply = MATCHURL + "userFriend/isLogin/dealFriendApply.do";
    /**
     * 存储友盟返回的ID
     * <p>
     * *
     */
    public String saveUmengId = MATCHURL + "user/isLogin/userInfo/saveUmengId.do";
    /**
     * 获取通讯录好友列表
     * <p>
     * *
     */
    public String getFriends = MATCHURL + "friend/isLogin/list.do";
    /**
     *
     * 获取我的邀请统计数
     * *
     */
    public String getSMoneyAndUser = MATCHURL + "sysAward/isLogin/getSMoneyAndUser.do";
    /**
     *
     * 主播分销TOP
     * *
     */
    public String getShareTop = MATCHURL + "menuList/getShareTop.do";
    /**
     *
     * 4.1.11分销金额充值
     * *
     */
    public String topUpShareAssociate = MATCHURL + "sysAward/isLogin/topUpShareAssociate.do";
    /**
     *
     * 4.1.11分销金额提现
     * *
     */
    public String waShareAssociate = MATCHURL + "sysAward/isLogin/waShareAssociate.do";
    /**
     *
     * 获得分销的规则是6元，10%，10%
     * *
     */
    public String getSAProfitRules = MATCHURL + "sysAward/getSAProfitRules.do";
    /**
     *
     * 用户可查看自己邀请的的具体信息列表。
     * *
     */
    public String getSAByPage = MATCHURL + "/sysAward/isLogin/page/getSAByPage.do";
    /**
     * 删除好友
     * <p>
     * *
     */
    public String deleteFriend = MATCHURL + "userFriend/isLogin/deleteFriend.do";

    /**
     * 获取通话记录
     * userId	int	用户id	是
     * userType	int	买卖标志	是	0是买，1是卖
     * status	int	查询条件	是	0是全部，1是呼入，2是呼出，3是未接
     * pageNo	int	页数	是
     * pageSize	int	每页数量	是
     * <p>
     * *
     */
    public String getCallHistory = ORDER + "order/callrecord/batchquery";

    public String getvoiceHistory = ORDER + "order/callrecord/vioce/batchquery";
    /**
     * 删除通话记录
     * id	int	用户id	是	通话记录id
     * userType	int	买卖标志	是	0是买家删除，1是卖家删除
     * key	String	查询条件	是	id + userType + "callHistory"
     * <p>
     * <p>
     * *
     */
    public String deleteCallHistory = ORDER + "order/deleteCallHistory.do";


    /**
     * 获取用户的profile
     * showId	String	用户的showId	是
     * *
     */
    public String queryFriend = MATCHURL + "userFriend/isLogin/queryFriend242" +
            "" +
            "" +
            ".do";

    /**
     * 用户反馈
     * content	String	反馈的详情信息	是	200字以内的描述信息
     * *
     */
    public String feedback = MATCHURL + "feedback/isLogin/feedback.do";


    /**
     * 免打扰
     * userId	int	用户id		是
     * sex	int	卖家性别	是	1 男，2 女
     * flag	int	标志	是	0是免打扰，１是取消免打扰
     * key	String	md5校验值	是	userId+sex +flag+ "notrouble"
     * <p>
     * *
     */
    public String notrouble = NEW_MATCHURL + "notrouble.do";


    /**
     * 查询是否是免打扰
     * userId 	int	用户id	 是
     * <p>
     * *
     */
    public String queryIsNotrouble = NEW_MATCHURL + "queryIsNotrouble.do";

    /**
     * 卖家修改价格
     * sellerId	int	卖家的ID	是
     * price	String	价格	是	2位小数
     * key	String	MD5校验值	是	sellerId+price + "changePricehaiwei"
     * *
     */
    public String changePrice = NEW_MATCHURL + "changePrice";
    /**
     *获取所有的订单投诉选项
     */
    public String getAllFackOrderProperties = ORDER + "order/complain/optionlist/query";

    /**
     * 卖家修改价格
     * sellerId	int	卖家的id	是
     * key	String	md5校验码	是	sellerId + "bsellerhaiwei"
     * <p>
     * *
     */
//    public String getPrice = MATCHURL + "getSellerPriceRange";
    public String getPrice = MATCHURL + "getSellerPriceRange";


    /**
     * 加入黑名单
     * blacklistedId	int	被拉黑者id	是
     * *
     */
    public String addBlackList = MATCHURL + "userFriend/isLogin/blacklistFriend.do";

    /**
     * 将黑名单中的用户移除黑名单
     * blacklistedId	Int	被移除黑名单用户id	是
     * *
     */
    public String removeBlacklist = MATCHURL + "userFriend/isLogin/removeBlacklist.do";


    public String addWithdraw = MATCHURL + "withdraw/isLogin/addWidthdraw.do";
    public String delWithdraw = MATCHURL + "withdraw/isLogin/delWidthdraw.do";
    public String withdrawInfo = MATCHURL + "withdraw/isLogin/withdrawInfo.do";
    public String selectConfigs = MATCHURL + "withdraw/isLogin/selectConfigs.do";

    /**
     * 获取黑名单列表
     * *
     */
    public String getBlacklist = MATCHURL + "userFriend/isLogin/getBlacklist.do";
    /**
     * 反馈异常
     * <p>
     * file	File
     * <p>
     * *
     */
    public String feedBack = URL + "userLogin/uploadAppLog.do";

    /**
     * 用户登录系统后,可获取系统公告。接口返回一个页面http地址
     * <p>
     * userType	String	用户角色	是	0买家, 1卖家
     * appType	String	用户类型	是	1表示Android用户，2表示ios用户
     */
    public String getActivity = MATCHURL + "sysAward/isLogin/getActivity.do";
    /**
     * 获取充值金额归档列表
     * <p>
     * 用户充值时选择充值金额，此接口返回充值金额列表
     */
    public String getTopUpList = MATCHURL + "user/isLogin/buyerInfo/getTopUpList.do";
    /**
     * 充值 [v2.0.2]
     */
    public String addMoney_new = MATCHURL + "user/isLogin/buyerInfo/addMoney.do";

    /**
     * 获取OOS token
     * flag  int  随机数  是  1000以内的随机数
     * key  string  md5校验值  是  flag +"haiweimediaPolicy"
     */
//    public String getOOSToken = MEDIO + "media/policy/getpolicy";

    /**
     * 订单内打赏
     * orderId	int	订单id	是	订单id
     * money	int	打赏金额	是	单位为元
     * key	String	MD5加密这段	是	"abs" + orderId + money +"enjoy"
     */

    public String enjoy = ORDER + "order/enjoy/video/create";

    public String voiceenjoy = ORDER + "order/enjoy/voice/create";
    /**
     * 用户分页获取账单列表信息(v2.0.02版本新增 ：6首充赠送,7充值赠送)
     * page	String	当前多少页	否	默认第一页
     * rows	String	每页显示的条目数	否	默认10条。如果该值小于0或者大于30也会被系统处理为默认值
     */
    public String getDealDetails2 = URL + "isLogin/userInfo/page/getDealDetails.do";
    /**
     * 获取首页栏目及数据
     */

    public String getSellerMenuList = MATCHURL + "menuList/getSellerNewMenuList.do";
    /**
     * IM聊天扣费
     * sellerId	int	卖家id	是
     buyerId	int	买家id	是
     key	String	md5校验值	是	"imorder" + sellerId + buyerId + "imorder"
     */
    public String createim = ORDER + "order/im/create";
    /**
     * 获取所有礼物列表
     */
    public String gift = ORDER + "order/gift/batchquery";
    /**
     * 首页banner
     */
    public String getAdverts = MATCHURL + "sysAward/getNewAdverts.do";

    /**
     * 获取分享数据集合
     */
    public String getShare = MATCHURL + "sysAward/isLogin/getShare.do";



    /**
     * 用户删除通知中消息记录
     */
    public String delMessageRecord = MATCHURL + "message/isLogin/messageInfo/delMessageRecord.do";

    /**
     * 上传--相册
     */
    public String uploadPhotoAlbum = MATCHURL + "user/isLogin/userInfo/uploadPhotoAlbum.do";


    /**
     * 上传--个人介绍
     */
    public String updatePresentation = URL + "isLogin/userInfo/updatePresentation.do";

    /**
     * 删除--相册照片
     */
    public String deletePhoto = URL + "isLogin/userInfo/deletePhoto.do";

    /**
     * 新增的IM聊天打赏接口
     *
     *
     */
    public String enjoyIM = ORDER + "order/enjoy/im/create";


    /**
     * 账单
     * userID	用户ID	String	是
     userType	用户类型	String	是	0:买家;1:卖家
     isIncome	收入/支出	int	是	0:收入;1:支出
     pageNo	页数	int	是
     pageSize	条数	int	是

     */
    public String getBillHistory = ORDER + "order/getBillHistory.do";



    /**
     * 获取相册照片
     */
    public String getPhotos = URL + "isLogin/userInfo/v1.1/getPhotos.do";
//    public String getPhotos = URL + "isLogin/userInfo/getPhotos.do";
    /**
     * 获取最新公告及数量
     */
    public String getNoticeNew = MATCHURL + "message/isLogin/messageInfo/getNoticeNew.do";

    /**
     * 删除--相册照片
     */
    public String getWelfareList = MATCHURL + "getWelfareList.do";

    /**
     * 投诉
     * buyerId	买家id	int	是
     sellerId	卖家id	int	是
     isSellerInitiative	是否卖家主动投诉	int	是	如果是，那么值是1，否则是0，取值只能是0或1
     reason	投诉原因	String	是	传递的是订单投诉选项的id或者是自定义输入的数据
     orderId	关联的订单id	int	是
     key	MD5	String	是	"" + buyerId + sellerId + isSellerInitiative + reason + orderId + "absfuckorder"
     */

    public String submitOption = ORDER + "order/orderfuck/create230.do";
    /**
     * 包头			是	包头参见：2.2.1
     file	File	截图的图片	是
     buyerId	int	买家id	是
     sellerId	int	卖家id	是
     isSellerInitiative	int	是否卖家主动投诉	是	如果是，那么值是1，否则是0
     reason	String	投诉原因	是
     orderId	int	关联的订单id	是
     key	String		是	buyerId + sellerId + isSellerInitiative + reason + orderId + "absfuckorder"
     */
//    public String create230 = ORDER + "order/orderfuck/create230.do";
    public String create230 = ORDER + "order/complain/create";
    /**
     * 提交昵称、简介审核--
     */
    public String updateNickNamePresentation =  URL + "isLogin/userInfo/updateNickNamePresentation.do";


    /**
     * 获取余额
     * *
     */
    public String getUserMoney = URL + "isLogin/userInfo/getUserMoney.do";

    /**
     * 通讯录——好友请求
     */
    public String friendRequest = MATCHURL + "friend/isLogin/apply/list.do";

    /**
     * 通讯录——删除item
     */
    public String mailDeleteItem = MATCHURL + "friend/isLogin/deleteFriend.do";

    /**
     * 通讯录——修改备注名
     */
    public String mailUpdateName = MATCHURL + "friend/isLogin/updateRemark.do";

    /**
     * 通讯录——删除好友请求
     */
    public String mailDeleteFriendReq = MATCHURL + "friend/isLogin/apply/del.do";

    /**
     * 通讯录——通过好友请求
     */
    public String mailPassFriendReq = MATCHURL + "friend/isLogin/apply/pass.do";

    /**
     * 通讯录——清除请求
     */
    public String mailClearReq = MATCHURL + "friend/isLogin/apply/clear.do";

    /**
     * Profile——添加好友
     */
    public String addFriendReq = MATCHURL + "friend/isLogin/apply/add.do";

    /**
     * 新的好友请求——删除好友
     */
    public String deleteFriendReq = MATCHURL + "friend/isLogin/apply/del.do";

    /**
     * 对方同意好友请求后，根据targetId,获取对象
     */
    public String getFriendBeanById = MATCHURL + "friend/isLogin/getFriendInfo.do";


    /**
     * 首页用户数据
     */
    public String getmatchSeller = NEW_MATCHURL+"goldmaster/newpeople/batchquery";

    /**
     * 图像验证码
     */
    public String getImageCode = BaseURL+"match/captcha";
    /**
     * 职业列表
     */
    public String getWorks=MATCHURL+"user/isLogin/userInfo/customjob/batchquery";
    /**
     * 爱好列表
     */
    public String getHobby=MATCHURL+ "user/isLogin/userInfo/hobby/batchquery";
    /**
     * 主播界面
     */
    public String getSellerNewMenuList=MATCHURL+ "menuList/getSellerNewMenuList.do";

    /**
     * 支付回调
     */
    public String notify_url = MATCHURL + "user/pay/callback/alipay.do";
    /**
     * 匹配推荐
     */
    public String getCustomerQuene=NEW_MATCHURL+ "customerQuene242/getCustomerQuene";

    /**
     * 小额打赏
     */
    public String getsmallgift=ORDER+ "order/gift/smallgift/query";


    /**
     * 主播收入H5页面
     */
    public static final String seller_bill_income_usl = "http://h5.seecolove.com/pro/sincome";


    /**
     * 用户消费H5页面
     */
    public static final String buyer_bill_outcome_usl = "http://h5.seecolove.com/pro/boutcome";

    public static final String h5_url_contact_us = "http://h5.seecolove.com/static/contact";

    public static final String h5_url_give = "http://h5.seecolove.com/static/give";

    public static final String h5_url_gifts = "http://h5.seecolove.com/pro/packages";

    public static final String h5_url_task = "http://h5.seecolove.com/task?userId=%s&appType=android&userType=%s";

    public static final String h5_url_share = "http://h5.seecolove.com/stc/share";

    public static final String h5_url_share_rules = "http://h5.seecolove.com/static/srules";
    public static final String h5_url_wd_rules = "http://h5.seecolove.com/static/wdrules";
    public static final String h5_url_qrwx_rules = "http://h5.seecolove.com/static/wxqr";
    public static final String h5_url_qrali_rules = "http://h5.seecolove.com/static/aliqr";


    /**
     * Profile——关注
     */
    public String addFollow = MATCHURL + "follow/addFollow.do";
    /**
     * Profile——是否关注
     */
    public String getFollowFlag = MATCHURL + "follow/getFollowFlag.do";
    /**
     * Profile——delete关注
     */
    public String delFollow = MATCHURL + "follow/delFollow.do";
    /**
     * Profile——list关注
     */
    public String getFollowlist = MATCHURL + "follow/getFollowlist.do";

    public static final String rcode_imagepath =  "http://23.91.98.54:8330/weixin/rcode.png";


    public  String updateAppVersion = URL +  "userLogin/extend/updateVersion.do";

    public  String Become_Anchor =  NEW_MATCHURL+"audit/addAudit";

    public  String getNewMenuList =  MATCHURL+"menuList/getNewMenuList.do";


    /**
     * 上传--私照
     */
    public String uploadUserPhoto = MATCHURL + "isLogin/privatePhotos/uploadUserPhoto.do";
    /**
     * 获取私照
     */
    public String getOtherPhoto = MATCHURL + "isLogin/privatePhotos/getPrivatePhotos.do";
    /**
     * 获取私照
     */
    public String getUserPhoto = MATCHURL + "isLogin/privatePhotos/getPhotos.do";
    /**
     * 删除--私照片
     */
    public String deleteprivatePhotos = MATCHURL + "isLogin/privatePhotos/deletePhoto.do";


    /**
     * 新增的image扣费接口
     */
    public String enjoyImage = ORDER + "order/photo/create";

    public String GetMuenTable=MATCHURL+"menuList/getMenuList.do";//传userId

    public String addRemark=MATCHURL+ "remark/isLogin/addRemark.do";

    //获取开通微信售卖礼物数
    public String getGiftReceiveCount= MATCHURL + "weixnno/mall/getGiftReceiveCount.do";
    //提交微信号码
    public String submitWeixinNo=MATCHURL +"weixnno/mall/submitWeixinNo.do";
    //兑换微信号
    public String SellerWX=ORDER+"order/enjoy/wexinNo/create";

    //兑换微信号
    public String GIFT_setDisplay=NEW_MATCHURL+"editGiftShowFlag";//userId  flag:0 设置  1取消
}