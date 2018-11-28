package com.xiaoe.shop.wxb.common.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.xiaoe.shop.wxb.business.main.ui.MainActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.jpush.entity.JgPushReceiverEntity;
import com.xiaoe.shop.wxb.common.web.BrowserActivity;
import com.xiaoe.shop.wxb.events.OnUnreadMsgEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

import static com.xiaoe.shop.wxb.common.jpush.Logger.d;
import static com.xiaoe.shop.wxb.common.jpush.Logger.w;

/**
 * @author Administrator
 * <p>
 * 描述：自定义接收器
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JgPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JIGUANG-Example";
    private static JgPushReceiverEntity jgPushReceiverEntity;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            d(TAG, "[JgPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                d(TAG, "[JgPushReceiver] 接收Registration Id : " + regId);
                // send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                d(TAG, "[JgPushReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                processCustomMessage(context, bundle);

            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                d(TAG, "[JgPushReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                d(TAG, "[JgPushReceiver] 接收到推送下来的通知的ID: " + notifactionId);

                EventBus.getDefault().post(new OnUnreadMsgEvent(0, 1));
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                d(TAG, "[JgPushReceiver] 用户点击打开了通知");
                if (jgPushReceiverEntity == null) {
                    return;
                }
                jumpDetail(context);
            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                d(TAG, "[JgPushReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                // 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                w(TAG, "[JgPushReceiver]" + intent.getAction() + " connected state change to " + connected);
            } else {
                d(TAG, "[JgPushReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jumpDetail(Context context) {
        /*
         * 跳转类型：0-无跳转，1-图文，2-音频，3-视频，5-外部链接，6-专栏，7-直播
         */
        switch (jgPushReceiverEntity.getAction()) {
            case 0:
                break;
            case 1:
                JumpDetail.jumpImageText(context, jgPushReceiverEntity.getAction_params().getResource_id(), "", "");
                break;
            case 2:
                JumpDetail.jumpAudio(context, jgPushReceiverEntity.getAction_params().getResource_id(), 0);
                break;
            case 3:
                JumpDetail.jumpVideo(context, jgPushReceiverEntity.getAction_params().getResource_id(), "", false, "");
                break;
            case 4:
                break;
            case 5:
                BrowserActivity.openUrl(context, jgPushReceiverEntity.getAction_params().getWebsite_url(), "外部链接");
                break;
            case 6:
//                if ("8".equals(jgPushReceiverEntity.getAction_params().getResource_type())) {
//                    JumpDetail.jumpColumn(context, jgPushReceiverEntity.getAction_params().getResource_id(), "", true);
//                } else {
//                    JumpDetail.jumpColumn(context, jgPushReceiverEntity.getAction_params().getResource_id(), "", false);
//                }
                int resourceType = Integer.parseInt(jgPushReceiverEntity.getAction_params().getResource_type());
                JumpDetail.jumpColumn(context, jgPushReceiverEntity.getAction_params().getResource_id(), "", resourceType);
                break;
            case 7:
                break;
            default:
//                // 打开自定义的Activity
//                Intent i = new Intent(context, TestActivity.class);
//                i.putExtras(bundle);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                context.startActivity(i);
                break;
        }
    }

    /**
     * 打印所有的 intent extra 数据
     *
     * @param bundle
     * @return
     */
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            switch (key) {
                case JPushInterface.EXTRA_NOTIFICATION_ID:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getInt(key));
                    break;
                case JPushInterface.EXTRA_CONNECTION_CHANGE:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.getBoolean(key));
                    break;
                case JPushInterface.EXTRA_EXTRA:
                    if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                        Logger.i(TAG, "This message has no Extra data");
                        continue;
                    }
                    try {
                        JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                        Iterator<String> it = json.keys();

                        while (it.hasNext()) {
                            String myKey = it.next();
                            sb.append("\nkey:").append(key).append(", value: [").append(myKey).append(" - ").append(json.optString(myKey)).append("]");
                            if (jgPushReceiverEntity == null) {
                                jgPushReceiverEntity = new JgPushReceiverEntity();
                            }
                            if ("action".equals(myKey)) {
                                jgPushReceiverEntity.setAction(Integer.parseInt(json.optString(myKey)));
                            }
                            if ("action_params".equals(myKey)) {
                                JgPushReceiverEntity.ActionParams actionParams = JSON.parseObject(json.optString(myKey), JgPushReceiverEntity.ActionParams.class);
                                jgPushReceiverEntity.setAction_params(actionParams);
                            }
                        }
//                        Log.e(TAG, "printBundle: " + jgPushReceiverEntity.toString());
                    } catch (JSONException e) {
                        Logger.e(TAG, "Get message extra JSON error!");
                    }
                    break;
                default:
                    sb.append("\nkey:").append(key).append(", value:").append(bundle.get(key));
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * send msg to MainActivity
     *
     * @param context
     * @param bundle
     */
    private void processCustomMessage(Context context, Bundle bundle) {
        if (MainActivity.isForeground) {
            String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
            msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
            if (!ExampleUtil.isEmpty(extras)) {
                try {
                    JSONObject extraJson = new JSONObject(extras);
                    if (extraJson.length() > 0) {
                        msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
                    }
                } catch (JSONException ignored) {
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
        }
    }
}
