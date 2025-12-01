package dev.nitron.wayfinder.content.cca;

import dev.nitron.wayfinder.Wayfinder;
import dev.nitron.wayfinder.content.block_entity.SignalArrayBlockEntity;
import dev.nitron.wayfinder.content.data_component.SignalscopeComponent;
import dev.nitron.wayfinder.content.item.SignalscopeItem;
import dev.nitron.wayfinder.init.ModComponents;
import dev.nitron.wayfinder.init.ModDataComponents;
import dev.nitron.wayfinder.init.ModItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.List;

public class SignalSoundsComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final PlayerEntity player;

    private float factor0 = 0;
    private float factor1 = 0;
    private float factor2 = 0;

    public SignalSoundsComponent(PlayerEntity player) {
        this.player = player;
    }
    public void sync(){
        ModComponents.SOUNDS.sync(this.player);
    }

    public float getFactor0(){
        return this.factor0;
    }

    public float getFactor1(){
        return this.factor1;
    }

    public float getFactor2(){
        return this.factor2;
    }

    @Override
    public void tick() {
        if (player.getEntityWorld().isClient()) return;

        SignalPlacementsComponent comp = ModComponents.SIGNAL_PLACEMENTS.get(player.getEntityWorld());
        List<SignalPlacementsComponent.SignalData> signals = comp.getSignalPositions();

        if (!(player.getMainHandStack().getItem() instanceof SignalscopeItem)) return;

        float newFactor0 = 0f;
        float newFactor1 = 0f;
        float newFactor2 = 0f;

        if (!signals.isEmpty()){
            for (SignalPlacementsComponent.SignalData signalData : signals){
                BlockPos pos = signalData.pos;
                double distance = player.getEntityPos().distanceTo(Vec3d.ofCenter(pos));
                BlockEntity be = player.getEntityWorld().getBlockEntity(pos);

                SignalArrayBlockEntity signalArrayBlockEntity =
                        be instanceof SignalArrayBlockEntity sa ? sa : null;

                if (signalArrayBlockEntity == null) {
                    continue;
                }

                boolean powered = signalData.isPowered;
                if (powered && !player.getUuid().equals(signalArrayBlockEntity.owner_uuid)) {
                    continue;
                }

                if (signalData.isPowered && !player.getUuid().equals(signalData.owner)) continue;

                SignalscopeComponent component = player.getMainHandStack().get(ModDataComponents.SIGNALSCOPE_COMPONENT_COMPONENT_TYPE);
                if (component == null) continue;

                if (component.lensItemstack().isOf(ModItems.PRIVACY_LENS) && !player.getUuid().equals(signalData.owner)) continue;
                if (component.lensItemstack().isOf(ModItems.VANTAGE_LENS) && player.getUuid().equals(signalData.owner)) continue;
                if (component.lensItemstack().isEmpty()) continue;
                if (component.frequency() != signalData.frequency) continue;

                double maxDis = (component.upgradeItemstack().isOf(ModItems.SIGNAL_EXPANDER) ? 500 : component.upgradeItemstack().isOf(ModItems.SIGNAL_BOOSTER) ? 300 : 200) * (((component.gain() - 30) + 10) / 2);
                int type = signalArrayBlockEntity.type;

                float factor = (float) dev.nitron.wayfinder.util.Math.getLookFactor(
                        player,
                        pos,
                        75F,
                        1.0F
                );

                if (distance < maxDis){
                    switch (type) {
                        case 0 -> newFactor0 = Math.max(newFactor0, factor);
                        case 1 -> newFactor1 = Math.max(newFactor1, factor);
                        case 2 -> newFactor2 = Math.max(newFactor2, factor);
                        default -> {}
                    }
                }
            }
        }

        this.factor0 = newFactor0;
        this.factor1 = newFactor1;
        this.factor2 = newFactor2;
        this.sync();

        if (this.factor0 == 1 && this.factor2 == 1 && this.factor1 == 1){
            Wayfinder.grantAdvancement(player, Identifier.of(Wayfinder.MOD_ID, "let_the_choir_sing"), "incode");
        }
    }

    @Override
    public void readData(ReadView readView) {
        this.factor0 = readView.getFloat("factor0", 0);
        this.factor1 = readView.getFloat("factor1", 0);
        this.factor2 = readView.getFloat("factor2", 0);
    }

    @Override
    public void writeData(WriteView writeView) {
        writeView.putFloat("factor0", this.factor0);
        writeView.putFloat("factor1", this.factor1);
        writeView.putFloat("factor2", this.factor2);
    }
}
