package org.example.buff;

import io.netty.util.ResourceLeakDetector;
import io.netty.buffer.ByteBuf;

import java.lang.reflect.Method;

public class LeakReportInvoker {
    public static void main(String[] args) {
        try {
            // 1. 创建 ResourceLeakDetector 实例（模拟 Netty 内部逻辑）
            ResourceLeakDetector<ByteBuf> leakDetector = new ResourceLeakDetector<>(ByteBuf.class, 128);

            // 2. 获取 private 方法 reportLeak()
            Method reportLeakMethod = ResourceLeakDetector.class.getDeclaredMethod("reportLeak");
            reportLeakMethod.setAccessible(true); // 绕过访问限制

            // 3. 调用 reportLeak() 强制触发泄漏检测
            reportLeakMethod.invoke(leakDetector);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
