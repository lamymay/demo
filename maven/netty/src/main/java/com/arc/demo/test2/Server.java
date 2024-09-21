
package com.arc.demo.test2;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);

            System.out.println("等待客户端连接...");
            Socket clientSocket = serverSocket.accept();

            System.out.println("客户端已连接");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(System.in);

            // 创建一个单独的线程来接收客户端消息
            Thread receiveThread = new Thread(() -> {
                try {
                    String clientMessage;
                    while ((clientMessage = in.readLine()) != null) {
                        System.out.println("客户端说: " + clientMessage);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            while (true) {
                System.out.print("请输入消息: ");
                String message = scanner.nextLine(); // 从命令行读取用户输入的消息
                out.println(message);

                if (message.equalsIgnoreCase("exit")) {
                    break;
                }
            }

            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
