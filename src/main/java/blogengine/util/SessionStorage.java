package blogengine.util;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionStorage {
    
    private volatile Map<String, Integer> sessions = new HashMap<>();

    public Map<String, Integer> getSessions() {
        return sessions;
    }
}
