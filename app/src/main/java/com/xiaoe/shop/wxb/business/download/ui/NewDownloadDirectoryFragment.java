package com.xiaoe.shop.wxb.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.xiaoe.common.app.Constants;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.ColumnListRequst;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.DownLoadListAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.interfaces.OnClickListPlayListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author: zak
 * @date: 2018/12/20
 */
public class NewDownloadDirectoryFragment extends BaseFragment implements OnLoadMoreListener, BaseQuickAdapter.OnItemChildClickListener {

    private static final String TAG = "NewDownloadFragment";

    private static final String DOWNLOAD_ALL = "0";     // 拉取全部类型
    private static final String DOWNLOAD_MEMBER = "5";  // 拉取会员
    private static final String DOWNLOAD_COLUMN = "6";  // 拉取专栏
    private static final String DOWNLOAD_TOPIC = "8";   // 拉取大专栏
    private static final String TOPIC_LITTLE_REQUEST_TAG = "1001";//大专栏中请求小专栏

    View mRootView;
    Unbinder unbinder;

    @BindView(R.id.download_refresh)
    SmartRefreshLayout downloadRefresh;
    @BindView(R.id.download_recycler_view_new)
    RecyclerView downloadRecyclerViewNew;
    @BindView(R.id.all_select_txt_new)
    TextView allSelectTextNew;
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
    // 记录非单品的资源列表
    private List<MultiItemEntity> ColumnList;
    private boolean refreshData = false;
    private boolean showDataByDB = false;
    private DownLoadListAdapter downLoadListAdapter;
    private String downTitle = "";
    private int totalSelectedCount;

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
        allSelectTextNew = (TextView) mRootView.findViewById(R.id.all_select_txt_new);
        selectSizeNew = (TextView) mRootView.findViewById(R.id.select_size_new);
        btnDownloadNew = (TextView) mRootView.findViewById(R.id.btn_download_new);

        downloadRefresh.setOnLoadMoreListener(this);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setAutoMeasureEnabled(true);

