package dev.nitron.wayfinder.content.block;

import com.mojang.serialization.MapCodec;
import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.block_entity.SignalArrayBlockEntity;
import dev.nitron.wayfinder.content.cca.SignalPlacementsComponent;
import dev.nitron.wayfinder.content.data_component.SignalTweakerComponent;
import dev.nitron.wayfinder.content.item.SignalTweakerItem;
import dev.nitron.wayfinder.init.ModBlocks;
import dev.nitron.wayfinder.init.ModDataComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.block.WireOrientation;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

public class SignalArrayBlock extends BlockWithEntity implements Waterloggable {
    public static final BooleanProperty POWERED = Properties.POWERED;

    public SignalArrayBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(SignalArrayBlock::new);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(POWERED, ctx.getWorld().isReceivingRedstonePower(ctx.getBlockPos()));
    }

    @Override
    protected boolean canBucketPlace(BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public boolean canFillWithFluid(@Nullable LivingEntity filler, BlockView world, BlockPos pos, BlockState state, Fluid fluid) {
        return false;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        if (world.getBlockEntity(pos) instanceof SignalArrayBlockEntity signalArrayBlockEntity){
            if (placer instanceof PlayerEntity player){
                signalArrayBlockEntity.setUuid(player.getUuid());
            }
        }
        if (!world.isClient()) {
            SignalPlacementsComponent comp = SignalPlacementsComponent.get(world);

            if (world.getBlockEntity(pos) instanceof SignalArrayBlockEntity be) {
                comp.addSignal(new SignalPlacementsComponent.SignalData(
                        pos,
                        be.name,
                        be.color,
                        be.frequency,
                        be.owner_uuid,
                        world.isReceivingRedstonePower(pos),
                        be.frequency
                ));
            }
        }
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if (!world.isClient()) {
            SignalPlacementsComponent comp = SignalPlacementsComponent.get(world);
            comp.removeSignal(pos);
        }
        return state;
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, @Nullable WireOrientation wireOrientation, boolean notify) {
        SignalPlacementsComponent component = SignalPlacementsComponent.get(world);
        SignalPlacementsComponent.SignalData signal = component.getSignalPositions().stream()
                .filter(signalData -> signalData.pos.equals(pos))
                .findFirst()
                .orElse(null);

        component.updateSignal(pos, signal.name, signal.color, signal.type, signal.owner, world.isReceivingRedstonePower(pos), signal.frequency);

        if (world.isReceivingRedstonePower(pos) && signal != null){
            PlayerEntity player = world.getPlayerByUuid(signal.owner);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (player.isSneaking()){
            SignalPlacementsComponent placementsComponent = SignalPlacementsComponent.get(world);
            SignalPlacementsComponent.SignalData signal = SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions().stream()
                    .filter(signalData -> signalData.pos.equals(pos))
                    .findFirst()
                    .orElse(null);

            if (player.getMainHandStack().getItem() instanceof SignalTweakerItem){
                player.getMainHandStack().set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE, new SignalTweakerComponent(
                        signal.color,
                        signal.name,
                        signal.isPowered,
                        signal.frequency,
                        signal.type,
                        0
                ));
            }

            world.playSound(null,
                    pos.toCenterPos().x, pos.toCenterPos().y, pos.toCenterPos().z,
                    SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.BLOCKS, 1.0F, 1.0F);

        } else {
            if (world.getBlockEntity(pos) instanceof SignalArrayBlockEntity signalArrayBlockEntity && player.getMainHandStack().getItem() instanceof SignalTweakerItem){
                SignalTweakerComponent component = player.getMainHandStack().get(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE);
                signalArrayBlockEntity.update(
                        component.name(),
                        component.color(),
                        component.type()
                );
                if (component.privateNetwork()){
                    Wayfinder.grantAdvancement(player, Identifier.of(Wayfinder.MOD_ID, "personal_vpn"), "incode");
                }
                SignalPlacementsComponent placementsComponent = SignalPlacementsComponent.get(world);
                SignalPlacementsComponent.SignalData signal = SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions().stream()
                        .filter(signalData -> signalData.pos.equals(pos))
                        .findFirst()
                        .orElse(null);

                placementsComponent.updateSignal(
                        pos,
                        component.name(),
                        component.color(),
                        component.type(),
                        signal.owner,
                        component.privateNetwork(),
                        component.freq()
                );

                if (world instanceof ServerWorld world1){

                    int r  = component.color().getX();
                    int g  = component.color().getY();
                    int b  = component.color().getZ();
                    int a = (int)(255);
                    int color = (a << 24) | (r << 16) | (g << 8) | b;

                    DustParticleEffect particleEffect = new DustParticleEffect(color, 2);

                    world1.spawnParticles(
                            particleEffect,
                            pos.toCenterPos().x,
                            pos.getY(),
                            pos.toCenterPos().z,
                            10,
                            0.1F,
                            0.25F,
                            0.1F,
                            1
                    );
                    world1.playSound(null,
                            pos.toCenterPos().x, pos.toCenterPos().y, pos.toCenterPos().z,
                            SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onDestroyedByExplosion(ServerWorld world, BlockPos pos, Explosion explosion) {
        super.onDestroyedByExplosion(world, pos, explosion);
        SignalPlacementsComponent comp = SignalPlacementsComponent.get(world);
        comp.removeSignal(pos);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignalArrayBlockEntity(pos, state);
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.cuboid(0.125, 0, 0.125, 0.875, 0.5, 0.875);
    }
}
