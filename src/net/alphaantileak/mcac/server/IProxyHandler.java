package net.alphaantileak.mcac.server;

import io.netty.buffer.ByteBuf;

public interface IProxyHandler {
    void onRecv(ByteBuf data);
    void onConnect();
    void onDisconnect();
}
