package com.olo.ding.utils;




import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Clob;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("AlibabaAvoidPatternCompileInMethod")
public class DealString {
	/**
	 * Logger for this class
	 */
	private final static String[] hex = { "00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B", "0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D", "1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B", "9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD", "AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF", "C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1", "D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3", "E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5", "F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"};
    private final static byte[]   val = { 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F };

	
    /**灏嗗瓧绗︿覆鍊掑簭  */
    public static String toStringByReverse(String allstr) {
    	StringBuffer sBuffer=new StringBuffer();
    	sBuffer.append(allstr);
    	sBuffer.reverse();
		return sBuffer.toString();
	}
    
    /** 鎶婂瓧绗︿覆杞崲涓烘暟缁勶紝allstr浠�","闂撮殧 */
	public static String[] getAnyone(String allstr) {
		if (allstr == null || "".equals(allstr)) {
			return null;
		}
        String[] anyone = null;
		try {
				anyone = allstr.split(",");
			} catch (Exception ex) {
				return null;
			}
		return anyone;
	}
    
    /** 
     * 鎶婂瓧绗︿覆杞崲涓烘暟缁�(鍘婚櫎绌哄�间互鍙婇潪鏁板�煎瀷鍙傛暟鍒ゆ柇)
     * allstr浠�","闂撮殧 
     * @param type  1:鏁板��(鍘婚櫎闈炴暟鍊煎瀷鍙傛暟)   2瀛楃涓�((鍘婚櫎绌哄��)
     * */
	public static String[] getAnyZeroWithTrim(String allstr,int type) {		
		if (allstr == null || "".equals(allstr)) {
			return null;
		}
        String[] anyone = null;
		try {
				String[] strs = allstr.split(",");				
				if(strs!=null&&strs.length>0){
					ArrayList<String> list=new ArrayList<String>();
					 for(String str:strs){
						 if(type==1){
							 str=DealString.toStringCheckLong(str,-100);
							 if(!"-100".equals(str)){
								 list.add(str); 
							 }
						 }else{
							 str=DealString.toString(str);
							 if(str.length()>0){
								 list.add(str); 							 
							 }	
						 }						 					 
					 }
					 if(list!=null&&list.size()>0){
						 anyone= list.toArray(new String[list.size()]);
					 }
				}
			} catch (Exception ex) {
				return null;
			}
		return anyone;
	}	
	
	/** 鎶妉ist杞崲涓� "," 闂撮殧鐨勫瓧绗︿覆(鍙敤浜巗ql in)
	 * @param type  1:鏁板��   2瀛楃涓�
	 *  */
	public static String listToString(List<?> list,int type){
        if (list==null) {
            return null;
        }
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        for (Object obj : list) {
            if (flag) {
                result.append(",");
            }else {
                flag=true;
            }
            if(type==2) {
                result.append("'"+obj.toString()+"'");
            } else {
                result.append(obj.toString());
            }
        }
        return result.toString();
    }
	
	
	
	/** 鎶妉ist杞崲涓簂ist,闄愬畾澶у皬鎷嗗垎(鍙敤浜巗ql in)
	 *  list 鍊间负 "," 闂撮殧鐨勫瓧绗︿覆
	 * @param type  1:鏁板��   2瀛楃涓�
	 * @param MaxNum 闂撮殧澶у皬
	 *  */
	public static List<String> listToStringForList(List<?> list,int type,int MaxNum){
        if (list==null||list.size()<=0) {
            return new ArrayList<String>();
        }
        boolean flag=false;
        StringBuilder result=new StringBuilder();
        List<String> rList=new ArrayList<String>();
        for (int i=0;i<list.size();i++) {
        	Object obj = list.get(i);
            if (flag) {
                result.append(",");
            }else {
                flag=true;
            }
            if(type==2) {
                result.append("'"+obj.toString()+"'");
            } else {
                result.append(obj.toString());
            }
            if((i+1)%MaxNum==0||(i==list.size()-1)){
            	rList.add(result.toString());
            	result=new StringBuilder(); 
            	flag=false;
            }
        }
        return rList;
    }
	
	/** 鎶婂瓧绗︿覆杞崲涓烘暟缁勶紝浠�"" */
	public static  String[] getAnytwo(String allstr) {
		//log.info("getAnytwo(),鍘熷瓧绗︿覆:"+allstr);
		int j = 0;
		if (allstr == null || "".equals(allstr)) {
			return null;
		}
		j = allstr.length();
		if(j>0){
            String[] anyone = new String[j];
		  for (int i = 0; i < j; i++) {
			anyone[i]=allstr.substring(i,i+1);			
		  }
		 return anyone;
		}else {
            return null;
        }
	}
	
	/** 
	 * 鎶婂瓧绗︿覆杞崲涓哄瓧绗︿覆(鍘婚櫎绌哄�间互鍙婇潪鏁板�煎瀷鍙傛暟鍒ゆ柇)(鍙敤浜巗ql in)     
     * allstr浠�","闂撮殧 
     * @param type  1:鏁板��(鍘婚櫎闈炴暟鍊煎瀷鍙傛暟)   2瀛楃涓�((鍘婚櫎绌哄��)
     * */
	public static String strToStr(String allstr,int type){
		String resultStr="";
		String[] strs=getAnyZeroWithTrim(allstr,type);		
        StringBuilder result=new StringBuilder();
        boolean flag=false;
        if(strs!=null&&strs.length>0){
        for (String obj : strs) {
            if (flag) {
                result.append(",");
            }else {
                flag=true;
            }            
            if(type==2) {
                result.append("'"+obj+"'");
            } else {
                result.append(obj);
            }
         }
          resultStr=result.toString();
        }
        return resultStr;
    }
	
	
	/** 鎶婂瓧绗︿覆杞崲涓烘暟缁勶紝浠�"*" */
	public static String[] getXing1(String allstr) {
		int j = 0;
		if (allstr == null || "".equals(allstr)) {
			return null;
		}
		j = allstr.length();
        String[] anyone = null;
		for (int i = 0; i < j; i++) {
			try {
				anyone = allstr.split("[*]");
				// System.out.println(anyone[i]);
			} catch (ArrayIndexOutOfBoundsException ex) {}
		}
		
	return anyone;
	}

	/** 鎶婂瓧绗︿覆杞崲涓烘暟缁勶紝浠�"鈼�" */
	public static String[] getOne(String allstr) {
		int j = allstr.length();
        String[] anyone = null;
		for (int i = 0; i < j; i++) {
			try {
				anyone = allstr.split("鈼�");
			} catch (ArrayIndexOutOfBoundsException ex) {

			}
		}
		return anyone;
	}

