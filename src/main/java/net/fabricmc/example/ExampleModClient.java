package net.fabricmc.example;

import com.github.vindiagram.item.PotatoGunItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.object.builder.v1.client.model.FabricModelPredicateProviderRegistry;
import net.minecraft.client.item.UnclampedModelPredicateProvider;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Initializer for client side only modifications.
 */
public class ExampleModClient implements ClientModInitializer {

  private float loadPotatoGun(ItemStack stack, ClientWorld world, LivingEntity entity, int i) {
    return entity == null || PotatoGunItem.isReady(stack) ? 0.0f
        : (stack.getMaxUseTime() - entity.getItemUseTimeLeft()) / stack.getMaxUseTime();
  }

  private float loadingPotatoGun(ItemStack stack, ClientWorld world, LivingEntity entity, int i) {
    return entity != null && entity.isUsingItem() && entity.getActiveItem() == stack
        && !PotatoGunItem.isReady(stack) ? 1.0f : 0.0f;
  }

  private float readyPotatoGun(ItemStack stack, ClientWorld world, LivingEntity entity, int i) {
    return entity != null && PotatoGunItem.isReady(stack) ? 1.0f : 0.0f;
  }

  private float brokenPotatoGun(ItemStack stack, ClientWorld world, LivingEntity entity, int i) {
    return entity != null && PotatoGunItem.isUsable(stack) ? 0.0f : 1.0f;
  }

  /**
   * Register a predicate for "transforming" models.
   *
   * @param item       the item to register for.
   * @param identifier the unique "override" identifier.
   * @param provider   the predicate provider.
   */
  private void registerModelPredicate(Item item, String identifier,
      UnclampedModelPredicateProvider provider) {
    FabricModelPredicateProviderRegistry.register(item, new Identifier(identifier), provider);
  }

  @Override
  public void onInitializeClient() {
    // Register our potato projectile, so it will be rendered by the client when flying through the air.
    EntityRendererRegistry.register(ExampleMod.POTATO_PROJECTILE, FlyingItemEntityRenderer::new);

    /*
    Potato Gun "Override" flags
    <br> load - defines if loading
    <br> loading - defines loading progress
    <br> ready - defines if ready to fire.
    <br> broken - defines if in broken state.
     */
    registerModelPredicate(ExampleMod.POTATO_GUN, "load", this::loadPotatoGun);
    registerModelPredicate(ExampleMod.POTATO_GUN, "loading", this::loadingPotatoGun);
    registerModelPredicate(ExampleMod.POTATO_GUN, "ready", this::readyPotatoGun);
    registerModelPredicate(ExampleMod.POTATO_GUN, "broken", this::brokenPotatoGun);
  }


}
