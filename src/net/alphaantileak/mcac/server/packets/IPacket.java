package net.alphaantileak.mcac.server.packets;

import io.netty.buffer.ByteBuf;

public interface IPacket {
    void read(ByteBuf buf);
    void write(ByteBuf buf);
}
