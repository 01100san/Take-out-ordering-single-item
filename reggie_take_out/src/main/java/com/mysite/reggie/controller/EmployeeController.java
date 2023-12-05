package com.mysite.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysite.reggie.common.R;
import com.mysite.reggie.entity.Employee;
import com.mysite.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sun.security.provider.MD5;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * ClassName: EmployeeController
 * Package: com.mysite.reggie.controller
 * Description
 *
 * @Author zhl
 * @Create 2023/11/21 20:34
 * version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpServletRequest request){
        //1. 先对页面提交的密码进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2. 根据页面的 username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3. 如果没有查询到则返回登录失败的结果
        if (emp == null){
            return R.error("登录失败，请检查用户名或密码");
        }
        //4. 密码比对，不一致返回
        if (! emp.getPassword().equals(password)){
            return R.error("登录失败，请检查用户名或密码");
        }
        //5. 比较员工状态
        if (emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //6. 登录成功,将员工id 存入 Session并返回登录成功的结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        /*在最开始 我无法获取request.getSession().getRemoveAttribute("employee")中对应的在login传入的id值,
        * 这是因为 我没有先登录，再退出，没有保存session中id的值。
        * */
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    /**
     * 新增员工
     * @param employee
     * @return
     */
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息：{}",employee.toString());
        /*
            Employee(id=null, username=sss, name=444, password=null, phone=13333333333, sex=1,
            idNumber=222222222222222222, status=null, createTime=null, updateTime=null, createUser=null, updateUser=null)
         */
        //status数据库中默认值是 1
        //设置初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //设置创建时间
        /*employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());*/

        //通过request.getSession().getAttribute("employee")获得登录用户的id
        /*Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setCreateUser(empId);
        employee.setUpdateUser(empId);*/

        employeeService.save(employee);

        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page<Employee>> page(@RequestParam(name = "page") Integer currentPage,
                                  @RequestParam Integer pageSize,
                                  //required默认是true,默认全部接收来自前端的参数值，如果required=false，说明按照 name员工姓名查找
                                  @RequestParam(required = false) String name){
        log.info("currentPage：{}",currentPage);             //当前页
        log.info("pageSize：{}",pageSize);                   //当前的页码
        log.info("name：{}",name);                           //按员工姓名查询
        Page<Employee> employeePage = new Page<>(currentPage,pageSize);
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //判断name是否为空
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //按照更新时间降序
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //按照条件分页查询
        Page<Employee> page = employeeService.page(employeePage,queryWrapper);
        return R.success(page);
    }

    /**
     * 不能根据id 回显员工数据
     * 在前端响应的数据中，有精度缺失的情况
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> employee(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        log.info("根据id 回显查询得到的数据：{}",employee);
        return R.success(employee);
    }

    /**
     * 修改员工的信息 包括员工的状态，和信息
     * @param employee 获取前端JSON格式的员工信息
     * @return
     */
    @PutMapping
    public R<String> editEmployee(HttpServletRequest request,@RequestBody Employee employee){
        log.info("需要修改的员工信息为：{}",employee);
        /*Long updateId = (Long) request.getSession().getAttribute("employee");*/
        //更新修改时间
        /*employee.setUpdateTime(LocalDateTime.now());*/
        //更新修改人
        /*employee.setUpdateUser(updateId);*/
        return employeeService.updateById(employee) == true ? R.success("修改成功") : R.error("操作失败");
        /*return R.success(employee);*/
    }
}
