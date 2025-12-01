package dev.nitron.wayfinder.content.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.nitron.wayfinder.init.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public record SignalscopeComponent(int frequency, double gain, ItemStack lensItemstack, ItemStack upgradeItemstack, int selected) {
    public static final Codec<SignalscopeComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("frequency").forGetter(SignalscopeComponent::frequency),
            Codec.DOUBLE.fieldOf("gain").forGetter(SignalscopeComponent::gain),
            ItemStack.CODEC.xmap(
                    stack -> stack.getItem() == Items.BARRIER ? ItemStack.EMPTY : stack,
                    stack -> stack.isEmpty() ? new ItemStack(Items.BARRIER) : stack
            ).fieldOf("lensItemstack").forGetter(SignalscopeComponent::lensItemstack),
            ItemStack.CODEC.xmap(
                    stack -> stack.getItem() == Items.BARRIER ? ItemStack.EMPTY : stack,
                    stack -> stack.isEmpty() ? new ItemStack(Items.BARRIER) : stack
            ).fieldOf("upgradeItemstack").forGetter(SignalscopeComponent::upgradeItemstack),
            Codec.INT.fieldOf("selected").forGetter(SignalscopeComponent::selected)
    ).apply(instance, SignalscopeComponent::new));
}

