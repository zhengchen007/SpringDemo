package com.olo.ding.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.druid.util.StringUtils;
import com.olo.ding.entity.PgdEntity;
import com.olo.ding.entity.RankEntity;
import com.olo.ding.mapper.RankMapper;
import com.olo.ding.utils.DateUtil2;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;

@Controller
@RequestMapping(value="/px")
public class TestPx {
	
	 	@Autowired
	    RankMapper mapper;
	    @Autowired
	    RankMapper rankMapper;
	    
	    static SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
		static SimpleDateFormat df_time = new SimpleDateFormat("HH:mm");// 设置日期格式
		static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
		static SimpleDateFormat year = new SimpleDateFormat("yyyy");// 设置日期格式
		static SimpleDateFormat df_hh = new SimpleDateFormat("yyyy-MM-dd HH");// 设置日期格式
	
	@RequestMapping(value="/test" ,method=RequestMethod.GET)
    public String getNeedToDoListMsg(Integer type){
		Map<String, Object> returnMap = new LinkedHashMap<String, Object>();
		Map<String, Object> timeMap = new HashMap<String, Object>();
		if (type ==0) {
			//季度
			//当前季度任务数据插入
			timeMap.put("begintDay", DateUtil2.getCurrentQuarterBegin());
			timeMap.put("endDay", DateUtil2.getCurrentQuarterEnd());
		}else if (type ==1) {
			//年度
			//当前年度任务数据插入
			timeMap.put("begintDay", year.format(new Date())+"-01-01");
			timeMap.put("endDay", year.format(new Date())+"-12-31");
		}
		List<PgdEntity> listRank = new ArrayList<PgdEntity>(); 
		listRank = mapper.getPgdRank(timeMap);
		if (listRank.isEmpty()) {
			return null;
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
			try {
				if (df_hh.parse(clsj).getTime()<=df_hh.parse(rwzj).getTime()) {
					a.setTaskScore(1);
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
			if (type == 0) {
				//当前季度
				returnMap.put("quarterAllPeopelRank",sortMapByValue(mapFirst30,"bitToSmall","all"));
				System.out.println("当前季度公司所有人排名为===="+sortMapByValue(mapFirst30,"bitToSmall","all").toString());
				//redisService.expire("First30", 60*60L);
			}else if(type == 1){
				//当前年
				returnMap.put("yearAllPeopelRank",sortMapByValue(mapFirst30,"bitToSmall","all"));
				//redisService.expire("First30", 60*60L);
			}
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//将公司得分前三十名的数据存入redis
		try {
			//当前季度
			if (type == 0) {
				returnMap.put("quarterFirst30",sortMapByValue(mapFirst30,"bitToSmall","30"));
				//redisService.expire("quarterFirst30", 60*60L);
			}else if(type == 1) {
				returnMap.put("yearFirst30",sortMapByValue(mapFirst30,"bitToSmall","30"));
				//redisService.expire("yearFirst30", 60*60L);
			}
			
			//当前年
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//将公司得分后三十名的数据存入redis
		try {
			if (type == 0) {
				returnMap.put("quarterEnd30",sortMapByValue(mapFirst30,"smallToBig","30"));
				//redisService.expire("quarterEnd30", 60*60L);
			}else if(type == 1) {
				returnMap.put("yearEnd30",sortMapByValue(mapFirst30,"smallToBig","30"));
				//redisService.expire("yearEnd30", 60*60L);
			}	
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//将按部门分组得分的数据存入redis{部门id:{张三:1，李四：2}}n
	 	Map<String,  Map<String, Integer>> pgdGroupByDep=	
	 		listDf.stream().filter(a->a.getDepartmentId()==null).filter(a->a.getDepartmentId()=="").collect(
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
	 	taskGroupByDep.forEach((key_str,value_map)->{
	 		 if (pgdGroupByDep.containsKey(key_str)) {
	 			 value_map.forEach((key_str_1,value_str_1)->pgdGroupByDep.get(key_str).merge(key_str_1, value_str_1, Integer::sum));
	 		}else
	 		 {
	 			 pgdGroupByDep.put(key_str,taskGroupByDep.get(key_str));
	 		}
	 	 });
		try {
			Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
			pgdGroupByDep.forEach((key,value)->{
				tempMap.put(key, value.toString());
			});
			if (type == 0) {
				if (!tempMap.isEmpty()) {
					returnMap.put("quarterGroupByDep",tempMap);
					//redisService.expire("quarterGroupByDep", 60*60L);
				}
			}else if(type == 1) {
				if (!tempMap.isEmpty()) {
					returnMap.put("yaerGroupByDep",tempMap);
					//redisService.expire("yearGroupByDep", 60*60L);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnMap.toString();
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
}
