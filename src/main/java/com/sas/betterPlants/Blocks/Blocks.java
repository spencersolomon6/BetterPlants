package com.sas.betterPlants.Blocks;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.*;

@ObjectHolder("betterPlants")
public class Blocks {

    private static final DeferredRegister<Block> BLOCK_REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, "betterPlants");

    public static final Block BETTER_CARROT = register("betterCarrot", new BetterCarrotBlock(BlockBehaviour.Properties.of(Material.PLANT).noCollission().randomTicks().instabreak().sound(SoundType.CROP))).get();

    private static RegistryObject<Block> register(String blockName, Block block) {
        return BLOCK_REGISTRY.register(blockName, () -> block);
    }

    public static void rebuildCache() {
        Block.BLOCK_STATE_REGISTRY.forEach(BlockBehaviour.BlockStateBase::initCache);
    }
}
