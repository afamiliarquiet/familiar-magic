package io.github.afamiliarquiet.familiar_magic.block;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.MapColor;
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
    public static final Block SMOKE_WISP = regitem(SMOKE_WISP_KEY,
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
                    .luminance(EnchantedCandleBlock.STATE_TO_LUMINANCE)
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

//    public static final RegistryKey<Block> SPRINKLED_GLOWSTONE_DUST_KEY = key("sprinkled_glowstone_dust");
//    public static final Block SPRINKLED_GLOWSTONE_DUST = regitem(SPRINKLED_GLOWSTONE_DUST_KEY,
//            new SprinkledGlowstoneDustBlock(AbstractBlock.Settings.create()
//                    .mapColor(MapColor.PALE_YELLOW)
//                    .noCollision()
//                    .breakInstantly()
//                    .sounds(BlockSoundGroup.GLASS)
//                    .luminance(SprinkledGlowstoneDustBlock.lumiumi(9))
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_COAL_KEY = key("strewn_coal");
//    public static final Block STREWN_COAL = regitem(STREWN_COAL_KEY,
//            new StrewnBlock(Items.COAL, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.BLACK)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_DIAMOND_KEY = key("strewn_diamond");
//    public static final Block STREWN_DIAMOND = regitem(STREWN_DIAMOND_KEY,
//            new StrewnBlock(Items.DIAMOND, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.DIAMOND_BLUE)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_EMERALD_KEY = key("strewn_emerald");
//    public static final Block STREWN_EMERALD = regitem(STREWN_EMERALD_KEY,
//            new StrewnBlock(Items.EMERALD, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.EMERALD_GREEN)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_LAPIS_KEY = key("strewn_lapis");
//    public static final Block STREWN_LAPIS = regitem(STREWN_LAPIS_KEY,
//            new StrewnBlock(Items.LAPIS_LAZULI, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.LAPIS_BLUE)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_QUARTZ_KEY = key("strewn_quartz");
//    public static final Block STREWN_QUARTZ = regitem(STREWN_QUARTZ_KEY,
//            new StrewnBlock(Items.QUARTZ, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.OFF_WHITE)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_RAW_COPPER_KEY = key("strewn_raw_copper");
//    public static final Block STREWN_RAW_COPPER = regitem(STREWN_RAW_COPPER_KEY,
//            new StrewnBlock(Items.RAW_COPPER, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.ORANGE)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_RAW_GOLD_KEY = key("strewn_raw_gold");
//    public static final Block STREWN_RAW_GOLD = regitem(STREWN_RAW_GOLD_KEY,
//            new StrewnBlock(Items.RAW_GOLD, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.GOLD)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );
//
//    public static final RegistryKey<Block> STREWN_RAW_IRON_KEY = key("strewn_raw_iron");
//    public static final Block STREWN_RAW_IRON = regitem(STREWN_RAW_IRON_KEY,
//            new StrewnBlock(Items.RAW_IRON, AbstractBlock.Settings.create()
//                    .mapColor(MapColor.RAW_IRON_PINK)
//                    .noCollision()
//                    .breakInstantly()
//                    .pistonBehavior(PistonBehavior.DESTROY)
//            )
//    );

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
