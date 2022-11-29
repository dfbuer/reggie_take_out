package com.buer.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.buer.regi.common.R;
import com.buer.regi.entity.Employee;
import com.buer.regi.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登陆功能
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        //加入HttpServletRequest是如果登录成功就把登陆员工的id上传到session于中，方便获取使用
        /**
         * 1.对接受的密码进行md5加密；
         * 2.根据username数据库进行比较
         * 3.如果没有查询到就返回登陆失败的结果
         * 4.密码比对，不一致就返回登陆失败结果
         * 5.查看员工状态，如果已经禁用就返回员工禁用的结果
         * 6.登录成功，将员工id存入session
         */
        //1.对接受的密码进行md5加密；
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据username数据库进行比较
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        //由于数据库对username进行了唯一处理，所以直接用getOne（）方法查询就行
        Employee emp = employeeService.getOne(queryWrapper);

        //3.如果没有查询到就返回登陆失败的结果
        if (emp == null){
            return R.error("登陆失败");
        }

        //4.密码比对，不一致就返回登陆失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("登录失败");
        }

        //5.查看员工状态，如果已经禁用就返回员工禁用的结果
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }

        //6.登录成功，将员工id存入session
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /**
     * 员工退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清理session域中的保存的当前员工
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param request
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee){
        log.info("新增员工：{}",employee.toString());

        //设置初始密码123456，且进行MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //采用公共字段自动填充
        //employee.setCreateTime(LocalDateTime.now());
        //employee.setUpdateTime(LocalDateTime.now());

        //采用公共字段自动填充
        //从session中获取当前操作新增员工这个员工的id
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setCreateUser(empId);
        //employee.setUpdateUser(empId);

        //调用方法，将这个员工对象保存数据库
        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    /**
     * 分页查询步骤
     * 1.页面发送ajax请求，将分页查询参数（page,pageSize,name）提交到服务端
     * 2.服务端Controller接受页面提交的数据并调用Service查询数据
     * 3.Service调用Mapper操作数据库，查询分页数据
     * 4.Controller将查询到的分页数据相应给页面
     * 5.页面接收到分页数据并通过ElementUI的Table组件展示到页面上
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name){
        log.info("page = {}, pageSize = {}, name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据员工id更新员工信息
     * @param request
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request, @RequestBody Employee employee){

        //采用公共字段自动填充
        //Long empId = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empId);

        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有查询到对应员工信息");
    }
}
























