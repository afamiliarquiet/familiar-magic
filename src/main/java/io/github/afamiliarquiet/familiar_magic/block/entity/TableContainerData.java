package io.github.afamiliarquiet.familiar_magic.block.entity;

import net.minecraft.core.UUIDUtil;
import net.minecraft.world.inventory.SimpleContainerData;

import java.util.UUID;

public class TableContainerData extends SimpleContainerData {
    public TableContainerData(int size) {
        super(size);
    }

    public UUID getUuid() {
        return UUIDUtil.uuidFromIntArray(new int[]{this.get(0), this.get(1), this.get(2), this.get(3)});
    }

    public boolean isModifiable() {
        return this.get(4) != 0;
    }
}
