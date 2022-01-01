package com.github.vindiagram.item;


import com.github.vindiagram.entity.PotatoProjectileEntity;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PotatoGunItem extends Item {

  // check if a stack is of potatoes and not empty.
  public static final Predicate<ItemStack> HAS_POTATOES = stack -> stack.isOf(Items.POTATO)
      && !stack.isEmpty();
  // flag key for potato gun ready state.
  private static final String READY_KEY = "PotatoGunReady";

  public PotatoGunItem() {
    super(
        new Item.Settings().maxCount(1).group(ItemGroup.COMBAT).maxDamage(1000).fireproof().rarity(
            Rarity.EPIC));
  }

  /**
   * Checks if the itemStack contains the "READY" flag.
   *
   * @param stack the itemStack to check.
   * @return true if contains "READY" flag, else false.
   */
  public static boolean isReady(ItemStack stack) {
    NbtCompound nbtCompound = stack.getNbt();
    return nbtCompound != null && nbtCompound.getBoolean(READY_KEY);
  }

  /**
   * Sets the "READY" flag on the itemStack.
   *
   * @param stack to set the "READY" flag.
   * @param ready value to set the "READY" flag to.
   */
  public static void setReady(ItemStack stack, boolean ready) {
    NbtCompound nbtCompound = stack.getOrCreateNbt();
    nbtCompound.putBoolean(READY_KEY, ready);
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

  /**
   * Check if the potato gun can be loaded (to prevent breaking).
   *
   * @param stack the potato gun
   * @return true if not at max damage.
   */
  public static boolean isUsable(ItemStack stack) {
    return stack.getDamage() < stack.getMaxDamage() - 1;
  }

  /**
   * Check if the user has potatoes.
   *
   * @param user to check for potatoes.
   * @return true if any of the following is true.
   * <br>* user in creative mode.
   * <br>* potatoes in opposite user hand.
   * <br>* potatoes in user inventory.
   */
  private ItemStack getPotatoes(PlayerEntity user) {
    // if not potatogun,
    // if creative, free potatoes.
    if (user.getAbilities().creativeMode) {
      return new ItemStack(Items.POTATO);
    }
    // check for potatoes in main hand.
    if (HAS_POTATOES.test(user.getStackInHand(Hand.MAIN_HAND))) {
      return user.getStackInHand(Hand.MAIN_HAND);
    }
    // check for potatoes in off hand.
    if (HAS_POTATOES.test(user.getStackInHand(Hand.OFF_HAND))) {
      return user.getStackInHand(Hand.OFF_HAND);
    }
    // check for potatoes in inventory.
    for (int i = 0; i < user.getInventory().size(); ++i) {
      if (HAS_POTATOES.test(user.getInventory().getStack(i))) {
        return user.getInventory().getStack(i);
      }
    }
    // no potatoes.
    return ItemStack.EMPTY;
  }

  /**
   * Load the potato gun.
   *
   * @param user performing the loading.
   * @return true if user is in creative mode or had at least one potato to load.
   */
  private boolean loadPotatoes(LivingEntity user) {
    boolean success = false;
    if (user instanceof PlayerEntity) {
      ItemStack potatoes = getPotatoes((PlayerEntity) user);
      if (!potatoes.isEmpty()) {
        potatoes.decrement(1);
        if (potatoes.isEmpty()) {
          ((PlayerEntity) user).getInventory().removeOne(potatoes);
        }
        success = true;
      }
    }
    return success;
  }

  @Override
  public boolean isUsedOnRelease(ItemStack stack) {
    return stack.isOf(this);
  }

  /* time it takes to reload. */
  @Override
  public int getMaxUseTime(ItemStack stack) {
    return 20;
  }

  /* material that can be used to repair the item. */
  @Override
  public boolean canRepair(ItemStack stack, ItemStack ingredient) {
    return ingredient.isOf(Items.LAPIS_BLOCK);
  }

  /* Called when user "left" clicks. */
  @Override
  public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
    // should be our potato gun.
    ItemStack itemStack = user.getStackInHand(hand);
    // check if ready to fire
    if (isReady(itemStack)) {
      // "fire when ready" - Grand Mof Tarkin
      shoot(world, user);
      // reset ready state.
      setReady(itemStack, false);
      // deal damage to our "weapon"
      itemStack.damage(1, user, e -> e.sendToolBreakStatus(user.getActiveHand()));
    }
    // check if user has potatoes to load, and not about to break.
    else if (isUsable(itemStack) && !getPotatoes(user).isEmpty()) {
      // set the current hand to start the player pose animation.
      user.setCurrentHand(hand);
    }
    // fail state.
    else {
      return TypedActionResult.fail(itemStack);
    }
    return TypedActionResult.consume(itemStack);
  }

  /* Called when user lets go of "left" click. */
  @Override
  public void onStoppedUsing(ItemStack stack, World world, LivingEntity user,
      int remainingUseTicks) {
    // calculate if loaded.
    boolean loaded =
        (float) (getMaxUseTime(stack) - remainingUseTicks) / getMaxUseTime(stack) >= 1.0f;
    // check if loaded, not ready, this if successfully load.
    if (loaded && !PotatoGunItem.isReady(stack) && loadPotatoes(user)) {
      // potato has been loaded and the weapon is ready.
      PotatoGunItem.setReady(stack, true);
      // play sound after load.
      world.playSound(null, user.getX(), user.getY(), user.getZ(),
          SoundEvents.ITEM_CROSSBOW_LOADING_END, SoundCategory.PLAYERS, 1.0f, 4.0f);
    }
  }

  @Override
  public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip,
      TooltipContext context) {
    Text projectileText =
        isReady(stack) ? new ItemStack(Items.POTATO).toHoverableText() : new LiteralText("[]");
    tooltip.add(new TranslatableText("item.minecraft.crossbow.projectile").append(" ")
        .append(projectileText));

    int damage = (int) ((stack.getMaxDamage() - stack.getDamage()) / (float) stack.getMaxDamage()
        * 100);

    StringBuilder builder = new StringBuilder();
    builder.append("Mana: ").append(damage).append("%");
    tooltip.add(new LiteralText(builder.toString()));
  }
}
