package com.olo.ding.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.olo.ding.entity.PgdEntity;
import com.olo.ding.entity.RankEntity;
@Component
@Mapper
public interface RankMapper {
	List<RankEntity> getTaskRank(Map<String, Object> map);
	List<PgdEntity> getPgdList(String dateType);
	List<PgdEntity> getPgdRank(Map<String, Object> map);
	List<PgdEntity> getHolidayCount(Map<String, Object> map);
	List<RankEntity> getDepIdAndName(Map<String, Object> map);
	List<RankEntity> getPeopleName(Map<String, Object> map);
	//个人派工单得分
	List<PgdEntity> getPersonalPgd(Map<String, Object> map);
	//个人任务得分
	List<RankEntity> getPersonalTask(Map<String, Object> map);
	//个人任务应得分
	List<RankEntity> getPersonalTaskCount(Map<String, Object> map);
	//个人派工单应得分
	List<PgdEntity>  getPersonalPgdCount(Map<String, Object> map);
	
}
