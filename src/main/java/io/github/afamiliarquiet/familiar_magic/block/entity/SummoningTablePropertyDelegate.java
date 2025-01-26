package io.github.afamiliarquiet.familiar_magic.block.entity;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.screen.ArrayPropertyDelegate;

public class SummoningTablePropertyDelegate extends ArrayPropertyDelegate {
    // what an awful name. i'd hate to be a PropertyDelegate
    // also for some delightful reason minecraft was chopping my ints in half, so. now i do 2 per int i guess! so efficient
    // i swear i recall reading about that issue but i can't find it again so whatever
    public SummoningTablePropertyDelegate(int size) {
        super(size);
    }

    public byte[] getNybbles() {
        return FamiliarTricks.chompsToNybbles(new int[]{get(0), get(1), get(2), get(3), get(4), get(5), get(6), get(7), get(8), get(9), get(10), get(11), get(12), get(13), get(14), get(15)});
    }

    public boolean isModifiable() {
        return get(16) != 0;
    }
}
