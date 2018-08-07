package net.alphaantileak.mcac.server.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

/**
 * @author notaviable
 * @since 19.01.2017
 */
public class NettyEncryptionTranslator {
    private final Cipher cipher;
    private byte[] input = new byte[0];
    private byte[] output = new byte[0];

    protected NettyEncryptionTranslator(Cipher cipherIn) {
        this.cipher = cipherIn;
    }

    private byte[] readBuffer(ByteBuf buffer) {
        int i = buffer.readableBytes();
        if (this.input.length < i) {
            this.input = new byte[i];
        }

        buffer.readBytes(this.input, 0, i);
        return this.input;
    }

    protected ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
        int i = buffer.readableBytes();
        byte[] abyte = this.readBuffer(buffer);
        ByteBuf bytebuf = ctx.alloc().heapBuffer(this.cipher.getOutputSize(i));
        bytebuf.writerIndex(this.cipher.update(abyte, 0, i, bytebuf.array(), bytebuf.arrayOffset()));
        return bytebuf;
    }

    protected void cipher(ByteBuf inputBuffer, ByteBuf outputBuffer) throws ShortBufferException {
        int i = inputBuffer.readableBytes();
        byte[] abyte = this.readBuffer(inputBuffer);
        int j = this.cipher.getOutputSize(i);
        if (this.output.length < j) {
            this.output = new byte[j];
        }

        outputBuffer.writeBytes(this.output, 0, this.cipher.update(abyte, 0, i, this.output));
    }
}
