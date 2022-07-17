package zk.fornax.http.core.exchange;

import java.util.Objects;

import reactor.netty.http.server.HttpServerRequest;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.http.core.exchange.WebExchange.Builder;

public class DefaultWebExchangeBuilder implements WebExchange.Builder {

    private final WebExchange delegator;

    private HttpServerRequest request;

    private HttpServerResponse response;

    DefaultWebExchangeBuilder(WebExchange delegator) {
        this.delegator = delegator;
    }

    @Override
    public Builder request(HttpServerRequest request) {
        this.request = request;
        return this;
    }

    @Override
    public Builder response(HttpServerResponse response) {
        this.response = response;
        return this;
    }

    @Override
    public WebExchange build() {
        return new MutativeDecorator(delegator, request, response);
    }

    private static class MutativeDecorator extends WebExchangeDecorator {

        private HttpServerRequest request;

        private HttpServerResponse response;

        private MutativeDecorator(WebExchange delegator, HttpServerRequest request, HttpServerResponse response) {
            super(delegator);
            this.request = request;
            this.response = response;
        }

        @Override
        public HttpServerRequest getRequest() {
            return Objects.nonNull(request) ? request : getDelegator().getRequest();
        }

        @Override
        public HttpServerResponse getResponse() {
            return Objects.nonNull(response) ? response : getDelegator().getResponse();
        }

    }

}
