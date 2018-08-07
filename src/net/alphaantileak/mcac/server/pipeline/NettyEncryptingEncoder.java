package net.alphaantileak.mcac.server.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import javax.crypto.Cipher;

/**
 * @author notaviable
 * @since 19.01.2017
 */
public class NettyEncryptingEncoder extends MessageToByteEncoder<ByteBuf> {
    private NettyEncryptionTranslator encryptionCodec;

    public NettyEncryptingEncoder(Cipher cipher) {
        this.encryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    public void encode(ChannelHandlerContext ctx, ByteBuf input, ByteBuf output) throws Exception {
        this.encryptionCodec.cipher(input, output);
    }
}
