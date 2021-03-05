package org.fincl.miss.server.codec.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import org.apache.commons.lang3.StringUtils;

public class LengthHeaderFrameEncoder extends MessageToByteEncoder<byte[]> {
    
    private int LEGNTH_FIELD_SIZE = 6;
    
    private int contentLength = 0; // include length field size
    
    public LengthHeaderFrameEncoder() {
        
    }
    
    public LengthHeaderFrameEncoder(final int lengthFieldSize) {
        this.LEGNTH_FIELD_SIZE = lengthFieldSize;
    }
    
    @Override
    protected void encode(final ChannelHandlerContext paramChannelHandlerContext, final byte[] message, final ByteBuf out) throws Exception {
        String messageLength = String.valueOf(message.length); // exclude length field size
        messageLength = StringUtils.leftPad(messageLength, this.LEGNTH_FIELD_SIZE, '0'); // left 0 padding
        this.contentLength = messageLength.getBytes().length;
        out.writeBytes(messageLength.getBytes());
        this.contentLength += message.length;
        out.writeBytes(message);
    }
}