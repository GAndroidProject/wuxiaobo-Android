package com.xiaoe.common.entitys;

/**
 * Created by Administrator on 2017/9/4.
 */

public class DeviceInfo {
    //对应build.gradle中的versionName
    private String versionName;
    //获取设备的唯一标识，deviceId
    private String deviceId;
    //获取手机品牌
    private String phoneBrand;
    //获取手机型号
    private String phoneModel;
    //获取手机Android API等级（22、23 ...）
    private String buildLevel;
    //获取手机Android 版本（4.4、5.0、5.1 ...）
    private String buildVersion;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getPhoneBrand() {
        return phoneBrand;
    }

    public void setPhoneBrand(String phoneBrand) {
        this.phoneBrand = phoneBrand;
    }

    public String getBuildLevel() {
        return buildLevel;
    }

    public void setBuildLevel(String buildLevel) {
        this.buildLevel = buildLevel;
    }

    public String getBuildVersion() {
        return buildVersion;
    }

    public void setBuildVersion(String buildVersion) {
        this.buildVersion = buildVersion;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }
}
