package client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

/**
 *  它与服务器端代码并没有真正的区别。
 *  那么ChannelHandler实现呢?它应该从服务器接收一个32位整数，
 *  将其转换为人类可读的格式，打印转换后的时间，并关闭连接
 *
 *  它看起来非常简单，与服务器端示例没有任何不同。
 *  然而，这个处理程序有时会拒绝抛出IndexOutOfBoundsException。
**/
public class TimeClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("fdsf");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        // 在TCP/IP协议中，Netty将从对等端发送的数据读入ByteBuf。
        ByteBuf m = (ByteBuf) msg;
        try {
            long currentTimeMillis = (m.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        } finally {
            m.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}