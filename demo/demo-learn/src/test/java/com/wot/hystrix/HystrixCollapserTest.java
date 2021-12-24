package com.wot.hystrix;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class HystrixCollapserTest {

    @Test
    public void megerToRequestTest() throws InterruptedException, ExecutionException {
        ImmutablePairService pairService = new ImmutablePairService();
        HystrixRequestContext context = null;
        try {
            context = HystrixRequestContext.initializeContext();
            //
            Future<ImmutablePair<Long, String>> queue = new ImmutablePairHystrixCollapser(1L, pairService).queue();
            Future<ImmutablePair<Long, String>> queue2 = new ImmutablePairHystrixCollapser(2L, pairService).queue();
            Future<ImmutablePair<Long, String>> queue3 = new ImmutablePairHystrixCollapser(3L, pairService).queue();
            Thread.sleep(110);
            Future<ImmutablePair<Long, String>> queue4 = new ImmutablePairHystrixCollapser(4L, pairService).queue();
            Thread.sleep(10);
            Future<ImmutablePair<Long, String>> queue5 = new ImmutablePairHystrixCollapser(5L, pairService).queue();
//            ImmutablePairHystrixCollapser immutablePairHystrixCollapser2 = new ImmutablePairHystrixCollapser(2L, pairService);
//            ImmutablePairHystrixCollapser immutablePairHystrixCollapser3 = new ImmutablePairHystrixCollapser(3L, pairService);
//            ImmutablePairHystrixCollapser immutablePairHystrixCollapser4 = new ImmutablePairHystrixCollapser(4L, pairService);
//            ImmutablePairHystrixCollapser immutablePairHystrixCollapser5 = new ImmutablePairHystrixCollapser(5L, pairService);
//            ImmutablePairHystrixCollapser immutablePairHystrixCollapser6 = new ImmutablePairHystrixCollapser(6L, pairService);
//        ImmutablePair<Long, String> pair1 = immutablePairHystrixCollapser.execute();
//        ImmutablePair<Long, String> pair2 = immutablePairHystrixCollapser2.execute();
//        ImmutablePair<Long, String> pair3= immutablePairHystrixCollapser3.execute();
//        ImmutablePair<Long, String> pair4 = immutablePairHystrixCollapser4.execute();
//        ImmutablePair<Long, String> pair5 = immutablePairHystrixCollapser5.execute();
            System.out.println(queue.get().toString());
            System.out.println(queue2.get().toString());
            System.out.println(queue3.get().toString());
            System.out.println(queue4.get().toString());
            System.out.println(queue5.get().toString());
//            System.out.println(immutablePairHystrixCollapser2.execute().getRight());
//            System.out.println(immutablePairHystrixCollapser3.execute().getRight());
//            System.out.println(immutablePairHystrixCollapser4.execute().getRight());
//            System.out.println(immutablePairHystrixCollapser5.execute().getRight());
//            Thread.sleep(1000);
//            System.out.println(immutablePairHystrixCollapser6.execute().getRight());
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    public static class ImmutablePairHystrixCollapser extends HystrixCollapser<List<ImmutablePair<Long, String>>, ImmutablePair<Long, String>, Long> {

        /**请求合并key**/
        private final Long key;
        private final ImmutablePairService pairService;

        public ImmutablePairHystrixCollapser(Long key, ImmutablePairService pairService) {
            super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("immutablePairHystrixCollapser")).
                    andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
            this.key = key;
            this.pairService = pairService;
        }

        @Override
        public Long getRequestArgument() {
            return key;
        }

        @Override
        protected HystrixCommand<List<ImmutablePair<Long, String>>> createCommand(Collection<CollapsedRequest<ImmutablePair<Long, String>, Long>> collapsedRequests) {
            return new BatchCommand(collapsedRequests, pairService);
        }

        @Override
        protected void mapResponseToRequests(List<ImmutablePair<Long, String>> batchResponse, Collection<CollapsedRequest<ImmutablePair<Long, String>, Long>> collapsedRequests) {
            //设置请求数据
            if (CollectionUtils.isEmpty(batchResponse)) {
                return;
            }
            Map<Long, ImmutablePair<Long, String>> map = new HashMap<>(batchResponse.size());
            for (ImmutablePair<Long, String> immutablePair : batchResponse) {
                map.put(immutablePair.getLeft(), immutablePair);
            }
            for (CollapsedRequest<ImmutablePair<Long, String>, Long> request : collapsedRequests) {
                request.setResponse(map.getOrDefault(request.getArgument(), null));
            }
        }

        private static class BatchCommand extends HystrixCommand<List<ImmutablePair<Long, String>>> {

            private final Collection<CollapsedRequest<ImmutablePair<Long, String>, Long>> collapsedRequests;
            private final ImmutablePairService pairService;

            protected BatchCommand(Collection<CollapsedRequest<ImmutablePair<Long, String>, Long>> collapsedRequests
                , ImmutablePairService pairService) {
                super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                        .andCommandKey(HystrixCommandKey.Factory.asKey("GetValueForKey")));
                this.collapsedRequests = collapsedRequests;
                this.pairService = pairService;
            }

            @Override
            protected List<ImmutablePair<Long, String>> run() throws Exception {
                List<Long> rowIds = new ArrayList<>(collapsedRequests.size());
                for (CollapsedRequest<ImmutablePair<Long, String>, Long> request : this.collapsedRequests) {
                    rowIds.add(request.getArgument());
                }
                System.out.println("--------合并Ids:" + rowIds.toString());
                //批量获取数据
                return pairService.queryForListByIds(rowIds);
            }

//            @Override
//            public Throwable getFailedExecutionException() {
//                //自定义异常
//                return super.getFailedExecutionException();
//            }
        }


    }

    public static class ImmutablePairService {

        public List<ImmutablePair<Long, String>> queryForListByIds(List<Long> ids) {
            if (CollectionUtils.isEmpty(ids)) {
                return new ArrayList<>();
            }
            List<ImmutablePair<Long, String>> list = new ArrayList<>(ids.size());
            for (Long id : ids) {
                list.add(new ImmutablePair<>(id, "ImmutablePair Test Id = " + id.toString()));
            }
            return list;
        }

    }

}
