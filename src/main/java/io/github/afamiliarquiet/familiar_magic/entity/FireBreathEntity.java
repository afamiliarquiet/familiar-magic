package io.github.afamiliarquiet.familiar_magic.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FireBreathEntity extends ThrownEntity {
    private static final int MAX_AGE = 13;
    private static final TrackedData<List<ParticleEffect>> POTION_SWIRLS = DataTracker.registerData(FireBreathEntity.class, TrackedDataHandlerRegistry.PARTICLE_LIST);
    private static final TrackedData<Float> BREATH_SIZE_SCALE = DataTracker.registerData(FireBreathEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private final Collection<StatusEffectInstance> statusEffects;

    protected FireBreathEntity(EntityType<? extends ThrownEntity> entityType, World world) {
        super(entityType, world);
        this.statusEffects = new ArrayList<>();
        this.setScale(1f);
    }

    public FireBreathEntity(LivingEntity owner, World world, double scale) {
        super(FamiliarEntities.FIRE_BREATH_TYPE, owner, world);
        this.statusEffects = /*new ArrayList<>(*/owner.getStatusEffects();
                //.stream().filter((statusEffect)-> !(statusEffect.getEffectType().matchesId(MawEntities.DRACONIC_OMEN_STATUS_EFFECT_ID)))
                //.toList());
        updateSwirls();
        this.setScale((float) scale);
    }

    private void updateSwirls() {
        List<ParticleEffect> list = this.statusEffects.stream().filter(StatusEffectInstance::shouldShowParticles).map(StatusEffectInstance::createParticle).toList();
        this.dataTracker.set(POTION_SWIRLS, list);
    }

    private void setScale(float scale) {
        this.dataTracker.set(BREATH_SIZE_SCALE, scale);
    }

    private float getScale() {
        return this.dataTracker.get(BREATH_SIZE_SCALE);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if (nbt.contains("active_effects", 9)) {
            NbtList nbtList = nbt.getList("active_effects", 10);

            for(int i = 0; i < nbtList.size(); ++i) {
                NbtCompound nbtCompound = nbtList.getCompound(i);
                StatusEffectInstance statusEffectInstance = StatusEffectInstance.fromNbt(nbtCompound);
                if (statusEffectInstance != null) {
                    this.statusEffects.add(statusEffectInstance);
                }
            }

            updateSwirls();
        }
        this.setScale(nbt.getFloat("breath_size_scale"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        if (!this.statusEffects.isEmpty()) {
            NbtList nbtList = new NbtList();

            for (StatusEffectInstance statusEffectInstance : this.statusEffects) {
                nbtList.add(statusEffectInstance.writeNbt());
            }

            nbt.put("active_effects", nbtList);
        }
        nbt.putFloat("breath_size_scale", this.getScale());
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {
        builder.add(POTION_SWIRLS, List.of());
        builder.add(BREATH_SIZE_SCALE, 1.0f);
    }

    @Override
    public void tick() {
        super.tick();

        // friction copied from fire particle.. its ok :thumbsup:
        // also means that gravity has a little more takeover power, as original v will be fricted but gravity keep goin
        this.setVelocity(this.getVelocity().multiply(0.96));
        this.calculateDimensions();

        if (this.getWorld().isClient) {
            List<ParticleEffect> particles = this.dataTracker.get(POTION_SWIRLS);
            if (!particles.isEmpty()) {
                if (this.random.nextInt(13) == 0) {
                    this.getWorld().addParticle(Util.getRandom(particles, this.random),
                            this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5),
                            0, 0.13, 0);
                }
            }

            Vec3d p = this.getPos();
            this.getWorld().playSound(null, p.x, p.y, p.z,
                    SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.PLAYERS,
                    0.5f, (this.getRandom().nextFloat() * 0.1f + 0.4f));
        }

        if (this.age > MAX_AGE) {
            this.discard();
        }
    }

    @Override
    public double getGravity() {
        return -0.015 * this.getScale();
    }

    @Override
    public boolean isOnFire() {
        // why bother with particles when you can just burn the thing that actually does collisions and get free fire?
        //return true;
        return !this.isSubmergedInWater();
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        // todo - maybe add a blacklist of entities already hit like areaeffectcloud. idk if that'd really be very helpful tho
        super.onEntityHit(entityHitResult);

        if (this.getWorld().isClient()) {
            return;
        }

        Entity entity = entityHitResult.getEntity();

        // these flames don't seem to respect pvp by default (i hoped extending projectile would do that)
        // so we be safe and respectful by checking here and stopping if requested :)
        // (including not burning pets because burning pets is bad and this is just for a fest
        // (tho it sounds like pvp will be on for the fest so this won't matter but whatever!!!
        // the point is you shouldn't try to burn pets. i could just make that always the case actually but.. ehhhh. eh.)
        // ok well it looks like i did eventually make it so pets never get harmed. maybe. cool!
        boolean beNice = (!(entity.getWorld().getServer() != null && entity.getWorld().getServer().isPvpEnabled()) &&
                entity instanceof PlayerEntity ||
                entity instanceof TameableEntity possiblePet && possiblePet.isTamed());

        // dunno if these checks for fire/splash immunity are necessary but..
        // it's good to be respectful to the entity's wishes anyway
        // would need to create my own damage type to properly add the attacker to the target's lastDamageSource
        if (!entity.isFireImmune()) {
            entity.setOnFireForTicks(beNice ? 13 : 20);
        }

        if (entity instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityHitResult.getEntity();

            if (this.statusEffects != null && livingEntity.isAffectedBySplashPotions() && !livingEntity.isFireImmune()) {
                // splat on a copy of every status effect we can, except for the fiery stuff (it has combusted)
                // maybe source should be the owner of this instead? idk
                for (StatusEffectInstance statusEffect : this.statusEffects) {
                    if (livingEntity.canHaveStatusEffect(statusEffect) &&
                            !(beNice && statusEffect.getEffectType().value().getCategory().equals(StatusEffectCategory.HARMFUL))) {
                        livingEntity.addStatusEffect(new StatusEffectInstance(statusEffect), this);
                    }
                }
            }
        }
    }

    @Override
    public EntityDimensions getDimensions(EntityPose pose) {
        // bigger... BIGGER (vwoosh)
        float agePercent = this.age / (float) MAX_AGE;
        float size = this.getScale() * (agePercent * agePercent + 0.05f);
        return EntityDimensions.changing(size, size);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);

        // better than going through blocks, but still not super nice looking..
        // what i'd really like is to just kill the velocity in collision direction, but that seems like a lot.
        if (!this.getWorld().isClient && hitResult.getType().equals(HitResult.Type.BLOCK)) {
            this.discard();
        }
    }

    public static FireBreathEntity create(EntityType<? extends ThrownEntity> entity, World world) {
        return new FireBreathEntity(entity, world);
    }
}
