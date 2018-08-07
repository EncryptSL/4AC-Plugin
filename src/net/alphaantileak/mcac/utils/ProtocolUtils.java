package net.alphaantileak.mcac.utils;

import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

/**
 * @author notaviable
 * @since 18.01.2017
 */
public class ProtocolUtils {
    public static int predictVarIntSize(int input) {
        if ((input & 0xFFFFFF80) == 0) {
            return 1;
        }

        if ((input & 0xFFFFC000) == 0) {
            return 2;
        }

        if ((input & 0xFFE00000) == 0) {
            return 3;
        }

        if ((input & 0xF0000000) == 0) {
            return 4;
        }

        return 5;
    }

    public static int readVarInt(ByteBuf buf) {
        int i = 0;
        int j = 0;

        while (true) {
            byte b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }

    public static long readVarLong(ByteBuf buf) {
        long i = 0L;
        int j = 0;

        while (true) {
            byte b0 = buf.readByte();
            i |= (long) (b0 & 127) << j++ * 7;
            if (j > 10) {
                throw new RuntimeException("VarLong too big");
            }

            if ((b0 & 128) != 128) {
                break;
            }
        }

        return i;
    }


    public static void writeVarInt(ByteBuf buf, int input) {
        while ((input & -128) != 0) {
            buf.writeByte(input & 127 | 128);
            input >>>= 7;
        }

        buf.writeByte(input);
    }

    public static void writeVarLong(ByteBuf buf, long value) {
        while ((value & -128L) != 0L) {
            buf.writeByte((int) (value & 127L) | 128);
            value >>>= 7;
        }

        buf.writeByte((int) value);
    }

    public static String readString(ByteBuf buf) {
        int size = readVarInt(buf);
        byte[] strBuf = new byte[size];
        buf.readBytes(strBuf);
        return new String(strBuf, StandardCharsets.UTF_8);
    }

    public static void writeString(ByteBuf buf, String str) {
        byte[] strBytes = str.getBytes(StandardCharsets.UTF_8);
        writeVarInt(buf, strBytes.length);
        buf.writeBytes(strBytes);
    }

    public static byte[] readByteArray(ByteBuf buf) {
        int size = readVarInt(buf);
        byte[] b = new byte[size];
        buf.readBytes(b);
        return b;
    }

    public static void writeByteArray(ByteBuf buf, byte[] b) {
        writeVarInt(buf, b.length);
        buf.writeBytes(b);
    }

    /**
     * @param buf The packet buffer
     * @param enumClass The class of the enum
     * @param <T> The type of the enum
     * @return EnumValue An instance of the enum
     */
    public static <T extends Enum<T>> T readEnumValue(ByteBuf buf, Class<T> enumClass) {
        return enumClass.getEnumConstants()[readVarInt(buf)];
    }

    /**
     * @param buf The packet buffer
     * @param value The Enum
     */
    public static void writeEnumValue(ByteBuf buf, Enum<?> value) {
        writeVarInt(buf, value.ordinal());
    }
}
