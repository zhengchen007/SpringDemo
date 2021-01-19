package com.olo.ding.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.sun.tools.extcheck.Main;

public class DateUtil {
	private final static SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private final static SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 当前季度开始日期
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

	* 计算两个日期之间相差的天数

	* @param smdate 较小的时间

	* @param bdate 较大的时间

	* @return 相差天数

	* @throws ParseException

	*/

	public static int daysBetween(Date smdate,Date bdate) throws ParseException

	{

	Calendar cal = Calendar.getInstance();

	cal.setTime(smdate);

	long time1 = cal.getTimeInMillis();

	cal.setTime(bdate);

	long time2 = cal.getTimeInMillis();
	
	if (time1 == time2) {
		return 1;
	}

	long between_days=(time2-time1)/(1000*3600*24);

	return Integer.parseInt(String.valueOf(between_days));

	}
	
	/**
	 * 当前季度结束日期
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
	
	public static void main(String[] args) {
		String a = "2020-10-08 08:08";
		System.out.println(a.length());
	}
}
