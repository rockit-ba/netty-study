package NettyCoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 *  自定义解码器
**/
public class Byte2IntegerDecoder extends ByteToMessageDecoder {
    @Override
    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        // 一个整数 4个字节
        while (in.readableBytes() >= 4) {
            int i = in.readInt();
            System.out.println("解码出一个整数: " + i);
            out.add(i);
        }
    }
}