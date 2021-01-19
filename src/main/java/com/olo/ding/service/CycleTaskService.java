package com.olo.ding.service;

import com.olo.ding.entity.TaskDistributeEntity;

public interface CycleTaskService {
	
	 String queryCycleTaskDetail(String taskId);
	 String queryCrycleTaskList(Integer userId);
	 String aheadofStop(String taskId);
	 String modify(TaskDistributeEntity distributeEntity);
	 String modifyDeatil(String taskId);
}
