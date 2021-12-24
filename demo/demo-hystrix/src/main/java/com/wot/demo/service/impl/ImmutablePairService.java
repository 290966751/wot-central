package com.wot.demo.service.impl;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCollapser;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import com.wot.demo.service.IImmutablePairService;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

@Service
public class ImmutablePairService implements IImmutablePairService {

    private static final Logger logger = LoggerFactory.getLogger(ImmutablePairService.class);

    /**
     * 合并请求，减少数据库IO，适用于并发量比较大的接口
     */
    @HystrixCollapser(collapserKey = "getLeft"
            , batchMethod = "queryForList"
            , scope = com.netflix.hystrix.HystrixCollapser.Scope.GLOBAL
            , collapserProperties = {
            @HystrixProperty(name = "timerDelayInMilliseconds", value = "1000"),
            @HystrixProperty(name = "maxRequestsInBatch", value = "10")
    })
    @Override
    public Future<ImmutablePair<Long, String>> queryForObject(Long id) {
        logger.info("queryForObject:{}", id);
        return null;
    }

    @HystrixCommand
    @Override
    public List<ImmutablePair<Long, String>> queryForList(List<Long> ids) {
        logger.info("queryForList:{}", ids.toString());

        //模拟sql查询会去除重复的数据
        Set<Long> set = new HashSet<>(ids);
        List<ImmutablePair<Long, String>> list = new ArrayList<>(ids.size());
        for (Long id : ids) {
            list.add(new ImmutablePair(id, "total:" + id));
        }

        Map<Long, ImmutablePair<Long, String>> map = new HashMap<>(list.size());
        for (ImmutablePair<Long, String> longStringImmutablePair : list) {
            map.put(longStringImmutablePair.getLeft(), longStringImmutablePair);
        }

        //按顺序补充重复数据
        List<ImmutablePair<Long, String>> result = new ArrayList<>(ids.size());
        for (Long id : ids) {
            result.add(map.getOrDefault(id, new ImmutablePair<Long, String>(0L, null)));
        }

        return result;
    }

}
