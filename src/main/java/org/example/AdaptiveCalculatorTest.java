package org.example;

import io.netty.util.internal.AdaptiveCalculator;

public class AdaptiveCalculatorTest {
    public static void main(String[] args) {
        AdaptiveCalculator calculator = new AdaptiveCalculator(64, 2048, 65536);
        for (int i = 0; i < 100; i++) {
            calculator.record(i);
            System.out.println(i+":" + calculator.nextSize());
        }
    }
}
