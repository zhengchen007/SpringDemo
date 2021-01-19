package com.olo.ding.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.druid.util.StringUtils;
import com.google.gson.Gson;
import com.olo.ding.entity.TaskDetailEntity;
import com.olo.ding.entity.TaskDistributeEntity;
import com.olo.ding.mapper.TaskDistributeMapper;
import com.olo.ding.service.AddTaskService;
import com.olo.ding.utils.CZDateUtil;
import com.olo.ding.utils.HttpRequester;
import com.olo.ding.utils.JsonToMap;
import com.olo.ding.utils.MapToJson;
import com.olo.ding.utils.WeChatService;

import cn.com.weaver.services.webservices.WorkflowServicePortTypeProxy;
import localhost.services.SaveModeData.SaveModeDataPortTypeProxy;
import net.sf.json.JSONObject;
import weaver.workflow.webservices.WorkflowBaseInfo;
import weaver.workflow.webservices.WorkflowMainTableInfo;
import weaver.workflow.webservices.WorkflowRequestInfo;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;
@Service("AddTaskService")
@Transactional(rollbackFor = Exception.class)
public class AddTaskServiceImpl implements AddTaskService{
	private static final String SUCCESSCODE="201";
	private static final String FILEDCODE = "501";
	static SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
	static final String url_zs_hd =  "http://crmmobile1.olo-home.com:8038/oloDingDing/taskDistribute/addZqTask";
	static final String url_zs_add = "http://crmmobile1.olo-home.com:8038/quartz/quartz/httpJob/add";
	static final String url_zs_del = "http://crmmobile1.olo-home.com:8038/quartz/job/delete?jobName=";
	static final String url_cs_hd =  "http://10.201.0.65:8080/oloDingDing/taskDistribute/addZqTask";
	static final String url_cs_add = "http://10.201.0.65:8089/quartz/httpJob/add";
	static final String url_cs_del = "http://10.201.0.65:8089/quartz/job/delete?jobName=";

	/*
	 * 可添加的任务类型分为：上下级，项目专项，会议（非周期性）定时任务执行 周期要务（周期性任务），定时任务执行
	 */

