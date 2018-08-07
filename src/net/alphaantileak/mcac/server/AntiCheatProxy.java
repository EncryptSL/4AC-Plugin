package net.alphaantileak.mcac.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

import java.net.SocketAddress;

public class AntiCheatProxy extends ChannelInboundHandlerAdapter {
    private final IProxyHandler handler;
    private ChannelHandlerContext ctx;

    public AntiCheatProxy(IProxyHandler handler) {
        this.handler = handler;
    }

    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        handler.onConnect();
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        handler.onDisconnect();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ByteBuf)) throw new IllegalStateException("Expected msg to be ByteBuf, but got " + msg.getClass().getName());

        try {
            handler.onRecv((ByteBuf) msg);
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    public void disconnect() {
        ctx.close();
    }

    public void send(byte[] data) {
        ctx.writeAndFlush(Unpooled.wrappedBuffer(data));
    }

    public void send(ByteBuf data) {
        ctx.write(data);
    }

    public SocketAddress getLocalAddr() {
        return ctx.channel().localAddress();
    }
}
