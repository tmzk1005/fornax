package zk.fornax.manager.core;

import java.util.List;

import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerRequest;

import zk.fornax.http.core.AntPathMatcher;
import zk.fornax.http.core.ResponseHelper;
import zk.fornax.http.core.exchange.WebExchange;
import zk.fornax.http.core.filter.HttpApiFilter;
import zk.fornax.http.core.filter.HttpFilterChain;
import zk.fornax.http.core.session.AnonymousPrincipal;

public class AuthenticationHttpApiFilter implements HttpApiFilter {

    private final List<String> noNeedLoginPaths;

    public AuthenticationHttpApiFilter(List<String> noNeedLoginPath) {
        this.noNeedLoginPaths = noNeedLoginPath;
    }

    @Override
    public Mono<Void> filter(WebExchange webExchange, HttpFilterChain chain) {
        return webExchange.getPrincipal().flatMap(principal -> {
            if (principal instanceof AnonymousPrincipal) {
                return handleAnonymousRequest(webExchange, chain);
            }
            return chain.filter(webExchange);
        });
    }

    public Mono<Void> handleAnonymousRequest(WebExchange webExchange, HttpFilterChain chain) {
        if (noNeedLogin(webExchange.getRequest())) {
            return chain.filter(webExchange);
        }
        return ResponseHelper.sendJson(webExchange.getResponse(), HttpResponseStatus.UNAUTHORIZED);
    }

    private boolean noNeedLogin(final HttpServerRequest request) {
        for (String pattern : noNeedLoginPaths) {
            if (AntPathMatcher.getDefaultInstance().match(pattern, request.fullPath())) {
                return true;
            }
        }
        return false;
    }

}
