package org.fincl.miss.service.server.cocurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class ThreadLocalTest {
    
    static final ThreadLocal<List> userName = new ThreadLocal();
    static final ThreadLocal<List> threadLocal = new ThreadLocal<List>();
    ThreadPoolExecutor executorService = new ThreadPoolExecutor2(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());;
    
    @Test
    public void threadLocal() throws Exception {
        threadLocal.set(new ArrayList());
        List gg = threadLocal.get();
        gg.add("11111");
        
        ChildThread aa = new ChildThread();
        new Thread(aa).start();
        
        println(aa.aa);
        println(threadLocal);
        // new Thread(run).start();
        
    }
    
    @Test
    public void inheritableThreadLocal() {
        threadLocal.set(new ArrayList());
        List gg = threadLocal.get();
        gg.add("11111");
        for (int i = 0; i < 10; i++) {
            Runnable run = new Runnable() {
                @Override
                public void run() {
                    // inheritableThreadLocal.set(inheritableThreadLocal.get() + "gggg");
                    Runnable run2 = new Runnable() {
                        
                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            List gg = threadLocal.get();
                            synchronized (gg) {
                                gg.add("xxxx");
                            }
                            println(threadLocal);
                        }
                    };
                    
                    new Thread(run2).start();
                    List gg = threadLocal.get();
                    synchronized (gg) {
                        gg.add("2222");
                    }
                    println(threadLocal);
                }
            };
            new Thread(run).start();
        }
        List ggd = threadLocal.get();
        synchronized (gg) {
            ggd.add("33333");
        }
        println(threadLocal);
    }
    
    @Test
    public void pool() throws Exception {
        
        List gg = threadLocal.get();
        gg.add("11111");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                println(threadLocal);
            }
        });
        List gga = threadLocal.get();
        gga.add("11111");
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                println(threadLocal);
            }
        });
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                println(threadLocal);
            }
        });
        
        println(threadLocal);
    }
    
    private void println(ThreadLocal<List> threadLocal) {
        System.out.println(Thread.currentThread().getName() + ":" + threadLocal.get());
    }
    
    public static class ThreadPoolExecutor2 extends ThreadPoolExecutor {
        
        public ThreadPoolExecutor2(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
        }
        
        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            threadLocal.remove();
            // inheritableThreadLocal.set("abc");
            System.out.println("t = " + Thread.currentThread().getName() + "//" + t);
        }
    }
}