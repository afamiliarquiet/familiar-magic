package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarComponents;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.FallibleItemDispenserBehavior;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class TrueNameDispenserBehavior extends FallibleItemDispenserBehavior {
    @Override
    protected ItemStack dispenseSilently(BlockPointer pointer, ItemStack startStack) {
        ServerWorld world = pointer.world();

        if (world.isClient() || Boolean.TRUE.equals(startStack.get(FamiliarComponents.SINGED_COMPONENT))) {
            return startStack;
        }

        BlockPos targetPos = pointer.pos().offset(pointer.state().get(DispenserBlock.FACING));
        List<LivingEntity> targets = world.getEntitiesByClass(LivingEntity.class, new Box(targetPos), EntityPredicates.EXCEPT_SPECTATOR);

        if (!targets.isEmpty()) {
            ItemStack named = FamiliarItems.TRUE_NAME.getDefaultStack();
            named.set(DataComponentTypes.CUSTOM_NAME, Text.literal(FamiliarTricks.uuidToTrueName(targets.getFirst().getUuid())));

            super.dispenseSilently(pointer, named);
            startStack.decrement(1);

            this.setSuccess(true);
        } else {
            this.setSuccess(false);
        }

        return startStack;
    }
}
