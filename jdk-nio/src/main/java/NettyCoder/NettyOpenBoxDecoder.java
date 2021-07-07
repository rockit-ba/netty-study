package NettyCoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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


    /**
     * LengthFieldBasedFrameDecoder使用实例 1
     * 输出：
     *
     * 打印出一个str: 1次发送->从零开始异世界生活
     * 打印出一个str: 2次发送->从零开始异世界生活
     * 打印出一个str: 3次发送->从零开始异世界生活
     */
    @Test
    public void testLengthFieldBasedFrameDecoder1() {
        try {
            final LengthFieldBasedFrameDecoder spliter =
            new LengthFieldBasedFrameDecoder(
                    // 发送的数据包的最大长度。示例程序中该值为1024，表示一个数据包最多可发送1024个字节。
                    1024,
                    // 长度字段偏移量。指的是长度字段位于整个数据包内部的字节数组中的下标值。
                    0,
                    // 长度字段所占的字节数。如果长度字段是一个int整数，则为4，如果长度字段是一个short整数，则为2。
                    4,
                    /**
                     *
                     * // 长度的矫正值。
                     * // 在传输协议比较复杂的情况下，例如包含了长度字段、协议版本号、魔数等等。
                     * // 那么，解码时，就需要进行长度矫正。长度矫正值的计算公式为：
                     * // 内容字段偏移量–长度字段偏移量–长度字段的字节数。
                    **/
                    0,
                    /**
                     * // 丢弃的起始字节数。在有效数据字段Content前面，
                     * // 还有一些其他字段的字节，作为最终的解析结果，可以丢弃。
                     * // 上面的示例程序中，前面有4个节点的长度字段，它起辅助的作用，最终的结果中不需要这个长
                     * // 所以丢弃的字节数为4。
                    **/
                    4);
            ChannelInitializer i = new ChannelInitializer<EmbeddedChannel>() {
                @Override
                protected void initChannel(EmbeddedChannel ch) {
                    ch.pipeline().addLast(spliter);
                    ch.pipeline().addLast(new StringDecoder(Charset.forName("UTF-8")));
                    ch.pipeline().addLast(new StringProcessHandler());
                }
            };
            EmbeddedChannel channel = new EmbeddedChannel(i);
            for (int j = 1; j <= 100; j++) {
                ByteBuf buf = Unpooled.buffer();
                String s = j + "次发送->"+content;
                byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
                // 写入长度字段 int 类型 占4个字节
                buf.writeInt(bytes.length );
                // 写入content
                buf.writeBytes(bytes);
                // 写到入站
                channel.writeInbound(buf);
            }
            Thread.sleep(Integer.MAX_VALUE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}