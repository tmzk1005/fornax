package zk.fornax.http.framework.utils;

import java.nio.file.Paths;

public class HttpPathNormalizer {

    private HttpPathNormalizer() {
    }

    public static String normalize(String path) {
        String newPath = Paths.get(path).normalize().toString();
        if (newPath.length() == 0) {
            return "/";
        }
        if (!newPath.startsWith("/")) {
            newPath = "/" + newPath;
        }
        return newPath;
    }

    public static String removeFirstSegment(String path) {
        String newPath = normalize(path);
        if (newPath.equals("/")) {
            return newPath;
        }
        int secondSlashIndex = newPath.indexOf("/", 1);
        if (secondSlashIndex < 0) {
            return "/";
        } else {
            return newPath.substring(newPath.indexOf("/", 1));
        }
    }

}
