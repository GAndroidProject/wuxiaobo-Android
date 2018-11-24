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

    public OnUnreadMsgEvent(int messageType, int unreadCount) {
        this.messageType = messageType;
        this.unreadCount = unreadCount;
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

    @Override
    public String toString() {
        return "OnUnreadMsgEvent{" +
                "messageType=" + messageType +
                ", unreadCount=" + unreadCount +
                '}';
    }
}
