package org.fincl.miss.server.codec.tcp;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.fincl.miss.server.channel.BoundChannel;
import org.fincl.miss.server.exeption.ErrorConstant;
import org.fincl.miss.server.exeption.ServerException;
import org.fincl.miss.server.util.EnumCode.DataRawType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StxEtxFrameDecoder extends ReplayingDecoder<StxEtxFrameDecoder.State> {
    
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    
    protected enum State {
        READY, READ_STX, READ_PAYLOAD_LENGTH, READ_PAYLOAD, READ_ETX
    }
    
    private int payloadLength = 0;
    
    private ByteBuf framePayloadLength;
    private ByteBuf frameData;
    
    //
    private int lengthFieldSize = 0;
    private int stxFieldSize = 0;
    private int etxFieldSize = 0;
    private String stxHexString = null;
    private String etxHexString = null;
    
    private BoundChannel extChannel;
    
    public StxEtxFrameDecoder(BoundChannel extChannel) {
        super(State.READY);
        
        this.extChannel = extChannel;
        
        this.lengthFieldSize = extChannel.getHeaderLengthSize();
        this.stxFieldSize = extChannel.getStxSize();
        this.etxFieldSize = extChannel.getEtxSize();
        this.stxHexString = extChannel.getStxHex();
        this.etxHexString = extChannel.getEtxHex();
        logger.debug("lengthField: {} , stx :{}, etx :{}. stxHex :{}, etxHex:{} ",this.lengthFieldSize, this.stxFieldSize, this.etxFieldSize, this.stxHexString, this.etxHexString);
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
    
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        logger.debug("Decode State ==> {}", state());
        switch (state()) {
            case READY:
                if (isEnableStx()) {
                    state(State.READ_STX);
                }
                else {
                	logger.debug("No Stx...");
                    state(State.READ_PAYLOAD_LENGTH);
                }
                checkpoint(state());
                break;
            case READ_STX:
                readStx(in);
                state(State.READ_PAYLOAD_LENGTH);
                checkpoint(state());
                break;
            case READ_PAYLOAD_LENGTH:
                readPayloadLength(in);
                state(State.READ_PAYLOAD);
                checkpoint(state());
                break;
            case READ_PAYLOAD:
                readPayload(in);
                if (isEnableEtx()) {
                    state(State.READ_ETX);
                    checkpoint(state());
                    
                    readEtx(in);
                    state(State.READY);
                    checkpoint(state());
                }
                else {
                    state(State.READY);
                    checkpoint(state());
                }
                
                ByteBuf nFrame = Unpooled.wrappedBuffer(framePayloadLength.array(), frameData.array());
                out.add(nFrame);
                
                break;
            
            default:
                throw new IllegalStateException("Unexpected State");
        }
        
    }
    
    private void readStx(ByteBuf buffer) {
        ByteBuf stxFrame = buffer.readBytes(stxFieldSize);
        // String hStx = DatatypeConverter.printHexBinary(stxFrame.array());
    }
    
    private void readEtx(ByteBuf buffer) {
        ByteBuf etxFrame = buffer.readBytes(etxFieldSize);
        // String hExt = DatatypeConverter.printHexBinary(etxFrame.array());
    }
    
    private void readPayloadLength(ByteBuf buffer) {
    	logger.debug("read lengthFieldSize : {}",lengthFieldSize);
        framePayloadLength = buffer.readBytes(lengthFieldSize);
        byte[] bFramePayloadLength = framePayloadLength.array();
        logger.debug("FramePayloadLength Value [{}]", new String(bFramePayloadLength));
        if (extChannel.getTxRawDataTypeEnum() == DataRawType.STRING) {
            String sLength = new String(bFramePayloadLength);
            try {
                this.payloadLength = Integer.parseInt(sLength);
            }
            catch (NumberFormatException e) {
                throw new ServerException(ErrorConstant.CHANNEL_DECODE_ERROR, "PayloadLength(String) parse error [" + sLength + "]", e);
            }
            logger.debug("String Length [{}]", this.payloadLength);
        }
        else if (extChannel.getTxRawDataTypeEnum() == DataRawType.BYTE) {
            String hLength = null;
            try {
                hLength = DatatypeConverter.printHexBinary(bFramePayloadLength);
                this.payloadLength = Integer.decode("0x" + hLength);
                logger.debug("Byte Length [{}]", this.payloadLength);
            }
            catch (NumberFormatException e) {
                throw new ServerException(ErrorConstant.CHANNEL_DECODE_ERROR, "PayloadLength(Byte) parse error [" + hLength + "]", e);
            }
            catch (IllegalArgumentException e) {
                throw new ServerException(ErrorConstant.CHANNEL_DECODE_ERROR, "PayloadLength(Byte) parse error [" + hLength + "]", e);
            }
            
        }
        
    }
    
    private void readPayload(ByteBuf buffer) {
        frameData = buffer.readBytes(this.payloadLength);
    }
    
}
