package com.arc.demo.main;

import com.arc.demo.EchoService;
import com.arc.demo.extend.EchoServiceV1;

public class EchoMain {
    public EchoMain() {
    }

    public static void main(String[] args) throws InterruptedException {
        EchoService echoService = new EchoServiceV1("hello");
        long startRun = System.currentTimeMillis();
        while (System.currentTimeMillis() - startRun < 600000000L) {
            Thread.sleep(1000L);
            Object echo = echoService.echo();
            System.out.println(echo);
        }

    }
}
