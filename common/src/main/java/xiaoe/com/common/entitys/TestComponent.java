package xiaoe.com.common.entitys;

import java.util.List;

public class TestComponent {

    /**
     * is_homepage : true
     * page_title : iOS app微页面
     * micro_page_id : 6227
     * components : [{}]
     */

    private boolean is_homepage;
    private String page_title;
    private int micro_page_id;
    private List<ComponentsBean> components;

    public boolean isIs_homepage() {
        return is_homepage;
    }

    public void setIs_homepage(boolean is_homepage) {
        this.is_homepage = is_homepage;
    }

    public String getPage_title() {
        return page_title;
    }

    public void setPage_title(String page_title) {
        this.page_title = page_title;
    }

    public int getMicro_page_id() {
        return micro_page_id;
    }

    public void setMicro_page_id(int micro_page_id) {
        this.micro_page_id = micro_page_id;
    }

    public List<ComponentsBean> getComponents() {
        return components;
    }

    public void setComponents(List<ComponentsBean> components) {
        this.components = components;
    }

    public static class ComponentsBean {
        /**
         * type : search_bar
         * type_title : 课程搜索
         * help_href :
         * version : 1
         * max_list_count : 0
         * is_list : false
         * default_rule_num : 10
         * list : []
         * origin_type : image_ad
         * title : 频道
         * show_title : 1
         * check_all : true
         * list_style : 0
         * from : 0
         * tag_num : 6
         * tag_id : 2003
         */

        private String type;
        private String type_title;
        private String help_href;
        private int version;
        private int max_list_count;
        private boolean is_list;
        private int default_rule_num;
        private String origin_type;
        private String title;
        private int show_title;
        private boolean check_all;
        private int list_style;
        private int from;
        private int tag_num;
        private String tag_id;
        private List<?> list;

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

        public int getMax_list_count() {
            return max_list_count;
        }

        public void setMax_list_count(int max_list_count) {
            this.max_list_count = max_list_count;
        }

        public boolean isIs_list() {
            return is_list;
        }

        public void setIs_list(boolean is_list) {
            this.is_list = is_list;
        }

        public int getDefault_rule_num() {
            return default_rule_num;
        }

        public void setDefault_rule_num(int default_rule_num) {
            this.default_rule_num = default_rule_num;
        }

        public String getOrigin_type() {
            return origin_type;
        }

        public void setOrigin_type(String origin_type) {
            this.origin_type = origin_type;
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

        public int getTag_num() {
            return tag_num;
        }

        public void setTag_num(int tag_num) {
            this.tag_num = tag_num;
        }

        public String getTag_id() {
            return tag_id;
        }

        public void setTag_id(String tag_id) {
            this.tag_id = tag_id;
        }

        public List<?> getList() {
            return list;
        }

        public void setList(List<?> list) {
            this.list = list;
        }
    }
}
