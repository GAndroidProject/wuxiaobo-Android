package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.interfaces.OnClickSendCommentListener;

/**
 * Created by Administrator on 2017/8/4.
 */

public class CommentView extends FrameLayout implements View.OnClickListener, View.OnLayoutChangeListener, View.OnTouchListener {
    private static final String TAG = "CommentView";
    public final static int TYPE_REPLY = 2100;
    public final static int TYPE_LIKE = 2200;
    public final static int TYPE_DELETE = 2300;
    private Context mContext;
    private EditText editComment;
    private TextView sendComment;
    private OnClickSendCommentListener sendListener;
    private String srcHint;
    private boolean isReply = false;
    private ForegroundColorSpan colorSpan;
    private SpannableStringBuilder stringBuilder;
    // 当前登录用户是否为游客
    private boolean isTourists;

    public CommentView(Context context) {
        this(context, null);
    }

    public CommentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        mContext = context;
        colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.secondary_title_color));
        stringBuilder = new SpannableStringBuilder();

        View sendCommentView = LayoutInflater.from(mContext).inflate(R.layout.layout_send_comment,null,false);

        sendComment = (TextView) sendCommentView.findViewById(R.id.id_icon_comment_send);
        sendComment.setOnClickListener(this);
        editComment = (EditText) sendCommentView.findViewById(R.id.id_edit_comment);
        editComment.clearFocus();
        editComment.setFocusable(false);
        editComment.setFocusableInTouchMode(false);
        editComment.setOnTouchListener(this);
        editComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editComment.getLineCount() == 5){
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editComment.getLayoutParams();
                    lp.height = editComment.getHeight();
                    editComment.setLayoutParams(lp);
                }else if(editComment.getLineCount() < 5){
                    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editComment.getLayoutParams();
                    lp.height = RelativeLayout.LayoutParams.WRAP_CONTENT;
                    editComment.setLayoutParams(lp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editComment.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                String commentContent = editComment.getText().toString();
                if(isReply && (keyCode == KeyEvent.KEYCODE_DEL) && srcHint.length() == commentContent.length()){
                    return true;
                }else{
                    return false;
                }

            }
        });
        addView(sendCommentView);
    }
    public void setEditHint(String text){
        editComment.setText("");
        editComment.setHint(text);
    }
    public void setSrcCommentHint(String hint){
        isReply = true;
        srcHint = hint+"：";
        stringBuilder.clear();
        stringBuilder.append(srcHint);
        stringBuilder.setSpan(colorSpan,0,srcHint.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editComment.setText(stringBuilder);
        editComment.setSelection(srcHint.length());
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_icon_comment_send:
                clickBtnSend();
                break;
            default:
                break;
        }
    }

    /**
     * 发送评论
     */
    private void clickBtnSend() {
        editComment.clearFocus();
        editComment.setFocusable(false);
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null){
            imm.hideSoftInputFromWindow(editComment.getWindowToken(), 0);
        }

        String commentContent = editComment.getText().toString().trim();
        editComment.setHint("我来说两句");
        editComment.setText("");
        if(isReply){
            commentContent = commentContent.substring(srcHint.length());
        }
        if(sendListener != null){
            sendListener.onSend(commentContent);
        }
        isReply = false;
        srcHint = "";
        editComment.setText("");
    }


    // 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
    public boolean isHideInput(View v, MotionEvent ev) {
        if (v != null && (v instanceof EditText)) {
            int[] l = {0, 0};
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (ev.getX() > left && ev.getX() < right && ev.getY() > top
                    && ev.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    // 隐藏软键盘
    public void hideSoftInput(View view) {
        InputMethodManager manager = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (manager != null) {
            editComment.clearFocus();
            editComment.setFocusable(false);
            editComment.setFocusableInTouchMode(false);
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    public void setActivityRootView(View activityRootView) {
        if(activityRootView != null){
            activityRootView.addOnLayoutChangeListener(this);
        }
    }
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了100px屏幕高，就认为软键盘弹起
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > 100)){
            sendComment.setVisibility(VISIBLE);
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editComment.getLayoutParams();
            lp.width = LayoutParams.MATCH_PARENT;
            editComment.setLayoutParams(lp);
        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > 100)){
            String tempContent = editComment.getText().toString();
            if(isReply){
                tempContent = tempContent.substring(srcHint.length());
            }
            if(TextUtils.isEmpty(tempContent)){
                sendComment.setVisibility(GONE);
                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) editComment.getLayoutParams();
                lp.width = LayoutParams.MATCH_PARENT;
                editComment.setLayoutParams(lp);

                isReply = false;
                srcHint = "";
                editComment.setText("");
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isTourists) {
            if(v.getId() == R.id.id_edit_comment){
                editComment.requestFocus();
                editComment.setFocusable(true);
                editComment.setFocusableInTouchMode(true);
            }
        } else {
            showTouristDialog();
        }

        return false;
    }

    public void setSendListener(OnClickSendCommentListener sendListener) {
        this.sendListener = sendListener;
    }

    public boolean isReply(){
        return isReply;
    }

    public void setReply(boolean reply) {
        isReply = reply;
    }

    public void setIsTourists(boolean isTourists) {
        this.isTourists = isTourists;
    }

    private void showTouristDialog() {
        final TouristDialog touristDialog = new TouristDialog(mContext);
        touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touristDialog.dismissDialog();
            }
        });
        touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpDetail.jumpLogin(mContext, true);
            }
        });
        touristDialog.showDialog();
    }
}
