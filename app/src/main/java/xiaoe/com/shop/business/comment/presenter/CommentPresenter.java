package xiaoe.com.shop.business.comment.presenter;

import android.text.TextUtils;

import xiaoe.com.common.app.Constants;
import xiaoe.com.common.entitys.CommentEntity;
import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.CommentDeleteRequest;
import xiaoe.com.network.requests.CommentLikeRequest;
import xiaoe.com.network.requests.CommentListRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SendCommentRequest;
import xiaoe.com.network.utils.ThreadPoolUtils;

public class CommentPresenter implements IBizCallback {
    private static final String TAG = "CommentPresenter";
    private INetworkResponse iNetworkResponse;

    public CommentPresenter(INetworkResponse iNetworkResponse) {
        this.iNetworkResponse = iNetworkResponse;
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        ThreadPoolUtils.runTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                iNetworkResponse.onMainThreadResponse(iRequest, success, entity);
            }
        });
    }

    /**
     * 获取评论列表
     * @param recordId
     * @param recordType 	1-图文 2-音频 3-视频，20-电子书
     * @param pageSize
     * @param lastCommentId
     * @param label last-最新,hot-热门，默认是last
     */
    public void requestCommentList(String recordId, int recordType, int pageSize, int lastCommentId, String label){
        CommentListRequest listRequest = new CommentListRequest(this);
        listRequest.addRequestParam("shop_id",Constants.getAppId());
        listRequest.addRequestParam("record_id",recordId);
        listRequest.addRequestParam("record_type",recordType);
        listRequest.addRequestParam("page_size",pageSize);
        listRequest.addRequestParam("last_comment_id",lastCommentId);
        listRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        if (TextUtils.isEmpty(label)) {
            label = "last";
        }
        listRequest.addRequestParam("label",label);
        listRequest.sendRequest();
    }

    /**
     * 发送评论
     * @param recordId
     * @param recordType
     * @param recordTitle
     * @param content
     * @param replyComment 被回复的信息
     */
    public void sendComment(String recordId, int recordType, String recordTitle, String content, CommentEntity replyComment){
        SendCommentRequest commentRequest = new SendCommentRequest(this);
        commentRequest.addRequestParam("shop_id",Constants.getAppId());
        commentRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        commentRequest.addRequestParam("record_id",recordId);
        commentRequest.addRequestParam("record_type",recordType);
        commentRequest.addRequestParam("record_title",recordTitle);
        if(replyComment != null){
            commentRequest.addRequestParam("src_comment_id",replyComment.getComment_id());
            commentRequest.addRequestParam("src_content",replyComment.getContent());
            commentRequest.addRequestParam("src_nick_name",replyComment.getUser_nickname());
            commentRequest.addRequestParam("src_user_id",replyComment.getUser_id());
        }
        commentRequest.addRequestParam("content",content);

        commentRequest.sendRequest();
    }

    /**
     * 评论点赞
     * @param recordId
     * @param recordType
     * @param commentId
     * @param srcUserId
     * @param commentContent
     * @param praised
     */
    public void likeComment(String recordId, int recordType, int commentId, String srcUserId, String commentContent, boolean praised){
        CommentLikeRequest commentLikeRequest = new CommentLikeRequest(this);
        commentLikeRequest.addRequestParam("shop_id",Constants.getAppId());
        commentLikeRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        commentLikeRequest.addRequestParam("record_id",recordId);
        commentLikeRequest.addRequestParam("record_type",recordType);
        commentLikeRequest.addRequestParam("comment_id",commentId);
        commentLikeRequest.addRequestParam("src_user_id",srcUserId);
        commentLikeRequest.addRequestParam("src_comment_content",commentContent);
        commentLikeRequest.addRequestParam("is_praised",praised);
        commentLikeRequest.sendRequest();
    }
    public void deleteComment(String recordId, int recordType, int commentId){
        CommentDeleteRequest commentDeleteRequest = new CommentDeleteRequest(this);
        commentDeleteRequest.addRequestParam("shop_id",Constants.getAppId());
        commentDeleteRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        commentDeleteRequest.addRequestParam("comment_id",commentId);
        commentDeleteRequest.addRequestParam("record_id",recordId);
        commentDeleteRequest.addRequestParam("record_type",recordType);

        commentDeleteRequest.sendRequest();
    }
}
