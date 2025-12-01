package dev.nitron.wayfinder.content.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import dev.nitron.wayfinder.content.cca.SignalPlacementsComponent;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.UUID;

public class SignalCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess access, CommandManager.RegistrationEnvironment environment) {
        dispatcher.register(
                CommandManager.literal("signals").requires(source -> source.hasPermissionLevel(2))
                        .then(CommandManager.argument("pos", BlockPosArgumentType.blockPos())
                                .then(CommandManager.literal("add")
                                        .executes(SignalCommand::add))
                                .then(CommandManager.literal("remove")
                                        .executes(SignalCommand::remove))
                                .then(CommandManager.literal("clear")
                                        .executes(SignalCommand::clear)))
        );
    }

    private static int clear(CommandContext<ServerCommandSource> context) {
        World world = context.getSource().getWorld();
        SignalPlacementsComponent.get(world).clear();
        return Command.SINGLE_SUCCESS;
    }

    private static int remove(CommandContext<ServerCommandSource> context) {
        World world = context.getSource().getWorld();
        SignalPlacementsComponent.get(world).removeSignal(BlockPosArgumentType.getBlockPos(context, "pos"));
        return Command.SINGLE_SUCCESS;
    }

    private static int add(CommandContext<ServerCommandSource> context) {
        World world = context.getSource().getWorld();
        SignalPlacementsComponent.SignalData data = new SignalPlacementsComponent.SignalData(
                BlockPosArgumentType.getBlockPos(context, "pos"),
                "New Signal",
                new Vec3i(255, 255, 255),
                3,
                context.getSource().getPlayer() != null ? context.getSource().getPlayer().getUuid() : UUID.fromString(""),
                false,
                1
        );
        SignalPlacementsComponent.get(world).addSignal(data);
        return Command.SINGLE_SUCCESS;
    }
}
