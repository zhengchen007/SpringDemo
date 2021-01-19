package com.olo.ding.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.sun.tools.classfile.Annotation.element_value;

public class CZDateUtil {

	private final static SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
	static SimpleDateFormat df_time_hour = new SimpleDateFormat("yyyy-MM-dd HH");// 设置日期格式

	/**
	 * 当前季度开始日期
	 * 
	 * @return
	 */
	public static Date getCurrentQuarterStartTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3)
				c.set(Calendar.MONTH, 0);
			else if (currentMonth >= 4 && currentMonth <= 6)
				c.set(Calendar.MONTH, 3);
			else if (currentMonth >= 7 && currentMonth <= 9)
				c.set(Calendar.MONTH, 4);
			else if (currentMonth >= 10 && currentMonth <= 12)
				c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	/**
	 * 
	 * 计算两个日期之间相差的天数
	 * 
	 * @param smdate 较小的时间
	 * 
	 * @param bdate  较大的时间
	 * 
	 * @return 相差天数
	 * 
	 * @throws ParseException
	 * 
	 */

	public static int daysBetween(Date smdate, Date bdate) throws ParseException

	{

		Calendar cal = Calendar.getInstance();

		cal.setTime(smdate);

		long time1 = cal.getTimeInMillis();

		cal.setTime(bdate);

		long time2 = cal.getTimeInMillis();

		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));

	}

	/**
	 * 当前季度结束日期
	 * 
	 * @return
	 */
	public static Date getCurrentQuarterEndTime() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		Date now = null;
		try {
			if (currentMonth >= 1 && currentMonth <= 3) {
				c.set(Calendar.MONTH, 2);
				c.set(Calendar.DATE, 31);
			} else if (currentMonth >= 4 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 5);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 7 && currentMonth <= 9) {
				c.set(Calendar.MONTH, 8);
				c.set(Calendar.DATE, 30);
			} else if (currentMonth >= 10 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 11);
				c.set(Calendar.DATE, 31);
			}
			now = longSdf.parse(shortSdf.format(c.getTime()) + " 23:59:59");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return now;
	}

	public static String getDatePoor(Date endDate, Date nowDate) {

		long nd = 1000 * 24 * 60 * 60;
		long nh = 1000 * 60 * 60;
		long nm = 1000 * 60;
		// long ns = 1000;
		// 获得两个时间的毫秒时间差异
		long diff = endDate.getTime() - nowDate.getTime();
		// 计算差多少天
		long day = diff / nd;
		// 计算差多少小时
		long hour = diff % nd / nh;
		// 计算差多少分钟
		long min = diff % nd % nh / nm;
		// 计算差多少秒//输出结果
		// long sec = diff % nd % nh % nm / ns;
		return day + "天" + hour + "小时";
		// + min + "分钟";
	}

	/**
	 * 获取当前季度
	 * 
	 * @param args
	 */

	public static String getCurrentQuarter() {
		String nowDate = shortSdf.format(new Date());
		String nowMonth = nowDate.substring(5, 7);
		String currentQuarter = "-1";
		switch (nowMonth) {
		case "01":
			currentQuarter = "第一季度";
			break;
		case "02":
			currentQuarter = "第一季度";
			break;
		case "03":
			currentQuarter = "第一季度";
			break;
		case "04":
			currentQuarter = "第二季度";
			break;
		case "05":
			currentQuarter = "第二季度";
			break;
		case "06":
			currentQuarter = "第二季度";
			break;
		case "07":
			currentQuarter = "第三季度";
			break;
		case "08":
			currentQuarter = "第三季度";
			break;
		case "09":
			currentQuarter = "第三季度";
			break;
		case "10":
			currentQuarter = "第四季度";
			break;
		case "11":
			currentQuarter = "第四季度";
			break;
		case "12":
			currentQuarter = "第四季度";
			break;
		}
		return currentQuarter;
	}

	/**
	 * 获取终止日期后一期的前一天
	 * 
	 * @param d
	 * @param day
	 * @param type
	 * @return
	 * @throws ParseException
	 */
	public static Date addDate(Date d, long day, Integer type) throws ParseException {

		// 如果type是0，代表月份，如果type是1，代表季度
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(d);
		if (type == 0) {
			calendar.add(Calendar.MONTH, Integer.parseInt(String.valueOf(day)));
			return calendar.getTime();
		} else if (type == 1) {
			// 当前日期加多少季度返回日期
			calendar.add(Calendar.MONTH, (int) day * 3);// 增加一个季度
			return calendar.getTime();
		} else if (type == -1) {
			long time = d.getTime();
			day = day * 24 * 60 * 60 * 1000;
			time += day;
			return new Date(time);
		}
		return null;
	}

	
	
	/**
	 * 获取下个季度日期
	 * 
	 * @param d
	 * @param hour
	 * @return
	 * @throws ParseException
	 */
	public static Date getNextJd(Date d) throws ParseException {
		// 如果type是0，代表月份，如果type是1，代表季度
		Calendar calendar = Calendar.getInstance();
		// 当前日期加多少季度返回日期
		calendar.setTime(d);
		calendar.add(Calendar.MONTH, 3);// 增加一个季度
		return calendar.getTime();
	}

	public static Date addHour(Date d, long hour) throws ParseException {
		long time = d.getTime();
		hour = hour * 60 * 60 * 1000;
		time += hour;
		return new Date(time);
	}

	/**
	 * 获取当月倒数后三天的日期
	 * 
	 * @param lastDate
	 * @return
	 */
	public static String getDate(Integer lastDate) {
		// 获取前月的最后一天
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cale = Calendar.getInstance();
		cale = Calendar.getInstance();
		cale.add(Calendar.MONTH, 1);
		cale.set(Calendar.DAY_OF_MONTH, 0);
		String lastday = shortSdf.format(cale.getTime());
		if (lastDate == 1) {
			return lastday;
		} else if (lastDate == 2) {
			return String.valueOf(Integer.parseInt(lastday.substring(8, 10)) - 1);
		} else if (lastDate == 3) {
			return String.valueOf(Integer.parseInt(lastday.substring(8, 10)) - 2);
		}
		return null;
	}

	private static final SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ? yyyy");

	/***
	 * 功能描述：日期转换cron表达式
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateByPattern(Date date) {
		String formatTimeStr = null;
		if (Objects.nonNull(date)) {
			formatTimeStr = sdf.format(date);
		}
		return formatTimeStr;
	}

	/***
	 * convert Date to cron, eg "0 07 10 15 1 ? 2016"
	 * 
	 * @param date : 时间点
	 * @return
	 */
	public static String getCron(Date date) {
		return formatDateByPattern(date);
	}

	/**
	 * 计算第一期开始时间
	 * 
	 * @param args
	 */

	public static String getFirstTime(String taskCreatedate, Integer type, String[] zykssj) {
		// zykssj数组，[月，周，天，小时，分钟]
		try {
			SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm");
			String nowDate = sdf.format(new Date());
			String nowTime = sdf_time.format(new Date());
			// 0按天1按周 2按月 3按季度
			if (type == 0) {
				// 判断时间大小
				if (sdf_time.parse(nowTime).getTime() > sdf_time.parse(zykssj[3] + ":" + zykssj[4]).getTime()) {
					System.out.println("当前时间>开始日期");
						return addDay(taskCreatedate,"day",0,0,0)+" "+zykssj[3]+":"+zykssj[4];
				} else {
						return taskCreatedate+" "+zykssj[3]+":"+zykssj[4];
				}
			} else if (type == 1) {
				if (Integer.parseInt(dateToWeek(taskCreatedate)) > Integer.parseInt(zykssj[1])) {
					return addDay(getNowWeek(Integer.parseInt(taskCreatedate.substring(0, 4)),
							Integer.parseInt(taskCreatedate.substring(5, 7)), Integer.parseInt(taskCreatedate.substring(8, 10)),
							Integer.parseInt(zykssj[1])),"week",0,0,0)+" "+zykssj[3]+":"+zykssj[4];
				} else if (Integer.parseInt(dateToWeek(taskCreatedate)) == Integer.parseInt(zykssj[1])) {
					// 判断时间大小
								if (sdf_time.parse(nowTime).getTime() > sdf_time.parse(zykssj[3] + ":" + zykssj[4]).getTime()) {
									return addDay(getNowWeek(Integer.parseInt(taskCreatedate.substring(0, 4)),
											Integer.parseInt(taskCreatedate.substring(5, 7)), Integer.parseInt(taskCreatedate.substring(8, 10)),
											Integer.parseInt(zykssj[1])),"week",0,0,0)+" "+zykssj[3]+":"+zykssj[4];
							} else {
									return taskCreatedate+" "+zykssj[3]+":"+zykssj[4];
							}
				} else if (Integer.parseInt(dateToWeek(taskCreatedate)) < Integer.parseInt(zykssj[1])) {
					return getNowWeek(Integer.parseInt(taskCreatedate.substring(0, 4)),
							Integer.parseInt(taskCreatedate.substring(5, 7)), Integer.parseInt(taskCreatedate.substring(8, 10)),
							Integer.parseInt(zykssj[1]))+" "+zykssj[3]+":"+zykssj[4];
				}
			} else if (type == 2) {
				if (Integer.parseInt(taskCreatedate.substring(8, 10))>Integer.parseInt(zykssj[2])) {
									return addDay(taskCreatedate,"month",0,0,0).substring(0, 7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
				}else if(Integer.parseInt(taskCreatedate.substring(8, 10))==Integer.parseInt(zykssj[2])){
					            if (sdf_time.parse(nowTime).getTime() > sdf_time.parse(zykssj[3] + ":" + zykssj[4]).getTime()) {
					            	return addDay(taskCreatedate,"month",0,0,0).substring(0, 7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
								}else {
									return taskCreatedate+" "+zykssj[3]+":"+zykssj[4];
								}
				}else if (Integer.parseInt(taskCreatedate.substring(8, 10))<Integer.parseInt(zykssj[2])) {
									return taskCreatedate.substring(0,7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
				}
			} else if (type == 3) {
				System.out.println(123);
				if (Integer.parseInt(getMonthInQuarter(taskCreatedate))>Integer.parseInt(zykssj[0])) {
									return  addDay(taskCreatedate,"quarter",Integer.parseInt(getMonthInQuarter(taskCreatedate)),Integer.parseInt(zykssj[0]),0).substring(0, 7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
				}else if(Integer.parseInt(getMonthInQuarter(taskCreatedate))==Integer.parseInt(zykssj[0])){
					if (Integer.parseInt(taskCreatedate.substring(8,10)) > Integer.parseInt(zykssj[2])) {
									return  addDay(taskCreatedate,"quarter",0,0,0).substring(0, 7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
					}else if(Integer.parseInt(taskCreatedate.substring(8,10)) == Integer.parseInt(zykssj[2])){
						if (sdf_time.parse(nowTime).getTime() > sdf_time.parse(zykssj[3] + ":" + zykssj[4]).getTime()) {
									return  addDay(taskCreatedate,"quarter",0,0,0).substring(0, 7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
						}else {
									return taskCreatedate+" "+zykssj[3]+":"+zykssj[4];
						}
					}else if (Integer.parseInt(taskCreatedate.substring(8,10)) < Integer.parseInt(zykssj[2])) {
									return taskCreatedate.substring(0,7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
					}
				}else if (Integer.parseInt(getMonthInQuarter(taskCreatedate))<Integer.parseInt(zykssj[0])) {
					return  addDay(taskCreatedate,"quarter",Integer.parseInt(zykssj[0]),Integer.parseInt(getMonthInQuarter(taskCreatedate)),1).substring(0, 7)+"-"+zykssj[2]+" "+zykssj[3]+":"+zykssj[4];
				}
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return taskCreatedate;
	}

	
	/**
	 * 根据日期返会参数月份所在当前季度的第几个月
	 * 
	 */
	public static String getMonthInQuarter(String taskCreatedate) {
		String yf = taskCreatedate.substring(5,7);
		String dqy = "";
		switch (yf) {
		case "01":
			dqy = "1";
			break;
		case "02":
			dqy = "2";
			break;
		case "03":
			dqy = "3";
			break;
		case "04":
			dqy = "1";
			break;
		case "05":
			dqy = "2";
			break;
		case "06":
			dqy = "3";
			break;
		case "07":
			dqy = "1";
			break;
		case "08":
			dqy = "2";
			break;
		case "09":
			dqy = "3";
			break;
		case "10":
			dqy = "1";
			break;
		case "11":
			dqy = "2";
			break;
		case "12":
			dqy = "3";
			break;
		}
		return dqy;
	}
	
	/**
	     * 根据当前日期返回下个星期的指定星期几
	     * @param year
	     * @param month
	     * @param day
	     * @param xq
	     * @return
	     */
	   private static String getNextWeek(int year,int month,int day,int xq){
	    
			LocalDate localDate =  LocalDate.of(year, month, day);
			int now = localDate.getDayOfWeek().getValue();
			String returnStr = "";
			for (int i = 1; i < 8; i++) {
				if (xq == i) {
					returnStr = localDate.plusDays(7-now+i).toString();
				}
			}
		   return returnStr;
	    }

	/**
	     * 根据当前日期返回本个星期的指定星期几
	     * @param year
	     * @param month
	     * @param day
	     * @param xq
	     * @return
	     */
	   private static String getNowWeek(int year,int month,int day,int xq){
	    
			LocalDate localDate =  LocalDate.of(year, month, day);
			int now = localDate.getDayOfWeek().getValue();
			String returnStr = "";
			for (int i = 1; i < 8; i++) {
				if (xq == i) {
					returnStr = localDate.plusDays(i-now).toString();
				}
			}
		   return returnStr;
	    }

	/**
	     * 根据日期获取 星期 （2019-05-06 ——> 星期一）
	     * @param datetime
	     * @return
	     */
	    public static String dateToWeek(String datetime) {

	        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
	        //1-7周一到周日
	        String[] weekDays = {"7", "1", "2", "3", "4", "5", "6"};
	        Calendar cal = Calendar.getInstance();
	        Date date;
	        try {
	            date = f.parse(datetime);
	            cal.setTime(date);
	        } catch (ParseException e) {
	            e.printStackTrace();
	        }
	        //一周的第几天
	        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	        if (w < 0)
	            w = 0;
	        return weekDays[w];
	    }

	   /**
	    * 当前日期加一天,一个星期，或者一个星期，或者一个月，或者一个季度
	    * @param args
	    */
	    public static String addDay(String taskCreatedate,String type,Integer int1,Integer int2,Integer int3) {
	    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    		Date date;
				try {
					date = sdf.parse(taskCreatedate);
					Calendar   calendar   =   new   GregorianCalendar(); 
					calendar.setTime(date); 
					if ("day".equals(type)) {
						calendar.add(calendar.DATE,1);//把日期往后增加一天.整数往后推,负数往前移动 
					}else if ("week".equals(type)) {
						calendar.add(calendar.WEEK_OF_MONTH, 1);//把日期往后增加一个星期.整数往后推,负数往前移动
					}else if ("month".equals(type)) {
						System.out.println(type);
						calendar.add(calendar.MONTH, 1);//把日期往后增加一个月.整数往后推,负数往前移动
					}else if ("quarter".equals(type)) {
						//当前时间是季度中的第几个月，如果taskCreatedate是当前季度的第一个月， addQuarter=3
						//如果taskCreatedate是当前季度的第二个月，addQuarter=
						if (int3 == 1) {
							calendar.add(calendar.MONTH, 0-int2+int1);//把日期往后增加一个月.整数往后推,负数往前移动
						}else {
							calendar.add(calendar.MONTH, 3-int1+int2);//把日期往后增加一个月.整数往后推,负数往前移动
						}
					}
					//calendar.add(calendar.YEAR, 1);//把日期往后增加一年.整数往后推,负数往前移动
					
					date=calendar.getTime();   //这个时间就是日期往后推一天的结果 
					System.out.println(sdf.format(date));
					return sdf.format(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
	    }
	    /**
	     * 补位
	     * @param args
	     */
	    public static String bw(Integer num) {
	    	 if (num<10) {
				return "0"+String.valueOf(num);
			}
	    	 return String.valueOf(num);
	    }
	public static void main(String[] args) {
		// zykssj数组，[月，周，天，小时，分钟]
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
		Date today = new Date();//Mon Aug 27 00:09:29 CST 2018
        Date tomorrow = null;
        int delay = 1;
        int num = 10;//根据需要设置,这个值就是业务需要的n个工作日
        while(delay <= num){
            tomorrow = getTomorrow(today);
            //当前日期+1即tomorrow,判断是否是节假日,同时要判断是否是周末,都不是则将scheduleActiveDate日期+1,直到循环num次即可
            if(!isWeekend(sdf.format(tomorrow))){
                delay++;
                today = tomorrow;
            } else if (isWeekend(sdf.format(tomorrow))){
//                tomorrow = getTomorrow(tomorrow);
                today = tomorrow;
                System.out.println(sdf.format(tomorrow) + "::是周末");
            } 
        }
        System.out.println("10个工作日后,即计划激活日期为::" + sdf.format(today));
		
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
	}
	
	/**
	 * 获取几天后的工作日日期
	 */
	public static String getScheduleActiveDate(String beginDate,Integer numDay) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
		Date today =  sdf.parse(beginDate);
				//new Date();//Mon Aug 27 00:09:29 CST 2018
        Date tomorrow = null;
        int delay = 1;
        //int num = 10;//根据需要设置,这个值就是业务需要的n个工作日
        while(delay <= numDay){
            tomorrow = getTomorrow(today);
            //当前日期+1即tomorrow,判断是否是节假日,同时要判断是否是周末,都不是则将scheduleActiveDate日期+1,直到循环num次即可
            if(!isWeekend(sdf.format(tomorrow))){
                delay++;
                today = tomorrow;
            } else if (isWeekend(sdf.format(tomorrow))){
//                tomorrow = getTomorrow(tomorrow);
                today = tomorrow;
                System.out.println(sdf.format(tomorrow) + "::是周末");
            } 
        }
        System.out.println(numDay.toString()+"个工作日后,即计划激活日期为::" + sdf.format(today));
		return sdf.format(today);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
			return null;
		}
	}
	
	  /**
     * 获取明天的日期
     *
     * @param date
     * @return
     */
    public static Date getTomorrow(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        date = calendar.getTime();
        return date;
    }

    /**
     * 判断是否是weekend
     *
     * @param sdate
     * @return
     * @throws ParseException
     */
    public static boolean isWeekend(String sdate) throws ParseException {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(sdate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
            return true;
        } else{
            return false;
        }

    }
	
	/**
	 * 获取任务截至时间,list(0)天，list(1)日，list(2)时
	 */
	public static String getJzDate(List<String> list,String kssj) {
		//将时间转换为时间戳
		String day = list.get(0);
		String hour = list.get(1);
		String mins = list.get(2);
		long sc = Integer.parseInt(day)*24*3600000+Integer.parseInt(hour)*3600000+Integer.parseInt(mins)*60000;
		SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			long kssjLong = shortSdf.parse(kssj).getTime();
			String jzsj = shortSdf.format(kssjLong+sc);
			return jzsj;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "-1";
	}
	
}
