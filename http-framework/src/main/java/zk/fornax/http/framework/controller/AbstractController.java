package zk.fornax.http.framework.controller;

import zk.fornax.http.framework.Dto;
import zk.fornax.http.framework.Po;
import zk.fornax.http.framework.Resource;
import zk.fornax.http.framework.Vo;

public class AbstractController<P extends Po<D>, D extends Dto, V extends Vo<P>> {

    protected final Resource<P, D, V> resource;

    public AbstractController(Resource<P, D, V> resource) {
        this.resource = resource;
    }

    public Object handleRequest() {
        return null;
    }

}
