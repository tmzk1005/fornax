package zk.fornax.manager.controller;

import reactor.core.publisher.Mono;

import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.annotation.RequestBody;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.http.framework.exception.BizException;
import zk.fornax.manager.bean.dto.LoginDto;
import zk.fornax.manager.bean.dto.UserDto;
import zk.fornax.manager.bean.vo.UserVo;
import zk.fornax.manager.service.ServiceFactory;
import zk.fornax.manager.service.UserService;

@Controller
public class UserController {

    private final UserService userService;

    public UserController() {
        userService = ServiceFactory.get(UserService.class);
    }

    @Route(path = "_login", method = HttpMethod.POST)
    public Mono<UserVo> login(@RequestBody LoginDto loginDto) {
        return userService.doLogin(loginDto).map(UserVo::fromPo)
            .switchIfEmpty(Mono.error(new BizException("登录失败!用户名或密码错误.")));
    }

    @Route(method = HttpMethod.POST)
    public Mono<UserVo> create(@RequestBody UserDto userDto) {
        return userService.create(userDto).map(UserVo::fromPo);
    }

}
