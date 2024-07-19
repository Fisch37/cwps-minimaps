package de.fisch37.cwpsminimaps.network;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

public enum AccessLevel {
    SECRET,
    PRIVATE,
    PUBLIC,
    OPEN;

    public static final PacketCodec<RegistryByteBuf, AccessLevel> PACKET_CODEC = PacketCodec.of(
            (val, buf) -> buf.writeByte(val.ordinal()),
            buf -> AccessLevel.values()[buf.readByte()]
    );
}
