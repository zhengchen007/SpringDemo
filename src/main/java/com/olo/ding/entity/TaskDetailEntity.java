package com.olo.ding.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel
public class TaskDetailEntity {
	private String mainId;
	@ApiModelProperty(value="任务内容")
	private String rwnr;
	private String lastname;
	private String requestId;
	private String id;
	private String uuid;
}
