package client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TimeClient {
    public static void main(String[] args) throws Exception {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        
        try {
            // Bootstrap类似于ServerBootstrap，不同的是它用于非服务器通道，如客户端或无连接通道。
            Bootstrap b = new Bootstrap();
            // 如果你只指定一个EventLoopGroup，它将同时作为boss组和worker组使用。但是boss worker并不用于客户端。
            b.group(workerGroup);
            // NioSocketChannel被用于创建客户端通道，而不是NioServerSocketChannel。
            b.channel(NioSocketChannel.class);
            // 注意，这里不像ServerBootstrap那样使用childOption()，因为客户端SocketChannel没有父类。
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new TimeClientHandler());
                }
            });
            
            // 启动客户端
            // 我们应该调用connect()方法，而不是bind()方法。
            ChannelFuture f = b.connect(host, port).sync();

            // 连接关闭之前一直等待
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}