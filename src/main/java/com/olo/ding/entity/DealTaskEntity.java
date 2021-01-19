package com.olo.ding.entity;

import java.util.List;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@ApiModel
public class DealTaskEntity {
	private String name;
	private String zxr;
	private String jdr;
	private String xzr;
	private String rwksrq;
	private String rwjzrq;
	private List<String> rwfjdz;
	private String rwfjdzStr;
	private String mxRwnr;
	private String id;
	private List<String> rwnr;
	private String zxrXm;
	private String xzrXm;
	
	
	private String requestName;
	private String lcmc;
	private String nodeName;
	private String receiveTime;
	private String operateTime;
	private String dealTime;
	private String standardTime;
	private String cslc;
	
	//Integer requestId,Integer userId,Integer submitType,Integer taskResource,List<> zxrfjdz,list<>jdrfjdz
	private String requestId;
	private Integer userId;
	private Integer submitType;
	private Integer taskResource;
	private List<String> zxrfjdz;
	private List<String> jdrfjdz;
	private String zxrfjdzStr;
	private String jdrfjdzStr;
	private Integer nodeId;
	private String jdczr;
	
	private String zxrbz;
	private String jdrbz;
	
	private String thjl;
	private String csyy;
	private String jdrId;
	
	
	private String filerealPath;
	private String filePath;
	private String fileName;
	private String docid;
	
	private String zxrfj;
	private String jdrfj;
	private String rwfj;
	private String rwnrStr;
	private String newJzrq;
	
	private String lastSubmitTime;
	 
}
