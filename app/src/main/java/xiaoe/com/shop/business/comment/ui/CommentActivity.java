package xiaoe.com.shop.business.comment.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.comment.CommentListAdapter;
import xiaoe.com.shop.base.XiaoeActivity;

public class CommentActivity extends XiaoeActivity {
    private static final String TAG = "CommentActivity";
    private RecyclerView commentRecyclerView;
    private CommentListAdapter commentAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
    }

    private void initView() {
        TextView commentCount = (TextView) findViewById(R.id.comment_count);
        commentCount.setText("评论 1027 条");
        commentRecyclerView = (RecyclerView) findViewById(R.id.comment_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        commentRecyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.HORIZONTAL));
        commentRecyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentListAdapter(this);
        commentRecyclerView.setAdapter(commentAdapter);

    }
}
