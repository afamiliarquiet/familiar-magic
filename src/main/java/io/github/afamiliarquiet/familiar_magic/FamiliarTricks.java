package io.github.afamiliarquiet.familiar_magic;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.UUIDUtil;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.UUID;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FamiliarTricks {
    // yea ill uhhh kill a quarter byte
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

    public static String uuidToTrueName(UUID uuid) {
        byte[] uuidBytes = UUIDUtil.uuidToByteArray(uuid);
        StringBuilder trueNameBuilder = new StringBuilder();
        for (byte uuidByte : uuidBytes) {
            trueNameBuilder.append(I_TAKE_A_BYTE[(uuidByte >> 4) & 0b1111]);
            trueNameBuilder.append(I_TAKE_A_BYTE[uuidByte & 0b1111]);
        }
        String trueName = trueNameBuilder.toString();
        return trueName.substring(0, 1).toUpperCase() + trueName.substring(1);
    }

    public static String uuidStringToTrueName(String uuidString) {
        return uuidToTrueName(UUID.fromString(uuidString));
    }

    public static String trueNameToUuidString(String trueName) {
        return trueNameToUUID(trueName).toString();
    }

    public static UUID trueNameToUUID(String trueName) {
        long uuidMost = 0, uuidLeast = 0;
        char[] trueNameArr = trueName.toLowerCase().toCharArray();

        for (int i = 0; i < 32; i++) {
            if (i < 16) {
                uuidMost <<= 4;
                uuidMost |= OW_IVE_BEEN_BYTTEN[trueNameArr[i]];
            } else {
                uuidLeast <<= 4;
                uuidLeast |= OW_IVE_BEEN_BYTTEN[trueNameArr[i]];
            }
        }

        return new UUID(uuidMost, uuidLeast);
    }
}
