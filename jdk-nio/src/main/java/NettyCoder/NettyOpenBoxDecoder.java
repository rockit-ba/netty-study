package NettyCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class NettyOpenBoxDecoder {
    static String linebase = "\r\n";
    static String mylinebase = "$";
    static String content = "从零开始异世界生活";

    /**
     * LineBasedFrameDecoder 使用示例
     * 输出
     * ####################################
     * 1
     * 打印出一个str: 从零开始异世界生活
     * 1
     * 打印出一个str: 从零开始异世界生活
     * 2
     * 打印出一个str: 从零开始异世界生活从零开始异世界生活
     * 3
     * 打印出一个str: 从零开始异世界生活从零开始异世界生活从零开始异世界生活
    **/
    @Test
    public void testLineBasedFrameDecoder() {
        try {
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                @Override
                protected void initChannel(EmbeddedChannel ch) {
                    // 分隔符解码 只是对byte包进行疯了分隔符风格
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                    // 将分割后的byte数据string 解码
                    ch.pipeline().addLast(new StringDecoder());
                    // handle处理器
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);
            for (int j = 0; j < 100; j++) {
                //1-3之间的随机数
                int random = new Random().nextInt(3)+1;
                System.out.println(random);

                ByteBuf buf = Unpooled.buffer();
                for (int k = 0; k < random; k++) {
                    // 按照随机数 buf中写入随机数个content
                    buf.writeBytes(content.getBytes(StandardCharsets.UTF_8));
                }
                // 最后再整个随机数个的content后添加分行符
                buf.writeBytes(linebase.getBytes(StandardCharsets.UTF_8));
                channel.writeInbound(buf);
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * LengthFieldBasedFrameDecoder使用实例
     * 输出：
     * ##################################
     * 1
     * 打印出一个str: 从零开始异世界生活
     * 1
     * 打印出一个str: 从零开始异世界生活
     * 2
     * 打印出一个str: 从零开始异世界生活从零开始异世界生活
     */
    @Test
    public void testDelimiterBasedFrameDecoder() {
        try {
            // 定义 分割符
            final ByteBuf delimiter = Unpooled.copiedBuffer(mylinebase.getBytes(StandardCharsets.UTF_8));
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                @Override
                protected void initChannel(EmbeddedChannel ch) {
                    ch.pipeline().addLast(
                            // 最大包长； 解码后的帧是否应该去掉 xxx 分隔符； 分割符
                            new DelimiterBasedFrameDecoder(1024, true, delimiter));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);
            for (int j = 0; j < 100; j++) {
                //1-3之间的随机数
                int random = new Random().nextInt(3)+1;
                System.out.println(random);
                ByteBuf buf = Unpooled.buffer();
                for (int k = 0; k < random; k++) {
                    buf.writeBytes(content.getBytes(StandardCharsets.UTF_8));
                }
                buf.writeBytes(mylinebase.getBytes(StandardCharsets.UTF_8));
                channel.writeInbound(buf);
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}