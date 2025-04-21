package io.github.afamiliarquiet.familiar_magic.data;

import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.afamiliarquiet.familiar_magic.FamiliarMagic;
import io.github.afamiliarquiet.familiar_magic.FamiliarSounds;
import io.github.afamiliarquiet.familiar_magic.entity.FireBreathEntity;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public class CurseAttachment {
    public static final PacketCodec<ByteBuf, CurseAttachment> PACKET_CODEC = PacketCodec.tuple(
            Curse.PACKET_CODEC,
            CurseAttachment::currentAffliction,
            CurseAttachment::new
    );

    public static final Codec<CurseAttachment> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Curse.CODEC.fieldOf("current").forGetter(CurseAttachment::currentAffliction)
            ).apply(instance, CurseAttachment::new)
    );

    public static final HashMultimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> FAMILIAR_BITE_ATTRIBUTES = HashMultimap.create();
    static {
        FAMILIAR_BITE_ATTRIBUTES.put(EntityAttributes.GENERIC_SCALE, new EntityAttributeModifier(FamiliarMagic.id("familiar_bite"), -0.6, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
    }

    // the debt clock's gears are grinding loudly. :ohno:
    // this debt is incurred by my theft of @enjarai's code
    // everyone please say "thank you evelyn!"
    private FoxEntity entity = null;
    private final Curse currentAffliction;

    public CurseAttachment(Curse currentAffliction) {
        this.currentAffliction = currentAffliction;
    }

    public Curse currentAffliction() {
        return this.currentAffliction;
    }

    public void updateFox(PlayerEntity player) {
        entity = new FoxEntity(EntityType.FOX, player.getWorld());
        entity.setAiDisabled(true);
        entity.setInvulnerable(true);

        player.calculateDimensions();
    }

    public FoxEntity asFox(PlayerEntity player) {
        if (entity == null) {
            updateFox(player);
        }

        entity.setYaw(player.getYaw());
        entity.prevYaw = player.prevYaw;
        entity.setPitch(player.getPitch());
        entity.prevPitch = player.prevPitch;

        entity.setPos(player.getX(), player.getY(), player.getZ());
        entity.prevX = player.prevX;
        entity.prevY = player.prevY;
        entity.prevZ = player.prevZ;

        entity.setBodyYaw(player.bodyYaw);
        entity.prevBodyYaw = player.prevBodyYaw;
        entity.setHeadYaw(player.headYaw);
        entity.prevHeadYaw = player.prevHeadYaw;

        entity.hurtTime = player.hurtTime;

        entity.handSwinging = player.handSwinging;
        entity.handSwingTicks = player.handSwingTicks;
        entity.handSwingProgress = player.handSwingProgress;
        entity.lastHandSwingProgress = player.lastHandSwingProgress;

        entity.setStackInHand(Hand.MAIN_HAND, player.getMainHandStack());

        ((FoxthingLimbAnimator) entity.limbAnimator).familiar_magic$copyFrom(player.limbAnimator);

        if (player.getPose() == EntityPose.CROUCHING) {
            entity.setSitting(true);
            entity.setPose(EntityPose.STANDING);
        } else {
            entity.setSitting(false);
            entity.setPose(player.getPose());
        }

        return entity;
    }

    public @Nullable FoxEntity getFox() {
        return entity;
    }

    public void tick(Entity bearer) {
        if (entity != null) {
            entity.setPosition(bearer.getPos());
            if (entity.getRandom().nextInt(1000) < entity.ambientSoundChance++) {
                entity.ambientSoundChance = -entity.getMinAmbientSoundDelay();
                entity.playAmbientSound();
            }
        } else {
            if (bearer instanceof PlayerEntity player) {
                updateFox(player);
            }
        }
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches") // i don't care it'll change later probably
    public enum Curse {
        NOTHING,
        FAMILIAR_BITE,
        DRAGON;

        // surely there exists an enum codec already. whatever. i can tell im gonna change this later. sorry, future me!
        // eventually this should maybe be like.. an interface?
        public static final PacketCodec<ByteBuf, Curse> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.STRING,
                Curse::name,
                Curse::valueOf
        );

        public static final Codec<Curse> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        Codec.STRING.fieldOf("name").forGetter(Curse::name)
                ).apply(instance, Curse::valueOf)
        );

        public CurseAttachment attachment() {
            return new CurseAttachment(this);
        }

        public void apply(LivingEntity bearer) {
            strip(bearer);
            CurseAttachment attachment = FamiliarAttachments.setCurse(bearer, this.attachment());

            switch (this) {
                case FAMILIAR_BITE -> {
//                    bearer.getAttributes().addTemporaryModifiers(CurseAttachment.FAMILIAR_BITE_ATTRIBUTES);
                    if (bearer instanceof PlayerEntity player) {
                        attachment.updateFox(player);
                    }
//                    bearer.calculateDimensions();
                }
                case DRAGON -> {
                    if (bearer.getWorld() instanceof ServerWorld world) {
//                        Box size = entity.getDimensions(entity.getPose()).getBoxAt(0,0,0);
//
//                        world.spawnParticles(ParticleTypes.GUST,
//                                entity.offsetX(0.5), entity.getBodyY(0.5), entity.offsetZ(0.5),
//                                6, size.getLengthX()*0.75, size.getLengthY()*0.5, size.getLengthZ()*0.75, 0);
//
//                        world.spawnParticles(ParticleTypes.FLAME,
//                                entity.getX(), entity.getBodyY(0.5), entity.getZ(),
//                                7, size.getLengthX()*0.75, size.getLengthY()*0.5, size.getLengthZ()*0.75, 0);

                        if (bearer instanceof PlayerEntity player) {
                            Vec3d p = bearer.getPos();
                            bearer.getWorld().playSound(null, p.x, p.y, p.z, FamiliarSounds.CURSE_APPLY, SoundCategory.PLAYERS, 0.5f, 1.3f);
                            player.playSoundToPlayer(FamiliarSounds.CURSE_APPLY_PERSONAL, SoundCategory.PLAYERS, 0.1f, 1.3f);
                            //player.sendMessage(Text.translatable("message.familiar_magic.curse.dragon.applied").withColor(0x4fe7ac), true);
                        }
                    }
                }
            }
        }

        public void strip(LivingEntity bearer) {
            FamiliarAttachments.removeCurse(bearer);
            switch (this) {
                case FAMILIAR_BITE -> {
//                    entity.getAttributes().removeModifiers(CurseAttachment.FAMILIAR_BITE_ATTRIBUTES);
//                    bearer.calculateDimensions();
                }
                case DRAGON -> {
                    // :(
                    bearer.removeStatusEffect(StatusEffects.FIRE_RESISTANCE);
                    bearer.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 200, 0));
                    if (bearer instanceof PlayerEntity player) { // new curse. OBJECTIFY
                        Vec3d p = bearer.getPos();
                        bearer.getWorld().playSound(null, p.x, p.y, p.z, FamiliarSounds.CURSE_REMOVE, SoundCategory.PLAYERS, 0.5f, 0.7f);
                        player.playSoundToPlayer(FamiliarSounds.CURSE_REMOVE_PERSONAL, SoundCategory.PLAYERS, 0.7f, 0.7f);
                    }
                }
            }
        }

        public List<Text> requestForComment() {
            return switch(this) {
                case DRAGON -> draconicComments();
                default -> noComment();
            };
        }

        public void inflict(World world, LivingEntity bearer) {
            switch(this) {
                case DRAGON -> draconicInfliction(world, bearer);
                default -> noInfliction(world, bearer);
            }
        }

        public int maxUseTime() {
            return switch(this) {
                case DRAGON -> 62;
                default -> Integer.MAX_VALUE; // go for it. hold the odd trinket :)
            };
        }

        public int cooldown() {
            return switch(this) {
                case DRAGON -> 130;
                default -> 0;
            };
        }

        public static List<Text> noComment() {
            return List.of(); // nothing for you
        }

        public static void noInfliction(World world, LivingEntity bearer) {
            // yep thats right. still nothing.
        }

        public static List<Text> draconicComments() {
            // not yet sure what im doing with this actually since tooltip doesn't have player. but will consider
            return List.of();
        }

        public static void draconicInfliction(World world, LivingEntity bearer) {
            if (!world.isClient) {
                EntityDimensions sizey = bearer.getDimensions(bearer.getPose());
                float scaley = Math.max(sizey.height(), sizey.width()) / 1.8f;

                FireBreathEntity flameExclamationMark = new FireBreathEntity(bearer, world, scaley);
                flameExclamationMark.setVelocity(bearer, bearer.getPitch(), bearer.headYaw, 0, 0.5f * scaley, 3.1f);
                flameExclamationMark.setPosition(flameExclamationMark.getPos().add(bearer.getRotationVector().multiply(0.75 * scaley)).addRandom(bearer.getRandom(), 0.013f * scaley));
                world.spawnEntity(flameExclamationMark);

                Vec3d p = bearer.getPos();
                world.playSound(null, p.x, p.y, p.z, FamiliarSounds.CURSE_DRAGON_FIRE_BREATH, SoundCategory.PLAYERS, 0.2f, bearer.getRandom().nextFloat() * 0.13f + 1);
            }
        }

        // this class structure is so wrong for what i'm doing. i'll change it later when i have a better idea of what i want
        public static boolean shouldYip(LivingEntity entity) {
            return FamiliarAttachments.getCurse(entity).currentAffliction == FAMILIAR_BITE;
        }

        public static boolean shouldMaw(LivingEntity entity) {
            return FamiliarAttachments.getCurse(entity).currentAffliction == DRAGON && (entity.getMainHandStack().isEmpty() || entity.getMainHandStack().isOf(FamiliarItems.ODD_TRINKET)) && !entity.isInCreativeMode();
        }

        public static final List<RegistryEntry<StatusEffect>> YUM = List.of(
                StatusEffects.HASTE, StatusEffects.STRENGTH, StatusEffects.REGENERATION,
                StatusEffects.LUCK, StatusEffects.ABSORPTION
        );
        public static final List<RegistryEntry<StatusEffect>> YUCK = List.of(
                StatusEffects.DARKNESS, StatusEffects.BLINDNESS, StatusEffects.UNLUCK,
                StatusEffects.POISON, StatusEffects.WEAKNESS, StatusEffects.HUNGER, StatusEffects.SLOWNESS
        );

        public static String yipify(String chatText) {
            if (chatText.isEmpty() || chatText.charAt(0) == '\\') {
                return chatText;
            }

            StringBuilder yipping = new StringBuilder();
            char[] theYip = {'y', 'i', 'p'};
            Set<Character> allowable = Set.of('!', '?', '~', '.', ',', ' ');
            for (int i = 0, yipi = 0; i < chatText.length(); i++, yipi++) {
                char current = chatText.charAt(i);
                if (allowable.contains(current)) {
                    yipping.append(current);
                    yipi = 2; // effectively 0 after ++
                } else {
                    yipping.append(theYip[yipi % theYip.length]);
                }
            }
            return yipping.toString();
        }

        public static void simulateDraconicDigestion(ServerPlayerEntity player, Block eatenBlock) {
            // this is still a bit bland, but it's something at least. prob very subject to change
            // effects are chosen with a very very pseudo sort of random because.. i want it to be consistent but i don't wanna decide
            // however you kinda can decide for your own blocks if you alter the translation key. you shouldn't. that's an awful idea.
            if (eatenBlock.getDefaultState().isIn(FamiliarTags.ESPECIALLY_TASTY_FOR_DRAGONS)) {
                String thinger = eatenBlock.getTranslationKey();
                player.addStatusEffect(new StatusEffectInstance(YUM.get(thinger.charAt(thinger.length() - 1) % YUM.size()), 20 * player.getRandom().nextBetween(10, 60), MathHelper.floorLog2((int) eatenBlock.getHardness())));
            }
            if (eatenBlock.getDefaultState().isIn(FamiliarTags.ESPECIALLY_GROSS_FOR_DRAGONS)) {
                String thinger = eatenBlock.getTranslationKey();
                player.addStatusEffect(new StatusEffectInstance(YUCK.get(thinger.charAt(thinger.length() - 1) % YUCK.size()), 20 * player.getRandom().nextBetween(3, 30), MathHelper.floorLog2((int) eatenBlock.getHardness())));
            }
        }
    }

    // more to come :)
}
