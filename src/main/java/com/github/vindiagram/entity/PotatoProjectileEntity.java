package com.github.vindiagram.entity;

import net.fabricmc.example.ExampleMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

/**
 * An {@link ThrownItemEntity} of {@link Items#POTATO}.
 * <br>
 * <br> On impact with a {@link LivingEntity} will add the status effects:
 * <br>* {@link StatusEffects#WEAKNESS}
 * <br>* {@link StatusEffects#SLOWNESS}
 */
public class PotatoProjectileEntity extends ThrownItemEntity {

  public PotatoProjectileEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
    super(entityType, world);
  }

  public PotatoProjectileEntity(World world, LivingEntity owner) {
    super(ExampleMod.POTATO_PROJECTILE, owner, world);
  }

  public PotatoProjectileEntity(World world, double x, double y, double z) {
    super(ExampleMod.POTATO_PROJECTILE, x, y, z, world);
  }

  @Override
  protected Item getDefaultItem() {
    return Items.POTATO;
  }

  @Override
  protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
    super.onEntityHit(entityHitResult);
    Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)
    if (entity instanceof LivingEntity livingEntity) { // checks if entity is an instance of LivingEntity (meaning it is not a boat or minecart)
      entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), 0.5f); // deals damage
      livingEntity.addStatusEffect((new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 3, 1)));
      livingEntity.addStatusEffect((new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 3, 1)));
      livingEntity.playSound(SoundEvents.ENTITY_PLAYER_HURT, 2F,
          1F); // plays a sound for the entity hit only
    }
  }

  @Override
  protected void onCollision(HitResult hitResult) { // called on collision with a block
    super.onCollision(hitResult);
    if (!this.world.isClient) { // checks if the world is client
      this.world.sendEntityStatus(this, (byte) 3); // particle?
      this.kill(); // kills the projectile
    }
  }
}