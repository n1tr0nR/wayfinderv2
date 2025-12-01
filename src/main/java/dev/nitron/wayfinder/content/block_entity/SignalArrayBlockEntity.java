package dev.nitron.wayfinder.content.block_entity;

import dev.nitron.wayfinder.init.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SignalArrayBlockEntity extends BlockEntity {
    public String name;
    public Vec3i color;
    public int type;
    public int frequency;
    public UUID owner_uuid;

    public SignalArrayBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.SIGNAL_ARRAY_BE, pos, state);
        this.name = "Signal";
        this.color = new Vec3i(76, 255, 135);
        this.type = 0;
        this.frequency = 3;
    }

    public void setUuid(UUID uuid){
        this.owner_uuid = uuid;
        if (this.world != null) {
            this.markDirty();
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    public void update(String name, Vec3i color, int type){
        this.name = name;
        this.color = color;
        this.type = type;
        if (this.world != null) {
            this.markDirty();
            this.world.updateListeners(this.pos, this.getCachedState(), this.getCachedState(), Block.NOTIFY_ALL);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putString("name", this.name);
        view.putString("owner_uuid", this.owner_uuid.toString());
        view.putInt("red", this.color.getX());
        view.putInt("green", this.color.getY());
        view.putInt("blue", this.color.getZ());
        view.putInt("type", this.type);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.name = view.getString("name", "");
        this.owner_uuid = UUID.fromString(view.getString("owner_uuid", ""));
        this.color = new Vec3i(view.getInt("red", 0), view.getInt("green", 0), view.getInt("blue", 0));
        this.type = view.getInt("type", 0);
    }

    @Override
    public @Nullable Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}
