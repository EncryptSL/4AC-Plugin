package net.alphaantileak.mcac.server.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import net.alphaantileak.mcac.server.Attributes;
import net.alphaantileak.mcac.server.packets.IPacket;
import net.alphaantileak.mcac.server.packets.PacketRegistry;
import net.alphaantileak.mcac.utils.ProtocolUtils;

import java.util.List;

/**
 * @author notaviable
 * @version 1.0
 */
public class PacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() > 0) {
            int id = ProtocolUtils.readVarInt(in);
            IPacket packet = PacketRegistry.getPacket(id, ctx.channel().attr(Attributes.PROTOCOL_STAGE).get(), ctx.channel().attr(Attributes.HANDLER_SIDE).get());

            if (packet == null) {
                throw new CorruptedFrameException("Invalid Packet Received");
            }

            packet.read(in);
            out.add(packet);
        }
    }
}