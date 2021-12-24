package com.wot.demo.controller;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.wot.demo.service.IImmutablePairService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@RequestMapping(value = "/hystrix")
public class HystrixController {

    private static final Logger logger = LoggerFactory.getLogger(HystrixController.class);

    /**
     * hystrix超时接口demo
     * @param time
     * @return
     * @throws InterruptedException
     */
    @HystrixCommand(groupKey = "groupTest", commandKey = "commandTest", fallbackMethod = "testFallback"
            , commandProperties = {
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "2000")
            })
    @RequestMapping("/timeOut")
    public String testRequestTimeOut(@RequestParam(defaultValue = "1") int time) throws InterruptedException {
        logger.info("----------testRequestTimeOut start-----------------");
        Thread.sleep(1000 * time);
        logger.info("----------testRequestTimeOut end-----------------");
        return "success";
    }

    /**
     * test访问熔断后回调页面
     * @param time
     * @return
     */
    protected String testFallback(@RequestParam(defaultValue = "1") int time) {
        return "fallback";
    }

    /**
     * 合并请求
     * @param id
     * @return
     */
    @HystrixCollapser(collapserKey = "getLeft"
            , batchMethod = "hystrixCollapserBatchTest"
            , scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL
            , collapserProperties = {
                @HystrixProperty(name = "timerDelayInMilliseconds", value = "2000")
            })
    @RequestMapping("/meger")
    public Future<ImmutablePair<Long, String>> hystrixCollapserTest(Long id) {
        logger.info("----------hystrixCollapserTest start-----------------");
        return null;
    }

    @HystrixCommand
//    @RequestMapping("/megerBatch")
    public List<ImmutablePair<Long, String>> hystrixCollapserBatchTest(List<Long> ids) {
        logger.info("----------hystrixCollapserBatchTest start-----------------");
        logger.info("----------hystrixCollapserBatchTest ids:{}", ids.toString());
        List<ImmutablePair<Long, String>> list = new ArrayList<>(ids.size());
        for (Long id : ids) {
            list.add(new ImmutablePair(id, "total:" + id));
        }
        return list;
    }

    @Autowired
    private IImmutablePairService immutablePairService;

    @RequestMapping("/megerTest")
    public String megerTest(Long id) throws ExecutionException, InterruptedException {
        logger.debug("----------megerTest start-----------------");
        Future<ImmutablePair<Long, String>> immutablePairFuture = immutablePairService.queryForObject(id);

        Assert.notNull(immutablePairFuture, "queryForObject返回null, id:" + id);

        return immutablePairFuture.get().right;
    }

}
