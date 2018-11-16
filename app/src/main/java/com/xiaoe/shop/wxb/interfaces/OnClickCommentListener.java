package com.xiaoe.shop.wxb.interfaces;

import com.xiaoe.common.entitys.CommentEntity;

public interface OnClickCommentListener {
    void onClickComment(CommentEntity commentEntity, int type, int position);
}
