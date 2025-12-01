package dev.nitron.wayfinder.content.cca;

import dev.nitron.wayfinder.init.ModComponents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.CommonTickingComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SignalPlacementsComponent implements AutoSyncedComponent, CommonTickingComponent {
    private final World world;

    private final List<SignalData> signalPositions = new ArrayList<>();

    public SignalPlacementsComponent(World world) {
        this.world = world;
    }
    public void sync(){
        ModComponents.SIGNAL_PLACEMENTS.sync(this.world);
    }
    public static SignalPlacementsComponent get(World world){
        return ModComponents.SIGNAL_PLACEMENTS.get(world);
    }

    @Override
    public void tick() {

    }

    @Override
    public void readData(ReadView readView) {
        signalPositions.clear();
        ReadView.ListReadView list = readView.getListReadView("Signals");
        for (ReadView element : list){
            BlockPos pos = new BlockPos(element.getInt("x", 0), element.getInt("y", 0), element.getInt("z", 0));
            String name = element.getString("name", "");
            Vec3i color = new Vec3i(element.getInt("red", 0), element.getInt("green", 0), element.getInt("blue", 0));
            int type = element.getInt("type", 0);
            int freq = element.getInt("freq", 3);
            UUID ownerUUID = UUID.fromString(element.getString("ownerUUID", ""));
            boolean powered = element.getBoolean("powered", false);
            signalPositions.add(new SignalData(pos, name, color, freq, ownerUUID, powered, type));
        }
    }

    @Override
    public void writeData(WriteView writeView) {
        WriteView.ListView list = writeView.getList("Signals");
        for (SignalData data : signalPositions) {
            WriteView element = list.add();
            element.putInt("x", data.pos.getX());
            element.putInt("y", data.pos.getY());
            element.putInt("z", data.pos.getZ());
            element.putString("name", data.name);
            element.putInt("red", data.color.getX());
            element.putInt("green", data.color.getY());
            element.putInt("blue", data.color.getZ());
            element.putInt("type", data.type);
            element.putInt("freq", data.frequency);
            element.putString("ownerUUID", data.owner.toString());
            element.putBoolean("powered", data.isPowered);
        }
    }

    public void updateSignal(BlockPos pos, String name, Vec3i color, int type, UUID ownerUuid, boolean powered, int freq) {;
        for (SignalData s : signalPositions) {
            if (s.pos.equals(pos)) {
                s.name = name;
                s.color = color;
                s.type = type;
                s.owner = ownerUuid;
                s.isPowered = powered;
                s.frequency = freq;
                sync();
                return;
            }
        }
        signalPositions.add(new SignalData(pos, name, color, freq, ownerUuid, powered, type));
        sync();
    }

    public void addSignal(SignalData data) {
        signalPositions.add(data);
        sync();
    }

    public void removeSignal(BlockPos pos) {
        signalPositions.removeIf(signalData -> signalData.pos.equals(pos));
        sync();
    }

    public void clear(){
        signalPositions.clear();
        sync();
    }

    public List<SignalData> getSignalPositions() {
        return signalPositions;
    }

    public static class SignalData{
        public BlockPos pos;
        public String name;
        public Vec3i color;
        public int frequency;
        public UUID owner;
        public boolean isPowered;
        public int type;

        public SignalData(BlockPos pos, String name, Vec3i color, int frequency, UUID ownerUUID, boolean isPowered, int type){
            this.pos = pos;
            this.name = name;
            this.color = color;
            this.type = type;
            this.owner = ownerUUID;
            this.isPowered = isPowered;
            this.frequency = frequency;
        }
    }
}
