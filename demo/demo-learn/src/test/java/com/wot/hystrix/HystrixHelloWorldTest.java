package com.wot.hystrix;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Assert;
import org.junit.Test;

/**
 * Hystrix尝试
 */
public class HystrixHelloWorldTest {

    @Test
    public void testSynchronous() {
        String world = new CommandHelloWorld("World").execute();
        String world2 = new CommandHelloWorld("Bob").execute();
        Assert.assertTrue("Hello World!".equals(world));
        Assert.assertTrue("Hello Bob!".equals(world2));
        System.out.println(world + "\n" + world2);
    }

    public static class CommandHelloWorld extends HystrixCommand<String> {

        private final String name;
        protected CommandHelloWorld(String name) {
            super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
            this.name = name;
        }

        @Override
        protected String run() throws Exception {
            return "Hello " + name + "!";
        }
    }

}
