package org.example.buff;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class NettyByteBuffer {
    public static void main(String[] args) {
        ByteBuf buffer = Unpooled.buffer(1024);
        System.out.println("初始化buffer：" + buffer);
        buffer.writeByte(1);
        System.out.println("写入数据：" + buffer);
        buffer.writeByte(2);
        System.out.println("写入数据：" + buffer);
        buffer.writeByte(3);
        System.out.println("写入数据：" + buffer);

        System.out.println(buffer.getByte(0));
        System.out.println(buffer.getByte(1));
        System.out.println(buffer.getByte(2));

        System.out.println("buffer数据：" + buffer);

        System.out.println("buffer 0数据：" + buffer.getUnsignedByte(0));
        System.out.println("buffer 1数据：" + buffer.getUnsignedByte(1));
        System.out.println("buffer 2数据：" + buffer.getUnsignedByte(2));

        System.out.println("buffer数据：" + buffer);
    }
}