        downloadRecyclerViewNew.setLayoutManager(manager);
        downloadRecyclerViewNew.setNestedScrollingEnabled(false);
        downLoadListAdapter = new DownLoadListAdapter(getActivity(), new ArrayList<MultiItemEntity>());
        downLoadListAdapter.setOnItemChildClickListener(this);
        downloadRecyclerViewNew.setAdapter(downLoadListAdapter);
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        String intentRt = intent.getStringExtra("resourceType");
        resourceId = intent.getStringExtra("resourceId");
        downTitle = intent.getStringExtra("down_title");
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
                    resourceType = DOWNLOAD_ALL;
                    break;
            }
        }
        String requestType;
        switch (resourceType) {
            case DOWNLOAD_COLUMN: // 若是专栏则请求该专栏的详情
                columnPresenter.requestColumnList(resourceId, DOWNLOAD_ALL, pageIndex, pageSize, true, TOPIC_LITTLE_REQUEST_TAG);
                break;
            case DOWNLOAD_MEMBER: // 若是会员则请求该会员的详情
                columnPresenter.requestColumnList(resourceId, DOWNLOAD_ALL, pageIndex, pageSize, true, TOPIC_LITTLE_REQUEST_TAG);
                break;
            case DOWNLOAD_TOPIC: // 若是大专栏则只请求小专栏的数据
                requestType = DOWNLOAD_COLUMN;
                columnPresenter.requestColumnList(resourceId, requestType, pageIndex, pageSize, true, resourceType);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        totalSelectedCount = 0;
        unbinder.unbind();
    }

    @OnClick({R.id.all_select_txt_new, R.id.btn_download_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_select_txt_new:
                List<MultiItemEntity> multiItemEntities = downLoadListAdapter.getData();
                if (Objects.equals(allSelectTextNew.getCompoundDrawables()[0].getConstantState(), Objects.requireNonNull(getContext().getDrawable(R.mipmap.download_checking)).getConstantState())) {
                    allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
                    notifyAllItem(multiItemEntities, false);
                } else {
                    allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_checking), null, null, null);
                    notifyAllItem(multiItemEntities, true);
                }
                break;
            case R.id.btn_download_new:
                break;
            default:
                break;
        }
    }

    /**
     * 更新全部 item 的选中状态
     * @param multiItemEntities 栏目集合
     * @param isSelect          是否选中
     */
    private void notifyAllItem(List<MultiItemEntity> multiItemEntities, boolean isSelect) {
        for (MultiItemEntity multiItemEntity : multiItemEntities) {
            if (multiItemEntity instanceof ExpandableLevel) {
                ExpandableLevel level = (ExpandableLevel) multiItemEntity;
                level.setSelect(isSelect);
                List<ExpandableItem> expandableItems = level.getSubItems();
                for (ExpandableItem expandableItem : expandableItems) {
                    expandableItem.setSelect(isSelect);
                    if (isSelect) {
                        if (!TextUtils.isEmpty(expandableItem.getResource_id())) {
                            totalSelectedCount++;
                        }
                    } else {
                        if (!TextUtils.isEmpty(expandableItem.getResource_id())) {
                            totalSelectedCount--;
                        }
                    }
                }
                downLoadListAdapter.notifyDataSetChanged();
            }
        }
        if (totalSelectedCount == 0) {
            selectSizeNew.setText("");
        } else {
            selectSizeNew.setText(String.format(getContext().getString(R.string.the_selected_count), totalSelectedCount));
        }
    }

    // 上拉加载更多
    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        pageIndex++;
        if (resourceType.equals(DOWNLOAD_TOPIC)) { // 如果是大专栏则只请求小专栏
            columnPresenter.requestColumnList(resourceId, DOWNLOAD_COLUMN, pageIndex, pageSize, false, resourceType+"");
        } else { // 否则表示只有一个非单品的情况，此时禁止加载更多
            downloadRefresh.setEnableLoadMore(false);
            downloadRefresh.setNoMoreData(true);
            downloadRefresh.finishLoadMore();
        }
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
        JSONArray dataArr = (JSONArray) data;
        JSONArray resultArr = new JSONArray();
        if (code != NetworkCodes.CODE_SUCCEED && !TOPIC_LITTLE_REQUEST_TAG.equals(iRequest.getRequestTag())) {
            if(iRequest instanceof ColumnListRequst){
                // TODO: 列表加载失败
                Log.d(TAG, "onMainThreadResponse: 列表加载失败了..");
            }
            return;
        }
        if (iRequest instanceof ColumnListRequst) {
            allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
            if (TOPIC_LITTLE_REQUEST_TAG.equals(iRequest.getRequestTag())) { // 请求大专栏下第一个小专栏下的资源列表
                topicLittleColumnRequest(result);
            } else if (resourceType.equals(iRequest.getRequestTag())) { // 下载列表的请求处理
                filterData(dataArr, resultArr);
                columnListRequest(resultArr);
                downloadRefresh.finishLoadMore();
                if (resultArr.size() < 20 && dataArr.size() < 20) { // 请求回来的数据与实际可下载的数组的大小都小于 20，则加载完成
                    downloadRefresh.setEnableLoadMore(false);
                }
            } else { // 下载列表 item 展开的请求
                filterData(dataArr, resultArr);
                listLoadSucceed(resultArr, iRequest.getRequestTag());
            }
        }
    }

    /**
     * 大专栏下第一个小专栏下的资源列表请求
     * @param data
     */
    private void topicLittleColumnRequest(JSONObject data){
        int code = data.getIntValue("code");
        generateColumnListIfNeed();
        ExpandableLevel level = (ExpandableLevel) ColumnList.get(0);
        if(code != NetworkCodes.CODE_SUCCEED){
            level.getSubItem(0).setLoadType(DownLoadListAdapter.LOAD_FAIL);
        }else{
            List<ExpandableItem> expandableItems = columnPresenter.formatExpandableChildEntity(data.getJSONArray("data"), level.getTitle(), level.getResource_id(), level.getBigColumnId(), 1);
            level.setExpand(true);
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
                level.getSubItem(0).setLoadType(DownLoadListAdapter.NO_LOAD);
            }
            for (ExpandableItem item : expandableItems) {
                if (item.getResource_type() == 1) {
                    expandableItems.remove(item);
                }
            }
            level.getSubItems().addAll(0, expandableItems);
        }
        refreshDownloadListData(ColumnList);
    }

    /**
     * 按需生成专栏列表
     */
    private void generateColumnListIfNeed() {
        if (ColumnList == null) { // 只有一个非单品的情况，需要构造一个 json 数组作为头布局
            JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("resource_type", Integer.parseInt(resourceType));
            jsonObject.put("img_url_compress", "");
            jsonObject.put("resource_list", new Object[1]);
            jsonObject.put("resource_id", resourceId);
            jsonObject.put("app_id", Constants.getAppId());
            jsonObject.put("audio_compress_url", "");
            jsonObject.put("m3u8_url", "");
            jsonObject.put("video_length", "");
            jsonObject.put("title", downTitle);
            jsonObject.put("img_url", "");
            jsonObject.put("audio_length", 0);
            jsonObject.put("try_audio_url", "");
            jsonObject.put("video_url", "");
            jsonObject.put("try_m3u8_url", "");
            jsonObject.put("audio_url", "");
            jsonArray.add(jsonObject);
            ColumnList = columnPresenter.formatDownloadExpandableEntity(jsonArray, resourceId, 1);
        }
    }

    // 专栏列表初始化（大专栏的情况）
    private void columnListRequest(JSONArray data) {
        switch (resourceType) {
            case DOWNLOAD_TOPIC:
                if (refreshData || showDataByDB || pageIndex == 1) {
                    refreshData = false;
                    showDataByDB = false;
                    ColumnList = columnPresenter.formatDownloadExpandableEntity(data, resourceId, 1);
                    //请求第一个小专栏下资源
                    if (ColumnList.size() > 0) {
                        ExpandableLevel level = (ExpandableLevel) ColumnList.get(0);

                        //比较播放中的专栏是否和点击的状态相同
                        AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                        boolean resourceEquals = !TextUtils.isEmpty(level.getBigColumnId()) && audioPlayEntity != null
                                && level.getBigColumnId().equals(audioPlayEntity.getBigColumnId())
                                && level.getResource_id().equals(audioPlayEntity.getColumnId());
                        if (resourceEquals && level.getChildPage() == 1 && audioPlayEntity != null && audioPlayEntity.getPlayColumnPage() > 1) {
                            level.setChildPageSize(level.getChildPageSize() * audioPlayEntity.getPlayColumnPage());
                        }
                        level.setExpand(true);
                        columnPresenter.requestColumnList(level.getResource_id(), "0", level.getChildPage(), level.getChildPageSize(), false, TOPIC_LITTLE_REQUEST_TAG);
                    }
                } else {
                    addDownloadListData(columnPresenter.formatDownloadExpandableEntity(data, resourceId, 1));
                }
            break;
        }
    }

    public void addDownloadListData(List<MultiItemEntity> list) {
        if (downLoadListAdapter != null) {
            downLoadListAdapter.addData(list);
        }
    }

    public void refreshDownloadListData(List<MultiItemEntity> list) {
        setDownloadListData(list);
    }

    public void setDownloadListData(List<MultiItemEntity> list) {
        downLoadListAdapter.setNewData(list);
        if (list.size() > 0) {
            downLoadListAdapter.expand(0);
        }
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        // position 为点击的位置；pos 为头布局所在的真实位置
        int pos = downLoadListAdapter.getParentPosition(downLoadListAdapter.getData().get(position));
        ExpandableLevel level = (ExpandableLevel) downLoadListAdapter.getItem(pos);
        switch (id) {
            case R.id.group_bottom_wrap:
                collapseByPos(level, pos);
                break;
            case R.id.group_title_tail:
                expandIfNeed(level, position);
                break;
            case R.id.group_title_head:
                handleHeadSelect(view, level, pos);
                break;
            case R.id.group_item_wrap:
                handleItemSelect(view, level, position, pos);
                break;
            case R.id.btn_expand_load_all:
                clickLoadMore(position, (ExpandableItem) downLoadListAdapter.getData().get(position));
                break;
        }
    }

    /**
     * 根据指定的位置，折叠展开的子列表
     *
     * @param level 折叠的头布局
     * @param pos 折叠头布局的下标
     */
    private void collapseByPos(ExpandableLevel level, int pos) {
        downLoadListAdapter.collapse(pos);
        if (level != null) {
            level.setExpand(false);
        }
    }

    /**
     * 根据指定的位置，展开子列表
     * @param level    折叠的头布局
     * @param position 折叠的头布局所在页面的下标
     */
    private void expandIfNeed(ExpandableLevel level, int position) {
        int itemType = level != null ? level.getSubItem(0).getItemType() : 0;
        if (level != null && !level.isExpanded()) {
            downLoadListAdapter.expand(position);
            level.setExpand(true);
        }
        if (itemType == 3) {
            //点击展开加载数据，并发时失败加载
            clickFailRefresh(position + 1, level.getSubItem(0));
        }
    }

    /**
     * 处理选中图片的切换
     * @param view  点击的 view（这里是 TextView）
     * @param level 点击的头布局
     * @param pos   头布局所在的真实位置
     */
    private void handleHeadSelect(View view, ExpandableLevel level, int pos) {
        TextView head = (TextView) view;
        // 第一个是 drawableStart
        if (Objects.equals(head.getCompoundDrawables()[0].getConstantState(), Objects.requireNonNull(getContext().getDrawable(R.mipmap.download_checking)).getConstantState())) {
            if (level != null) {
                notifyHeadSelectState(level, false, pos);
            }
            head.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
        } else {
            if (level != null) {
                notifyHeadSelectState(level, true, pos);
            }
            head.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_checking), null, null, null);
        }
    }

    /**
     * 更新头部布局选中状态
     * @param level    选中的头部布局
     * @param isSelect 是否选中
     * @param pos      头部布局的真实位置
     */
    private void notifyHeadSelectState(ExpandableLevel level, boolean isSelect, int pos) {
        level.setSelect(isSelect);
        List<ExpandableItem> items = level.getSubItems();
        for (ExpandableItem item : items) {
            if (!TextUtils.isEmpty(item.getResource_id())) {
                if (isSelect) { // 是真正的资源数量才发生变化
                    if (items.size() > 21) {
                        if (totalSelectedCount < items.size() - 2) {
                            totalSelectedCount++;
                        }
                    } else {
                        if (totalSelectedCount < items.size() - 1) {
                            totalSelectedCount++;
                        }
                    }
                } else {
                    if (items.size() > 21) {
                        if (totalSelectedCount > 2) {
                            totalSelectedCount--;
                        }
                    } else {
                        if (totalSelectedCount > 1) {
                            totalSelectedCount--;
                        }
                    }
                }
            }
            item.setSelect(isSelect);
        }
        if (totalSelectedCount == 0) {
            selectSizeNew.setText("");
            allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
        } else {
            selectSizeNew.setText(String.format(getContext().getString(R.string.the_selected_count), totalSelectedCount));
            allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_checking), null, null, null);
        }
        if (level.isExpand()) { // 打开了
            downLoadListAdapter.collapse(pos, false);
            downLoadListAdapter.expand(pos, false);
        }
    }

    /**
     * 处理点击的子列表 item 的选中状态
     * @param view     点击的 view（此处为 FrameLayout）
     * @param level    点击的 view 所在头布局
     * @param position 点击的 View 所在的位置
     * @param pos      点击的 View 所在的头布局的位置
     */
    private void handleItemSelect(View view, ExpandableLevel level, int position, int pos) {
        FrameLayout wrap = (FrameLayout) view;
        TextView head = (TextView) wrap.findViewById(R.id.group_item_head);
        // 第一个是 drawableStart
        if (Objects.equals(head.getCompoundDrawables()[0].getConstantState(), Objects.requireNonNull(getContext().getDrawable(R.mipmap.download_checking)).getConstantState())) {
            head.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
            if (level != null) {
                // 拿到点击的 expandItem 下标，并设置是否选中
                notifyIfAllSelect(level, false, position, pos);
            }
        } else {
            head.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_checking), null, null, null);
            if (level != null) {
                // 拿到点击的 expandItem 下标，并设置是否选中
                notifyIfAllSelect(level, true, position, pos);
            }
        }
    }

    /**
     * 刷新全部选中
     * @param level 刷新的头布局
     * @param isItemSelect 点击的 item 是否被选中
     * @param clickPos  点击位置
     * @param realPos   刷新的头布局所在页面的位置
     */
    private void notifyIfAllSelect(ExpandableLevel level, boolean isItemSelect, int clickPos, int realPos) {
        int clickItemIndex = clickPos - realPos - 1;
        List<ExpandableItem> items = level.getSubItems();
        items.get(clickItemIndex).setSelect(isItemSelect);
        if (isItemSelect) {
            totalSelectedCount++;
        } else {
            totalSelectedCount--;
        }
        if (totalSelectedCount == 0) {
            selectSizeNew.setText("");
        } else {
            selectSizeNew.setText(String.format(getContext().getString(R.string.the_selected_count), totalSelectedCount));
        }
        boolean allSelect = true;
        if (items.size() < 20) {
            for (int i = 0; i < items.size() - 1; i++) {
                if (!items.get(i).isSelect()) {
                    allSelect = false;
                }
            }
            if (level.getSubItems().size() - 1 == totalSelectedCount) {
                allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_checking), null, null, null);
            } else {
                if (Objects.equals(allSelectTextNew.getCompoundDrawables()[0].getConstantState(), Objects.requireNonNull(getContext().getDrawable(R.mipmap.download_checking)).getConstantState())) {
                    allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
                }
            }
        } else {
            for (int i = 0; i < items.size() - 2; i++) {
                if (!items.get(i).isSelect()) {
                    allSelect = false;
                }
            }
            if (level.getSubItems().size() - 2 == totalSelectedCount) {
                allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_checking), null, null, null);
            } else {
                if (Objects.equals(allSelectTextNew.getCompoundDrawables()[0].getConstantState(), Objects.requireNonNull(getContext().getDrawable(R.mipmap.download_checking)).getConstantState())) {
                    allSelectTextNew.setCompoundDrawablesWithIntrinsicBounds(getContext().getDrawable(R.mipmap.download_tocheck), null, null, null);
                }
            }
        }
        level.setSelect(allSelect);
        downLoadListAdapter.collapse(realPos, false);
        downLoadListAdapter.expand(realPos, false);
    }

    /**
     * 点击加载更多
     * @param position       点击的位置
     * @param expandableItem 点击的 item
     */
    private void clickLoadMore(int position, ExpandableItem expandableItem) {
        int parentPosition = downLoadListAdapter.getParentPosition(expandableItem);
        ExpandableLevel level = (ExpandableLevel) downLoadListAdapter.getItem(parentPosition);
        if(level  == null){
            return;
        }
        expandableItem.setLoadType(DownLoadListAdapter.LOADING);
        downLoadListAdapter.notifyItemChanged(position);

        int page = level.getChildPage() + 1;
        level.setChildPage(page);
        columnPresenter.requestColumnList(level.getResource_id(), "0", page, level.getChildPageSize(), false, level.getResource_id());
    }

    private void clickFailRefresh(int position, ExpandableItem expandableItem) {
        expandableItem.setLoadType(DownLoadListAdapter.LOADING);
        downLoadListAdapter.notifyItemChanged(position);

        int parentPosition = downLoadListAdapter.getParentPosition(expandableItem);
        ExpandableLevel level = (ExpandableLevel) downLoadListAdapter.getItem(parentPosition);
        if(level  == null){
            return;
        }
        expandableItem.setLoadType(DownLoadListAdapter.LOADING);
        downLoadListAdapter.notifyItemChanged(position);

        //比较播放中的专栏是否和点击的状态相同
        AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
        boolean resourceEquals = !TextUtils.isEmpty(level.getBigColumnId()) && audioPlayEntity != null
                && level.getBigColumnId().equals(audioPlayEntity.getBigColumnId())
                && level.getResource_id().equals(audioPlayEntity.getColumnId());
        int page = level.getChildPage();
        int pageSize = level.getChildPageSize();
        if(resourceEquals && level.getChildPage() == 1 && audioPlayEntity != null && audioPlayEntity.getPlayColumnPage() > 1){
            pageSize = level.getChildPageSize() * audioPlayEntity.getPlayColumnPage();
            level.setChildPageSize(pageSize);
        }

        columnPresenter.requestColumnList(level.getResource_id(), "0", page, pageSize, false, level.getResource_id());
    }

    /**
     * 加上锁是因为可能存在点击多个加载，防止列表数据错乱
     * @param data
     * @param requestTag
     */
    private synchronized void listLoadSucceed(JSONArray data, String requestTag) {
        int position = -1;
        for (MultiItemEntity entity : downLoadListAdapter.getData()){
            position++;
            if(entity instanceof ExpandableLevel){
                ExpandableLevel level = (ExpandableLevel) entity;
                if(requestTag.equals(level.getResource_id())){
                    List<ExpandableItem> expandableItems = columnPresenter.formatExpandableChildEntity(data, level.getTitle(), level.getResource_id(), level.getBigColumnId(), 1);
//                    int insertedPosition = (level.getChildPage() - 1) * level.getChildPageSize();
                    if (expandableItems.size() > 0) { // 如果拿到新的数据的话，就改变头布局的选中状态
                        level.setSelect(false);
                    }
                    int insertedPosition = level.getSubItems().size() - 2;
                    boolean isExpanded = level.isExpanded();
                    level.getSubItems().addAll(insertedPosition, expandableItems);

                    if(expandableItems.size() < level.getChildPageSize()){
                        //说明小专栏下列表资源已加载完
                        if(expandableItems.size()  > 0){
                            expandableItems.get(expandableItems.size() - 1).setLastItem(true);
                        }else {
                            // 因为写死了两个 item 用于表示 loading 和 展开，所以需要判断初始的长度大于 2，否则无数据时会报错
                            if (level.getSubItems().size() > 2) {
                                ExpandableItem item = level.getSubItem(level.getSubItems().size() - 3);
                                if (item.getItemType() == 1) {
                                    item.setLastItem(true);
                                }
                            } else {
                                ExpandableItem item = level.getSubItem(1);
                                item.setLastItem(true);
                            }
                        }
                        if(isExpanded){
                            downLoadListAdapter.remove(position + insertedPosition + 1);
                        }else{
                            level.removeSubItem(level.getSubItems().size() - 2);
                        }
                    }else {
                        level.getSubItem(level.getSubItems().size() - 2).setLoadType(DownLoadListAdapter.NO_LOAD);
                    }

                    if(isExpanded){
                        downLoadListAdapter.addData(position + insertedPosition+1, expandableItems);
                        downLoadListAdapter.notifyDataSetChanged();
                    }
                    break;
                }
            }
        }
    }

    /**
     * 过滤无用数据
     * @param dataArr   源数据
     * @param resultArr 结果数据
     */
    private void filterData (JSONArray dataArr, JSONArray resultArr) {
        for (Object item : dataArr) {
            JSONObject itemObj = (JSONObject) item;
            int tempResourceType = itemObj.getInteger("resource_type") == null ? -1 : itemObj.getInteger("resource_type");
            if (tempResourceType != -1 && tempResourceType != 1 && tempResourceType != 4 && tempResourceType != 20) { // 非音、视频单品
                resultArr.add(item);
            }
        }
    }
}
