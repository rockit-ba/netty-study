package netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;

public class SliceTest {
    public static void main(String[] args) {
        new SliceTest().testSlice();
    }
    public void testSlice() {
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        // ridx: 0, widx: 0, cap: 9/100
        System.out.println("动作：分配ByteBuf(9, 100)"+ buffer);
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        // ridx: 0, widx: 4, cap: 9/100
        System.out.println("动作：写入4个字节 (1,2,3,4)"+buffer);
        ByteBuf slice = buffer.slice();
        // ridx: 0, widx: 4, cap: 4/4
        // slice()无参数方法所生成的切片就是源ByteBuf可读部分的浅层复制
        System.out.println("动作：切片 slice"+slice);
    }
}