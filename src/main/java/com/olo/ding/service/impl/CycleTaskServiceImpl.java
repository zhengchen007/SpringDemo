package com.olo.ding.service.impl;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.olo.ding.entity.ModyftEntity;
import com.olo.ding.entity.RankEntity;
import com.olo.ding.entity.TaskDistributeEntity;
import com.olo.ding.mapper.CycleTaskMapper;
import com.olo.ding.mapper.RankMapper;
import com.olo.ding.service.CycleTaskService;
import com.olo.ding.utils.CZDateUtil;
import com.olo.ding.utils.HttpRequester;
import com.olo.ding.utils.JsonToMap;
import com.olo.ding.utils.MapToJson;

import net.sf.json.JSONObject;

@Service("CycleTaskService")
public class CycleTaskServiceImpl implements CycleTaskService{
	static SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	private static final String SUCCESSCODE="201";
	private static final String FILEDCODE ="501";
	static final String url_zs_update =  "http://crmmobile1.olo-home.com:8038/quartz/quartz/job/updatejob";
	static final String url_cs_update =  "http://localhost:8089/quartz/job/updatejob";
	
	static final String url_zs_hd =  "http://crmmobile1.olo-home.com:8038/oloDingDing/taskDistribute/addZqTask";
	static final String url_zs_add = "http://crmmobile1.olo-home.com:8038/quartz/quartz/httpJob/add";
	static final String url_zs_del = "http://crmmobile1.olo-home.com:8038/quartz/job/delete?jobName=";
	
	
	
	
	static final String url_cs_hd =  "http://10.201.0.65:8080/oloDingDing/taskDistribute/addZqTask";
	static final String url_cs_add = "http://10.201.0.65:8089/quartz/httpJob/add";
	static final String url_cs_del = "http://10.201.0.65:8089/quartz/job/deleteforApp?jobName=";
	@Autowired
	CycleTaskMapper cycleTaskMapper;
	@Autowired
	RankMapper rankMapper;
	
