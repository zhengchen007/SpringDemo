package com.olo.ding.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.olo.ding.entity.HomePageEntity;
import com.olo.ding.entity.TaskDistributeEntity;
import com.olo.ding.mapper.TaskDistributeMapper;
import com.olo.ding.service.TaskDistributeService;
import com.olo.ding.utils.CZDateUtil;



@Service("TaskDistributeService")
@Transactional(rollbackFor = Exception.class)
public class TaskDistributeServiceImpl implements TaskDistributeService{

	@Autowired
	private TaskDistributeMapper taskDisttributemMapper;
	static SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	private static final String SUCCESSCODE="201";
	private static final String FILEDCODE = "501";
	//任务未逾期个数
	private static Integer taskWyqCount =0;
	//任务逾期个数
	private static Integer taskYqCount =0;
	@Override
	public String getDistributeList(Integer userId,Integer type,Integer taskResource) {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> map = new HashMap<String, Object>();
		//获取任务列表、计算截止时间、计算是否逾期
		Map<String, Object> parmMap = new HashMap<String, Object>();
		List<String> rwNodeId = new ArrayList<String>();
		if (type == 0) {
			parmMap.put("type", "0");
			rwNodeId.add("90550");
			rwNodeId.add("90551");
		}else {
			parmMap.put("type", "1");
			rwNodeId.add("90552");
		}
		parmMap.put("userId", userId);
		parmMap.put("nodeId", rwNodeId);
		parmMap.put("taskResource", taskResource);
		List<HomePageEntity> list = new ArrayList<HomePageEntity>();
		list = taskDisttributemMapper.getTaskDistributeList(parmMap);
		System.out.println("list==="+list.toString());
		//获取当前时间
		String nowDate = df_time.format(new Date());
		parmMap.put("nowDate", nowDate);
		List<HomePageEntity> notCreateTaskList = new ArrayList<HomePageEntity>();
		notCreateTaskList = taskDisttributemMapper.getNotCreateTask(parmMap);
		System.out.println("notCreateTaskList==="+notCreateTaskList.toString());
		//把未触发的任务添加到列表中
		list.addAll(notCreateTaskList);
	 	list.sort(Comparator.comparing(HomePageEntity::getReceivetime).reversed());
		System.out.println("list.addAll==="+list.toString());
	 	list.stream().forEach(a->{
				//如果是待办，计算截止日期是截止时间~当前时间,如果是未逾期，显示截止时间，截止时间-当前时间,如果是已逾期，显示已逾期，当前时间-截止时间
	 			//如果是已办，计算截止日期是截止时间~处理时间,如果是未逾期，显示截止时间，截止时间-流程处理时间，如果是已逾期，显示已逾期，处理时间-截止时间
	 		try {
	 			if (type == 0) {
	 				a.setIsOverTime(df_time.parse(a.getTaskDemandTime()).compareTo(df_time.parse(df_time.format(new Date())))>=0?1:0);
					if (a.getIsOverTime() == 1) {
						//未逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time.parse(a.getTaskDemandTime()),df_time.parse(df_time.format(new Date()))));
					}else if(a.getIsOverTime() == 0){
						//已逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time.parse(df_time.format(new Date())),df_time.parse(a.getTaskDemandTime())));
					} 
				}else if(type == 1) {
	 				a.setIsOverTime(df_time.parse(a.getTaskDemandTime()).compareTo(df_time.parse(a.getOperateDateAndTime()))>=0?1:0);
	 				System.out.println(df_time.parse(a.getTaskDemandTime()).compareTo(df_time.parse(a.getOperateDateAndTime()))>=0?1:0);
	 				if (a.getIsOverTime() == 1) {
						//未逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time.parse(a.getTaskDemandTime()),df_time.parse(a.getOperateDateAndTime())));
					}else if(a.getIsOverTime() == 0){
						//已逾期
						a.setCutoffTime(CZDateUtil.getDatePoor(df_time.parse(a.getOperateDateAndTime()),df_time.parse(a.getTaskDemandTime())));
					} 
				}
				
				if (a.getIsOverTime()==0) {
					taskYqCount = taskYqCount+1;
				}else if(a.getIsOverTime()==1) {
					taskWyqCount = taskWyqCount+1;
				}
	 		} catch (Exception e) {
				// TODO: handle exception
	 			System.out.println(e);
			}
	 	});
	 	map.put("rw", list);
		map.put("isNotOverTimeCount",taskWyqCount);
	 	map.put("isOverTimeCount",taskYqCount);
	 	returnMap.put("code",SUCCESSCODE);
	 	returnMap.put("data",map);
	 	JSONObject jsonObj = new JSONObject(returnMap);
	 	taskYqCount = 0;
	 	taskWyqCount = 0;
		return JSONObject.toJSONString(jsonObj,SerializerFeature.WriteMapNullValue);
		} catch (Exception e2) {
			// TODO: handle exception
			taskYqCount = 0;
		 	taskWyqCount = 0;
		 	returnMap.put("code",FILEDCODE);
		 	JSONObject jsonObj = new JSONObject(returnMap);
		 	return JSONObject.toJSONString(jsonObj,SerializerFeature.WriteMapNullValue);
		}
	}
	

	public String deleteNotPf(TaskDistributeEntity taskDistributeEntity) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("taskId",taskDistributeEntity.getTaskId());
			taskDisttributemMapper.deleteTask(map);
			returnMap.put("code", SUCCESSCODE);
			JSONObject jsonObj = new JSONObject(returnMap);
			return JSONObject.toJSONString(jsonObj,SerializerFeature.WriteMapNullValue);
		} catch (Exception e) {
			returnMap.put("code", FILEDCODE);
			JSONObject jsonObj = new JSONObject(returnMap);
			return JSONObject.toJSONString(jsonObj,SerializerFeature.WriteMapNullValue);
		}
		
	}
	
	
	public String test() {
		return "success";
	}
	
}
