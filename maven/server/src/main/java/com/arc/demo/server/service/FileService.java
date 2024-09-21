package com.arc.demo.server.service;

import com.arc.util.YmlPropertiesLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FileService {

    private static final Logger log = LoggerFactory.getLogger(FileService.class);

    @Autowired
    private ResourceLoader resourceLoader;

    public Map<String,Object> readFile(String filePath) {
        log.info("readFile path={}", filePath);
        return YmlPropertiesLoader.loadConfig(filePath);

    }

}
