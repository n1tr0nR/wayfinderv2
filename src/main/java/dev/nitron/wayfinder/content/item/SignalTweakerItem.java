package dev.nitron.wayfinder.content.item;

import dev.nitron.wayfinder.content.block_entity.SignalArrayBlockEntity;
import dev.nitron.wayfinder.content.cca.SignalPlacementsComponent;
import dev.nitron.wayfinder.content.data_component.SignalTweakerComponent;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModSounds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.NameTagItem;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class SignalTweakerItem extends Item {
    public SignalTweakerItem(Settings settings) {
        super(settings.component(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE, new SignalTweakerComponent(
                new Vec3i(255, 255, 255), "Signal", false, 3, 0, 0
        )));
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().withColor(0xf7db70);
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        if (clickType == ClickType.RIGHT){
            if (cursorStackReference.get().getItem() instanceof NameTagItem){
                if (cursorStackReference.get().contains(DataComponentTypes.CUSTOM_NAME) && !player.getItemCooldownManager().isCoolingDown(stack)){
                    SignalTweakerComponent component = stack.get(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE);
                    stack.set(
                            ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE,
                            new SignalTweakerComponent(
                                    component.color(),
                                    cursorStackReference.get().get(DataComponentTypes.CUSTOM_NAME).getString(),
                                    component.privateNetwork(),
                                    component.freq(),
                                    component.type(),
                                    component.selected()
                            )

                    );
                    if (player.getEntityWorld().isClient())
                        player.playSoundToPlayer(SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    player.getItemCooldownManager().set(stack, 10);
                    return true;
                } else if (player.getItemCooldownManager().isCoolingDown(stack)){
                    if (player.getEntityWorld().isClient())
                        player.playSoundToPlayer(SoundEvents.BLOCK_VAULT_INSERT_ITEM_FAIL, SoundCategory.PLAYERS, 0.1F, 1.0F);
                    return true;
                }
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();

        if (player.isSneaking()){
            SignalPlacementsComponent placementsComponent = SignalPlacementsComponent.get(world);
            SignalPlacementsComponent.SignalData signal = SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions().stream()
                    .filter(signalData -> signalData.pos.equals(pos))
                    .findFirst()
                    .orElse(null);

            if (player.getMainHandStack().getItem() instanceof SignalTweakerItem && signal != null){
                player.getMainHandStack().set(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE, new SignalTweakerComponent(
                        signal.color,
                        signal.name,
                        signal.isPowered,
                        signal.frequency,
                        signal.type,
                        0
                ));
            }

            player.sendMessage(Text.literal("Successfully copied Signal Data").formatted(Formatting.YELLOW), true);

            world.playSound(null,
                    pos.toCenterPos().x, pos.toCenterPos().y, pos.toCenterPos().z,
                    SoundEvents.BLOCK_TRIAL_SPAWNER_DETECT_PLAYER, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return ActionResult.SUCCESS;
        } else {
            if (world.getBlockEntity(pos) instanceof SignalArrayBlockEntity signalArrayBlockEntity && player.getMainHandStack().getItem() instanceof SignalTweakerItem) {
                SignalTweakerComponent component = player.getMainHandStack().get(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE);
                signalArrayBlockEntity.update(
                        component.name(),
                        component.color(),
                        component.type()
                );
                SignalPlacementsComponent placementsComponent = SignalPlacementsComponent.get(world);
                SignalPlacementsComponent.get(MinecraftClient.getInstance().world).getSignalPositions().stream()
                        .filter(signalData -> signalData.pos.equals(pos))
                        .findFirst().ifPresent(signal -> placementsComponent.updateSignal(
                                pos,
                                component.name(),
                                component.color(),
                                component.type(),
                                signal.owner,
                                component.privateNetwork(),
                                component.freq()
                        ));

                if (world instanceof ServerWorld world1) {

                    int r = component.color().getX();
                    int g = component.color().getY();
                    int b = component.color().getZ();
                    int a = (int) (255);
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
                    return ActionResult.SUCCESS;
                }
            }
        }
        return super.useOnBlock(context);
    }
}
