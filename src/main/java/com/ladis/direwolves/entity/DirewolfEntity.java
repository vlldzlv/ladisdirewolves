package com.ladis.direwolves.entity;

import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;
import com.ladis.direwolves.init.ModItems;
import com.ladis.direwolves.item.DirewolfTreatmentItem;
import com.ladis.direwolves.item.WhistleItem;

public class DirewolfEntity extends Wolf implements GeoEntity, PlayerRideableJumping {

    public static final int MAX_STAT_LEVEL = 10;
    public static final double BASE_MAX_HEALTH = 30.0D;
    public static final double BASE_ATTACK_DAMAGE = 7.0D;
    public static final double BASE_MOVEMENT_SPEED = 0.33D;
    public static final double BASE_ARMOR = 4.0D;
    public static final int DASH_COOLDOWN_TICKS = 45;
    public static final float JUMP_HEIGHT_MULTIPLIER = 1.6F;

    private static final EntityDataAccessor<Integer> DATA_TREATS =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_STRENGTH =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_VITALITY =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_AGILITY =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_PILLOW_PAW =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_IRON_HIDE =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<String> DATA_VARIANT =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> DATA_HAS_SADDLE =
            SynchedEntityData.defineId(DirewolfEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);
    private float playerJumpPendingScale;
    private int dashCooldown;
    private boolean wasSitting;
    private boolean sitUsesLie;

    protected static final RawAnimation SIT_ANIM = RawAnimation.begin().thenLoop("sit");
    protected static final RawAnimation LIE_ANIM = RawAnimation.begin().thenLoop("lie");
    protected static final RawAnimation STAY_ANIM = RawAnimation.begin().thenLoop("stay");
    protected static final RawAnimation WALK_ANIM = RawAnimation.begin().thenLoop("walk");
    protected static final RawAnimation RUN_ANIM = RawAnimation.begin().thenLoop("run");

    public DirewolfEntity(EntityType<? extends DirewolfEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TREATS, 0);
        builder.define(DATA_VARIANT, "default");
        builder.define(DATA_HAS_SADDLE, false);
        builder.define(DATA_STRENGTH, 0);
        builder.define(DATA_VITALITY, 0);
        builder.define(DATA_AGILITY, 0);
        builder.define(DATA_PILLOW_PAW, 0);
        builder.define(DATA_IRON_HIDE, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Wolf.createAttributes()
                .add(Attributes.MAX_HEALTH, BASE_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BASE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BASE_MOVEMENT_SPEED)
                .add(Attributes.ARMOR, BASE_ARMOR)
                .add(Attributes.FOLLOW_RANGE, 24.0D);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement", 5, this::movementController));
    }

