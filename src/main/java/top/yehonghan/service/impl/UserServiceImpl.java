package top.yehonghan.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.yehonghan.entity.User;
import top.yehonghan.mapper.UserMapper;
import top.yehonghan.service.UserService;

/**
 * @Author yehonghan
 * @2022/5/4 19:59
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
