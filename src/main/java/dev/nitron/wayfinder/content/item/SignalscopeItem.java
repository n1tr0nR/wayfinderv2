package dev.nitron.wayfinder.content.item;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import dev.nitron.wayfinder.init.ModSounds;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class SignalscopeItem extends Item {
    public SignalscopeItem(Settings settings) {
        super(settings.maxCount(1).component(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE,
                new SignalscopeComponent(3, 35, ModItems.LENS.getDefaultStack(), ItemStack.EMPTY, 0)));
    }

    @Override
    public boolean allowComponentsUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        SignalscopeComponent component = stack.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);

        if (clickType == ClickType.RIGHT) {

            if (player.currentScreenHandler instanceof CraftingScreenHandler craftingScreenHandler){
                if (slot.id == 0){
                    return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
                }
            }

            ItemStack lens = component.lensItemstack();
            ItemStack upgrade = component.upgradeItemstack();

            boolean u = component.selected() == 2;

            if (lens.isEmpty() && (otherStack.isOf(ModItems.LENS) || otherStack.isOf(ModItems.PRIVACY_LENS) || otherStack.isOf(ModItems.VANTAGE_LENS))) {
                ItemStack inserted = otherStack.copyWithCount(1);
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        inserted,
                        upgrade.isEmpty() ? ItemStack.EMPTY : upgrade.copy(),
                        1
                ));
                otherStack.decrement(1);
                updateSignalscopeModel(stack, new SignalscopeComponent(component.frequency(), component.gain(), inserted, upgrade, 0));
                if (player.getEntityWorld().isClient())
                    player.playSoundToPlayer(ModSounds.PLACE_LENS, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }

            if (!lens.isEmpty() && (otherStack.isOf(ModItems.LENS) || otherStack.isOf(ModItems.PRIVACY_LENS) || otherStack.isOf(ModItems.VANTAGE_LENS))) {
                ItemStack inserted = otherStack.copyWithCount(1);
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        inserted,
                        upgrade.isEmpty() ? ItemStack.EMPTY : upgrade.copy(),
                        1
                ));
                otherStack.decrement(1);
                cursorStackReference.set(lens.copy());
                updateSignalscopeModel(stack, new SignalscopeComponent(component.frequency(), component.gain(), inserted, upgrade, 0));
                if (player.getEntityWorld().isClient())
                    player.playSoundToPlayer(ModSounds.PLACE_LENS, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }

            if (!lens.isEmpty() && cursorStackReference.get().isEmpty()  && !u) {
                cursorStackReference.set(lens.copy());
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        ItemStack.EMPTY,
                        upgrade.isEmpty() ? ItemStack.EMPTY : upgrade.copy(),
                        upgrade.isEmpty() ? 0 : 2
                ));
                updateSignalscopeModel(stack, new SignalscopeComponent(component.frequency(), component.gain(), ItemStack.EMPTY, upgrade, 0));
                if (player.getEntityWorld().isClient())
                    player.playSoundToPlayer(ModSounds.TAKE_LENS, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }

            if (upgrade.isEmpty() && (otherStack.isOf(ModItems.SIGNAL_BOOSTER) || otherStack.isOf(ModItems.SIGNAL_EXPANDER))) {
                ItemStack inserted = otherStack.copyWithCount(1);
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        lens.isEmpty() ? ItemStack.EMPTY : lens.copy(),
                        inserted,
                        2
                ));
                otherStack.decrement(1);
                updateSignalscopeModel(stack, new SignalscopeComponent(component.frequency(), component.gain(), lens, inserted, 0));
                if (player.getEntityWorld().isClient())
                    player.playSoundToPlayer(ModSounds.PLACE_LENS, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }

            if (!upgrade.isEmpty() && (otherStack.isOf(ModItems.SIGNAL_BOOSTER) || otherStack.isOf(ModItems.SIGNAL_EXPANDER))) {
                ItemStack inserted = otherStack.copyWithCount(1);
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        lens.isEmpty() ? ItemStack.EMPTY : lens.copy(),
                        inserted,
                        2
                ));
                otherStack.decrement(1);
                cursorStackReference.set(upgrade.copy());
                updateSignalscopeModel(stack, new SignalscopeComponent(component.frequency(), component.gain(), lens, inserted, 0));
                if (player.getEntityWorld().isClient())
                    player.playSoundToPlayer(ModSounds.PLACE_LENS, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }

            if (!upgrade.isEmpty() && cursorStackReference.get().isEmpty() && u) {
                cursorStackReference.set(upgrade.copy());
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        lens.isEmpty() ? ItemStack.EMPTY : lens.copy(),
                        ItemStack.EMPTY,
                        lens.isEmpty() ? 0 : 1
                ));
                updateSignalscopeModel(stack, new SignalscopeComponent(component.frequency(), component.gain(), lens, ItemStack.EMPTY, 0));
                if (player.getEntityWorld().isClient())
                    player.playSoundToPlayer(ModSounds.TAKE_LENS, SoundCategory.PLAYERS, 1.0F, 1.0F);
                return true;
            }

            if (!lens.isEmpty()){
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        lens.copy(),
                        upgrade.isEmpty() ? ItemStack.EMPTY : upgrade.copy(),
                        1
                ));
            } else if (!upgrade.isEmpty()){
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        ItemStack.EMPTY,
                        upgrade.copy(),
                        2
                ));
            } else {
                stack.set(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE, new SignalscopeComponent(
                        component.frequency(),
                        component.gain(),
                        ItemStack.EMPTY,
                        ItemStack.EMPTY,
                        0
                ));
            }
        }
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }

    public void updateSignalscopeModel(ItemStack signalscope, SignalscopeComponent component){
        String lens = "";
        String upgrade = "";

        if (component.lensItemstack().isEmpty()){
            lens = "_empty";
        }
        if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS)){
            lens = "_privacy";
        }
        if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS)){
            lens = "_vantage";
        }

        if (component.upgradeItemstack().isOf(ModItems.SIGNAL_BOOSTER)){
            upgrade = "_b";
        }
        if (component.upgradeItemstack().isOf(ModItems.SIGNAL_EXPANDER)){
            upgrade = "_e";
        }

        Identifier newIdentifier = Identifier.of(Wayfinder.MOD_ID, "signalscope" + lens + upgrade);
        signalscope.set(DataComponentTypes.ITEM_MODEL, newIdentifier);
    }

    @Override
    public Text getName(ItemStack stack) {
        return super.getName(stack).copy().withColor(0xf7db70);
    }
}
