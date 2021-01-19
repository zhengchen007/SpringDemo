package com.olo.ding.task;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.map.LinkedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.olo.ding.entity.PgdEntity;
import com.olo.ding.entity.RankEntity;
import com.olo.ding.entity.TaskDetailEntity;
import com.olo.ding.entity.TaskDistributeEntity;
import com.olo.ding.mapper.RankMapper;
import com.olo.ding.mapper.TaskDistributeMapper;
import com.olo.ding.service.HomePageService;
import com.olo.ding.service.RedisService;
import com.olo.ding.utils.CZDateUtil;
import com.olo.ding.utils.DateUtil2;
import com.olo.ding.utils.MapToJson;
import com.olo.ding.utils.ThreadUtils;
import com.olo.ding.utils.WeChatService;

import cn.com.weaver.services.webservices.WorkflowServicePortTypeProxy;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import weaver.workflow.webservices.WorkflowBaseInfo;
import weaver.workflow.webservices.WorkflowMainTableInfo;
import weaver.workflow.webservices.WorkflowRequestInfo;
import weaver.workflow.webservices.WorkflowRequestTableField;
import weaver.workflow.webservices.WorkflowRequestTableRecord;
@Configuration
@EnableScheduling // 启用定时任务
public class Task {
	
    @Autowired
    RankMapper mapper;
    @Autowired
    RankMapper rankMapper;
    @Autowired
	private RedisService redisService;
    @Autowired
    TaskDistributeMapper addTaskMapper;
    static SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	static SimpleDateFormat df_time = new SimpleDateFormat("HH:mm");// 设置日期格式
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
	static SimpleDateFormat year = new SimpleDateFormat("yyyy");// 设置日期格式
	static SimpleDateFormat df_hh = new SimpleDateFormat("yyyy-MM-dd HH");// 设置日期格式
	
