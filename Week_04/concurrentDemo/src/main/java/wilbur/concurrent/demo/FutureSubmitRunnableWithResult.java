package wilbur.concurrent.demo;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 本周作业：（必做）思考有多少种方式，在main函数启动一个新线程或线程池，
 * 异步运行一个方法，拿到这个方法的返回值后，退出主线程？
 * 写出你的方法，越多越好，提交到github。
 * <p>
 * 一个简单的代码参考：
 */
public class FutureSubmitRunnableWithResult {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        new FutureSubmitRunnableWithResult().runFuture();
    }

    private void runFuture() throws ExecutionException, InterruptedException {
        long start = System.currentTimeMillis();
        // 在这里创建一个线程或线程池，
        // 异步执行 下面方法
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Result res = new Result();

        Future<Result> futureResult = executorService.submit(new Task(res), res);

        int result = futureResult.get().getSum();//这是得到的返回值
        executorService.shutdown();

        // 确保  拿到result 并输出
        System.out.println("异步计算结果为：" + result);

        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        // 然后退出main线程
    }

    private static int sum() {
        return fibo(36);
    }

    private static int fibo(int a) {
        if (a < 2) {
            return 1;
        }
        return fibo(a - 1) + fibo(a - 2);
    }

    class Task implements Runnable {
        Result result;

        public Task(Result result) {
            this.result = result;
        }

        @Override
        public void run() {
            result.sum = sum();
        }
    }

    class Result {
        private int sum;

        public int getSum() {
            return sum;
        }

        public void setSum(int sum) {
            this.sum = sum;
        }
    }
}
