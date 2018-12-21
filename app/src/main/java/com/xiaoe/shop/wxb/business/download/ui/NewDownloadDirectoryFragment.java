package com.xiaoe.shop.wxb.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.tree.ExpandableItemAdapter;
import com.xiaoe.shop.wxb.adapter.tree.TreeChildRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.interfaces.OnClickListPlayListener;
import com.xiaoe.shop.wxb.widget.DashlineItemDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author: zak
 * @date: 2018/12/20
 */
public class NewDownloadDirectoryFragment extends BaseFragment implements OnLoadMoreListener, OnClickListPlayListener {

    private static final String TAG = "NewDownloadFragment";

    private static final String DOWNLOAD_ALL = "0";     // 拉取全部类型
    private static final String DOWNLOAD_MEMBER = "5";  // 拉取会员
    private static final String DOWNLOAD_COLUMN = "6";  // 拉取专栏
    private static final String DOWNLOAD_TOPIC = "8";   // 拉取大专栏
    private final String TOPIC_LITTLE_REQUEST_TAG = "1001";//大专栏中请求小专栏

    View mRootView;
    Unbinder unbinder;

    @BindView(R.id.download_refresh)
    SmartRefreshLayout downloadRefresh;
    @BindView(R.id.download_recycler_view_new)
    RecyclerView downloadRecyclerViewNew;
    @BindView(R.id.all_select_txt_new)
    TextView allSelectTxtNew;
    @BindView(R.id.select_size_new)
    TextView selectSizeNew;
    @BindView(R.id.btn_download_new)
    TextView btnDownloadNew;
    @BindView(R.id.bottom_select_download_wrap_new)
    LinearLayout bottomSelectDownloadWrap;

