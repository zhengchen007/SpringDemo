package com.olo.ding.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.olo.ding.entity.ModyftEntity;
import com.olo.ding.entity.TaskDistributeEntity;

@Component
@Mapper
public interface CycleTaskMapper {
	List<TaskDistributeEntity>getCycleTaskDistributeList(Map<String, Object> map);
	List<TaskDistributeEntity> getCycleTaskForm(Map<String, Object> map); 
	List<TaskDistributeEntity> getCycleTaskList(Map<String, Object> map); 
	List<TaskDistributeEntity> getCycleTaskRwnr(Map<String, Object> map); 
	List<TaskDistributeEntity> getTriggeredCount(Map<String, Object> map);
	void aheadofStop(Map<String, Object> map);
	void modify(Map<String, Object> map);
	List<ModyftEntity> modifyDeatil(Map<String, Object> map);
	List<TaskDistributeEntity>getItemTime(Map<String, Object> map);
	List<TaskDistributeEntity>queryJobName(Map<String, Object> map);
	void updateJob(Map<String, Object> map);
	void updateJduuid(Map<String, Object> map);
}
