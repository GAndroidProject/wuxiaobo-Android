package xiaoe.com.network.downloadUtil;

public class RelationTableInfo {
    private String appId;
    private String id;
    private String resourceId;
    private String path;//json格式{"topic":"","column":""}//如果未空，说明是单品

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
