package com.arc.demo.test1;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 8080); // 连接服务器，指定服务器地址和端口号

            // 获取输入流和输出流
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            while (true) {
                System.out.print("请输入消息: ");
                String message = reader.readLine(); // 从控制台读取用户输入的消息
                out.println(message); // 发送消息给服务器

                String response = in.readLine(); // 读取服务器的响应
                System.out.println("服务器回复: " + response);
            }

            //clientSocket.close(); // 这里注释掉了，可以保持连接
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
