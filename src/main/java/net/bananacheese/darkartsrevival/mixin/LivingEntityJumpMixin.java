package net.bananacheese.darkartsrevival.mixin;

import net.bananacheese.darkartsrevival.block.custom.CloudBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityJumpMixin {

    @Inject(method = "jump", at = @At("HEAD"))
    private void onJump(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Only handle players on the server side
        if (!(entity instanceof PlayerEntity player) || entity.getWorld().isClient) {
            return;
        }

        // Check the block directly below the player's feet
        BlockPos playerPos = player.getBlockPos();
        BlockPos belowPos = playerPos.down();

        // Check if standing on a cloud block
        if (player.getWorld().getBlockState(belowPos).getBlock() instanceof CloudBlock) {
            // Also verify player is on the ground (not jumping through from below)
            if (player.isOnGround()) {
                CloudBlock.onPlayerJump(player.getWorld(), belowPos, player);
            }
        }
    }
}