package com.arc.demo.test5;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client1 {
    public static void main(String[] args) {
        try {
            Socket clientSocket = new Socket("localhost", 8080);

            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            Scanner scanner = new Scanner(System.in);

            // 创建一个单独的线程来接收服务器消息
            Thread receiveThread = new Thread(() -> {
                try {
                    String serverResponse;
                    while ((serverResponse = in.readLine()) != null) {
                        System.out.println(serverResponse);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            System.out.print("请输入目标客户端的IP地址和端口（格式：IP:端口）: ");
            String targetClientAddress = scanner.nextLine();

            while (true) {
                System.out.print("请输入消息: ");
                String message = scanner.nextLine(); // 从命令行读取用户输入的消息

                // 发送私聊消息给服务器
                if (message.startsWith("/private ")) {
                    out.println(message);
                } else {
                    out.println(message); // 发送消息给服务器
                }

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
