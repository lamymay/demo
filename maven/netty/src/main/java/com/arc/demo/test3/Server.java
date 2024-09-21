package com.arc.demo.test3;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private static List<ClientHandler> clients = new ArrayList<>();

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

                while (true) {
                    String message = in.readLine();
                    if (message == null) {
                        break;
                    }

                    System.out.println("客户端 " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " 说: " + message);

                    // 将消息广播给所有客户端
                    broadcastMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                    clients.remove(this);
                    System.out.println("客户端 " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " 断开连接");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // 广播消息给所有客户端
        private void broadcastMessage(String message) {
            for (ClientHandler client : clients) {
                if (client != this) {
                    client.out.println("客户端 " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " 说: " + message);
                }
            }
        }
    }
}
