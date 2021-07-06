package EmbeddedChannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;


/**
 * @author lucky
 */
public class EmbeddedChannelInboundDemo {
    static class SimpleInHandlerA extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("入站处理器 A: 被回调 ");
            /**
             * 打印当前Handler业务处理器的信息，然后调用父
             * 类的channelRead()方法，而父类的channelRead()方法会自动调用下一个
             * inBoundHandler的channelRead()方法，并且会把当前inBoundHandler入站处理器中处
             * 理完毕的对象传递到下一个inBoundHandler入站处理器，
             *
             * 也就是说不调用super.channelRead 整个调用链就不会往下
            **/
//            super.channelRead(ctx, msg);
            // 还可以使用以下方法达到传递效果
            // 若要阶段handle链，不调用这两个方法即可
            /**
             *  如果要截断其他
             * 的入站处理的流水线操作（使用Xxx指代），也可以同样处理
            **/
            ctx.fireChannelRead(msg);
        }
    }

    static class SimpleInHandlerB extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("入站处理器 B: 被回调 ");
            super.channelRead(ctx, msg);
        }
    }

    static class SimpleInHandlerC extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println("入站处理器 C: 被回调 ");
            super.channelRead(ctx, msg);
        }
    }

    public static void main(String[] args) {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) {
                ch.pipeline().addLast(new SimpleInHandlerA());
                ch.pipeline().addLast(new SimpleInHandlerB());
                ch.pipeline().addLast(new SimpleInHandlerC());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);
        //向通道写一个入站报文（数据包）
        channel.writeInbound(buf);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
