package com.olo.ding.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import com.olo.ding.entity.UserInfo;

@Component
@Mapper
public interface UserInfoMapper {

    UserInfo selectByPrimaryKey(Integer id);
    
    UserInfo test(Integer id);
}