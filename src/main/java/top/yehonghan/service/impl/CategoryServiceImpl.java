package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.yehonghan.common.CustomException;
import top.yehonghan.entity.Category;
import top.yehonghan.entity.Dish;
import top.yehonghan.entity.Setmeal;
import top.yehonghan.mapper.CategoryMapper;
import top.yehonghan.service.CategoryService;
import top.yehonghan.service.DishService;
import top.yehonghan.service.SetmealService;

/**
 * @Author yehonghan
 * @2022/5/2 21:46
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id删除套餐，删除前先判断
     * @param id
     */
    @Override
    public void remove(Long id) {
        //查询当前分类是否关联菜品
        LambdaQueryWrapper<Dish> DishqueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        DishqueryWrapper.eq(Dish::getCategoryId, id);
        int count = dishService.count(DishqueryWrapper);
        if(count>0){
            //关联菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品不能删除");
        }

        //查询当前分类是否关联套餐
        LambdaQueryWrapper<Setmeal> SetmealqueryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        SetmealqueryWrapper.eq(Setmeal::getCategoryId, id);
        int count1 = setmealService.count(SetmealqueryWrapper);
        if(count1>0){
            //关联套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐不能删除");
        }

        //正常删除
        super.removeById(id);
    }
}
