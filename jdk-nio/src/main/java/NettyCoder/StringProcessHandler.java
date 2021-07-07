package NettyCoder;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutorGroup;

/**
 * @author lucky
 */
public class StringProcessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 经过解码器handle处理 这里的对象已经成为一个integer 对象了 可直接强转
        String str = (String) msg;
        System.out.println("打印出一个str: " + str);
    }
}