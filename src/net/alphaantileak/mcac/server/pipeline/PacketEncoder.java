package net.alphaantileak.mcac.server.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.alphaantileak.mcac.server.Attributes;
import net.alphaantileak.mcac.server.data.HandlerSide;
import net.alphaantileak.mcac.server.packets.IPacket;
import net.alphaantileak.mcac.server.packets.PacketRegistry;
import net.alphaantileak.mcac.utils.ProtocolUtils;

/**
 * @author notaviable
 * @version 1.0
 */
public class PacketEncoder extends MessageToByteEncoder<IPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IPacket msg, ByteBuf out) throws Exception {
        ProtocolUtils.writeVarInt(out, PacketRegistry.getId(msg));
        msg.write(out);
    }
}
