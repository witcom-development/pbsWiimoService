package org.fincl.miss.server.channel.outbound.pool;

import org.apache.commons.pool.impl.GenericObjectPool;
import org.fincl.miss.core.context.ApplicationContextSupport;
import org.fincl.miss.server.channel.outbound.OutBoundChannelImpl;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Scope("prototype")
@Component
public class OutBoundChannelSenderPool extends GenericObjectPool<OutBoundSender> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private OutBoundChannelImpl outBoundChannel;
    
    public OutBoundChannelSenderPool() {
        
    }
    
    public OutBoundChannelSenderPool(OutBoundChannelImpl outBoundChannel, @SuppressWarnings("rawtypes") Class outBoundSenderClazz, int defaultPoolcount) {
        this(outBoundChannel, outBoundSenderClazz, defaultPoolcount, defaultPoolcount, defaultPoolcount);
    }
    
    @SuppressWarnings("unchecked")
    public OutBoundChannelSenderPool(OutBoundChannelImpl outBoundChannel, @SuppressWarnings("rawtypes") Class outBoundSenderClazz, int maxActive, int maxIdle, int minIdle) {
        // int maxActive = 1;
        // byte whenExhaustedAction = 2;
        // long maxWait = 1000;
        // int maxIdle = 2;
        // int minIdle = 1;
        // boolean testOnBorrow = true;
        // boolean testOnReturn = true;
        // long timeBetweenEvctionRunsMillis = 600000;
        // int numTestsPerEvictionRun = 5;
        // long minEvictableIdleTimeMillis = 3600000;
        // boolean testWhileIdle = true;
        
        // maxActive 커넥션 풀이 제공할 최대 커넥션 개수
        // whenExhaustedAction 커넥션 풀에서 가져올 수 있는 커넥션이 없을 때 어떻게 동작할지를 지정한다.
        // maxWait 사용되지 않고 풀에 저장될 수 있는 최대 커넥션 개수. 음수일 경우 제한이 없다.
        // maxIdle 사용되지 않고 풀에 저장될 수 있는 최소 커넥션 개수.
        // testOnBorrow true일 경우 커넥션 풀에서 커넥션을 가져올 때 커넥션이 유효한지의 여부를 검사한다.
        // testOnReturn true일 경우 커넥션 풀에 커넥션을 반환할 때 커넥션이 유효한지의 여부를 검사한다.
        // timeBetweenEvctionRunsMillis 사용되지 않은 커넥션을 추출하는 쓰레드의 실행 주기를 지정한다.
        // numTestsPerEvictionRun 사용되지 않는 커넥션을 몇 개 검사할지 지정한다.
        // minEvictableIdleTimeMillis 사용되지 않는 커넥션을 추출할 때 이 속성에서 지정한 시간 이상 비활성화 상태인 커넥션만 추출한다.
        // testWhileIdle true일 경우 비활성화 커넥션을 추출할 때 커넥션이 유효한지의 여부를 검사해서 유효하지 않은 커넥션은 풀에서 제거한다.
        
        // super(new OutBoundChannelSenderFactory(outBoundChannel, clazz));
        super(ApplicationContextSupport.getBean(OutBoundChannelSenderFactory.class, new Object[] { outBoundChannel, outBoundSenderClazz }));
        setMaxActive(maxActive);
        setMaxIdle(maxIdle);
        setMinIdle(minIdle);
        
        // setMaxActive(1);
        // setMaxIdle(1);
        // setMinIdle(1);
        
        this.outBoundChannel = outBoundChannel;
    }
    
    public boolean shutdown() {
        logger.debug("try shutdown start {}", outBoundChannel);
        try {
            logger.debug("close start");
            close();
            logger.debug("close end");
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logger.debug("try shutdown end {}", outBoundChannel);
        
        return true;
    }
    
    public boolean startup() {
        
        return true;
    }
    
    public ChannelStatus getStatus() {
        ChannelStatus channelStatus = ChannelStatus.DEAD;
        if (!isClosed()) {
            channelStatus = ChannelStatus.ALIVE;
        }
        return channelStatus;
    }
    
}
