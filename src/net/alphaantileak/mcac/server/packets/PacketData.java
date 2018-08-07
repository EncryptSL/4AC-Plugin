package net.alphaantileak.mcac.server.packets;

import io.netty.buffer.ByteBuf;

public class PacketData implements IPacket {
    private byte[] data;

    public PacketData() {}
    public PacketData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    @Override
    public void read(ByteBuf buf) {
        data = new byte[buf.readableBytes()];
        buf.readBytes(data);
    }

    @Override
    public void write(ByteBuf buf) {
        buf.writeBytes(data);
    }
}
