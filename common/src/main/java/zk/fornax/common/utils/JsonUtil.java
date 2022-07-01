package zk.fornax.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class JsonUtil {

    public static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final ObjectMapper OM = new JsonMapper()
        .registerModule(new JavaTimeModule())
        .setTimeZone(TimeZone.getTimeZone(ZoneId.of("CTT", ZoneId.SHORT_IDS)))
        .setDateFormat(new SimpleDateFormat(DEFAULT_DATE_TIME_PATTERN));

    private static final ObjectWriter OW = OM.writerWithDefaultPrettyPrinter();

    private JsonUtil() {
    }

    public static String toJson(Object object, boolean pretty) throws JsonProcessingException {
        return pretty ? OW.writeValueAsString(object) : OM.writeValueAsString(object);
    }

    public static String toJsonPretty(Object object) throws JsonProcessingException {
        return toJson(object, true);
    }

    public static String toJson(Object object) throws JsonProcessingException {
        return toJson(object, false);
    }

    public static <T> T readValue(String content, Class<T> clazz) throws JsonProcessingException {
        return OM.readValue(content, clazz);
    }

    public static <T> T readValue(InputStream inputStream, Class<T> clazz) throws IOException {
        return OM.readValue(inputStream, clazz);
    }

    @SuppressWarnings("unchecked")
    public static Map<Object, Object> readValue(String content) throws JsonProcessingException {
        return OM.readValue(content, HashMap.class);
    }

}
