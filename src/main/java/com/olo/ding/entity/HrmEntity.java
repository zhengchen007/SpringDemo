package com.olo.ding.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@ApiModel
public class HrmEntity {
	@ApiModelProperty(value="id")
	private String id;
	@ApiModelProperty(value="姓名")
	private String name;
	@ApiModelProperty(value="分部")
	private String fb;
	@ApiModelProperty(value="部门")
	private String bm;
	@ApiModelProperty(value="职务")
	private String zw;
	@ApiModelProperty(value="电话")
	private String mobile;
	private String zjid;
	private String IsUser;
}	
