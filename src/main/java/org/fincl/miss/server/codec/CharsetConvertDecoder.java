package org.fincl.miss.server.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.CharsetConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharsetConvertDecoder extends ByteToMessageDecoder {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private Charset sourceCharset;
    private Charset targetCharset;
    
    public CharsetConvertDecoder(Charset sourceCharset, Charset targetCharset) {
        this.sourceCharset = sourceCharset;
        this.targetCharset = targetCharset;
    }
    
    /**
     * TODO PREFIX의 전문길이 값을 변환된 Chatset 기준으로 변경하기
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        
        byte[] bOriginal = new byte[in.readableBytes()];
        in.readBytes(bOriginal);
        if (logger.isDebugEnabled()) {
            logger.debug("before decode [{}][{}][{}][{}]", sourceCharset, targetCharset, bOriginal.length, new String(bOriginal));
        }
        byte[] bResult = null;
        try {
            bResult = CharsetConvertUtil.convert(bOriginal, sourceCharset, targetCharset);
        }
        catch (IOException e) {
            throw new ServerException(ErrorConstant.CHANNEL_DECODE_ERROR, "Characterset convert error [source charset:" + sourceCharset + ", target charset:" + targetCharset + "]", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("after decode [{}][{}][{}][{}]", sourceCharset, targetCharset, bResult.length, new String(bResult));
        }
        ByteBuf bf = Unpooled.wrappedBuffer(bResult);
        out.add(bf);
    }
    
}
