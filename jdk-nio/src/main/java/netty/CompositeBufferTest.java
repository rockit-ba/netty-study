package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.CharsetUtil;

/**
 * 在很多通信编程场景下，需要多个ByteBuf组成一个完整的消息：例如HTTP协
 * 议传输时消息总是由Header（消息头）和Body（消息体）组成的。如果传输的内容
 * 很长，就会分成多个消息包进行发送，消息中的Header就需要重用，而不是每次发
 * 送都在程序中创建新的Header。
 * 下面演示一下通过CompositeByteBuf来复用Header
**/
public class CompositeBufferTest {

    public void byteBufComposite() {
        CompositeByteBuf cbuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //消息头
        ByteBuf headerBuf = Unpooled.copiedBuffer("从零开始异世界", CharsetUtil.UTF_8);
        //消息体1
        ByteBuf bodyBuf = Unpooled.copiedBuffer("拉姆", CharsetUtil.UTF_8);
        cbuf.addComponents(headerBuf, bodyBuf);
        sendMsg(cbuf);
        //在refCnt为0前, retain 为消息头增加一次引用，不要被回收
        headerBuf.retain();
        cbuf.release();

        // 准备第二次发消息
        cbuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        //消息体2
        bodyBuf = Unpooled.copiedBuffer("蕾姆", CharsetUtil.UTF_8);
        cbuf.addComponents(headerBuf, bodyBuf);
        sendMsg(cbuf);
        cbuf.release();
    }

    private void sendMsg(CompositeByteBuf cbuf) {
        //处理整个消息
        for (ByteBuf b :cbuf) {
            int length = b.readableBytes();
            byte[] array = new byte[length];
            //将CompositeByteBuf中的数据复制到数组中
            b.getBytes(b.readerIndex(), array);
            //处理一下数组中的数据
            System.out.print(new String(array, CharsetUtil.UTF_8));
        }
        System.out.println();
    }
}