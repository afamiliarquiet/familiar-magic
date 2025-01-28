package io.github.afamiliarquiet.familiar_magic.mixin;

import io.github.afamiliarquiet.familiar_magic.block.FamiliarBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemPlaceabilityMixin {
    @Inject(method = "useOnBlock", at = @At("TAIL"), cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        // i don't feel like this is a very good option in terms of efficiency.
        // but it seems like the least intrusive option?
        if (context.getStack().isOf(Items.GLOWSTONE_DUST)) {
            cir.setReturnValue(FamiliarBlocks.SPRINKLED_GLOWSTONE_DUST.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.COAL)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_COAL.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.DIAMOND)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_DIAMOND.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.EMERALD)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_EMERALD.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.LAPIS_LAZULI)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_LAPIS.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.QUARTZ)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_QUARTZ.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.RAW_COPPER)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_RAW_COPPER.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.RAW_GOLD)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_RAW_GOLD.asItem().useOnBlock(context));
        } else if (context.getStack().isOf(Items.RAW_IRON)) {
            cir.setReturnValue(FamiliarBlocks.STREWN_RAW_IRON.asItem().useOnBlock(context));
        }
    }
}
