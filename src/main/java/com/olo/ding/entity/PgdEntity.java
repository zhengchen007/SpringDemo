package com.olo.ding.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
@ApiModel
public class PgdEntity {
	@ApiModelProperty(value="接收日期")
	private String receiveDate;
	@ApiModelProperty(value="接收时间")
	private String operateTime;
	@ApiModelProperty(value="处理日期")
	private String operateDate;
	@ApiModelProperty(value="requestId")
	private String requestId;
	@ApiModelProperty(value="节点id")
	private String nodeId;
	@ApiModelProperty(value="节点名称")
	private String nodeName;
	@ApiModelProperty(value="流程id")
	private String workflowId;
	@ApiModelProperty(value="流程名称")
	private String workflowName;
	@ApiModelProperty(value="")
	private String cfpgdsc;
	@ApiModelProperty(value="requestName")
	private String requestName;
	@ApiModelProperty(value="接收日期")
	private String signorder;
	@ApiModelProperty(value="处理人id")
	private String operateId;
	@ApiModelProperty(value="接收时间")
	private String receiveTime;
	@ApiModelProperty(value="操作人")
	private String czr;
	@ApiModelProperty(value="")
	private String myDate;
	@ApiModelProperty(value="")
	private String sfwjjr;
	private String jqts;
	@ApiModelProperty(value="requestNewName")
	private String requestNameNew;
	@ApiModelProperty(value="超时流程id")
	private String cslc;
	@ApiModelProperty(value="截止日期")
	private String jzrq;
	@ApiModelProperty(value="提交时间")
	private String tjsj;
	@ApiModelProperty(value="是否完成")
	private String iscomplete;
	@ApiModelProperty(value="处理时间")
	private String dealTime;
	@ApiModelProperty(value="假期时长")
	private Double holidayCount;
	@ApiModelProperty(value="是否得分")
	private Boolean isGetScore;
	@ApiModelProperty(value="")
	private Integer pf;
	@ApiModelProperty(value="部门id")
	private String departmentId;
	@ApiModelProperty(value="个人派工单得分")
	private Integer personalPgdCount;
	
	
}
