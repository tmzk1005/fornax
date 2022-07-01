package zk.fornax.common.utils;

import java.util.regex.Pattern;

public class IpUtil {

    public static final String IP_NUM = "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]?\\d)";

    public static final String IP_V4 = IP_NUM + "(\\." + IP_NUM + "){3}";

    public static final String IP_V4_SUBNET = IP_V4 + "/(\\d|1\\d|2\\d|30|31|32)";

    public static final Pattern IP_V4_SUBNET_PATTERN = Pattern.compile(IP_V4_SUBNET);

    public static final String IP_V4_RANGE = IP_V4 + "-" + IP_V4;

    public static final Pattern IP_V4_RANGE_PATTERN = Pattern.compile(IP_V4_RANGE);

    public static final Pattern IP_V4_PATTERN = Pattern.compile(IP_V4);

    private IpUtil() {
    }

    public static boolean isIpV4(String ip) {
        return IP_V4_PATTERN.matcher(ip).matches();
    }

    public static boolean isIpV4Subnet(String text) {
        return IP_V4_SUBNET_PATTERN.matcher(text).matches();
    }

    public static boolean isIpV4Range(String text) {
        return IP_V4_RANGE_PATTERN.matcher(text).matches();
    }

    public static long ipv4ToLong(String ip) {
        if (!isIpV4(ip)) {
            return -1;
        }
        final String[] segments = ip.split("\\.");
        return (Short.parseShort(segments[0]) & 0xffL) << 24
            | (Short.parseShort(segments[1]) & 0xffL) << 16
            | (Short.parseShort(segments[2]) & 0xffL) << 8
            | (Short.parseShort(segments[3]) & 0xffL);
    }

}
