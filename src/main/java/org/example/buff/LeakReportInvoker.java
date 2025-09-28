package org.example.buff;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.util.ResourceLeakDetector;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LeakReportInvoker {
    public static void main(String[] args) {
        try {
            System.setProperty("io.netty.leakDetection.targetRecords","4");
            System.setProperty("io.netty.leakDetection.level","SIMPLE");
            System.setProperty("io.netty.leakDetection.samplingInterval","1");
            PooledByteBufAllocator allocator = PooledByteBufAllocator.DEFAULT;

            Field leakDetectorFiled = AbstractByteBuf.class.getDeclaredField("leakDetector");
            leakDetectorFiled.setAccessible(true);
            ResourceLeakDetector<ByteBuf> leakDetector = (ResourceLeakDetector<ByteBuf>) leakDetectorFiled.get(null);


            // 2. 获取 private 方法 reportLeak()
            Method reportLeakMethod = ResourceLeakDetector.class.getDeclaredMethod("reportLeak");
            reportLeakMethod.setAccessible(true); // 绕过访问限制


            ByteBuf byteBuf = allocator.heapBuffer();
            byteBuf.writeByte(1);
            byteBuf.writeByte(2);
            byteBuf.writeByte(3);
            byteBuf.writeByte(4);
            byteBuf.writeByte(5);
            RecordTest recordTest = tee();
            byte b1 = byteBuf.readByte();
            byteBuf=null;
            System.gc();

            Thread.sleep(1000);
            System.out.println("#####");
            // 3. 调用 reportLeak() 强制触发泄漏检测
            Object invoke = reportLeakMethod.invoke(leakDetector);
            recordTest.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static RecordTest tee(){
        System.out.println("tee");
        return new RecordTest();
    }
}
