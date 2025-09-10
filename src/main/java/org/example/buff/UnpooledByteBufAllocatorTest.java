package org.example.buff;

import io.netty.buffer.AbstractByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.util.ResourceLeakDetector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class UnpooledByteBufAllocatorTest {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        // 使用默认的 UnpooledByteBufAllocator（启用泄漏检测）
        UnpooledByteBufAllocator allocator = UnpooledByteBufAllocator.DEFAULT;

        // 创建一个未释放的 Direct Buffer
        ByteBuf buf = allocator.directBuffer(1024);

        // 1. 获取 AbstractByteBuf 的 Class 对象
        Class<?> clazz = AbstractByteBuf.class;

        // 2. 获取私有静态字段 "leakDetector"
        Field leakDetectorField = clazz.getDeclaredField("leakDetector");
        leakDetectorField.setAccessible(true); // 绕过访问限制

        // 3. 读取静态字段的值（参数为 null，因为是静态字段）
        ResourceLeakDetector<?> leakDetector = (ResourceLeakDetector<?>) leakDetectorField.get(buf);

        buf.writeBytes("Hello, Leak!".getBytes());

        // 手动触发 GC（实际中可能需要多次分配或大对象）
        buf = null;
        System.gc();

        // 等待泄漏检测触发（可能需要短暂延迟）
        try {
            Thread.sleep(1000);
            //allocator.directBuffer(1024);

            Method reportLeakMethod = ResourceLeakDetector.class.getDeclaredMethod("reportLeak");
            reportLeakMethod.setAccessible(true); // 绕过访问限制

            // 3. 调用 reportLeak() 强制触发泄漏检测
            reportLeakMethod.invoke(leakDetector);

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        // 退出前强制触发一次泄漏检测
        System.out.println("Leak detection complete.");
    }
}
