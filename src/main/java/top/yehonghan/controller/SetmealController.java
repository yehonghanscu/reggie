package top.yehonghan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.web.bind.annotation.*;
import top.yehonghan.common.R;
import top.yehonghan.dto.SetmealDto;
import top.yehonghan.entity.Category;
import top.yehonghan.entity.Setmeal;
import top.yehonghan.service.CategoryService;
import top.yehonghan.service.SetmealDishService;
import top.yehonghan.service.SetmealService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 * @Author yehonghan
 * @2022/5/3 21:02
 */
@Slf4j
@RestController
@RequestMapping("setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    //新增套餐后清理所有套餐缓存数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息:{}",setmealDto);
        setmealService.saveWithDish(setmealDto);
       return R.success("新增套餐成功");
    }

    /**
     * 根据条件进行分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page( int page,int pageSize,String name){
        log.info("分页查询:{}",page);
        //设置分页构造器
        Page<Setmeal> pageInfo=new Page<>(page,pageSize);
        Page<SetmealDto> setmealDtoPage=new Page<>();
        //设置过滤条件
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null, Setmeal::getName,name);
        //设置排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,setmealDtoPage,"records");

        List<Setmeal> records = pageInfo.getRecords();
        //获取pageInfo中数据并进行加工
        List<SetmealDto> list=records.stream().map((item)->{
            SetmealDto setmealDto=new SetmealDto();
            //获取套餐分类id再获取名称
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            //再将Setmeal对象拷贝到SetmealDto中
            BeanUtils.copyProperties(item, setmealDto);
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(list);
        return R.success(setmealDtoPage);
    }

    /**
     * 删除套装
     * @param ids
     * @return
     */
    @DeleteMapping
    //删除套餐后清理所有套餐缓存数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> deleteByIds(@RequestParam Long[] ids){
        log.info("删除的id数:{}", ids.length);
        setmealService.deleteWithDish(ids);
        return R.success("删除成功");
    }

    @PutMapping
    //修改套餐后清理所有套餐缓存数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> updateWithDish(@RequestBody SetmealDto setmealDto){
        setmealService.updateWithDish(setmealDto);
        return  R.success("修改套餐成功");
    }

    /**
     * 修改套餐状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    //禁用套餐后清理所有套餐缓存数据
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> updateSetmealStatus(@PathVariable int status,Long[] ids){
        log.info("修改状态为:{}",status);
        log.info("修改菜品数:{}",ids.length);
        setmealService.updateSetmealStatusByids(status,ids);
        return R.success(status==0?"停售成功":"启售成功");
    }

    /**
     * 根据条件查询套餐数据
     * @param categoryId
     * @param status
     * @return
     */
    @GetMapping("/list")
    //缓存对应套餐数据
    @Cacheable(value = "setmealCache" ,key = "#categoryId+'_'+#status")
    public R<List<Setmeal>> list(String categoryId,int status){
        LambdaQueryWrapper<Setmeal> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        queryWrapper.eq(status==1, Setmeal::getStatus,status);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = setmealService.list(queryWrapper);
        return R.success(setmealList);
    }

    /**
     * 根据id获取套餐信息和关联菜品信息
     * @param setmealId
     * @return
     */
    @GetMapping("/{setmealId}")
    public R<SetmealDto> getSetmealDtoById(@PathVariable Long setmealId){
        log.info("传入套餐id为：{}", setmealId);
        SetmealDto setmealDto=setmealService.getSetmealDtoById(setmealId);
        return R.success(setmealDto);
    }
}
