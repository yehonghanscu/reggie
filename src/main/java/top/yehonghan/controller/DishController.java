package top.yehonghan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import top.yehonghan.common.R;
import top.yehonghan.dto.DishDto;
import top.yehonghan.entity.Category;
import top.yehonghan.entity.Dish;
import top.yehonghan.entity.DishFlavor;
import top.yehonghan.service.CategoryService;
import top.yehonghan.service.DishFlavorService;
import top.yehonghan.service.DishService;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author yehonghan
 * @2022/5/3 13:23
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        dishService.saveWithFlavor(dishDto);

        //清理对应分类下菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        return R.success("新增菜品成功！");
    }

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page ,int pageSize,String name){
        //构造分页构造器
        Page<Dish> pageInfo=new Page<>(page,pageSize);
        Page<DishDto> dtoPageInfo=new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(name!=null, Dish::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行查询
        dishService.page(pageInfo, queryWrapper);
        //对象拷贝
        BeanUtils.copyProperties(pageInfo, dtoPageInfo,"records");

        //取出菜品数据放入DishDto对象中
        List<Dish> records = pageInfo.getRecords();

        //取出菜品数据处理，加入菜品分类后，改变对象为DishDto，放入list集合中
        List<DishDto> list=records.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //拷贝除categoryName外其他属性
            BeanUtils.copyProperties(item, dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //获取分类名称
            Category category= categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        //将处理过的数据放入dtoPageInfo中
        dtoPageInfo.setRecords(list);

        return R.success(dtoPageInfo);
    }


    /**
     * 根据id查询菜品信息与口味信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }


    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        dishService.updateWithFlavor(dishDto);

        //清理对应分类下菜品缓存数据
        String key="dish_"+dishDto.getCategoryId()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);

        return R.success("修改菜品成功！");
    }

    /**
     * 按id删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteById(@RequestParam Long[] ids){
        log.info("删除菜品id有:"+ids.length);
        dishService.deleteWithFlavor(ids);

        //清理所有菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return R.success("删除成功");
    }

    /**
     * 按id修改状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateDishStatus(@PathVariable int status,Long[] ids){
        log.info("修改状态为:{}",status);
        log.info("修改菜品数:{}",ids.length);
        dishService.updateDishStatusByids(status,ids);

        //清理所有菜品缓存数据
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);

        return R.success(status==0?"停售成功":"启售成功");
    }

    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
//        //添加查询条件
//        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//        List<Dish> dishList = dishService.list(queryWrapper);
//        return R.success(dishList);
//    }

    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){
        List<DishDto> dishDtoList;
        //动态构造key
        String key="dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        //从redis中获取缓存数据
        dishDtoList =(List<DishDto>)redisTemplate.opsForValue().get(key);
        if(dishDtoList!=null){
            //如果存在，直接返回
            return R.success(dishDtoList);
        }

        LambdaQueryWrapper<Dish> queryWrapper=new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
        List<Dish> dishList = dishService.list(queryWrapper);

        //取出菜品数据处理，加入菜品口味后，改变对象为DishDto，放入list集合中
        dishDtoList=dishList.stream().map((item)->{
            DishDto dishDto=new DishDto();
            //拷贝除categoryName外其他属性
            BeanUtils.copyProperties(item, dishDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //获取分类名称
            Category category= categoryService.getById(categoryId);
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            //获取菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> Wrapper=new LambdaQueryWrapper();
            Wrapper.eq(DishFlavor::getDishId,dishId);
            //获取菜品口味数据
            List<DishFlavor> dishFlavorList = dishFlavorService.list(Wrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;
        }).collect(Collectors.toList());

        //不存在查询数据库，并前将查询到数据缓存到redis
        redisTemplate.opsForValue().set(key,dishDtoList,60, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }

}
