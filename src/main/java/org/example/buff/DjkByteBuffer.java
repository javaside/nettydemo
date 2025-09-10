package org.example.buff;

import java.nio.ByteBuffer;

public class DjkByteBuffer {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        System.out.println("初始化buffer：" + buffer);
        buffer.put("hello world".getBytes());
        buffer.flip();
        byte[] bytes = new byte[5];
        buffer.get(bytes);
        System.out.println("buffer数据：" + new String(bytes));

        buffer.compact();
        System.out.println("compact后buffer：" + buffer);

        bytes = new byte[5];
        buffer.get(bytes);
        System.out.println("buffer数据：" + new String(bytes));

//        byte[] bytes = new byte[buffer.remaining()];
//        buffer.get(bytes);
//
//        System.out.println("buffer数据：" + new String(bytes));
//        buffer.rewind();
//        bytes = new byte[buffer.remaining()];
//        buffer.get(bytes);
//        System.out.println("buffer数据：" + new String(bytes));
    }
}
