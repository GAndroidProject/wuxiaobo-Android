package xiaoe.com.shop.adapter.tree;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class ChildViewHolder extends BaseViewHolder {

	public TextView text;
	public RelativeLayout relativeLayout;
	private final TextView playLength;
	private final ImageView playIcon;

	public ChildViewHolder(Context context, View itemView) {
		super(itemView);
		text = (TextView) itemView.findViewById(R.id.text);
		relativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_container);
		playLength = (TextView) itemView.findViewById(R.id.item_play_length);
		playIcon = (ImageView) itemView.findViewById(R.id.item_play_icon);
	}

	public void bindView(final ColumnSecondDirectoryEntity itemData, int position) {
		text.setText(itemData.getTitle());
		int type = itemData.getResource_type();
		if(type == 2){
			setMediaTypeState(View.VISIBLE);
			playLength.setText(DateFormat.longToString(itemData.getAudio_length() * 1000));
		}else if (type == 3){
			setMediaTypeState(View.VISIBLE);
			playLength.setText(DateFormat.longToString(itemData.getVideo_length() * 1000));
		}else{
			setMediaTypeState(View.GONE);
		}
		setMediaType(type);
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				openFileInSystem(itemData.getTitle(), view.getContext());
			}
		});
	}
	private void setMediaTypeState(int visibility){
		playLength.setVisibility(visibility);
		playIcon.setVisibility(visibility);
	}

	public void setMediaType(int type){
		if(type == 1){

		}else if(type == 2){
			playIcon.setImageResource(R.mipmap.audiolist_playall);
		}else if(type == 3){
			playIcon.setImageResource(R.mipmap.audiolist_vedio);
		}
	}

	private void openFileInSystem(String path, Context context) {

	}

	@SuppressLint("DefaultLocale")
	private String fileExt(String url) {
		if (url.indexOf("?") > -1) {
			url = url.substring(0, url.indexOf("?"));
		}
		if (url.lastIndexOf(".") == -1) {
			return null;
		} else {
			String ext = url.substring(url.lastIndexOf("."));
			if (ext.indexOf("%") > -1) {
				ext = ext.substring(0, ext.indexOf("%"));
			}
			if (ext.indexOf("/") > -1) {
				ext = ext.substring(0, ext.indexOf("/"));
			}
			return ext.toLowerCase();
		}
	}

}
