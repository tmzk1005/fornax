package zk.fornax.http.core;

import java.util.Map;

public interface PathMatcher {

    boolean isPattern(String path);

    boolean match(String pattern, String path);

    Map<String, String> extractUriTemplateVariables(String pattern, String path);

}