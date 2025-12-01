package dev.nitron.wayfinder.init;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.item.SignalTweakerItem;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static void init(){
        //Itemgroups

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
            entries.addAfter(Items.SPYGLASS, VANTAGE_LENS);
            entries.addAfter(Items.SPYGLASS, PRIVACY_LENS);
            entries.addAfter(Items.SPYGLASS, LENS);
            entries.addAfter(Items.SPYGLASS, SIGNAL_EXPANDER);
            entries.addAfter(Items.SPYGLASS, SIGNAL_BOOSTER);
            entries.addAfter(Items.SPYGLASS, SIGNAL_TWEAKER);
            entries.addAfter(Items.SPYGLASS, SIGNALSCOPE);
        });

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(entries -> {
            entries.addAfter(Items.CALIBRATED_SCULK_SENSOR, ModBlocks.SIGNAL_ARRAY);
        });
    }

    public static final Item SIGNAL_BOOSTER = register("signal_booster", Item::new, new Item.Settings().maxCount(1));
    public static final Item SIGNAL_EXPANDER = register("signal_expander", Item::new, new Item.Settings().maxCount(1));
    public static final Item LENS = register("lens", Item::new, new Item.Settings().maxCount(1));
    public static final Item PRIVACY_LENS = register("privacy_lens", Item::new, new Item.Settings().maxCount(1));
    public static final Item VANTAGE_LENS = register("vantage_lens", Item::new, new Item.Settings().maxCount(1));

    public static final Item SIGNALSCOPE = register("signalscope", SignalscopeItem::new, new Item.Settings());
    public static final Item SIGNAL_TWEAKER = register("signal_tweaker", SignalTweakerItem::new, new Item.Settings());

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(Wayfinder.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);
        return item;
    }
}
