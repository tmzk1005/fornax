package zk.fornax.http.framework.utils;

import java.nio.file.Paths;

public class HttpPathNormalizer {

    private static final String SLASH = "/";

    private HttpPathNormalizer() {
    }

    public static String normalize(String path) {
        String newPath = Paths.get(path).normalize().toString();
        if (newPath.length() == 0) {
            return SLASH;
        }
        if (!newPath.startsWith(SLASH)) {
            newPath = SLASH + newPath;
        }
        return newPath;
    }

    public static String removeFirstSegment(String path) {
        String newPath = normalize(path);
        if (newPath.equals(SLASH)) {
            return newPath;
        }
        int secondSlashIndex = newPath.indexOf(SLASH, 1);
        if (secondSlashIndex < 0) {
            return SLASH;
        } else {
            return newPath.substring(newPath.indexOf(SLASH, 1));
        }
    }

}
