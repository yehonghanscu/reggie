package top.yehonghan.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.yehonghan.common.R;
import top.yehonghan.entity.User;
import top.yehonghan.service.UserService;
import top.yehonghan.utils.ValidateCodeUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @Author yehonghan
 * @2022/5/4 20:01
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    //邮箱验证码发送需要bean
    @Resource
    private JavaMailSender javaMailSender;
    //读取yml文件中username的值并赋值给form
    @Value("${spring.mail.username}")
    private String from;

    //注入redis执行对象
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     *发送邮箱验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取邮箱账户
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成6位验证码
            String code = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("code:{}", code);
            //通过邮箱发送验证码-------------------------------------------------------------
            // 构建一个邮件对象
            SimpleMailMessage message = new SimpleMailMessage();
            // 设置邮件发送者
            message.setFrom(from);
            // 设置邮件接收者
            message.setTo(phone);
            // 设置邮件的主题
            message.setSubject("登录验证码");
            // 设置邮件的正文
            String text = "您的验证码为：" + code + ",请勿泄露给他人。";
            message.setText(text);
            // 发送邮件
            try {
                javaMailSender.send(message);
            } catch (MailException e) {
                e.printStackTrace();
            }

            //需要保存生成验证码到session中
//            session.setAttribute(phone, code);

            //将生成的验证码缓存到redis中，并且设置有效期为5分钟
            stringRedisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("邮箱验证码发送成功");
        }
        return R.error("邮箱验证码发送失败");
    }

    /**
     * 移动端客户登录
     * @param userMap
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map userMap, HttpSession session){
        log.info(userMap.toString());
        //获取邮箱
        String phone = userMap.get("phone").toString();
        //获取验证码
        String code = userMap.get("code").toString();
        //从session中获取保存的验证码
//        Object codeInSession = session.getAttribute(phone);

        //从redis中获取获取保存的验证码
        String codeInRedis = stringRedisTemplate.opsForValue().get(phone);

        //进行验证码对比
        if(codeInRedis!=null&&codeInRedis.equals(code)){
            //判断当前用户是否为新用户，是则自动注册
            LambdaQueryWrapper<User> queryWrapper=new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);
            if(user==null){
                user=new User();
                user.setPhone(phone);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());
            //如果用户登录成功删除验证码
            stringRedisTemplate.delete(phone);

            return R.success(user);
        }
        return R.error("登录失败，验证码错误");
    }

    /**
     * 移动端退出功能
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
        //清理保存的session
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
