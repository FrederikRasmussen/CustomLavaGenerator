package customlavagenerator.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStone;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class LavaFlowEvents {
  

  public static final String[] DEFAULT_BLOCK_CONFIG = new String[]{
    "minecraft:cobblestone",
    "minecraft:cobblestone",
    "minecraft:stone:3",
    "minecraft:stone:3",
    "minecraft:stone:1",
    "minecraft:stone:1",
    "minecraft:stone:5",
    "minecraft:stone:5"};
  private static List<Supplier<IBlockState>> blocks;

  public static void updateConfig(String[] blockConfig) {
    blocks = new ArrayList<>();
    for (String blockName : blockConfig) {
      String[] splitName = blockName.split(":");
      int meta;
      if (splitName.length == 2) {
        meta = 0;
      } else if (splitName.length == 3) {
        meta = Integer.parseInt(splitName[2]);
      } else {
        throw new RuntimeException("Bad blockname in config: " + blockName);
      }
      blocks.add(() -> getBlockStateFromBlockAndMeta(
        Block.getBlockFromName(splitName[0] + ":" + splitName[1]),
        meta));
    }
  }

  private static IBlockState getBlockStateFromBlockAndMeta(Block block, int meta) {
    return block.getStateFromMeta(meta);
  }

  @SubscribeEvent(priority = EventPriority.HIGH)
  public void lavaGeneration(BlockEvent.FluidPlaceBlockEvent event) {
    if (Blocks.FLOWING_LAVA == event.getOriginalState().getBlock()) {
      World world = event.getWorld();
      BlockPos lavaPosition = event.getLiquidPos();
      if (0 == getBlockMeta(world, lavaPosition)) {
        return;
      }
      findWater(
        world,
        lavaPosition.north(),
        lavaPosition.east(),
        lavaPosition.south(),
        lavaPosition.west())
          .map(position -> getBlockMeta(world, position))
          //.ifPresent(level -> Block.getBlockFromName("minecraft:stone").getStateFromMeta(5));
          .ifPresent(level -> event.setNewState(blocks.get(level).get()));
          //.ifPresent(blockMeta -> placeBlock(world, event, blockMeta));
    }
  }

  private void placeBlock(World world, BlockEvent.FluidPlaceBlockEvent event, int blockMeta) {
    switch (blockMeta) {
      case 2:
      case 3:
        event.setNewState(
          Blocks.STONE
              .getDefaultState()
              .withProperty(BlockStone.VARIANT, BlockStone.EnumType.DIORITE));
        break;
      case 4:
      case 5:
        event.setNewState(
          Blocks.STONE
              .getDefaultState()
              .withProperty(BlockStone.VARIANT, BlockStone.EnumType.GRANITE));
        break;
      case 6:
      case 7:
        event.setNewState(
          Blocks.STONE
              .getDefaultState()
              .withProperty(BlockStone.VARIANT, BlockStone.EnumType.ANDESITE));
        break;
      default:
        event.setNewState(
          Blocks.COBBLESTONE
              .getDefaultState());
    }
  }

  private int getBlockMeta(World world, BlockPos position) {
    return world
        .getBlockState(position)
        .getValue(BlockFluidBase.LEVEL);
  }

  private Optional<BlockPos> findWater(World world, BlockPos... positions) {
    for (BlockPos position : positions) {
      if (Material.WATER == getMaterialForPosition(world, position)) {
        return Optional.of(position);
      }
    }
    return Optional.empty();
  }

  private Material getMaterialForPosition(World world, BlockPos position) {
    return world
        .getBlockState(position)
        .getMaterial();
  }
}