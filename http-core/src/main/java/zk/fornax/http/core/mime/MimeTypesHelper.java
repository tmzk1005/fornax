package zk.fornax.http.core.mime;

import java.util.HashMap;
import java.util.Map;

public final class MimeTypesHelper {

    private static final Map<String, String> mimeTypes = new HashMap<>(32);

    static {
        // 按需设置了部分，没有设置全，要设置全可以参考nginx的conf目录下的mime.types文件
        mimeTypes.put("html", "text/html");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("shtml", "text/html");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("xml", "text/xml");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("js", "application/javascript ");
        mimeTypes.put("atom", "application/atom+xml");
        mimeTypes.put("rss", "application/rss+xml");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("json", "application/json");
        mimeTypes.put("png", "image/png");
        mimeTypes.put("svg", "image/svg+xml");
        mimeTypes.put("svgz", "image/svg+xml");
        mimeTypes.put("ico", "image/x-icon");
    }

    private MimeTypesHelper() {
    }

    public static String getContentTypeBySuffix(String suffix) {
        return mimeTypes.get(suffix);
    }

}
