package xiaoe.com.shop.business.comment.presenter;

import android.text.TextUtils;

import xiaoe.com.network.network_interface.IBizCallback;
import xiaoe.com.network.network_interface.INetworkResponse;
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
        listRequest.addRequestParam("shop_id","apppcHqlTPT3482");
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
     * @param srcCommentId 被回复的id
     */
    public void sendComment(String recordId, int recordType, String recordTitle, String content, int srcCommentId){
        SendCommentRequest commentRequest = new SendCommentRequest(this);
        commentRequest.addRequestParam("shop_id","apppcHqlTPT3482");
        commentRequest.addRequestParam("user_id","u_591d643ce9c2c_fAbTq44T");
        commentRequest.addRequestParam("record_id",recordId);
        commentRequest.addRequestParam("record_type",recordType);
        commentRequest.addRequestParam("record_title",recordTitle);
        if(srcCommentId >= 0){
            commentRequest.addRequestParam("src_comment_id",srcCommentId);
        }
        commentRequest.addRequestParam("content",content);

        commentRequest.sendRequest();
    }
}
