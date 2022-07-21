package zk.fornax.manager.service.impl;

import java.util.Objects;

import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import reactor.core.publisher.Mono;

import zk.fornax.http.core.session.ReactiveRequestContextHolder;
import zk.fornax.http.core.session.RequestContext;
import zk.fornax.http.core.session.WebSession;
import zk.fornax.http.framework.security.Pbkdf2PasswordEncoder;
import zk.fornax.manager.bean.dto.LoginDto;
import zk.fornax.manager.bean.po.User;
import zk.fornax.manager.repository.RepositoryFactory;
import zk.fornax.manager.repository.UserRepository;
import zk.fornax.manager.service.UserService;

public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl() {
        userRepository = RepositoryFactory.get(UserRepository.class);
    }

    @Override
    public Mono<User> doLogin(LoginDto loginDto) {
        return userRepository.findOneByUsername(loginDto.getUsername())
            .flatMap(user -> passwordMatch(loginDto.getPassword(), user.getPassword()) ? setLoginStatus(user) : Mono.empty());
    }

    private static boolean passwordMatch(String dtoPassword, String hashedPassword) {
        if (Objects.isNull(dtoPassword) || Objects.isNull(hashedPassword)) {
            return false;
        }
        return Pbkdf2PasswordEncoder.getDefaultInstance().matches(dtoPassword, hashedPassword);
    }

    private static Mono<User> setLoginStatus(User user) {
        return ReactiveRequestContextHolder.getContext()
            .map(RequestContext::getWebExchange)
            .flatMap(webExchange -> webExchange.getSession().flatMap(session -> {
                Cookie cookie = new DefaultCookie(WebSession.COOKIE_NAME_SESSION, session.getId());
                cookie.setHttpOnly(true);
                cookie.setPath("/");
                cookie.setMaxAge(session.getMaxIdleTime().toSeconds());
                webExchange.getResponse().addCookie(cookie);
                session.getAttributes().put(WebSession.ATTR_NAME_PRINCIPAL, user);
                return session.save().thenReturn(user);
            }));
    }

}
