package xiaoe.com.common.entitys;

public class CouponInfo {

    /**
     * id : cou_5bb2ede4d258b-xITkSE
     * title : 用户分组
     * type : 0
     * valid_at : 2018-10-01 00:00:00
     * invalid_at : 2018-11-01 00:00:00
     * price : 2200
     * require_price : 0
     * bind_res_count : 1
     * cu_id : cu_5bc49887cecc5_f3sqcHSK
     */

    private String id;
    private String title;
    private int type;
    private String valid_at;
    private String invalid_at;
    private int price;
    private int require_price;
    private int bind_res_count;
    private String cu_id;
    private boolean valid = false;
    private boolean select = false;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValid_at() {
        return valid_at;
    }

    public void setValid_at(String valid_at) {
        this.valid_at = valid_at;
    }

    public String getInvalid_at() {
        return invalid_at;
    }

    public void setInvalid_at(String invalid_at) {
        this.invalid_at = invalid_at;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getRequire_price() {
        return require_price;
    }

    public void setRequire_price(int require_price) {
        this.require_price = require_price;
    }

    public int getBind_res_count() {
        return bind_res_count;
    }

    public void setBind_res_count(int bind_res_count) {
        this.bind_res_count = bind_res_count;
    }

    public String getCu_id() {
        return cu_id;
    }

    public void setCu_id(String cu_id) {
        this.cu_id = cu_id;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }
}
