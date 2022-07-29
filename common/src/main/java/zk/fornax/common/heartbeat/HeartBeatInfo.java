package zk.fornax.common.heartbeat;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeartBeatInfo {

    private long timestmap;

    private String osName;

    private String ip;

    private Map<String, String> systemProperties;

    private HeartBeatInfo(long timestamp) {
        this.timestmap = timestamp;
    }

    public static HeartBeatInfo newInstance() {
        HeartBeatInfo heartBeatInfo = new HeartBeatInfo(System.currentTimeMillis());
        heartBeatInfo.setOsName("TODO");
        return heartBeatInfo;
    }

}
