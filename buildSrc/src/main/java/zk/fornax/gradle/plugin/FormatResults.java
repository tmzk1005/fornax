package zk.fornax.gradle.plugin;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

public class FormatResults {

    private final Set<String> unchanged = new ConcurrentSkipListSet<>();

    private final Set<String> succeed = new ConcurrentSkipListSet<>();

    private final Map<String, String> failed = new ConcurrentHashMap<>();

    public void addUnchangedFile(String fileName) {
        unchanged.add(fileName);
    }

    public void addSucceed(String fileName) {
        succeed.add(fileName);
    }

    public void addFailed(String fileName, String reason) {
        failed.put(fileName, reason);
    }

    public Set<String> getUnchanged() {
        return unchanged;
    }

    public Set<String> getSucceed() {
        return succeed;
    }

    public Map<String, String> getFailed() {
        return failed;
    }

}
