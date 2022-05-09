package top.yehonghan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yehonghan.common.BaseContext;
import top.yehonghan.common.R;
import top.yehonghan.entity.ShoppingCart;
import top.yehonghan.service.ShoppingCartService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Author yehonghan
 * @2022/5/4 22:40
 */
@Slf4j
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据:{}",shoppingCart );
        //设置用户id
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);
        //查询菜品或者套餐是否在购物车中
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if(dishId!=null){
            //提交的是菜品
             queryWrapper.eq(ShoppingCart::getDishId,dishId);

        }else {
            //提交的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        //查询菜品或者套餐是否在购物车中
        ShoppingCart cartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(cartServiceOne!=null){
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number+1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne=shoppingCart;
            cartServiceOne.setNumber(1);
        }
        return R.success(cartServiceOne);
    }

    /**
     * 查看购物车
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        //获取用户id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        return R.success(shoppingCartList);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        //获取用户id
        Long currentId = BaseContext.getCurrentId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        shoppingCartService.remove(queryWrapper);
        return R.success("清空成功");
    }

    @PostMapping("/sub")
    public R<String> sub(@RequestBody ShoppingCart shoppingCart){
        log.info("提交的菜品为：{}", shoppingCart);
        Long dishId=shoppingCart.getDishId();
        Long setmealId=shoppingCart.getSetmealId();
        //获取当前登录用户id
        Long currentId = BaseContext.getCurrentId();
        //查询需要减少菜品或套餐是否在购物车中
        LambdaQueryWrapper<ShoppingCart> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(dishId!=null,ShoppingCart::getDishId,dishId)
                .eq(setmealId!=null,ShoppingCart::getSetmealId,setmealId)
                .eq(ShoppingCart::getUserId,currentId);
        ShoppingCart shoppingCartServiceOne = shoppingCartService.getOne(queryWrapper);
        if(shoppingCartServiceOne!=null){
            //获取当前菜品数量
            Integer currNumber = shoppingCartServiceOne.getNumber();
            if(currNumber>0){
                //修改菜品数量后更新
                shoppingCartServiceOne.setNumber(currNumber-1);
                shoppingCartService.updateById(shoppingCartServiceOne);
            }
        }
        return R.success("修改成功");
    }
}
