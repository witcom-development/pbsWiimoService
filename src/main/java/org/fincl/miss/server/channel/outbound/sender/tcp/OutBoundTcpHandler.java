package org.fincl.miss.server.channel.outbound.sender.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.fincl.miss.server.annotation.EnableTraceLogging;
import org.fincl.miss.server.channel.outbound.sender.OutBoundSender;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.SingleYn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OutBoundTcpHandler extends SimpleChannelInboundHandler<ByteBuf> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private OutBoundSender outBoundTcpSender;
    
    private SingleYn singleYn = SingleYn.NO;
    
    private ChannelFuture future;
    
    private final BlockingQueue<byte[]> responseQueue = new LinkedBlockingQueue<byte[]>();
    
    public OutBoundTcpHandler(OutBoundSender outBoundTcpSender) {
        super(false);
        this.outBoundTcpSender = outBoundTcpSender;
        this.singleYn = SingleYn.getEnum(outBoundTcpSender.getOutBoundChannel().getSingleYn());
        
    }
    
    @EnableTraceLogging
    public byte[] sendMessageAndReceive(Channel channel, byte[] bMessage) {
        System.out.println("message send start ====" + channel);
        future = channel.writeAndFlush(bMessage);
        boolean interrupted = false;
        byte[] bRes;
        for (;;) {
            try {
                bRes = responseQueue.take();
                break;
            }
            catch (InterruptedException e) {
                interrupted = true;
            }
        }
        
        if (interrupted) {
            Thread.currentThread().interrupt();
        }
        System.out.println("message send end ====" + channel);
        return bRes;
    }
    
    @EnableTraceLogging
    public void sendMessage(Channel channel, byte[] bMessage) {
        future = channel.writeAndFlush(bMessage);
        if (future != null) {
            try {
                future.sync();
            }
            catch (InterruptedException e) {
                if (logger.isDebugEnabled()) {
                    e.printStackTrace();
                }
                throw new ServerException(ErrorConstant.CHANNEL_OUTBOUND_SENDER_ERROR, e, bMessage);
            }
        }
    }
    
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        outBoundTcpSender.getOutBoundChannel().getAllChannels().put(System.identityHashCode(ctx.channel()) + "", ctx);
    }
    
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        outBoundTcpSender.getOutBoundChannel().getAllChannels().remove(ctx.channel().toString());
        logger.info("OutBoundChannel [ChannelID:{}] {} inactive.", outBoundTcpSender.getOutBoundChannel().getChannelId(), ctx.channel());
        
        // if (outBoundTcpSender instanceof OutBoundNoneSingleTcpSender) {
        // OutBoundNoneSingleTcpSender outBoundNoneSinleTcpsender = (OutBoundNoneSingleTcpSender) outBoundTcpSender;
        // outBoundTcpSender.startup();
        // // outBoundNoneSinleTcpsender.getGroup().schedule(new Runnable() {
        // // @Override
        // // public void run() {
        // // logger.info("OutBoundChannel channelInactive [ChannelID:{}] reconnect. {}.", outBoundTcpSender.getOutBoundChannel().getChannelId(), ctx.channel().closeFuture().cause());
        // // outBoundTcpSender.startup();
        // // }
        // // }, 5L, TimeUnit.SECONDS);
        // }
        
    }
    
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf bfMessage) throws Exception {
        
        byte[] bMessage = new byte[bfMessage.readableBytes()];
        bfMessage.readBytes(bMessage);
        logger.debug("[{}] outBound received utf-8 [{}][{}]", outBoundTcpSender.getOutBoundChannel().getChannelId(), bMessage.length, new String(bMessage, "UTF-8"));
        logger.debug("[{}] outBound received euc-kr [{}][{}]", outBoundTcpSender.getOutBoundChannel().getChannelId(), bMessage.length, new String(bMessage, "EUC-KR"));
        responseQueue.add(bMessage);
        
        // Single 모드일 경우 응답을 받고 종료한다.
        logger.debug("singleYn is {}", singleYn);
        if (singleYn == SingleYn.YES) {
            logger.debug("outBound will close");
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
}