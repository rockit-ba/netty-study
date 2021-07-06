package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.CharsetUtil;

public class BufferTypeTest {

    //堆缓冲区
    public void testHeapBuffer() {
        //取得堆内存
        ByteBuf heapBuf = ByteBufAllocator.DEFAULT.buffer();
        heapBuf.writeBytes("蕾姆".getBytes(CharsetUtil.UTF_8));
        if (heapBuf.hasArray()) {
            //取得内部数组
            byte[] array = heapBuf.array();
            int offset = heapBuf.arrayOffset() + heapBuf.readerIndex();
            int length = heapBuf.readableBytes();
            System.out.println(new String(array, offset, length, CharsetUtil.UTF_8));
        }
        heapBuf.release();
    }

    //直接缓冲区
    public void testDirectBuffer() {
        ByteBuf directBuf = ByteBufAllocator.DEFAULT.directBuffer();
        directBuf.writeBytes("拉姆".getBytes(CharsetUtil.UTF_8));
        // 不等于 堆内存
        if (!directBuf.hasArray()) {
            int length = directBuf.readableBytes();
            byte[] array = new byte[length];
            //把数据读取（复制）到堆内存
            directBuf.getBytes(directBuf.readerIndex(), array);
            System.out.println(new String(array, CharsetUtil.UTF_8));
        }
        directBuf.release();
    }
}