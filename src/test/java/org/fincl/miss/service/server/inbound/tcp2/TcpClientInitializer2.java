package org.fincl.miss.service.server.inbound.tcp2;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

import org.fincl.miss.server.channel.inbound.InBoundChannelImpl;
import org.fincl.miss.server.codec.tcp.StxEtxFrameDecoder;
import org.fincl.miss.server.codec.tcp.StxEtxFrameEncoder;
import org.fincl.miss.server.util.EnumCode.ChannelStatus;
import org.fincl.miss.server.util.EnumCode.DataRawType;

import com.dkitec.cfood.core.CfoodException;

/**
 * Creates a newly configured {@link ChannelPipeline} for a new channel.
 */
public class TcpClientInitializer2 extends ChannelInitializer<SocketChannel> {
    
    private final SslContext sslCtx;
    
    private final String host;
    
    private final String port;
    
    public TcpClientInitializer2(String host, String port, SslContext sslCtx) {
        this.host = host;
        this.port = port;
        this.sslCtx = sslCtx;
    }
    
    @Override
    public void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), this.host, Integer.parseInt(this.port)));
        }
        
        // int lengthFieldSize, int stxFieldSize, int etxFieldSize, String stxHexString, String etxHexString
        // Add the text line codec combination first,
        InBoundChannelImpl extChannel = new InBoundChannelImpl(null, null) {
            
            @Override
            public boolean startup() throws CfoodException {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public boolean shutdown() throws CfoodException {
                // TODO Auto-generated method stub
                return false;
            }
            
            @Override
            public byte[] sendAndReceive(String clientId, byte[] message) {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public ChannelStatus getStatus() throws CfoodException {
                // TODO Auto-generated method stub
                return null;
            }
            
            @Override
            public void send(String clientId, byte[] message) {
                // TODO Auto-generated method stub
                
            }
        };
        extChannel.setTxRawDataTypeEnum(DataRawType.BYTE);
        extChannel.setHeaderLengthSize(2);
        extChannel.setEtxSize(1);
        extChannel.setStxSize(2);
        extChannel.setStxHex("E000");
        extChannel.setEtxHex("E1");
        pipeline.addLast(new StxEtxFrameDecoder(extChannel));
        pipeline.addLast(new StxEtxFrameEncoder(extChannel));
        // pipeline.addLast(new ByteArrayDecoder());
        // pipeline.addLast(new ByteArrayEncoder());
        
        // and then business logic.
        pipeline.addLast(new TcpClientHandler2());
    }
}