package io.github.afamiliarquiet.familiar_magic.block.entity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;

@MethodsReturnNonnullByDefault
public enum SummoningTableState implements StringRepresentable {
    INACTIVE("inactive", 7),
    BURNING("burning", 10),
    SUMMONING("summoning", 13);

    private final String name;
    private final int lightLevel;

    SummoningTableState(String name, int lightLevel) {
        this.name = name;
        this.lightLevel = lightLevel;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }

    public int lightLevel() {
        return this.lightLevel;
    }
}
