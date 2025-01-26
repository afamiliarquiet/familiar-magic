package io.github.afamiliarquiet.familiar_magic;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.UUID;

public class FamiliarTricks {
    // todo - configurize?
    public static final int SUMMONING_TIME_SECONDS = 30;

    public static final byte NO_CANDLE = (byte) 0b10000000;
    public static final byte UNLIT_CANDLE = (byte) 0b01000000;
    public static final byte CANDLE_ERROR_MASK = (byte) 0b11110000;

    public static final char[] I_TAKE_A_BYTE = { // todo - for fun maybe make this configurable
            'd', // 0
            'o', // 1
            'c', // 2
            'u', // 3
            'm', // 4
            'e', // 5
            'n', // 6
            't', // 7
            'a', // 8
            'w', // 9
            'r', // a
            'i', // b
            'l', // c
            'y', // d
            's', // e
            'h', // f
    };

    // yea ill uhhh kill a quarter byte
    public static final byte[] OW_IVE_BEEN_BYTTEN = new byte[256];
    static {
        Arrays.fill(OW_IVE_BEEN_BYTTEN, (byte) -1);
        OW_IVE_BEEN_BYTTEN['d'] = 0;
        OW_IVE_BEEN_BYTTEN['o'] = 1;
        OW_IVE_BEEN_BYTTEN['c'] = 2;
        OW_IVE_BEEN_BYTTEN['u'] = 3;
        OW_IVE_BEEN_BYTTEN['m'] = 4;
        OW_IVE_BEEN_BYTTEN['e'] = 5;
        OW_IVE_BEEN_BYTTEN['n'] = 6;
        OW_IVE_BEEN_BYTTEN['t'] = 7;
        OW_IVE_BEEN_BYTTEN['a'] = 8;
        OW_IVE_BEEN_BYTTEN['w'] = 9;
        OW_IVE_BEEN_BYTTEN['r'] = 10;
        OW_IVE_BEEN_BYTTEN['i'] = 11;
        OW_IVE_BEEN_BYTTEN['l'] = 12;
        OW_IVE_BEEN_BYTTEN['y'] = 13;
        OW_IVE_BEEN_BYTTEN['s'] = 14;
        OW_IVE_BEEN_BYTTEN['h'] = 15;
    }

    public static int height(byte nybble) {
        return ((nybble >> 2) & 0b11) + 1;
    }

    public static int quantity(byte nybble) {
        return (nybble & 0b11) + 1;
    }

    public static byte makeNybble(int height, int quantity) {
        return (byte) ((((height - 1) & 0b11) << 2) | ((quantity - 1) & 0b11));
    }

    public static String uuidToTrueName(UUID uuid) {
        byte[] uuidBytes = Uuids.toByteArray(uuid);
        StringBuilder trueNameBuilder = new StringBuilder();
        for (byte uuidByte : uuidBytes) {
            trueNameBuilder.append(I_TAKE_A_BYTE[(uuidByte >> 4) & 0b1111]);
            trueNameBuilder.append(I_TAKE_A_BYTE[uuidByte & 0b1111]);
        }
        String trueName = trueNameBuilder.toString();
        return trueName.substring(0, 1).toUpperCase() + trueName.substring(1);
    }

    public static byte[] uuidToNybbles(UUID uuid) {
        byte[] nybbles = new byte[32];
        byte[] bytes = Uuids.toByteArray(uuid);

        for (int i = 0; i < 16; i++) {
            nybbles[2*i] = (byte) ((bytes[i] >> 4) & 0b1111);
            nybbles[2*i + 1] = (byte) (bytes[i] & 0b1111);
        }

        return nybbles;
    }

    public static UUID nybblesToUUID(byte[] nybbles) {
        if (nybbles.length != 32) {
            // woe
        }

        long uuidMost = 0, uuidLeast = 0;

        for (int i = 0; i < 32; i++) {
            if (i < 16) {
                uuidMost <<= 4;
                uuidMost |= nybbles[i] & 0b1111;
            } else {
                uuidLeast <<= 4;
                uuidLeast |= nybbles[i] & 0b1111;
            }
        }

        return new UUID(uuidMost, uuidLeast);
    }

    // these two are used for preserving extra data on the side of the nybbles in containerdata
    // i'm tired of checking array size so just. Don't Cause Problems. shrimple.
    public static int nybblesToIntChomp(byte[] nybbles, int chompIndex) {
        int chomp = 0;

        for (int i = 0; i < 2; i++) {
            chomp <<= 8;
            chomp |= ((int) nybbles[chompIndex * 2 + i]) & 0xFF;  // o luna.. why did i choose to delve into bitwise land
        }

        return chomp;
    }

    public static byte[] chompsToNybbles(int[] chomps) {
        byte[] nybbles = new byte[32];

        for (int i = 0; i < 16; i++) {
            int chomp = chomps[i];
//            nybbles[i * 2 + 3] = (byte) (chomp & 0xFF);
//            chomp >>= 8;
//            nybbles[i * 2 + 2] = (byte) (chomp & 0xFF);
//            chomp >>= 8;
            nybbles[i * 2 + 1] = (byte) (chomp & 0xFF);
            chomp >>= 8;
            nybbles[i * 2] = (byte) (chomp & 0xFF);
        }

        return nybbles;
    }

    public static byte @Nullable [] trueNameToNybbles(String trueName) {
        if (trueName.length() != 32) {
            return null;
        }

        byte[] nybbles = new byte[32];
        char[] trueNameArr = trueName.toLowerCase().toCharArray();

        for (int i = 0; i < 32; i++) {
            byte nybble = OW_IVE_BEEN_BYTTEN[trueNameArr[i]];
            if (nybble == -1) { // if not one of the 16 defined values, bad string
                nybbles[i] = (byte) 0xF0;
            } else {
                nybbles[i] = nybble;
            }
        }

        return nybbles;
    }

    public static @Nullable Entity findTargetByUuid(UUID uuid, MinecraftServer server) {
        Entity target = null;
        for (ServerWorld possibleWorld : server.getWorlds()) {
            Entity possibleTarget = possibleWorld.getEntity(uuid);
            if (possibleTarget != null) {
                target = possibleTarget;
                break;
            }
        }
        return target;
    }

    public static String nybblesToStrings(byte[] nybbles) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            sb.append(String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(nybbles[i]))).replace(' ', '0'));
            sb.append(", ");
            if (i == 5 || i == 11 || i == 15 || i == 19 || i == 25) {
                sb.append('\n');
            }
        }
        return sb.toString();
    }

    public static boolean canIgnite(ItemStack stack) {
        return stack.isOf(Items.FLINT_AND_STEEL) || stack.isOf(Items.FIRE_CHARGE);
    }
}
