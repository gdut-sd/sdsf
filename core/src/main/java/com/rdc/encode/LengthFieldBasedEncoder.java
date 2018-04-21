package com.rdc.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author SD
 */
public class LengthFieldBasedEncoder extends MessageToByteEncoder<ByteBuf> {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws Exception {
        byteBuf2.writeInt(byteBuf.readableBytes());
        byteBuf2.writeBytes(byteBuf);
    }
}
