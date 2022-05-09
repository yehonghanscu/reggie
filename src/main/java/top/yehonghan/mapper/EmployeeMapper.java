package top.yehonghan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.yehonghan.entity.Employee;

/**
 * @Author yehonghan
 * @2022/5/1 20:47
 */
@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
