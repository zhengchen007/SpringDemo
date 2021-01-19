package com.olo.ding.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.olo.ding.entity.HomePageEntity;
import com.olo.ding.entity.TaskDetailEntity;
import com.olo.ding.entity.TaskDistributeEntity;

@Component
@Mapper
public interface TaskDistributeMapper {
	List<HomePageEntity> getTaskDistributeList(Map<String, Object> parmMap);
	List<TaskDistributeEntity> getNeedToCreateTask();
	List<TaskDistributeEntity> queryTaskDetail(Map<String, Object> parmMap);
	List<TaskDetailEntity>getTaskMx(Map<String, Object> parmMap);
	Integer addRwnr(Map<String, Object> parmMap);
	List<TaskDetailEntity>getPeopleName(Map<String, Object> parmMap);
	List<TaskDetailEntity>mx(Map<String, Object> map);
	List<TaskDetailEntity>queryTaskId(Map<String, Object>map); 
	void updateSfcf(Map<String, Object> map);
	void updateScsj(Map<String, Object> map);
	List<HomePageEntity>getNotCreateTask(Map<String, Object> map);
	List<TaskDistributeEntity>queryTaskDetailByUf(Map<String, Object>map);
	String test();
	List<TaskDistributeEntity> queryUfTableByUuid(Map<String, Object>map);
	void updateSfzx(Map<String, Object> map);
	List<TaskDistributeEntity>getUserId();
	List<TaskDistributeEntity>aleradyCreateworkFlow(Map<String, Object>map);
	void deleteTask(Map<String, Object> map);
	
}
