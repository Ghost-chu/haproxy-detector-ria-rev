package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SetCompression extends DefinedPacket
{

    private int threshold;
    private boolean useZstd = false;
    private int level = 6;
    private byte[] zstdDict = new byte[0];

    @Override
    public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        threshold = DefinedPacket.readVarInt( buf );
        useZstd = DefinedPacket.readVarIntSafely( buf ) == 1;
        if (useZstd) {
            level = DefinedPacket.readVarIntSafely( buf );
            zstdDict = DefinedPacket.readArray( buf );
        }
    }

    @Override
    public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion)
    {
        DefinedPacket.writeVarInt( threshold, buf );
        if(useZstd){
            DefinedPacket.writeVarInt( 1, buf );
            DefinedPacket.writeVarInt( level, buf);
            DefinedPacket.writeArray( zstdDict, buf );
        }
    }

    @Override
    public void handle(AbstractPacketHandler handler) throws Exception
    {
        handler.handle( this );
    }
}
