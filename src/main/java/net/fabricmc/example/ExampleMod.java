package net.fabricmc.example;


import com.github.vindiagram.item.PotatoGunItem;
import com.github.vindiagram.entity.PotatoProjectileEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExampleMod implements ModInitializer {
    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LogManager.getLogger("modid");
    public static final Item POTATO_GUN = new PotatoGunItem();
    public static ToolItem AMETHYST_SWORD = new SwordItem(AmesthystToolMaterial.INSTANCE, 10, -1F, new Item.Settings().group(ItemGroup.COMBAT));

    // Create a potato as a projectile.
    public static final EntityType<PotatoProjectileEntity> POTATO_PROJECTILE = FabricEntityTypeBuilder
            .<PotatoProjectileEntity>create(SpawnGroup.MISC, PotatoProjectileEntity::new) // create new projectile
            .dimensions(EntityDimensions.fixed(0.25F, 0.25F)) // dimensions in Minecraft units of the projectile
            .trackRangeBlocks(4).trackedUpdateRate(10) // necessary for all thrown projectiles (as it prevents it from breaking, lol)
            .build(); // Create the entity

    @Override
    public void onInitialize() {
        // Register custom items.
        Registry.register(Registry.ITEM, new Identifier("tutorial", "amethyst-sword"), AMETHYST_SWORD);
        Registry.register(Registry.ITEM, new Identifier("potato-gun", "potato-gun"), POTATO_GUN);

        // Register custom entities.
        Registry.register(Registry.ENTITY_TYPE, new Identifier("potato-gun", "potato_gun_projectile"), POTATO_PROJECTILE);
    }

}
