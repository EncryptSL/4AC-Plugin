package net.alphaantileak.mcac.server.pipeline;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import javax.crypto.Cipher;
import java.util.List;

/**
 * @author notaviable
 * @since 19.01.2017
 */

public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final NettyEncryptionTranslator decryptionCodec;

    public NettyEncryptingDecoder(Cipher cipher) {
        this.decryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    public void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> obj) throws Exception {
        obj.add(this.decryptionCodec.decipher(ctx, input));
    }
}