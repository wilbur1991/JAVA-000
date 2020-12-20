package com.wilbur.rpcfx.demo.provider;

import com.wilbur.rpcfx.demo.api.UserService;
import com.wilbur.rpcfx.demo.api.User;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Override
    public User findById(int id) {
        return new User(id, "KK" + System.currentTimeMillis());
    }
}
