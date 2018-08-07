package net.alphaantileak.mcac.server.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.alphaantileak.mcac.utils.ProtocolUtils;

/**
 * @author notaviable
 * @version 1.0
 */
public class PacketPrepender extends MessageToByteEncoder<ByteBuf> {

    @Override
    public void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {
        out.writeInt(msg.readableBytes());
        out.writeBytes(msg, msg.readerIndex(), msg.readableBytes());
    }

}
