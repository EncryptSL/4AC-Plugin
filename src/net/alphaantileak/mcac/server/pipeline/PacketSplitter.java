package net.alphaantileak.mcac.server.pipeline;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author notaviable
 * @version 1.0
 */
public class PacketSplitter extends ByteToMessageDecoder {

    public void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        while (in.readableBytes() >= 4) {
            in.markReaderIndex();
            int length = in.readInt();
            if (in.readableBytes() >= length) {
                if (in.hasMemoryAddress()) {
                    out.add(in.slice(in.readerIndex(), length).retain());
                    in.skipBytes(length);
                } else {
                    ByteBuf dst = ctx.alloc().directBuffer(length);
                    in.readBytes(dst);
                    out.add(dst);
                }
            } else {
                in.resetReaderIndex();
                return;
            }
        }
    }
}