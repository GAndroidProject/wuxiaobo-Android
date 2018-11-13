package xiaoe.com.shop.business.upgrade;

/**
 * Created by Haley.Yang on 2018/5/31.
 * <p>
 * 描述：
 */
public class UpgradeResult extends BaseResult {

    public Data data;

    public UpgradeResult(){

    }
    public UpgradeResult(int code, String msg) {
        super(code, msg);
    }

    public static class Data{
        public String[] msg;
        public String download_url;//新版本下载地址
        public int update_mode;// 0:弹窗提示更新 1:强制更新 2.红点提示更新
        public int is_update;//0:不用更新 1:需要更新
        public String version;//版本名称
    }
}
