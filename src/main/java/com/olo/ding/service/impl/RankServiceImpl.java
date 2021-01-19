package com.olo.ding.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.olo.ding.entity.PgdEntity;
import com.olo.ding.entity.RankEntity;
import com.olo.ding.mapper.RankMapper;
import com.olo.ding.service.RankService;
import com.olo.ding.service.RedisService;
import com.olo.ding.task.Task;
import com.olo.ding.utils.CZDateUtil;
import com.olo.ding.utils.DateUtil2;

@Service("RankService")
public class RankServiceImpl implements RankService {
	@Autowired
	RankMapper rankMapper;
	@Autowired
	RedisService redisServer;
	private static final String SUCCESSCODE = "201";
	static SimpleDateFormat year = new SimpleDateFormat("yyyy");// 设置日期格式
	static SimpleDateFormat df_date = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
	static SimpleDateFormat df_time = new SimpleDateFormat("HH:mm");// 设置日期格式
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
	static SimpleDateFormat df_hh = new SimpleDateFormat("yyyy-MM-dd HH");// 设置日期格式

	@Override
	public String getRank(Integer userId, Integer cycleType) {
		// TODO Auto-generated method stub
		// 从redis中获取排名数据
		Map<String, Object> returnMap = new HashMap<String, Object>();
		Map<String, Object> dataMap = new HashMap<String, Object>();
		Map<String, Object> userMap_userid = new LinkedHashMap<String, Object>();
		
		if (cycleType == 0) {
			// 获取当季度第一天和最后一天
			String quarterBegintDay = DateUtil2.getCurrentQuarterBegin();
			String quarterEndDay = DateUtil2.getCurrentQuarterEnd();
			userMap_userid.put("begintDay", quarterBegintDay);
			userMap_userid.put("endDay", quarterEndDay);
		} else if (cycleType == 1) {
			// 获取当年的第一天和最后一天
			String yearBegintDay = year.format(new Date()) + "-01-01";
			String yearEndDay = year.format(new Date()) + "-12-31";
			userMap_userid.put("begintDay", yearBegintDay);
			userMap_userid.put("endDay", yearEndDay);
		}

		userMap_userid.put("userId", userId);
		try {
		/********************个人得分开始************************************/
		List<PgdEntity> listRank = rankMapper.getPersonalPgd(userMap_userid);
		listRank.stream().forEach(a -> {
			try {
				// 接收时间
				Date date_receivedate = df
						.parse(Task.computeReceivetime(a.getReceiveDate() + " " + a.getReceiveTime()));
				// 处理时间
				Date date_dealdate = df.parse(a.getOperateDate() + " " + a.getOperateTime());
				// 处理时间-接收时间(工作日时间)
				double gzrDouble = (DateUtil2.addInWorkDate(date_receivedate, Double.parseDouble(a.getDealTime())))
						.getTime();
				// 假期时间计算
				Map<String, Object> parmMap = new HashMap<String, Object>();
				parmMap.put("beginTime", a.getReceiveDate());
				parmMap.put("endTime", df.format(gzrDouble));
				List<PgdEntity> list_jqts = rankMapper.getHolidayCount(parmMap);
				double jqts_double = 0;
				if (list_jqts!=null&&list_jqts.get(0) !=null&&!StringUtils.isEmpty(list_jqts.get(0).getJqts())) {
					jqts_double = Double.parseDouble(list_jqts.get(0).getJqts());
				}
				String jzrq = df.format(gzrDouble + (jqts_double * 3600000));
				// 截止时间和处理时间比较,如果截止时间小于处理时间就不得分，反之则得分
				if (date_dealdate.compareTo(df.parse(jzrq)) == 1) {
					a.setIsGetScore(false);
				} else {
					a.setIsGetScore(true);
				}

			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		});

		// 过滤一个流程重复得分的情况，当一个流程的IsGetScore已经为true，就过滤掉相同的requestid
		List<PgdEntity> listDf = listRank.stream().filter(a -> a.getIsGetScore() == true)
				.collect(Collectors.collectingAndThen(
						Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PgdEntity::getRequestId))),
						ArrayList::new));
		System.out.println(listDf.toString());
		// 任务得分（除去会议任务）
		List<RankEntity> list_task_score = rankMapper.getPersonalTask(userMap_userid);
		list_task_score.forEach(a -> {
			String clsj = a.getOperatedate() + " " + a.getOperatetime();
			String rwzj = a.getRwjzrq();
				if (StringUtils.isEmpty(a.getOperatedate())||a.getOperatedate()==null) {
					clsj = df_hh.format(new Date());
				}
				try {
					if (df_hh.parse(clsj).getTime() <= df_hh.parse(rwzj).getTime()) {
						a.setTaskScore(1);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

		});

		Map<String, Integer> mapPersonalScore = list_task_score.stream().filter(a -> a.getTaskScore() != null)
				.collect(Collectors.groupingBy(RankEntity::getTaskReceiver,
						Collectors.reducing(0, RankEntity::getTaskScore, Integer::sum)));
		System.out.println(list_task_score.toString());
		Map<String, Integer> mapPeosonal = listDf.stream().collect(
				Collectors.groupingBy(PgdEntity::getCzr, Collectors.reducing(0, PgdEntity::getPf, Integer::sum)));
		// 把两个map中key值相同的value做累加
		mapPersonalScore.forEach((key, value) -> mapPeosonal.merge(key, value, Integer::sum));
		
		/********************个人得分结束************************************/
		
		Map<String, Object> allPeopelRankMap = new HashMap<String, Object>();
		if (cycleType == 0) {
			//allPeopelRankMap = redisServer.hget("quarterAllPeopelRank", HashMap.class);
			String quarterAllPeopelRankStr = redisServer.get("quarterAllPeopelRank");
			allPeopelRankMap = JSONObject.parseObject(quarterAllPeopelRankStr);
			// allPeopelRankMap_int =
			// allPeopelRankMap_int.entrySet().stream().sorted(comparingByValue()).collect(toMap(Map.Entry::getKey,
			// Map.Entry::getValue, (e1, e2) -> e2,LinkedHashMap::new));
		} else if (cycleType == 1) {
			//allPeopelRankMap = redisServer.hget("yearAllPeopelRank", HashMap.class);
			String yearAllPeopelRankStr = redisServer.get("yearAllPeopelRank");
			allPeopelRankMap = JSONObject.parseObject(yearAllPeopelRankStr);
			
			// allPeopelRankMap_int =
			// allPeopelRankMap_int.entrySet().stream().sorted(comparingByValue()).collect(toMap(Map.Entry::getKey,
			// Map.Entry::getValue, (e1, e2) -> e2,LinkedHashMap::new));
		}
		Map<String, Integer> allPeopelRankMap_int = new HashMap<String, Integer>();
		for (Entry<String, Object> entry : allPeopelRankMap.entrySet()) {
			allPeopelRankMap_int.put(entry.getKey(), Integer.parseInt(String.valueOf(entry.getValue())));
		}
		allPeopelRankMap_int = Task.sortMapByValue(allPeopelRankMap_int, "bitToSmall", "all");
		System.out.println("全公司排名为======" + allPeopelRankMap_int.toString());
		
		
		
		dataMap.put("scoreByPersonal", mapPeosonal);
		// 个人应得分
		Integer needToGetScore = (rankMapper.getPersonalTaskCount(userMap_userid).get(0) == null ? 0
				: rankMapper.getPersonalTaskCount(userMap_userid).get(0).getPersonalDealTaskCount())
				+ (rankMapper.getPersonalPgdCount(userMap_userid).get(0) == null ? 0
						: rankMapper.getPersonalPgdCount(userMap_userid).get(0).getPersonalPgdCount());
		dataMap.put("needToGetScore", needToGetScore);
		// 按时完成率
		double completionRate = 0.0;
		double personalScore = 0.0;
		if (needToGetScore > 0 && mapPeosonal != null && !mapPeosonal.isEmpty()) {
			personalScore = (mapPeosonal.get(userId.toString()));
			completionRate = ((double) (Math.round(personalScore * 100 / needToGetScore) / 100.0)) * 100;
		}
		dataMap.put("completionRate", String.format("%10.1f%%", completionRate).trim());

		// 在公司排名
		/**************************************************************
		 * 个人在公司排名开始
		 ************************************************************/
		

			int rank_all = 1;
			List<Map<String, Object>> allPeopelRankList = new ArrayList<Map<String, Object>>();
			for (Entry<String, Integer> entry : allPeopelRankMap_int.entrySet()) {
				Map<String, Object> fisst30Map_lastName = new LinkedHashMap<String, Object>();
				Map<String, Object> userMap = new HashMap<String, Object>();
				System.out.println("entry.getKey()="+entry.getKey());
				if (userId.toString().equals(entry.getKey())) {
					userMap.put("userId", Integer.parseInt(entry.getKey()));
					List<RankEntity> list = rankMapper.getPeopleName(userMap);
					fisst30Map_lastName.put("name", list.get(0).getLastName());
					fisst30Map_lastName.put("score", entry.getValue());
					fisst30Map_lastName.put("rank", rank_all);
					allPeopelRankList.add(fisst30Map_lastName);
					break;
				}
				rank_all++;
			}
			dataMap.put("personalRank", allPeopelRankList);
			/**************************************************************
			 * 个人在公司排名结束
			 ************************************************************/

			// 当前季度
			dataMap.put("currentQuarter", CZDateUtil.getCurrentQuarter());
			String currentYear = df_date.format(new Date());
			dataMap.put("currentYear", currentYear.substring(0, 4) + "年");

			// 公司前三十
			Map<String, Object> first30Map = new HashMap<String, Object>();
			if (cycleType == 0) {
				String first30 = redisServer.get("quarterFirst30");
				first30Map = JSONObject.parseObject(first30);
			} else if (cycleType == 1) {
				String first30 = redisServer.get("yearFirst30");
				first30Map =JSONObject.parseObject(first30);
			}
			Map<String, Integer> first30Map_int = new HashMap<String, Integer>();
			for (Entry<String, Object> entry : first30Map.entrySet()) {
				first30Map_int.put(entry.getKey(), Integer.parseInt(String.valueOf(entry.getValue())));
			}
			first30Map_int = Task.sortMapByValue(first30Map_int, "bitToSmall", "30");
			List<Map<String, Object>> first30List = new ArrayList<Map<String, Object>>();
			int rank_first_30 = 1;
			for (Entry<String, Integer> entry : first30Map_int.entrySet()) {
				Map<String, Object> fisst30Map_lastName = new LinkedHashMap<String, Object>();
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("userId", Integer.parseInt(entry.getKey()));
				List<RankEntity> list = rankMapper.getPeopleName(userMap);
				fisst30Map_lastName.put("name", list.get(0).getLastName());
				fisst30Map_lastName.put("score", entry.getValue());
				fisst30Map_lastName.put("rank", rank_first_30);
				first30List.add(fisst30Map_lastName);
				rank_first_30++;
			}
			dataMap.put("First30", first30List);

			// 公司后三十
			Map<String, Object> end30Map = new HashMap<String, Object>();
			if (cycleType == 0) {
				String end30 = redisServer.get("quarterEnd30");
				end30Map = JSONObject.parseObject(end30);
			} else if (cycleType == 1) {
				String end30 = redisServer.get("yearEnd30");
				end30Map = JSONObject.parseObject(end30);
			}

			Map<String, Integer> end30Map_int = new HashMap<String, Integer>();
			for (Entry<String, Object> entry : end30Map.entrySet()) {
				end30Map_int.put(entry.getKey(), Integer.parseInt(String.valueOf(entry.getValue())));
			}
			end30Map_int = Task.sortMapByValue(end30Map_int, "smallToBig", "30");
			List<Map<String, Object>> end30MapList = new ArrayList<Map<String, Object>>();
			int rank_end_30 = 1;
			for (Entry<String, Integer> entry : end30Map_int.entrySet()) {
				Map<String, Object> end30Map_lastName = new LinkedHashMap<String, Object>();
				Map<String, Object> userMap = new HashMap<String, Object>();
				userMap.put("userId", Integer.parseInt(entry.getKey()));
				List<RankEntity> list = rankMapper.getPeopleName(userMap);
				end30Map_lastName.put("name", list.get(0).getLastName());
				end30Map_lastName.put("score", entry.getValue());
				end30Map_lastName.put("rank", rank_end_30);
				end30MapList.add(end30Map_lastName);
				rank_end_30++;
			}
			dataMap.put("End30", end30MapList);

			// 同部门，根据当前人的id查询当前人的下属部门部门id去map中根据key做匹配获取同部门排名
			Map<String, Object> parmMap = new JSONObject();
			parmMap.put("userId", userId);
			List<RankEntity> list_bm = rankMapper.getDepIdAndName(parmMap);
			Map<String, Object> groupByDepMap = new HashMap<String, Object>();

			if (cycleType == 0) {
				String groupByDep = redisServer.get("quarterGroupByDep");
				groupByDepMap = JSONObject.parseObject(groupByDep);
			} else if (cycleType == 1) {
				String groupByDep = redisServer.get("yaerGroupByDep");
				groupByDepMap = JSONObject.parseObject(groupByDep);
			}

			List<Map<String, Object>> groupByDepList = new ArrayList<Map<String, Object>>();
			for (int i = 0; i < list_bm.size(); i++) {
				String str = (String) groupByDepMap.get(list_bm.get(i).getDepartmentId());
				Map<String, Object> groupByDepMap_querybydec = new HashMap<String, Object>();
				if (!StringUtils.isEmpty(str)) {
					groupByDepMap_querybydec = getStringToMap(str.replace("{", "").replace("}", ""));
				}
				Map<String, Integer> groupByDepMap_querybydec_int = new HashMap<String, Integer>();
				for (Entry<String, Object> entry : groupByDepMap_querybydec.entrySet()) {
					groupByDepMap_querybydec_int.put(entry.getKey(), Integer.parseInt(String.valueOf(entry.getValue())));
				}
				groupByDepMap_querybydec_int = Task.sortMapByValue(groupByDepMap_querybydec_int, "bitToSmall", "all");
				int rank_dep_30 = 1;
				for (Entry<String, Integer> entry : groupByDepMap_querybydec_int.entrySet()) {
					Map<String, Object> dep_lastName = new LinkedHashMap<String, Object>();
					Map<String, Object> userMap = new HashMap<String, Object>();
					userMap.put("userId", Integer.parseInt(entry.getKey().trim()));
					List<RankEntity> list = rankMapper.getPeopleName(userMap);
					dep_lastName.put("name", list.get(0).getLastName());
					dep_lastName.put("score", entry.getValue());
					dep_lastName.put("rank", rank_dep_30);
					groupByDepList.add(dep_lastName);
					rank_dep_30++;
				}
			}
			dataMap.put("groupByDep", groupByDepList);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		returnMap.put("code", SUCCESSCODE);
		returnMap.put("data", dataMap);
		JSONObject jsonObj = new JSONObject(returnMap);
		return JSONObject.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);

	}

	public static Map<String, Object> getStringToMap(String str) {
		// 判断str是否有值
		if (null == str || "".equals(str)) {
			return null;
		}
		// 根据&截取
		String[] strings = str.split(",");
		// 设置HashMap长度
		int mapLength = strings.length;
		// 判断hashMap的长度是否是2的幂。
		if ((strings.length % 2) != 0) {
			mapLength = mapLength + 1;
		}

		Map<String, Object> map = new HashMap<>(mapLength);
		// 循环加入map集合
		for (int i = 0; i < strings.length; i++) {
			// 截取一组字符串
			String[] strArray = strings[i].split("=");
			// strArray[0]为KEY strArray[1]为值
			map.put(strArray[0], strArray[1]);
		}
		return map;
	}

}
