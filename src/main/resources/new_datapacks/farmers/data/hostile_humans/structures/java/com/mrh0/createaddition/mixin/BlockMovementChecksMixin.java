package com.mrh0.createaddition.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//import com.mrh0.createaddition.blocks.modular_accumulator.ModularAccumulatorBlock;
import com.simibubi.create.api.connectivity.ConnectivityHandler;
import com.simibubi.create.api.contraption.BlockMovementChecks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(value = BlockMovementChecks.class, remap = false)
public class BlockMovementChecksMixin {

//	@Inject(
//			method = "isBlockAttachedTowardsFallback",
//			at = @At("HEAD"),
//			cancellable = true
//	)
//	private static void isBlockAttachedTowardsFallback(BlockState state, Level world, BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> info) {
//		if (state.getBlock() instanceof ModularAccumulatorBlock) {
//			info.setReturnValue(ConnectivityHandler.isConnected(world, pos, pos.relative(direction)));
//		}
//	}

}
