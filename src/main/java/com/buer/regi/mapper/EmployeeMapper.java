package com.buer.regi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.buer.regi.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    //此类注解加继承后，mybatis-plus自动写了增删改查的方法
}
