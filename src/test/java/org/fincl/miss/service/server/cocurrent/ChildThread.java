package org.fincl.miss.service.server.cocurrent;

import java.util.List;

public class ChildThread implements Runnable {
    public static final InheritableThreadLocal<List> aa = new InheritableThreadLocal<List>();
    
    @Override
    public void run() {
        aa.set(ThreadLocalTest.threadLocal.get());
        List gg = aa.get();
        gg.add("2222");
        System.out.println(Thread.currentThread().getName() + ">>" + aa.get());
        
    }
    
}
