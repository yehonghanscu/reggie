package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.yehonghan.entity.OrderDetail;
import top.yehonghan.mapper.OrderDetailMapper;
import top.yehonghan.service.OrderDetailService;

/**
 * @Author yehonghan
 * @2022/5/4 23:25
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
