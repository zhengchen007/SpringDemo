package com.olo.ding.entity;

import java.util.Map;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@ToString
@Data
@ApiModel
public class RankEntity extends HomePageEntity{
	@ApiModelProperty(value="姓名")
	private String peopleName;
	@ApiModelProperty(value="排名")
	private String rank;
	@ApiModelProperty(value="我的得分")
	private Integer taskScore;
	@ApiModelProperty(value="部门id")
	private String departmentId;
	@ApiModelProperty(value="部门")
	private String departmentName;
	@ApiModelProperty(value="")
	private String lastName;
	@ApiModelProperty(value="个人应得分")
	private Integer personalDealTaskCount;
	private String rwjzrq;
	private String operatedate;
	private String operatetime;
	private String taskReceiver;
}
