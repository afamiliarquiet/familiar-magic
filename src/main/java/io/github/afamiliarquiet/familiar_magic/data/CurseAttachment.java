package io.github.afamiliarquiet.familiar_magic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.afamiliarquiet.familiar_magic.FamiliarSounds;
import io.github.afamiliarquiet.familiar_magic.entity.FireBreathEntity;
import io.github.afamiliarquiet.familiar_magic.item.FamiliarItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public record CurseAttachment(Curse currentAffliction) {
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

    @SuppressWarnings("SwitchStatementWithTooFewBranches") // i don't care it'll change later probably
    public enum Curse {
        NOTHING,
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
