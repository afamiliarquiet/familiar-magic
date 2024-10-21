package io.github.afamiliarquiet.familiar_magic.block;

import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static io.github.afamiliarquiet.familiar_magic.FamiliarMagic.MOD_ID;

public class FamiliarBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MOD_ID);



    public static final DeferredBlock<CandleBlock> ENCHANTED_CANDLE_BLOCK = registerBlockWithItem(
            "enchanted_candle",
            () -> new EnchantedCandleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CANDLE)) // maybe toy with properties later
    );

    public static final DeferredBlock<SummoningTableBlock> SUMMONING_TABLE_BLOCK = registerBlockWithItem(
            "summoning_table",
            () -> new SummoningTableBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .instrument(NoteBlockInstrument.BASEDRUM)
                    .requiresCorrectToolForDrops() // hmm good point. i should fix this by adding this to stone pick tag or whatever
                    .lightLevel(state -> state.getValue(SummoningTableBlock.SUMMONING_TABLE_STATE).lightLevel())
                    .strength(5.0F, 1200.0F)
            )
    );

    @SuppressWarnings("unused") // because it IS USED >:( it is NOT safe to delete
    public static final DeferredBlock<SmokeWispBlock> SMOKE_WISP_BLOCK = BLOCKS.register(
            "smoke_wisp",
            () -> new SmokeWispBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_GRAY)
                    .replaceable()
                    .noCollission()
                    .instabreak()
                    .sound(SoundType.WOOL)
                    .pushReaction(PushReaction.DESTROY)
            )
    );

    @SuppressWarnings("DataFlowIssue") // totally get it. yeah. it says notnull. but neoforge docs say it's fine so it's fine
    public static final Supplier<BlockEntityType<SummoningTableBlockEntity>> SUMMONING_TABLE_BLOCK_ENTITY = BLOCK_ENTITY_TYPES.register(

            "summoning_table_block_entity",
            () -> BlockEntityType.Builder.of(
                    SummoningTableBlockEntity::new,
                    SUMMONING_TABLE_BLOCK.get()
            ).build(null)
    );



    private static <T extends Block> DeferredBlock<T> registerBlockWithItem(String name, Supplier<T> block) {
        DeferredBlock<T> deferredBlock = BLOCKS.register(name, block);
        FamiliarItems.ITEMS.register(name, () -> new BlockItem(deferredBlock.get(), new Item.Properties()));
        return deferredBlock;
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        BLOCK_ENTITY_TYPES.register(eventBus);
    }
}
