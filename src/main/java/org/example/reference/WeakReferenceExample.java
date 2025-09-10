package org.example.reference;

import java.lang.ref.WeakReference;

public class WeakReferenceExample {
    public static void main(String[] args) {
        // 创建一个强引用对象
        Object strongRef = new Object();

        // 创建弱引用，指向该对象
        WeakReference<Object> weakRef = new WeakReference<>(strongRef);

        // 此时对象既有强引用又有弱引用
        System.out.println("强引用存在时: " + weakRef.get());

        // 移除强引用
        strongRef = null;

        // 建议垃圾回收
        System.gc();

        // 给GC一点时间
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 弱引用可能为null（对象已被回收）
        System.out.println("强引用移除后: " + weakRef.get());
    }
}