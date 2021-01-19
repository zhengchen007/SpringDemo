package com.olo.ding.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

import javax.enterprise.inject.New;

/**
 * @author gcs
 * @date 2020/4/20 15:01
 */
public class DateUtil2 {
	private static SimpleDateFormat	year			= new SimpleDateFormat("yyyy");
	private static SimpleDateFormat	hm			= new SimpleDateFormat("HH:mm:ss");
	private static SimpleDateFormat	ymd			= new SimpleDateFormat("yyyy-MM-dd");
	private static SimpleDateFormat	ymdhm		= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/*
	 * 多线程SimpleDateFormat的线程安全问题解决
	 */
	 public static String hm_formatDate(Date date)throws ParseException{
	        synchronized(hm){
	            return hm.format(date);
	        }  
	    }
	    
	    public static Date hm_parse(String strDate) throws ParseException{
	        synchronized(hm){
	            return hm.parse(strDate);
	        }
	    } 
	    
	    public static String ymd_formatDate(Date date)throws ParseException{
	        synchronized(ymd){
	            return ymd.format(date);
	        }  
	    }
	    
	    public static Date ymd_parse(String strDate) throws ParseException{
	        synchronized(ymd){
	            return ymd.parse(strDate);
	        }
	    } 
	    
	    public static String ymdhm_formatDate(Date date)throws ParseException{
	        synchronized(ymdhm){
	            return ymdhm.format(date);
	        }  
	    }
	    
	    public static Date ymdhm_parse(String strDate) throws ParseException{
	        synchronized(ymdhm){
	            return ymdhm.parse(strDate);
	        }
	    } 
	
	
	private static Date				lunchBreak	= null;
	private static Date				workGoOn	= null;
	private static Date				offDuty		= null;
	private static Date				startWork	= null;
	static {
		try {
			startWork = hm_parse("08:30:00");
			lunchBreak = hm_parse("11:30:00");
			workGoOn = hm_parse("13:00:00");
			offDuty = hm_parse("18:00:00");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 工作日内时间加法计算
	 * 
	 * @param date
	 * @return
	 */
	public static Date addInWorkDate(Date date, double hour) throws ParseException {
//        hour = hour * 14;
		// 将要加的时间计算出来，需要加几天，每天按照8小时计算，超过整数天当成多一天计算
		// 先计算天数，如果添加了之后在周末，需要多加两天
		// 最后计算小时和分钟是否在工作时段，如果不在，顺延
		// 注意，虽然天数已经计算了，超过了也多加了一天，但是如果在哪一天最后的工作时段，多加了一个小时，还是会跨天：解决方法，将计算的时间加上天数剩余的小时，查看是否超过了最后的工作时间，如果超过了，再多加一天
		Double oneDay = 8d;
		Double days = hour / oneDay;
		Double littleNum = Double.valueOf("0." + days.toString().split("\\.")[1]);
		int intDay = days.intValue();
		Double remainHour = littleNum * oneDay;
		// 剩余的小时数转换为分，通过日历计算是否超过了最后的下班时间，如果超过了，天数加1,小时和天分开算
		Integer millSeconds = new Double(remainHour * 60 * 60 * 1000).intValue();
		Date jumpRestday = jumpRestday(date, intDay, millSeconds);
		Date jumpRestTime = jumpRestTime(date, millSeconds);
		Date ret = ymdhm_parse(ymd_formatDate(jumpRestday) + " " + hm_formatDate(jumpRestTime));
		return ret;
	}

	private static String checkAndFixHm(String time) throws ParseException {
		Date parse = hm_parse(time);
		if (parse.before(startWork)) {
			parse = startWork;
		} else if (parse.after(lunchBreak) && parse.before(workGoOn)) {
			parse = workGoOn;
		} else if (parse.after(offDuty)) {
			parse = startWork;
		}
		return hm_formatDate(parse);
	}

	/**
	 * 计算时间 时间校验
	 * 
	 * @param date
	 * @param millSeconds
	 * @return
	 */
	private static Date jumpRestTime(Date date, Integer millSeconds) throws ParseException {
		try {
			String dateStr = checkAndFixHm(hm_formatDate(date));
			date = hm_parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);

			/**
			 * 当前逻辑是：指定时间在中午以前，直接加，加完判断是否超过了午休时间，如果超过了，在下午上班时间的基础上增加差量
			 *
			 * 如果当前时间在下班以后，计算当前时间
			 *
			 *
			 * 正确的逻辑是：当前时间如果在中午下班之前，直接加，加完检查是否超过了午休，如果是，计算差量，将差量加到下午上班以后
			 *
			 * 如果指定时间在下午上班之后，直接加，加完检查是否超过了晚上下班，如果超过了，将差量加到第二天早上上班
			 *
			 */

			// 判断是否跨越了中午，指定的时间如果是在上午，加上时间量超过了午休时间就是跨越了中午，需要把多的时间加到下午去
			if (calendar.getTime().before(lunchBreak) || calendar.getTime().equals(lunchBreak)) {
				calendar.add(Calendar.MILLISECOND, millSeconds);
				Date temp = calendar.getTime();
				if (temp.after(lunchBreak)) {
					Long dif = temp.getTime() - lunchBreak.getTime();
					calendar.setTime(workGoOn);
					calendar.add(Calendar.MILLISECOND, dif.intValue());
					temp = calendar.getTime();
					return temp;
				}
			} else if (calendar.getTime().after(workGoOn) || calendar.getTime().equals(workGoOn)) {
				// 判断是否跨越了晚上，指定的时间如果超过了下班时间，需要把多余的时间从早上加
				calendar.add(Calendar.MILLISECOND, millSeconds);
				Date temp = calendar.getTime();
				if (temp.after(offDuty)) {
					Long dif = temp.getTime() - offDuty.getTime();
					calendar.setTime(startWork);
					calendar.add(Calendar.MILLISECOND, dif.intValue());
					temp = calendar.getTime();
					return temp;
				}
			}
			Date time = calendar.getTime();
			return time;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("jumpRestTime方法报错===="+e);
		}
		return null;
		
	}

	/**
	 * 判断时间是否超过了最晚下班时间，如果是，添加一天
	 * 
	 * @return
	 */
	private static int judgeIsAddOneDay(Date date, Integer millSeconds) throws ParseException {
		String dateStr = checkAndFixHm(hm_formatDate(date));
		date = hm_parse(dateStr);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		// 判断是否跨越了中午，指定的时间如果是在上午，加上时间量超过了午休时间就是跨越了中午，需要把多的时间加到下午去
		if (calendar.getTime().after(workGoOn) || calendar.getTime().equals(workGoOn)) {
			// 判断是否跨越了晚上，指定的时间如果超过了下班时间，需要把多余的时间从早上加
			calendar.add(Calendar.MILLISECOND, millSeconds);
			Date temp = calendar.getTime();
			if (temp.after(offDuty)) {
				return 1;
			}
		}
		return 0;
	}

	private static Date jumpRestday(Date timeVar, int amount, Integer millSeconds) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(timeVar);
		// 判断时间是否过了当天工作时间，如果过了，添加一天
		if (hm_parse(hm_formatDate(timeVar)).after(offDuty)) {
			amount = amount + 1;
		}
		
		// 判断时间是否超过了最晚下班时间，如果是，添加一天
		int i1 = judgeIsAddOneDay(timeVar, millSeconds);
		amount = amount + i1;
		
		//System.out.println("需要添加的天数:" + amount);
		calendar.add(Calendar.DATE, amount);
		
		Date time = calendar.getTime();
		return time;
	}

