package net.alphaantileak.mcac.server.packets;

import io.netty.buffer.ByteBuf;
import net.alphaantileak.mcac.utils.CryptManager;
import net.alphaantileak.mcac.utils.ProtocolUtils;

import javax.crypto.SecretKey;
import java.security.PrivateKey;

public class PacketEncryptionKey implements IPacket {
    private byte[] encryptionKey;

    public PacketEncryptionKey() {}

    /**
     * Constructs a new instance which can be sent
     * @param encryptionKey The encrypted Encryption Key returned by the backend
     */
    public PacketEncryptionKey(byte[] encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    public SecretKey getEncryptionKey(PrivateKey decryptionKey) {
        return CryptManager.decryptSharedKey(decryptionKey, encryptionKey);
    }

    @Override
    public void read(ByteBuf buf) {
        this.encryptionKey = ProtocolUtils.readByteArray(buf);
    }

    @Override
    public void write(ByteBuf buf) {
        ProtocolUtils.writeByteArray(buf, encryptionKey);
    }
}
