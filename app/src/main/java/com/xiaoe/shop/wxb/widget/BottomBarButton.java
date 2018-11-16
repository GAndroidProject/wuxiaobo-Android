package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;

public class BottomBarButton extends FrameLayout {
    private static final String TAG = "BottomBarButton";
    public static final int DRAWABLE_LEFT = 0;
    public static final int DRAWABLE_TOP = 1;
    public static final int DRAWABLE_RIGHT = 2;
    public static final int DRAWABLE_BOTTOM = 3;
    private Context mContext;
    private View rootView;
    private TextView buttonName;
    private ImageView buttonImage;
    private boolean isImageUrl = false;
    private String imageUrl = "";
    private String imageUrlChecked = "";
    private int imageResource = 0;
    private int imageResourceChecked = 0;
    private int buttonNameColor = 0;
    private int buttonNameCheckedColor = 0;
    private String buttonNameText = "";

    public BottomBarButton(@NonNull Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public BottomBarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.view_bottom_bar_button, this);
        buttonName = (TextView) rootView.findViewById(R.id.button_name);
        buttonImage = (ImageView) findViewById(R.id.button_image);
    }

    /**
     * 设置按钮文字
     * @param text
     */
    public void setButtonText(String text){
        buttonNameText = text;
    }
    public void setButtonTextSize(int dp) {
        buttonName.setTextSize(dp);
    }
    public void setButtonNameColor(int nameColor, int nameCheckedColor){
        buttonNameColor = nameColor;
        buttonNameCheckedColor = nameCheckedColor;
    }
    public void setButtonImageURL(String url, String checkedUrl){
        isImageUrl = true;
        imageUrl = url;
        imageUrlChecked = checkedUrl;
    }
    public void setButtonImageResource(int resId, int checkedResId){
        isImageUrl = false;
        imageResource = resId;
        imageResourceChecked = checkedResId;
    }


    /**
     * 设置按钮状态
     * @param checked
     */
    public void setButtonChecked(boolean checked){
        if(checked){
            buttonName.setTextColor(buttonNameCheckedColor);
            if(isImageUrl){
                buttonImage.setImageURI(Uri.parse(imageUrlChecked));
            }else{
                buttonImage.setImageResource(imageResourceChecked);
            }
        }else{
            buttonName.setTextColor(buttonNameColor);
            if(isImageUrl){
                buttonImage.setImageURI(Uri.parse(imageUrl));
            }else{
                buttonImage.setImageResource(imageResource);
            }
        }
        buttonName.setText(buttonNameText);
        setEnabled(!checked);
    }

}
