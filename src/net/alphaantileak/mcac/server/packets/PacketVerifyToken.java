package net.alphaantileak.mcac.server.packets;

import io.netty.buffer.ByteBuf;
import net.alphaantileak.mcac.utils.ProtocolUtils;

public class PacketVerifyToken implements IPacket {
    private byte[] token;

    public PacketVerifyToken() {}

    public PacketVerifyToken(byte[] token) {
        this.token = token;
    }

    public byte[] getToken() {
        return token;
    }

    @Override
    public void read(ByteBuf buf) {
        token = new byte[buf.readableBytes()];
        buf.readBytes(token);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(token);
    }
}
