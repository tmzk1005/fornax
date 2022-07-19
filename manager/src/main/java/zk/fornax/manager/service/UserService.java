package zk.fornax.manager.service;

import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.dto.LoginDto;
import zk.fornax.manager.bean.po.User;

public interface UserService {

    Mono<User> doLogin(LoginDto loginDto);

}
