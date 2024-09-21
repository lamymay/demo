package com.arc.demo;

import java.util.Map;

public interface EchoService {

    Object echo();

    Map<String,Object> prepare(String key,Object object);

    Map<String,Object> process();

}
