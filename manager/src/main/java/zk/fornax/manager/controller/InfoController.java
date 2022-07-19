package zk.fornax.manager.controller;

import zk.fornax.http.framework.annotation.Controller;
import zk.fornax.http.framework.annotation.Route;

@Controller
public class InfoController {

    @Route
    public String test() {
        return "Fornax manager server works fine!";
    }

}
