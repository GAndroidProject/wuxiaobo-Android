package xiaoe.com.shop.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xiaoe.com.common.entitys.ItemData;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.BatchDownloadAdapter;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.interfaces.OnSelectListener;

public class DownloadActivity extends XiaoeActivity implements View.OnClickListener,OnSelectListener {
    private static final String TAG = "DownloadActivity";
    private RecyclerView downloadRecyclerView;
    private BatchDownloadAdapter adapter;
    private ImageView allSetectImage;
    private TextView allSelectText;
    private RelativeLayout btnAllSelect;
    private boolean isAllSelect = false;
    private ImageView btnBack;
    private TextView btnOffLineCache;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        initViews();
        initData();
    }

    private void initViews() {
        downloadRecyclerView = (RecyclerView) findViewById(R.id.download_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        downloadRecyclerView.setLayoutManager(layoutManager);
        adapter = new BatchDownloadAdapter(this);
        adapter.setSelectListener(this);
        downloadRecyclerView.setAdapter(adapter);

        allSetectImage = (ImageView) findViewById(R.id.all_select_image);
        allSelectText = (TextView) findViewById(R.id.all_select_text);
        btnAllSelect = (RelativeLayout) findViewById(R.id.btn_all_select);
        btnAllSelect.setOnClickListener(this);

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnOffLineCache = (TextView) findViewById(R.id.btn_off_line_cache);
        btnOffLineCache.setOnClickListener(this);
    }

    private void initData() {
        List<ItemData> list = new ArrayList<ItemData>();
        for (int i = 0; i < 30; i++){
            int type = ItemData.ITEM_TYPE_PARENT;
            String text = "专栏-"+i;
            ItemData itemData = new ItemData(type, text,"", UUID.randomUUID().toString(), 1, null);
            List<ItemData> childList = new ArrayList<ItemData>();
            for (int j = 0; j < i + 1; j++){
                childList.add(new ItemData(ItemData.ITEM_TYPE_CHILD, text+"_音视频图文-"+j,"",UUID.randomUUID().toString(), 2,null));
            }
            itemData.setChildren(childList);
            list.add(itemData);
        }
        adapter.setItemData(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_select:
                clickAllSelect();
                break;
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_off_line_cache:
                Intent intent = new Intent(this, OffLineCacheActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }
    private void setAllsetectState(){
        if(isAllSelect){
            allSelectText.setText(getResources().getString(R.string.cancel_text));
            allSetectImage.setImageResource(R.mipmap.download_checking);
        }else{
            allSelectText.setText(getResources().getString(R.string.all_select_text));
            allSetectImage.setImageResource(R.mipmap.download_tocheck);
        }
    }
    private void clickAllSelect() {
        isAllSelect = !isAllSelect;
        setAllsetectState();
        for (ItemData item : adapter.getDate()) {
            item.setSelect(isAllSelect);
            for (ItemData childItem : item.getChildren()) {
                childItem.setSelect(isAllSelect);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelect(int positiont) {
        int selectCount = 0;
        for (ItemData item: adapter.getDate()) {
            if(item.isSelect()){
                selectCount++;
            }
        }
        isAllSelect = selectCount == adapter.getDate().size();
        setAllsetectState();
    }
}
