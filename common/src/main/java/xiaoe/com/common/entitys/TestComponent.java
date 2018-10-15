package xiaoe.com.common.entitys;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TestComponent {


    /**
     * code : 0
     * msg :
     * data : {"micro_page_id":"6202","is_homepage":true,"page_title":"额额额","components":[{...}, {...}]}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * micro_page_id : 6202
         * is_homepage : true
         * page_title : 额额额
         * components : [{...}, {...}]
         */

        @SerializedName("micro_page_id")
        private String micro_page_idX;
        @SerializedName("is_homepage")
        private boolean is_homepageX;
        @SerializedName("page_title")
        private String page_titleX;
        @SerializedName("components")
        private List<ComponentsBean> componentsX;

        public String getMicro_page_idX() {
            return micro_page_idX;
        }

        public void setMicro_page_idX(String micro_page_idX) {
            this.micro_page_idX = micro_page_idX;
        }

        public boolean isIs_homepageX() {
            return is_homepageX;
        }

        public void setIs_homepageX(boolean is_homepageX) {
            this.is_homepageX = is_homepageX;
        }

        public String getPage_titleX() {
            return page_titleX;
        }

        public void setPage_titleX(String page_titleX) {
            this.page_titleX = page_titleX;
        }

        public List<ComponentsBean> getComponentsX() {
            return componentsX;
        }

        public void setComponentsX(List<ComponentsBean> componentsX) {
            this.componentsX = componentsX;
        }

        public static class ComponentsBean {
            /**
             * type : punch_card
             * type_title : 打卡
             * help_href :
             * version : 1
             * id : 59186
             * max_list_count : 50
             * is_list : 1
             * default_rule_num :
             * tagid :
             * more_url : http://wxdd198a901fa24220.h5.inside.xiaoe-tech.com/mp_more/eyJpZCI6IjE4ODkiLCJjaGFubmVsX2lkIjpudWxsfQ
             * list : [{...}, {...}]
             * title : 打卡
             * show_title : 1
             * check_all : true
             * list_style : 1
             * from : 1
             * tag_id : 1889
             * origin_type : punch_card
             * sub_list :
             * tag_num : 6
             */

            private String type;
            private String type_title;
            private String help_href;
            private int version;
            private int id;
            private int max_list_count;
            private int is_list;
            private String default_rule_num;
            private String tagid;
            private String more_url;
            private String title;
            private int show_title;
            private boolean check_all;
            private int list_style;
            private int from;
            private String tag_id;
            private String origin_type;
            private String sub_list;
            private int tag_num;
            private List<ListBean> list;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getType_title() {
                return type_title;
            }

            public void setType_title(String type_title) {
                this.type_title = type_title;
            }

            public String getHelp_href() {
                return help_href;
            }

            public void setHelp_href(String help_href) {
                this.help_href = help_href;
            }

            public int getVersion() {
                return version;
            }

            public void setVersion(int version) {
                this.version = version;
            }

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getMax_list_count() {
                return max_list_count;
            }

            public void setMax_list_count(int max_list_count) {
                this.max_list_count = max_list_count;
            }

            public int getIs_list() {
                return is_list;
            }

            public void setIs_list(int is_list) {
                this.is_list = is_list;
            }

            public String getDefault_rule_num() {
                return default_rule_num;
            }

            public void setDefault_rule_num(String default_rule_num) {
                this.default_rule_num = default_rule_num;
            }

            public String getTagid() {
                return tagid;
            }

            public void setTagid(String tagid) {
                this.tagid = tagid;
            }

            public String getMore_url() {
                return more_url;
            }

            public void setMore_url(String more_url) {
                this.more_url = more_url;
            }

            public String getTitle() {
                return title;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public int getShow_title() {
                return show_title;
            }

            public void setShow_title(int show_title) {
                this.show_title = show_title;
            }

            public boolean isCheck_all() {
                return check_all;
            }

            public void setCheck_all(boolean check_all) {
                this.check_all = check_all;
            }

            public int getList_style() {
                return list_style;
            }

            public void setList_style(int list_style) {
                this.list_style = list_style;
            }

            public int getFrom() {
                return from;
            }

            public void setFrom(int from) {
                this.from = from;
            }

            public String getTag_id() {
                return tag_id;
            }

            public void setTag_id(String tag_id) {
                this.tag_id = tag_id;
            }

            public String getOrigin_type() {
                return origin_type;
            }

            public void setOrigin_type(String origin_type) {
                this.origin_type = origin_type;
            }

            public String getSub_list() {
                return sub_list;
            }

            public void setSub_list(String sub_list) {
                this.sub_list = sub_list;
            }

            public int getTag_num() {
                return tag_num;
            }

            public void setTag_num(int tag_num) {
                this.tag_num = tag_num;
            }

            public List<ListBean> getList() {
                return list;
            }

            public void setList(List<ListBean> list) {
                this.list = list;
            }

            public static class ListBean {
                /**
                 * title : 创建付费打卡
                 * summary :
                 * img_url : https://wechatapppro-1252524126.cossh.myqcloud.com/apppcHqlTPT3482/image/e843e552f68c7b0d610f5f437e0893e8.png
                 * img_url_compressed_larger :
                 * alive_img_url :
                 * start_at : 2018-10-01
                 * stop_at : 2018-10-31
                 * period :
                 * product_name :
                 * view_count :
                 * comment_count :
                 * resource_count :
                 * feeds_count : 0
                 * finished_state : 0
                 * manual_stop_at :
                 * is_stop_sell : 0
                 * alive_type : 0
                 * show_price :
                 * marketing_tag : []
                 * purchase_count :
                 * resource_tag : []
                 * src_type : punch_card
                 * start_at_pc : 18-10-01 00:00:00
                 * mp_url : /page/home/punch_card_details/punch_card_details?id=ac_5bc00ab754955_Ew8THm5v
                 * jump_url : http://app.inside.xiaoe-tech.com/content_page/eyJ0eXBlIjoyLCJyZXNvdXJjZV90eXBlIjoxNiwicmVzb3VyY2VfaWQiOiJhY181YmMwMGFiNzU0OTU1X0V3OFRIbTV2IiwicHJvZHVjdF9pZCI6IiIsImFwcF9pZCI6ImFwcHBjSHFsVFBUMzQ4MiJ9
                 * is_navigation : false
                 * mp_support : 1
                 * people_count : 4
                 */

                private String title;
                private String summary;
                private String img_url;
                private String img_url_compressed_larger;
                private String alive_img_url;
                private String start_at;
                private String stop_at;
                private String period;
                private String product_name;
                private String view_count;
                private String comment_count;
                private String resource_count;
                private int feeds_count;
                private int finished_state;
                private String manual_stop_at;
                private int is_stop_sell;
                private int alive_type;
                private String show_price;
                private String purchase_count;
                private String src_type;
                private String start_at_pc;
                private String mp_url;
                private String jump_url;
                private boolean is_navigation;
                private int mp_support;
                private int people_count;
                private List<?> marketing_tag;
                private List<?> resource_tag;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getSummary() {
                    return summary;
                }

                public void setSummary(String summary) {
                    this.summary = summary;
                }

                public String getImg_url() {
                    return img_url;
                }

                public void setImg_url(String img_url) {
                    this.img_url = img_url;
                }

                public String getImg_url_compressed_larger() {
                    return img_url_compressed_larger;
                }

                public void setImg_url_compressed_larger(String img_url_compressed_larger) {
                    this.img_url_compressed_larger = img_url_compressed_larger;
                }

                public String getAlive_img_url() {
                    return alive_img_url;
                }

                public void setAlive_img_url(String alive_img_url) {
                    this.alive_img_url = alive_img_url;
                }

                public String getStart_at() {
                    return start_at;
                }

                public void setStart_at(String start_at) {
                    this.start_at = start_at;
                }

                public String getStop_at() {
                    return stop_at;
                }

                public void setStop_at(String stop_at) {
                    this.stop_at = stop_at;
                }

                public String getPeriod() {
                    return period;
                }

                public void setPeriod(String period) {
                    this.period = period;
                }

                public String getProduct_name() {
                    return product_name;
                }

                public void setProduct_name(String product_name) {
                    this.product_name = product_name;
                }

                public String getView_count() {
                    return view_count;
                }

                public void setView_count(String view_count) {
                    this.view_count = view_count;
                }

                public String getComment_count() {
                    return comment_count;
                }

                public void setComment_count(String comment_count) {
                    this.comment_count = comment_count;
                }

                public String getResource_count() {
                    return resource_count;
                }

                public void setResource_count(String resource_count) {
                    this.resource_count = resource_count;
                }

                public int getFeeds_count() {
                    return feeds_count;
                }

                public void setFeeds_count(int feeds_count) {
                    this.feeds_count = feeds_count;
                }

                public int getFinished_state() {
                    return finished_state;
                }

                public void setFinished_state(int finished_state) {
                    this.finished_state = finished_state;
                }

                public String getManual_stop_at() {
                    return manual_stop_at;
                }

                public void setManual_stop_at(String manual_stop_at) {
                    this.manual_stop_at = manual_stop_at;
                }

                public int getIs_stop_sell() {
                    return is_stop_sell;
                }

                public void setIs_stop_sell(int is_stop_sell) {
                    this.is_stop_sell = is_stop_sell;
                }

                public int getAlive_type() {
                    return alive_type;
                }

                public void setAlive_type(int alive_type) {
                    this.alive_type = alive_type;
                }

                public String getShow_price() {
                    return show_price;
                }

                public void setShow_price(String show_price) {
                    this.show_price = show_price;
                }

                public String getPurchase_count() {
                    return purchase_count;
                }

                public void setPurchase_count(String purchase_count) {
                    this.purchase_count = purchase_count;
                }

                public String getSrc_type() {
                    return src_type;
                }

                public void setSrc_type(String src_type) {
                    this.src_type = src_type;
                }

                public String getStart_at_pc() {
                    return start_at_pc;
                }

                public void setStart_at_pc(String start_at_pc) {
                    this.start_at_pc = start_at_pc;
                }

                public String getMp_url() {
                    return mp_url;
                }

                public void setMp_url(String mp_url) {
                    this.mp_url = mp_url;
                }

                public String getJump_url() {
                    return jump_url;
                }

                public void setJump_url(String jump_url) {
                    this.jump_url = jump_url;
                }

                public boolean isIs_navigation() {
                    return is_navigation;
                }

                public void setIs_navigation(boolean is_navigation) {
                    this.is_navigation = is_navigation;
                }

                public int getMp_support() {
                    return mp_support;
                }

                public void setMp_support(int mp_support) {
                    this.mp_support = mp_support;
                }

                public int getPeople_count() {
                    return people_count;
                }

                public void setPeople_count(int people_count) {
                    this.people_count = people_count;
                }

                public List<?> getMarketing_tag() {
                    return marketing_tag;
                }

                public void setMarketing_tag(List<?> marketing_tag) {
                    this.marketing_tag = marketing_tag;
                }

                public List<?> getResource_tag() {
                    return resource_tag;
                }

                public void setResource_tag(List<?> resource_tag) {
                    this.resource_tag = resource_tag;
                }
            }
        }
    }
}
