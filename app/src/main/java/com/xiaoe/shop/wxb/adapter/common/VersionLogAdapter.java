package com.xiaoe.shop.wxb.adapter.common;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.xiaoe.common.entitys.VersionLog;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.RippleView;

import java.util.List;

/**
 * 版本更新说明
 *
 * @author Administrator
 */
public class VersionLogAdapter extends BaseAdapter {

    private static final String TAG = "VersionLogAdapter";
    private Context context;
    private Handler handler;
    private List<VersionLog> lists;

    public VersionLogAdapter(Context context, Handler handler, List<VersionLog> lists) {
        this.context = context;
        this.handler = handler;
        this.lists = lists;
    }

    @Override
    public int getCount() {
        return lists == null ? 0 : lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists == null ? null : lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.option_item_vote_et, parent, false);
            holder = new ViewHolder();
            holder.et_item_vote = (EditText) convertView.findViewById(R.id.et_item_vote);
            holder.iv_delete_item_vote = (ImageView) convertView.findViewById(R.id.iv_delete_item_vote);
            holder.rv_delete_item_vote = (RippleView) convertView.findViewById(R.id.rv_delete_item_vote);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (position + 1 == lists.size()) {
            holder.et_item_vote.requestFocus();
            holder.iv_delete_item_vote.setImageResource(R.drawable.icon_add_item_vote);
        } else {
            holder.iv_delete_item_vote.setImageResource(R.drawable.icon_delete_item_vote);
        }

        holder.et_item_vote.setHint("请输入更新说明" + (position + 1));
        VersionLog voteItem = lists.get(position);
        String content = voteItem.getContent();
        if (!TextUtils.isEmpty(content)) {
            holder.et_item_vote.setText(voteItem.getContent());
        }

        holder.et_item_vote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 100) {
                    ToastUtils.show(context, "不能超过100个字符");
                } else {
                    lists.get(position).setContent(s.toString());

                    // 给用户显示已输入了几项
                    Message msg = new Message();
                    msg.arg1 = position;
                    msg.what = 3;
                    handler.sendMessage(msg);

                    Log.d(TAG, "afterTextChanged: " + position + " voteItem content:" + s);
                }
            }
        });
        holder.rv_delete_item_vote.setOnRippleCompleteListener(rippleView -> {
            Message msg = new Message();
            msg.arg1 = position;
            if (position + 1 == lists.size()) {
                msg.what = 1;
            } else {
                msg.what = 2;
            }
            handler.sendMessage(msg);
        });

        return convertView;
    }

    class ViewHolder {
        EditText et_item_vote;
        ImageView iv_delete_item_vote;
        RippleView rv_delete_item_vote;
    }
}
