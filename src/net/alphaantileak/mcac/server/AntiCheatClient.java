package net.alphaantileak.mcac.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import net.alphaantileak.mcac.server.data.HandlerSide;
import net.alphaantileak.mcac.server.data.MinecraftAuthStartRequest;
import net.alphaantileak.mcac.server.data.Stage;
import net.alphaantileak.mcac.server.packets.*;
import net.alphaantileak.mcac.server.pipeline.NettyEncryptingDecoder;
import net.alphaantileak.mcac.server.pipeline.NettyEncryptingEncoder;
import net.alphaantileak.mcac.utils.CryptManager;
import net.alphaantileak.mcac.utils.HttpAPI;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AntiCheatClient extends ChannelInboundHandlerAdapter implements IProxyHandler {

    private final AntiCheatServer server;
    private ChannelHandlerContext ctx;
    private String username;
    private AntiCheatProxy proxy;

    public AntiCheatClient(AntiCheatServer server) {
        this.server = server;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;

        ctx.channel().attr(Attributes.PROTOCOL_STAGE).set(Stage.HANDSHAKE);
        ctx.channel().attr(Attributes.HANDLER_SIDE).set(HandlerSide.SERVER);

        server.clients.add(this);
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (proxy != null) proxy.disconnect();

        server.clients.remove(this);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            //System.out.println(msg.getClass().getName());
            if (msg instanceof PacketHandshake) {
                if (!((PacketHandshake) msg).isValid()) {
                    ctx.disconnect();
                    return;
                }
                username = ((PacketHandshake) msg).getUsername();
                ctx.channel().attr(Attributes.PROTOCOL_STAGE).set(Stage.VERIFICATION);
                byte[] verifyToken = Base64.getDecoder().decode(HttpAPI.startAuth(new MinecraftAuthStartRequest(
                        username,
                        server.getKeyPair().getPublic()
                )).verifyToken);
                ctx.writeAndFlush(new PacketVerifyToken(verifyToken));
            } else if (msg instanceof PacketEncryptionKey) {
                SecretKey enc = ((PacketEncryptionKey) msg).getEncryptionKey(server.getKeyPair().getPrivate());

                NettyEncryptingEncoder nettyEnc = new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(Cipher.ENCRYPT_MODE, enc));
                NettyEncryptingDecoder nettyDec = new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(Cipher.DECRYPT_MODE, enc));

                ctx.pipeline().addBefore("packet_splitter", "mcac_decryptor", nettyDec);
                ctx.pipeline().addBefore("packet_prepender", "mcac_encryptor", nettyEnc);

                ctx.channel().writeAndFlush(new PacketEncryptionEnabled());

                ctx.channel().attr(Attributes.PROTOCOL_STAGE).set(Stage.CONNECTED);

                proxy = server.connectProxy("127.0.0.1", server.getPort() + 1, this);
            } else if (msg instanceof PacketData) {
                proxy.send(((PacketData) msg).getData());
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void onRecv(ByteBuf data) {
        byte[] buf = new byte[data.readableBytes()];
        data.readBytes(buf);
        ctx.writeAndFlush(new PacketData(buf));
    }

    @Override
    public void onConnect() {
    }

    @Override
    public void onDisconnect() {
        ctx.disconnect();
    }

    public AntiCheatProxy getProxy() {
        return proxy;
    }
}
