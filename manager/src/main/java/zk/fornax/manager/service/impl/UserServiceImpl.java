package zk.fornax.manager.service.impl;

import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.dto.LoginDto;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.service.UserService;

public class UserServiceImpl implements UserService {

    @Override
    public Mono<User> doLogin(LoginDto loginDto) {
        return null;
    }

}
