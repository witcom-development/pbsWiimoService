package org.fincl.miss.server.codec;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.CharsetConvertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CharsetConvertEncoder extends MessageToMessageEncoder<byte[]> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private Charset sourceCharset;
    private Charset targetCharset;
    
    public CharsetConvertEncoder(Charset sourceCharset, Charset targetCharset) {
        this.sourceCharset = sourceCharset;
        this.targetCharset = targetCharset;
    }
    
    @Override
    protected void encode(ChannelHandlerContext ctx, byte[] bOrginal, List<Object> out) throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("before encode [{}][{}][{}][{}]", sourceCharset, targetCharset, bOrginal.length, new String(bOrginal));
        }
        byte[] rMsg = null;
        try {
            rMsg = CharsetConvertUtil.convert(bOrginal, sourceCharset, targetCharset);
        }
        catch (IOException e) {
            throw new ServerException(ErrorConstant.CHANNEL_ENCODE_ERROR, "Characterset convert error [source charset:" + sourceCharset + ", target charset:" + targetCharset + "]", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("after encode [{}][{}][{}][{}]", sourceCharset, targetCharset, rMsg.length, new String(rMsg));
        }
        out.add(rMsg);
    }
    
}
