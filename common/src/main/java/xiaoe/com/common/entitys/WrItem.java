package xiaoe.com.common.entitys;

public class WrItem {

    private String wrTitle; // 提现记录 item 标题
    private int wrMoney; // 提现记录 item 金额
    private String wrTime; // 提现记录 item 时间
    private String wrState; // 提现记录 item 状态

    public void setWrTitle(String wrTitle) {
        this.wrTitle = wrTitle;
    }

    public void setWrMoney(int wrMoney) {
        this.wrMoney = wrMoney;
    }

    public void setWrTime(String wrTime) {
        this.wrTime = wrTime;
    }

    public void setWrState(String wrState) {
        this.wrState = wrState;
    }

    public String getWrTitle() {

        return wrTitle;
    }

    public int getWrMoney() {
        return wrMoney;
    }

    public String getWrTime() {
        return wrTime;
    }

    public String getWrState() {
        return wrState;
    }
}
