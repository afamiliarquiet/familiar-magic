package io.github.afamiliarquiet.familiar_magic.block;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;

public class FamiliarBlocks {
    public static final RegistryKey<Block> SMOKE_WISP_KEY = key("smoke_wisp");
    public static final Block SMOKE_WISP = register(SMOKE_WISP_KEY,
            new SmokeWispBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.GRAY)
                    .replaceable()
                    .noCollision()
                    .breakInstantly()
                    .sounds(BlockSoundGroup.WOOL)
                    .pistonBehavior(PistonBehavior.DESTROY)
            )
    );

    public static final RegistryKey<Block> ENCHANTED_CANDLE_KEY = key("enchanted_candle");
    public static final Block ENCHANTED_CANDLE = regitem(ENCHANTED_CANDLE_KEY,
            new EnchantedCandleBlock(AbstractBlock.Settings.copy(Blocks.CANDLE)
                    .mapColor(MapColor.ORANGE)
            )
    );

    public static final RegistryKey<Block> SUMMONING_TABLE_KEY = key("summoning_table");
    public static final Block SUMMONING_TABLE = regitem(SUMMONING_TABLE_KEY,
            new SummoningTableBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.PURPLE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresTool()
                    .luminance(state -> state.get(SummoningTableBlock.SUMMONING_TABLE_STATE).lightLevel())
                    .strength(5, 1200)
            )
    );

    // this will change in 1.21.2 wahooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo
    public static final BlockEntityType<SummoningTableBlockEntity> SUMMONING_TABLE_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            FamiliarMagic.id("summoning_table_block_entity"),
            BlockEntityType.Builder.create(SummoningTableBlockEntity::new, SUMMONING_TABLE).build()
    );

    public static void initialize() {

    }

    public static Block register(RegistryKey<Block> blockKey, Block block) {
        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    public static Block regitem(RegistryKey<Block> blockKey, Block block) {
        RegistryKey<Item> itemKey = FamiliarItems.key(blockKey.getValue().getPath());

        BlockItem blockItem = new BlockItem(block, new Item.Settings());
        Registry.register(Registries.ITEM, itemKey, blockItem);

        return Registry.register(Registries.BLOCK, blockKey, block);
    }

    public static RegistryKey<Block> key(String thing) {
        return RegistryKey.of(RegistryKeys.BLOCK, FamiliarMagic.id(thing));
    }
}
