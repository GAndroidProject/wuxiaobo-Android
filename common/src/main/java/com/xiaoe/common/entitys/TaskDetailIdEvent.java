package com.xiaoe.common.entitys;

public class TaskDetailIdEvent {

    private String taskDetailId;

    public TaskDetailIdEvent(String taskDetailId) {
        this.taskDetailId = taskDetailId;
    }

    public void setTaskDetailId(String taskDetailId) {
        this.taskDetailId = taskDetailId;
    }

    public String getTaskDetailId() {

        return taskDetailId;
    }
}
