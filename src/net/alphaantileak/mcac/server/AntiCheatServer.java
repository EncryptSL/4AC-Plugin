package net.alphaantileak.mcac.server;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import net.alphaantileak.mcac.server.pipeline.PacketDecoder;
import net.alphaantileak.mcac.server.pipeline.PacketEncoder;
import net.alphaantileak.mcac.server.pipeline.PacketPrepender;
import net.alphaantileak.mcac.server.pipeline.PacketSplitter;
import net.alphaantileak.mcac.utils.CryptManager;
import net.alphaantileak.mcac.utils.NettyUtils;

import java.io.IOException;
import java.security.KeyPair;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class AntiCheatServer {
    private final int port;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private ServerBootstrap bootstrap;
    private KeyPair rsaKeypair;
    private static AntiCheatServer instance;
    public List<AntiCheatClient> clients = new CopyOnWriteArrayList<>();

    public AntiCheatServer(int port) {
        instance = this;
        this.port = port;
        this.rsaKeypair = CryptManager.generateKeyPair();
    }

    public static AntiCheatServer instance() {
        return instance;
    }

    public void start() {
        bossGroup = NettyUtils.createEventLoop();
        workerGroup = NettyUtils.createEventLoop();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NettyUtils.getServerChannel())
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast("packet_splitter", new PacketSplitter());
                        ch.pipeline().addLast("packet_prepender", new PacketPrepender());
                        ch.pipeline().addLast("packet_decoder", new PacketDecoder());
                        ch.pipeline().addLast("packet_encoder", new PacketEncoder());
                        ch.pipeline().addLast("packet_handler", new AntiCheatClient(AntiCheatServer.this));
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true);

        bootstrap.bind(port).syncUninterruptibly();
    }

    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    public KeyPair getKeyPair() {
        return rsaKeypair;
    }

    public int getPort() {
        return port;
    }

    public AntiCheatProxy connectProxy(String ip, int port, IProxyHandler handler) throws IOException, InterruptedException {
        AntiCheatProxy proxy = new AntiCheatProxy(handler);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(workerGroup)
                .channel(NettyUtils.getChannel())
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast("handler", proxy);
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true);

        ChannelFuture cf = bootstrap.connect(ip, port).await();
        if (cf.isSuccess()) {
            return proxy;
        } else {
            throw new IOException("Failed connecting");
        }
    }
}
