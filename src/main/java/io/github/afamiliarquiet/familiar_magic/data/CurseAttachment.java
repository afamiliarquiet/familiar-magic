package io.github.afamiliarquiet.familiar_magic.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.afamiliarquiet.familiar_magic.entity.FireBreathEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
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
                default -> noComment();
                case DRAGON -> draconicComments();
            };
        }

        public void inflict(World world, LivingEntity bearer) {
            switch(this) {
                default -> noInfliction(world, bearer);
                case DRAGON -> draconicInfliction(world, bearer);
            }
        }

        public int maxUseTime() {
            return switch(this) {
                default -> Integer.MAX_VALUE; // go for it. hold the odd trinket :)
                case DRAGON -> 62;
            };
        }

        public int cooldown() {
            return switch(this) {
                default -> 0;
                case DRAGON -> 130;
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
                flameExclamationMark.setPosition(flameExclamationMark.getPos().add(bearer.getRotationVector().multiply(0.5 * scaley)).addRandom(bearer.getRandom(), 0.013f * scaley));
                world.spawnEntity(flameExclamationMark);

                Vec3d p = bearer.getPos();
                world.playSound(null, p.x, p.y, p.z, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS, 0.2f, bearer.getRandom().nextFloat() * 0.13f + 1);
            }
        }

        public static boolean shouldMaw(LivingEntity entity) {
            return FamiliarAttachments.getCurse(entity).currentAffliction == DRAGON && entity.getMainHandStack().isEmpty();
        }
    }

    // more to come :)
}
