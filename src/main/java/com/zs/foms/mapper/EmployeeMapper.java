package com.zs.foms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zs.foms.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee>{
}
