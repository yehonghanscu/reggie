package top.yehonghan.dto;

import lombok.Data;
import top.yehonghan.entity.Setmeal;
import top.yehonghan.entity.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
