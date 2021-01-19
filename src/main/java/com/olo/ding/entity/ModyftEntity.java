package com.olo.ding.entity;

import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel
public class ModyftEntity {
		private String rwmc;
	 	private String zxr;
	 	private String jdr;
	 	private String xzr;
	 	private String cfzq;
	 	private String rwksrq;
	 	private String rwkssj;
	 	private String rwjsrq;
	 	private String rwjssj;
	 	private String rwnr;
	 	private String rwfj;
	 	private List<String> rwnrList;
	 	private List<String> rwfjList;
	 	private List<Map<String, Object>> zxrList;
	 	private List<Map<String, Object>> jdrList;
	 	private List<Map<String, Object>> xzrList;
	 	private String positiveType;
	 	private String zyyqsc;
	 	private String cycleStartDate;
	 	private String zykssj;
	 	private String periods;
	 	private String zyyqscJson;
	 	private String zykssjJson;
	 	private Integer intervalNum;
	 	private String jduuid;
}
