package org.example.reference;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

public class WeakReferenceWithQueue {
    public static void main(String[] args) {
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        String obj = new String("WeakReferenceTest");

        WeakReference<Object> weakRef = new WeakReference<>(obj, queue);
        System.out.println(weakRef.get());
        System.out.println(weakRef);

        // 移除强引用
        obj = null;
        System.gc();

        try {
            Thread.sleep(1000);
            for (;;){
                WeakReference<?> ref = (WeakReference)queue.poll();
                if (ref == null) {
                    break;
                }

                System.out.println(weakRef);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}