    private ColumnPresenter columnPresenter;
    private String resourceId;  // 资源 id
    private String resourceType;// 资源类型
    private int pageIndex = 1;
    private int pageSize = 20;
    //请求大专栏下第一个小专栏下资源列表，（小专栏、会员页面无效）
    private boolean requestTopicFirstLittle = false;
    //记录大专栏下小专栏列表，（小专栏、会员页面无效）
    private List<MultiItemEntity> topicLittleColumnList;
    private boolean refreshData = false;
    private boolean showDataByDB = false;
    private ExpandableItemAdapter expandAdapter;
    private TreeChildRecyclerAdapter treeChildAdapter;
    private boolean isAddList = false;
    private List<MultiItemEntity> tempExpandableList;
    private List<ColumnSecondDirectoryEntity> tempSecondDirectoryList;
    private String downTitle = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_download_directory_new, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        columnPresenter = new ColumnPresenter(this);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initView();
    }

    private void initView() {
        downloadRefresh = (SmartRefreshLayout) mRootView.findViewById(R.id.download_refresh);
        downloadRecyclerViewNew = (RecyclerView) mRootView.findViewById(R.id.download_recycler_view_new);
        allSelectTxtNew = (TextView) mRootView.findViewById(R.id.all_select_txt_new);
        selectSizeNew = (TextView) mRootView.findViewById(R.id.select_size_new);
        btnDownloadNew = (TextView) mRootView.findViewById(R.id.btn_download_new);

        downloadRefresh.setOnLoadMoreListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setAutoMeasureEnabled(true);

        downloadRecyclerViewNew.setLayoutManager(manager);
        downloadRecyclerViewNew.setNestedScrollingEnabled(false);

        if (resourceType.equals(DOWNLOAD_TOPIC)) {
            expandAdapter = new ExpandableItemAdapter(new ArrayList<MultiItemEntity>());
            downloadRecyclerViewNew.setAdapter(expandAdapter);
        } else {
            int padding = Dp2Px2SpUtil.dp2px(getContext(), 16);
            downloadRecyclerViewNew.addItemDecoration(new DashlineItemDivider(padding, padding));
            treeChildAdapter = new TreeChildRecyclerAdapter(getContext(), this);
            downloadRecyclerViewNew.setAdapter(treeChildAdapter);
        }
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        String intentRt = intent.getStringExtra("resourceType");
        resourceId = intent.getStringExtra("resourceId");
        if (!TextUtils.isEmpty(intentRt)) { // 从 intent 中获取的资源 type 不为空
            switch (intentRt) {
                case DOWNLOAD_MEMBER:
                    resourceType = DOWNLOAD_MEMBER;
                    break;
                case DOWNLOAD_COLUMN:
                    resourceType = DOWNLOAD_COLUMN;
                    break;
                case DOWNLOAD_TOPIC:
                    resourceType = DOWNLOAD_TOPIC;
                    break;
                default:
                    resourceType = "0";
                    break;
            }
        }
        String requestType = resourceType.equals(DOWNLOAD_TOPIC) ? DOWNLOAD_COLUMN : DOWNLOAD_ALL; // 若是大专栏则只请求小专栏的数据
        columnPresenter.requestColumnList(resourceId, requestType, pageIndex, pageSize, true, resourceType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.all_select_txt_new, R.id.btn_download_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_select_txt_new:
                break;
            case R.id.btn_download_new:
                break;
            default:
                break;
        }
    }

    // 上拉加载更多
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (entity == null || !success) {
            return;
        }
        int code = result.getInteger("code");
        Object data = result.get("data");
        if (code != NetworkCodes.CODE_SUCCEED && !TOPIC_LITTLE_REQUEST_TAG.equals(iRequest.getRequestTag())) {
            if(iRequest instanceof ColumnListRequst){
                // TODO: 列表加载失败
            }
            return;
        }
        if (iRequest instanceof ColumnListRequst) {
            if(TOPIC_LITTLE_REQUEST_TAG.equals(iRequest.getRequestTag())){
                //请求大专栏下第一个小专栏下的资源列表
                topicLittleColumnRequest(result);
            }else{
                JSONArray dataArr = (JSONArray) data;
                columnListRequest(dataArr);
            }
        }
    }

    /**
     * 大专栏下第一个小专栏下的资源列表请求
     * @param data
     */
    private void topicLittleColumnRequest(JSONObject data){
        int code = data.getIntValue("code");

        requestTopicFirstLittle = false;
        ExpandableLevel level = (ExpandableLevel) topicLittleColumnList.get(0);
        if(code != NetworkCodes.CODE_SUCCEED){
            level.getSubItem(0).setLoadType(3);
        }else{
            List<ExpandableItem> expandableItems = columnPresenter.formatExpandableChildEntity(data.getJSONArray("data"), level.getTitle(), level.getResource_id(), level.getBigColumnId(), 1);
            if(expandableItems.size() < level.getChildPageSize()){
                //说明小专栏下列表资源已加载完
                if(expandableItems.size()  > 0){
                    expandableItems.get(expandableItems.size() - 1).setLastItem(true);
                }else {
                    ExpandableItem item = level.getSubItem(level.getSubItems().size() - 3);
                    if(item.getItemType() == 1){
                        item.setLastItem(true);
                    }
                }
                level.getSubItems().remove(0);
            }else{
                level.getSubItem(0).setLoadType(0);
            }
            level.getSubItems().addAll(0, expandableItems);
        }
        refreshExpandableData(topicLittleColumnList);
    }

    private void columnListRequest(JSONArray data) {
        switch (resourceType) {
            case DOWNLOAD_TOPIC:
                if (refreshData || showDataByDB || pageIndex == 1) {
                    refreshData = false;
                    showDataByDB = false;
                    topicLittleColumnList = columnPresenter.formatExpandableEntity(data, resourceId, 1);
                    //请求第一个小专栏下资源
                    if (topicLittleColumnList.size() > 0) {
                        requestTopicFirstLittle = true;
                        ExpandableLevel level = (ExpandableLevel) topicLittleColumnList.get(0);

                        //比较播放中的专栏是否和点击的状态相同
                        AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                        boolean resourceEquals = !TextUtils.isEmpty(level.getBigColumnId()) && audioPlayEntity != null
                                && level.getBigColumnId().equals(audioPlayEntity.getBigColumnId())
                                && level.getResource_id().equals(audioPlayEntity.getColumnId());
                        if (resourceEquals && level.getChildPage() == 1 && audioPlayEntity != null && audioPlayEntity.getPlayColumnPage() > 1) {
                            level.setChildPageSize(level.getChildPageSize() * audioPlayEntity.getPlayColumnPage());
                        }

                        columnPresenter.requestColumnList(level.getResource_id(), "0", level.getChildPage(), level.getChildPageSize(), false, TOPIC_LITTLE_REQUEST_TAG);
                    }
                } else {
                    addExpandableData(columnPresenter.formatExpandableEntity(data, resourceId, 1));
                }
                break;
            case DOWNLOAD_MEMBER:
                if (refreshData || showDataByDB || pageIndex == 1) {
                    refreshData = false;
                    showDataByDB = false;
                    refreshSecondDirectoryData(columnPresenter.formatSingleResourceEntity(data, downTitle, resourceId, "", 1));
                } else {
                    addSecondDirectoryData(columnPresenter.formatSingleResourceEntity(data, downTitle, resourceId, "", 1));
                }
                break;
            default:
                if (refreshData || showDataByDB || pageIndex == 1) {
                    refreshData = false;
                    showDataByDB = false;
                    refreshSecondDirectoryData(columnPresenter.formatSingleResourceEntity(data, downTitle, resourceId, "", 1));
                } else {
                    addSecondDirectoryData(columnPresenter.formatSingleResourceEntity(data, downTitle, resourceId, "", 1));
                }
                break;
        }
    }

    public void addExpandableData(List<MultiItemEntity> list){
        if(expandAdapter != null){
            isAddList = true;
            expandAdapter.addData(list);
        }else{
            tempExpandableList = list;
        }
    }

    public void refreshExpandableData(List<MultiItemEntity> list){
        setExpandableData(list);
    }

    public void setExpandableData(List<MultiItemEntity> list) {
        expandAdapter.setNewData(list);
        if(list.size() > 0){
            expandAdapter.expand(0);
        }
    }

    public void addSecondDirectoryData(List<ColumnSecondDirectoryEntity> list){
        if(treeChildAdapter != null){
            isAddList = true;
            treeChildAdapter.addAll(list);
        }else{
            tempSecondDirectoryList = list;
        }
    }
    public void setSecondDirectoryData(List<ColumnSecondDirectoryEntity> list){
        treeChildAdapter.setData(list);
    }

    public void refreshSecondDirectoryData(List<ColumnSecondDirectoryEntity> list){
        if (treeChildAdapter == null) {
            treeChildAdapter = new TreeChildRecyclerAdapter(getContext(), this);
        }
        treeChildAdapter.refreshData(list);
    }

    @Override
    public void onPlayPosition(View view, int parentPosition, int position, boolean jumpDetail) {

    }

    @Override
    public void onJumpDetail(ColumnSecondDirectoryEntity itemData, int parentPosition, int position) {

    }
}
