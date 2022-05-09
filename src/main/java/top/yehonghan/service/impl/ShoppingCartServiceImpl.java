package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.yehonghan.entity.ShoppingCart;
import top.yehonghan.mapper.ShoppingCartMapper;
import top.yehonghan.service.ShoppingCartService;

/**
 * @Author yehonghan
 * @2022/5/4 22:38
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
