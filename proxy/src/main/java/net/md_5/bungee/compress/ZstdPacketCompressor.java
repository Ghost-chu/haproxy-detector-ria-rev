package net.md_5.bungee.compress;

import com.github.luben.zstd.ZstdCompressCtx;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import net.md_5.bungee.protocol.DefinedPacket;

public class ZstdPacketCompressor extends MessageToByteEncoder<ByteBuf> {
    private final int level;
    private ZstdCompressCtx compressCtx;
    @Setter
    private int threshold = 256;

    public ZstdPacketCompressor(int compressionLevel) {
        this.level = compressionLevel;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        compressCtx = new ZstdCompressCtx();
        compressCtx.setLevel(level);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        compressCtx = null;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) {
        int origSize = msg.readableBytes();
        if (origSize < threshold) {
            DefinedPacket.writeVarInt(0, out);
            out.writeBytes(msg);
        } else {
            DefinedPacket.writeVarInt(origSize, out);
            byte[] readed = new byte[origSize];
            msg.readBytes(readed);
            out.writeBytes(compressCtx.compress(readed));
        }
    }
}
