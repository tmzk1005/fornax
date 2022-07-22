package zk.fornax.manager.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import zk.fornax.manager.bean.Role;
import zk.fornax.manager.bean.dto.LoginDto;
import zk.fornax.manager.bean.dto.UserDto;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.security.HasRole;

public interface UserService {

    Mono<User> doLogin(LoginDto loginDto);

    @HasRole(Role.SYSTEM_ADMIN)
    Mono<User> create(UserDto userDto);

    Flux<User> listUsers(int pageNum, int pageSize);

}
