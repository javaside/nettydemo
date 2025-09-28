package org.example.buff;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

public class PooledByteBufAllocatoTest {
    public static void main(String[] args) {
        // 使用默认配置（自动根据CPU核心数和内存大小计算堆内/堆外区域数量）
        //PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

        // 自定义配置：2个堆内区域，2个堆外区域，页面大小16KB，最大阶数9（chunkSize=8MB）
        PooledByteBufAllocator allocator = new PooledByteBufAllocator(
                false,  // 不优先使用堆外内存
                2,      // 堆内区域数量
                2,      // 堆外区域数量
                16 * 1024, // 页面大小
                9,      // 最大阶数
                512,    // 小内存缓存大小
                128,    // 普通内存缓存大小
                true    // 所有线程启用缓存
        );


        // 分配初始容量1KB，最大容量2KB的堆内缓冲区
        System.out.println("page: 1");
        ByteBuf heapBuf = allocator.heapBuffer(16 * 1024, Integer.MAX_VALUE);
        ByteBuf heapBufsub1 = allocator.heapBuffer(1 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 2");
        ByteBuf heapBuf1 = allocator.heapBuffer(2 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 3");
        ByteBuf heapBuf2 = allocator.heapBuffer(3 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 4");
        ByteBuf heapBuf3 = allocator.heapBuffer(4 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 5");
        ByteBuf heapBuf4 = allocator.heapBuffer(5 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 6");
        ByteBuf heapBuf5 = allocator.heapBuffer(6 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 7");
        ByteBuf heapBuf6 = allocator.heapBuffer(7 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 8");
        ByteBuf heapBuf7 = allocator.heapBuffer(8 * 16 * 1024, Integer.MAX_VALUE);
        System.out.println("page: 9");
        ByteBuf heapBuf8 = allocator.heapBuffer(9 * 16 * 1024, Integer.MAX_VALUE);

        ByteBuf heapBuf9 = allocator.heapBuffer(16, Integer.MAX_VALUE);

        try {
            // 写入数据
            heapBuf.writeBytes("Hello Netty".getBytes());

            // 读取数据
            byte[] data = new byte[heapBuf.readableBytes()];
            heapBuf.readBytes(data);
            System.out.println(new String(data));
        } finally {
            // 释放内存（触发 PoolArena.free()）
            heapBuf.release();
            heapBuf1.release();
            heapBuf2.release();
            heapBuf3.release();
            heapBuf4.release();
            heapBuf5.release();
            heapBuf6.release();
            heapBuf7.release();
            heapBuf8.release();
            heapBuf9.release();
            heapBufsub1.release();
        }

        System.out.println(allocator.dumpStats());
        System.out.println("====");
        System.out.println(allocator.metric());
    }
}
