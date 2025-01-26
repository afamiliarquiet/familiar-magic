package io.github.afamiliarquiet.familiar_magic.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import io.github.afamiliarquiet.familiar_magic.block.entity.SummoningTableBlockEntity;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PlaceCandlesCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(literal("placecandles")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(2))
                .then(
                        argument("target", EntityArgumentType.entity())
                                .then(
                                        argument("pos", BlockPosArgumentType.blockPos())
                                                .executes(
                                                        context ->
                                                                placeCandles(
                                                                        context.getSource(),
                                                                        EntityArgumentType.getEntity(context, "target"),
                                                                        context.getSource().getWorld(),
                                                                        BlockPosArgumentType.getBlockPos(context, "pos"),
                                                                        true
                                                                )
                                                )
                                                .then(
                                                        argument("lit", BoolArgumentType.bool())
                                                                .executes(
                                                                        context -> placeCandles(
                                                                                context.getSource(),
                                                                                EntityArgumentType.getEntity(context, "target"),
                                                                                context.getSource().getWorld(),
                                                                                BlockPosArgumentType.getBlockPos(context, "pos"),
                                                                                BoolArgumentType.getBool(context, "lit")
                                                                        )
                                                                )
                                                )
                                )
                )
        );
    }

    private static int placeCandles(ServerCommandSource source, Entity target, ServerWorld level, BlockPos pos, boolean lit) {
        SummoningTableBlockEntity.superburn(level, pos, target.getUuid(), lit);
        source.sendFeedback(
                () -> Text.translatable(
                        "commands.familiar_magic.success",
                        target.getDisplayName(),
                        pos.getX(),
                        pos.getY(),
                        pos.getZ()
                ),
                true
        );
        return 1;
    }
}
