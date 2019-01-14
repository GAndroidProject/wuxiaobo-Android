package com.xiaoe.common.entitys;

/**
 * @author: zak
 * @date: 2019/1/12
 */
public class LabelItemEntity {

    private String labelContent;
    private String labelBackground;
    private String labelFontColor;

    public void setLabelContent(String labelContent) {
        this.labelContent = labelContent;
    }

    public void setLabelBackground(String labelBackground) {
        this.labelBackground = labelBackground;
    }

    public void setLabelFontColor(String labelFontColor) {
        this.labelFontColor = labelFontColor;
    }

    public String getLabelContent() {

        return labelContent;
    }

    public String getLabelBackground() {
        return labelBackground;
    }

    public String getLabelFontColor() {
        return labelFontColor;
    }
}