	@Autowired
	TaskDistributeMapper distributeMapper;
	public String addMission(TaskDistributeEntity taskDistributeEntity) throws Exception {
		//获取当前日期
		String res = "";
		String nowDate = df_time.format(new Date());
		// 根据sfwzq判断是否为周期任务,sfwzq=true 是周期任务，sfwzq=false 不是周期任务
		SaveModeDataPortTypeProxy proxy = new SaveModeDataPortTypeProxy();
		Map<String, String> map = new HashMap<String, String>();
		map.put("sfwzq", taskDistributeEntity.getSfwzq());
		map.put("rwksrq", taskDistributeEntity.getRwksrq());
		map.put("rwkssj", taskDistributeEntity.getRwkssj());
		map.put("rwjsrq", taskDistributeEntity.getRwjsrq());
		map.put("rwjssj", taskDistributeEntity.getRwjssj());
		map.put("rwmc", taskDistributeEntity.getRwmc());
		map.put("cjr", taskDistributeEntity.getCjr());
		map.put("zxr", taskDistributeEntity.getZxr());
		map.put("jdr", taskDistributeEntity.getJdr());
		map.put("xzr", taskDistributeEntity.getXzr());
		map.put("rwfj", taskDistributeEntity.getRwfj());
		map.put("rwly", taskDistributeEntity.getTaskResource());
	
		if ("0".equals(taskDistributeEntity.getSfwzq())) {
			// 将周期要务的数据存到建模中
			map.put("rwly", "2");
			map.put("cfzq", taskDistributeEntity.getCfzq());
			List<String> zyyqscList = new ArrayList<String>();
			Map<String,Object> zykssj = JsonToMap.JsonToMap(taskDistributeEntity.getZykssj());
			Integer month = (Integer) zykssj.get("month");
			Integer week = (Integer) zykssj.get("week");
			Integer day = (Integer) zykssj.get("day");
			Integer hour = (Integer) zykssj.get("hour");
			Integer mins = (Integer) zykssj.get("mins");
			zyyqscList  = Arrays.asList(taskDistributeEntity.getZyyqsc().split(","));
			//map.put("taskCreatedate", taskDistributeEntity.getRwksrq() + "~" + taskDistributeEntity.getRwjsrq());  
			SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
			/***************************新增字段开始**************************************************/
			//期数
			map.put("periods", taskDistributeEntity.getPeriods().toString());
			//周期开始时间
			map.put("cycleStartDate", taskDistributeEntity.getCycleStartDate());
			//作业要求时长
			map.put("zyyqsc", taskDistributeEntity.getZyyqscStr());
			//作业开始时间
			map.put("zykssj", taskDistributeEntity.getBeginTimeStr());
			//作业要求时长json
			map.put("zyyqscJson", taskDistributeEntity.getZyyqsc());
			//作业开始时间json
			map.put("zykssjJson", taskDistributeEntity.getZykssj());
			//作业开始时间positiveType
			map.put("positiveType", taskDistributeEntity.getPositiveType());
			//创建时间
			map.put("zqywcjsj", df.format(new Date()));
			//间隔
			map.put("sjjg",String.valueOf(taskDistributeEntity.getIntervalNum()));
			/***************************新增字段结束**************************************************/
			
			/***************************拼接cron开始**************************************************/
			String[] cronList = new String[taskDistributeEntity.getPeriods()];
			String cron = "";
			String zzCron = "";
			String cycleStartDate = taskDistributeEntity.getCycleStartDate();
			String[] zykssjArray = new String[5];
			zykssjArray[0] = CZDateUtil.bw(month);
			zykssjArray[1] = CZDateUtil.bw(week);
			zykssjArray[2] = CZDateUtil.bw(day);
			zykssjArray[3] = CZDateUtil.bw(7);
			zykssjArray[4] = CZDateUtil.bw(30);
			
			String jobName = "";
			String zzJobName = "";
			//参数为生成的uuid，并且把uuid存储到业务表中，uf_olo_ding_task的zqywUuid
			String uuid = UUID.randomUUID().toString().replaceAll("-","");
			if ("0".equals(taskDistributeEntity.getCfzq())) {
				//拼接重复周期为每天的cron表达式(周期开始时间+重复周期+期数=cron)，提前一天的11点触发
				//Seconds Minutes Hours DayofMonth Month DayofWeek Year
				String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 0, zykssjArray);
				//截止日期计算=开始日期+(期数-1)*每隔几天
				String lastTimeTaskDate = "";
				if (taskDistributeEntity.getIntervalNum()>0) {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (taskDistributeEntity.getPeriods())*(taskDistributeEntity.getIntervalNum()+1),-1));
				}else {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), taskDistributeEntity.getPeriods(),-1));
				}
				cron = "0 "+"30"+" "+"07"+" * * ?";
				//拼接终止当前周期任务的cron,周期截止日期再加一个周期的日期
				zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
				map.put("sjjg", String.valueOf(taskDistributeEntity.getIntervalNum()));
				map.put("dyckssj", firstTaskDate);
				map.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate);
				map.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 0, zykssjArray));
			}else if ("1".equals(taskDistributeEntity.getCfzq())) {
				//每周拼接重复周期为每周的cron表达式
				//第一次触发时间
				String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 1, zykssjArray);
				//截止日期计算=开始日期+(期数-1)*(每隔几天*7)
				String lastTimeTaskDate = "";
				if (taskDistributeEntity.getIntervalNum()>0) {
					lastTimeTaskDate =  df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (taskDistributeEntity.getPeriods())*((taskDistributeEntity.getIntervalNum()+1)*7),-1));
				}else {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), taskDistributeEntity.getPeriods()*7,-1));
				}
				Integer actWeek = 0;
				switch (week) {
				case 1:
					actWeek = 2;
					break;
				case 2:
					actWeek = 3;
					break;
				case 3:
					actWeek = 4;
					break;
				case 4:
					actWeek = 5;
					break;
				case 5:
					actWeek = 6;
					break;
				case 6:
					actWeek = 7;
					break;
				case 7:
					actWeek = 1;
					break;
				default:
					break;
				}
				cron = "0 "+"30"+" "+"07"+" ? * "+actWeek;
				zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
				map.put("sjjg", String.valueOf(taskDistributeEntity.getIntervalNum()*7));
				map.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
				map.put("dyckssj", firstTaskDate);
				map.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 1, zykssjArray));
			}else if ("2".equals(taskDistributeEntity.getCfzq())) {
				//每月拼接重复周期为每月的cron表达式
				if ("0".equals(taskDistributeEntity.getPositiveType())) {
					//1~28
					String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 2, zykssjArray);
					String lastTimeTaskDate = "";
					if (taskDistributeEntity.getIntervalNum()>0) {
						lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (taskDistributeEntity.getPeriods())*(taskDistributeEntity.getIntervalNum()+1),0));
					}else {
						lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), taskDistributeEntity.getPeriods(),0));
					}
					cron = "0 "+"30"+" "+"07"+" "+day+" * ?";
					zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
					map.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
					map.put("dyckssj", firstTaskDate);
					map.put("cron",cron);
				}else if ("1".equals(taskDistributeEntity.getPositiveType())) {
					//倒数3天
					//获取当前日期计算出倒数3天对应的日期
					String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 2, zykssjArray);
					String lastTimeTaskDate = "";
					if (taskDistributeEntity.getIntervalNum()>0) {
						lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (taskDistributeEntity.getPeriods())*(taskDistributeEntity.getIntervalNum()+1),0));
					}else {
						lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), taskDistributeEntity.getPeriods(),0));
					}
					cron = "0 "+"30"+" "+"07"+" "+CZDateUtil.getDate(day)+" * ?";
					zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
					map.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
					map.put("dyckssj", firstTaskDate);
					map.put("cron",cron);
				}
			}else if ("3".equals(taskDistributeEntity.getCfzq())) {
				//计算出每一期任务，生成n个任务
				//算出第一期的执行时间
				SimpleDateFormat df_jd = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");// 设置日期格式
				if ("0".equals(taskDistributeEntity.getPositiveType())) {
					
				}else if ("1".equals(taskDistributeEntity.getPositiveType())) {
					String 	rq	= CZDateUtil.getDate(Integer.parseInt(zykssjArray[2]));
					zykssjArray[2]  = rq.substring(rq.lastIndexOf("-")+1);
				}
				String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 3, zykssjArray);
				
				String startTimeTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 3, zykssjArray);
				String tempTime = "";
				String lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), taskDistributeEntity.getPeriods(),1));
				map.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
				map.put("dyckssj", firstTaskDate);
				if ("0".equals(taskDistributeEntity.getPositiveType())) {
					cron = "0 "+"30"+" "+"07"+" "+day+" * ?";
					map.put("cron",cron);
					
				}else if ("1".equals(taskDistributeEntity.getPositiveType())) {
					cron = "0 "+"30"+" "+"07"+" "+CZDateUtil.getDate(day)+" * ?";
					map.put("cron",cron);
					
				}
				//将uf_olo_ding_task中的sfwjd（是否为季度）更新为0
				map.put("sfwjd", "0");
				for (int i = 0; i < cronList.length; i++) {
					if (i == 0) {
						cronList[i] = CZDateUtil.getCron(df_jd.parse(startTimeTaskDate+":"+"00"));
						tempTime = startTimeTaskDate;
					}else {
						if (tempTime.length()<=16) {
							tempTime = tempTime+":00";
						}
						cronList[i] = CZDateUtil.getCron(CZDateUtil.getNextJd(df_jd.parse(tempTime)));
						tempTime = df_jd.format(CZDateUtil.getNextJd(df_jd.parse(tempTime)));
					}
				}
				String jduuid = "";
				
				for (int i = 0; i < cronList.length; i++) {
					String jduuid_taskname = UUID.randomUUID().toString().replaceAll("-","");
					Map<String, Object> uuidMap = new HashMap<String, Object>();
					uuidMap.put("uuid", uuid);
					Map<String, Object> bodyMap_del = new HashMap<String, Object>();
					bodyMap_del.put("jobName",taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+jduuid_taskname);
					bodyMap_del.put("jobGroup","周期任务"+jduuid_taskname);
					bodyMap_del.put("description",taskDistributeEntity.getRwmc()+df.format(new Date()));
					bodyMap_del.put("requestType","GET");
					bodyMap_del.put("url",url_zs_hd);
					bodyMap_del.put("params",MapToJson.mapToJson(uuidMap).toString());
					bodyMap_del.put("cronExpression",cronList[i]);
					HttpRequester.requestPOST(url_zs_add, MapToJson.mapToJson(bodyMap_del).toString(), "application/json;charset=utf-8");
					jobName = taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+jduuid_taskname;
					jduuid = jduuid+taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+jduuid_taskname+",";
				}
				map.put("jduuid", jduuid.substring(0,jduuid.length()-1));
				
			}
			/***************************拼接cron结束**************************************************/
			
			/***************************调用调度服务接口插入数据开始*************************************************/
		
			if (!"3".equals(taskDistributeEntity.getCfzq())) {
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			Map<String, Object> uuidMap = new HashMap<String, Object>();
			uuidMap.put("uuid", uuid);
			bodyMap.put("jobName",taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+uuid);
			bodyMap.put("jobGroup","周期要务"+uuid);
			bodyMap.put("description",taskDistributeEntity.getRwmc()+df.format(new Date()));
			bodyMap.put("requestType","GET");
			bodyMap.put("url",url_zs_hd);
			bodyMap.put("params",MapToJson.mapToJson(uuidMap).toString());
			bodyMap.put("cronExpression",cron);
			HttpRequester.requestPOST(url_zs_add, MapToJson.mapToJson(bodyMap).toString(), "application/json;charset=utf-8");
			jobName = taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+uuid;
			}
			/***************************调用调度服务接口插入数据结束**************************************************/
			
			/***************************调用调度服务接口插入结束周期要务Begin**************************************************/
			//参数为生成的uuid，并且把uuid存储到业务表中，uf_olo_ding_task的zqywUuid
			if (!"3".equals(taskDistributeEntity.getCfzq())) {
				String uuid_del = UUID.randomUUID().toString().replaceAll("-","");
				Map<String, Object> bodyMap_del = new HashMap<String, Object>();
				Map<String, Object> parmMap = new HashMap<String, Object>();
				parmMap.put("jobName", taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+uuid);
				parmMap.put("jobGroup", "周期要务"+uuid);
				bodyMap_del.put("jobName","终止---"+taskDistributeEntity.getRwmc()+df.format(new Date())+uuid_del);
				bodyMap_del.put("jobGroup","终止周期要务---"+uuid_del);
				bodyMap_del.put("description","终止---"+taskDistributeEntity.getRwmc()+df.format(new Date()));
				bodyMap_del.put("requestType","POST_JSON");
				bodyMap_del.put("url",url_zs_del+taskDistributeEntity.getRwmc()+df.format(new Date())+"-"+uuid
						+"&jobGroup="+"周期要务"+uuid);
				bodyMap_del.put("params",parmMap);
				bodyMap_del.put("cronExpression",zzCron);
				HttpRequester.requestPOST(url_zs_add, MapToJson.mapToJson(bodyMap_del).toString(), "application/json;charset=utf-8");
				zzJobName = "终止---"+taskDistributeEntity.getRwmc()+df.format(new Date())+uuid_del;
			}
			/***************************调用调度服务接口插入结束周期要务End**************************************************/
			  List<String> rwnrList = taskDistributeEntity.getRwnr();
				String rwnr = "";
				for (int i = 0; i < rwnrList.size(); i++) {
					rwnr += rwnrList.get(i) + "&";
				}
			//jobName
			map.put("jobName",jobName);
			//jobGroup
			map.put("zzJobName", zzJobName);
			map.put("rwnr", rwnr.substring(0, rwnr.length()-1));
			map.put("zqywUuid",uuid);
			net.sf.json.JSONObject obj = MapToJson.mapToJson(map);
			Stack<JSONObject> stObj = new Stack<JSONObject>();
			stObj.push(obj);
			String str = stObj.toString();
			//测试环境90742
			//正式环境103742
			res = String
					.valueOf(proxy.createYxb("103742", String.valueOf(taskDistributeEntity.getCjr()), str, ""));
		} else {
			
			List<String> rwnrList = taskDistributeEntity.getRwnr();
			String rwnr = "";
			for (int i = 0; i < rwnrList.size(); i++) {
				rwnr += rwnrList.get(i) + "&";
			}
			map.put("rwnr", rwnr.substring(0, rwnr.length()-1));
			map.put("cfzq", "");
			// 非周期任务直接把数据插入到uf_olo_ding_task表中
			net.sf.json.JSONObject obj = MapToJson.mapToJson(map);
			Stack<JSONObject> stObj = new Stack<JSONObject>();
			stObj.push(obj);
			String str = stObj.toString();
			res = String
					.valueOf(proxy.createYxb("103742", String.valueOf(taskDistributeEntity.getCjr()), str, ""));
			System.out.println(res);
		}
		/*********************如果任务的开始日期是当天的话，周期要务和非周期要务都要触发，但是周期要务要插入到建模之中BEGIN*****************************************/
		
	
		String lccfzt = "true";
		if (nowDate.equals(taskDistributeEntity.getRwksrq())&&!"0".equals(taskDistributeEntity.getSfwzq())) {
			taskDistributeEntity.setTaskId(Integer.parseInt(res));
			taskDistributeEntity.setRwjmid(res);
			List<String> requestIdlist = this.createTaskWorkflow(taskDistributeEntity);
			for (int i = 0; i < requestIdlist.size(); i++) {
				if (Integer.parseInt(requestIdlist.get(i))<=0) {
					lccfzt = "false";
				}
			}
		}
		/*********************如果任务的开始日期是当天的话，周期要务和非周期要务都要触发，但是周期要务要插入到建模之中END*****************************************/
		Map<String, Object> returnMap = new HashMap<String, Object>();
		returnMap.put("isCreateWorkFlow",lccfzt);
		returnMap.put("code", SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();

	}

