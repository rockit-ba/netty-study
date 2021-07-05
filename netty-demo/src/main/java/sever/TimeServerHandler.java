package sever;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *  它与前面的示例(discardserverhandle)不同之处在于，它发送一条包含32位整数的消息，而不接收任何请求，并在消息发送后关闭连接。
 *  在本例中，您将学习如何构造和发送消息，以及如何在完成时关闭连接。
 *
 * 因为我们将忽略任何接收到的数据，而是在建立连接后立即发送消息，所以这次不能使用channelRead()方法。
 * 相反，我们应该重写channelActive()方法。实现如下:
**/
public class TimeServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *  channelActive()方法将在建立连接并准备生成流量时被调用。让我们用这个方法写一个32位整数来表示当前时间。
    **/
    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        /**
         *  要发送一个新消息，我们需要分配一个包含该消息的新缓冲区。
         *  我们将编写一个32位整数，因此我们需要一个容量至少为4字节的ByteBuf。
         *  通过ChannelHandlerContext.alloc()获取当前的ByteBufAllocator并分配一个新的缓冲区。
        **/
        final ByteBuf time = ctx.alloc().buffer(4);
        time.writeInt((int) (System.currentTimeMillis() / 1000L + 2208988800L));
        /**
         *  像往常一样，我们编写已构造的消息。
         *  但是等等，翻转在哪里?我们以前不是在NIO(java nio库)中发送消息之前调用java.nio.ByteBuffer.flip()吗?
         *  ByteBuf没有这样的方法，因为它有两个指针;一个用于读操作，另一个用于写操作。
         *  当您向ByteBuf写入内容时，写入器索引增加，而读取器索引不变。读者索引和作者索引分别表示消息开始和结束的位置。
         *
         *  相比之下，NIO缓冲区没有提供一种干净的方法来确定消息内容的开始和结束位置，
         *  而无需调用flip方法。当您忘记翻转缓冲区时，您将遇到麻烦，
         *  因为没有任何东西或不正确的数据将被发送。这样的错误不会在Netty中发生，因为有不同的指针用于不同的操作类型。
         *
         *  另一点要注意的是，ChannelHandlerContext.write()(和writeAndFlush())方法返回一个ChannelFuture。
         *  ChannelFuture表示尚未发生的I/O操作。
         *  这意味着，任何请求的操作可能还没有被执行，因为在Netty中所有的操作都是异步的。
         *  例如，以下代码示例了：我们能够做到在消息发送之前关闭连接:
         *  Channel ch = ...;
         *  ch.writeAndFlush(message);
         *  ch.close();
         *
         *  因此，您需要在完成ChannelFuture之后再调用close()方法，该方法由write()方法返回，
         *  并在写操作完成时通知它的侦听器。
         *  请注意，close()也可能不会立即关闭连接，它返回一个ChannelFuture（无限套娃）。
         *
         **/
        final ChannelFuture f = ctx.writeAndFlush(time);
        /**
         *  那么当写请求完成时，我们如何得到通知呢?这就像向返回的ChannelFuture添加一个ChannelFutureListener一样简单。
         *  这里，我们创建了一个新的匿名ChannelFutureListener，它在操作完成时关闭Channel。
         *
         * 或者，你可以使用一个预定义的侦听器来简化代码: f.addListener(ChannelFutureListener.CLOSE);
        **/
        f.addListener(new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) throws Exception {
                assert f == future;
                ctx.close();
            }
        });
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}