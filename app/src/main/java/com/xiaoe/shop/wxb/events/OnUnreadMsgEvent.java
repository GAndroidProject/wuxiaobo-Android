package com.xiaoe.shop.wxb.events;

/**
 * @author flynnWang
 * @date 2018/11/24
 * <p>
 * 描述：
 */
public class OnUnreadMsgEvent {

    private int messageType;
    private int unreadCount;
    private MessageOrigin messageOrigin;

    /**
     * 消息来源
     */
    public enum MessageOrigin {
        /**
         * 通知栏（推送）
         */
        NOTICE,
        /**
         * 网络请求
         */
        HTTP
    }

    public OnUnreadMsgEvent(int messageType, int unreadCount, MessageOrigin messageOrigin) {
        this.messageType = messageType;
        this.unreadCount = unreadCount;
        this.messageOrigin = messageOrigin;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public MessageOrigin getMessageOrigin() {
        return messageOrigin;
    }

    public void setMessageOrigin(MessageOrigin messageOrigin) {
        this.messageOrigin = messageOrigin;
    }

    @Override
    public String toString() {
        return "OnUnreadMsgEvent{" +
                "messageType=" + messageType +
                ", unreadCount=" + unreadCount +
                ", messageOrigin=" + messageOrigin +
                '}';
    }
}
