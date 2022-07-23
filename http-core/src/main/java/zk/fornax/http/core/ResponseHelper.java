package zk.fornax.http.core;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import zk.fornax.common.rest.RestShuck;

public class ResponseHelper {

    private ResponseHelper() {
    }

    public static Mono<Void> sendOk(HttpServerResponse response, HttpResponseStatus httpResponseStatus) {
        return sendJson(response, httpResponseStatus, RestShuck.CODE_OK);
    }

    public static Mono<Void> sendJson(HttpServerResponse response, HttpResponseStatus httpResponseStatus) {
        return sendJson(response, httpResponseStatus, httpResponseStatus.reasonPhrase());
    }

    public static Mono<Void> sendJson(HttpServerResponse response, HttpResponseStatus httpResponseStatus, String message) {
        return sendJson(response, httpResponseStatus, httpResponseStatus.code(), message);
    }

    public static Mono<Void> sendJson(HttpServerResponse response, String jsonMessage) {
        return send(response, HttpResponseStatus.OK, HttpHeaderValues.APPLICATION_JSON.toString(), jsonMessage);
    }

    public static Mono<Void> sendJson(HttpServerResponse response, HttpResponseStatus httpResponseStatus, int code) {
        return sendJson(response, httpResponseStatus, code, RestShuck.MESSAGE_OK);
    }

    public static Mono<Void> sendJson(HttpServerResponse response, HttpResponseStatus httpResponseStatus, int code, String message) {
        String json = "{\"code\": " + code + ", \"message\": \"" + message + "\"}";
        return send(response, httpResponseStatus, HttpHeaderValues.APPLICATION_JSON.toString(), json);
    }

    public static Mono<Void> send(HttpServerResponse response, HttpResponseStatus httpResponseStatus, String contentType, String content) {
        return response.status(httpResponseStatus)
            .header(HttpHeaderNames.CONTENT_TYPE, contentType)
            .sendString(Mono.just(content))
            .then();
    }

}
