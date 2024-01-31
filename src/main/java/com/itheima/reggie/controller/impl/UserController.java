package com.itheima.reggie.controller.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session) {
        // 获取手机号
        String phone = user.getPhone();

        if (StringUtils.isNotEmpty(phone)) {
            //生成4位随机验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            // 用阿里云提供的 API 发送短信
            log.info("验证码为:{}", code);
            // 生成的验证码缓存到Redis中，设置有效期为5分钟
            stringRedisTemplate.opsForValue().set(phone, code, 5, TimeUnit.MINUTES);
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info("{}", map);

        String phone = map.get("phone").toString();
        String code = map.get("code").toString();

        // 从 Redis 中获取保存的验证码
        String cacheCode = (String) stringRedisTemplate.opsForValue().get(phone);

        if (cacheCode != null && cacheCode.equals(code)) {
            // 检查是否是新用户
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user", user.getId());

            // 如果登陆成功，删除缓存的验证码
            stringRedisTemplate.delete(phone);
            return R.success(user);
        }
        return R.error("登陆失败");
    }
}
