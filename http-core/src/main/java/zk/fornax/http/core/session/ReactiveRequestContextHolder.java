package zk.fornax.http.core.session;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveRequestContextHolder {

    private static final String REQUEST_CONTEXT_KEY = "REQUEST_CONTEXT_KEY";

    private ReactiveRequestContextHolder() {
    }

    public static Mono<RequestContext> getContext() {
        return Mono.deferContextual(contextView -> Mono.just(Context.of(contextView)))
            .filter(ReactiveRequestContextHolder::hasRequestContext)
            .flatMap(ReactiveRequestContextHolder::getRequestContext);
    }

    private static boolean hasRequestContext(Context context) {
        return context.hasKey(REQUEST_CONTEXT_KEY);
    }

    private static Mono<RequestContext> getRequestContext(Context context) {
        return context.get(REQUEST_CONTEXT_KEY);
    }

    public static Context withRequestContext(Mono<? extends RequestContext> requestContext) {
        return Context.of(REQUEST_CONTEXT_KEY, requestContext);
    }

}
