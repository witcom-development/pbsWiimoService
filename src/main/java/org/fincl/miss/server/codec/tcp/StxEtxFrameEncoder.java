package org.fincl.miss.server.codec.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.lang3.StringUtils;
import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.DataRawType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StxEtxFrameEncoder extends MessageToByteEncoder<byte[]> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    //
    private int lengthFieldSize = 0;
    private int stxFieldSize = 0;
    private int etxFieldSize = 0;
    private String stxHexString = null;
    private String etxHexString = null;
    
    private BoundChannel extChannel;
    
    public StxEtxFrameEncoder(BoundChannel extChannel) {
        
        this.extChannel = extChannel;
        
        this.lengthFieldSize = extChannel.getHeaderLengthSize();
        this.stxFieldSize = extChannel.getStxSize();
        this.etxFieldSize = extChannel.getEtxSize();
        this.stxHexString = extChannel.getStxHex();
        this.etxHexString = extChannel.getEtxHex();
    }
    
    @Override
    protected void encode(final ChannelHandlerContext paramChannelHandlerContext, final byte[] message, final ByteBuf out) throws Exception {
        if (isEnableStx()) {
            try {
                out.writeBytes(DatatypeConverter.parseHexBinary(this.stxHexString));
                logger.debug("write stx [{}]", this.stxHexString);
            }
            catch (IllegalArgumentException e) {
                throw new ServerException(ErrorConstant.CHANNEL_ENCODE_ERROR, "Byte Hex parse error [" + this.stxHexString + "]", e);
            }
        }
        if (extChannel.getTxRawDataTypeEnum() == DataRawType.STRING) {
            String messageLength = String.valueOf(message.length);
            messageLength = StringUtils.leftPad(messageLength, this.lengthFieldSize, '0');
            logger.debug("String encode messageLength [{}]", messageLength);
            out.writeBytes(messageLength.getBytes());
        }
        else if (extChannel.getTxRawDataTypeEnum() == DataRawType.BYTE) {
            try {
                byte[] bLength = DatatypeConverter.parseHexBinary(StringUtils.leftPad(Integer.toHexString(message.length), this.lengthFieldSize * 2, '0'));
                out.writeBytes(bLength);
                logger.debug("String encode messageLength [{}]", bLength);
            }
            catch (IllegalArgumentException e) {
                throw new ServerException(ErrorConstant.CHANNEL_ENCODE_ERROR, "Byte Hex parse error [" + this.etxHexString + "]", e);
            }
        }
        out.writeBytes(message);
        if (isEnableEtx()) {
            try {
                out.writeBytes(DatatypeConverter.parseHexBinary(this.etxHexString));
            }
            catch (IllegalArgumentException e) {
                throw new ServerException(ErrorConstant.CHANNEL_ENCODE_ERROR, "Byte Hex parse error [" + this.etxHexString + "]", e);
            }
            logger.debug("write etx [{}]", this.etxHexString);
        }
    }
    
    private boolean isEnableStx() {
        if (this.stxFieldSize > 0 && this.stxHexString != null) {
            return true;
        }
        else {
            return false;
        }
    }
    
    private boolean isEnableEtx() {
        if (this.etxFieldSize > 0 && this.etxHexString != null) {
            return true;
        }
        else {
            return false;
        }
    }
}