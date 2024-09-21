package com.arc.demo.test3;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 8080);


            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(System.in);

            // 创建一个单独的线程来接收服务器消息
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println("服务器说: " + serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            while (true) {
                System.out.print("Client1 输入消息: ");
                String message = scanner.nextLine(); // 从命令行读取用户输入的消息
                out.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
