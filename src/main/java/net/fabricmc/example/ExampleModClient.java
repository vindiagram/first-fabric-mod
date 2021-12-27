package net.fabricmc.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

/**
 * Initializer for client side only modifications.
 */
public class ExampleModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register our potato projectile, so it will be rendered by the client when flying through the air.
        EntityRendererRegistry.register(ExampleMod.POTATO_PROJECTILE, FlyingItemEntityRenderer::new);
    }
}
