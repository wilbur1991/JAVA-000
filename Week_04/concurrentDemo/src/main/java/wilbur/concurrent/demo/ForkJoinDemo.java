/*******************************************************
 * Copyright (C) 2020 demo - All Rights Reserved
 *
 * This file is part of concurrentDemo.
 * Unauthorized copy of this file, via any medium is strictly prohibited.
 * Proprietary and Confidential.
 *
 * @Date 2020-11-10
 * @Author jiangwenbo
 *
 *******************************************************/

package wilbur.concurrent.demo;


import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinDemo {
    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ForkJoinPool forkJoinPool = new ForkJoinPool(16);
        Fibonacci fibonacci = new Fibonacci(36);
        int result = forkJoinPool.invoke(fibonacci);

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);

        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");

    }

    static class Fibonacci extends RecursiveTask<Integer> {
        final int n;

        public Fibonacci(int n) {
            this.n = n;
        }

        @Override
        protected Integer compute() {
            if (n <= 1) {
                return 1;
            }
            Fibonacci f1 = new Fibonacci(n - 1);
            f1.fork();
            Fibonacci f2 = new Fibonacci(n - 2);

            return f2.compute() + f1.join();
        }
    }
}
