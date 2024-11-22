package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TrueNameDispenseItemBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack oldName) {
        Level level = blockSource.level();
        if (level.isClientSide) {
            return oldName;
        }
        Component component = oldName.get(DataComponents.CUSTOM_NAME);
        SingedComponentRecord record = oldName.get(FamiliarItems.SINGED_COMPONENT);
        BlockPos targetPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
        boolean success = false;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(targetPos), EntitySelector.NO_SPECTATORS)) {
            if (record != null && !record.singed()) {
                ItemStack newName = FamiliarItems.TRUE_NAME_ITEM.toStack();
                newName.set(DataComponents.CUSTOM_NAME, Component.literal(FamiliarTricks.uuidToTrueName(target.getUUID())));
                this.spew(blockSource, newName);

                oldName.shrink(1);
                success = true;
            }
        }

        if (success) {
            return oldName;
        } else {
            return this.spew(blockSource, oldName);
        }
    }

    private ItemStack spew(BlockSource blockSource, ItemStack item) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        Position position = DispenserBlock.getDispensePosition(blockSource);
        ItemStack itemstack = item.split(1);
        spawnItem(blockSource.level(), itemstack, 6, direction, position);
        return item;
    }
}
