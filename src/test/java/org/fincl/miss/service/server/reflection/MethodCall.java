package org.fincl.miss.service.server.reflection;

import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.Method;

import com.esotericsoftware.reflectasm.MethodAccess;

public class MethodCall {
    
    public static void main(String[] args) throws Exception {
        MethodCall test = new MethodCall();
        test.run(1000000);
        test.run(10000000);
        test.run(50000000);
        test.run(100000000);
        test.run(500000000);
    }
    
    public void action() {/* DO NOTHING */
    }
    
    void run(int v) throws Exception {
        System.out.println("---- 반복횟수 (" + v + ") ----");
        
        int i = 0;
        
        start(); // TEST #1
        for (i = 0; i < v; i++)
            action();
        end("Calling a method directly");
        
        Method action = reflectMethod();
        start(); // TEST #2
        for (i = 0; i < v; i++)
            action.invoke(this);
        end("Calling a method through reflection");
        
        MethodAccess accessor = MethodAccess.get(this.getClass());
        start(); // TEST #3
        for (i = 0; i < v; i++)
            accessor.invoke(this, "action");
        end("Calling a method through ReflectASM - name accessing");
        
        int methodIndex = accessor.getIndex("action");
        start(); // TEST #4
        for (i = 0; i < v; i++)
            accessor.invoke(this, methodIndex);
        end("Calling a method through ReflectASM - index accessing");
    }
    
    Method reflectMethod() throws Exception {
        MethodDescriptor[] mds = Introspector.getBeanInfo(getClass()).getMethodDescriptors();
        
        for (MethodDescriptor md : mds)
            if ("action".equals(md.getName())) return md.getMethod();
        
        throw new Exception("Where is the action method?" + mds);
    }
    
    long start;
    
    void start() {
        start = System.currentTimeMillis();
    }
    
    void end(String msg) {
        System.out.println(msg + ":\t" + (System.currentTimeMillis() - start) + "ms");
    }
}