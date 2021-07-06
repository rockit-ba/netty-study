package nio;

import java.nio.IntBuffer;

/**
 * @author lucky
 */
public class BufferDemo {
    public static void main(String[] args) {
        // 抽象类，使用工厂方法获取实例
        IntBuffer intBuffer = IntBuffer.allocate(7);
        // 7
        System.out.println(intBuffer.capacity());
        // 0
        System.out.println(intBuffer.position());
        // 7 写模式下 limit 表示的是一个静态的可写个数上限值
        System.out.println(intBuffer.limit());

        System.out.println("##########################");
        // 开始即使写模式
        intBuffer.put(1);
        intBuffer.put(2);
        intBuffer.put(3);
        // 7
        System.out.println(intBuffer.capacity());
        // 3
        System.out.println(intBuffer.position());
        // 7 写模式下 limit 表示的是一个静态的可写个数上限值
        System.out.println(intBuffer.limit());
        System.out.println("##########################");

        // 切换读模式
        intBuffer.flip();
        // 7
        System.out.println(intBuffer.capacity());
        // 0
        System.out.println(intBuffer.position());
        // 3 读模式下 limit 表示的可读个数的上限
        System.out.println(intBuffer.limit());
        System.out.println("##########################");

        // 取出一个
        System.out.println("取出元素"+intBuffer.get());
        // 7
        System.out.println(intBuffer.capacity());
        // 1 读到了index 1 ，表示下一个要读的索引位
        System.out.println(intBuffer.position());
        // 3 上限不变（上限是一个固定的数字，一旦他翻转之后，可读的上限就确定了，
        // 这里是上限，不是还可以读多少个，不同于position的可变性）
        System.out.println(intBuffer.limit());
        System.out.println("##########################");

        intBuffer.compact(); // 读模式再切换写模式，此方法还原原来的写位（不同于clear）
        // 7
        System.out.println(intBuffer.capacity());
        // 2 读走一个 （写模式下 改索引表示下一个要写入的位置）
        System.out.println(intBuffer.position());
        // 7
        System.out.println(intBuffer.limit());
        System.out.println("##########################");

//        intBuffer.clear(); // 直接清空原来的写
//        // 7
//        System.out.println(intBuffer.capacity());
//        // 0
//        System.out.println(intBuffer.position());
//        // 7
//        System.out.println(intBuffer.limit());

        /**
         *  capacity 一旦初始化不可变
         *  limit 表示一个上限，在一个模式下不可变，代表元素个数
         *  position 随着操作而变化，代表索引
         *
        **/

    }
}
