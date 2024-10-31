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

public class NameTagDispenseItemBehavior extends DefaultDispenseItemBehavior {
    // sometimes u gotta add a feature to use a feature

    @Override
    protected @NotNull ItemStack execute(@NotNull BlockSource blockSource, @NotNull ItemStack nameTag) {
        Level level = blockSource.level();
        if (level.isClientSide) {
            return nameTag;
        }
        Component component = nameTag.get(DataComponents.CUSTOM_NAME);
        BlockPos targetPos = blockSource.pos().relative(blockSource.state().getValue(DispenserBlock.FACING));
        boolean success = false;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, new AABB(targetPos), EntitySelector.NO_SPECTATORS)) {
            if (component == null) {
                ItemStack trueName = FamiliarItems.TRUE_NAME_ITEM.toStack();
                trueName.set(DataComponents.CUSTOM_NAME, Component.literal(FamiliarTricks.uuidToTrueName(target.getUUID())));
                this.spew(blockSource, trueName);

                nameTag.shrink(1);
                success = true;
            } else if (!(target instanceof Player)) {
                target.setCustomName(component);
                if (target instanceof Mob mob) {
                    mob.setPersistenceRequired();
                }

                nameTag.shrink(1);
                success = true;
            }
        }

        if (success) {
            return nameTag;
        } else {
            return this.spew(blockSource, nameTag);
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
