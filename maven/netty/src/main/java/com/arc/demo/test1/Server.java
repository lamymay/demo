package com.arc.demo.test1;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080); // 创建服务器套接字，指定端口号

            System.out.println("等待客户端连接...");
            Socket clientSocket = serverSocket.accept(); // 等待客户端连接

            System.out.println("客户端已连接");

            // 获取输入流和输出流
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            while (true) {
                String messageFromClient = in.readLine(); // 读取来自客户端的消息
                if (messageFromClient == null) {
                    break;
                }
                System.out.println("客户端说: " + messageFromClient);

                // 发送响应给客户端
                String response = "收到你的消息: " + messageFromClient;
                out.println(response);
            }

            clientSocket.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
