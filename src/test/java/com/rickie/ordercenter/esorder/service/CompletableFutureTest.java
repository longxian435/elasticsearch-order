package com.rickie.ordercenter.esorder.service;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

//@RunWith(SpringRunner.class)
@SpringBootTest
public class CompletableFutureTest {

    @Test
    public void test(){
        int CPU_COUNT = Runtime.getRuntime().availableProcessors();
        int corePoolSize = Math.max(2, Math.min(CPU_COUNT - 1, 4));
        int maxPoolSize = CPU_COUNT * 2 + 1;
        System.out.println("CPU_COUNT="+CPU_COUNT+",corePoolSize="+corePoolSize+",maxPoolSize="+maxPoolSize);
    }

    @Test
    public void allOfTest1() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(50);


        List<Shop> asynShops = new ArrayList<>();


        for (int i = 1; i <= Constant.COUNT; i++) {
            asynShops.add(new Shop());
        }
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();


        List<CompletableFuture<Integer>> futures = asynShops.stream()
                .map(asynShop -> CompletableFuture.supplyAsync(() -> asynShop.sleep(), executorService)
                ).collect(Collectors.toList());

        /*归并结果*/
        List<Integer> results = futures.stream().filter(future -> null != future.join()).map
                (CompletableFuture::join).collect(Collectors.toList());
        executorService.shutdown();

        stopWatch.stop();

        System.out.println("花费总时间 totalTime = " + stopWatch.getTotalTimeMillis());
        System.out.println("返回结果总条数为 totalCount = " + results.size());
        System.out.println("返回结果结果为  results = " + results.toString());
    }


    @Test
    public void allOfTest2() throws Exception {
        List<CompletableFuture> comlist = new ArrayList<>();
        int size = 20;
        CountDownLatch countDownLatch = new CountDownLatch(size);
        for (int i = 0; i < size; i++) {
            final int res = i;
            CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                    System.out.println(System.currentTimeMillis() + ":" + res + "执行完成");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return res + "";
            });
            f1.whenCompleteAsync((x, y) -> {
                try {
                    TimeUnit.SECONDS.sleep(1);
                    System.out.println(System.currentTimeMillis() + ":" + x + "回调执行完成");
                    countDownLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            comlist.add(f1);
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(comlist.toArray(new CompletableFuture[size]));
        //阻塞，直到所有任务结束。任务complete就会执行,handler里面不一定会执行..
        System.out.println(System.currentTimeMillis() + ":阻塞");
        all.join();
        System.out.println(System.currentTimeMillis() + ":阻塞结束");
        countDownLatch.await();
        System.out.println("回调都结束...");
    }

}