	/**
	 * 周期要务列表查询
	 */
	public String queryCrycleTaskList(Integer userId){
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("userId", userId);
		List<TaskDistributeEntity> taskList = cycleTaskMapper.getCycleTaskDistributeList(parmMap);
		//执行人姓名拼接
		String zxrXm = "";
		for (int j = 0; j < taskList.size(); j++) {
			if (taskList.get(j).getTaskReceiver()!=null && !StringUtils.isEmpty(taskList.get(j).getTaskReceiver())) {
				List<String> zxrXmList = Arrays.asList(taskList.get(j).getTaskReceiver().split(","));
				for (int i = 0; i < zxrXmList.size(); i++) {
					Map<String, Object> peopleNameMap = new HashMap<String, Object>();
					System.out.println(zxrXmList.get(i));
					peopleNameMap.put("userId", zxrXmList.get(i));
					zxrXm += rankMapper.getPeopleName(peopleNameMap).get(0).getLastName()+",";
				}
				taskList.get(j).setTaskReceiver(zxrXm.substring(0, zxrXm.length()-1));
			}
			zxrXm = "";
		}
		//计算总期数和已经触发的期数
		for (int i = 0; i < taskList.size(); i++) {
			System.out.println(taskList.get(i).getPeriods());
			taskList.get(i).setAllCount(taskList.get(i).getPeriods());
			System.out.println(taskList.get(i).getAllCount());
			//计算已触发的期数
			Map<String, Object> triggeredParmMap = new HashMap<String, Object>();
			triggeredParmMap.put("rwjmId", taskList.get(i).getTaskId());
			List<TaskDistributeEntity> triggeredList = cycleTaskMapper.getTriggeredCount(triggeredParmMap);
			taskList.get(i).setTriggeredCount(triggeredList.get(0).getTriggeredCount());
		}
		
		returnMap.put("data", taskList);
		returnMap.put("code",SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("code",FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	
	  public static int getDaysOfMonth() {
	        Calendar calendar = Calendar.getInstance();
	        calendar.setTime(new Date());
	        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
	    }
	/**
	 * 查询周期要务详情
	 */
	@Override
	public String queryCycleTaskDetail(String taskId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		//获取周期要务详情表单数据
		Map<String, Object> taskIdMap = new HashMap<String, Object>();
		taskIdMap.put("taskId", taskId);
		List<TaskDistributeEntity> list_form = cycleTaskMapper.getCycleTaskForm(taskIdMap);
		//执行人，协作人姓名拼接
		String zxrXm = "";
		String xzrXm = "";
		if (list_form.get(0).getZxr()!=null&&!StringUtils.isEmpty(list_form.get(0).getZxr())) {
			List<String> zxrXmList = Arrays.asList(list_form.get(0).getZxr().split(","));
			for (int i = 0; i < zxrXmList.size(); i++) {
				Map<String, Object> peopleNameMap = new HashMap<String, Object>();
				peopleNameMap.put("userId", zxrXmList.get(i));
				zxrXm += rankMapper.getPeopleName(peopleNameMap).get(0).getLastName()+",";
			}
			list_form.get(0).setZxr(zxrXm.substring(0, zxrXm.length()-1));
		}
		
		if (list_form.get(0).getXzr()!=null&&!StringUtils.isEmpty(list_form.get(0).getXzr())) {
			List<String> zxrXmList = Arrays.asList(list_form.get(0).getXzr().split(","));
			for (int i = 0; i < zxrXmList.size(); i++) {
				Map<String, Object> peopleNameMap = new HashMap<String, Object>();
				peopleNameMap.put("userId", zxrXmList.get(i));
				xzrXm += rankMapper.getPeopleName(peopleNameMap).get(0).getLastName()+",";
			}
			list_form.get(0).setXzr(xzrXm.substring(0, xzrXm.length()-1));
		}
		
		//拼接任务内容
		String rwfjdz = list_form.get(0).getRwfjdz();
		if (rwfjdz != null && !StringUtils.isEmpty(rwfjdz)) {
			list_form.get(0).setRwfjList(Arrays.asList(rwfjdz.substring(1,rwfjdz.length()-1).split(",")));
		}else {
			list_form.get(0).setRwfjList(new ArrayList<String>());
		}
		//拼接任务附件
		String rwnr =  list_form.get(0).getRwnrStr();
		if (rwnr != null && !StringUtils.isEmpty(rwnr)) {
			list_form.get(0).setRwnr(Arrays.asList(rwnr.split("&")));
		}else {
			list_form.get(0).setRwnr((new ArrayList<String>()));
		}
		
		//获取周期要务触发过的列表数据(执行人备注，执行人附件，监督人备注，监督人附件)
		Map<String, Object> listParmMap = new HashMap<String, Object>();
		//获得的结果集先按时间（itemTIME）分组再按zxr分组
		List<TaskDistributeEntity> list_list = cycleTaskMapper.getCycleTaskList(taskIdMap);
		for (int i = 0; i < list_list.size(); i++) {
			//插入执行人
			Map<String, Object> peopleNameMap = new HashMap<String, Object>();
			peopleNameMap.put("userId",list_list.get(i).getZxr());
			List<RankEntity> list_peopleName = rankMapper.getPeopleName(peopleNameMap);
			if (list_peopleName.get(0).getLastName()!=null && !StringUtils.isEmpty(list_peopleName.get(0).getLastName())) {
				list_list.get(i).setZxrStr(list_peopleName.get(0).getLastName());
			}else {
				list_list.get(i).setZxrStr("");
			}
			//拼接zxrfjList
			if (list_list.get(i).getZxrfjdz() !=null&& !StringUtils.isEmpty(list_list.get(i).getZxrfjdz())) {
				list_list.get(i).setZxrfjList(Arrays.asList(list_list.get(i).getZxrfjdz().split(",")));
			}
			//拼接jdrfjList
			if (list_list.get(i).getJdrfjdz() !=null&& !StringUtils.isEmpty(list_list.get(i).getJdrfjdz())) {
				list_list.get(i).setJdrfjList(Arrays.asList(list_list.get(i).getJdrfjdz().split(",")));
			}
		}
		
		Map<String, List<TaskDistributeEntity>> groupByTime = new HashMap<String, List<TaskDistributeEntity>>();
		list_list.stream().collect(Collectors.groupingBy(TaskDistributeEntity::getItemTime,Collectors.toList()))
        .forEach(groupByTime::put);
		List<Map<String, Object>> list_return = new ArrayList<Map<String,Object>>();
		groupByTime.forEach((k,v)->{
			Map<String, Object> map = new HashMap<>();
			Map<String, Object> parmMap = new HashMap();
			//根据人员分组
			v.stream().collect(Collectors.groupingBy(TaskDistributeEntity::getZxr,Collectors.toList()))
	        .forEach(map::put);
			//把人员map变为人员数组[itemTime:"2020-07-23",person:[TaskDistributeEntity,TaskDistributeEntity]]
			List<Object> list = map.values().stream().collect(Collectors.toList());
			List<Object> list_ = new ArrayList<Object>();
			for (int i = 0; i < list.size(); i++) {
				System.out.println(list.get(i).toString());
				List<TaskDistributeEntity> list1 = (List<TaskDistributeEntity>) list.get(i);
				list_.add(list1.get(0));
			}
			parmMap.put("person", list_);	
			parmMap.put("itemTime", k);
			list_return.add(parmMap);
		});
		System.out.println(list_return.toString());
		
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("taskForm",list_form.get(0));
		dataMap.put("taskList",list_return);
		returnMap.put("data",dataMap);
		returnMap.put("code",SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("code",FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}

	/**
	 * 提前终止周期任务
	 */
	@Override
	public String aheadofStop(String taskId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("id", taskId);
		cycleTaskMapper.aheadofStop(parmMap);
		// TODO Auto-generated method stub
		returnMap.put("code",SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("code",FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	
	@Autowired
	RankMapper mapper;
	/**
	 * 周期任务修改详情
	 */
	public String modifyDeatil(String taskId) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("taskId", taskId);
		List<ModyftEntity> list_modifyDeatil = cycleTaskMapper.modifyDeatil(parmMap);
		//执行人
		List<Map<String, Object>> zxrList = new ArrayList<Map<String,Object>>();
		if (list_modifyDeatil.get(0).getZxr() != null && !StringUtils.isEmpty(list_modifyDeatil.get(0).getZxr())) {
			List<String> list = Arrays.asList(list_modifyDeatil.get(0).getZxr().split(","));
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = new HashMap<String, Object>();
				Map<String, Object> peopleNameParm = new HashMap<String, Object>();
				peopleNameParm.put("userId", list.get(i));
				List<RankEntity> peopleNameList = mapper.getPeopleName(peopleNameParm);
				map.put("id",list.get(i));
				map.put("name",peopleNameList.get(0).getLastName());
				zxrList.add(map);
			}
		}
		list_modifyDeatil.get(0).setZxrList(zxrList);
		//监督人
		List<Map<String, Object>> jdrList = new ArrayList<Map<String,Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> peopleNameParm = new HashMap<String, Object>();
		peopleNameParm.put("userId", list_modifyDeatil.get(0).getJdr());
		List<RankEntity> peopleNameList = mapper.getPeopleName(peopleNameParm);
		map.put("id",list_modifyDeatil.get(0).getJdr());
		map.put("name",peopleNameList.get(0).getLastName());
		jdrList.add(map);
		list_modifyDeatil.get(0).setJdrList(jdrList);
		//协作人
		List<Map<String, Object>> xzrList = new ArrayList<Map<String,Object>>();
		if (list_modifyDeatil.get(0).getXzr() != null && !StringUtils.isEmpty(list_modifyDeatil.get(0).getXzr())) {
			List<String> list = Arrays.asList(list_modifyDeatil.get(0).getXzr().split(","));
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map_xzr = new HashMap<String, Object>();
				Map<String, Object> peopleNameParm_xzr = new HashMap<String, Object>();
				peopleNameParm_xzr.put("userId", list.get(i));
				List<RankEntity> peopleNameList_xzr = mapper.getPeopleName(peopleNameParm_xzr);
				map_xzr.put("id",list.get(i));
				map_xzr.put("name",peopleNameList_xzr.get(0).getLastName());
				xzrList.add(map_xzr);
			}
		}
		list_modifyDeatil.get(0).setXzrList(xzrList);
		//任务内容拼接
		if (list_modifyDeatil.get(0).getRwnr() != null && !StringUtils.isEmpty(list_modifyDeatil.get(0).getRwnr())) {
				list_modifyDeatil.get(0).setRwnrList(Arrays.asList(list_modifyDeatil.get(0).getRwnr().split("&")));
		}else {
			list_modifyDeatil.get(0).setRwnrList(new ArrayList<String>());
		}
		//任务附件拼接
		if (list_modifyDeatil.get(0).getRwfj() != null && !StringUtils.isEmpty(list_modifyDeatil.get(0).getRwfj())) {
			 list_modifyDeatil.get(0).setRwfjList(Arrays.asList((list_modifyDeatil.get(0).getRwfj().substring(1,list_modifyDeatil.get(0).getRwfj().length()-1)).split("\\|")));
		}else {
			list_modifyDeatil.get(0).setRwfjList(new ArrayList<String>());
		}
		
		
		//拼接作业要求时长
		returnMap.put("data",list_modifyDeatil.get(0));
		returnMap.put("code",SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("code",FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}

	/**
	 * 周期任务修改
	 */
	@Override
	public String modify(TaskDistributeEntity distributeEntity) {
		Map<String, Object> returnMap = new HashMap<String, Object>();
		try {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("id", distributeEntity.getTaskId());
		parmMap.put("rwmc", distributeEntity.getRwmc());
		parmMap.put("zxr", distributeEntity.getZxr());
		parmMap.put("jdr", distributeEntity.getJdr());
		parmMap.put("xzr", distributeEntity.getXzr());
		parmMap.put("cfzq", distributeEntity.getCfzq());
		parmMap.put("rwksrq", distributeEntity.getRwksrq());
		parmMap.put("rwkssj", distributeEntity.getRwkssj());
		parmMap.put("rwjsrq", distributeEntity.getRwjsrq());
		parmMap.put("rwjssj", distributeEntity.getRwjssj());
		
		parmMap.put("zykssjJson", distributeEntity.getZykssj());
		parmMap.put("zyyqscJson", distributeEntity.getZyyqsc());
		parmMap.put("periods", distributeEntity.getPeriods());
		parmMap.put("zykssj", distributeEntity.getBeginTimeStr());
		parmMap.put("zyyqsc", distributeEntity.getZyyqscStr());
		parmMap.put("cycleStartDate", distributeEntity.getCycleStartDate());
		parmMap.put("positiveType", distributeEntity.getPositiveType());
		if ("0".equals(distributeEntity.getCfzq())||"2".equals(distributeEntity.getCfzq())) {
			parmMap.put("sjjg", String.valueOf(distributeEntity.getIntervalNum()));
		}else if ("1".equals(distributeEntity.getCfzq())) {
			parmMap.put("sjjg", String.valueOf(distributeEntity.getIntervalNum()*7));
		}else if ("3".equals(distributeEntity.getCfzq())) {
			parmMap.put("sjjg",0);
		}
		//根据id查询jobName
		Map<String, Object> jobNameMap = new HashMap<String, Object>();
		jobNameMap.put("taskId",distributeEntity.getTaskId());
		List<TaskDistributeEntity> jobNameList = cycleTaskMapper.queryJobName(jobNameMap);
		/***************************拼接cron开始**************************************************/
		List<String> zyyqscList = new ArrayList<String>();
		Map<String,Object> zykssj = JsonToMap.JsonToMap(distributeEntity.getZykssj());
		Integer month = (Integer) zykssj.get("month");
		Integer week = (Integer) zykssj.get("week");
		Integer day = (Integer) zykssj.get("day");
		Integer hour = (Integer) zykssj.get("hour");
		Integer mins = (Integer) zykssj.get("mins");
		zyyqscList  = Arrays.asList(distributeEntity.getZyyqsc().split(","));
		String[] cronList = new String[distributeEntity.getPeriods()];
		String cron = "";
		String zzCron = "";
		String cycleStartDate = distributeEntity.getCycleStartDate();
		String[] zykssjArray = new String[5];
		zykssjArray[0] = CZDateUtil.bw(month);
		zykssjArray[1] = CZDateUtil.bw(week);
		zykssjArray[2] = CZDateUtil.bw(day);
		zykssjArray[3] = CZDateUtil.bw(7);
		zykssjArray[4] = CZDateUtil.bw(30);
		SimpleDateFormat df_time = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		String jobName = "";
		if ("0".equals(distributeEntity.getCfzq())) {
			//拼接重复周期为每天的cron表达式(周期开始时间+重复周期+期数=cron)，提前一天的11点触发
			//Seconds Minutes Hours DayofMonth Month DayofWeek Year
			String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 0, zykssjArray);
			String lastTimeTaskDate = "";
			if (distributeEntity.getIntervalNum()>0) {
				lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (distributeEntity.getPeriods())*(distributeEntity.getIntervalNum()+1),-1));
			}else {
				lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), distributeEntity.getPeriods(),-1));
			}
			cron = "0 "+"30"+" "+"07"+" * * ?";
			//拼接终止当前周期任务的cron,周期截止日期再加一个周期的日期
			zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
			parmMap.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate);
			parmMap.put("dyckssj", firstTaskDate);
			parmMap.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 0, zykssjArray));
		}else if ("1".equals(distributeEntity.getCfzq())) {
			//每周拼接重复周期为每周的cron表达式
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
			String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 1, zykssjArray);
			String lastTimeTaskDate = "";
			if (distributeEntity.getIntervalNum()>0) {
				lastTimeTaskDate =  df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (distributeEntity.getPeriods())*((distributeEntity.getIntervalNum()+1)*7),-1));
			}else {
				lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), distributeEntity.getPeriods()*7,-1));
			}
			cron = "0 "+"30"+" "+"07"+" ? * "+actWeek;
			zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
			parmMap.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
			parmMap.put("dyckssj", firstTaskDate);
			parmMap.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 1, zykssjArray));
		}else if ("2".equals(distributeEntity.getCfzq())) {
			//每月拼接重复周期为每月的cron表达式
			if ("0".equals(distributeEntity.getPositiveType())) {
				//1~28
				String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 2, zykssjArray);
				String lastTimeTaskDate = "";
				if (distributeEntity.getIntervalNum()>0) {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (distributeEntity.getPeriods())*(distributeEntity.getIntervalNum()+1),0));
				}else {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), distributeEntity.getPeriods(),0));
				}
				cron = "0 "+"30"+" "+"07"+" "+day+" * ?";
				zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
				parmMap.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
				parmMap.put("dyckssj", firstTaskDate);
				parmMap.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 2, zykssjArray));
			}else if ("1".equals(distributeEntity.getPositiveType())) {
				//倒数3天
				//获取当前日期计算出倒数3天对应的日期
				String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 2, zykssjArray);
				String lastTimeTaskDate = "";
				if (distributeEntity.getIntervalNum()>0) {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), (distributeEntity.getPeriods())*(distributeEntity.getIntervalNum()+1),0));
				}else {
					lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(firstTaskDate), distributeEntity.getPeriods(),0));
				}				cron = "0 "+"30"+" "+"07"+" "+CZDateUtil.getDate(day)+" * ?";
				zzCron =  CZDateUtil.getCron(CZDateUtil.addHour(df.parse(lastTimeTaskDate+" "+"00:"+mins.toString()+":"+hour.toString()), 1));
				parmMap.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
				parmMap.put("dyckssj", firstTaskDate);
				parmMap.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 2, zykssjArray));
			}
		}else if ("3".equals(distributeEntity.getCfzq())) {
			//计算出每一期任务，生成n个任务
			//算出第一期的执行时间
			SimpleDateFormat df_jd = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");// 设置日期格式
			String firstTaskDate = CZDateUtil.getFirstTime(cycleStartDate, 3, zykssjArray);
			String tempTime = "";
			String lastTimeTaskDate = df_time.format(CZDateUtil.addDate(df_time.parse(cycleStartDate), distributeEntity.getPeriods(),1));
			parmMap.put("taskCreatedate",firstTaskDate+"~"+lastTimeTaskDate );
			parmMap.put("cron",CZDateUtil.getFirstTime(firstTaskDate, 3, zykssjArray));
			parmMap.put("dyckssj", firstTaskDate);
			//将uf_olo_ding_task中的sfwjd（是否为季度）更新为0
			parmMap.put("sfwjd", "0");
			for (int i = 0; i < cronList.length; i++) {
				if (i == 0) {
					cronList[i] = CZDateUtil.getCron(df_jd.parse(firstTaskDate+":"+"00"));
					tempTime = firstTaskDate;
				}else {
					if (tempTime.length()<=16) {
						tempTime = tempTime+":00";
					}
					cronList[i] = CZDateUtil.getCron(CZDateUtil.getNextJd(df_jd.parse(tempTime)));
					tempTime = df_jd.format(CZDateUtil.getNextJd(df_jd.parse(tempTime)));
				}
			}
			List<String> nameList = new ArrayList<>();
			if (distributeEntity.getJduuid()!=null&&(!StringUtils.isEmpty(distributeEntity.getJduuid()))) {
				nameList = Arrays.asList(distributeEntity.getJduuid().split(","));
			}
			//删除之前的季度任务
			for (int i = 0; i < nameList.size(); i++) {
				Map<String, Object> parmMap_del = new HashMap<String, Object>();
				HttpRequester.requestGET(
						url_zs_del+URLEncoder.encode(nameList.get(i))+"&jobGroup="+ URLEncoder.encode("周期任务"+nameList.get(i).substring(nameList.get(i).length()-32, nameList.get(i).length())))
						;
			}
			String uuid = UUID.randomUUID().toString().replaceAll("-","");
			String jduuid = "";
			for (int i = 0; i < cronList.length; i++) {
				String jduuid_taskname = UUID.randomUUID().toString().replaceAll("-","");
				Map<String, Object> uuidMap = new HashMap<String, Object>();
				uuidMap.put("uuid", uuid);
				Map<String, Object> bodyMap_del = new HashMap<String, Object>();
				bodyMap_del.put("jobName",distributeEntity.getRwmc()+df.format(new Date())+"-"+jduuid_taskname);
				bodyMap_del.put("jobGroup","周期任务"+jduuid_taskname);
				bodyMap_del.put("description",distributeEntity.getRwmc()+df.format(new Date()));
				bodyMap_del.put("requestType","GET");
				bodyMap_del.put("url",url_zs_hd);
				bodyMap_del.put("params",MapToJson.mapToJson(uuidMap).toString());
				bodyMap_del.put("cronExpression",cronList[i]);
				HttpRequester.requestPOST(url_zs_add, MapToJson.mapToJson(bodyMap_del).toString(), "application/json;charset=utf-8");
				jobName = distributeEntity.getRwmc()+df.format(new Date())+"-"+jduuid_taskname;
				jduuid = jduuid+distributeEntity.getRwmc()+df.format(new Date())+"-"+jduuid_taskname+",";
			}
			parmMap.put("jduuid", jduuid);
			parmMap.put("zqywuuid", uuid);
			//更新uf_olo_ding_task 中的jduuid和zqywuuid
			cycleTaskMapper.updateJduuid(parmMap);
			
			
		}
		/***************************拼接cron结束**************************************************/
		
		
		
		if (!"3".equals(distributeEntity.getCfzq())) {
			/***************************调用修改cron接口开始**************************************************/
			Map<String, Object> bodyMap = new HashMap<String, Object>();
			bodyMap.put("jobName",jobNameList.get(0).getJobName());
			bodyMap.put("jobGroup","周期要务"+jobNameList.get(0).getJobName().substring(jobNameList.get(0).getJobName().length()-32,jobNameList.get(0).getJobName().length()));
			bodyMap.put("cronExpression",cron);
			HttpRequester.requestPOST(url_zs_update, MapToJson.mapToJson(bodyMap).toString(), "application/json;charset=utf-8");
			/***************************调用修改cron接口结束**************************************************/
			
			
			/***************************调用修改终止cron接口开始**************************************************/
			Map<String, Object> zzbodyMap = new HashMap<String, Object>();
			zzbodyMap.put("jobName",jobNameList.get(0).getZzJobName());
			zzbodyMap.put("jobGroup","终止周期要务"+"---"+jobNameList.get(0).getZzJobName().substring(jobNameList.get(0).getZzJobName().length()-32,jobNameList.get(0).getZzJobName().length()));
			zzbodyMap.put("cronExpression",zzCron);
			String result = HttpRequester.requestPOST(url_zs_update, MapToJson.mapToJson(zzbodyMap).toString(), "application/json;charset=utf-8");
			System.out.println(result);
			/***************************调用修改终止cron接口结束**************************************************/
		}
	
		

		List<String> rwnr = distributeEntity.getRwnr();
		String rwnrStr = "";
		for (int i = 0; i < rwnr.size(); i++) {
			rwnrStr += rwnr.get(i)+"&";
		}
		if (rwnr != null && !rwnr.isEmpty()) {
			parmMap.put("rwnr", rwnrStr.substring(0, rwnrStr.length()-1));
		}else {
			parmMap.put("rwnr", "");
		}
		String rwfj = distributeEntity.getRwfj();
		if (rwfj !=null&&!rwfj.isEmpty()) {
			if (rwfj != null && !rwfj.isEmpty()) {
				parmMap.put("rwfj",rwfj);
			}else {
				parmMap.put("rwfj", "[]");
			}
		}else {
			parmMap.put("rwfj", "[]");
		}
		
		cycleTaskMapper.modify(parmMap);
		//更改
		
		returnMap.put("code",SUCCESSCODE);
		return JSONObject.fromObject(returnMap).toString();
		} catch (Exception e) {
			// TODO: handle exception
			returnMap.put("code",FILEDCODE);
			return JSONObject.fromObject(returnMap).toString();
		}
	}
	public static void main(String[] args) {
		String str = "556c9d238a284a6bb0c6dfacfbec57e5";
		System.out.println(str.length());
	}
}
