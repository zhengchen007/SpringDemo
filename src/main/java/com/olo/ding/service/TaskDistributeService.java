package com.olo.ding.service;

import com.olo.ding.entity.TaskDistributeEntity;

public interface TaskDistributeService {
	String getDistributeList(Integer userId,Integer type,Integer taskResource);
	String deleteNotPf(TaskDistributeEntity taskDistributeEntity);
}
