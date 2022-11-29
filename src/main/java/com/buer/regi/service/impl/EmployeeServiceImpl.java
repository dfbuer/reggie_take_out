package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.entity.Employee;
import com.buer.regi.mapper.EmployeeMapper;
import com.buer.regi.service.EmployeeService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
