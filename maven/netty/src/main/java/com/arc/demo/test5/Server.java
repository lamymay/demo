package com.arc.demo.test5;

import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static ConcurrentHashMap<String, PrintWriter> clients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("服务器已启动，等待客户端连接...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已连接，IP地址: " + clientSocket.getInetAddress() + "，端口号: " + clientSocket.getPort());

                // 创建一个新的客户端处理线程
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 客户端处理类
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String clientAddress;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
            this.clientAddress = socket.getInetAddress() + ":" + socket.getPort();
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                clients.put(clientAddress, out); // 存储客户端的输出流

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }

                    System.out.println("客户端 " + clientAddress + " 说: " + message);

                    // 检查消息是否是私聊消息
                    if (message.startsWith("/private ")) {
                        sendPrivateMessage(message);
                    } else {
                        broadcastMessage(message, clientAddress);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    clients.remove(clientAddress);
                    System.out.println("客户端 " + clientAddress + " 断开连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 发送私聊消息给目标客户端
        private void sendPrivateMessage(String message) {
            String[] parts = message.split(" ", 3); // 格式: /private <目标客户端地址> <消息内容>
            if (parts.length == 3) {
                String targetClientAddress = parts[1];
                String privateMessage = parts[2];

                PrintWriter targetWriter = clients.get(targetClientAddress);
                if (targetWriter != null) {
                    targetWriter.println("私聊消息来自 " + clientAddress + ": " + privateMessage);
                }
            }
        }
    }

    // 广播消息给所有客户端，除了发送者
    private static void broadcastMessage(String message, String sender) {
        for (String clientAddress : clients.keySet()) {
            if (!clientAddress.equals(sender)) {
                PrintWriter writer = clients.get(clientAddress);
                writer.println("客户端 " + sender + " 说: " + message);
            }
        }
    }
}
