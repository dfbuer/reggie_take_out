package com.buer.regi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.buer.regi.entity.Employee;


public interface EmployeeService extends IService<Employee> {
    //此类注解加继承后，mybatis-plus自动写了从获取mapper中方法获得的数据库数据
}
