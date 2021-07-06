package EmbeddedChannel;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;

/**
 * @author lucky
 */
public class EmbeddedChannelOutboundDemo {
    public class SimpleOutHandlerA extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出站处理器 A: 被回调");
            super.write(ctx, msg, promise);
        }
    }

    public class SimpleOutHandlerB extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出站处理器 B: 被回调");
            super.write(ctx, msg, promise);
        }
    }

    public class SimpleOutHandlerC extends ChannelOutboundHandlerAdapter {
        @Override
        public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
            System.out.println("出站处理器 C: 被回调");
            super.write(ctx, msg, promise);
        }
    }
    public void test() {
        /**
         *  ChannelInitializer 实际上继承了ChannelInboundHandlerAdapter
         *  遗传你他也是一个handle处理器，但是一条通道只需要初始化一次，因此，
         *  它会在回调中将自己充处理器链中移除
        **/
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) {
                ch.pipeline().addLast(new SimpleOutHandlerA());
                ch.pipeline().addLast(new SimpleOutHandlerB());
                ch.pipeline().addLast(new SimpleOutHandlerC());
                /**
                 *  最后添加的会被最先调用
                 *  输出：
                 *  出站处理器 C: 被回调
                 *  出站处理器 B: 被回调
                 *  出站处理器 A: 被回调
                 **/
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);
        ByteBuf buf = Unpooled.buffer();
        buf.writeInt(1);
        //向通道写一个出站报文(或数据包)
        channel.writeOutbound(buf);
        try {
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new EmbeddedChannelOutboundDemo().test();
    }
}
