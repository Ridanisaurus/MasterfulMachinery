package com.ticticboooom.mods.mm.block;

import com.ticticboooom.mods.mm.block.tile.ControllerBlockEntity;
import lombok.Getter;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class ControllerBlock extends DirectionalBlock {

    private RegistryObject<TileEntityType<?>> type;
    @Getter
    private String controllerName;
    @Getter
    private String controllerId;
    @Getter
    private String texOverride;

    public ControllerBlock(RegistryObject<TileEntityType<?>> type, String name, String id, String texOverride) {
        super(AbstractBlock.Properties.of(Material.METAL)
                .harvestLevel(1)
                .harvestTool(ToolType.PICKAXE));
        this.type = type;
        this.controllerName = name;
        this.controllerId = id;
        this.texOverride = texOverride;
        this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder.add(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return type.get().create();
    }

    @Override
    public ActionResultType use(BlockState state, World level, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult traceResult) {
        if (!level.isClientSide()) {
            TileEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ControllerBlockEntity) {
                NetworkHooks.openGui(((ServerPlayerEntity) player), (ControllerBlockEntity)blockEntity, pos);
            }
        }
        return ActionResultType.SUCCESS;
    }


}
