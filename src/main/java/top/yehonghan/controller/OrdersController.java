package top.yehonghan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yehonghan.common.BaseContext;
import top.yehonghan.common.R;
import top.yehonghan.dto.DishDto;
import top.yehonghan.dto.OrdersDto;
import top.yehonghan.entity.Category;
import top.yehonghan.entity.Dish;
import top.yehonghan.entity.OrderDetail;
import top.yehonghan.entity.Orders;
import top.yehonghan.service.OrderDetailService;
import top.yehonghan.service.OrdersService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yehonghan
 * @2022/5/4 23:22
 */
@Slf4j
@RestController
@RequestMapping("/order")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;
    @Autowired
    private OrderDetailService orderDetailService;


    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据：{}", orders);
        ordersService.submit(orders);
        return R.success("支付成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String number){
        log.info("分页查询信息pade:{},pageSize:{}", page,pageSize);
        //分页查询
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        //添加排序条件
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        queryWrapper.eq(number!=null,Orders::getNumber,number);
        //开始分页查询
        ordersService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Orders orders){
        log.info("修改状态为：{}", orders.getStatus());
        ordersService.updateById(orders);
        return R.success("修改状态成功");
    }

    /**
     * 更具用户id获取对于订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userOrderPage(int page,int pageSize){
        //获取用户id
        Long currentId = BaseContext.getCurrentId();
        //构造分页构造器
        Page<Orders> pageInfo=new Page<>(page,pageSize);
        Page<OrdersDto> dtoPageInfo=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper=new LambdaQueryWrapper<>();
        //根据用户id查出用户对于订单信息
        queryWrapper.eq(Orders::getUserId, currentId);
        //添加排序条件
        queryWrapper.orderByDesc(Orders::getOrderTime);
        //执行查询
        ordersService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPageInfo,"records");

        //取出订单数据放入DishDto对象中
        List<Orders> records = pageInfo.getRecords();

        //取出菜品数据处理，加入菜品分类后，改变对象为DishDto，放入list集合中
        List<OrdersDto> list=records.stream().map((item)->{
            OrdersDto ordersDto=new OrdersDto();
            //拷贝外其他属性
            BeanUtils.copyProperties(item, ordersDto);
            //获取订单id
            Long orderId = item.getId();
            //按照订单id获取订单详情
            LambdaQueryWrapper<OrderDetail> queryWrapperDto=new LambdaQueryWrapper<>();
            queryWrapperDto.eq(OrderDetail::getOrderId,orderId);
            //添加排序条件
            queryWrapperDto.orderByAsc(OrderDetail::getAmount);
            List<OrderDetail> orderDetailList = orderDetailService.list(queryWrapperDto);
            ordersDto.setOrderDetails(orderDetailList);
            return ordersDto;
        }).collect(Collectors.toList());

        //将处理过的数据放入dtoPageInfo中
        dtoPageInfo.setRecords(list);

        return R.success(dtoPageInfo);
    }
}
