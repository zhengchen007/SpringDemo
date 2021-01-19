package com.olo.ding.entity;

import java.util.List;

import com.sun.xml.internal.xsom.impl.scd.Iterators.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@ApiModel(value="任务详情model")
public class TaskDistributeEntity{
		@ApiModelProperty(value="当前节点")
		private String currentNode;
		@ApiModelProperty(value="requestId")
		private String requestId; 
		@ApiModelProperty(value="任务id")
		private Integer taskId;
		@ApiModelProperty(value="是否为周期")
		private String sfwzq;
		@ApiModelProperty(value="任务开始时间")
		private String rwkssj;
		@ApiModelProperty(value="任务开始日期")
		private String rwksrq;
		@ApiModelProperty(value="重复周期")
		private String cfzq;
		@ApiModelProperty(value="任务结束时间")
		private String rwjssj;
		@ApiModelProperty(value="任务结束日期")
		private String rwjsrq;
		@ApiModelProperty(value="任务名称")
		private String rwmc;
		@ApiModelProperty(value="任务来源")
		private String rwly;
		@ApiModelProperty(value="创建人")
		private String cjr;
		@ApiModelProperty(value="执行人")
		private String zxr;
		@ApiModelProperty(value="监督人")
		private String jdr;
		@ApiModelProperty(value="协作人")
		private String xzr;
		@ApiModelProperty(value="任务内容")
		private List<String> rwnr;
		@ApiModelProperty(value="任务附件")
		private String rwfj;
		@ApiModelProperty(value="userId")
		private Integer userId;
		@ApiModelProperty(value="任务内容字符串")
		private String rwnrStr;
		@ApiModelProperty(value="周期任务起止时间")
		private String taskCreatedate;
		@ApiModelProperty(value="任务内容")
		private String sfgd;
		private String taskResource;
		private String cfsj;
		private String rwfjdz;
		private Integer triggeredCount;
		private String rwjmid;
		private Integer allCount;
		private String cfzqName;
		private String tqzz;
		private List<String> rwfjList;
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
		private String taskDemandTime;
		//任务提交连接url
		@ApiModelProperty(value="任务提交连接url")
		private String jumpUrl;
		/*
		 * 通用结束
		 */
		
		
		 
		
		/*
		 * 任务相关开始
		 */
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
		private String itemTime;
		private String zxrfjdz;
		private String jdrfjdz; 
		private List<String> zxrfjList;
		private List<String> jdrfjList;
		private String zxryj;
		private String jdryj;
		private String zxrStr;
		
		
		/*
		 *周期要务专属字段 
		 */
		//月份和季度的类型备注：
		//0：1~28；1：倒数的
		private String positiveType;
		//作业时长
		//[0,1]:0是天数，1是时长,2是分钟
		private String zyyqsc;
		//作业开始时间
		//{"month":"","week":"","day":"","hour":""，mins:""}
		private String zykssj;
		//期数
		private Integer periods;
		//周期开始时间
		private String cycleStartDate;
		
		private String beginTimeStr;
		private String zyyqscStr;
		private String cron;
		//是否为季度
		private String sfwjd;
		//是否继续执行周期要务	
		private String sfzx;
		private String zyyqscJson;
		private String zykssjJson;
		private String jobName;
		private String zzJobName;
		private Integer intervalNum;
		private String dyckssj;
		private String zqywcjsj;
		private String jduuid;
		private String isPf;
}
