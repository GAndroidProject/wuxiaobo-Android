package xiaoe.com.common.entitys;

// 是否需要切换到奖学金页面事件
public class ChangeToScholarshipEvent {

    public boolean needChange;

    public ChangeToScholarshipEvent(boolean needChange) {
        this.needChange = needChange;
    }
}
