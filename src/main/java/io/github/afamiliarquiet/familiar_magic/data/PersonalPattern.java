package io.github.afamiliarquiet.familiar_magic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record PersonalPattern(List<String> blocks, List<Byte> pattern) {
    // todo - should maybe have some optimization for if there's no pattern. some flag or something to raise, idk
    public static final Codec<PersonalPattern> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(Codec.STRING).fieldOf("blocks").forGetter(PersonalPattern::blocks),
                    Codec.list(Codec.BYTE).fieldOf("pattern").forGetter(PersonalPattern::pattern)
            ).apply(instance, PersonalPattern::new)
    );

    private static byte get(List<Byte> pattern, int xRow, int zColumn) {
        Byte index = pattern.get(xRow * 13 + zColumn);
        return index == null ? 0 : index;
    }

    private static void set(List<Byte> bytes, int xRow, int zColumn, byte value) {
        bytes.set(13 * xRow + zColumn, value);
    }

    public static PersonalPattern fromTable(World world, BlockPos pos) {
        List<String> blocks = new ArrayList<>();
        Byte[] inbetweener = new Byte[169];
        Arrays.fill(inbetweener, (byte) 0);
        List<Byte> pattern = Arrays.asList(inbetweener); // i think java initializes to 0. prolly.

        for (int xRow = 0; xRow < 13; xRow++) {
            for (int zColumn = 0; zColumn < 13; zColumn++) {
                RegistryEntry<Block> current = world.getBlockState(pos.add(xRow - 6, 0, zColumn - 6)).getRegistryEntry();

                if (current.isIn(FamiliarTags.OBJECTS_OF_PERSONAL_POWER)) {
                    if (!blocks.contains(current.getIdAsString())) {
                        blocks.addLast(current.getIdAsString());
                    }
                    // this indexOf is probably not gonna be super kind to me if someone makes a pattern with 169 different blocks. optimization comes later though
                    // with 169 potential blocks, byte should always be enough. + 1 to avoid the default empty 0.. could arrays.fill but i don't really care right now
                    set(pattern, xRow, zColumn, (byte) (blocks.indexOf(current.getIdAsString()) + 1));
                }
            }
        }

        return new PersonalPattern(blocks, pattern);
    }

    public boolean matches(World world, BlockPos pos) {
        for (int xRow = 0; xRow < 13; xRow++) {
            for (int zColumn = 0; zColumn < 13; zColumn++) {
                // could maybe optimize sparseness but.. i reckon the world lookup is significantly more impactful
                // than iterating over empty spots in the arrays, so it probb doesn't really matter
                if (get(this.pattern, xRow, zColumn) > 0) {
                    if (!world.getBlockState(pos.add(xRow - 6, 0, zColumn - 6)).getRegistryEntry()
                            .matchesId(Identifier.of(this.blocks.get(get(this.pattern, xRow, zColumn) - 1)))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