	@Scheduled(cron = "0 0 9 * * ? ")
	@Scheduled(cron = "0 0 14 * * ? ")
	//@Scheduled(cron="0 0/1 * * * ?")
	public void task() {
		Map<String, Object> jdMap = new HashMap<String, Object>();
		Map<String, Object> ndMap = new HashMap<String, Object>();
		//当前季度任务数据插入
		jdMap.put("begintDay", DateUtil2.getCurrentQuarterBegin());
		jdMap.put("endDay", DateUtil2.getCurrentQuarterEnd());
		this.pushTaskMsg(jdMap,0);
		//当前年度任务数据插入
		ndMap.put("begintDay", year.format(new Date())+"-01-01");
		ndMap.put("endDay", year.format(new Date())+"-12-31");
		this.pushTaskMsg(ndMap,1);
		
		
	}

	
	public void pushTaskMsg(Map<String, Object> timeMap,Integer cycleType) {
		
		List<PgdEntity> listRank = new ArrayList<PgdEntity>(); 
		listRank = mapper.getPgdRank(timeMap);
		if (listRank.isEmpty()) {
			return;
		}
		listRank.stream().forEach(a->{
			try {
				//接收时间
				Date date_receivedate = df
						.parse(computeReceivetime(a.getReceiveDate() + " " + a.getReceiveTime()));
				//处理时间
				Date date_dealdate = df.parse(a.getOperateDate()+" "+a.getOperateTime());
				//处理时间-接收时间(工作日时间)
				double gzrDouble = (DateUtil2.addInWorkDate(date_receivedate, Double.parseDouble(a.getDealTime())))
						.getTime();
				//假期时间计算
				Map<String, Object> parmMap = new HashMap<String, Object>();
				parmMap.put("beginTime", a.getReceiveDate());
				parmMap.put("endTime", df.format(gzrDouble));
				List<PgdEntity> list_jqts = new ArrayList<PgdEntity>();
				list_jqts = mapper.getHolidayCount(parmMap);
				double jqts_double = 0;
				if (list_jqts != null) {
					if (list_jqts.get(0)!=null&&list_jqts.get(0).getJqts() != null) {
						if (!StringUtils.isEmpty(list_jqts.get(0).getJqts()))
						{
							jqts_double = Double.parseDouble(list_jqts.get(0).getJqts());
						}
					}
				}
				String jzrq = df.format(gzrDouble + (jqts_double * 3600000));
				//截止时间和处理时间比较,如果截止时间小于处理时间就不得分，反之则得分
				if (date_dealdate.compareTo(df.parse(jzrq))==1) {
					a.setIsGetScore(false);
				}else {
					a.setIsGetScore(true);
				}
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
		System.out.println("listRank的数量为============="+listRank.size());
		//过滤一个流程重复得分的情况，当一个流程的IsGetScore已经为true，就过滤掉相同的requestid
		List<PgdEntity> listDf = 
				listRank.stream().filter(a ->a.getIsGetScore()==true).collect(
			            Collectors.collectingAndThen(
			                    Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(a ->a.getRequestId()+";"+a.getCzr()))), ArrayList::new)
			);
		System.out.println("listDf的数量为============="+listDf.size());
		//任务得分（除去会议任务）
		List<RankEntity> list = rankMapper.getTaskRank(timeMap);
		list.forEach(a->{
			String clsj = a.getOperatedate()+" "+a.getOperatetime();
			String rwzj = a.getRwjzrq();
			if (StringUtils.isEmpty(a.getOperatedate())||a.getOperatedate()==null) {
				clsj = df_hh.format(new Date());
			}
			try {
				if (df_hh.parse(clsj).getTime()<=df_hh.parse(rwzj).getTime()) {
					a.setTaskScore(1);
				}else {
					a.setTaskScore(0);
				}
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		});
		Map<String, Integer> groupByReceiver = list.stream()
                .collect(
                        Collectors.groupingBy(
                        		RankEntity::getTaskReceiver, 
                                Collectors.summingInt(RankEntity::getTaskScore)
                        		)
                );
		Map<String, Integer> mapFirst30 =  listDf.stream().collect(
                Collectors.groupingBy(
                		PgdEntity::getCzr,
                		Collectors.summingInt(PgdEntity::getPf)
                      )
        );
		// 把两个map中key值相同的value做累加
		groupByReceiver.forEach((key,value) -> mapFirst30.merge(key,value,Integer::sum));
		//将全公司排名的数据存入redis
		try {
			if (cycleType == 0) {
				//当前季度
				redisService.set("quarterAllPeopelRank", MapToJson.mapToJson(sortMapByValue(mapFirst30,"bitToSmall","all")).toString());
				//redisService.hmset("quarterAllPeopelRank",sortMapByValue(mapFirst30,"bitToSmall","all"));
				//redisService.expire("First30", 60*60L);
			}else if(cycleType == 1){
				//当前年
				redisService.set("yearAllPeopelRank", MapToJson.mapToJson(sortMapByValue(mapFirst30,"bitToSmall","all")).toString());
				//redisService.hmset("yearAllPeopelRank",sortMapByValue(mapFirst30,"bitToSmall","all"));
				//redisService.expire("First30", 60*60L);
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//将公司得分前三十名的数据存入redis
		try {
			//当前季度
			if (cycleType == 0) {
				redisService.set("quarterFirst30",MapToJson.mapToJson(sortMapByValue(mapFirst30,"bitToSmall","30")).toString());
				//redisService.expire("quarterFirst30", 60*60L);
			}else if(cycleType == 1) {
				redisService.set("yearFirst30",MapToJson.mapToJson(sortMapByValue(mapFirst30,"bitToSmall","30")).toString());
				//redisService.expire("yearFirst30", 60*60L);
			}
			
			//当前年
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//将公司得分后三十名的数据存入redis
		try {
			if (cycleType == 0) {
				redisService.set("quarterEnd30",MapToJson.mapToJson(sortMapByValue(mapFirst30,"smallToBig","30")).toString());
				//redisService.expire("quarterEnd30", 60*60L);
			}else if(cycleType == 1) {
				redisService.set("yearEnd30",MapToJson.mapToJson(sortMapByValue(mapFirst30,"smallToBig","30")).toString());
				//redisService.expire("yearEnd30", 60*60L);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//将按部门分组得分的数据存入redis{部门id:{张三:1，李四：2}}n
	 	Map<String,  Map<String, Integer>> pgdGroupByDep=	
	 		listDf.stream().filter(a->a.getDepartmentId()!=null).filter(a->!StringUtils.isEmpty(a.getDepartmentId())).collect(
                Collectors.groupingBy(
                		PgdEntity::getDepartmentId, Collectors.groupingBy(PgdEntity::getCzr,
                                Collectors.reducing(
                                        0,
                                        PgdEntity::getPf,
                                        Integer::sum))
                )
	 	);
	 	
	 	Map<String,  Map<String, Integer>> taskGroupByDep=	
	 			list.stream().collect(
	 	                Collectors.groupingBy(
	 	                		RankEntity::getDepartmentId, Collectors.groupingBy(
	 	                        		RankEntity::getTaskReceiver,
	 	                                Collectors.reducing(
	 	                                        0,
	 	                                        RankEntity::getTaskScore,
	 	                                        Integer::sum))
	 	                )
	 		 	);
	 	if (taskGroupByDep.size()>=pgdGroupByDep.size()) {
	 		System.out.println("taskGroupByDep.size()="+taskGroupByDep.size());
	 		System.out.println("pgdGroupByDep.size()="+pgdGroupByDep.size());
	 		taskGroupByDep.forEach((key_str,value_map)->{
		 		 if (pgdGroupByDep.containsKey(key_str)) {
		 			 value_map.forEach((key_str_1,value_str_1)->pgdGroupByDep.get(key_str).merge(key_str_1, value_str_1, Integer::sum));
		 		}else
		 		 {
		 			 pgdGroupByDep.put(key_str,taskGroupByDep.get(key_str));
		 		}
		 	 });
		}else {
			System.out.println("taskGroupByDep.size()="+taskGroupByDep.size());
	 		System.out.println("pgdGroupByDep.size()="+pgdGroupByDep.size());
			pgdGroupByDep.forEach((key_str,value_map)->{
		 		 if (taskGroupByDep.containsKey(key_str)) {
		 			 value_map.forEach((key_str_1,value_str_1)->taskGroupByDep.get(key_str).merge(key_str_1, value_str_1, Integer::sum));
		 		}else
		 		 {
		 			taskGroupByDep.put(key_str,pgdGroupByDep.get(key_str));
		 		}
		 	 });
		}
	 	
		try {
			Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
			pgdGroupByDep.forEach((key,value)->{
				tempMap.put(key, value.toString());
			});
			if (cycleType == 0) {
				if (!tempMap.isEmpty()) {
					redisService.set("quarterGroupByDep",MapToJson.mapToJson(tempMap).toString());
					//redisService.expire("quarterGroupByDep", 60*60L);
				}
			}else if(cycleType == 1) {
				if (!tempMap.isEmpty()) {
					redisService.set("yaerGroupByDep",MapToJson.mapToJson(tempMap).toString());
					//redisService.expire("yearGroupByDep", 60*60L);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}


	public static Map<String, Integer> sortMapByValue(Map<String, Integer> map,String sortType,String rowCount) {

		 Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();

		 List<Entry<String,Integer>>lists=new ArrayList<Entry<String,Integer>>(map.entrySet());
		 Collections.sort(lists,new Comparator<Map.Entry<String, Integer>>() {
		 public int compare(Map.Entry<String, Integer> o1,Map.Entry<String, Integer> o2)
		 {
		 double q1=o1.getValue();
		 double q2=o2.getValue();
		 double p=q2-q1;
		 if (sortType.equals("bitToSmall")) {
			 if(p>0){
				 return 1;
				 }
		}else if(sortType.equals("smallToBig")){
			 if(p<0){
				 return 1;
				 }
		}else if (sortType.equals("all")) {
			 if(p>0){
				 return 1;
				 }
		}
		  if(p==0){
		 return 0;
		 }
		 else
		 return -1;
		 }
		 });

		 if(lists.size()>=2){

		 //lists.subList()用法
		 int rowInt = 0;
		 if ("30".equals(rowCount)) {
			 if (lists.size()<30) {
				 rowInt = lists.size();
			}else {
				rowInt = 30;
			}
			
		}else if("all".equals(rowCount)) {
			rowInt = lists.size();
		}
		 for(Map.Entry<String, Integer> set:lists.subList(0, rowInt)){
		 sortedMap.put(set.getKey(), set.getValue());
		 }
		 }else {
		 for(Map.Entry<String, Integer> set:lists){
		 sortedMap.put(set.getKey(), set.getValue());
		 }
		 }
		 return sortedMap;
		 }
		
	 public static String  computeReceivetime(String firstString) {
		 	//计算开始
		 	int time = isIntTime(firstString);
		 	switch (time) {
			case 0:
				firstString = firstString.substring(0,10)+" 08:30:00";
				break;
			case 1:
				firstString = firstString.substring(0,10)+" 13:00:00";
				break;
			case 2:
				try {
					Date nextDay =  new Date(df_date.parse(firstString.substring(0,10)).getTime()+24*3600*1000);
					firstString = df_date.format(nextDay)+" "+"08:30:00";
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}     
				break;
			}
		 	
		 	return firstString;
	    }  
	
	public static int isIntTime(String receiveTimeStr) {
		//如果接收时间和当前时间都在 8:30 ~ 18:00之间，就正常计算
	 	//如果接收时间在0:00 ~ 8:31之间 ，那么接收时间就改为8:30 返回0
	 	//如果接收时间在11:31 ~ 12:59，那么接收时间就改为13:00 返回1
	 	//如果接收时间在>18:00,那么接收时间就改为第二天的8:30 返回2
		String date = receiveTimeStr.substring(0,10);
	 	DateTime firstTime = DateUtil.parse(receiveTimeStr);
	 	boolean isInMorningTime = DateUtil.isIn(firstTime, DateUtil.parse(date+" "+"00:00"), DateUtil.parse(date+" "+"08:31"));
	 	boolean isInafternoonTime = DateUtil.isIn(firstTime, DateUtil.parse(date+" "+"11:30"), DateUtil.parse(date+" "+"12:59"));
	 	boolean isNightTime = DateUtil.isIn(firstTime, DateUtil.parse(date+" "+"18:00"), DateUtil.parse(date+" "+"23:59"));
	 	int returnInt = 3;
	 	if (isInMorningTime) {
	 		returnInt = 0;
		}
	 	if (isInafternoonTime) {
	 		returnInt = 1;
		} 
	 	if (isNightTime) {
	 		returnInt = 2;
		}
	 	return returnInt;
	}
	 
	//定时任务生成任务(生成的任务都为非当天新建的任务,当天新建的任务立马生成)
	
	//每天23点生成第二天的定时任务
	WorkflowServicePortTypeProxy	proxy	= new WorkflowServicePortTypeProxy();
	@Autowired
	TaskDistributeMapper taskDistributeMapper;
	@Scheduled(cron = "0 0 23 * * ? ")
	public void Createtask() {
		System.out.println("@@@@@@@@@@@@@@@@@@@@@@@进入定时任务生成任务@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
		List<TaskDistributeEntity> list = taskDistributeMapper.getNeedToCreateTask();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date date = new Date(System.currentTimeMillis());
		String nowTime = formatter.format(date);
		list.forEach(a->{
			ThreadUtils.SocketTransThreadPool.execute(() -> {
			boolean isCreateWorkflow = false;
				 if("1".equals(a.getSfwzq())) {
					try {
						if (isExecute(a.getRwksrq()+" "+a.getRwkssj(),a.getRwjsrq()+" "+a.getRwjssj(), "-1", (String)nowTime)) {
							isCreateWorkflow = true;
						}
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
					try {
							//如果是周期要务要判断到程静的流程是否归档
								if (isCreateWorkflow) {
									// 生成任务流程
									SimpleDateFormat formatter_Date = new SimpleDateFormat("yyyy-MM-dd");
									Date date_now = new Date(System.currentTimeMillis());
									String date_now_str = formatter_Date.format(date_now);
									String zxr = a.getZxr();
									String jdr = a.getJdr();
									String xzrStr = a.getXzr();
									List<String> xzr =  Arrays.asList(xzrStr.split(",")); 
									String missionStartTime = date_now_str+" "+a.getRwkssj();
									String missionEndTime = a.getRwjsrq()+" "+a.getRwjssj();
									String cjr = a.getCjr();
									String xzrName = Optional.ofNullable(xzr).orElse(Arrays.asList("")).stream().collect(Collectors.joining(","));
									// 任务描述
									String missionContent = a.getRwnrStr();
									String rwfjStr = a.getRwfj();
									//List<Object> rwfjObj = JSONObject.parseArray(rwfjStr);
								    //List<String> rwfjArray = rwfjObj.stream().map(Object::toString).collect(Collectors.toList());
									List<String> rwfjArray = Arrays.asList(rwfjStr.substring(1,rwfjStr.length()-1).split(","));
									// 主字段
									List<String> zxrList = Arrays.asList(zxr.split(","));
									for (int j = 0; j < zxrList.size(); j++) {
										WorkflowRequestTableField[] mainTableFields = new WorkflowRequestTableField[12]; // 字段信息
										mainTableFields[0] = new WorkflowRequestTableField();
										mainTableFields[0].setFieldName("name");//
										mainTableFields[0].setFieldValue(a.getRwmc());//
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
										mainTableFields[2].setFieldValue(xzrName);//
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
										mainTableFields[9].setFieldValue(a.getCfzq());//
										mainTableFields[9].setView(true);// 字段是否可见
										mainTableFields[9].setEdit(true);// 字段是否可编辑
										
										mainTableFields[10] = new WorkflowRequestTableField();
										mainTableFields[10].setFieldName("taskResource");//
										mainTableFields[10].setFieldValue(a.getRwly());//
										mainTableFields[10].setView(true);// 字段是否可见
										mainTableFields[10].setEdit(true);// 字段是否可编辑
										
										mainTableFields[11] = new WorkflowRequestTableField();
										mainTableFields[11].setFieldName("rwjmid");//
										mainTableFields[11].setFieldValue(String.valueOf(a.getTaskId()));//
										mainTableFields[11].setView(true);// 字段是否可见
										mainTableFields[11].setEdit(true);// 字段是否可编辑

										
										SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
										String nowDate = df.format(new Date());
										String requestName = "任务流程-" + nowDate;

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
											Map<String, Object> parmTaskidMap = new HashMap<String, Object>();
											parmTaskidMap.put("requestId", result);
											List<TaskDetailEntity> listId = addTaskMapper.queryTaskId(parmTaskidMap);
											//把任务内容插入任务流程明细表
												List<String> rwnrList = Arrays.asList(missionContent.split("&"));
												Map<String, Object> parmRwnr = new HashMap<String, Object>();
												for (int i = 0; i < rwnrList.size(); i++) {
													if (!StringUtils.isEmpty(rwnrList.get(i))&&!"null".equals(rwnrList.get(i))&&rwnrList.get(i)!=null) {
														parmRwnr.clear();
														parmRwnr.put("mainId", listId.get(0).getId());
														parmRwnr.put("rwnr", rwnrList.get(i));
														addTaskMapper.addRwnr(parmRwnr);
													}
												}
												
												//如果是非周期任务,更新uf_olo_ding_task中的sfcf（是否触发任务）
												if ("1".equals(a.getSfwzq())) {
													Map<String, Object> updateParm = new HashMap<String,Object>();
													updateParm.put("taskId", a.getTaskId());
													taskDistributeMapper.updateSfcf(updateParm);
												}
												//如果是周期任务，更新uf_olo_ding_task中的cfsj（触发任务的时间）
												if ("0".equals(a.getSfwzq())) {
													Map<String, Object> updateParm = new HashMap<String,Object>();
													updateParm.put("cfsj", date_now_str+",");
													updateParm.put("taskId", a.getTaskId());
													taskDistributeMapper.updateScsj(updateParm);
												}
												
											
										} catch (RemoteException e) {
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
									}

								}
							
						
					} catch (DataAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
			});
			
		});
	}
	@Autowired
	HomePageService homePageService;
	@Scheduled(cron = "0 30 8 ? * *")
	//@Scheduled(cron = "0 */1 * * * ?")
	public void statistics() {
		//查询今天有任务的人员，已经该人员一共有多少条任务，多少条未逾期，多少条已经逾期
		List<TaskDistributeEntity> list = addTaskMapper.getUserId();
		for (int i = 0; i < list.size(); i++) {
			String jsonStr = homePageService.getListMsg(Integer.parseInt(list.get(i).getZxr()), 0);
			JSONObject jsonObj = JSONObject.parseObject(jsonStr);
			Map<String, Object> map = jsonObj.getJSONObject("data");
			Integer isNotOverTimeCount = (Integer) map.get("isNotOverTimeCount");   
			Integer isOverTimeCount = (Integer) map.get("isOverTimeCount");
			Integer isFallDue = (Integer) map.get("isFallDue");
			Integer allCount = isNotOverTimeCount+isOverTimeCount;
			String text = "您共有"+allCount.toString()+"条任务待处理。\n"+"已逾期共有"+isOverTimeCount.toString()+"条。\n即将逾期共有"+isFallDue+"条。\n请尽快进入我乐钉钉处理。";
			if ((allCount>0)&&(isNotOverTimeCount>0||isOverTimeCount>0||isFallDue>0)) {
				WeChatService.newSendGet(list.get(i).getZxr(),text);
			}
		}
		
		
	}
	
	/**
	 * 判断第二天需要生成的任务流程
	 * @param beginDate
	 * @param endDate
	 * @param ts
	 * @return
	 * @throws ParseException 
	 */
	public static boolean isExecute(String beginDate,String endDate,String circleType,String nowTime) throws ParseException {
		//-1:非周期任务;0：每天；1：每周；2：每月；3：每季度
		SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
		Date nowDate = Task.getNextDay(formatter_date.parse(nowTime),1);
		Date beginDate_date = formatter_date.parse(beginDate);
		Date endDate_date = formatter_date.parse(endDate);
		Integer day = 0;
		if ("0".equals(circleType)) {
			 day = 1;
		}else if ("1".equals(circleType)) {
			 day = 7;
		}else if ("2".equals(circleType)) {
			 day = 30;
		}else if ("3".equals(circleType)) {
			 day = CZDateUtil.daysBetween(CZDateUtil.getCurrentQuarterStartTime(),CZDateUtil.getCurrentQuarterEndTime());
		}
		if (nowDate.getTime()-beginDate_date.getTime()>=0 && nowDate.getTime()-endDate_date.getTime()<=0 && (!"-1".equals(circleType))) {
			Date tempDate = formatter_date.parse(beginDate);
			while (tempDate.getTime() <= endDate_date.getTime()) {
				if (formatter_date.parse(nowTime).getTime() == tempDate.getTime()) {
					System.out.println("触发周期任务");
					return true;
				}
				tempDate = Task.getNextDay(tempDate,day);
				continue;
			}
		}else if (("-1".equals(circleType))) {
			//非周期任务
			if (formatter_date.parse(nowTime).getTime() == formatter_date.parse(beginDate).getTime()) {
				System.out.println("触发非周期任务");
				return true;
			}
		}
		return false;
		
		}
	public static Date getNextDay(Date date,int time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +time);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }
	
	
	// Map的value值降序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortDescend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return -compare;
            }
        });
 
        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }
 
    // Map的value值升序排序
    public static <K, V extends Comparable<? super V>> Map<K, V> sortAscend(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                int compare = (o1.getValue()).compareTo(o2.getValue());
                return compare;
            }
        });
 
        Map<K, V> returnMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : list) {
            returnMap.put(entry.getKey(), entry.getValue());
        }
        return returnMap;
    }

    public static void main(String[] args) {
    	LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
		for (int i = 0; i < 1000; i++) {
			if (i==10) {
				map.put(Integer.toString(i+1), 10000);
			}else {
				map.put(Integer.toString(i+1), i+2);
			}
		}
		System.out.println(map);
		System.out.println(sortAscend(map));
		
		int a=1188;
		int b=93;
		double c = 0.0;

		c=(double)(Math.round(a*100/b)/100.0);//这样为保持2位
		System.out.println(c);
	}
	
}
