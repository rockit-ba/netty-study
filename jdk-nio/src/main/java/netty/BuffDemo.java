package netty;

import io.netty.buffer.*;

/**
 * @author lucky
 */
public class BuffDemo {
    /**
     *  无特殊需求前两个即可
    **/
    public void showAlloc() {
        ByteBuf buffer = null;
        //方法一：分配器默认分配初始容量为9，最大容量100的缓冲区
        buffer = ByteBufAllocator.DEFAULT.buffer(9, 100);
        //方法二：分配器默认分配初始容量为256，最大容量Integer.MAX_VALUE的缓冲区
        buffer = ByteBufAllocator.DEFAULT.buffer();
        //方法三：非池化分配器，分配基于Java的堆（Heap）结构内存缓冲区
        buffer = UnpooledByteBufAllocator.DEFAULT.heapBuffer();
        //方法四：池化分配器，分配基于操作系统管理的直接内存缓冲区
        buffer = PooledByteBufAllocator.DEFAULT.directBuffer();
        //…..其他方法
    }

    public static void main(String[] args) {
        // 注意，一旦提及索引指的都是下一次可读或者可写的位置
//        ByteBuf buffer = Unpooled.buffer(5);
        // 4.1版本 默认是池化的allocator
        // 可以通过Java系统参数（System Property）的选项
        // io.netty.allocator.type进行配置，配置时使用字符串值："unpooled"，"pooled"。
        // 也可以 bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        //       bootstrap.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(5, 20);
        // 0
        System.out.println(buffer.readerIndex());
        // 0
        System.out.println(buffer.writerIndex());
        // 5 标示总共的 可写容量的大小
        System.out.println(buffer.capacity());
        // 2147483647
        System.out.println(buffer.maxCapacity());
        System.out.println("########################");

        buffer.writeBytes(new byte[]{1,2,3});
        // 0
        System.out.println(buffer.readerIndex());
        // 3
        System.out.println(buffer.writerIndex());
        // 5 标示总共的 可写容量的大小
        System.out.println(buffer.capacity());
        // 20
        System.out.println(buffer.maxCapacity());
        System.out.println("########################");

        buffer.readByte();
        // 1
        System.out.println(buffer.readerIndex());
        // 3
        System.out.println(buffer.writerIndex());
        // 5 标示总共的 可写容量的大小
        System.out.println(buffer.capacity());
        // 20
        System.out.println(buffer.maxCapacity());
        System.out.println("########################");

        // getByte不会影响指针索引
        buffer.getByte(0);
        // 1
        System.out.println(buffer.readerIndex());
        // 3
        System.out.println(buffer.writerIndex());
        // 5 标示总共的 可写容量的大小
        System.out.println(buffer.capacity());
        // 20
        System.out.println(buffer.maxCapacity());
        System.out.println("########################");





    }

}
