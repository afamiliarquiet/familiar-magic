package io.github.afamiliarquiet.familiar_magic.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.afamiliarquiet.familiar_magic.block.Burnable;
import net.minecraft.block.BlockState;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(FireChargeItem.class)
public abstract class FireChargeIgnitionMixin {
    @ModifyExpressionValue(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/CandleBlock;canBeLit(Lnet/minecraft/block/BlockState;)Z"))
    private boolean allowBurnables(boolean original, @Local BlockState clickedState) {
        return original || clickedState.getBlock() instanceof Burnable;
    }

    @WrapOperation(method = "useOnBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;with(Lnet/minecraft/state/property/Property;Ljava/lang/Comparable;)Ljava/lang/Object;"))
    private Object burnify(BlockState state, Property property, Comparable comparable, Operation<Object> original, @Local(argsOnly = true) ItemUsageContext context) {
        if (state.getBlock() instanceof Burnable burnyBlock) {
            return burnyBlock.onIgnition(state, context);
        } else {
            return original.call(state, property, comparable);
        }
    }
}
