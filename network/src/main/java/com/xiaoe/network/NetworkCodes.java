package com.xiaoe.network;

public class NetworkCodes {
    public static final int CODE_SUCCEED = 0;
    public static final int CODE_FAILED = -1;
    public static final int CODE_UNKNOWN = 1000;// 未知错误
    public static final int CODE_DB_ERROR_CODE = 1001; // 数据库错误
    public static final int CODE_DB_NOT_FIND = 1002;// 数据库查不到
    public static final int CODE_PARAMS_ERROR =1003;// 参数错误

    public static final int CODE_SEARCH_ERROR = 3000; //搜索出错（店铺搜索）
    public static final int CODE_GOODS_GROUPS_NOT_FIND = 3001; //商品分组不存在
    public static final int CODE_GOODS_GROUPS_DELETE = 3002; // 商品分组已被删除
    public static final int CODE_GOODS_NOT_FIND = 3003;// 商品不存在
    public static final int CODE_GOODS_DELETE = 3004;// 商品已被删除
    public static final int CODE_TINY_PAGER_NO_FIND = 3005;// 微页面不存在
    public static final int CODE_TINY_PAGER_DELETE = 3006;// 微页面已被删除
    public static final int CODE_RESOURCE_NOT_BUY = 3007;// 资源还没有购买
    public static final int CODE_GOODS_ORDER_LIST_FAILED = 3008;// 获取商品订购量失败
    public static final int CODE_RESOURCE_INFO_FAILED = 3009;// 获取指定资源信息失败

    public static final int CODE_PARAMS_EXCEPTION = 3020;// 参数异常
    public static final int CODE_SEND_COMMENT_FAILED = 3021;// 添加评论失败
    public static final int CODE_DELETE_COMMENT_FAILED = 3022;// 删除评论失败
    public static final int CODE_LIKE_FAILED = 3023; //点赞失败

    public static final int CODE_COLLECT_FAILED = 3031;// 收藏商品失败
    public static final int CODE_DELETE_COLLECT_FAILED = 3032;// 删除收藏失败
    public static final int CODE_COLLECT_LIST_FAILED = 3033;// 获取收藏列表失败

    public static final int CODE_SHOP_NOT_FIND = 3404;// 店铺不存在

    public static final int CODE_DATA_FAILED = 3500;// 拉取数据失败

    public static final int CODE_LOGIN_FAIL = -1; // 登录失败
    public static final int CODE_LOGIN_PASSWORD_ERROR = -2; // 密码错误
    public static final int CODE_HAD_REGISTER = 1105; // 手机号已注册
    public static final int CODE_NO_REGISTER = 1106; // 手机号未注册
    public static final int CODE_OBTAIN_ACCESS_TOKEN_FAIL = 1004; // 获取 access token 失败
    public static final int CODE_REGISTER_FAIL = -1; // 注册失败
    public static final int CODE_PHONE_CODE_ERROR = 2001; // 验证码错误
    public static final int CODE_LIMIT_USER = 3001; // 受限用户
    public static final int CODE_PHONE_HAD_BIND = 1101; // 手机号已被绑定
    public static final int CODE_WX_HAD_BIND = 1102; // 微信号已被绑定

    public static final int CODE_OBTAIN_LEARNING_FAIL = 3042; // 获取学习记录失败

    public static final int CODE_PERSON_PARAM_LOSE = 2501;
    public static final int CODE_PERSON_PARAM_UNUSEFUL = 2502;
    public static final int CODE_PERSON_PHONE_REPEAT = 2504;
    public static final int CODE_PERSON_NOT_FOUND = 2506;

    public static final int CODE_REQUEST_ERROR = -1;

    public static final int CODE_SUPER_VIP = 3011;

    public static final int CODE_NOT_LOAING = 2009;//未登陆

    public static final int CODE_OPEN_ID_ERROR = 4108;
    public static final int CODE_NO_MONEY = 4190;
}
