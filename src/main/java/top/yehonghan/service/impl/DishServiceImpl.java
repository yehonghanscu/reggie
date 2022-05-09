package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.yehonghan.dto.DishDto;
import top.yehonghan.entity.Dish;
import top.yehonghan.entity.DishFlavor;
import top.yehonghan.mapper.DishMapper;
import top.yehonghan.service.DishFlavorService;
import top.yehonghan.service.DishService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yehonghan
 * @2022/5/2 22:28
 */
@Slf4j
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新增菜品同时保存口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品基本信息到菜品表
        this.save(dishDto);
        //保存菜品口味数据到口味表
        Long dishId = dishDto.getId();//菜品id
        //加工flavors将dishId赋值
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id查询菜品信息与口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto=new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);
        //清理当前菜品口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors=flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据ids删除菜品
     * @param ids
     */
    @Override
    public void deleteWithFlavor(Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            //先根据菜品id删除对应口味
            LambdaQueryWrapper<DishFlavor> queryWrapper=new LambdaQueryWrapper<>();
            //添加删除条件
            queryWrapper.eq(DishFlavor::getDishId, ids[i]);
            dishFlavorService.remove(queryWrapper);
            //再根据id删除对应菜品
            this.removeById(ids[i]);
        }
    }

    /**
     * 根据id修改状态
     * @param status
     * @param ids
     */
    @Override
    public void updateDishStatusByids(int status, Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            //将状态与id封装为对象
            Dish dish=new Dish();
            dish.setStatus(status);
            dish.setId(ids[i]);
            this.updateById(dish);
        }
    }

}
