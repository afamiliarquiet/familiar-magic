package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class OddTrinketItem extends Item {
    public OddTrinketItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        //return super.use(world, user, hand);
        user.setCurrentHand(hand);
        return TypedActionResult.consume(user.getStackInHand(hand));
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        LivingEntity target = user.isSneaking() ? user : entity;
        if (user.getUuid().equals(FamiliarMagic.its_sourceful_name) && target instanceof PlayerEntity) { // limited targets just for safety.. this isn't a good idea. this whole stolen homework thing. i'm doing it anyway
            if (FamiliarAttachments.getCurse(target).currentAffliction() == CurseAttachment.Curse.FAMILIAR_BITE) {
                CurseAttachment.Curse.FAMILIAR_BITE.strip(target);
            } else {
                CurseAttachment.Curse.FAMILIAR_BITE.apply(target);
            }
            return ActionResult.SUCCESS;
        } else {
            return super.useOnEntity(stack, user, entity, hand);
        }
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        FamiliarAttachments.getCurse(user).currentAffliction().inflict(world, user);
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player && user.getItemUseTime() > 13) {
            player.getItemCooldownManager().set(this, FamiliarAttachments.getCurse(user).currentAffliction().cooldown());
        }

        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, FamiliarAttachments.getCurse(user).currentAffliction().cooldown());
        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return FamiliarAttachments.getCurse(user).currentAffliction().maxUseTime();
    }
}
