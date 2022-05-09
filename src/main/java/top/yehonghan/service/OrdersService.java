package top.yehonghan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.yehonghan.entity.Orders;

/**
 * @Author yehonghan
 * @2022/5/4 23:20
 */
public interface OrdersService extends IService<Orders> {
    //用户下单
    void submit(Orders orders);
}
