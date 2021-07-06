package NettyCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;

import java.util.concurrent.TimeUnit;

/**
 *  测试自定义解码器
**/
public class Byte2IntegerDecoderTester {
    public static void main(String[] args) {
        new Byte2IntegerDecoderTester().testByteToIntegerDecoder();
    }
    /**
     * 整数解码器的使用实例
     */
    public void testByteToIntegerDecoder() {
        ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) {
                // 注意顺序，解码之后会把解码的pojo 作为 msg 发送给后面的入站处理器
                ch.pipeline().addLast(new Byte2IntegerDecoder());
                ch.pipeline().addLast(new IntegerProcessHandler());
            }
        };
        EmbeddedChannel channel = new EmbeddedChannel(i);
        for (int j = 0; j < 100; j++) {
            ByteBuf buf = Unpooled.buffer();
            buf.writeInt(j);
            // 每次写出一个
            channel.writeInbound(buf);
        }
        try {
            TimeUnit.HOURS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}