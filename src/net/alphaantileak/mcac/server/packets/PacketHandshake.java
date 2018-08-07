package net.alphaantileak.mcac.server.packets;

import io.netty.buffer.ByteBuf;
import net.alphaantileak.mcac.utils.ProtocolUtils;

public class PacketHandshake implements IPacket {
    private static final String HANDSHAKE_VALUE = "AlphaAntiLeakMineCraftAntiCheat_V1.0";

    public PacketHandshake() {}

    public PacketHandshake(String username) {
        this.handshake = HANDSHAKE_VALUE;
        this.username = username;
    }

    private String handshake;
    private String username;

    public boolean isValid() {
        return HANDSHAKE_VALUE.equals(handshake);
    }

    public String getUsername() {
        return username;
    }

    @Override
    public void read(ByteBuf buf) {
        handshake = ProtocolUtils.readString(buf);
        username = ProtocolUtils.readString(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ProtocolUtils.writeString(buf, handshake);
        ProtocolUtils.writeString(buf, username);
    }
}
