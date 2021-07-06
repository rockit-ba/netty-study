package NettyCoder;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 *  测试自定义解码器的handle
**/
public class IntegerProcessHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 经过解码器handle处理 这里的对象已经成为一个integer 对象了 可直接强转
        Integer integer = (Integer) msg;
        System.out.println("打印出一个整数: " + integer);
    }
}