package com.github.vindiagram.item;


import com.github.vindiagram.entity.PotatoProjectileEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class PotatoGunItem extends Item {

  public PotatoGunItem() {
    super(new Item.Settings().group(ItemGroup.COMBAT).maxDamage(100));
  }

  /**
   * Fires a potato
   *
   * @param world  the type of world.
   * @param entity the entity performing the action.
   */
  private static void shoot(World world, LivingEntity entity) {
    PotatoProjectileEntity potato = new PotatoProjectileEntity(world, entity);
    potato.setVelocity(entity.getRotationVector().x, entity.getRotationVector().y,
        entity.getRotationVector().z, 5.0f, 1);
    world.spawnEntity(potato);
    world.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
        SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS, 1, 10);
  }

  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    user.setCurrentHand(hand);
    return TypedActionResult.pass(user.getStackInHand(hand));
  }

  @Override
  public void onStoppedUsing(ItemStack itemstack, World world, LivingEntity entityLiving,
      int timeLeft) {
    if (!world.isClient() && entityLiving instanceof ServerPlayerEntity entity) {

      // fetch a stack of potatoes
      ItemStack stack = entity.getInventory().main.stream()
          .filter(teststack -> teststack.getItem() == Items.POTATO).findFirst()
          .orElse(ItemStack.EMPTY);

      // if creative or we have potatoes
      if (entity.getAbilities().creativeMode || stack != ItemStack.EMPTY) {
        // "fire when ready" - Grand Mof Tarkin
        shoot(world, entity);
        // deal damage to our "weapon"
        itemstack.damage(1, entity, e -> e.sendToolBreakStatus(entity.getActiveHand()));
        // if not in creative then consume a potato from the stack.
        if (!entity.getAbilities().creativeMode) {
          stack.decrement(1);
          // if the stack is empty then remove it from the inventory.
          if (stack.isEmpty()) {
            entity.getInventory().removeOne(stack);
          }
        }
      }
    }
  }
}
