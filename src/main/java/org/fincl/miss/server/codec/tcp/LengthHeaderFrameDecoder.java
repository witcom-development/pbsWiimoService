package org.fincl.miss.server.codec.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class LengthHeaderFrameDecoder extends ReplayingDecoder<Void> {
    
    private int LEGNTH_FIELD_SIZE = 6; // default header field length
    
    private int contentLength = 0; // total telegram length int value
    
    private boolean readLength = false;
    
    private ByteBuf frameLength;
    
    public LengthHeaderFrameDecoder() {
        
    }
    
    public LengthHeaderFrameDecoder(final int lengthFieldSize) {
        this.LEGNTH_FIELD_SIZE = lengthFieldSize;
    }
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        
        if (!readLength) {
            frameLength = in.readBytes(this.LEGNTH_FIELD_SIZE);
            this.contentLength = Integer.parseInt(new String(frameLength.array()));
            // length = in.readInt();
            readLength = true;
            checkpoint();
        }
        
        if (readLength) {
            ByteBuf frameData = in.readBytes(this.contentLength);
            readLength = false;
            checkpoint();
            // byte[] combined = new byte[frameLength.array().length + frame.array().length];
            // System.arraycopy(frameLength.array(), 0, combined, 0, frameLength.array().length);
            // System.arraycopy(frame.array(), 0, combined, frameLength.array().length, frame.array().length);
            // out.add(frameLength);
            ByteBuf nFrame = Unpooled.wrappedBuffer(frameLength.array(), frameData.array());
            out.add(nFrame);
            
            ReferenceCountUtil.release(frameLength);
            ReferenceCountUtil.release(frameData);
            
        }
        
        // ByteBuf frameLength = in.readBytes(this.LEGNTH_FIELD_SIZE);
        // // byte[] aa = frameLength.array();
        // // System.out.println(">>>>1111 [" + new String(aa) + "]");
        // // out.add(frame);
        //
        // this.contentLength = Integer.parseInt(new String(frameLength.array()));
        //
        // // ByteBuf frameContent = in.readBytes(this.contentLength);
        // // byte[] aaa = frame2.array();
        // // System.out.println(">>>>222 [" + new String(aaa) + "]");
        //
        // // byte[] combined = new byte[aa.length + aaa.length];
        //
        // // System.arraycopy(aa, 0, combined, 0, aa.length);
        // // System.arraycopy(aaa, 0, combined, aa.length, aaa.length);
        //
        // // System.out.println("all]]" + new String(combined));
        //
        // in.resetReaderIndex();
        //
        // ByteBuf frameContent = in.readBytes(this.contentLength + this.LEGNTH_FIELD_SIZE);
        //
        // out.add(frameContent);
        
    }
}