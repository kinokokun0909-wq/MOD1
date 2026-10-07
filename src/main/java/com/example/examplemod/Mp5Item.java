package com.example.examplemod;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/** Server-authoritative hitscan prototype: hold right click to fire. */
public final class Mp5Item extends Item {
    private static final double RANGE = 64.0D;
    private static final float DAMAGE = 3.0F;
    private static final int FIRE_INTERVAL_TICKS = 2;
    private static final ResourceKey<DamageType> BULLET = ResourceKey.create(
            Registries.DAMAGE_TYPE, new ResourceLocation(ExampleMod.MODID, "mp5_bullet"));

    public Mp5Item() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player,
                                                  InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }
        player.startUsingItem(hand);
        if (level instanceof ServerLevel server) fire(server, player);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public void onUseTick(Level level, LivingEntity user, ItemStack stack,
                          int remainingUseTicks) {
        if (level instanceof ServerLevel server && user instanceof Player player
                && !player.getCooldowns().isOnCooldown(this)) {
            fire(server, player);
        }
    }

    private void fire(ServerLevel level, Player player) {
        player.getCooldowns().addCooldown(this, FIRE_INTERVAL_TICKS);
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle();
        Vec3 maximumEnd = start.add(direction.scale(RANGE));
        var blockHit = level.clip(new ClipContext(start, maximumEnd,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = blockHit.getType() == HitResult.Type.MISS
                ? maximumEnd : blockHit.getLocation();
        double nearestDistance = start.distanceToSqr(end);
        Entity target = null;

        AABB search = player.getBoundingBox().expandTowards(direction.scale(RANGE))
                .inflate(1.0D);
        for (Entity candidate : level.getEntities(player, search,
                entity -> entity.isAlive() && entity.isPickable() && !entity.isSpectator()
                        && !entity.isPassengerOfSameVehicle(player))) {
            AABB bounds = candidate.getBoundingBox().inflate(candidate.getPickRadius());
            Vec3 point = bounds.contains(start) ? start : bounds.clip(start, end).orElse(null);
            if (point == null) continue;
            double distance = start.distanceToSqr(point);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                end = point;
                target = candidate;
            }
        }

        boolean damaged = false;
        if (target != null) {
            var type = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(BULLET);
            damaged = target.hurt(new DamageSource(type, player, player), DAMAGE);
        }

        // Keep the counter on the player: changing the held stack's NBT each shot
        // would send inventory updates while the player is using the gun.
        int previousShots = player.getPersistentData().getInt("mod1_mp5_shots");
        int shots = previousShots < 0 || previousShots == Integer.MAX_VALUE
                ? 1 : previousShots + 1;
        player.getPersistentData().putInt("mod1_mp5_shots", shots);
        var feedback = target == null
                ? Component.translatable(blockHit.getType() == HitResult.Type.MISS
                        ? "message.mod1.mp5.miss" : "message.mod1.mp5.block")
                        .withStyle(ChatFormatting.GRAY)
                : Component.translatable(damaged
                        ? "message.mod1.mp5.hit" : "message.mod1.mp5.no_damage",
                        target.getDisplayName())
                        .withStyle(damaged ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
        if (damaged && target instanceof LivingEntity living) {
            feedback.append(Component.translatable("message.mod1.mp5.health",
                    Math.round(Math.max(0.0F, living.getHealth()) * 10.0F) / 10.0F));
        }
        player.displayClientMessage(Component.translatable("message.mod1.mp5.fired", shots)
                .withStyle(ChatFormatting.GOLD).append(" ｜ ").append(feedback), true);

        Vec3 muzzle = start.add(direction.scale(0.65D));
        level.playSound(null, player.getX(), player.getEyeY(), player.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.PLAYERS, 0.45F, 1.85F);
        level.sendParticles(ParticleTypes.SMOKE, muzzle.x, muzzle.y, muzzle.z,
                3, 0.025D, 0.025D, 0.025D, 0.005D);

        double length = start.distanceTo(end);
        for (double distance = 0.75D; distance < length; distance += 2.0D) {
            Vec3 point = start.add(direction.scale(distance));
            level.sendParticles(ParticleTypes.CRIT, point.x, point.y, point.z,
                    1, 0.0D, 0.0D, 0.0D, 0.0D);
        }
        level.sendParticles(ParticleTypes.CRIT, end.x, end.y, end.z,
                5, 0.05D, 0.05D, 0.05D, 0.02D);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip,
                                TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.mod1.mp5"));
    }
}
