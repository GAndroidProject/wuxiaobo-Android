package xiaoe.com.common.entitys;

import java.util.UUID;


public class SearchHistory {

    private String mId;
    private String mContent;
    private String mCreate;

    public SearchHistory(String historyContent, String create) {
        this(UUID.randomUUID().toString(), historyContent, create);
    }

    public SearchHistory(String id, String historyContent, String create) {
        this.mId = id;
        this.mContent = historyContent;
        this.mCreate = create;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public String getmId() {
        return mId;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmCreate(String mCreate) {
        this.mCreate = mCreate;
    }

    public String getmCreate() {
        return mCreate;
    }

    @Override
    public String toString() {
        return "historyContent --- " + this.mContent + " create --- " + this.mCreate;
    }
}
