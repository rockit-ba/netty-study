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
    static short version = 6;

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
     * 常用但复杂
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
                    // 这里面出现的所有饿长度均指字节长度
            new LengthFieldBasedFrameDecoder(
                    // 发送的数据包的最大长度。示例程序中该值为1024，表示一个数据包最多可发送1024个字节。
                    1024,
                    // 长度字段偏移量（表示一个索引）。指的是长度字段位于整个数据包内部的字节数组中的下标值。
                    // 偏移量为0，也就是长度字段放在了最前面，处于数据包的起始位置。
                    0,
                    // 描述内容长度的 长度字段 所占的字节数（表示个数）。如果长度字段是一个int整数，则为4，如果长度字段是一个short整数，则为2。
                    4,
                    /**
                     *
                     * // 长度的矫正值。
                     * // 在传输协议比较复杂的情况下，例如包含了长度字段、协议版本号、魔数等等。
                     * // 那么，解码时，就需要进行长度矫正。长度矫正值的计算公式为：
                     * // 内容字段偏移量–长度字段偏移量–长度字段的字节数。
                     * 内容字段偏移量（表示一个索引）：首先 长度字段 偏移量为0，长度4，所以它占据的索引：0 1 2 3 下一个索引就是内容了，因此，内容偏移量为 4
                     * 实例中的值：4-0-4 = 0（事实上我们不会这样算，我们会直接写入中间添加内容的字节长度即可实现矫正）
                     * 如果情况复杂 长度字段 和内容之间还添加了 协议版本号、魔数 ，事实上我们的校正值 就是中间的东西所占的字节长度
                     * 矫正的意思就是正确地发现内容（content）
                    **/
                    0,
                    /**
                     * // 丢弃的起始字节数。在有效数据字段Content前面，
                     * // 还有一些其他字段的字节，作为最终的解析结果，可以丢弃。
                     * // 上面的示例程序中，前面有4个节点的长度字段，它起辅助的作用，最终的结果中不需要这个长
                     * // 所以丢弃的字节数为4。
                     * 最终抛弃的我们可以指定，如果写了字段长度+矫正值 那么会只留下content，如果写了字段长度，那么会留下中间的附带东西
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
                System.out.println(bytes.length);
                // 写入长度字段(示例程序最大为41 因此写入int即可)
                buf.writeInt(bytes.length);
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


    /**
     * LengthFieldBasedFrameDecoder使用实例 2  带版本号的
     * 长度字段  版本号  content
     *
     * 事实上还可以
     * 版本号  长度字段  魔数 content
     * 这样矫正值就是魔数的字节长度
     * 最后的忽略值就是 版本号  长度字段  魔数 的长度
     */
    @Test
    public void testLengthFieldBasedFrameDecoder2() {
        try {
            final LengthFieldBasedFrameDecoder spliter =
                    // 这里面出现的所有饿长度均指字节长度
                    new LengthFieldBasedFrameDecoder(
                            // 发送的数据包的最大长度。示例程序中该值为1024，表示一个数据包最多可发送1024个字节。
                            1024,
                            // 长度字段偏移量（表示一个索引）。指的是长度字段位于整个数据包内部的字节数组中的下标值。
                            // 偏移量为0，也就是长度字段放在了最前面，处于数据包的起始位置。
                            0,
                            // 描述内容长度的 长度字段 所占的字节数（表示个数）。如果长度字段是一个int整数，则为4，如果长度字段是一个short整数，则为2。
                            4,
                            /**
                             *
                             * // 长度的矫正值。
                             * // 在传输协议比较复杂的情况下，例如包含了长度字段、协议版本号、魔数等等。
                             * // 那么，解码时，就需要进行长度矫正。长度矫正值的计算公式为：
                             * // 内容字段偏移量–长度字段偏移量–长度字段的字节数。
                             * 内容字段偏移量（表示一个索引）：首先 长度字段 偏移量为0，长度4，所以它占据的索引：0 1 2 3 下一个索引就是内容了，因此，内容偏移量为 4
                             * 实例中的值：4-0-4 = 0（事实上我们不会这样算，我们会直接写入中间添加内容的字节长度即可实现矫正）
                             * 如果情况复杂 长度字段 和内容之间还添加了 协议版本号、魔数 ，事实上我们的校正值 就是中间的东西所占的字节长度
                             * 矫正的意思就是正确地发现内容（content）
                             * 矫正值始终是 长度字段和content之间内容所占的字节长度
                             **/
                            2,
                            /**
                             * // 丢弃的起始字节数。在有效数据字段Content前面，
                             * // 还有一些其他字段的字节，作为最终的解析结果，可以丢弃。
                             * // 上面的示例程序中，前面有4个节点的长度字段，它起辅助的作用，最终的结果中不需要这个长
                             * // 所以丢弃的字节数为4。
                             * 最终抛弃的我们可以指定，如果写了字段长度+矫正值 那么会只留下content，如果写了字段长度，那么会留下中间的附带东西
                             **/
                            6);
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
                System.out.println(bytes.length);
                // 写入长度字段(示例程序最大为41 因此写入int即可)
                buf.writeInt(bytes.length);
                buf.writeShort(version);
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