package io.github.noskinbadname.opsponges.mixin;

import io.github.noskinbadname.opsponges.DelayedAction;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin {
	/**
	 * @author NoSkinBadName
	 * @reason Removed annoying Sponge-Limits
	 */
	@Overwrite
	private boolean removeWaterBreadthFirstSearch(Level world, BlockPos pos) {
		boolean waterDried = false;
		BlockPos[] blocksNearby = {
				pos.above(),
				pos.below(),
				pos.north(),
				pos.east(),
				pos.south(),
				pos.west()
		};
		for (BlockPos blockPos: blocksNearby) {
			BlockState blockState = world.getBlockState(blockPos);
			FluidState fluidState = world.getFluidState(blockPos);
			if (fluidState.is(FluidTags.WATER)) {
				Block block = blockState.getBlock();
				if (block instanceof BucketPickup fluidDrainable && !fluidDrainable.pickupBlock(null, world, blockPos, blockState).isEmpty()) {
					waterDried = true;
				} else if (block instanceof LiquidBlock) {
					world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
					waterDried = true;
				} else {
					if (!blockState.is(Blocks.KELP) && !blockState.is(Blocks.KELP_PLANT) && !blockState.is(Blocks.SEAGRASS) && !blockState.is(Blocks.TALL_SEAGRASS)) {
						continue;
					}

					BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
					Block.dropResources(blockState, world, blockPos, blockEntity);
					world.setBlock(blockPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
					waterDried = true;
				}
				new DelayedAction(4, () -> removeWaterBreadthFirstSearch(world, blockPos));
			}
		}
		return waterDried;
	}
}