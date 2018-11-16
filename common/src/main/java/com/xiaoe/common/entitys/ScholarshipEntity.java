package com.xiaoe.common.entitys;

// 奖学金实体类
public class ScholarshipEntity {

    // 奖学金任务状态
    public static final int TASK_RECEIVED = 1; // 已领取
    public static final int TASK_UNFINISHED = 2; // 未完成（需要判断是否已购买）
    public static final int TASK_NOT_RECEIVED = 3; // 未领取

    // 奖学金发放状态
    public static final int SCHOLARSHIP_ISSUED = 3; // 已发放
    public static final int SCHOLARSHIP_PROCESSING = 2; // 处理中
    public static final int SCHOLARSHIP_FAIL = 1; // 未发放

    private String taskId; // 任务 id
    private int taskState; // 任务状态
    private int issueState; // 发放状态
    private String taskDetailId; // 任务详情 id，用于请求发放状态

    private static ScholarshipEntity scholarshipEntity = null;

    private ScholarshipEntity () {}

    public static ScholarshipEntity getInstance() {
        if (scholarshipEntity == null) {
            synchronized (ScholarshipEntity.class) {
                if (scholarshipEntity == null) {
                    scholarshipEntity = new ScholarshipEntity();
                }
            }
        }
        return scholarshipEntity;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public void setTaskState(int taskState) {
        this.taskState = taskState;
    }

    public void setIssueState(int issueState) {
        this.issueState = issueState;
    }

    public void setTaskDetailId(String taskDetailId) {
        this.taskDetailId = taskDetailId;
    }

    public String getTaskId() {

        return taskId;
    }

    public int getTaskState() {
        return taskState;
    }

    public int getIssueState() {
        return issueState;
    }

    public String getTaskDetailId() {
        return taskDetailId;
    }
}
