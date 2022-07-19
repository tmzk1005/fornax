package zk.fornax.manager.controller;

import reactor.core.publisher.Mono;

import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.annotation.RequestBody;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.manager.bean.dto.LoginDto;
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
        return userService.doLogin(loginDto).map(user -> new UserVo().setFromPo(user));
    }

}
