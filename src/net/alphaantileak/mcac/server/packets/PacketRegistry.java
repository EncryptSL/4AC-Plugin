package net.alphaantileak.mcac.server.packets;

import net.alphaantileak.mcac.server.data.HandlerSide;
import net.alphaantileak.mcac.server.data.Stage;

import java.util.HashMap;
import java.util.Map;

public class PacketRegistry {
    private static class PacketKey {
        private Stage stage;
        private HandlerSide side;
        private int id;

        PacketKey(HandlerSide side, Stage stage, int id) {
            this.side = side;
            this.stage = stage;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PacketKey packetKey = (PacketKey) o;

            if (id != packetKey.id) return false;
            if (stage != packetKey.stage) return false;
            return side == packetKey.side;
        }

        @Override
        public int hashCode() {
            int result = (stage != null ? stage.hashCode() : 0);
            result = 31 * result + (side != null ? side.hashCode() : 0);
            result = 31 * result + id;
            return result;
        }
    }

    private static Map<PacketKey, Class<? extends IPacket>> PACKET_REGISTRY = new HashMap<>();
    private static Map<Class<? extends IPacket>, Integer> PACKET_ID_REGISTRY = new HashMap<>();

    public static int getId(IPacket msg) {
        return PACKET_ID_REGISTRY.get(msg.getClass());
    }

    public static IPacket getPacket(int packetId, Stage stage, HandlerSide handler) {
        try {
            return PACKET_REGISTRY.get(new PacketKey(handler, stage, packetId)).newInstance();
        } catch (NullPointerException e) {
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void registerPacket(HandlerSide side, Stage stage, Class<? extends IPacket> clazz, int id) {
        PACKET_ID_REGISTRY.put(clazz, id);
        PACKET_REGISTRY.put(new PacketKey(side, stage, id), clazz);
    }

    static {
        registerPacket(HandlerSide.SERVER, Stage.HANDSHAKE, PacketHandshake.class, 0);
        registerPacket(HandlerSide.CLIENT, Stage.VERIFICATION, PacketVerifyToken.class, 0);
        registerPacket(HandlerSide.SERVER, Stage.VERIFICATION, PacketEncryptionKey.class, 0);
        registerPacket(HandlerSide.CLIENT, Stage.VERIFICATION, PacketEncryptionEnabled.class, 1);
        registerPacket(HandlerSide.CLIENT, Stage.CONNECTED, PacketData.class, 0);
        registerPacket(HandlerSide.SERVER, Stage.CONNECTED, PacketData.class, 0);
    }
}
