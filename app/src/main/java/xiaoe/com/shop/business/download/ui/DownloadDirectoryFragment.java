package xiaoe.com.shop.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.BatchDownloadAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.interfaces.OnSelectListener;

public class DownloadDirectoryFragment extends BaseFragment implements View.OnClickListener, OnSelectListener {
    private static final String TAG = "DownloadDirectory";
    private RecyclerView downloadRecyclerView;
    private BatchDownloadAdapter adapter;
    private ImageView allSetectImage;
    private TextView allSelectText;
    private RelativeLayout btnAllSelect;
    private boolean isAllSelect = false;
    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_download_directory, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        String dataJson = intent.getStringExtra("bundle_dataJSON");

    }

    private void initView() {
        downloadRecyclerView = (RecyclerView) mRootView.findViewById(R.id.download_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadRecyclerView.setLayoutManager(layoutManager);
        adapter = new BatchDownloadAdapter(getContext());
        adapter.setSelectListener(this);
        downloadRecyclerView.setAdapter(adapter);

        allSetectImage = (ImageView) mRootView.findViewById(R.id.all_select_image);
        allSelectText = (TextView) mRootView.findViewById(R.id.all_select_text);
        btnAllSelect = (RelativeLayout) mRootView.findViewById(R.id.btn_all_select);
        btnAllSelect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_select:
                clickAllSelect();
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
        for (ColumnDirectoryEntity item : adapter.getDate()) {
            item.setSelect(isAllSelect);
            for (ColumnSecondDirectoryEntity childItem : item.getResource_list()) {
                childItem.setSelect(isAllSelect);
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onSelect(int positiont) {
        int selectCount = 0;
        for (ColumnDirectoryEntity item: adapter.getDate()) {
            if(item.isSelect()){
                selectCount++;
            }
        }
        isAllSelect = selectCount == adapter.getDate().size();
        setAllsetectState();
    }
}
