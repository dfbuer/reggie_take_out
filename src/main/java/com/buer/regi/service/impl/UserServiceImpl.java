package com.buer.regi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.buer.regi.entity.User;
import com.buer.regi.mapper.UserMapper;
import com.buer.regi.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
