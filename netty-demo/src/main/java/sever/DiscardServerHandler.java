package sever;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *
 * @author 潘吉祥
 *
 * DiscardServerHandler扩展了ChannelInboundHandlerAdapter，它是ChannelInboundHandler的一个实现。
 * channelboundhandler提供了可以覆盖的各种事件处理程序方法。
 * 现在，扩展ChannelInboundHandlerAdapter就足够了，而不是自己实现处理程序接口(channelboundhandler)。
**/
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {

    /**
     *  我们在这里重写了channelRead()事件处理程序方法。
     *  每当从客户端接收到新数据时，将使用接收到的消息调用此方法。
     *  在本例中，接收到的消息类型是ByteBuf。
    **/
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
//        /**
//         *  ByteBuf是一个引用计数对象，它必须通过release()方法显式释放。
//         *  通常是这样实现的：
//         *   try {
//         *         // Do something with msg
//         *     } finally {
//         *         ReferenceCountUtil.release(msg);
//         *     }
//        **/
//        // 丢弃客户端的消息
//        ByteBuf in = (ByteBuf) msg;
//        try {
//            /**
//             *  这个低效的循环实际上可以简化为:
//             *  System.out.println(in.toString(io.netty.util.CharsetUtil.US_ASCII))
//            **/
////            while (in.isReadable()) {
////                System.out.print((char) in.readByte());
////                System.out.flush();
////            }
//            System.out.println(in.toString(CharsetUtil.US_ASCII));
//        } finally {
//            ReferenceCountUtil.release(msg);
//        }
//
//    }

    /**
     *  有响应的sever
    **/
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //ChannelHandlerContext对象提供各种操作，使您能够触发各种I/O事件和操作。
        // 这里，我们调用write(Object)来逐字写入接收到的消息。
        // 请注意，我们没有像在丢弃示例中那样释放接收到的消息。
        // 那是因为netty在写完后会帮你释放。
        ctx.write(msg);
        /**
         *  write(Object)不会将消息写入到连线中。
         *  它在内部进行缓冲，然后通过ctx.flush()将其刷新到网络上。
         *  或者，你也可以调用ctx.writeAndFlush(msg)来简洁。
        **/
        ctx.flush();
        // 如果再次运行telnet命令，您将看到服务器发回 您发送给它的内容。
    }

    /**
     *  当Netty由于I/O错误或处理程序实现由于在处理事件时抛出异常而引发异常时，
     *  使用Throwable调用exceptionCaught()事件处理程序方法。
     *  在大多数情况下，应该记录捕获的异常，并在这里关闭与其相关的通道，
     *  尽管此方法的实现可能因处理异常情况的方法不同而有所不同。
     *  例如，您可能希望在关闭连接之前发送带有错误代码的响应消息。
    **/
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 引发异常时关闭连接。
        cause.printStackTrace();
        ctx.close();
    }
}