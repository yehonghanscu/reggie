package top.yehonghan.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.yehonghan.entity.User;

/**
 * @Author yehonghan
 * @2022/5/4 19:57
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