	/** 鎶婂瓧绗︿覆杞崲涓烘暟缁勶紝浠�"鈽�" */
	public static String[] getXing(String allstr) {
		int j = allstr.length();
        String[] anyone = null;
		for (int i = 0; i < j; i++) {
			try {
				anyone = allstr.split("鈽�");
			} catch (ArrayIndexOutOfBoundsException ex) {

			}
		}
		return anyone;
	}
	/** 鎶婂瓧绗︿覆杞崲涓烘暟缁勶紝浠�"-" */
	public static String[] getHeng(String allstr) {
		int j = allstr.length();
        String[] anyone = null;
		for (int i = 0; i < j; i++) {
			try {
				anyone = allstr.split("-");
			} catch (ArrayIndexOutOfBoundsException ex) {

			}
		}
		return anyone;
	}
	/** 鍒ゆ柇鍓嶈�呮槸鍚﹀寘鍚湪鍚庤�呬腑
	 *  鏈� 杩斿洖true
	 * */
	public static boolean toStrIsHave(String str,String strs) {
		str=toString(str);
		strs=toString(strs);
		str=str.toUpperCase();
		strs=strs.toUpperCase();
        return strs.indexOf(str) != -1;
	}
    
	/** 瀛楃涓插幓闄ゆ牱寮�*/
	public static String replaceValue(String value) {
		//System.out.println("1:"+value);
		value=value.replaceAll("<[^<|^>]*>","");	
		//System.out.println("2"+value);
		return value;	
	}
	/** 瀛楃涓插幓闄tml鏍囩*/
	public static String getTxtWithoutHTMLElement(String element) {

		if (null == element || "".equals(element.trim())) {
			return element;
		}
		Pattern pattern = Pattern.compile("< [^< |^>]*>");
		Matcher matcher = pattern.matcher(element);
		StringBuffer txt = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group();
			if (group.matches("< [\\s]*>")) {
				matcher.appendReplacement(txt, group);
			} else {
				matcher.appendReplacement(txt, "");
			}
		}
		matcher.appendTail(txt);
		repaceEntities(txt, "&amp;", "&");
		repaceEntities(txt, "&lt;", "<");
		repaceEntities(txt, "&gt;", ">");
		repaceEntities(txt, "&quot;", "\"");
		repaceEntities(txt, "&nbsp;", "");
		return txt.toString();
	}
	private static void repaceEntities(StringBuffer txt, String entity,
			String replace) {
		int pos = -1;
		while (-1 != (pos = txt.indexOf(entity))) {
			txt.replace(pos, pos + entity.length(), replace);
		}
	}
	public static String getDateTime() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyyMMddHHmmss");
		String time = f.format(new Date());
		return time;
	}

	/** 鎶婃暟缁勮浆鎹负瀛楃涓� */
	public static String getAllstr(String[] anyone) {
		String str = "";
		if (anyone == null) {
			return str;
		}
		int j = anyone.length;

		for (int i = 0; i < j - 1; i++) {
			str = str + anyone[i] + ",";
		}
		str = str + anyone[j - 1];
		return str;
	}

	/** 鎶妌ull杞寲涓�" " */
	public static String toString(String str) {
		if (str == null) {
            str = "";
        }
		if (str.equals("null")) {
            str = "";
        }
		str = str.trim();
		return str;
	}
	/** 鎶妌ull杞寲涓�" ",鍒ゆ柇闈炴硶鍙傛暟 */
	public static String toStringCheckString(String str) {
		if (str == null) {
            str = "";
        }
		if (str.equals("null")) {
            str = "";
        }
		str = str.trim();
		str=sql_inj_replaceall(str);
		return str;
	}
	/** 鎶妌ull杞寲涓�" ",鍒ゆ柇long鍨嬪弬鏁�,榛樿鍊� */
	public static String toStringCheckLong(String str,long s) {
		if (str == null) {
            str = "";
        }
		if (str.equals("null")) {
            str = "";
        }
		str = str.trim();
		try {
			str=Long.parseLong(str)+"";
		} catch (Exception e) {
			str=s+"";
		}
		return str;
	}

	/** 鎶妌ull杞寲涓�" ",鍒ゆ柇int鍨嬪弬鏁帮紝榛樿鍊� */
	public static String toStringCheckInt(String str,int s) {
		if (str == null) {
            str = "";
        }
		if (str.equals("null")) {
            str = "";
        }
		str = str.trim();
		try {
			str=Integer.parseInt(str)+"";
		} catch (Exception e) {
			str=s+"";
		}
		return str;
	}

	/** 鎶婃枃鏈煙涓殑鎹㈣绗︽浛鎹负html鏍囩 */
	public static String toStringForTextArea(String value){
		if(value==null||"".equals(value)) {
            return "";
        }
		String v = "";
	    v=value.replaceAll("\"","鈥�");
	    v=value.replaceAll("\'","鈥�");
	    v=value.replaceAll("<","銆�");
	    v=value.replaceAll(">","銆�");
	    v=sql_inj_replaceall(v);
		return v;
	}

	/** 杞崲缂栫爜 涓枃澶勭悊 */
	public static String toGBK(String str) {
		try {
			if (str == null) {
                str = "";
            } else {
                str = new String(str.getBytes(StandardCharsets.ISO_8859_1), "GBK");
            }
		} catch (Exception e) {
		}
		return str;
	}

	/**
	 * 鏍规嵁绯荤粺鏃堕棿 鐢熸垚10浣� 鍒扮 鏃堕棿鎴�(1552102984)
	 * @return
	 */
	public static String getCurrentTime10() {
		return System.currentTimeMillis()/1000+"";
	}

	/**
	 * 鏍规嵁绯荤粺鏃堕棿 鐢熸垚13浣� 鍒版绉掔 鏃堕棿鎴�(1552102984000)
	 * @return
	 */
	public static String getCurrentTime13() {
		return System.currentTimeMillis()+"";
	}

	/** 杞崲瀛楃涓蹭负鏃堕棿 鏍煎紡2007-5 */
	public static String dateTostr(Date date,String format) {
		if(date==null){
			return "";
		}
		try {
			SimpleDateFormat sdf2 = new SimpleDateFormat(format);
			String newstr = sdf2.format(date);
			return newstr;
		}catch (Exception e){
			return "";
		}
	}

	/** 杞崲瀛楃涓叉椂闂存牸寮忎负鎸囧畾鏍煎紡浠�,  sourFormat 鍘熸牸寮�,objFormat 鐩爣鏍煎紡*/
	public static String strTotime0(String str,String sourFormat,String objFormat) {
		if("".equals(str)||str==null)
		{
			return "";
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat(sourFormat);
		Date date = null;
		try {
			date = sdf1.parse(str);
		} catch (ParseException e) {
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat(objFormat);
		String newstr = sdf2.format(date);
		return newstr;

	}

	/** 杞崲瀛楃涓蹭负鏃堕棿 鏍煎紡2007-5 */
	public static String strTotime(String str) {
		if("".equals(str)||str==null)
		{
			return "";
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-M");
		Date date = null;
		try {
			date = sdf1.parse(str);
		} catch (ParseException e) {
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-M");
		String newstr = sdf2.format(date);
		return newstr;

	}
	/** 杞崲瀛楃涓蹭负鏃堕棿 鏍煎紡2007-5-30 */
	public static String strTotimeone(String str) {
		if("".equals(str)||str==null)
		{
			return "";
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf1.parse(str);
		} catch (ParseException e) {
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
		String newstr = sdf2.format(date);
		return newstr;

	}
	/** 鏍煎紡鍖栨椂闂� 鏍煎紡2007-5-30 2:30 */
	public static String strTotimethree(String str) {
		if("".equals(str)||str==null)
		{
			return "";
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		Date date = null;
		try {
			date = sdf1.parse(str);
		} catch (ParseException e) {
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String newstr = sdf2.format(date);
		return newstr;

	}

	/** 杞崲瀛楃涓蹭负鏃堕棿 鏍煎紡2007-05-30 01:30:20 */
	public static String strTotimetwo(String str) {
		if("".equals(str)||str==null)
		{
			return "";
		}
		SimpleDateFormat sdf1 = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf1.parse(str);
		} catch (ParseException e) {
		}
		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String newstr = sdf2.format(date);
		return newstr;

	}
	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 01:30:44 */
	public static Date getDateTimetwoDate(String str) {
		//娉ㄦ剰锛歋impleDateFormat鏋勯�犲嚱鏁扮殑鏍峰紡涓巗trDate鐨勬牱寮忓繀椤荤浉绗�
		 SimpleDateFormat  simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  //蹇呴』鎹曡幏寮傚父try
		 Date date=null;
		 try {
			 date=simpleDateFormat.parse(str);
		  // System.out.println(date);
		  }
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }
        return date;
	}
	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 01:30:44.345 */
	public static Date getDateTimethreeDate(String str) {
		//娉ㄦ剰锛歋impleDateFormat鏋勯�犲嚱鏁扮殑鏍峰紡涓巗trDate鐨勬牱寮忓繀椤荤浉绗�
		 SimpleDateFormat  simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		  //蹇呴』鎹曡幏寮傚父try
		 Date date=null;
		 try {
			 date=simpleDateFormat.parse(str);
		  // System.out.println(date);
		  }
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }
        return date;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 */
	public static Date getDateTimeDate() {
		//娉ㄦ剰锛歋impleDateFormat鏋勯�犲嚱鏁扮殑鏍峰紡涓巗trDate鐨勬牱寮忓繀椤荤浉绗�
		 SimpleDateFormat  simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		  //蹇呴』鎹曡幏寮傚父try
		 Date date=null;
		 String str=getDateTimeone();
		 try {
			 date=simpleDateFormat.parse(str);
		   //System.out.println(date);
		  }
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }
       return date;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 01:30:44 */
	public static Date getDateTimetwoDate() {
		//娉ㄦ剰锛歋impleDateFormat鏋勯�犲嚱鏁扮殑鏍峰紡涓巗trDate鐨勬牱寮忓繀椤荤浉绗�
		 SimpleDateFormat  simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  //蹇呴』鎹曡幏寮傚父try
		 Date date=null;
		 String str=getDateTimetwo();
		 try {
			 date=simpleDateFormat.parse(str);
		  // System.out.println(date);
		  }
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }
        return date;
	}
	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 01:30:44.345 */
	public static Date getDateTimethreeDate() {
		//娉ㄦ剰锛歋impleDateFormat鏋勯�犲嚱鏁扮殑鏍峰紡涓巗trDate鐨勬牱寮忓繀椤荤浉绗�
		 SimpleDateFormat  simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		  //蹇呴』鎹曡幏寮傚父try
		 Date date=null;
		 String str=getDateTimeThree();
		 try {
			 date=simpleDateFormat.parse(str);
		  // System.out.println(date);
		  }
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }
        return date;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 */
	public static Date getDateTimeDate(String str) {
		//娉ㄦ剰锛歋impleDateFormat鏋勯�犲嚱鏁扮殑鏍峰紡涓巗trDate鐨勬牱寮忓繀椤荤浉绗�
		 SimpleDateFormat  simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
		  //蹇呴』鎹曡幏寮傚父try
		 Date date=null;
		 try {
			 date=simpleDateFormat.parse(str);
		   //System.out.println(date);
		  }
		  catch(ParseException px)
		  {
		   px.printStackTrace();
		  }
       return date;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05 */
	public static String getDateTimeFour() {
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM");
		String time = f.format(new Date());
		return time;
	}
	/** 鍙栧緱绯荤粺褰撳墠鏃堕棿缂栧彿 绮剧‘鍒版绉� */
	public static String getDateTime0() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyyMMddHHmmssSSS");
		String time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆棩鏈� 鏍煎紡2007-5-30 */
	public static String getDateTimeone() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy-MM-dd");
		String time = f.format(new Date());
		return time;
	}
	/** 鍙栫郴缁熷綋鍓嶆棩鏈� 鏍煎紡2007 */
	public static String getDateyyyy() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy");
		String time = f.format(new Date());
		return time;
	}
	/** 鍙栫郴缁熷綋鍓嶆棩鏈� 鏍煎紡1 */
	public static String getDatemm() {
		SimpleDateFormat f = new SimpleDateFormat(
				"M");
		String time = f.format(new Date());
		return time;
	}
	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 01:30:44 */
	public static String getDateTimetwo() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String time = f.format(new Date());
		return time;
	}
	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007-05-30 01:30:44.345 */
	public static String getDateTimeThree() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss.SSS");
		String time = f.format(new Date());
		return time;
	}
	public static String getDateTime3() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm");
		String time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡0705 */
	public static String getDateTimebh() {
		SimpleDateFormat f = new SimpleDateFormat("yyMM");
		String time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡20070505 */
	public static String getDateAll() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyyMMdd");
		String time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡31(澶�) */
	public static String getDateTimeDay2() {
		SimpleDateFormat f = new SimpleDateFormat("dd");
		String time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007/07/08 14:23:12 */
	public static String getDates() {
		String time = null;
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007/07/08*/
	public static String getDates1() {
		String time = null;
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy/MM/dd");
		time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡2007骞�07鏈�08鏃� */
	public static String getNYR() {
		String time = null;
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy骞碝M鏈坉d鏃�");
		time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡200705 */
	public static String getDateTimezz() {
		SimpleDateFormat f = new SimpleDateFormat("yyyyMM");
		String time = f.format(new Date());
		return time;
	}

	/** 鍙栫郴缁熷綋鍓嶆椂闂� 鏍煎紡070505 */
	public static String getDateTimeDay() {
		SimpleDateFormat f = new SimpleDateFormat("yyMMdd");
		String time = f.format(new Date());
		return time;
	}

	/** 鏇挎崲瀛楃涓� */
	public static String replace(String source, String oldString,
			String newString) {
		StringBuffer output = new StringBuffer();

		int lengthOfSource = source.length(); // 婧愬瓧绗︿覆闀垮害
		int lengthOfOld = oldString.length(); // 鑰佸瓧绗︿覆闀垮害
		int posStart = 0; // 寮�濮嬫悳绱綅缃�
		int pos; // 鎼滅储鍒拌�佸瓧绗︿覆鐨勪綅缃�

		while ((pos = source.indexOf(oldString, posStart)) >= 0) {
			output.append(source, posStart, pos);
			output.append(newString);
			posStart = pos + lengthOfOld;
		}
		if (posStart < lengthOfSource) {
			output.append(source.substring(posStart));
		}
		return output.toString();
	}

	/** 灏嗗瓧绗︿覆鏍煎紡鍖栦负鍥哄畾闀垮害 */
	public static String toLengthStr(String instr, int len) {
		int n = instr.length();
		for (int i = 0; i < (len - n); i++) {
			instr = " " + instr;
		}
		return instr;
	}

	/** 杞崲涓簎tf8缂栫爜 */
	public static String toUtf8String(String s) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c >= 0 && c <= 255) {
				sb.append(c);
			} else {
				byte[] b;
				try {
					b = Character.toString(c).getBytes(StandardCharsets.UTF_8);
				} catch (Exception ex) {
					b = new byte[0];
				}
				for (int j = 0; j < b.length; j++) {
					int k = b[j];
					if (k < 0) {
                        k += 256;
                    }
					sb.append("%" + Integer.toHexString(k).toUpperCase());
				}
			}
		}
		return sb.toString();
	}
    /**姣旇緝涓や釜鏃ユ湡涔嬪樊 鍓嶈��>鍚庤�� 鏃ユ湡鏍煎紡yyyy/MM/dd   杩斿洖澶╂暟*/
	public static long getQuot1(String time1, String time2) { // 姣旇緝涓や釜鏃ユ湡涔嬪樊
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();

			quot = quot / 1000 / 60 / 60 / 24;

		} catch (ParseException e) {
		}
		return quot;
	}
	/**姣旇緝涓や釜鏃ユ湡涔嬪樊 鍓嶈��>鍚庤�� 鏃ユ湡鏍煎紡yyyy/MM/dd HH:mm:ss   杩斿洖灏忔椂鐩稿樊姣鏁�*/
	public static long getQuot2(String time1, String time2) { // 姣旇緝涓や釜鏃堕棿涔嬪樊 绮剧‘鍒扮
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
		} catch (ParseException e) {

		}
		return quot;
	}

	/**姣旇緝涓や釜鏃ユ湡涔嬪樊 鍓嶈��>鍚庤�� 鏃ユ湡鏍煎紡yyyy-MM-dd HH:mm   杩斿洖灏忔椂鐩稿樊澶╂暟*/
	public static long getQuot3(String time1, String time2) { // 姣旇緝涓や釜鏃堕棿涔嬪樊
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot / 1000 / 60 / 60 / 24;
			//System.out.println(quot);
		} catch (ParseException e) {
//			if (logger.isEnabledFor(org.apache.log4j.Level.ERROR)) {
//				logger.error("getQuot3(String, String)", e);
//			}
		}
		return quot;
	}
	/**姣旇緝涓や釜鏃ユ湡涔嬪樊 鍓嶈��>鍚庤�� 鏃ユ湡鏍煎紡yyyy-MM-dd  杩斿洖灏忔椂鐩稿樊澶╂暟*/
	public static long getQuot4(String time1, String time2) { // 姣旇緝涓や釜鏃堕棿涔嬪樊
		// 绮剧‘鍒板ぉ
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
			quot = quot /(24*60*60*1000);
			//System.out.println(quot);
		} catch (ParseException e) {
//			if (logger.isEnabledFor(org.apache.log4j.Level.ERROR)) {
//				logger.error("getQuot4(String, String)", e);
//			}
		}
		return quot;
	}

	/**姣旇緝涓や釜鏃ユ湡涔嬪樊 鍓嶈��>鍚庤�� 鏃ユ湡鏍煎紡yyyy-MM-dd HH:mm:ss   杩斿洖灏忔椂鐩稿樊姣鏁�*/
	public static long getQuot5(String time1, String time2) { // 姣旇緝涓や釜鏃堕棿涔嬪樊 绮剧‘鍒扮
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = ft.parse(time1);
			Date date2 = ft.parse(time2);
			quot = date1.getTime() - date2.getTime();
		} catch (ParseException e) {

		}
		return quot;
	}
	/**
	 * 姣旇緝涓や釜鏃堕棿鏄笉鏄浉绛�,鐩哥瓑杩斿洖0,涓嶇浉绛夎繑鍥為潪0鐨刲ong鍊�
	 */
	public static long getTimeis(String mintime, String maxtime) {
		long quot = 0;
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = ft.parse(maxtime);
			Date date2 = ft.parse(mintime);
			quot = date1.getTime() - date2.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return quot;
	}


	/**
	 *
	 * @param gc1	澶ф椂闂�(琚瘮杈�)
	 * @param gc2	灏忔椂闂�
	 * @return	杩斿洖0琛ㄧず涓や釜鏃堕棿鐩哥瓑,杩斿洖>0鐨勫�艰〃绀哄墠鍙傛暟鏃堕棿澶т簬鍚庡弬鏁版椂闂�,杩斿洖<0鐨勫�艰〃绀哄墠鍙傛暟鏃堕棿灏忎簬鍚庡弬鏁版椂闂�
	 */
	public static int gettimeis2(String gc1,String  gc2)
	{
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
		int i=0;
		try
		{
			Date temp1=ft.parse(gc1);
			Date temp2=ft.parse(gc2);
			Calendar gca=Calendar.getInstance();
			Calendar gcb=Calendar.getInstance();
			gca.setTime(temp1);
			gcb.setTime(temp2);
			i=gca.compareTo(gcb);
		}
		 catch (ParseException e) {
				e.printStackTrace();
			}


		//System.out.println(i);
		return i;

	}

	/**
	 *
	 * @return	杩斿洖瀹為檯骞撮緞   鏍规嵁  birthday yyyy-MM
	 */
	public static int getTrueAgeByData(String birthday)
	{
		int i=0;
		birthday=strTotime(birthday);
		String[] str1=birthday.split("-");
		if(str1.length>0){
			try {
				int year=Integer.parseInt(str1[0]);
				int month=Integer.parseInt(str1[1]);
				int now_year=Integer.parseInt(getDateyyyy());
				int now_month=Integer.parseInt(getDatemm());
				if(now_month>=month) {
                    i=now_year-year;
                } else {
                    i=now_year-year-1;
                }
			} catch (Exception e) {
				//System.out.println("Err");
			}
		}
		else {
            i=-1;
        }
		return i;
	}
	/**
	 * 鑾峰緱缁欏畾鏃ユ湡鏄槦鏈熷嚑
	 * @param day
	 *  鏃ユ湡
	 * @return j 0琛ㄧず閿欒,杩斿洖1-7,1鏄槦鏈熶竴,7鏄槦鏈熷ぉ
	 */
	public static int getweeks(String day) {
		int i = 0; // 杩斿洖0琛ㄧず閿欒
		int j = 0;
		String tmp = day;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date myDate = sdf.parse(tmp);
			Calendar myCalendar = Calendar.getInstance();
			myCalendar.setTime(myDate);
			i = myCalendar.get(Calendar.DAY_OF_WEEK);
			//System.out.println(i);// 鏄熸湡鏃==1锛屾槦鏈熷叚i==7
			if (i == 1) {
				j = 7;
			}
			if (i == 2) {
				j = 1;
			}
			if (i == 3) {
				j = 2;
			}
			if (i == 4) {
				j = 3;
			}
			if (i == 5) {
				j = 4;
			}
			if (i == 6) {
				j = 5;
			}
			if (i == 7) {
				j = 6;
			}
		} catch (Exception ex) {
			//System.out.println("Err");
		}
		return j;
	}

	/**
	 * 缁欏畾鏃ユ湡+涓婁竴澶�
	 *
	 * @param s
	 * @param n
	 * @return
	 * @throws ParseException
	 */
	public static String addDay(String s, int n) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar cd = Calendar.getInstance();
		String temp = null;
		try {

			cd.setTime(sdf.parse(s));
			cd.add(Calendar.DATE, n);
			temp = sdf.format(cd.getTime());
			//System.out.println(temp);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}

	/**
	 * 鏃ユ湡鐩稿噺
	 * @param s
	 * @param n
	 * @return
	 */
	public static String subtractDay(String s,int n)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cd = Calendar.getInstance();
		String temp = null;
		try {
		cd.setTime(sdf.parse(s));
		cd.add(Calendar.DAY_OF_MONTH, n);
		temp = sdf.format(cd.getTime());
		//System.out.print(temp);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return temp;
	}



	/**
	 * 缁欏畾鏃堕棿鍒ゆ柇涓婂崍杩樻槸涓嬪崍 涓婂崍杩斿洖0,涓嬪崍杩斿洖1
	 *
	 * @param time1
	 * @return
	 */
	public static int getselectDays(String time1) {
		GregorianCalendar ca = new GregorianCalendar();
		SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date date1 = ft.parse(time1);
			ca.setTime(date1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(ca.get(GregorianCalendar.AM_PM));
		return ca.get(GregorianCalendar.AM_PM);
	}

	// 璁＄畻鏃ユ湡宸殑鏂规硶(瀹屾暣鐗�,鍖呮嫭闂板勾娑︽湀绛�)
	public static int getDays(GregorianCalendar g1, GregorianCalendar g2) {
		int elapsed = 0;
		GregorianCalendar gc1, gc2;
		if (g2.after(g1)) {
			gc2 = (GregorianCalendar) g2.clone();
			gc1 = (GregorianCalendar) g1.clone();
		} else {
			gc2 = (GregorianCalendar) g1.clone();
			gc1 = (GregorianCalendar) g2.clone();
		}
		gc1.clear(Calendar.MILLISECOND);
		gc1.clear(Calendar.SECOND);
		gc1.clear(Calendar.MINUTE);
		gc1.clear(Calendar.HOUR_OF_DAY);
		gc2.clear(Calendar.MILLISECOND);
		gc2.clear(Calendar.SECOND);
		gc2.clear(Calendar.MINUTE);
		gc2.clear(Calendar.HOUR_OF_DAY);
		while (gc1.before(gc2)) {
			gc1.add(Calendar.DATE, 1);
			elapsed++;
		}
		return elapsed;
	}

	// 鏁板瓧杞崲鎴愪腑鏂� getChinese(2147483648l,0) 鏁板瓧+l,0
	public static String getChinese(long number, int depth) {
		if (depth < 0) {
            depth = 0;
        }
		String chinese = "";
		String src = number + "";
		if (src.charAt(src.length() - 1) == 'l'
				|| src.charAt(src.length() - 1) == 'L') {
			src = src.substring(0, src.length() - 1);
		}

		if (src.length() > 4) {
            chinese = getChinese(Integer.parseInt(src.substring(0,
                    src.length() - 4)), depth + 1)
                    + getChinese(Integer.parseInt(src.substring(
                            src.length() - 4)), depth - 1);
        } else {
			char prv = 0;
			for (int i = 0; i < src.length(); i++) {
				switch (src.charAt(i)) {
				case '0':
					if (prv != '0') {
                        chinese = chinese + "闆�";
                    }
					break;
				case '1':
					chinese = chinese + "涓�";
					break;
				case '2':
					chinese = chinese + "浜�";
					break;
				case '3':
					chinese = chinese + "涓�";
					break;
				case '4':
					chinese = chinese + "鍥�";
					break;
				case '5':
					chinese = chinese + "浜�";
					break;
				case '6':
					chinese = chinese + "鍏�";
					break;
				case '7':
					chinese = chinese + "涓�";
					break;
				case '8':
					chinese = chinese + "鍏�";
					break;
				case '9':
					chinese = chinese + "涔�";
					break;
				}
				prv = src.charAt(i);

				switch (src.length() - 1 - i) {
				case 1:// 鍗�
					if (prv != '0') {
                        chinese = chinese + "鍗�";
                    }
					break;
				case 2:// 鐧�
					if (prv != '0') {
                        chinese = chinese + "鐧�";
                    }
					break;
				case 3:// 鍗�
					if (prv != '0') {
                        chinese = chinese + "鍗�";
                    }
					break;

				}
			}
		}
		while (chinese.length() > 0
				&& chinese.lastIndexOf("闆�") == chinese.length() - 1) {
            chinese = chinese.substring(0, chinese.length() - 1);
        }
		if (depth == 1) {
            chinese += "涓�";
        }
		if (depth == 2) {
            chinese += "浜�";
        }

		return chinese;
	}

	/** 鎶婂皬鍐欐暟瀛楄浆鎹㈡垚澶у啓姹夊瓧 */
	public static String convertNumberToString(long number, int depth) {
		if (depth < 0) {
            depth = 0;
        }
		String chinese = "";
		String src = number + "";
		if (src.charAt(src.length() - 1) == 'l'
				|| src.charAt(src.length() - 1) == 'L') {
			src = src.substring(0, src.length() - 1);
		}

		if (src.length() > 4) {
            chinese = convertNumberToString(Integer.parseInt(src.substring(0,
                    src.length() - 4)), depth + 1)
                    + convertNumberToString(Integer.parseInt(src.substring(src
                            .length() - 4)), depth - 1);
        } else {
			char prv = 0;
			for (int i = 0; i < src.length(); i++) {
				switch (src.charAt(i)) {
				case '0':
					if (prv != '0') {
                        chinese = chinese + "闆�";
                    }
					break;
				case '1':
					chinese = chinese + "澹�";
					break;
				case '2':
					chinese = chinese + "璐�";
					break;
				case '3':
					chinese = chinese + "鍙�";
					break;
				case '4':
					chinese = chinese + "鑲�";
					break;
				case '5':
					chinese = chinese + "浼�";
					break;
				case '6':
					chinese = chinese + "闄�";
					break;
				case '7':
					chinese = chinese + "鏌�";
					break;
				case '8':
					chinese = chinese + "鎹�";
					break;
				case '9':
					chinese = chinese + "鐜�";
					break;
				}
				prv = src.charAt(i);

				switch (src.length() - 1 - i) {
				case 1:// 鍗�
					if (prv != '0') {
                        chinese = chinese + "鎷�";
                    }
					break;
				case 2:// 鐧�
					if (prv != '0') {
                        chinese = chinese + "浣�";
                    }
					break;
				case 3:// 鍗�
					if (prv != '0') {
                        chinese = chinese + "浠�";
                    }
					break;

				}
			}
		}
		while (chinese.length() > 0
				&& chinese.lastIndexOf("闆�") == chinese.length() - 1) {
            chinese = chinese.substring(0, chinese.length() - 1);
        }
		if (depth == 1) {
            chinese += "涓�";
        }
		if (depth == 2) {
            chinese += "浜�";
        }

		return chinese;
	}

	public static Double convertToDouble(Double number) {
		if (number == null) {
            return null;
        }

		BigDecimal temp = new BigDecimal(number.toString()).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return Double.valueOf(temp.toString());
	}

	/**
	 * 鍥涜垗浜斿叆锛屼繚鐣欎袱浣嶅皬鏁�
	 *
	 * @param number
	 * @return
	 */
	public static Double convertToDouble(String number) {
		if (number == null) {
            return null;
        }
		String strTemp = number.trim();
		if (strTemp.length() < 1) {
            return null;
        }

		BigDecimal temp = new BigDecimal(strTemp).setScale(2,
				BigDecimal.ROUND_HALF_UP);
		return Double.valueOf(temp.toString());
	}
	/**
	 * 鍘婚櫎瀛楃涓蹭腑鐨勫洖杞︺�佹崲琛� 鍒惰〃绗�
	 *
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
		    Matcher m = p.matcher(str);
		    String after = m.replaceAll("");
		return after;
	}

	/**
	 * 鍥涜垗浜斿叆锛屼繚鐣欎袱浣嶅皬鏁�
	 *
	 * @param number
	 * @return
	 */
	public static Double convertToDouble(double number) {
		BigDecimal temp = new BigDecimal(number).setScale(3,
				BigDecimal.ROUND_HALF_UP);
		return Double.valueOf(temp.toString());
	}

	/**
	 * 鏍规嵁鍙傛暟淇濈暀鏌愬嚑浣嶅皬鏁帮紝鍥涜垗浜斿叆锛�
	 *
	 * @param number
	 * @param scale
	 *            淇濈暀灏忔暟鐨�
	 * @return
	 */
	public static String convertToDouble(double number, int scale) {
		BigDecimal temp = new BigDecimal(number).setScale(scale,
				BigDecimal.ROUND_HALF_UP);
		return temp.toString();
	}

	/** 鎴彇灏忔暟鐐瑰悗闈㈢殑鏁板瓧 */
	public static String jqXsdTwo(String no) {
		String str = no;
		if (checkXsd(no)) {
			str = no.substring(no.indexOf("."));
		}
		return str;
	}
	/** 鎴彇灏忔暟鐐瑰墠闈㈢殑鏁板瓧 */
	public static String jqXsdOne(String no) {
		String str = no;
		if (checkXsd(no)) {
			str = no.substring(0, no.indexOf("."));
		}
		return str;
	}

	/** 妫�鏌ユ暟瀛椾腑鏄惁鍖呭惈灏忔暟鐐� */
	public static boolean checkXsd(String number) {
		for (int i = 0; i < number.length(); i++) {
			char c = number.charAt(i);
			if (c == '.') {
				return true;
			}
		}
		return false;
	}
	/**妫�鏌ュ瓧绗︿覆鏄惁鏄� 鏁板瓧鍜屽皬鏁扮偣**/
	public static boolean isnumber(String str){
        String number = "0123456789.";
        for (int i = 0; i < str.length(); i++) {
            if (number.indexOf(str.charAt(i)) == -1) {
                return false;
            }
        }
        return true;

	}

    /**妫�鏌ュ瓧绗︿覆鏄惁鏄� 鏁板瓧涓旀病鏈夊皬鏁扮偣**/
    public static boolean isNumberWithOutDot(String str){
        String number = "0123456789";
        for (int i = 0; i < str.length(); i++) {
            if (number.indexOf(str.charAt(i)) == -1) {
                return false;
            }
        }
        return true;

    }

	/**鍘婚櫎瀛楃涓茬殑瀛楃 淇濈暀鏁板瓧鍜屽皬鏁扮偣**/
	public static String getnumber(String str){
		StringBuffer str1=new StringBuffer();
        String number = "0123456789.";
        for (int i = 0; i < str.length(); i++) {
            if (number.indexOf(str.charAt(i)) == -1) {

            }else{
            	str1.append(str, i, i+1);
            }
        }
        return str1.toString();

	}


	public static boolean sql_inj(String s)
	{
	    boolean flag = true;
	    if(s==null || "".equals(s) ){
	    	return flag;
	    }
	    if(s.toLowerCase().indexOf("pagecount") != -1) {
            s = s.replaceAll("pagecount", "pagecoun");
        }
        String[] as = {
                "create", "into", "insert", "select", "delete", "update", "count", "window.", "exec master.dbo.xp_cmdshell", "net localgroup administrators", "truncate", "declare",
                "script", "'", ";", "sp_", "%", "<", ">", "drop", "http", "\"", "html", "\""
        };
	    int i = 0;
	    do
	    {
	        if(i >= as.length) {
                break;
            }
	        if(s.toLowerCase().indexOf(as[i]) != -1)
	        {
	            flag = false;
	            break;
	        }
	        i++;
	    } while(true);
	    return flag;
	}

	public static int getCount(String s1,String s2){
        int count=0;
        int temp;
        if(s1.length()>=s2.length()){
            for(int i=0;i<=s1.length();i++){
                temp=s1.indexOf(s2);
                if(temp>=0){
                    ++count;
                    s1=s1.substring(temp+1);
                }
            }
        }
        return count;
    }

	public static String HTMLEncode(String aTagFragment) {
	    StringBuffer result = new StringBuffer();
	    StringCharacterIterator iterator = new StringCharacterIterator(aTagFragment);
	    char character = iterator.current();
	    while (character != 65535) {
	      if (character == '<') {
              result.append("<");
          } else if (character == '>') {
              result.append(">");
          } else if (character == '"') {
              result.append("鈥�");
          } else if (character == '\'') {
              result.append("鈥�");
          } else if (character == '\\') {
              result.append("锕�");
          } else if (character == '&') {
              result.append("锕�");
          } else {
              result.append(character);
          }
	      character = iterator.next();
	    }
	    return result.toString();
	  }

	 /**
	  * 妫�鏌ヨ〃鍗曪紝濡傛灉琛ㄥ崟鍊间腑鏈夐潪娉曠洿鎺ョ疆鎹⑩�溾��
	  * @param obj 娓呴櫎闈炴硶鍊间负鈥溾��.
	  * @return
	  */
	 public static <T extends Object> T check(T obj){

		 Field[] fields = obj.getClass().getDeclaredFields();
		 for (Field field : fields) {
			 if(field.getType()== String.class){
					field.setAccessible(true);
					try {
						if(field.get(obj) == null) {
                            continue;
                        }
						String newString=sql_inj_replaceall(field.get(obj).toString());
						field.set(obj,newString);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
			 }
		 }

    	return obj;
	 }

	 /**
	  * 妫�鏌ヨ〃鍗曪紝濡傛灉琛ㄥ崟鍊间腑鏈夐潪娉曠洿鎺alse;
	  * @param obj 杩斿洖null閫氳繃锛屾湁闈炴硶杩斿洖闈炴硶鐨勫睘鎬у悕绉�.
	  * @return
	  */
	 public static String checkForm(Object obj){
		 Field[] fields = obj.getClass().getDeclaredFields();
		 for (Field field : fields) {
			 if(field.getType()== String.class){
					field.setAccessible(true);
					try {
						if(field.get(obj) == null) {
                            continue;
                        }
						if(!sql_inj(field.get(obj).toString())){
							return field.getName();
						}
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
			 }
		 }

    	return null;
	 }
	 /**
	  * 妫�鏌ヨ〃鍗曪紝濡傛灉琛ㄥ崟鍊间腑鏈夐潪娉曠洿鎺ョ疆鎹⑩�溾��
	  * @param map 娓呴櫎闈炴硶鍊间负鈥溾��.
	  * @return
	  */
	 public static Map<String,Object> checkMapForReplace(Map<String,Object> map){
		 Map<String,Object> newMap=new HashMap<String, Object>();
		 if(map!=null){
			 for (String key : map.keySet()) {
				   String value=DealString.objectoString(map.get(key));
				   value=sql_inj_replaceall(value);
				   newMap.put(key, value);
				  }
		 }else {
             newMap=null;
         }
	     return newMap;
	 }
	 /**
	  * 妫�鏌ヨ〃鍗曪紝濡傛灉琛ㄥ崟鍊间腑鏈夐潪娉曞�奸儴鍒嗙洿鎺ョ疆鎹负鈥溾��
	  * @param map 鏇挎崲闈炴硶鍊奸儴鍒嗕负鈥溾��
	  * @return
	  */
	 public static Map<String,Object> checkMapClear(Map<String,Object> map){
		 Map<String,Object> newMap=new HashMap<String, Object>();
		 if(map!=null){
			 for (String key : map.keySet()) {
				   String value=DealString.objectoString(map.get(key));
				   if(!sql_inj(value)){
					   value="";
					}
				   newMap.put(key, value);
				  }
		 }else {
             newMap=null;
         }
	     return newMap;
	 }

	 public static String objectoString(Object o)
		{
			 String s="";
		  if(o!=null&&!"".equals(o.toString())) {
              s=o.toString().trim();
          }
		  return s;
		}
	 /**
		* 鑾峰彇瀹㈡埛绔疘P
		* @param request
		* @return
		*/
		public static String getIp(HttpServletRequest request) {
		        String ip = request.getHeader("X-Forwarded-For");
		        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		            ip = request.getHeader("Proxy-Client-IP");
		        }
		        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		            ip = request.getHeader("WL-Proxy-Client-IP");
		        }
		        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		            ip = request.getHeader("HTTP_CLIENT_IP");
		        }
		        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		        }
		        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
		            ip = request.getRemoteAddr();
		        }
		        return ip;
		    }

	public static final String POJECT_PATH =  DealString.class.getClassLoader().getResource("").getPath().substring(1).replaceAll("WEB-INF/classes/", "").replace("/", "\\");

	/** 鍙栫郴缁熷綋鍓嶆棩鏃ユ湡鐨勪笅涓�澶╃殑鍑屾櫒1鐐� */
	public static Date getNextDay() {
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy-MM-dd");

		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(f.parse(getDateTimeone()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.DATE, 1);
		cal.add(Calendar.HOUR, 1);
		return cal.getTime();
	}
	/** 鎸囧畾鏃ユ湡,鎸囧畾闂撮殧鐨勬棩鏈�  yyyy-MM-dd
	 *  闂撮殧 澶�
	 * */
	public static String getNextDaytoStr(String timeStr,String format,int num) {
		SimpleDateFormat f = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance(); 
		try {
			cal.setTime(f.parse(timeStr));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cal.add(Calendar.DATE,num);		
		return f.format(cal.getTime());
	}
	
    public static boolean isMobileNO(String mobiles){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 楠岃瘉Email
     * @param email email鍦板潃锛屾牸寮忥細zhangsan@sina.com锛寊hangsan@xxx.com.cn锛寈xx浠ｈ〃閭欢鏈嶅姟鍟�
     * @return 楠岃瘉鎴愬姛杩斿洖true锛岄獙璇佸け璐ヨ繑鍥瀎alse
     */
    public static boolean checkEmail(String email) {
        String regex = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z]{2,}){1}$)";
        return Pattern.matches(regex, email);
    }

    

    

  
    
    /**
     * 灏咰lob瀛楁杞负String銆�
     * @param clob
     * @return resultstr
     */
    public static String Clob2String(Clob clob) {
        int i = 0;
        String resultstr = "";
        try {
            if(clob!=null&&!"".equals(clob.toString())){
                InputStream input = clob.getAsciiStream();
                int len = (int)clob.length();
                byte[] by = new byte[len];
                while(-1 != (i = input.read(by, 0, by.length))){
                    input.read(by, 0, i);
                }
                resultstr = new String(by, StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
        }
        return resultstr;
    }

    public static String getImgSrc(String source_str){
        int start = source_str.indexOf("src=\"")+5;
        int end = source_str.indexOf("\"",start+5);
        return source_str.substring(start,end);
    }
    
    public  static String sql_inj_replaceall(String str){
    	 Pattern pattern  =null;
    	 Matcher matcher =null;
    	 boolean flag=false;
		if(str==null || "".equals(str) ){
	    	return "";
	    }
        String[] as = {
                "create", "into", "insert", "select", "delete", "update", "count", "window.", "exec master.dbo.xp_cmdshell", "net localgroup administrators", "truncate", "declare",
                "script", "'", "sp_", "%", "<", ">", "drop", "\""
        };
		
		for (int i = 0; i < as.length; i++) {
			  pattern = Pattern.compile(as[i],Pattern.CASE_INSENSITIVE);
		      matcher = pattern.matcher(str);
		      flag= matcher.find();
              if(flag) {
                  str = matcher.replaceAll("");
              }
		}		
		return str;
	}
    
    /**
	 * 缂栫爜
	 * 
	 * @param s
	 * @return
	 */
	public static String escape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int len = s.length();
		for (int i = 0; i < len; i++) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') { // 'A'..'Z' : as it was
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') { // 'a'..'z' : as it was
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') { // '0'..'9' : as it was
				sbuf.append((char) ch);
			} else if (ch == '-'
					|| ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*'
					|| ch == '\'' || ch == '(' || ch == ')') {
				sbuf.append((char) ch);
			} else if (ch <= 0x007F) { // other ASCII : map to %XX
				sbuf.append('%');
				sbuf.append(hex[ch]);
			} else { // unicode : map to %uXXXX
				sbuf.append('%');
				sbuf.append('u');
				sbuf.append(hex[(ch >>> 8)]);
				sbuf.append(hex[(0x00FF & ch)]);
			}
		}
		return sbuf.toString();
	}

	/**
	 * 瑙ｇ爜 璇存槑锛氭湰鏂规硶淇濊瘉 涓嶈鍙傛暟s鏄惁缁忚繃escape()缂栫爜锛屽潎鑳藉緱鍒版纭殑鈥滆В鐮佲�濈粨鏋�	 * 
	 * @param s
	 * @return
	 */
	public static String unescape(String s) {
		StringBuffer sbuf = new StringBuffer();
		int i = 0;
		int len = s.length();
		while (i < len) {
			int ch = s.charAt(i);
			if ('A' <= ch && ch <= 'Z') { // 'A'..'Z' : as it was
				sbuf.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') { // 'a'..'z' : as it was
				sbuf.append((char) ch);
			} else if ('0' <= ch && ch <= '9') { // '0'..'9' : as it was
				sbuf.append((char) ch);
			} else if (ch == '-'
					|| ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*'
					|| ch == '\'' || ch == '(' || ch == ')') {
				sbuf.append((char) ch);
			} else if (ch == '%') {
				int cint = 0;
				if ('u' != s.charAt(i + 1)) { // %XX : map to ascii(XX)
					cint = (cint << 4) | val[s.charAt(i + 1)];
					cint = (cint << 4) | val[s.charAt(i + 2)];
					i += 2;
				} else { // %uXXXX : map to unicode(XXXX)
					cint = (cint << 4) | val[s.charAt(i + 2)];
					cint = (cint << 4) | val[s.charAt(i + 3)];
					cint = (cint << 4) | val[s.charAt(i + 4)];
					cint = (cint << 4) | val[s.charAt(i + 5)];
					i += 5;
				}
				sbuf.append((char) cint);
			} else { // 瀵瑰簲鐨勫瓧绗︽湭缁忚繃缂栫爜
				sbuf.append((char) ch);
			}
			i++;
		}
		return sbuf.toString();
	}
    
    public static HashMap<String,Object> getGrid(List<?> list,int count){
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("rows", list);
		map.put("records", count);
		return map;
	}
    /**
     * 璁剧疆鏈�澶у��,鑾峰彇闅忔満鏁版暟缁�
     * **/
    public static int[] getRandom(int max){
    	int[] seed = new int[max];
    	for(int i=0;i<max;i++){
    		seed[i]=i;
    	}
    	int[] ranArr = new int[max];
    	Random ran = new Random();
    	  // 鏁伴噺浣犲彲浠ヨ嚜宸卞畾涔夈��
    	  for (int i = 0; i < seed.length; i++) {
    	  // 寰楀埌涓�涓綅缃�
    	  int j = ran.nextInt(seed.length - i);
    	  // 寰楀埌閭ｄ釜浣嶇疆鐨勬暟鍊�
    	  ranArr[i] = seed[j];
    	  // 灏嗘渶鍚庝竴涓湭鐢ㄧ殑鏁板瓧鏀惧埌杩欓噷
    	  seed[j] = seed[seed.length - 1 - i];
         }    	  
    	  return ranArr;
    }

    /**
     * 鏍￠獙鍙傛暟鏄惁鏈夌┖
     * @author gcs
 	 * @param params 鍙傛暟
     * @return void
     * @create: 2019/6/25 14:24
     */

	public static boolean checkParamsNotNull(Object... params) {
		if (params != null && params.length > 0) {
			for (Object element : params) {
				if (element == null) {
					return false;
				}
			}
		}
		return true;
	}



	public static void main(String[] args) {
//    	String string=DealString.strTotime0("2016-12-07","yyyy-MM-dd","yyyyMMdd");
//    	System.out.println("-------------"+string);
//	    for(int i=0;i<13;i++){
//		System.out.println("json:"+DealString.getNextDaytoStr(string,"yyyyMMdd",i));
//       }
//	    TOPIC.getNameByValue(10000);
//	    System.out.println(getCurrentTime10());
//	    System.out.println(TOPIC.getNameByValue(0,14001));
	}
}