    private <E extends DirewolfEntity> PlayState movementController(AnimationState<E> event) {
        boolean sitting = this.isInSittingPose() || this.isOrderedToSit();
        if (sitting) {
            if (!this.wasSitting) {
                this.sitUsesLie = this.getRandom().nextBoolean();
                this.wasSitting = true;
            }
            return event.setAndContinue(this.sitUsesLie ? LIE_ANIM : SIT_ANIM);
        }
        this.wasSitting = false;
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player && event.isMoving()) {
            return event.setAndContinue(RUN_ANIM);
        }
        if (event.isMoving()) {
            if (this.isSprinting()) {
                return event.setAndContinue(RUN_ANIM);
            }
            return event.setAndContinue(WALK_ANIM);
        }
        return event.setAndContinue(STAY_ANIM);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.geoCache;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(ModItems.WHISTLE.get())) {
            if (stack.getItem() instanceof WhistleItem whistle) {
                return whistle.openDirewolfMenu(player, this, hand);
            }
            return InteractionResult.CONSUME;
        }
        if (stack.is(ModItems.DIREWOLF_TREATMENT.get())) {
            if (stack.getItem() instanceof DirewolfTreatmentItem treatItem) {
                InteractionResult result = treatItem.feedDirewolf(player, this, stack);
                return result == InteractionResult.PASS ? InteractionResult.CONSUME : result;
            }
            return InteractionResult.CONSUME;
        }
        if (stack.is(ModItems.DIREWOLF_FOOD.get())) {
            if (this.isTame() && !this.isDeadOrDying() && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getMaxHealth());
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.CONSUME;
        }
        if (stack.is(net.minecraft.world.item.Items.SADDLE)) {
            if (this.isTame() && this.isOwnedBy(player)) {
                if (this.hasSaddle()) {
                    this.mountPlayer(player);
                    return InteractionResult.sidedSuccess(this.level().isClientSide);
                }
                this.equipSaddle(player, stack);
                return InteractionResult.sidedSuccess(this.level().isClientSide);
            }
            return InteractionResult.CONSUME;
        }
        if (stack.is(net.minecraft.world.item.Items.SHEARS)) {
            if (this.hasSaddle()) {
                this.removeSaddle(player);
                return InteractionResult.SUCCESS;
            }
            return super.mobInteract(player, hand);
        }
        if (stack.isEmpty() && this.hasSaddle() && this.isTame() && this.isOwnedBy(player)
                && !player.isPassenger() && !player.isShiftKeyDown()) {
            this.mountPlayer(player);
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, hand);
    }

    public void mountPlayer(Player player) {
        player.startRiding(this);
    }

    public String getColorVariant() {
        return this.getEntityData().get(DATA_VARIANT);
    }

    public void setColorVariant(String variant) {
        this.getEntityData().set(DATA_VARIANT, variant);
    }

    public boolean hasSaddle() {
        return this.getEntityData().get(DATA_HAS_SADDLE);
    }

    public void setHasSaddle(boolean hasSaddle) {
        this.getEntityData().set(DATA_HAS_SADDLE, hasSaddle);
    }

    public void equipSaddle(Player player, ItemStack stack) {
        this.getEntityData().set(DATA_HAS_SADDLE, true);
        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }
        this.playSound(SoundEvents.ARMOR_EQUIP_WOLF.value(), 1.0F, this.getVoicePitch());
    }

    public void removeSaddle(Player player) {
        this.getEntityData().set(DATA_HAS_SADDLE, false);
        ItemStack saddle = new ItemStack(net.minecraft.world.item.Items.SADDLE);
        if (!player.addItem(saddle)) {
            player.drop(saddle, false);
        }
        this.playSound(SoundEvents.ARMOR_UNEQUIP_WOLF, 1.0F, 1.0F);
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return SoundEvents.WOLF_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WOLF_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WOLF_DEATH;
    }

    public int getTreats() {
        return this.getEntityData().get(DATA_TREATS);
    }

    public int getStrengthLevel() {
        return this.getEntityData().get(DATA_STRENGTH);
    }

    public int getVitalityLevel() {
        return this.getEntityData().get(DATA_VITALITY);
    }

    public int getAgilityLevel() {
        return this.getEntityData().get(DATA_AGILITY);
    }

    public int getPillowPawLevel() {
        return this.getEntityData().get(DATA_PILLOW_PAW);
    }

    public int getIronHideLevel() {
        return this.getEntityData().get(DATA_IRON_HIDE);
    }

    public void setStrengthLevel(int level) {
        this.getEntityData().set(DATA_STRENGTH, level);
    }

    public void setVitalityLevel(int level) {
        this.getEntityData().set(DATA_VITALITY, level);
    }

    public void setAgilityLevel(int level) {
        this.getEntityData().set(DATA_AGILITY, level);
    }

    public void setPillowPawLevel(int level) {
        this.getEntityData().set(DATA_PILLOW_PAW, level);
    }

    public void setIronHideLevel(int level) {
        this.getEntityData().set(DATA_IRON_HIDE, level);
    }

    public boolean gainTreat(ItemStack stack) {
        if (this.level().isClientSide || this.isDeadOrDying()) {
            return false;
        }
        this.getEntityData().set(DATA_TREATS, this.getTreats() + 1);
        this.playSound(SoundEvents.FOX_EAT, 1.0F, this.getVoicePitch());
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new ItemParticleOption(ParticleTypes.ITEM, stack),
                    this.getX(), this.getY() + this.getBbHeight() * 0.8D, this.getZ(),
                    5, 0.1D, 0.1D, 0.1D, 0.03D
            );
        }
        return true;
    }

    public boolean tryUpgrade(Stat stat) {
        if (this.getTreats() <= 0 || stat.getLevel(this) >= MAX_STAT_LEVEL) {
            return false;
        }
        this.getEntityData().set(DATA_TREATS, this.getTreats() - 1);
        stat.addLevel(this);
        this.applyStatLevels();
        this.playSound(SoundEvents.PLAYER_LEVELUP, 1.0F, 1.0F);
        return true;
    }

    public void applyStatLevels() {
        var damage = this.getAttribute(Attributes.ATTACK_DAMAGE);
        if (damage != null) {
            damage.setBaseValue(BASE_ATTACK_DAMAGE + this.getStrengthLevel());
        }
        var health = this.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(BASE_MAX_HEALTH + 2.0D * this.getVitalityLevel());
        }
        var speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.setBaseValue(BASE_MOVEMENT_SPEED * (1.0D + 0.02D * this.getAgilityLevel()));
        }
        var armor = this.getAttribute(Attributes.ARMOR);
        if (armor != null) {
            armor.setBaseValue(BASE_ARMOR + 1.0D * this.getIronHideLevel());
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("Treats", this.getTreats());
        tag.putInt("StrLvl", this.getStrengthLevel());
        tag.putInt("VitLvl", this.getVitalityLevel());
        tag.putInt("AgiLvl", this.getAgilityLevel());
        tag.putInt("PawLvl", this.getPillowPawLevel());
        tag.putInt("HideLvl", this.getIronHideLevel());
        tag.putString("Variant", this.getColorVariant());
        tag.putBoolean("HasSaddle", this.hasSaddle());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.getEntityData().set(DATA_TREATS, tag.getInt("Treats"));
        this.getEntityData().set(DATA_STRENGTH, tag.getInt("StrLvl"));
        this.getEntityData().set(DATA_VITALITY, tag.getInt("VitLvl"));
        this.getEntityData().set(DATA_AGILITY, tag.getInt("AgiLvl"));
        this.getEntityData().set(DATA_PILLOW_PAW, tag.getInt("PawLvl"));
        this.getEntityData().set(DATA_IRON_HIDE, tag.getInt("HideLvl"));
        String variant = tag.getString("Variant");
        if (variant.isEmpty()) {
            variant = "default";
        }
        if (variant.equals("woods")) {
            variant = "forest";
        }
        this.getEntityData().set(DATA_VARIANT, variant);
        this.getEntityData().set(DATA_HAS_SADDLE, tag.getBoolean("HasSaddle"));
        this.applyStatLevels();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) && this.getPillowPawLevel() > 0) {
            float reduction = 0.05F * this.getPillowPawLevel();
            if (reduction >= 1.0F) {
                return false;
            }
            amount *= (1.0F - reduction);
        }
        boolean healedDamage = super.hurt(source, amount);
        if (healedDamage && !this.level().isClientSide && this.getIronHideLevel() > 0
                && source.getDirectEntity() instanceof LivingEntity attacker
                && this.getRandom().nextFloat() < 0.01F * this.getIronHideLevel()) {
            attacker.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 0));
            attacker.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));
        }
        return healedDamage;
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (this.dashCooldown > 0) {
            this.dashCooldown--;
        }
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            this.getNavigation().stop();
        }
    }

    @Override
    public LivingEntity getControllingPassenger() {
        if (!this.hasSaddle()) {
            return null;
        }
        return this.getFirstPassenger() instanceof LivingEntity living ? living : super.getControllingPassenger();
    }

    @Override
    protected Vec3 getPassengerAttachmentPoint(Entity entity, EntityDimensions dimensions, float partialTick) {
        Vec3 base = super.getPassengerAttachmentPoint(entity, dimensions, partialTick);
        Vec3 backOffset = new Vec3(0.0D, 0.0D, -0.4D)
                .yRot(-entity.getYRot() * (float) (Math.PI / 180.0F));
        return base.add(backOffset).add(0.0D, -0.25D, 0.0D);
    }

    @Override
    protected void addPassenger(Entity passenger) {
        super.addPassenger(passenger);
        if (this.isOrderedToSit()) {
            this.setOrderedToSit(false);
        }
    }

    @Override
    protected void tickRidden(Player player, Vec3 travelVector) {
        super.tickRidden(player, travelVector);
        this.setRot(player.getYRot(), player.getXRot() * 0.5F);
        this.yRotO = this.yBodyRot = this.yHeadRot = this.getYRot();
        if (this.onGround()) {
            if (this.playerJumpPendingScale > 0.0F) {
                this.executeRidersJump(this.playerJumpPendingScale, travelVector);
            }
            this.playerJumpPendingScale = 0.0F;
        }
    }

    @Override
    protected Vec3 getRiddenInput(Player player, Vec3 travelVector) {
        float strafe = player.xxa * 0.5F;
        float forward = player.zza;
        if (forward <= 0.0F) {
            forward *= 0.25F;
        }
        return new Vec3(strafe, travelVector.y, forward);
    }

    @Override
    protected float getRiddenSpeed(Player player) {
        float f = player.isSprinting() && this.getJumpCooldown() == 0 ? 0.1F : 0.0F;
        return (float) this.getAttributeValue(Attributes.MOVEMENT_SPEED) + f;
    }

    @Override
    public void jumpFromGround() {
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            return;
        }
        super.jumpFromGround();
        if (this.getAgilityLevel() > 0 && !this.isPassenger()) {
            Vec3 motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x, motion.y * (1.0D + 0.02D * this.getAgilityLevel()), motion.z);
        }
    }

    @Override
    public boolean canJump() {
        return this.hasSaddle();
    }

    @Override
    public boolean canSprint() {
        return true;
    }

    @Override
    public void onPlayerJump(int jumpPower) {
        if (!this.hasSaddle() || this.dashCooldown > 0 || !this.onGround()) {
            return;
        }
        this.playerJumpPendingScale = dashPowerToScale(jumpPower);
    }

    @Override
    public void handleStartJump(int jumpPower) {
        if (!this.hasSaddle() || this.dashCooldown > 0 || !this.onGround()) {
            return;
        }
        if (this.isVehicle() && this.getControllingPassenger() instanceof Player) {
            this.executeRidersJump(dashPowerToScale(jumpPower), Vec3.ZERO);
        }
    }

    @Override
    public void handleStopJump() {
    }

    private static float dashPowerToScale(int jumpPower) {
        if (jumpPower < 0) {
            return 0.0F;
        }
        if (jumpPower >= 90) {
            return 1.0F;
        }
        return 0.4F + 0.4F * (float) jumpPower / 90.0F;
    }

    @Override
    public int getJumpCooldown() {
        return this.dashCooldown;
    }

    public int getDashCooldown() {
        return this.dashCooldown;
    }

    public void setDashCooldown(int ticks) {
        this.dashCooldown = ticks;
    }

    protected void executeRidersJump(float scale, Vec3 travelVector) {
        this.setDeltaMovement(this.getDeltaMovement().x, JUMP_HEIGHT_MULTIPLIER * this.getJumpPower(scale), this.getDeltaMovement().z);
        this.setDashCooldown(DASH_COOLDOWN_TICKS);
        this.hasImpulse = true;
        if (travelVector.z > 0.0F) {
            float f = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
            float f1 = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
            this.setDeltaMovement(this.getDeltaMovement().add((double) (-0.4F * f * scale), 0.0, (double) (0.4F * f1 * scale)));
        }
        net.neoforged.neoforge.common.CommonHooks.onLivingJump(this);
    }

    public enum Stat {
        STRENGTH {
            @Override
            public int getLevel(DirewolfEntity direwolf) {
                return direwolf.getStrengthLevel();
            }

            @Override
            public void addLevel(DirewolfEntity direwolf) {
                direwolf.setStrengthLevel(direwolf.getStrengthLevel() + 1);
            }
        },
        VITALITY {
            @Override
            public int getLevel(DirewolfEntity direwolf) {
                return direwolf.getVitalityLevel();
            }

            @Override
            public void addLevel(DirewolfEntity direwolf) {
                direwolf.setVitalityLevel(direwolf.getVitalityLevel() + 1);
            }
        },
        AGILITY {
            @Override
            public int getLevel(DirewolfEntity direwolf) {
                return direwolf.getAgilityLevel();
            }

            @Override
            public void addLevel(DirewolfEntity direwolf) {
                direwolf.setAgilityLevel(direwolf.getAgilityLevel() + 1);
            }
        },
        PILLOW_PAW {
            @Override
            public int getLevel(DirewolfEntity direwolf) {
                return direwolf.getPillowPawLevel();
            }

            @Override
            public void addLevel(DirewolfEntity direwolf) {
                direwolf.setPillowPawLevel(direwolf.getPillowPawLevel() + 1);
            }
        },
        IRON_HIDE {
            @Override
            public int getLevel(DirewolfEntity direwolf) {
                return direwolf.getIronHideLevel();
            }

            @Override
            public void addLevel(DirewolfEntity direwolf) {
                direwolf.setIronHideLevel(direwolf.getIronHideLevel() + 1);
            }
        };

        public abstract int getLevel(DirewolfEntity direwolf);

        public abstract void addLevel(DirewolfEntity direwolf);
    }
}