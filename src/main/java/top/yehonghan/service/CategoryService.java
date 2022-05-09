package top.yehonghan.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.yehonghan.entity.Category;

/**
 * @Author yehonghan
 * @2022/5/2 21:45
 */
public interface CategoryService extends IService<Category> {
    void remove(Long id);
}
