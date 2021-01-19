package com.olo.ding.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.olo.ding.entity.DealTaskEntity;
import com.olo.ding.entity.HomePageEntity;

@Component
@Mapper
public interface DealTaskMapper {
	List<DealTaskEntity>getTaskDetail(Map<String, Object> map);
	List<DealTaskEntity>getTaskDetailMx(Map<String, Object> map);
	List<DealTaskEntity>getPgdDetail(Map<String, Object> map);
	void updateZxrbzAndfj(Map<String, Object> map);
	void updateJdrbzAndfj(Map<String, Object> map);
	void updatePgd(Map<String, Object> map);
	List<DealTaskEntity>queryAddr(Map<String, Object> map); 
	List<DealTaskEntity>queryZxrfj(Map<String, Object> map); 
	void update_formtable_main_836(Map<String, Object> map);
	void update_formtable_main_836_jdrfj(Map<String, Object> map);
	List<DealTaskEntity>queryNotCfDetail(Map<String, Object> map);
	List<HomePageEntity>isOverTime(Map<String, Object> map);
	void updateJzrq(Map<String, Object> map);
	void updatezxrfj(Map<String, Object> map);
	void updatejdrfj(Map<String, Object> map);

}
