package com.xiaoe.common.entitys;

/**
 * 版本更新说明
 *
 * @author Administrator
 */
public class VersionLog {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "VersionLog{" +
                "content='" + content + '\'' +
                '}';
    }
}