package com.wot.future;

import org.junit.Test;
import org.springframework.util.StopWatch;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompeletableFutureTest {

    @Test
    public void supplyAsyncTest() throws ExecutionException, InterruptedException {
        System.out.println("start...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        CompletableFuture<String> future = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("say , 1");
                return "null";
            }

        });
        CompletableFuture<String> future2 = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                System.out.println("say , 2");
                return "kong";
            }
        });
        System.out.println(future.get());
        System.out.println(future2.get());
        stopWatch.stop();
        System.out.println("end...\nstopWatch:" + stopWatch.toString());
    }

    @Test
    public void runAsyncTest() throws ExecutionException, InterruptedException {
        System.out.println("start...");
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Void unused = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("run , 1");
            }
        }).get();
        Void unused2 = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                System.out.println("run , 2");
            }
        }).get();
        System.out.println(unused);
        System.out.println(unused2);
        stopWatch.stop();
        System.out.println("end...\nstopWatch:" + stopWatch.toString());
    }

    @Test
    public void completedFutureTest() throws ExecutionException, InterruptedException {
        System.out.println(CompletableFuture.completedFuture("123").get());
    }

    @Test
    public void thenRunAsyncTest() throws ExecutionException, InterruptedException {
        CompletableFuture<Void> future = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("say , 1");
            }
        }).thenRun(new Runnable() {
            @Override
            public void run() {
                System.out.println("say , 2");
            }
        });
        System.out.println(future.get());
    }

    @Test
    public void thenAcceptAsyncTest() {
        Void join = CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                return "a";
            }
        }).thenApplyAsync(new Function<String, String>() {
            @Override
            public String apply(String s) {
                System.out.println("获取上一个任务:" + s);
                return "b";
            }
        }).thenAcceptAsync(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("获取上一个任务:" + s);
                return;
            }
        }).join();
        System.out.println(join);
    }

}
