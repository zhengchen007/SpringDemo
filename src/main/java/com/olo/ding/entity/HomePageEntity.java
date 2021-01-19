package com.olo.ding.entity;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel("首页")
public class HomePageEntity {
	/*
	 * 通用开始
	 */
	//任务名称
	@ApiModelProperty(value="任务名称")
	private String taskName;
	//是否逾期
	@ApiModelProperty(value="是否逾期")
	private Integer isOverTime;
	//列表数据id
	@ApiModelProperty(value="列表数据id")
	private Integer ListId;
	@ApiModelProperty(value="requestId")
	private String requestId;
	//任务要求完成时间
	@ApiModelProperty(value="任务要求完成时间")
	private String taskDemandTime;
	//任务提交连接url
	@ApiModelProperty(value="任务提交连接url")
	private String jumpUrl;
	private String xzr;
	/*
	 * 通用结束
	 */
	
	
	 
	
	/*
	 * 任务相关开始
	 */
	private String taskId;
	//任务来源
	private Integer taskResource;
	//任务来源名称
	private String taskResourceName;
	//任务接收人
	private String taskReceiver;
	//任务派发人
	private String taskDistributor;
	//截至时间
	private String cutoffTime;
	//任务接收时间
	private String taskReceiveTime;
	private String taskCreatedate;
	private String cfzq;
	private Integer allCount;
	private Integer triggeredCount;
	private List<String> rwnr;
	/*
	 * 任务相关结束
	 */
	
	/*
	 * 管理派工单开始
	 */
	//超时流程
	 private String cslc;
	 //上级领导接收时间
	 private String sjldjssj;
	 //上级领导审核的标准时间
	 private String jdbzsj;
	/*
	 * 管理派工单结束
	 */
	
	 //流程处理时间
	private String operatedate;
	private String operatetime;
	private Integer isFinished;
	private String zxrfjdzStr;
	private List<String> zxrfjdz;
	private String jdrfjdzStr;
	private List<String> jdrfjdz;
	private String zxrbz;
	private String jdrbz;
	private String rwksrq;
	private String rwjmId;
	private String operateDateAndTime;
	private String receivetime;
	private String isPf;
}
