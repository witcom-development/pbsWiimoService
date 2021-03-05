package org.fincl.miss.service.server.reflection;

import org.fincl.miss.server.reflection.DynamicAccessor;
import org.fincl.miss.server.reflection.DynamicAccessorFactory;
import org.junit.Assert;
import org.junit.Test;

public class DynamicAccessorTest {
    
    static class User {
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public Double getAge() {
            return age;
        }
        
        public void setAge(Double age) {
            this.age = age;
        }
        
        private String email;
        private Double age;
        
        public User() {
        }
        
        public void includeAge(int delta) {
            age += delta;
        }
    }
    
    @Test
    public void dynamicInstancing() {
        DynamicAccessor<User> userAccessor = DynamicAccessorFactory.create(User.class);
        Assert.assertNotNull(userAccessor);
        
        Object user = userAccessor.newInstance();
        
        userAccessor.setProperty(user, "email", "sunghyouk.bae@gmail.com");
        userAccessor.setProperty(user, "age", 110.0);
        
        Assert.assertEquals("sunghyouk.bae@gmail.com", userAccessor.getProperty(user, "email"));
        Assert.assertEquals(110.0, userAccessor.getProperty(user, "age"));
    }
}