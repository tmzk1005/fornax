package zk.fornax.manager.controller;

import reactor.core.publisher.Mono;

import zk.fornax.common.heartbeat.HeartBeatInfo;
import zk.fornax.common.httpapi.HttpMethod;
import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.annotation.RequestBody;
import zk.fornax.http.framework.annotation.Route;
import zk.fornax.manager.security.ContextHelper;

@Controller 
public class HeartBeatController {

    @Route(method = HttpMethod.POST)
    public Mono<Void> heartbeat(@RequestBody HeartBeatInfo heartBeatInfo) {
        return ContextHelper.currentRequest().flatMap(request -> {
            String clientIp = request.remoteAddress().getAddress().getHostName();
            heartBeatInfo.setIp(clientIp);
            return Mono.empty();
        });
    }

}
