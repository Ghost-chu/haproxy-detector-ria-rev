package net.md_5.bungee.compress;

import com.github.luben.zstd.ZstdDecompressCtx;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.md_5.bungee.protocol.DefinedPacket;

import java.util.List;

public class ZstdPacketDecompressor extends MessageToMessageDecoder<ByteBuf> {

    private ZstdDecompressCtx decompressCtx;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        decompressCtx = new ZstdDecompressCtx();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        //zlib.free();
        decompressCtx = null;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        int size = DefinedPacket.readVarInt(in);
        if (size == 0) {
            out.add(in.retain());
        } else {
            ByteBuf decompressed = ctx.alloc().directBuffer();

            try {
                decompressCtx.decompress(decompressed.nioBuffer(), in.nioBuffer());
                Preconditions.checkState(decompressed.readableBytes() == size, "Decompressed packet size mismatch");
                out.add(decompressed);
                decompressed = null;
            } finally {
                if (decompressed != null) {
                    decompressed.release();
                }
            }
        }
    }
}
