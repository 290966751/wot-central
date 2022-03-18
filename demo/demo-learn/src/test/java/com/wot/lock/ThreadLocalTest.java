package com.wot.lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadLocalTest {
    static class Order {
        private final String orderNo;

        public Order(String orderNo) {
            this.orderNo = orderNo;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("Order{");
            sb.append("orderNo='").append(orderNo).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }
    static class OrderService {
//        private String orderNo;
        private ThreadLocal<String> orderNo = new ThreadLocal<String>();

        public String getOrderNo() {
            return orderNo.get();
        }

        public void setOrderNo(String orderNo) {
            this.orderNo.set(orderNo);
        }

        public Order getOrderInfo() {
            return new Order(getOrderNo());
        }
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        OrderService orderService = new OrderService();
        CountDownLatch countDownLatch = new CountDownLatch(3);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                orderService.setOrderNo("111");
                System.out.println(orderService.getOrderInfo());
                countDownLatch.countDown();
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                orderService.setOrderNo("222");
                System.out.println(orderService.getOrderInfo());
                countDownLatch.countDown();
            }
        });
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                orderService.setOrderNo("333");
                System.out.println(orderService.getOrderInfo());
                countDownLatch.countDown();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
        }

    }

}
