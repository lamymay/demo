package com.arc.demo.extend;

import com.arc.demo.EchoService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class EchoServiceV1 implements EchoService {

    final private String message;
    private final static HashMap<String, Object> contextMap = new HashMap<>();

    static {
        contextMap.put("now", LocalDateTime.now());
        contextMap.put("version", EchoServiceV1.class);
    }

    public EchoServiceV1(String message) {
        this.message = message;
    }

    @Override
    public Object echo() {
        contextMap.put("now", LocalDateTime.now());
        contextMap.put("status", "echo");
        contextMap.put("message", message);
        return contextMap;
    }

    @Override
    public Map<String, Object> prepare(String key, Object value) {
        contextMap.put(key, value);
        contextMap.put("status", "prepare");
        return contextMap;
    }

    @Override
    public Map<String, Object> process() {
        contextMap.put("process", LocalDateTime.now());
        contextMap.put("status", "process");
        return contextMap;

    }

//    @Override
//    public Map<String, Object> cancel() {
//        contextMap.put("status", "cancel");
//        contextMap.put("cancel", LocalDateTime.now());
//        return contextMap;
//    }


}
