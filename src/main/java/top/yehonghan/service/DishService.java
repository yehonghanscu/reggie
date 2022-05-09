package top.yehonghan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.yehonghan.dto.DishDto;
import top.yehonghan.entity.Dish;

/**
 * @Author yehonghan
 * @2022/5/2 22:27
 */
public interface DishService extends IService<Dish> {
    //新增菜品同时插入口味数据，操作两张表
    void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息与口味信息
    DishDto getByIdWithFlavor(Long id);
    //修改菜品同时修改口味数据，操作两张表
    void updateWithFlavor(DishDto dishDto);
    //根据ids删除菜品
    void deleteWithFlavor(Long[] ids);
    //根据id修改状态
    void updateDishStatusByids(int status , Long[] ids);
}
