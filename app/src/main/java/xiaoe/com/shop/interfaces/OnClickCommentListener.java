package xiaoe.com.shop.interfaces;

import xiaoe.com.common.entitys.CommentEntity;

public interface OnClickCommentListener {
    void onClickComment(CommentEntity commentEntity, int type, int position);
}
