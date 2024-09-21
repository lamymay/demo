package com.arc.demo.netty.test1;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ch.pipeline().addLast(new StringDecoder(), new StringEncoder(), new ClientHandler());
                        }
                    });

            Channel channel = bootstrap.connect("localhost", 8080).sync().channel();

            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            while (true) {
                String message = reader.readLine();
                channel.writeAndFlush(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }

    private static class ClientHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) {
            System.out.println("收到消息: " + msg);
        }
    }
}

//这些代码示例创建了一个基于Netty的简单聊天服务器和客户端。你可以根据需要扩展它们，以实现私聊或其他功能。客户端和服务器之间的通信使用字符串进行简单的文本消息传递。请注意，这只是一个起点，你可以根据你的需求进一步定制。