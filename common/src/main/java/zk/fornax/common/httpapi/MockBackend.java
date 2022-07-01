package zk.fornax.common.httpapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MockBackend {

    private int statusCode = 200;

    private Map<String, List<String>> headers = new HashMap<>();

    private String body = "";

    public MockBackend(String body) {
        this.body = body;
    }

}
