package com.xiaoe.common.entitys;

/**
 * @author flynnWang
 * @date 2018/12/20
 * <p>
 * 描述：历史消息页面的跳转信息实体
 */
public class ContentPageBean {

    /**
     * channel_id : 85442
     * type : 2
     * resource_type : 1
     * resource_id : i_5c09064de7980_fdS1uUb3
     * product_id :
     * app_id : appe0MEs6qX8480
     */

    private int channel_id;
    /**
     * 页面类型
     */
    private String type;
    /**
     * 资源类型
     */
    private String resource_type;
    /**
     * 资源id
     */
    private String resource_id;
    /**
     * 专栏id
     */
    private String product_id;
    /**
     * 店铺id
     */
    private String app_id;

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getResource_id() {
        return resource_id;
    }

    public void setResource_id(String resource_id) {
        this.resource_id = resource_id;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public String getApp_id() {
        return app_id;
    }

    public void setApp_id(String app_id) {
        this.app_id = app_id;
    }

    @Override
    public String toString() {
        return "ContentPageBean{" +
                "channel_id=" + channel_id +
                ", type='" + type + '\'' +
                ", resource_type='" + resource_type + '\'' +
                ", resource_id='" + resource_id + '\'' +
                ", product_id='" + product_id + '\'' +
                ", app_id='" + app_id + '\'' +
                '}';
    }
}
