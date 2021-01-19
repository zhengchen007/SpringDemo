package com.olo.ding.utils;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Node implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5121478741148825954L;

    public static final String URL = "url";

    public static final String ICON_CLS = "iconCls";

    private String id;

    private String isUser;
//    private String text;

    private String title;


	private String pid;//鍓嶇瑕佹眰 pid鍏ㄥ皬鍐�

//    private String iconCls;
//
//    private Map<String, Object> tag = new HashMap<String, Object>();
//
//    private String state;
//
//    private String pName;
//
//    private boolean checked;
//
//    private int flag;
//    private String flagId;
//    private String memo;
//
//    private String dtuId;
//
//    private String phoneticize;
//    
//    private Integer hasAddBllTree;
//
//    private Map<String, Object> alarm_Status = new HashMap<String, Object>();
//
//    private Map<String, Object> attributes = new HashMap<String, Object>();

    private List<Node> children = new ArrayList<Node>();
//    private String flagName;
    
    private String zj;
    
    public String getZj() {
		return zj;
	}

	public void setZj(String zj) {
		this.zj = zj;
	}

	public String getIsUser() {
  		return isUser;
  	}

  	public void setIsUser(String isUser) {
  		this.isUser = isUser;
  	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public List<Node> getChildren() {
		return children;
	}

	public void setChildren(List<Node> children) {
		this.children = children;
	}

	public static String getUrl() {
		return URL;
	}

	public static String getIconCls() {
		return ICON_CLS;
	}

//    private String alarmLevel;
//
//    private int areaMainType;
//    private Integer type;
//
//    private String pinyinHead;
//    private String pinyin;

//    public Integer getType() {
//        return type;
//    }
//
//    public void setType(Integer type) {
//        this.type = type;
//    }
//
//    public Node() {
//
//    }
//
//    public Node(String id, String text, String pId, String iconCls) {
//        this.id = id;
//        this.text = text;
//        this.pid = pId;
//        if (!"".equals(DealString.toString(iconCls))) {
//            this.attributes.put(ICON_CLS, iconCls);
//        }
//    }
//
//    public Node(String id, String text, String pId, String url, String iconCls) {
//        this.id = id;
//        this.text = text;
//        this.pid = pId;
//        if (!"".equals(DealString.toString(url))) {
//            this.attributes.put(URL, url);
//        }
//        if (!"".equals(DealString.toString(iconCls))) {
//            this.attributes.put(ICON_CLS, iconCls);
//        }
//    }
//
//    public static long getSerialVersionUID() {
//        return serialVersionUID;
//    }
//
//    public static String getURL() {
//        return URL;
//    }
//
//    public static String getIconCls() {
//        return ICON_CLS;
//    }
//
//    public void setIconCls(String iconCls) {
//        this.iconCls = iconCls;
//    }
//
//    public Map<String, Object> getTag() {
//        return tag;
//    }
//
//    public void setTag(Map<String, Object> tag) {
//        this.tag = tag;
//    }
//
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
//    public String getpName() {
//        return pName;
//    }
//
//    public void setpName(String pName) {
//        this.pName = pName;
//    }
//
//    public boolean isChecked() {
//        return checked;
//    }
//
//    public void setChecked(boolean checked) {
//        this.checked = checked;
//    }
//
//    public int getFlag() {
//        return flag;
//    }
//
//    public void setFlag(int flag) {
//        this.flag = flag;
//    }
//
//    public String getMemo() {
//        return memo;
//    }
//
//    public void setMemo(String memo) {
//        this.memo = memo;
//    }
//
//    public String getPhoneticize() {
//        return phoneticize;
//    }
//
//    public void setPhoneticize(String phoneticize) {
//        this.phoneticize = phoneticize;
//    }
//
//    public Map<String, Object> getAlarm_Status() {
//        return alarm_Status;
//    }
//
//    public void setAlarm_Status(Map<String, Object> alarm_Status) {
//        this.alarm_Status = alarm_Status;
//    }
//
//    public Map<String, Object> getAttributes() {
//        return attributes;
//    }
//
//    public void setAttributes(Map<String, Object> attributes) {
//        this.attributes = attributes;
//    }
//
//    public List<Node> getChildren() {
//        return children;
//    }
//
//    public void setChildren(List<Node> children) {
//        this.children = children;
//    }
//
//    public String getFlagName() {
//        return flagName;
//    }
//
//    public void setFlagName(String flagName) {
//        this.flagName = flagName;
//    }
//
//    public String getAlarmLevel() {
//        return alarmLevel;
//    }
//
//    public void setAlarmLevel(String alarmLevel) {
//        this.alarmLevel = alarmLevel;
//    }
//
//    public int getAreaMainType() {
//        return areaMainType;
//    }
//
//    public void setAreaMainType(int areaMainType) {
//        this.areaMainType = areaMainType;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public String getTitle() {
//        return title;
//    }
//
//    public void setTitle(String title) {
//        this.title = title;
//    }
//
//    public String getPid() {
//        return pid;
//    }
//
//    public void setPid(String pid) {
//        this.pid = pid;
//    }
//
//    public String getFlagId() {
//        return flagId;
//    }
//
//    public void setFlagId(String flagId) {
//        this.flagId = flagId;
//    }
//
//    public String getDtuId() {
//        return dtuId;
//    }
//
//    public void setDtuId(String dtuId) {
//        this.dtuId = dtuId;
//    }
//
//	public Integer getHasAddBllTree() {
//		return hasAddBllTree;
//	}
//
//	public void setHasAddBllTree(Integer hasAddBllTree) {
//		this.hasAddBllTree = hasAddBllTree;
//	}
//
//    public String getPinyinHead() {
//        return pinyinHead;
//    }
//
//    public void setPinyinHead(String pinyinHead) {
//        this.pinyinHead = pinyinHead;
//    }
//
//    public String getPinyin() {
//        return pinyin;
//    }
//
//    public void setPinyin(String pinyin) {
//        this.pinyin = pinyin;
//    }
    
    
}
