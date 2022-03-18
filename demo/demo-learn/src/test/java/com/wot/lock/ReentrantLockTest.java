package com.wot.lock;

import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {

    public static class LockTest implements Runnable {

        static int i = 0;
        private final CountDownLatch countDownLatch;
        private final Lock lock;

        public LockTest(CountDownLatch countDownLatch, Lock lock, int j) {
            this.countDownLatch = countDownLatch;
            this.lock = lock;
            Thread.currentThread().setName("lock" + j);
        }

        @Override
        public void run() {
//            System.out.println(Thread.currentThread());
//            System.out.println(i);
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            try {
//                for (int k = 0; k < 1; k++) {
                    indexAdd();
//                }
            } finally {
                countDownLatch.countDown();
            }
        }

        private void indexAdd() {
            lock.lock();
            System.out.println(Thread.currentThread().getName() + "获取锁");
            try {
                i++;
            } finally {
                lock.unlock();
            }
        }
    }

    @Test
    public void ReentrantLockTest() {
        System.out.println("start:" + LockTest.i);
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        int size = 10;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        Lock lock = new ReentrantLock(false);
        for (int j = 0; j < size; j++) {
//            System.out.println(j);
//            lock.lock();
            executorService.submit(new LockTest(countDownLatch, lock, j));
        }
        try {
//            System.out.println(LockTest.i);
            countDownLatch.await();
            System.out.println("result:" + LockTest.i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }
    }

}
