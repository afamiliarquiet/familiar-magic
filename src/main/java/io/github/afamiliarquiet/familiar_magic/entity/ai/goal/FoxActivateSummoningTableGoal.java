package io.github.afamiliarquiet.familiar_magic.entity.ai.goal;

import io.github.afamiliarquiet.familiar_magic.FamiliarTricks;
import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import io.github.afamiliarquiet.familiar_magic.block.SummoningTableBlock;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;

public class FoxActivateSummoningTableGoal extends MoveToTargetPosGoal {
    public FoxActivateSummoningTableGoal(PathAwareEntity mob, double speed, int range) {
        super(mob, speed, range);
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        return state.isOf(FamiliarBlocks.SUMMONING_TABLE)
                && state.get(SummoningTableBlock.SUMMONING_TABLE_STATE) == SummoningTableBlock.SummoningTableState.INACTIVE
                && world.getBlockEntity(pos) instanceof SummoningTableBlockEntity tablenty
                && tablenty.hasTarget();
    }

    @Override
    public void tick() {
        if (this.hasReached() && FamiliarTricks.canIgnite(this.mob.getMainHandStack())) {
            // time to try to activate i suppose !
            if (this.mob.getWorld().getBlockEntity(this.targetPos) instanceof SummoningTableBlockEntity steby) {
                this.mob.getWorld().setBlockState(this.targetPos, steby.trySummon(this.mob.getWorld().getBlockState(this.targetPos)));
            }
        }

        super.tick();
    }

    @Override
    public boolean canStart() {
        return this.mob instanceof FoxEntity fox && FamiliarTricks.canIgnite(fox.getMainHandStack()) && super.canStart();
    }

    @Override
    public void start() {
        if (this.mob instanceof FoxEntity fox) {
            fox.setSitting(false);
        }
    }
}
