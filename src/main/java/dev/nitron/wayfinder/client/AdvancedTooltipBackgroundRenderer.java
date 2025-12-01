package dev.nitron.wayfinder.client;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.data_component.SignalTweakerComponent;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalTweakerItem;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.gui.tooltip.TooltipPositioner;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class AdvancedTooltipBackgroundRenderer extends TooltipBackgroundRenderer {
    private final DrawContext context;

    public AdvancedTooltipBackgroundRenderer(DrawContext context) {
        this.context = context;
    }

    public void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, @Nullable Identifier texture, ItemStack stack) {
        Identifier tex = texture;

        text.removeIf(s -> s.contains(Text.of("Tools & Utilities")));

        if (stack.getItem() instanceof SignalscopeItem){
            SignalscopeComponent component = stack.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
            boolean shift = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT)
                    || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_SHIFT);
            boolean ctrl = InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_LEFT_CONTROL)
                    || InputUtil.isKeyPressed(MinecraftClient.getInstance().getWindow(), GLFW.GLFW_KEY_RIGHT_CONTROL);
            if (shift){
                text.add(Text.literal("Hold down [").append(Text.literal("SHIFT").withColor(0xf7db70)).append("] for details.").formatted(Formatting.DARK_GRAY));
                text.add(Text.of(" "));
                text.add(Text.literal("Performing ").append(Text.literal("ALT + SCROLL-WHEEL").withColor(0xf7db70).formatted(Formatting.BOLD)).formatted(Formatting.GRAY));
                text.add(Text.literal("- Scrolls through the Signalscope's").formatted(Formatting.DARK_GRAY));
                text.add(Text.literal("  frequency.").formatted(Formatting.DARK_GRAY));
                text.add(Text.of(" "));
                text.add(Text.literal("Performing ").append(Text.literal("SHIFT + ALT + SCROLL-WHEEL").withColor(0xf7db70).formatted(Formatting.BOLD)).formatted(Formatting.GRAY));
                text.add(Text.literal("- Scrolls through the Signalscope's").formatted(Formatting.DARK_GRAY));
                text.add(Text.literal("  gain.").formatted(Formatting.DARK_GRAY));
                text.add(Text.literal("- Increasing the gain also lowers the").formatted(Formatting.RED));
                text.add(Text.literal("  Signalscope's accuracy.").formatted(Formatting.RED));
                text.add(Text.of(" "));
                ctrl = false;
            }

            if (!shift){
                text.add(Text.literal("Hold down [").append(Text.literal("SHIFT").formatted(Formatting.GRAY)).append("] for details.").formatted(Formatting.DARK_GRAY));
            }
            if (!ctrl && component.selected() != 0 && (!component.lensItemstack().isEmpty() || !component.upgradeItemstack().isEmpty())){
                text.add(Text.literal("Hold down [").append(Text.literal("CTRL").formatted(Formatting.GRAY)).append("] for upgrade info.").formatted(Formatting.DARK_GRAY));
            }

            if (!shift && ctrl && component.selected() != 0 && (!component.lensItemstack().isEmpty() || !component.upgradeItemstack().isEmpty())){
                text.add(Text.literal("Hold down [").append(Text.literal("CTRL").withColor(0xf7db70)).append("] for upgrade info.").formatted(Formatting.DARK_GRAY));
                text.add(Text.of(" "));

                if (component.selected() == 1){
                    //Lens
                    text.add(Text.literal("- ").formatted(Formatting.DARK_GRAY).append(Text.literal("Lens:").withColor(0xf7db70)));
                    if (!component.lensItemstack().isEmpty()){
                        text.add(Text.literal("  ").formatted(Formatting.DARK_GRAY).append(Text.translatable(component.lensItemstack().getItem().getTranslationKey()).append(":").formatted(Formatting.GRAY)));
                        if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS)){
                            text.add(Text.literal("  Shows only signals that are placed").formatted(Formatting.DARK_GRAY));
                            text.add(Text.literal("  by OTHER players.").formatted(Formatting.DARK_GRAY));
                        }
                        if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS)){
                            text.add(Text.literal("  Shows only signals that are placed").formatted(Formatting.DARK_GRAY));
                            text.add(Text.literal("  by YOU.").formatted(Formatting.DARK_GRAY));
                        }
                    } else {
                        text.add(Text.literal("- ").formatted(Formatting.DARK_GRAY).append(Text.literal("No Lens Added").formatted(Formatting.GRAY)));
                    }
                }
                if (component.selected() == 2){
                    //Lens
                    text.add(Text.literal("- ").formatted(Formatting.DARK_GRAY).append(Text.literal("Upgrade:").withColor(0xf7db70)));
                    if (!component.upgradeItemstack().isEmpty()){
                        text.add(Text.literal("  ").formatted(Formatting.DARK_GRAY).append(Text.translatable(component.upgradeItemstack().getItem().getTranslationKey()).append(":").formatted(Formatting.GRAY)));
                        if (component.upgradeItemstack().isOf(ModItems.SIGNAL_BOOSTER)){
                            text.add(Text.literal("  Boosts the Signalscope's range").formatted(Formatting.DARK_GRAY));
                            text.add(Text.literal("  to 1500m-3000m").formatted(Formatting.DARK_GRAY));
                        }
                        if (component.upgradeItemstack().isOf(ModItems.SIGNAL_EXPANDER)){
                            text.add(Text.literal("  Boosts the Signalscope's range").formatted(Formatting.DARK_GRAY));
                            text.add(Text.literal("  to 2500m-5000m").formatted(Formatting.DARK_GRAY));
                        }
                    } else {
                        text.add(Text.literal("- ").formatted(Formatting.DARK_GRAY).append(Text.literal("No Upgrade Added").formatted(Formatting.GRAY)));
                    }
                }
            }

            text.add(1, Text.of(" "));


            tex = Identifier.of(Wayfinder.MOD_ID, "signal");

            if (component != null){
                if (!component.upgradeItemstack().isEmpty()){
                    String name = component.upgradeItemstack().getName().getString();
                    if (component.upgradeItemstack().contains(DataComponentTypes.CUSTOM_NAME)){
                        name = component.upgradeItemstack().get(DataComponentTypes.CUSTOM_NAME).getString();
                    }
                    text.add(1, Text.literal("- ").formatted(Formatting.DARK_GRAY).append(Text.literal(name).withColor(component.selected() == 2 ? 0xf7db70 : 0xa8a8a8)).append(Text.literal(component.selected() == 2 ? " <" : "").withColor(0xf7db70)));
                }
                if (!component.lensItemstack().isEmpty()){
                    String name = component.lensItemstack().getName().getString();
                    if (component.lensItemstack().contains(DataComponentTypes.CUSTOM_NAME)){
                        name = component.lensItemstack().get(DataComponentTypes.CUSTOM_NAME).getString();
                    }
                    text.add(1, Text.literal("- ").formatted(Formatting.DARK_GRAY).append(Text.literal(name).withColor(component.selected() == 1 ? 0xf7db70 : 0xa8a8a8)).append(Text.literal(component.selected() == 1 ? " <" : "").withColor(0xf7db70)));
                }
            }
            text.add(1, Text.of(" "));
            text.add(1, Text.of(" "));
            text.add(1, Text.of(" "));
        }
        if (stack.getItem() instanceof SignalTweakerItem){
            SignalTweakerComponent component = stack.get(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE);
            //if (MinecraftClient.getInstance().currentScreen instanceof CreativeInventoryScreen) text.remove(text.getLast());
            tex = Identifier.of(Wayfinder.MOD_ID, "signal");
            text.add(Text.literal("Hold down ALT to switch data.").formatted(Formatting.DARK_GRAY));
            text.add(Text.literal(""));
            text.add(Text.literal(String.format(
                    "  %03d  %03d  %03d  ",
                    component.color().getX(),
                    component.color().getY(),
                    component.color().getZ()
            )).withColor(0xf7db70));
            text.add(Text.literal(""));
            text.add(Text.literal("  Name: ").append(Text.literal(component.name()).withColor(0xf7db70)).formatted(Formatting.GRAY));
            text.add(Text.literal(""));
            text.add(Text.literal("  Network: ").append(Text.literal(component.privateNetwork() ? "Private": "Public").withColor(0xf7db70)).formatted(Formatting.GRAY));
            text.add(Text.literal(""));
            text.add(Text.literal("  Data: ").formatted(Formatting.GRAY));
            text.add(Text.literal("  - Frequency:  ").append(Text.literal(String.valueOf(component.freq())).withColor(0xf7db70)).append(" | - Type: ").append(Text.literal(getType(component.type())).withColor(0xf7db70)).formatted(Formatting.GRAY));
            text.add(Text.literal(""));
        }
        List<TooltipComponent> list = (List)text.stream().map(Text::asOrderedText).map(TooltipComponent::of).collect(Util.toArrayList());
        data.ifPresent((datax) -> list.add(list.isEmpty() ? 0 : 1, TooltipComponent.of(datax)));
        this.drawTooltip(textRenderer, list, x, y, HoveredTooltipPositioner.INSTANCE, tex, false, stack);
    }

    private void drawTooltip(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture, boolean focused, ItemStack stack) {
        if (!components.isEmpty()) {
            if (context.tooltipDrawer == null || focused) {
                context.tooltipDrawer = () -> this.drawTooltipImmediately(textRenderer, components, x, y, positioner, texture, stack);
            }
        }
    }

    public void drawTooltipImmediately(TextRenderer textRenderer, List<TooltipComponent> components, int x, int y, TooltipPositioner positioner, @Nullable Identifier texture, ItemStack stack) {
        int i = 0;
        int j = components.size() == 1 ? -2 : 0;

        for(TooltipComponent tooltipComponent : components) {
            int k = tooltipComponent.getWidth(textRenderer);
            if (k > i) {
                i = k;
            }

            j += tooltipComponent.getHeight(textRenderer);
        }

        int l = i;
        int m = j;
        Vector2ic vector2ic = positioner.getPosition(context.getScaledWindowWidth(), context.getScaledWindowHeight(), x, y, i, j);
        int n = vector2ic.x();
        int o = vector2ic.y();
        context.getMatrices().pushMatrix();
        TooltipBackgroundRenderer.render(context, n, o, i, j, texture);
        int p = o;

        for(int q = 0; q < components.size(); ++q) {
            TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(q);
            tooltipComponent2.drawText(context, textRenderer, n, p);
            p += tooltipComponent2.getHeight(textRenderer) + (q == 0 ? 2 : 0);
        }

        p = o;

        for(int q = 0; q < components.size(); ++q) {
            TooltipComponent tooltipComponent2 = (TooltipComponent)components.get(q);
            tooltipComponent2.drawItems(textRenderer, n, p, l, m, context);
            p += tooltipComponent2.getHeight(textRenderer) + (q == 0 ? 2 : 0);
        }

        if (stack.getItem() instanceof SignalscopeItem) {
            n -= 12;
            o += 12;


            Identifier TEXTURE = Identifier.of(Wayfinder.MOD_ID, "textures/gui/sprites/hud/signalscope_tooltip.png");
            context.drawTexture(
                    RenderPipelines.GUI_TEXTURED,
                    TEXTURE,
                    n + 15,
                    o,
                    0,
                    0,
                    64,
                    26,
                    64,
                    64
            );

            SignalscopeComponent component = stack.get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
            if (component != null) {
                if (!component.lensItemstack().isEmpty()) {
                    context.drawItem(component.lensItemstack(), n + 20, o + 5);
                }
                if (!component.upgradeItemstack().isEmpty()) {
                    context.drawItem(component.upgradeItemstack(), n + 52, o + 5);
                }
                if (component.selected() != 0) {
                    context.drawTexture(
                            RenderPipelines.GUI_TEXTURED,
                            TEXTURE,
                            n + 18 + (component.selected() == 2 ? 32 : 0),
                            o + 3,
                            0,
                            26,
                            64,
                            26,
                            64,
                            64
                    );
                }
            }
        }

        if (stack.getItem() instanceof SignalTweakerItem){
            SignalTweakerComponent component = stack.get(ModDataComponents.SIGNAL_TWEAKER_COMPONENT_COMPONENT_TYPE);
            if (component != null){
                Identifier TEXTURE1 = Identifier.of(Wayfinder.MOD_ID, "hud/bars_selected");
                Identifier TEXTURE = Identifier.of(Wayfinder.MOD_ID, "hud/bars");
                Identifier HUD = Identifier.of(Wayfinder.MOD_ID, "textures/gui/sprites/hud/signal_tweaker_tooltip.png");
                boolean extended = MinecraftClient.getInstance().options.advancedItemTooltips;
                boolean create = false;

                int a = n - 3 - 9;
                int b = o - 3 - 9 + (extended ? 20 : 0) + (create ? 10 : 0);
                int c = 5
                        + 3 + 3 + 18;
                int d = 5
                        + 3 + 3 + 18;

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, component.selected() == 0 ? TEXTURE1 : TEXTURE,
                        a + 8,
                        b + 31,
                        c + 12,
                        d + 4
                );

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, component.selected() == 1 ? TEXTURE1 : TEXTURE,
                        a + 8 + 26,
                        b + 31,
                        c + 12,
                        d + 4
                );

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, component.selected() == 2 ? TEXTURE1 : TEXTURE,
                        a + 8 + 52,
                        b + 31,
                        c + 12,
                        d + 4
                );

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE,
                        a + 8 + 80,
                        b + 31,
                        c + 5,
                        d + 5
                );

                int color = (255 << 24) | (component.color().getX() << 16) | (component.color().getY() << 8) | component.color().getZ();

                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        HUD,
                        n + 86,
                        o + 29  + (extended ? 20 : 0) + (create ? 10 : 0),
                        16,
                        15,
                        14,
                        14,
                        128,
                        128,
                        color
                );

                context.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        HUD,
                        n + textRenderer.getWidth(component.privateNetwork() ? "Private": "Public") + 60,
                        o + 70  + (extended ? 20 : 0) + (create ? 10 : 0),
                        40 - (component.privateNetwork() ? 0 : 9 ),
                        0,
                        9,
                        11,
                        128,
                        128,
                        0xFFFFFFFF
                );

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, component.selected() == 3 ? TEXTURE1 : TEXTURE,
                        a + 8 + 45,
                        b + 51 + 20,
                        c + textRenderer.getWidth(component.privateNetwork() ? "Private": "Public") - 6,
                        d + 5
                );

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, component.selected() == 4 ? TEXTURE1 : TEXTURE,
                        a + 8 + 74,
                        b + 52 + 50,
                        c + textRenderer.getWidth(String.valueOf(component.freq())) - 6,
                        d + 2
                );

                context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, component.selected() == 5 ? TEXTURE1 : TEXTURE,
                        a + 8 + 130 + (component.freq() == 12 ? 6 : 0),
                        b + 51 + 50,
                        c + textRenderer.getWidth(getTypeW(component.type())) - 6,
                        d + 5
                );
            }
        }

        context.getMatrices().popMatrix();
    }

    public String getType(int type){
        if (type == 1) return "Alternate  ";
        if (type == 2) return "Final  ";

        return "Default  ";
    }

    public String getTypeW(int type){
        if (type == 1) return "Alternate";
        if (type == 2) return "Final";

        return "Default";
    }
}