	public static String formatYmdhm(Date date) {
		String format;
		try {
			format = ymdhm_formatDate(date);
			return format;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) throws ParseException {
	}
	
	/**
	 * 获取本月的开始时间
	 * @return
	 */
	public static String getMonthStart(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		return ymd.format(calendar.getTime());
	}
	
	/**
	 * 获取本月的结束时间
	 * @return
	 */
	public static String getMonthEnd(){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DATE, 1);
		calendar.add(Calendar.MONTH, 1);
		calendar.add(Calendar.DATE, -1);
		return ymd.format(calendar.getTime());
	}
	
	/**
	 * 获取当前季度开始日期
	 */
	public static String getCurrentQuarterBegin() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int month = calendar.get(Calendar.MONTH)+1;
		String quarterBeginDate = "";
		switch (month) {
		case 1:
			quarterBeginDate=year.format(new Date())+"-01-01";
			break;
		case 2:
			quarterBeginDate=year.format(new Date())+"-01-01";
			break;
		case 3:
			quarterBeginDate=year.format(new Date())+"-01-01";
			break;
		case 4:
			quarterBeginDate=year.format(new Date())+"-04-01";
			break;
		case 5:
			quarterBeginDate=year.format(new Date())+"-04-01";
			break;
		case 6:
			quarterBeginDate=year.format(new Date())+"-04-01";
			break;
		case 7:
			quarterBeginDate=year.format(new Date())+"-07-01";
			break;
		case 8:
			quarterBeginDate=year.format(new Date())+"-07-01";
			break;
		case 9:
			quarterBeginDate=year.format(new Date())+"-07-01";
			break;
		case 10:
			quarterBeginDate=year.format(new Date())+"-10-01";
			break;
		case 11:
			quarterBeginDate=year.format(new Date())+"-10-01";
			break;
		case 12:
			quarterBeginDate=year.format(new Date())+"-10-01";
			break;
		default:
			break;
		}
		return quarterBeginDate;
	}	

	
	/**
	 * 获取当前季度结束日期
	 */
	public static String getCurrentQuarterEnd() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int month = calendar.get(Calendar.MONTH)+1;
		String quarterEndDate = "";
		switch (month) {
		case 1:
			quarterEndDate=year.format(new Date())+"-03-31";
			break;
		case 2:
			quarterEndDate=year.format(new Date())+"-03-31";
			break;
		case 3:
			quarterEndDate=year.format(new Date())+"-03-31";
			break;
		case 4:
			quarterEndDate=year.format(new Date())+"-06-30";
			break;
		case 5:
			quarterEndDate=year.format(new Date())+"-06-30";
			break;
		case 6:
			quarterEndDate=year.format(new Date())+"-06-30";
			break;
		case 7:
			quarterEndDate=year.format(new Date())+"-09-30";
			break;
		case 8:
			quarterEndDate=year.format(new Date())+"-09-30";
			break;
		case 9:
			quarterEndDate=year.format(new Date())+"-09-30";
			break;
		case 10:
			quarterEndDate=year.format(new Date())+"-12-31";
			break;
		case 11:
			quarterEndDate=year.format(new Date())+"-12-31";
			break;
		case 12:
			quarterEndDate=year.format(new Date())+"-12-31";
			break;
		default:
			break;
		}
		return quarterEndDate;
		
	}

}