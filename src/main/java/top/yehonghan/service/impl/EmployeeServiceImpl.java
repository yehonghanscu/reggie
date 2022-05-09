package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.yehonghan.entity.Employee;
import top.yehonghan.mapper.EmployeeMapper;
import top.yehonghan.service.EmployeeService;

/**
 * @Author yehonghan
 * @2022/5/1 20:49
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
}