/*
 * 触发任务流程
 */
	
	WorkflowServicePortTypeProxy	proxy	= new WorkflowServicePortTypeProxy();
	@Autowired
	TaskDistributeMapper taskDistributeMapper;
	@Autowired
	TaskDistributeMapper addTaskMapper;
	public List<String> createTaskWorkflow(TaskDistributeEntity taskDistributeEntity) {
		
		List<String> returnList = new ArrayList<String>();
		// 生成任务流程
		SimpleDateFormat formatter_Date = new SimpleDateFormat("yyyy-MM-dd");
		Date date_now = new Date(System.currentTimeMillis());
		String date_now_str = formatter_Date.format(date_now);
		String zxr = taskDistributeEntity.getZxr();
		String jdr = taskDistributeEntity.getJdr();
		String xzrStr = taskDistributeEntity.getXzr();
		List<String> xzr =  Arrays.asList(xzrStr.split(",")); 
		String missionStartTime = "";
		String missionEndTime = "";
		if ("0".equals(taskDistributeEntity.getSfwzq())) {
			String zyyqscJson = taskDistributeEntity.getZyyqscJson();
			String zykssjJson = taskDistributeEntity.getZykssjJson();
			Map<String, Object> map = new HashMap<String, Object>();
			Gson gson = new Gson();
			map = gson.fromJson(zykssjJson, map.getClass());
			missionStartTime = date_now_str+" "+CZDateUtil.bw((Integer)map.get("hour"))+":"+CZDateUtil.bw((Integer)map.get("mins"));
			//作业时长
			List<String> zyscList = Arrays.asList(zyyqscJson.split(","));
			String jzsj = CZDateUtil.getJzDate(zyscList, missionStartTime);
			if ("-1".equals(jzsj)) {
				missionEndTime = jzsj;
			}
		}else {
			 missionStartTime = date_now_str+" "+taskDistributeEntity.getRwkssj();
			 missionEndTime = taskDistributeEntity.getRwjsrq()+" "+taskDistributeEntity.getRwjssj();
		}
		
		String cjr = taskDistributeEntity.getCjr();
		String xzrName = Optional.ofNullable(xzr).orElse(Arrays.asList("")).stream().collect(Collectors.joining(","));
		// 任务描述
		List<String> rwnr = taskDistributeEntity.getRwnr();
		String rwfjStr = taskDistributeEntity.getRwfj();
		//List<Object> rwfjObj = JSONObject.parseArray(rwfjStr);
	    //List<String> rwfjArray = rwfjObj.stream().map(Object::toString).collect(Collectors.toList());
		List<String> rwfjArray = Arrays.asList(rwfjStr.substring(1,rwfjStr.length()-1).split(","));
		// 主字段
		List<String> zxrList = Arrays.asList(zxr.split(","));
		for (int j = 0; j < zxrList.size(); j++) {
			WorkflowRequestTableField[] mainTableFields = new WorkflowRequestTableField[13]; // 字段信息
			mainTableFields[0] = new WorkflowRequestTableField();
			mainTableFields[0].setFieldName("name");//
			mainTableFields[0].setFieldValue(taskDistributeEntity.getRwmc());//
			mainTableFields[0].setView(true);// 字段是否可见
			mainTableFields[0].setEdit(true);// 字段是否可编辑
			mainTableFields[1] = new WorkflowRequestTableField();
			mainTableFields[1].setFieldName("zxr");//
			mainTableFields[1].setFieldValue(zxrList.get(j));//
			mainTableFields[1].setView(true);// 字段是否可见
			mainTableFields[1].setEdit(true);// 字段是否可编辑

			mainTableFields[2] = new WorkflowRequestTableField();
			mainTableFields[2].setFieldName("jdr");//
			mainTableFields[2].setFieldValue(jdr);//
			mainTableFields[2].setView(true);// 字段是否可见
			mainTableFields[2].setEdit(true);// 字段是否可编辑

			mainTableFields[3] = new WorkflowRequestTableField();
			mainTableFields[3].setFieldName("xzr");//
			mainTableFields[3].setFieldValue(xzrName);//
			mainTableFields[3].setView(true);// 字段是否可见
			mainTableFields[3].setEdit(true);// 字段是否可编辑

			mainTableFields[4] = new WorkflowRequestTableField();
			mainTableFields[4].setFieldName("re_cjr");//
			mainTableFields[4].setFieldValue(cjr);//
			mainTableFields[4].setView(true);// 字段是否可见
			mainTableFields[4].setEdit(true);// 字段是否可编辑

			mainTableFields[5] = new WorkflowRequestTableField();
			mainTableFields[5].setFieldName("rwksrq");//
			mainTableFields[5].setFieldValue(missionStartTime);//
			mainTableFields[5].setView(true);// 字段是否可见
			mainTableFields[5].setEdit(true);// 字段是否可编辑

			mainTableFields[6] = new WorkflowRequestTableField();
			mainTableFields[6].setFieldName("rwjzrq");//
			mainTableFields[6].setFieldValue(missionEndTime);//
			mainTableFields[6].setView(true);// 字段是否可见
			mainTableFields[6].setEdit(true);// 字段是否可编辑

			String fileNameStr = "";
			String fileUrlStr = "";
			String head = "http:";
			String url = "";
			String name = "";

			if (rwfjArray != null && rwfjArray.size() > 0) {
				for (int g = 0; g < rwfjArray.size(); g++) {
					fileNameStr += (head + rwfjArray.get(g).substring(rwfjArray.get(g).lastIndexOf("/") + 1,
							rwfjArray.get(g).length())) + "|";
					fileUrlStr += rwfjArray.get(g)+ "|";
				}
				url = fileUrlStr.substring(0, fileUrlStr.length() - 1);
				name = fileNameStr.substring(0, fileNameStr.length() - 1);
			}

			System.out.println("===============同步url为===============" + url);
			System.out.println("===============同步fileName为===============" + name);
			mainTableFields[7] = new WorkflowRequestTableField();
			mainTableFields[7].setFieldName("rwfj");//
			mainTableFields[7].setFieldType(name);// http:开头代表该字段为附件字段
			mainTableFields[7].setFieldValue(url);// 附件地址
			mainTableFields[7].setView(true);// 字段是否可见 
			mainTableFields[7].setEdit(true);// 字段是否可编辑

			
			mainTableFields[8] = new WorkflowRequestTableField();
			mainTableFields[8].setFieldName("rwfjdz");//
			mainTableFields[8].setFieldValue(url);//
			mainTableFields[8].setView(true);// 字段是否可见
			mainTableFields[8].setEdit(true);// 字段是否可编辑
			    
			mainTableFields[9] = new WorkflowRequestTableField();
			mainTableFields[9].setFieldName("cfzq");//
			mainTableFields[9].setFieldValue(taskDistributeEntity.getCfzq());//
			mainTableFields[9].setView(true);// 字段是否可见
			mainTableFields[9].setEdit(true);// 字段是否可编辑
			
			mainTableFields[10] = new WorkflowRequestTableField();
			mainTableFields[10].setFieldName("taskResource");//
			mainTableFields[10].setFieldValue(taskDistributeEntity.getTaskResource());//
			mainTableFields[10].setView(true);// 字段是否可见
			mainTableFields[10].setEdit(true);// 字段是否可编辑
			
			
			mainTableFields[11] = new WorkflowRequestTableField();
			mainTableFields[11].setFieldName("rwjmid");//
			mainTableFields[11].setFieldValue(taskDistributeEntity.getRwjmid());//
			mainTableFields[11].setView(true);// 字段是否可见
			mainTableFields[11].setEdit(true);// 字段是否可编辑
			
			
			mainTableFields[12] = new WorkflowRequestTableField();
			mainTableFields[12].setFieldName("lcyy");//
			mainTableFields[12].setFieldValue("0");//
			mainTableFields[12].setView(true);// 字段是否可见
			mainTableFields[12].setEdit(true);// 字段是否可编辑
			
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
			String requestName = taskDistributeEntity.getRwmc();

			WorkflowRequestTableRecord[] mainTableRecords = new WorkflowRequestTableRecord[1];
			mainTableRecords[0] = new WorkflowRequestTableRecord();
			mainTableRecords[0].setWorkflowRequestTableFields(mainTableFields);
			WorkflowMainTableInfo mainTableInfo = new WorkflowMainTableInfo();
			mainTableInfo.setRequestRecords(mainTableRecords);

			WorkflowBaseInfo baseInfo = new WorkflowBaseInfo();
			//正式环境65542
			//测试环境65542
			baseInfo.setWorkflowId("65542");
			WorkflowRequestInfo requestInfo = new WorkflowRequestInfo();
			requestInfo.setCreatorId("1");
			requestInfo.setRequestLevel("1");
			requestInfo.setRequestName(requestName);
			requestInfo.setWorkflowMainTableInfo(mainTableInfo);
			requestInfo.setWorkflowBaseInfo(baseInfo);
			try {
				String result = proxy.doCreateWorkflowRequest(requestInfo, 1);
				//抄送
				WeChatService.newSendGet(xzrName, "您有一条新的任务，任务名称为："+taskDistributeEntity.getRwmc()+",请登录我乐钉钉及时处理。");
				Map<String, Object> parmTaskidMap = new HashMap<String, Object>();
				parmTaskidMap.put("requestId", result);
				List<TaskDetailEntity> listId = addTaskMapper.queryTaskId(parmTaskidMap);
				//把任务内容插入任务流程明细表
					Map<String, Object> parmRwnr = new HashMap<String, Object>();
					for (int i = 0; i < rwnr.size(); i++) {
						if (!StringUtils.isEmpty(rwnr.get(i))&&!"null".equals(rwnr.get(i))&&rwnr.get(i)!=null) {
							parmRwnr.clear();
							parmRwnr.put("mainId", listId.get(0).getId());
							parmRwnr.put("rwnr", rwnr.get(i));
							addTaskMapper.addRwnr(parmRwnr);
						}
					}
					
					//如果是非周期任务,更新uf_olo_ding_task中的sfcf（是否触发任务）
					if ("1".equals(taskDistributeEntity.getSfwzq())) {
						Map<String, Object> updateParm = new HashMap<String,Object>();
						updateParm.put("taskId", taskDistributeEntity.getTaskId());
						taskDistributeMapper.updateSfcf(updateParm);
					}
					//如果是周期任务，更新uf_olo_ding_task中的cfsj（触发任务的时间）
					if ("0".equals(taskDistributeEntity.getSfwzq())) {
						Map<String, Object> updateParm = new HashMap<String,Object>();
						updateParm.put("cfsj", date_now_str+",");
						updateParm.put("taskId", taskDistributeEntity.getTaskId());
						taskDistributeMapper.updateScsj(updateParm);
					}
					returnList.add(result);
				
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return returnList;
	}
	
	
/*
 * 时间格式化
 */
public static String dealDateFormat(String oldDate) {
       Date date1 = null;
       DateFormat df2 = null;
       try {
           oldDate= oldDate.replace("Z", " UTC");
           DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z");
           Date date = df.parse(oldDate);
           SimpleDateFormat df1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
           date1 = df1.parse(date.toString());
           df2 = new SimpleDateFormat("yyyy-MM-dd");
       } catch (ParseException e) {

           e.printStackTrace();
       }
       return df2.format(date1);
   }

@Override
public List<Map<String, Object>> missionUploadMethod(HttpServletRequest request, HttpServletResponse response)
		throws Exception {
	// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
	String savePath = "/data/olo_file/ding/"+df_time.format(new Date());
	System.out.println("savePath==="+savePath);
	//String savePath = "G:\\"+df_time.format(new Date());
	File file = new File(savePath);
	List<Map<String, Object>> fileUrl = new ArrayList<Map<String,Object>>();
	// 判断上传文件的保存目录是否存在
	if (!file.exists() && !file.isDirectory()) {
		System.out.println(savePath + "目录不存在，需要创建");
		// 创建目录
		file.mkdir();
	}
	String filePath = savePath+"/";
	File fileMission = new File(filePath);
	if (!fileMission.exists() && !fileMission.isDirectory()) {
		System.out.println(savePath + "目录不存在，需要创建");
		// 创建目录
		fileMission.mkdir();
	}
	// 消息提示
		// 使用Apache文件上传组件处理文件上传步骤：
		// 1、创建一个DiskFileItemFactory工厂
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 解决上传文件名的中文乱码
		upload.setHeaderEncoding("UTF-8");
		// 3、判断提交上来的数据是否是上传表单的数据
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 按照传统方式获取数据
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("result", "failed");
			fileUrl.add(map);
			return fileUrl;
		}
		// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
		
		List<FileItem> list = upload.parseRequest(request);
		//生成单例对象存储文件路径
		for (FileItem item : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			// 如果fileitem中封装的是普通输入项的数据
			if (item.isFormField()) {
				String name = item.getFieldName();
				// 解决普通输入项的数据的中文乱码问题
				String value = item.getString("UTF-8");
				// value = new String(value.getBytes("iso8859-1"),"UTF-8");
				System.out.println(name + "=" + value);
			} else {// 如果fileitem中封装的是上传文件
					// 得到上传的文件名称，
				String filename = item.getName();
				System.out.println(filename);
				if (filename == null || filename.trim().equals("")) {
					continue;
				}
				// 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
				// c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
				// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
				filename = filename.substring(filename.lastIndexOf("/") + 1);
				// 获取item中的上传文件的输入流
				InputStream in = item.getInputStream();
				// 创建一个文件输出流
				FileOutputStream out = new FileOutputStream(filePath + filename);
				String fileUrlStr = "";
				//正式环境地址
				fileUrlStr = "http://crmmobile1.olo-home.com:8038" + filePath  + filename;
				//本地测试地址
				//fileUrlStr =  "H:\\apache-tomcat-9.0.8\\webapps\\EnterpriseCalendar\\upload";
				map.put("fileUrl", fileUrlStr);
				map.put("fileName", filename);
				map.put("result", "success");
				fileUrl.add(map);
				// 创建一个缓冲区
				byte buffer[] = new byte[1024];
				// 判断输入流中的数据是否已经读完的标识
				int len = 0;
				// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
				while ((len = in.read(buffer)) > 0) {
					// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\"
					// + filename)当中
					out.write(buffer, 0, len);
				}
				// 关闭输入流
				in.close();
				// 关闭输出流
				out.close();
				// 删除处理文件上传时生成的临时文件
				item.delete();
			}
		}

	return fileUrl;
}


	@Override
	public String queryTaskDeail(Integer taskId) throws Exception {
		// TODO Auto-generated method stub
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		parmMap.put("taskId",taskId);
		List<TaskDistributeEntity> TaskList = distributeMapper.queryTaskDetail(parmMap);
		if (!TaskList.isEmpty()) {
			dataMap.put("requestid", TaskList.get(0).getRequestId());
			dataMap.put("taskId", TaskList.get(0).getTaskId());
			dataMap.put("rwmc", TaskList.get(0).getRwmc());
			dataMap.put("rwly", TaskList.get(0).getRwly());
			dataMap.put("zxr", TaskList.get(0).getZxr());
			dataMap.put("jdr", TaskList.get(0).getJdr());
			dataMap.put("cjr", TaskList.get(0).getCjr());
			dataMap.put("rwksrq", TaskList.get(0).getRwksrq());
			dataMap.put("rwjsrq", TaskList.get(0).getRwjsrq());
			dataMap.put("currentNode", TaskList.get(0).getCurrentNode());
			dataMap.put("cfzq", TaskList.get(0).getCfzq());
			List<String> xzrList = new ArrayList<String>();
			if (!xzrList.isEmpty() && xzrList!=null) {
				xzrList = Arrays.asList(TaskList.get(0).getXzr().split(","));
			}
			String xzrStr = "";
			for (int i = 0; i < xzrList.size(); i++) {
				Map<String, Object> xzrIdMap = new HashMap<String, Object>();
				xzrIdMap.put("id", xzrList.get(i));
				List<TaskDetailEntity> list = distributeMapper.getPeopleName(xzrIdMap);
				xzrStr = list.get(i).getLastname()+",";
			}
			
			if (!StringUtils.isEmpty(xzrStr)&&xzrStr!=null) {
				xzrStr = xzrStr.substring(0, xzrStr.length()-1);
			}
			
			Map<String, Object> rwMap = new HashMap<String, Object>();
			rwMap.put("mainid", TaskList.get(0).getTaskId());
			List<TaskDetailEntity> rwfjList = distributeMapper.mx(rwMap);
			
			String rwfjStr = TaskList.get(0).getRwfj();
			List<String> listRwfj = new ArrayList<String>();
			if (!StringUtils.isEmpty(rwfjStr)) {
				listRwfj = Arrays.asList(rwfjStr.split("\\|"));
			}
			List<String> list_rwnr = new ArrayList<String>();
			for (int i = 0; i < rwfjList.size(); i++) {
				list_rwnr.add(rwfjList.get(i).getRwnr());
			}
			
			dataMap.put("xzr",xzrStr);
			dataMap.put("rwfjList", listRwfj);
			dataMap.put("rwnrList", list_rwnr);
			
			returnMap.put("data",dataMap);
			returnMap.put("code", SUCCESSCODE);
			return JSONObject.fromObject(returnMap).toString();
		}else {
			List<TaskDistributeEntity> TaskList_uf_olo_ding_task = distributeMapper.queryTaskDetailByUf(parmMap);
			dataMap.put("requestid", TaskList_uf_olo_ding_task.get(0).getRequestId());
			dataMap.put("taskId", TaskList_uf_olo_ding_task.get(0).getTaskId());
			dataMap.put("rwmc", TaskList_uf_olo_ding_task.get(0).getRwmc());
			dataMap.put("rwly", TaskList_uf_olo_ding_task.get(0).getRwly());
			dataMap.put("zxr", TaskList_uf_olo_ding_task.get(0).getZxr());
			dataMap.put("jdr", TaskList_uf_olo_ding_task.get(0).getJdr());
			dataMap.put("cjr", TaskList_uf_olo_ding_task.get(0).getCjr());
			dataMap.put("rwksrq", TaskList_uf_olo_ding_task.get(0).getRwksrq());
			dataMap.put("rwjsrq", TaskList_uf_olo_ding_task.get(0).getRwjsrq());
			dataMap.put("currentNode", TaskList_uf_olo_ding_task.get(0).getCurrentNode());
			dataMap.put("cfzq", TaskList_uf_olo_ding_task.get(0).getCfzq());
			List<String> xzrList = new ArrayList<String>();
			if (!StringUtils.isEmpty(TaskList_uf_olo_ding_task.get(0).getXzr()) && TaskList_uf_olo_ding_task.get(0).getXzr()!=null) {
				xzrList = Arrays.asList(TaskList_uf_olo_ding_task.get(0).getXzr().split(","));
			}
			List<String> zxrList = new ArrayList<String>();
			if (!StringUtils.isEmpty(TaskList_uf_olo_ding_task.get(0).getZxr()) && TaskList_uf_olo_ding_task.get(0).getZxr()!=null) {
				zxrList = Arrays.asList(TaskList_uf_olo_ding_task.get(0).getZxr().split(","));
			}
			String zxrStr = "";
			for (int i = 0; i < zxrList.size(); i++) {
				Map<String, Object> zxrIdMap = new HashMap<String, Object>();
				zxrIdMap.put("id", zxrList.get(i));
				List<TaskDetailEntity> list = distributeMapper.getPeopleName(zxrIdMap);
				zxrStr = list.get(i).getLastname()+",";
			}
			String xzrStr = "";
			for (int i = 0; i < xzrList.size(); i++) {
				Map<String, Object> xzrIdMap = new HashMap<String, Object>();
				xzrIdMap.put("id", xzrList.get(i));
				List<TaskDetailEntity> list = distributeMapper.getPeopleName(xzrIdMap);
				xzrStr = list.get(i).getLastname()+",";
			}
			
			if (!StringUtils.isEmpty(xzrStr)&&xzrStr!=null) {
				xzrStr = xzrStr.substring(0, xzrStr.length()-1);
			}
			
			if (!StringUtils.isEmpty(zxrStr)&&xzrStr!=null) {
				zxrStr = zxrStr.substring(0, zxrStr.length()-1);
			}
			
			String rwfjStr = TaskList_uf_olo_ding_task.get(0).getRwfj();
			List<String> listRwfj = new ArrayList<String>();
			if (!StringUtils.isEmpty(rwfjStr)) {
				listRwfj = Arrays.asList(rwfjStr.substring(1,rwfjStr.length()-1).split(","));
			}
			List<String> list_rwnr = new ArrayList<String>();
			if (!StringUtils.isEmpty(TaskList_uf_olo_ding_task.get(0).getRwnrStr())) {
				list_rwnr = Arrays.asList(TaskList_uf_olo_ding_task.get(0).getRwnrStr().split("&"));
			}
			
			dataMap.put("xzr",xzrStr);
			dataMap.put("zxr", zxrStr);
			dataMap.put("rwfjList", listRwfj);
			dataMap.put("rwnrList", list_rwnr);
			
			returnMap.put("data",dataMap);
			returnMap.put("code", SUCCESSCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
		
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("code",FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
		
	}
	public static void main(String[] args) {
		try {
			String lastTimeTaskDate =  df_time.format(CZDateUtil.addDate(df_time.parse("2020-08-26"), (2+1)*7*4,-1));
			String[] a = new String[5];
			a[0] = "0";
			a[1] = "0";
			a[2] = "15";
			a[3] = "7";
			a[4] = "30";
			String b = CZDateUtil.getFirstTime("2020-12-31", 2,a);
			System.out.println("b="+b);
			System.out.println(lastTimeTaskDate);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
