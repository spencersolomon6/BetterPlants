package com.sas.betterPlants.blocks;

import com.sas.betterPlants.customLogic.EvolutionaryPlant;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BetterCarrotBlock extends EvolutionaryPlant {
    public static final int MAX_AGE = 7;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_7;
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D), Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)};


    public BetterCarrotBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), Integer.valueOf(0)));
    }

    protected ItemLike getBaseSeedId() {
        return Items.CARROT;
    }

    public VoxelShape getShape(BlockState p_51330_, BlockGetter p_51331_, BlockPos p_51332_, CollisionContext p_51333_) {
        return SHAPE_BY_AGE[p_51330_.getValue(this.getAgeProperty())];
    }

    protected boolean mayPlaceOn(BlockState p_52302_, BlockGetter p_52303_, BlockPos p_52304_) {
        return p_52302_.is(Blocks.FARMLAND);
    }

    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    public int getMaxAge() {
        return 7;
    }

    protected int getAge(BlockState p_52306_) {
        return p_52306_.getValue(this.getAgeProperty());
    }

    public BlockState getStateForAge(int p_52290_) {
        return this.defaultBlockState().setValue(this.getAgeProperty(), Integer.valueOf(p_52290_));
    }

    public boolean isMaxAge(BlockState p_52308_) {
        return p_52308_.getValue(this.getAgeProperty()) >= this.getMaxAge();
    }

    public boolean isRandomlyTicking(BlockState p_52288_) {
        return !this.isMaxAge(p_52288_);
    }

    public void randomTick(BlockState p_52292_, ServerLevel p_52293_, BlockPos p_52294_, Random p_52295_) {
        if (!p_52293_.isAreaLoaded(p_52294_, 1)) return; // Forge: prevent loading unloaded chunks when checking neighbor's light
        if (p_52293_.getRawBrightness(p_52294_, 0) >= 9) {
            int i = this.getAge(p_52292_);
            if (i < this.getMaxAge()) {
                float f = getGrowthSpeed(this, p_52293_, p_52294_);
                if (net.minecraftforge.common.ForgeHooks.onCropsGrowPre(p_52293_, p_52294_, p_52292_, p_52295_.nextInt((int)(25.0F / f) + 1) == 0)) {
                    p_52293_.setBlock(p_52294_, this.getStateForAge(i + 1), 2);
                    net.minecraftforge.common.ForgeHooks.onCropsGrowPost(p_52293_, p_52294_, p_52292_);
                }
            }
        }

    }

    public void growCrops(Level p_52264_, BlockPos p_52265_, BlockState p_52266_) {
        int i = this.getAge(p_52266_) + this.getBonemealAgeIncrease(p_52264_);
        int j = this.getMaxAge();
        if (i > j) {
            i = j;
        }

        p_52264_.setBlock(p_52265_, this.getStateForAge(i), 2);
    }

    protected int getBonemealAgeIncrease(Level p_52262_) {
        return Mth.nextInt(p_52262_.random, 2, 5);
    }

    protected static float getGrowthSpeed(Block p_52273_, BlockGetter p_52274_, BlockPos p_52275_) {
        float f = 1.0F;
        BlockPos blockpos = p_52275_.below();

        for(int i = -1; i <= 1; ++i) {
            for(int j = -1; j <= 1; ++j) {
                float f1 = 0.0F;
                BlockState blockstate = p_52274_.getBlockState(blockpos.offset(i, 0, j));
                if (blockstate.canSustainPlant(p_52274_, blockpos.offset(i, 0, j), net.minecraft.core.Direction.UP, (net.minecraftforge.common.IPlantable) p_52273_)) {
                    f1 = 1.0F;
                    if (blockstate.isFertile(p_52274_, p_52275_.offset(i, 0, j))) {
                        f1 = 3.0F;
                    }
                }

                if (i != 0 || j != 0) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        BlockPos blockpos1 = p_52275_.north();
        BlockPos blockpos2 = p_52275_.south();
        BlockPos blockpos3 = p_52275_.west();
        BlockPos blockpos4 = p_52275_.east();
        boolean flag = p_52274_.getBlockState(blockpos3).is(p_52273_) || p_52274_.getBlockState(blockpos4).is(p_52273_);
        boolean flag1 = p_52274_.getBlockState(blockpos1).is(p_52273_) || p_52274_.getBlockState(blockpos2).is(p_52273_);
        if (flag && flag1) {
            f /= 2.0F;
        } else {
            boolean flag2 = p_52274_.getBlockState(blockpos3.north()).is(p_52273_) || p_52274_.getBlockState(blockpos4.north()).is(p_52273_) || p_52274_.getBlockState(blockpos4.south()).is(p_52273_) || p_52274_.getBlockState(blockpos3.south()).is(p_52273_);
            if (flag2) {
                f /= 2.0F;
            }
        }

        return f;
    }

    public boolean canSurvive(BlockState p_52282_, LevelReader p_52283_, BlockPos p_52284_) {
        return (p_52283_.getRawBrightness(p_52284_, 0) >= 8 || p_52283_.canSeeSky(p_52284_)) && super.canSurvive(p_52282_, p_52283_, p_52284_);
    }

    public void entityInside(BlockState p_52277_, Level p_52278_, BlockPos p_52279_, Entity p_52280_) {
        if (p_52280_ instanceof Ravager && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(p_52278_, p_52280_)) {
            p_52278_.destroyBlock(p_52279_, true, p_52280_);
        }

        super.entityInside(p_52277_, p_52278_, p_52279_, p_52280_);
    }

    public ItemStack getCloneItemStack(BlockGetter p_52254_, BlockPos p_52255_, BlockState p_52256_) {
        return new ItemStack(this.getBaseSeedId());
    }

    public boolean isValidBonemealTarget(BlockGetter p_52258_, BlockPos p_52259_, BlockState p_52260_, boolean p_52261_) {
        return !this.isMaxAge(p_52260_);
    }

    public boolean isBonemealSuccess(Level p_52268_, Random p_52269_, BlockPos p_52270_, BlockState p_52271_) {
        return true;
    }

    public void performBonemeal(ServerLevel p_52249_, Random p_52250_, BlockPos p_52251_, BlockState p_52252_) {
        this.growCrops(p_52249_, p_52251_, p_52252_);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_52286_) {
        p_52286_.add(AGE);
    }

    public static List<ItemStack> getDrops(BlockState p_49870_, ServerLevel p_49871_, BlockPos p_49872_, @Nullable BlockEntity p_49873_) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_49871_)).withRandom(p_49871_.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_49872_)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, p_49873_);
        return p_49870_.getDrops(lootcontext$builder);
    }

    public static List<ItemStack> getDrops(BlockState p_49875_, ServerLevel p_49876_, BlockPos p_49877_, @Nullable BlockEntity p_49878_, @Nullable Entity p_49879_, ItemStack p_49880_) {
        LootContext.Builder lootcontext$builder = (new LootContext.Builder(p_49876_)).withRandom(p_49876_.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(p_49877_)).withParameter(LootContextParams.TOOL, p_49880_).withOptionalParameter(LootContextParams.THIS_ENTITY, p_49879_).withOptionalParameter(LootContextParams.BLOCK_ENTITY, p_49878_);
        return p_49875_.getDrops(lootcontext$builder);
    }

    public static void dropResources(BlockState p_152461_, LootContext.Builder p_152462_) {
        ServerLevel serverlevel = p_152462_.getLevel();
        BlockPos blockpos = new BlockPos(p_152462_.getParameter(LootContextParams.ORIGIN));
        p_152461_.getDrops(p_152462_).forEach((p_152406_) -> {
            popResource(serverlevel, blockpos, p_152406_);
        });
        p_152461_.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY);
    }

    public static void dropResources(BlockState p_49951_, Level p_49952_, BlockPos p_49953_) {
        if (p_49952_ instanceof ServerLevel) {
            getDrops(p_49951_, (ServerLevel)p_49952_, p_49953_, (BlockEntity)null).forEach((p_49944_) -> {
                popResource(p_49952_, p_49953_, p_49944_);
            });
            p_49951_.spawnAfterBreak((ServerLevel)p_49952_, p_49953_, ItemStack.EMPTY);
        }

    }

    public static void dropResources(BlockState p_49893_, LevelAccessor p_49894_, BlockPos p_49895_, @Nullable BlockEntity p_49896_) {
        if (p_49894_ instanceof ServerLevel) {
            getDrops(p_49893_, (ServerLevel)p_49894_, p_49895_, p_49896_).forEach((p_49859_) -> {
                popResource((ServerLevel)p_49894_, p_49895_, p_49859_);
            });
            p_49893_.spawnAfterBreak((ServerLevel)p_49894_, p_49895_, ItemStack.EMPTY);
        }

    }

    public static void dropResources(BlockState p_49882_, Level p_49883_, BlockPos p_49884_, @Nullable BlockEntity p_49885_, Entity p_49886_, ItemStack p_49887_) {
        if (p_49883_ instanceof ServerLevel) {
            getDrops(p_49882_, (ServerLevel)p_49883_, p_49884_, p_49885_, p_49886_, p_49887_).forEach((p_49925_) -> {
                popResource(p_49883_, p_49884_, p_49925_);
            });
            p_49882_.spawnAfterBreak((ServerLevel)p_49883_, p_49884_, p_49887_);
        }

    }
}
