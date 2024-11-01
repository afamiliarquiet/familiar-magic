package io.github.afamiliarquiet.familiar_magic.block.entity;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.world.inventory.SimpleContainerData;

public class TableContainerData extends SimpleContainerData {
    public TableContainerData(int size) {
        super(size);
    }

    public byte[] getNybbles() {
        return FamiliarTricks.chompsToNybbles(new int[]{this.get(0), this.get(1), this.get(2), this.get(3), this.get(4), this.get(5), this.get(6), this.get(7)});
    }

    public boolean isModifiable() {
        return this.get(8) != 0;
    }
}
