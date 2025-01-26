package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.entity.FireBreathEntity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
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
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            EntityDimensions sizey = user.getDimensions(user.getPose());
            float scaley = Math.max(sizey.height(), sizey.width()) / 1.8f;

            FireBreathEntity flameExclamationMark = new FireBreathEntity(user, world, scaley);
            flameExclamationMark.setVelocity(user, user.getPitch(), user.headYaw, 0, 0.5f * scaley, 13f);
            flameExclamationMark.setPosition(flameExclamationMark.getPos().add(user.getRotationVector().multiply(0.5 * scaley)).addRandom(user.getRandom(), 0.013f * scaley));
            world.spawnEntity(flameExclamationMark);

            Vec3d p = user.getPos();
            world.playSound(null, p.x, p.y, p.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, user.getRandom().nextFloat() * 0.13f + 1);
        }
    }

    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (user instanceof PlayerEntity player && user.getItemUseTime() > 13) {
            player.getItemCooldownManager().set(this, 130);
        }

        super.onStoppedUsing(stack, world, user, remainingUseTicks);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof PlayerEntity player) {
            player.getItemCooldownManager().set(this, 130);
        }

        return super.finishUsing(stack, world, user);
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 62;
    }
}
