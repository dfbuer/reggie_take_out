package com.buer.regi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buer.regi.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
