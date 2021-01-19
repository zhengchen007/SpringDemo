package com.olo.ding.controller;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.druid.util.StringUtils;
import com.olo.ding.entity.PgdEntity;
import com.olo.ding.entity.RankEntity;
import com.olo.ding.entity.UserInfo;
import com.olo.ding.mapper.RankMapper;
import com.olo.ding.mapper.UserInfoMapper;
import com.olo.ding.service.RedisService;
import com.olo.ding.task.Task;
import com.olo.ding.utils.DateUtil2;
import com.olo.ding.utils.GetBeanAndMethod;
import com.olo.ding.utils.JsonUtil;

/**
 * Created by xiaour on 2017/4/19.
 */
@RestController
@RequestMapping(value = "/test")
public class TestCtrl {

	@Autowired
	private RedisService redisService;

	@Autowired
	private UserInfoMapper userInfoMapper;

	@Autowired
	private RankMapper rankMapper;

	@RequestMapping(value="/getBean")
    public void getBean(){
		 try {
		  Class<?> clazz;
		  clazz = Class.forName("com.olo.ding.service.impl.TaskDistributeServiceImpl"); 
		  Method taskDistributeMapper = GetBeanAndMethod.getMethod("com.olo.ding.service.impl.TaskDistributeServiceImpl","test");
		  Object obj = taskDistributeMapper.invoke(clazz.newInstance());
		  System.out.println(obj.toString());
			} catch (Exception e) {
				// TODO: handle exception
			}
		  
		 
	}

	@RequestMapping(value = "/testRankMapper")
	public String testRankMapper() {
		Map<String, Object> parmMap = new HashMap<String, Object>();
		parmMap.put("beginTime", "2020-06-16");
		parmMap.put("endTime", "2020-06-31");
		List<PgdEntity> list_jqts = rankMapper.getHolidayCount(parmMap);
		return list_jqts.toString();
	}

	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式

	@RequestMapping(value = "/index")
	public String index() {

		// TODO Auto-generated method stub
		// 从redis中获取排名数据
		Map<String, Object> userMap_userid = new LinkedHashMap<String, Object>();
		userMap_userid.put("userId", 27089);
		// 个人得分，应得分，按时完成率，在公司排名,当前季度
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
				if (!StringUtils.isEmpty(list_jqts.get(0).getJqts())) {
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
		Map<String, Integer> mapPersonalScore = list_task_score.stream().collect(Collectors.groupingBy(
				RankEntity::getTaskReceiver, Collectors.reducing(0, RankEntity::getTaskScore, Integer::sum)));
		System.out.println(list_task_score.toString());
		Map<String, Integer> mapPeosonal = listDf.stream().collect(
				Collectors.groupingBy(PgdEntity::getCzr, Collectors.reducing(0, PgdEntity::getPf, Integer::sum)));
		// 把两个map中key值相同的value做累加
		mapPersonalScore.forEach((key, value) -> mapPeosonal.merge(key, value, Integer::sum));
		return mapPersonalScore.toString();
	}

	@RequestMapping(value = "/test/{id}")
	public Integer test(@PathVariable("id") int id) {
		UserInfo user = userInfoMapper.test(id);

		return user.getId();
	}

	/**
	 * 向redis存储值
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/set")
	public String set(String key, String value) throws Exception {

		redisService.set(key, value);
		return "success";
	}

	/**
	 * 获取redis中的值
	 * 
	 * @param key
	 * @return
	 */
	@RequestMapping("/get")
	public String get(String key) {
		System.out.println("123");
		try {
			return redisService.get(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 获取数据库中的用户
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/getUser/{id}")
	public String get(@PathVariable("id") int id) {
		try {
			System.out.println(333);
			UserInfo user = userInfoMapper.selectByPrimaryKey(id);
			return JsonUtil.getJsonString(user);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static void main(String[] args) {
		Map<String, Object> keyMap = new HashMap<>();
		keyMap.put("id", "编号");
		keyMap.put("name", "名称");

		String[] cnCloumn = { "编号", "名称" };

		System.out.println(Arrays.asList(convertMap(keyMap, cnCloumn)));

	}

	public static String[] convertMap(Map<String, Object> keyMap, String[] dataList) {

		for (int i = 0; i < dataList.length; i++) {

			for (Map.Entry<String, Object> m : keyMap.entrySet()) {
				if (m.getValue().equals(dataList[i])) {
					dataList[i] = m.getKey();
				}
			}
		}

		return dataList;
	}

	public static String getName(String name, String add) {
		return null;
	}

	public static void testGetClassName() {
		// 方法1：通过SecurityManager的保护方法getClassContext()
		String clazzName = new SecurityManager() {
			public String getClassName() {
				return getClassContext()[1].getName();
			}
		}.getClassName();
		System.out.println(clazzName);
		// 方法2：通过Throwable的方法getStackTrace()
		String clazzName2 = new Throwable().getStackTrace()[1].getClassName();
		System.out.println(clazzName2);
		// 方法3：通过分析匿名类名称()
		String clazzName3 = new Object() {
			public String getClassName() {
				String clazzName = this.getClass().getName();
				return clazzName.substring(0, clazzName.lastIndexOf('$'));
			}
		}.getClassName();
		System.out.println(clazzName3);
		// 方法4：通过Thread的方法getStackTrace()
		String clazzName4 = Thread.currentThread().getStackTrace()[2].getClassName();
		System.out.println(clazzName4);
	}

	@RequestMapping("/test")
	public String  test(String key) {
		try {
			throw new IllegalAccessException("测试异常通知");
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
}
