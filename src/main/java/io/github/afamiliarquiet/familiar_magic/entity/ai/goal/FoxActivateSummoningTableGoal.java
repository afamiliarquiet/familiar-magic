package io.github.afamiliarquiet.familiar_magic.entity.ai.goal;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.SummoningTableBlock;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableState;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
//ClassesAreTaggedByDefault
//i've complained about this before and i will do it again. but i do kinda appreciate it in the end. i'll never admit it.
public class FoxActivateSummoningTableGoal extends MoveToBlockGoal {
    public FoxActivateSummoningTableGoal(Fox mob, double speedModifier, int searchRange) {
        super(mob, speedModifier, searchRange);
    }

    @Override
    protected boolean isValidTarget(LevelReader level, BlockPos pos) {
        BlockState blockState = level.getBlockState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        return blockState.is(FamiliarBlocks.SUMMONING_TABLE_BLOCK)
                && blockState.getValue(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableState.INACTIVE
                && blockEntity instanceof SummoningTableBlockEntity tableEntity
                && tableEntity.canSummon();
    }

    @Override
    public void tick() {
        if (this.isReachedTarget() && canUse()) {
            // activate!
            BlockState targetBlock = this.mob.level().getBlockState(this.blockPos);
            BlockEntity targetBlockEntity = this.mob.level().getBlockEntity(this.blockPos);
            if (targetBlockEntity instanceof SummoningTableBlockEntity tableEntity) {
                this.mob.level().setBlockAndUpdate(this.blockPos, tableEntity.startSummoning(targetBlock, false));
                // idk how best to deal with durability or stack shrinking that normally comes from useOn. so i won't.
            }
        }

        super.tick();
    }

    @Override
    public boolean canUse() {
        return this.mob instanceof Fox fox
                && fox.getItemBySlot(EquipmentSlot.MAINHAND).canPerformAction(ItemAbilities.FIRESTARTER_LIGHT)
                && !fox.isSleeping() // i would hate terribly to wake a fox for this. sorry people that want to get summoned
                && super.canUse();

    }

    @Override
    public void start() {
        if (this.mob instanceof Fox fox) {
            fox.setSitting(false);
        }
        super.start();
    }
}
