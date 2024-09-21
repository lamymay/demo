package com.arc.demo.server;

import com.arc.demo.server.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@SpringBootApplication
public class RestServerApplication {

    private static final Logger log = LoggerFactory.getLogger(RestServerApplication.class);

    @Resource
    FileService fileService;


    public static void main(String[] args) {
        String classpath = System.getProperty("java.class.path");
        System.out.println("************************");
        System.out.println("Classpath: " + classpath);
        System.out.println("************************");

        long t1 = System.currentTimeMillis();
        log.info("启动时参数打印{}", args);
        if (args != null) {
            for (String arg : args) {
                log.info("{}", arg);

            }
        }
        ConfigurableApplicationContext context = SpringApplication.run(RestServerApplication.class, args);

    }

    @RequestMapping("/readFile")
    @ResponseBody
    public ResponseEntity<Map<String,Object>> readFile(@RequestBody Map<String, String> request) {
        log.info("################################ readFile ");
        String path = request.get("path");
        return ResponseEntity.ok(fileService.readFile(path));
    }

}
