package io.github.noskinbadname.opsponges.mixin;

import io.github.noskinbadname.opsponges.DelayedAction;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(SpongeBlock.class)
public abstract class SpongeBlockMixin {
	/**
	 * @author NoSkinBadName
	 * @reason Removed annoying Sponge-Limits
	 */
	@Overwrite
	private boolean absorbWater(World world, BlockPos pos) {
		boolean waterDried = false;
		BlockPos[] blocksNearby = {
				pos.up(),
				pos.down(),
				pos.north(),
				pos.east(),
				pos.south(),
				pos.west()
		};
		for (BlockPos blockPos: blocksNearby) {
			BlockState blockState = world.getBlockState(blockPos);
			FluidState fluidState = world.getFluidState(blockPos);
			if (fluidState.isIn(FluidTags.WATER)) {
				Block block = blockState.getBlock();
				if (block instanceof FluidDrainable fluidDrainable && !fluidDrainable.tryDrainFluid(null, world, blockPos, blockState).isEmpty()) {
					waterDried = true;
				} else if (block instanceof FluidBlock) {
					world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					waterDried = true;
				} else {
					if (!blockState.isOf(Blocks.KELP) && !blockState.isOf(Blocks.KELP_PLANT) && !blockState.isOf(Blocks.SEAGRASS) && !blockState.isOf(Blocks.TALL_SEAGRASS)) {
						continue;
					}

					BlockEntity blockEntity = blockState.hasBlockEntity() ? world.getBlockEntity(blockPos) : null;
					Block.dropStacks(blockState, world, blockPos, blockEntity);
					world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL);
					waterDried = true;
				}
				new DelayedAction(5, () -> absorbWater(world, blockPos));
			}
		}
		return waterDried;
	}
}