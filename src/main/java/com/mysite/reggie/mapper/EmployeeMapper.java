package com.mysite.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mysite.reggie.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: EmployeeMapper
 * Package: com.mysite.reggie.mapper
 * Description
 *
 * @Author zhl
 * @Create 2023/11/21 20:47
 * version 1.0
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
