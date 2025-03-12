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

public record PersonalPattern(List<String> blocks, List<Byte> pattern, int height, int width, int xInset, int zInset) {
    // todo - nbt is transferred as like. string, isn't it? converted to json and transferred like that. so wouldn't stringifying rows for codec be better? idk
    public static final Byte EMPTY = 0;
    public static final Byte SUMMONING_TABLE = 1;
    public static final Codec<PersonalPattern> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Codec.list(Codec.STRING).fieldOf("blocks").forGetter(PersonalPattern::blocks),
                    Codec.list(Codec.BYTE).fieldOf("pattern").forGetter(PersonalPattern::pattern),
                    Codec.INT.fieldOf("h").forGetter(PersonalPattern::height),
                    Codec.INT.fieldOf("w").forGetter(PersonalPattern::width),
                    Codec.INT.fieldOf("x").forGetter(PersonalPattern::xInset),
                    Codec.INT.fieldOf("z").forGetter(PersonalPattern::zInset)
            ).apply(instance, PersonalPattern::new)
    );

    private byte get(int xRow, int zColumn) {
        Byte index = pattern.get((xRow - xInset) * width + (zColumn - zInset));
        return index == null ? 0 : index;
    }

    // catalytic converters for the EMPTY and SUMMONING_TABLE to be disregarded
    // with 169 potential blocks, byte should always be enough. + 2 to avoid the empty 0 and summoning table 1
    // also i see that java bytes are signed. uhhh it seems like thats working fine enough for bitwise stuff w/ candles so. IGNORE ANY PROBLEMS
    // but also yea actually this may break if you do more than 127 different blocks. thats fucked up if you do that though
    private static byte blocksToPattern(int index) {
        return (byte) (index >= 0 ? index + 2 : -1);
    }

    // should i like. bitwise or this with an intish first or something. idk
    private static int patternToBlocks(byte pattern) {
        return pattern - 2;
    }

    public static PersonalPattern fromTable(World world, BlockPos pos) {
        List<String> blocks = new ArrayList<>();
        byte[][] pattern = new byte[13][13];

        // summoning table will always exist at 6,6
        int firstRow = 6;
        int lastRow = 6;
        int firstColumn = 6;
        int lastColumn = 6;

        for (int xRow = 0; xRow < 13; xRow++) {
            for (int zColumn = 0; zColumn < 13; zColumn++) {
                RegistryEntry<Block> current = world.getBlockState(pos.add(xRow - 6, 0, zColumn - 6)).getRegistryEntry();

                if (current.isIn(FamiliarTags.FAMILIAR_THINGS)) {
                    // track min/max per row and of rows, to trim strings and arrays
                    if (xRow < firstRow) {
                        firstRow = xRow;
                    }
                    if (xRow > lastRow) {
                        lastRow = xRow;
                    }
                    if (zColumn < firstColumn) {
                        firstColumn = zColumn;
                    }
                    if (zColumn > lastColumn) {
                        lastColumn = zColumn;
                    }

                    if (!blocks.contains(current.getIdAsString())) {
                        blocks.addLast(current.getIdAsString());
                    }
                    // this indexOf is probably not gonna be super kind to me if someone makes a pattern with 169 different blocks. optimization comes later though
                    pattern[xRow][zColumn] = blocksToPattern(blocks.indexOf(current.getIdAsString()));
                }
            }
        }
        pattern[6][6] = SUMMONING_TABLE; // set after juuust in case someone tries to get funky in the scan.
        // actually it might be better to not do this 'cause like. could have a pattern that's just one block in one corner.
        // but that's probably unlikely anyway idk. trimming it a lil is good enough improvement for me

        // time to do the trimming!
        //w ait thats harder with a flattened array
        // ok i unflatterated it
        int numRows = lastRow - firstRow + 1;
        int numColumns = lastColumn - firstColumn + 1;
        Byte[] trimmedBetweener = new Byte[numRows * numColumns];
        for (int neoRow = 0; neoRow < numRows; neoRow++) {
            for (int neoColumn = 0; neoColumn < numColumns; neoColumn++) {
                trimmedBetweener[neoRow * numColumns + neoColumn] = pattern[neoRow + firstRow][neoColumn + firstColumn];
            }
        }

        return new PersonalPattern(blocks, Arrays.asList(trimmedBetweener), numRows, numColumns, firstRow, firstColumn);
    }

    public boolean matches(World world, BlockPos pos) {
        for (int xRow = xInset; xRow < height + xInset; xRow++) {
            for (int zColumn = zInset; zColumn < width + zInset; zColumn++) {
                // could maybe optimize sparseness but.. i reckon the world lookup is significantly more impactful
                // than iterating over empty spots in the arrays, so it probb doesn't really matter
                int desiredBlockIndex = patternToBlocks(get(xRow, zColumn));
                if (desiredBlockIndex >= 0) {
                    if (!world.getBlockState(pos.add(xRow - 6, 0, zColumn - 6)).getRegistryEntry()
                            .matchesId(Identifier.of(this.blocks.get(desiredBlockIndex)))) {
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
