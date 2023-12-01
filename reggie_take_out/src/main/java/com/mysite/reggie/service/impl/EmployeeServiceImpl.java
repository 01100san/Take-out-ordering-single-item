package com.mysite.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysite.reggie.entity.Employee;
import com.mysite.reggie.mapper.EmployeeMapper;
import com.mysite.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: EmployeeServiceImpl
 * Package: com.mysite.reggie.service.impl
 * Description
 *
 * @Author zhl
 * @Create 2023/11/21 20:46
 * version 1.0
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService{
}
