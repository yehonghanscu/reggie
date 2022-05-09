package top.yehonghan.dto;

import lombok.Data;
import top.yehonghan.entity.Dish;
import top.yehonghan.entity.DishFlavor;

import java.util.ArrayList;
import java.util.List;

@Data
public class DishDto extends Dish {

    //菜品对应口味数据
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
