package com.buer.regi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.buer.regi.common.R;
import com.buer.regi.entity.User;
import com.buer.regi.service.UserService;
import com.buer.regi.utils.SMSUtils;
import com.buer.regi.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){

        //获取手机号
        String phone = user.getPhone();
        //判断手机号是否为空
        if (StringUtils.isNotEmpty(phone)){
            //调用工具类，随机生成x位的验证码,转成String类型存储
            String code = ValidateCodeUtils.generateValidateCode(6).toString();

            //调用阿里云提供的短信服务API完成发送短信
            //因为目前没有那个短信验证功能，所以下面先注释，采用控制台输出的方式来查看
            //SMSUtils.sendMessage("瑞吉外卖","这里填写那个码",phone,code);
            log.info("code = {}",code);

            //将生成的验证码保存到session中，以手机号为key,验证码为value
            session.setAttribute(phone,code);

            //将生成的验证码存入redis中，并设置有效时间为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机短信验证码发送成功");
        }

        return R.error("手机短信验证码发送失败");
    }

    /**
     * 移动端用户登陆
     * @param map
     * @param session
     * @return
     */
    //@PostMapping("/login")
    //public R<User> login(@RequestBody Map map, HttpSession session){
    //
    //    //获取手机号
    //    String phone = map.get("phone").toString();
    //
    //    //获取验证码
    //    String code = map.get("code").toString();
    //
    //    //从Session中获取保存的验证码
    //    Object condeInSession = session.getAttribute(code);
    //
    //    //进行验证码比对（页面提交的和Session中保存的）
    //    if (condeInSession != null && condeInSession.equals(code)){
    //
    //        //如果能够比对成功，说明登陆成功
    //        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
    //        queryWrapper.eq(User::getPhone,phone);
    //        User user = userService.getOne(queryWrapper);
    //        if (user == null){
    //            //新用户，自动保存到数据库完成注册
    //            user = new User();
    //            user.setPhone(phone);
    //            user.setStatus(1);
    //            userService.save(user);
    //        }
    //        session.setAttribute("user",user.getId());
    //        return R.success(user);
    //    }
    //    return R.error("登陆失败");
    //}

    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        //String code = map.get("code").toString();

        //从Session中获取保存的验证码
        //Object condeInSession = session.getAttribute(code);

        //从redis中获取保存的验证码
        //Object condeInSession = redisTemplate.opsForValue().get(phone);


        //进行验证码比对（页面提交的和Session中保存的）
        //if (condeInSession != null && condeInSession.equals(code)){
        //
        //    //如果能够比对成功，说明登陆成功
        //    LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //    queryWrapper.eq(User::getPhone,phone);
        //    User user = userService.getOne(queryWrapper);
        //    if (user == null){
        //        //新用户，自动保存到数据库完成注册
        //        user = new User();
        //        user.setPhone(phone);
        //        user.setStatus(1);
        //        userService.save(user);
        //    }
        //    session.setAttribute("user",user.getId());


        //    return R.success(user);
        //}

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone,phone);
        User user = userService.getOne(queryWrapper);
        if (user == null){
            //新用户，自动保存到数据库完成注册
            user = new User();
            user.setPhone(phone);
            user.setStatus(1);
            userService.save(user);
        }
        session.setAttribute("user",user.getId());

        //如果用户登陆成功，删除redis中缓存的验证码
        redisTemplate.delete(phone);

        return R.success(user);
    }
}
