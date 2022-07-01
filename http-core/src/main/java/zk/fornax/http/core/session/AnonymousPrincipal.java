package zk.fornax.http.core.session;

import java.security.Principal;

import lombok.Getter;

public class AnonymousPrincipal implements Principal {

    private static final String DEFAULT_NAME = "__anonymous__";

    @Getter
    private final String name;

    public AnonymousPrincipal() {
        this(DEFAULT_NAME);
    }

    public AnonymousPrincipal(String name) {
        this.name = name;
    }

}
