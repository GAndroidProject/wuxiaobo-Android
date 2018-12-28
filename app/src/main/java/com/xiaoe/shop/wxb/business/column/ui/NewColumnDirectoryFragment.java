package com.xiaoe.shop.wxb.business.column.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.tree.ExpandableItemAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.business.column.presenter.ColumnPresenter;
import com.xiaoe.shop.wxb.business.download.ui.DownloadActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class NewColumnDirectoryFragment extends BaseFragment implements View.OnClickListener,
        BaseQuickAdapter.OnItemChildClickListener {
    private static final String TAG = "NewColumnDirectoryFragm";
    private View rootView;
    private List<LoginUser> loginUserList;
    private TouristDialog touristDialog;
    private RecyclerView directoryRecyclerView;
    private LinearLayout btnBatchDownload;
    private ExpandableItemAdapter directoryAdapter;
//    private int playParentPosition = -1;
//    private int playPosition = -1;
    private boolean isHasBuy = false;
    private boolean isAddPlayList = false;
    private List<MultiItemEntity> tempList;
    private boolean isAddList = false;
    private String resourceId;
    private ColumnPresenter mColumnPresenter;
    private int recordPlayPage = 1;//记录播放资源所在小专栏中属于哪一页的数据

    public NewColumnDirectoryFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_column_directory, null, false);
        EventBus.getDefault().register(this);
        loginUserList = getLoginUserList();

        if (loginUserList.size() == 0) {
            touristDialog = new TouristDialog(getActivity());
            touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                    JumpDetail.jumpLogin(getActivity(), true);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        directoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.directory_recycler_view);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setAutoMeasureEnabled(true);
        directoryAdapter = new ExpandableItemAdapter(new ArrayList<MultiItemEntity>());
        directoryRecyclerView.setLayoutManager(manager);
        directoryRecyclerView.setNestedScrollingEnabled(false);
        directoryRecyclerView.setAdapter(directoryAdapter);

        btnBatchDownload = (LinearLayout) rootView.findViewById(R.id.btn_batch_download);
        btnBatchDownload.setOnClickListener(this);
        directoryAdapter.setOnItemChildClickListener(this);
    }

    private void initData() {
        mColumnPresenter = new ColumnPresenter(this);
        if(tempList != null && !isAddList){
            directoryAdapter.addData(tempList);
        }
    }


    public void addData(List<MultiItemEntity> list){
        if(directoryAdapter != null){
            isAddList = true;
            directoryAdapter.addData(list);
        }else{
            tempList = list;
        }
    }
    public void refreshData(List<MultiItemEntity> list){
        setData(list);
    }
    public void setData(List<MultiItemEntity> list){
        if (directoryAdapter == null) {
            directoryAdapter = new ExpandableItemAdapter(new ArrayList<MultiItemEntity>());
        }
        directoryAdapter.setNewData(list);
        if(list.size() > 0){
            directoryAdapter.expand(0);
        }
    }
    public void clearData(){
//        directoryAdapter.clearData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject jsonObject = (JSONObject) entity;
        int code = jsonObject.getInteger("code");
        if(success && code == NetworkCodes.CODE_SUCCEED){
            listLoadSucceed(jsonObject.getJSONArray("data"), iRequest.getRequestTag());
        }else{
            listLoadFail(iRequest.getRequestTag());
        }
    }

    /**
     * 加上锁是因为可能存在点击多个加载，防止列表数据错乱
     * @param data
     * @param requestTag
     */
    private synchronized void listLoadSucceed(JSONArray data, String requestTag) {
        int position = -1;
        for (MultiItemEntity entity : directoryAdapter.getData()){
            position++;
            if(entity instanceof ExpandableLevel){
                ExpandableLevel level = (ExpandableLevel) entity;
                if(requestTag.equals(level.getResource_id())){
                    List<ExpandableItem> expandableItems = mColumnPresenter.formatExpandableChildEntity(data, level.getTitle(), level.getResource_id(), level.getBigColumnId(), isHasBuy ? 1 : 0);
//                    int insertedPosition = (level.getChildPage() - 1) * level.getChildPageSize();
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
                            }
                        }
                        if(isExpanded){
                            directoryAdapter.remove(position + insertedPosition+1);
                        }else{
                            level.removeSubItem(level.getSubItems().size() - 2);
                        }
                    }else {
                        level.getSubItem(level.getSubItems().size() - 2).setLoadType(0);
                    }

                    if(isExpanded){
                        directoryAdapter.addData(position + insertedPosition+1, expandableItems);
                        directoryAdapter.notifyDataSetChanged();
                    }

                    //比较播放中的专栏是否和点击的状态相同
                    boolean resourceEquals = !TextUtils.isEmpty(level.getBigColumnId()) && AudioMediaPlayer.getAudio() != null
                            && level.getBigColumnId().equals(AudioMediaPlayer.getAudio().getBigColumnId())
                            && level.getResource_id().equals(AudioMediaPlayer.getAudio().getColumnId());
                    if(resourceEquals){
                        List<AudioPlayEntity> playList = getAudioPlayList(expandableItems, AudioPlayUtil.getInstance().getAudioList().size());
                        AudioPlayUtil.getInstance().addAudio(playList);
                    }
                    break;
                }
            }
        }
    }

    /**
     * 加上锁是因为可能存在点击多个加载，防止列表数据错乱
     * @param requestTag
     */
    private synchronized void listLoadFail(String requestTag) {
        int notifyPosition = 0;
        for (MultiItemEntity entity : directoryAdapter.getData()){
            if(entity instanceof ExpandableLevel){
                ExpandableLevel level = (ExpandableLevel) entity;
                if(requestTag.equals(level.getResource_id())){
                    level.getSubItem(0).setLoadType(3);
                    directoryAdapter.notifyItemChanged(notifyPosition + 1);
                    break;
                }
            }
            notifyPosition++;
        }
    }

    public void setHasBuy(boolean hasBuy) {
        isHasBuy = hasBuy;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_batch_download:
                clickBatchDownload();
                break;
            default:
                break;
        }
    }

    private void clickBatchDownload() {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom(getString(R.string.courses_not_purchased));
                return;
            }
            List<ColumnDirectoryEntity> newDataList = new ArrayList<ColumnDirectoryEntity>();
            for (MultiItemEntity entity : directoryAdapter.getData()){
                if(entity instanceof  ExpandableItem){
                    continue;
                }
                ExpandableLevel directoryEntity = (ExpandableLevel) entity;
                List<ColumnSecondDirectoryEntity> newChildDataList = new ArrayList<ColumnSecondDirectoryEntity>();
                int downloadCount = 0;
                for (ExpandableItem secondEntity : directoryEntity.getSubItems()){
                    ColumnSecondDirectoryEntity secondDirectoryEntity = ColumnPresenter.ExpandableItem2ColumnSecondDirectoryEntity( secondEntity);
                    if(secondDirectoryEntity.getResource_type() == 3 && !TextUtils.isEmpty(secondDirectoryEntity.getVideo_url())){
                        boolean isDownload = DownloadManager.getInstance().isDownload(secondDirectoryEntity.getApp_id(),secondDirectoryEntity.getResource_id());
                        secondDirectoryEntity.setEnable(!isDownload);
                        newChildDataList.add(secondDirectoryEntity);
                    }else if(secondDirectoryEntity.getResource_type() == 2 && !TextUtils.isEmpty(secondDirectoryEntity.getAudio_url())){
                        boolean isDownload = DownloadManager.getInstance().isDownload(secondDirectoryEntity.getApp_id(),secondDirectoryEntity.getResource_id());
                        secondDirectoryEntity.setEnable(!isDownload);
                        newChildDataList.add(secondDirectoryEntity);
                    }
                    if(!secondDirectoryEntity.isEnable()){
                        downloadCount++;
                    }
                }
                if(newChildDataList.size() > 0){
                    ColumnDirectoryEntity newDirectoryEntity = new ColumnDirectoryEntity();
                    newDirectoryEntity.setApp_id(directoryEntity.getApp_id());
                    newDirectoryEntity.setResource_type(directoryEntity.getResource_type());
                    newDirectoryEntity.setResource_id(directoryEntity.getResource_id());
                    newDirectoryEntity.setTitle(directoryEntity.getTitle());
                    newDirectoryEntity.setResource_list(newChildDataList);
                    newDirectoryEntity.setAudio_compress_url(directoryEntity.getAudio_compress_url());
                    newDirectoryEntity.setAudio_length(directoryEntity.getAudio_length());
                    newDirectoryEntity.setAudio_url(directoryEntity.getAudio_url());
                    newDirectoryEntity.setExpand(false);
                    newDirectoryEntity.setImg_url(directoryEntity.getImg_url());
                    newDirectoryEntity.setImg_url_compress(directoryEntity.getImg_url_compress());
                    newDirectoryEntity.setM3u8_url(directoryEntity.getM3u8_url());
                    newDirectoryEntity.setSelect(false);
                    newDirectoryEntity.setStart_at(directoryEntity.getStart_at());
                    newDirectoryEntity.setEnable(!(downloadCount == newChildDataList.size()));

                    newDataList.add(newDirectoryEntity);
                }
            }
            String dataJSON = JSONObject.toJSONString(newDataList);
            Intent intent = new Intent(getContext(), DownloadActivity.class);
            intent.putExtra("bundle_dataJSON", dataJSON);
            intent.putExtra("from_type", "ColumnDirectoryFragment");
            intent.putExtra("resourceId", resourceId);
            intent.putExtra("resourceType", ColumnActivity.RESOURCE_TYPE_TOPIC + "");
            intent.putExtra("down_title", ((ColumnActivity)getActivity()).title);
            startActivity(intent);
        } else {
            touristDialog.showDialog();
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
            case AudioPlayEvent.PAUSE:
            case AudioPlayEvent.PLAY:
            case AudioPlayEvent.STOP:
                AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                if(audioPlayEntity != null && !TextUtils.isEmpty(audioPlayEntity.getBigColumnId())){
                    getPlayColumnIndex(audioPlayEntity);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 获取当前播放小专栏位置，并更新条目
     * @param audioPlayEntity
     * @return
     */
    private int getPlayColumnIndex(AudioPlayEntity audioPlayEntity){
        int playParentIndex = -1;
        int playIndex = -1;
        int index = 0;
        for (MultiItemEntity val : directoryAdapter.getData()){
            if(val instanceof ExpandableLevel){
                ExpandableLevel level = (ExpandableLevel) val;
                if(!audioPlayEntity.getBigColumnId().equals(level.getBigColumnId())){
                    //播放的音频所在的大专栏不是该大专栏，所以直接退出遍历
                    break;
                }
                if(audioPlayEntity.getColumnId().equals(level.getResource_id())){
                    playParentIndex = index;
                }
            }else{
                ExpandableItem item = (ExpandableItem) val;
                if(!audioPlayEntity.getBigColumnId().equals(item.getBigColumnId()) && item.getItemType() == 1){
                    //播放的音频所在的大专栏不是该大专栏，所以直接退出遍历
                    break;
                }
                if(audioPlayEntity.getColumnId().equals(item.getColumnId()) && audioPlayEntity.getResourceId().equals(item.getResource_id())){
                    playIndex = index;
                    break;
                }
            }
            index++;
        }
        if(playParentIndex >= 0 && playIndex > 0){
            notifyDataSetChanged(playParentIndex, playIndex);
        }
        return playParentIndex;
    }

    public void clickPlayPosition(int position, boolean jumpDetail) {
        if (loginUserList.size() == 1) {
            MultiItemEntity multiItemEntity = directoryAdapter.getData().get(position);
            AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
            if(audioPlayEntity != null && !TextUtils.isEmpty(audioPlayEntity.getBigColumnId())){
                isAddPlayList = getPlayColumnIndex(audioPlayEntity) == position;
            }else{
                isAddPlayList = false;
            }
            if(multiItemEntity instanceof ExpandableLevel){
                //播放全部
                ExpandableLevel parentEntity = (ExpandableLevel) multiItemEntity;

                String bigColumnId = TextUtils.isEmpty(parentEntity.getBigColumnId()) ? "" : parentEntity.getBigColumnId();
                //比较播放中的专栏是否和点击的状态相同
                boolean resourceEquals = !TextUtils.isEmpty(bigColumnId) && audioPlayEntity != null
                        && bigColumnId.equals(audioPlayEntity.getBigColumnId())
                        && parentEntity.getResource_id().equals(audioPlayEntity.getColumnId());
                if(resourceEquals && AudioMediaPlayer.isPlaying()){
                    //点击全部暂停
                    AudioMediaPlayer.play();
                    return;
                }
                List<AudioPlayEntity> playList = getAudioPlayList(parentEntity.getSubItems(), 0);
                if(!isHasBuy){
                    toastCustom(getString(R.string.courses_not_purchased));
                    return;
                }
                if(playList.size() <= 0 ){
                    toastCustom(getString(R.string.no_audio_playback));
                    return;
                }
                //计算播放的条目位置
                int index = position;
                int tempPageIndex = 0;
                for (ExpandableItem entity : parentEntity.getSubItems()){
                    index++;
                    if(entity.getResource_type() == 2){
                        break;
                    }
                    tempPageIndex++;
                }
                recordPlayPage = (tempPageIndex / parentEntity.getChildPageSize()) + 1;
                Log.d(TAG, "clickPlayPosition: "+recordPlayPage);
                clickPlayAll(playList);
                notifyDataSetChanged(position, index);
            }else if(multiItemEntity instanceof ExpandableItem){
                //播放某一个，同时获取播放列表
                ExpandableItem playEntity = (ExpandableItem) multiItemEntity;
                if(!isHasBuy && playEntity.getIsTry() == 0){
                    toastCustom(getString(R.string.courses_not_purchased));
                    return;
                }
                if(playEntity.getResource_type() == 2){
                    //音频
                    int parentPos = directoryAdapter.getParentPosition(multiItemEntity);
                    ExpandableLevel parentEntity = (ExpandableLevel) directoryAdapter.getData().get(parentPos);
                    int index = parentEntity.getSubItems().indexOf(playEntity);
                    recordPlayPage = index / parentEntity.getChildPageSize() + 1;
                    Log.d(TAG, "clickPlayPosition: "+recordPlayPage);
                    List<AudioPlayEntity> playList = getAudioPlayList(parentEntity.getSubItems(), 0);
                    if(!isAddPlayList){
                        AudioPlayUtil.getInstance().setAudioList(playList);
                        AudioPlayUtil.getInstance().setFromTag("Topic");
                        AudioPlayUtil.getInstance().setSingleAudio(false);
                    }
                    isAddPlayList = true;
                    playPosition(playList, playEntity.getResource_id(), playEntity.getColumnId(), playEntity.getBigColumnId(), jumpDetail);
                    notifyDataSetChanged(position, position);
                }
            }
        } else {
            touristDialog.showDialog();
        }
    }

    private void clickPlayAll(List<AudioPlayEntity> playList) {
        if(playList.size() > 0){
            if(!isAddPlayList){
                AudioPlayUtil.getInstance().setAudioList(playList);
                AudioPlayUtil.getInstance().setFromTag("Topic");
                AudioPlayUtil.getInstance().setSingleAudio(false);
            }
            isAddPlayList = true;
            AudioMediaPlayer.stop();
            AudioPlayEntity playEntity = playList.get(0);
            playEntity.setPlay(true);

            playEntity.setPlayColumnPage(recordPlayPage);

            AudioMediaPlayer.setAudio(playEntity, true);
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
    }

    public void clickJumpDetail(int position, ExpandableItem itemData) {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom(getString(R.string.courses_not_purchased));
                return;
            }
            int resourceType = itemData.getResource_type();
            String resourceId = itemData.getResource_id();
            if(resourceType == 1){
                //图文
                JumpDetail.jumpImageText(getContext(), resourceId, null, itemData.getColumnId());
            }else if(resourceType == 2){
                //音频
                clickPlayPosition( position, true);
                JumpDetail.jumpAudio(getContext(), resourceId, 1);
            }else if(resourceType == 3){
                //视频
                JumpDetail.jumpVideo(getContext(), resourceId, "",false, itemData.getColumnId());
            }else{
                toastCustom(getString(R.string.unknown_course));
            }
        }else {
            touristDialog.showDialog();
        }
    }

    private void playPosition(List<AudioPlayEntity> playList, String resourceId, String columnId, String bigColumnId, boolean jumpDetail){
        boolean resourceEquals = false;
        AudioPlayEntity playAudio = AudioMediaPlayer.getAudio();
        if(playAudio != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playAudio.getResourceId(), playAudio.getColumnId(), playAudio.getBigColumnId(),
                    resourceId, columnId, bigColumnId);
            playAudio.setPlaying(!playAudio.isPlaying());
        }
        //正在播放的资源和点击的资源相同，则播放暂停操作
        if(playAudio != null && resourceEquals){
            if(AudioMediaPlayer.isStop()){
                AudioMediaPlayer.start();
            }else{
                //如果是跳转详情页，则保持播放状态,如果不是，则进行播放暂停动作
                if(!jumpDetail){
                    AudioMediaPlayer.play();
                }
            }
            return;
        }
        AudioMediaPlayer.stop();
        for (AudioPlayEntity playEntity : playList) {
            if(playEntity.getResourceId().equals(resourceId)){
                playEntity.setPlaying(!playEntity.isPlaying());
                playEntity.setPlay(true);

                playEntity.setPlayColumnPage(recordPlayPage);

                AudioMediaPlayer.setAudio(playEntity, true);
                new AudioPresenter(null).requestDetail(playEntity.getResourceId());
                break;
            }
        }
    }

    private void notifyDataSetChanged(int parentPosition, int position){
        if(position >= 0){
            directoryAdapter.notifyItemChanged(position);
        }
        if(parentPosition >= 0){
            directoryAdapter.notifyItemChanged(parentPosition);
        }
    }

    /**
     * 设置播放列表
     * @param list
     */
    private List<AudioPlayEntity> getAudioPlayList(List<ExpandableItem> list, int start){
        List<AudioPlayEntity> playList = new ArrayList<AudioPlayEntity>();
        int index = start;
        for (ExpandableItem entity : list) {
//            ExpandableItem entity = (ExpandableItem) itemEntity;
            if(entity.getResource_type() != 2){
                continue;
            }
            AudioPlayEntity playEntity = new AudioPlayEntity();
            playEntity.setAppId(entity.getApp_id());
            playEntity.setResourceId(entity.getResource_id());
            playEntity.setIndex(index);
            playEntity.setCurrentPlayState(0);
            playEntity.setTitle(entity.getTitle());
            playEntity.setState(0);
            playEntity.setPlay(false);
            playEntity.setPlayUrl(entity.getAudio_url());
            playEntity.setCode(-1);
            playEntity.setHasBuy(isHasBuy ? 1 : 0);
            playEntity.setColumnId(entity.getColumnId());
            playEntity.setBigColumnId(entity.getBigColumnId());
            playEntity.setTotalDuration(entity.getAudio_length());
            playEntity.setProductsTitle(entity.getColumnTitle());
            playEntity.setIsTry(entity.getIsTry());
            index++;
            playList.add(playEntity);
        }
        return playList;
    }

    private void clickLoadMore(int position, ExpandableItem expandableItem) {
        int parentPosition = directoryAdapter.getParentPosition(expandableItem);
        ExpandableLevel level = (ExpandableLevel) directoryAdapter.getItem(parentPosition);
        if(level  == null){
            return;
        }
        expandableItem.setLoadType(1);
        directoryAdapter.notifyItemChanged(position);

        int page = level.getChildPage() + 1;
        level.setChildPage(page);
        mColumnPresenter.requestColumnList(level.getResource_id(), "0", page, level.getChildPageSize(), false, level.getResource_id());
    }

    private void clickFailRefresh(int position, ExpandableItem expandableItem) {
        expandableItem.setLoadType(1);
        directoryAdapter.notifyItemChanged(position);

        int parentPosition = directoryAdapter.getParentPosition(expandableItem);
        ExpandableLevel level = (ExpandableLevel) directoryAdapter.getItem(parentPosition);
        if(level  == null){
            return;
        }
        expandableItem.setLoadType(1);
        directoryAdapter.notifyItemChanged(position);

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

        mColumnPresenter.requestColumnList(level.getResource_id(), "0", page, pageSize, false, level.getResource_id());
    }

    @Override
    public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
        int id = view.getId();
        if(id == R.id.expand_top_btn){
            int pos = directoryAdapter.getParentPosition(directoryAdapter.getData().get(position));
            directoryAdapter.collapse(pos);
        }else if(id == R.id.expand_down_btn){
            ExpandableLevel level = (ExpandableLevel) adapter.getItem(position);
            int itemType = level != null ? level.getSubItem(0).getItemType() : 0;
            if(level != null && !level.isExpanded()){
                directoryAdapter.expand(position);
            }
            if(itemType == 3){
                //点击展开加载数据，并发是失败加载
                clickFailRefresh(position + 1, level.getSubItem(0));
            }
        }else if(id == R.id.play_all_btn || id == R.id.btn_item_play){
            clickPlayPosition(position, false);
        }else if(id == R.id.expandable_container_child){
            clickJumpDetail(position, (ExpandableItem) directoryAdapter.getData().get(position));
        }else if(id == R.id.btn_expand_load_all){
            clickLoadMore(position, (ExpandableItem) directoryAdapter.getData().get(position));
        }else if(id == R.id.expandable_load){
            clickFailRefresh(position, (ExpandableItem) directoryAdapter.getData().get(position));
        }
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
