package top.yehonghan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.yehonghan.dto.SetmealDto;
import top.yehonghan.entity.Setmeal;

/**
 * @Author yehonghan
 * @2022/5/2 22:27
 */
public interface SetmealService extends IService<Setmeal> {
    //新增套餐同时保存套餐与菜品关联关系
    void saveWithDish(SetmealDto setmealDto);
    //删除套餐和对应菜品
    void deleteWithDish(Long[] ids);
    //修改套餐状态
    void updateSetmealStatusByids(int status,Long[] ids);
    //根据id查询套餐信息及相关菜品
    SetmealDto getSetmealDtoById(Long setmealId);
    //修改套餐信息
    void updateWithDish(SetmealDto setmealDto);
}
