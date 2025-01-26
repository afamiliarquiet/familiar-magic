package io.github.afamiliarquiet.familiar_magic.item;

import io.github.afamiliarquiet.familiar_magic.data.CurseAttachment;
import io.github.afamiliarquiet.familiar_magic.data.FamiliarAttachments;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsage;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public class CuriousVialItem extends Item {

    public CuriousVialItem(Settings settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
        }

        if (!world.isClient) {
            if (FamiliarAttachments.getCurse(user).currentAffliction() == CurseAttachment.Curse.DRAGON && user instanceof PlayerEntity player) {
                player.getHungerManager().add(1, 1.3F);
            } else {
                FamiliarAttachments.setCurse(user, new CurseAttachment(CurseAttachment.Curse.DRAGON));
                dragonPoofRawr(user);
            }

            Vec3d p = user.getPos();
            world.playSound(null, p.x, p.y, p.z, SoundEvents.ITEM_OMINOUS_BOTTLE_DISPOSE, user.getSoundCategory(), 1.0F, 1.0F);
        }

        stack.decrementUnlessCreative(1, user);
        return stack;
    }

    private void dragonPoofRawr(LivingEntity entity) {
        if (entity.getWorld() instanceof ServerWorld world) {
            Box size = entity.getDimensions(entity.getPose()).getBoxAt(0,0,0);

            world.spawnParticles(ParticleTypes.GUST,
                    entity.offsetX(0.5), entity.getBodyY(0.5), entity.offsetZ(0.5),
                    6, size.getLengthX()*0.75, size.getLengthY()*0.5, size.getLengthZ()*0.75, 0);

            world.spawnParticles(ParticleTypes.FLAME,
                    entity.getX(), entity.getBodyY(0.5), entity.getZ(),
                    7, size.getLengthX()*0.75, size.getLengthY()*0.5, size.getLengthZ()*0.75, 0);

            if (entity instanceof PlayerEntity player) {
                Vec3d p = entity.getPos();
                entity.getWorld().playSound(null, p.x, p.y, p.z, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.PLAYERS, 0.5f, 1.3f);
                player.playSoundToPlayer(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.PLAYERS, 0.1f, 1.3f);
                //player.sendMessage(Text.translatable("message.familiar_magic.curse.dragon.applied").withColor(0x4fe7ac), true);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        return 16; // snack
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.DRINK;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        return ItemUsage.consumeHeldItem(world, user, hand);
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.familiar_magic.curious_vial").formatted(Formatting.RED));

        super.appendTooltip(stack, context, tooltip, type);
    }
}
