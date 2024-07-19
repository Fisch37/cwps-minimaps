package de.fisch37.cwpsminimaps.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import org.jetbrains.annotations.Nullable;

public class NullableCodec<B extends ByteBuf, V> implements PacketCodec<B, V> {
    public final PacketCodec<B, V> parent;

    public NullableCodec(PacketCodec<B, V> parent) {
        this.parent = parent;
    }

    @Override
    @Nullable
    public V decode(B buf) {
        return PacketByteBuf.readNullable(buf, parent);
    }

    @Override
    public void encode(B buf, @Nullable V value) {
        PacketByteBuf.writeNullable(buf, value, parent);
    }
}
