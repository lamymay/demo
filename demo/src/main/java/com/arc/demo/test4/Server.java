
package com.arc.demo.test4;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static List<ClientHandler> clients = new ArrayList<>();
    private static ConcurrentHashMap<ClientHandler, PrintWriter> clientWriters = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            System.out.println("服务器已启动，等待客户端连接...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端已连接，IP地址: " + clientSocket.getInetAddress() + "，端口号: " + clientSocket.getPort());

                // 创建一个新的客户端处理线程
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
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

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                clientWriters.put(this, out); // 存储客户端的输出流

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }

                    System.out.println("客户端 " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " 说: " + message);

                    // 检查消息是否是私有消息
                    if (message.startsWith("/private ")) {
                        sendPrivateMessage(message);
                    } else {
                        broadcastMessage(message, this);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    clients.remove(this);
                    clientWriters.remove(this);
                    System.out.println("客户端 " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " 断开连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 发送私有消息给指定客户端
        private void sendPrivateMessage(String message) {
            String[] parts = message.split(" ", 3); // 格式: /private <目标客户端地址> <消息内容>
            if (parts.length == 3) {
                String targetClientAddress = parts[1];
                String privateMessage = parts[2];

                for (ClientHandler client : clients) {
                    if (client.getClientAddress().equals(targetClientAddress)) {
                        PrintWriter targetWriter = clientWriters.get(client);
                        targetWriter.println("私有消息来自 " + getClientAddress() + ": " + privateMessage);
                    }
                }
            }
        }

        // Getter方法用于获取客户端的地址信息
        public String getClientAddress() {
            return clientSocket.getInetAddress() + ":" + clientSocket.getPort();
        }
    }

    // 广播消息给所有客户端，除了发送者
    private static void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                PrintWriter writer = clientWriters.get(client);
                writer.println("客户端 " + sender.getClientAddress() + " 说: " + message);
            }
        }
    }
}
