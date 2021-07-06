package sever;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class DiscardServer {

    private int port;

    public DiscardServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        /**
         *  NioEventLoopGroup是一个处理I/O操作的多线程事件循环。Netty为不同类型的传输提供了各种EventLoopGroup实现。
         *  在本例中，我们正在实现一个服务器端应用程序，因此将使用两个NioEventLoopGroup。
         *  第一个，通常被称为“boss”，接受传入的connection。
         *  第二种，通常被称为“worker”，在boss接受connection并将接受的连接注册到worker之后，处理接受的连接的流量。
         *  使用了多少线程以及如何将它们映射到创建的通道取决于EventLoopGroup的实现，甚至可以通过构造函数进行配置。
         **/
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /**
             *  ServerBootstrap是一个帮助器类，用于设置服务器。
             *  可以直接使用Channel设置服务器。
             *  但是，请注意，这是一个繁琐的过程，在大多数情况下不需要这样做。
             **/
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    // 这里，我们指定使用NioServerSocketChannel类，该类用于实例化接受传入连接的新Channel。
                    .channel(NioServerSocketChannel.class)
                    // 这里指定的处理程序将始终由新接受的Channel进行评估。
                    // ChannelInitializer是一个特殊的处理程序，用于帮助用户配置新的Channel。
                    // 您很可能希望通过添加一些处理程序(如DiscardServerHandler)来配置新通道的ChannelPipeline，
                    // 以实现您的网络应用程序。
                    // 随着应用程序变得复杂，您可能会向管道添加更多的处理程序，并最终将这个匿名类提进行提取。
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        /**
                         *  注意此方法是父通道（nioserversochetchannel）接收到连接请求
                         *  并创建了新的niosocketchannel之后才开始调用的
                        **/
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            ch.pipeline().addLast(new DiscardServerHandler());
                            ch.pipeline().addLast(new TimeServerHandler());
                        }
                    })
                    // 您还可以设置特定于Channel实现的参数。
                    // 我们正在编写一个TCP/IP服务器，因此我们被允许设置套接字选项，如tcpNoDelay和keepAlive。
                    .option(ChannelOption.SO_BACKLOG, 128)
                    // option()用于接受传入连接的NioServerSocketChannel 选项配置。
                    // childOption()用于父ServerChannel receive 的channel，在本例中为 NioSocketChannel。
                    // 即客户端channel
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            // 绑定并开始接受传入连接。
            ChannelFuture f = b.bind(port).sync();

            // 等到服务器套接字关闭
            // 在本例中，这不会发生，但您可以这样做以优雅地 // 关闭您的服务器。
            f.channel().closeFuture().sync();
        } finally {
            // 释放资源，优雅关闭
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    /**
     *  现在我们已经编写了第一个服务器，我们需要测试它是否真的能工作。
     *  测试它的最简单方法是使用telnet命令。
     *  例如，您可以在命令行中输入telnet localhost 7777 并输入一些内容。
     *
     * 但是，我们能说服务器工作正常吗?
     * 我们不能真正知道，因为它是一个丢弃服务器。你将得不到任何回应。
     * 为了证明它确实能工作，让我们修改服务器以打印它收到的内容。
     *
     * 我们已经知道，每当接收到数据时都会调用channelRead()方法。
     * 让我们将一些代码放入DiscardServerHandler的channelRead()方法中:
     *
     * ByteBuf in = (ByteBuf) msg;
     *     try {
     *         while (in.isReadable()) { // (1)
     *             System.out.print((char) in.readByte());
     *             System.out.flush();
     *         }
     *     } finally {
     *         ReferenceCountUtil.release(msg); // (2)
     *     }
    **/

    public static void main(String[] args) throws Exception {
        int port = 7777;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new DiscardServer(port).run();
    }
}