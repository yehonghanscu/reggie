package top.yehonghan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.yehonghan.entity.Orders;

/**
 * @Author yehonghan
 * @2022/5/4 23:19
 */
@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
