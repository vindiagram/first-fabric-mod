package com.github.vindiagram.mixin;


import com.github.vindiagram.item.PotatoGunItem;
import net.fabricmc.example.ExampleMod;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class WeaponRenderingMixin {

  @Inject(method = "getArmPose", at = @At(value = "TAIL"), cancellable = true)
  private static void tryItemPose(AbstractClientPlayerEntity player, Hand hand,
      CallbackInfoReturnable<BipedEntityModel.ArmPose> ci) {
    ItemStack itemStack = player.getStackInHand(hand);
    // Handle player pose when active hand contains the potato gun.
    if (player.getActiveHand() == hand && itemStack.isOf(ExampleMod.POTATO_GUN)) {
      // while the potatogun is loading, show the CROSSBOW_CHARGE pose.
      if (player.getItemUseTimeLeft() > 0) {
        ci.setReturnValue(ArmPose.CROSSBOW_CHARGE);
      }
      // while the potatogun is loaded, show the CROSSBOW_HOLD pose
      if (PotatoGunItem.isReady(itemStack)){
        ci.setReturnValue(ArmPose.CROSSBOW_HOLD);
      }
    }
  }
}