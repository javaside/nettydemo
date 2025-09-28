package io.netty.buffer;

public class IntPriorityQueueTest {
    public static void main(String[] args) {
        IntPriorityQueue queue = new IntPriorityQueue();
        queue.offer(10000);
        for (int i = 20; i > -1 ; i--) {
            queue.offer(i);
        }

        queue.offer(-331);

        while (!queue.isEmpty()) {
            System.out.println(queue.poll());
        }
    }
}
