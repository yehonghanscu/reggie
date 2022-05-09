package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.yehonghan.common.CustomException;
import top.yehonghan.dto.SetmealDto;
import top.yehonghan.entity.Setmeal;
import top.yehonghan.entity.SetmealDish;
import top.yehonghan.mapper.SetmealMapper;
import top.yehonghan.service.SetmealDishService;
import top.yehonghan.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yehonghan
 * @2022/5/2 22:29
 */
@Slf4j
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    //新增套餐同时保存套餐与菜品关联关系
    @Transactional
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息
        this.save(setmealDto);
        //保存套餐与菜品关联信息
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //使用stream过滤器赋值套餐id
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐和对应菜品
     * @param ids
     */
    @Transactional
    @Override
    public void deleteWithDish(Long[] ids) {
        //判断套餐状态
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        //如果不能删除直接抛出异常信息
        int count = this.count(queryWrapper);
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
        for (int i = 0; i < ids.length; i++) {
            LambdaQueryWrapper<SetmealDish> wrapper=new LambdaQueryWrapper<>();
            wrapper.eq(SetmealDish::getSetmealId,ids[i]);
            //删除菜品关系表数据
            setmealDishService.remove(wrapper);
            //删除套餐数据
            this.removeById(ids[i]);
        }
    }

    /**
     * 根据id修改套餐状态
     * @param status
     * @param ids
     */
    @Override
    public void updateSetmealStatusByids(int status, Long[] ids) {
        for (int i = 0; i < ids.length; i++) {
            //将状态与id封装为对象
            Setmeal setmeal=new Setmeal();
            setmeal.setStatus(status);
            setmeal.setId(ids[i]);
            this.updateById(setmeal);
        }
    }

    @Override
    public SetmealDto getSetmealDtoById(Long setmealId) {
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealId);
        //获取菜品信息
        List<SetmealDish> setmealDishList=setmealDishService.list(queryWrapper);
        //获取套餐信息
        Setmeal setmeal=this.getById(setmealId);
        SetmealDto setmealDto=new SetmealDto();
        //封装SetmealDto
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(setmealDishList);

        return setmealDto;
    }

    /**
     * 更新套餐与菜品信息
     * @param setmealDto
     */
    @Override
    @Transactional
    public void updateWithDish(SetmealDto setmealDto) {
        //更新套餐表
        this.updateById(setmealDto);
        //清理当前菜品数据
        LambdaQueryWrapper<SetmealDish> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(queryWrapper);
        //获取当前提交菜品数据，并加入套餐id
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes=setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        //保存套餐菜品信息
        setmealDishService.saveBatch(setmealDishes);
    }
}
